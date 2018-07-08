-- Пояснения к запросу:
-- * запрос выдает результат для постановки https://bfg-integral.atlassian.net/wiki/spaces/BFGCON/pages/144637973
--      (длина простоя и обработки на конкретной машине), в данный момент запрос выдает общее суммарное время, а 
--      не среднее время (в работе)
-- * данные получены по месяцам за 2016-2018 годы
-- * по контрольному примеру - порядок данных очень похож
-- * агрегаты по неделям-месяцам-кварталам надо считать
-- * результирующие столбцы:
--      end_month         -> месяц обработки на данном станке
--      end_year          -> год обработки на данном станке
--      machine           -> наименование станка
--      queue_length      -> длина очереди ожидания (в днях) перед данным станком
--      processing_length -> длина (общая) обработки в днях на данном станке

SELECT 
    end_month, 
    end_year, 
    machine,
    sum(queue_days)      AS queue_length,
    sum(processing_days) AS processing_length
FROM (
WITH source_data AS (
SELECT
    stprogress.start_time,                                -- start timestamp for operation 
    endprogress.end_time,                                 -- end timestamp for operation
    prodprogress.groupstepnumber     as stepnumber,       -- step number for operation
    CASE 
        WHEN prodprogress.groupstepnumber > 99 THEN prodprogress.groupstepnumber - 100
        WHEN prodprogress.groupstepnumber < 100 THEN prodprogress.groupstepnumber - 10 
    END                              AS prev_stepnumber,
    prodprogress.operationcode       as operation,        -- operation code (code 9000* -> the latest operation in cycle)
    proddemand.article               as item_code,        -- article for item
    fullitem.summarizeddescription   as item_description, -- human-readable name for item
    prodprogress.productionordercode as prod_ordercode,   -- production order code
    proddemand.custorder             as customerorder,    -- customer order code
    round(quantity.qty, 3)           as quantity,         -- quatity by this step
    quantity.uom                     as units,            -- units of measurement   
    prodprogress.machinecode         as machine           -- code of machine
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
    
    -- alias quantity: get units of measurement and step quantity values
    LEFT JOIN (
        select productionordercode, groupstepnumber, primaryuomcode as uom, sum(primaryqty) as qty 
            from productionprogress where progresstemplatecode in ('P1', 'E1') 
                group by productionordercode, primaryuomcode, groupstepnumber) quantity 
                    on prodprogress.productionordercode = quantity.productionordercode and prodprogress.groupstepnumber = quantity.groupstepnumber

    -- alias proddemand: join with [productiondemandstep, productiondemand] on [productionordercode] -> for customer order and article number
    LEFT JOIN (
        select a.productionordercode, b.dlvsalorderlinesalesordercode as custorder,
            b.subcode01||'-'||b.subcode02||'-'||b.subcode03||'-'||b.subcode04||'-'||b.subcode05||'-'||b.subcode06 as article
            from productiondemandstep a, productiondemand b
                where a.productiondemandcode = b.code AND a.productiondemandcountercode = b.countercode
                    group by a.productionordercode, b.dlvsalorderlinesalesordercode,
                        b.subcode01||'-'||b.subcode02||'-'||b.subcode03||'-'||b.subcode04||'-'||b.subcode05||'-'||b.subcode06
    ) proddemand on proddemand.productionordercode = prodprogress.productionordercode
    
    -- alias fullitem: join with [fullitemkeydecoder] on [article] -> full item name (human-readable name)
    LEFT JOIN (
        select subcode01||'-'||subcode02||'-'||subcode03||'-'||subcode04||'-'||subcode05||'-'||subcode06 as code, summarizeddescription
            from fullitemkeydecoder
                where itemtypecode in ('100', '110', '115', '200')
    ) fullitem on fullitem.code = proddemand.article
    
WHERE 
    prodprogress.progresstemplatecode = 'S1' 
    -- filter by years, view should contain all time
    AND to_char(prodprogress.creationdatetime, 'YYYY') in ('2016', '2017', '2018')
    
    -- start and end times shouldn't be null (in order to take values into account)
    AND stprogress.start_time IS NOT NULL AND endprogress.end_time IS NOT NULL AND fullitem.summarizeddescription IS NOT NULL
    
    -- !!! remove this filter !!!    
    --AND proddemand.article = '27.00013-003D-0-0-000655-FHYDFIL001'
    --AND proddemand.article = '22.001164-001-D91200-L5A-600001-CSCSIXX001'
    --AND extract(month from to_timestamp(stprogress.start_time)) in (11, 12)
    --AND extract(year from to_timestamp(stprogress.start_time)) = 2017
    --AND prodprogress.operationcode like '9000-%'    
    -- !!! remove this filter !!!
    
GROUP BY 
    prodprogress.productionordercode, prodprogress.groupstepnumber, proddemand.article, prodprogress.operationcode, stprogress.start_time, 
        endprogress.end_time, proddemand.custorder, fullitem.summarizeddescription, prodprogress.machinecode, 
            to_char(prodprogress.creationdatetime, 'YYYY'), quantity.uom, quantity.qty, prodprogress.machinecode
--order by start_time desc, customerorder
ORDER BY item_code, prod_ordercode, start_time, operation
) -- end of WITH SQL query

    SELECT 
        sd1.start_time, 
        sd1.end_time,
        EXTRACT(MONTH FROM to_timestamp(sd1.end_time)) AS end_month,
        EXTRACT(YEAR  FROM to_timestamp(sd1.end_time)) AS end_year,
        sd1.stepnumber, 
        sd1.prev_stepnumber, 
        sd1.operation, 
        sd2.end_time                                   AS prev_end_time,
        -- queue length (waiting time before machine) in days -> convert interval to (seconds / 86400) = days and then round (3 digits after point)
        ROUND((EXTRACT( DAY    FROM (to_timestamp(sd1.start_time) - to_timestamp(sd2.end_time)) ) * 86400 +
               EXTRACT( HOUR   FROM (to_timestamp(sd1.start_time) - to_timestamp(sd2.end_time)) ) *  3600 +
               EXTRACT( MINUTE FROM (to_timestamp(sd1.start_time) - to_timestamp(sd2.end_time)) ) *    60 +
               EXTRACT( SECOND FROM (to_timestamp(sd1.start_time) - to_timestamp(sd2.end_time)) ))/86400, 3) as queue_days,
        -- processing time on concrete machine in days -> convert interval to (seconds / 86400) = days and then round (3 digits after point)
        ROUND((EXTRACT( DAY    FROM (to_timestamp(sd1.end_time) - to_timestamp(sd1.start_time)) ) * 86400 +
               EXTRACT( HOUR   FROM (to_timestamp(sd1.end_time) - to_timestamp(sd1.start_time)) ) *  3600 +
               EXTRACT( MINUTE FROM (to_timestamp(sd1.end_time) - to_timestamp(sd1.start_time)) ) *    60 +
               EXTRACT( SECOND FROM (to_timestamp(sd1.end_time) - to_timestamp(sd1.start_time)) ))/86400, 3) as processing_days,
        sd1.machine,
        sd1.item_code,
        sd1.item_description, 
        sd1.prod_ordercode
    FROM source_data sd1
        JOIN source_data sd2 
            ON sd1.item_code = sd2.item_code AND sd1.item_description = sd2.item_description 
                AND sd1.prod_ordercode = sd2.prod_ordercode AND sd1.prev_stepnumber = sd2.stepnumber
)
GROUP BY end_month, end_year, machine
ORDER BY end_month, end_year, machine
