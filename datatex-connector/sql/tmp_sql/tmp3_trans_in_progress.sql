-- noinspection SqlNoDataSourceInspectionForFile

--select count(*) from (
-- in this SQL query we filter descriptions of items by pseudo-table by like expressions
SELECT * FROM (
-- in this query we group parties (items) by article without accounting production order (count average)
SELECT
    sd.item_code, sd.item_description,                              -- party article and description (human readable)
    round(avg(sd.order_duration_days), 3)   AS order_duration_days, -- average duration by party
    min(sd.order_start)                     AS order_start,         -- party start timestamp (the earliest)
    extract(month from min(sd.order_start)) AS value_month,         -- extract party start month
    extract(year from min(sd.order_start))  AS value_year           -- extract party start year
FROM (
-- in this query we group parties with production order code, article (item_code) and description, coun sum of duration
WITH source_data as (
SELECT
    stprogress.start_time, endprogress.end_time,          -- start and end time for each operation
    prodprogress.operationcode       as operation,        -- operation code (code 9000* -> the latest operation in cycle)
    proddemand.article               as item_code,        -- article for item
    fullitem.summarizeddescription   as item_description, -- human-readable name for item
    prodprogress.productionordercode as prod_ordercode,   -- production order code
    proddemand.custorder             as customerorder,     -- customer order code
    -- calculation for step duration (dyas) - converting interval to (seconds / 86400) = days and then round (3 digits after point)
    ROUND(
    (EXTRACT( DAY    FROM (to_timestamp(endprogress.end_time) - to_timestamp(stprogress.start_time)) ) * 86400 +
     EXTRACT( HOUR   FROM (to_timestamp(endprogress.end_time) - to_timestamp(stprogress.start_time)) ) *  3600 +
     EXTRACT( MINUTE FROM (to_timestamp(endprogress.end_time) - to_timestamp(stprogress.start_time)) ) *    60 +
     EXTRACT( SECOND FROM (to_timestamp(endprogress.end_time) - to_timestamp(stprogress.start_time)) ))/86400, 3) as order_duration_days

FROM
    productionprogress prodprogress -- baseline table for query

    -- alias stprogress: self join on [productionordercode, groupstepnumber] -> operation start timestamp
    LEFT JOIN (
        SELECT productionordercode, groupstepnumber,
            to_char(MIN(progressstartprocessdate), 'DD.MM.YYYY')||' '||to_char(MIN(progressstartprocesstime), 'HH24:Mi:SS') AS start_time
                from productionprogress
                    where progresstemplatecode = 'S1' AND progressstartprocessdate IS NOT NULL AND progressstartprocesstime IS NOT NULL
                        group by productionordercode, groupstepnumber
        ) stprogress on prodprogress.productionordercode = stprogress.productionordercode AND prodprogress.groupstepnumber = stprogress.groupstepnumber

    -- alias endprogress: self join on [productionordercode, groupstepnumber] -> operation end timestamp
    LEFT JOIN (
        SELECT productionordercode, groupstepnumber,
              to_char(MIN(progressenddate), 'DD.MM.YYYY')||' '||to_char(MIN(progressendtime), 'HH24:Mi:SS') AS end_time
                FROM productionprogress
                    WHERE progresstemplatecode = 'E1' AND progressenddate IS NOT NULL AND progressendtime IS NOT NULL
                        GROUP BY productionordercode, groupstepnumber
    ) endprogress ON prodprogress.productionordercode = endprogress.productionordercode AND prodprogress.groupstepnumber = endprogress.groupstepnumber

    -- alias proddemand: join with other tables on [productionordercode] -> for customer order and article number
    LEFT JOIN (
        select a.productionordercode, b.dlvsalorderlinesalesordercode as custorder,
            b.subcode01||'-'||b.subcode02||'-'||b.subcode03||'-'||b.subcode04||'-'||b.subcode05||'-'||b.subcode06 as article
            from productiondemandstep a, productiondemand b
                where a.productiondemandcode = b.code AND a.productiondemandcountercode = b.countercode
                    group by a.productionordercode, b.dlvsalorderlinesalesordercode,
                        b.subcode01||'-'||b.subcode02||'-'||b.subcode03||'-'||b.subcode04||'-'||b.subcode05||'-'||b.subcode06
    ) proddemand on proddemand.productionordercode = prodprogress.productionordercode
    
    -- alias fullitem: join with table [fullitemkeydecoder] -> full item name (human-readable name)
    LEFT JOIN (
        select subcode01||'-'||subcode02||'-'||subcode03||'-'||subcode04||'-'||subcode05||'-'||subcode06 as code, summarizeddescription
            from fullitemkeydecoder
                where itemtypecode in ('100', '110', '115', '200')
    ) fullitem on fullitem.code = proddemand.article
    
WHERE 
    prodprogress.progresstemplatecode = 'S1' AND to_char(prodprogress.creationdatetime, 'YYYY') in ('2017', '2018', '2019')
    -- start and end times shouldn't be null
    AND stprogress.start_time IS NOT NULL AND endprogress.end_time IS NOT NULL
    AND fullitem.summarizeddescription IS NOT NULL

GROUP BY 
    prodprogress.productionordercode, proddemand.article, prodprogress.operationcode, stprogress.start_time, endprogress.end_time, 
        proddemand.custorder, fullitem.summarizeddescription, prodprogress.machinecode, to_char(prodprogress.creationdatetime, 'YYYY')

--order by start_time desc, customerorder
ORDER BY item_code, prod_ordercode, start_time, operation) -- end of WITH clause (internal SQL query)

    SELECT
        -- production order code (party), itemcode and description (human-readable)
        sd1.prod_ordercode, sd1.item_code, sd1.item_description,
        -- start and end timestamps
        MIN(to_timestamp(sd1.start_time))                                     AS order_start, 
        MAX(to_timestamp(sd1.end_time))                                       AS order_end,
        -- extract month number (starting from 1) and year from order start date
        --EXTRACT(MONTH FROM MIN(to_timestamp(sd1.start_time)))                 AS value_month,
        --EXTRACT(YEAR  FROM MIN(to_timestamp(sd1.start_time)))                 AS value_year,
        -- summ for steps in one party
        SUM(sd1.order_duration_days)                                          AS order_duration_days,
        -- duration of the order (data type -> interval) - for check, compare with sum (above) 
        --(MAX(to_timestamp(sd1.end_time)) - MIN(to_timestamp(sd1.start_time))) AS order_duration_interval,
        -- last operation for current production order
        sd2.operation                                                         AS last_operation
    FROM
        source_data sd1, source_data sd2 -- self join of source table for last operation
    WHERE
        -- join for sd2
        sd1.prod_ordercode = sd2.prod_ordercode AND sd1.item_code = sd2.item_code AND sd1.item_description = sd2.item_description
        AND sd2.operation like '9000-%'
        -- criteria for getting last operation value
        AND sd2.end_time =
            -- we can't use function here (max), so we have to do it in separate select)
            (SELECT to_char(MAX(to_timestamp(end_time)), 'DD.MM.YYYY HH24:Mi:SS') FROM source_data
                WHERE prod_ordercode = sd1.prod_ordercode AND item_code = sd1.item_code AND item_description = sd1.item_description)
    GROUP BY
        sd1.prod_ordercode, sd1.item_code, sd1.item_description, sd2.operation
    ORDER BY
        sd1.prod_ordercode, sd1.item_code, sd1.item_description
) sd -- end of SQL query from WITH clause
GROUP BY sd.item_code, sd.item_description
ORDER BY sd.item_code, sd.item_description

-- join pseudo table with values for like expression
) JOIN (SELECT column_value filter
          FROM table(sys.odcivarchar2list(
            'Ткань полиамидная подкладочная Brown Downproof%',
            'Ткань полиэфирно-вискозная KAPHRE%',
            'Ткань полиамидная с эластаном слой 5%', 
            'Ткань полиамидная слой 8%',
            'Полотно трикотажное влагоотводящее слой 1%',
            'Полотно трикотажное флис cлой 2%',
            'Полотно трикотажное Флис 185%',
            'Полотно трикотажное Флис 285%',
            'Полотно трикотажное футерованное с ворсованием%',
            'ткань усилительная%',
            'Арт.1220%',
            'Полотно трикотажное хлопчатобумажное кулирное%',
            'Полотно трикотажное хлопчатобумажное ластичное%',
            'Арт.1215%',
            'Ткань TETI%',
            'Ткань Каспий%',
            'Ткань SOBEK SPECIAL%', 
            'Ткань HERACLES%',
            'Ткань DIONYSUS%',
            'Ткань PUMA%',
            'Ткань высокопрочная%',
            'Бязь медицинская%',
            'Ткань ANUBI%',
            'Полотно BENGAL%',
            'Полотно KOALA%',
            'Ткань Prometheus%',
            'Ткань Ptah%',
            'Ткань Sobek%',
            'Трикотажное полотно PANDA%',
            'Трикотажное полотно ROBYN%',
            'Ткань OBELISK%',
            'Ткань CAMELOT%'
          ))
    ) ON item_description LIKE filter
ORDER BY item_description, value_year, value_month

--);