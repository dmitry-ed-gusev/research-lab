-- set current session value
alter session set current_schema = now4;

-- drop view(s)
drop view now4.exp_all_transactions_view; -- view with all operations
drop view now4.exp_all_parties_view;      -- view with all parties

-- create view: all operations
CREATE VIEW now4.exp_all_transactions_view as
SELECT
    stprogress.start_time,                                -- start timestamp for operation 
    endprogress.end_time,                                 -- end timestamp for operation
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
    AND to_char(prodprogress.creationdatetime, 'YYYY') in ('2017', '2018')
    
    -- start and end times shouldn't be null (in order to take values into account)
    AND stprogress.start_time IS NOT NULL AND endprogress.end_time IS NOT NULL AND fullitem.summarizeddescription IS NOT NULL
    
    -- !!! remove this filter !!!    
    AND proddemand.article = '27.00013-003D-0-0-000655-FHYDFIL001'
    --AND extract(month from to_timestamp(stprogress.start_time)) in (11, 12)
    --AND extract(year from to_timestamp(stprogress.start_time)) = 2017
    --AND prodprogress.operationcode like '9000-%'    
    -- !!! remove this filter !!!
    
GROUP BY 
    prodprogress.productionordercode, proddemand.article, prodprogress.operationcode, stprogress.start_time, endprogress.end_time, 
        proddemand.custorder, fullitem.summarizeddescription, prodprogress.machinecode, to_char(prodprogress.creationdatetime, 'YYYY'),
            quantity.uom, quantity.qty, prodprogress.machinecode
--order by start_time desc, customerorder
ORDER BY item_code, prod_ordercode, start_time, operation;

-- create view: all parties with related values
CREATE VIEW now4.exp_all_parties_view as
-- get cycles by months and years (all cycles separately), in next query we will group them by months and years
SELECT
    sd1.prod_ordercode,                                                       -- production order code (cycle/party) 
    sd1.item_code,                                                            -- item code (article)
    sd1.item_description,                                                     -- item description (human-readable)
    MIN(to_timestamp(sd1.start_time))                     AS order_start,     -- cycle start timestamp
    MAX(to_timestamp(sd1.end_time))                       AS order_end,       -- cycle end timestamp
    EXTRACT(MONTH FROM MIN(to_timestamp(sd1.start_time))) AS month_start,     -- month number (from 1) for cycle start date
    EXTRACT(YEAR  FROM MIN(to_timestamp(sd1.start_time))) AS year_start,      -- year number for cycle start date
    EXTRACT(MONTH FROM MAX(to_timestamp(sd1.end_time)))   AS month_end,       -- month number (from 1) for cycle end date
    EXTRACT(YEAR  FROM MAX(to_timestamp(sd1.end_time)))   AS year_end,        -- year number for cycle end date
    sd3.operation                                         AS first_operation, -- first operation for current production order
    sd2.operation                                         AS last_operation,  -- last operation for current production order
    -- calculation for party duration (days) - converting interval to (seconds / 86400) => days and then round (3 digits after point)
    ROUND((EXTRACT( DAY    FROM (max(to_timestamp(sd1.end_time)) - min(to_timestamp(sd1.start_time))) ) * 86400 +
           EXTRACT( HOUR   FROM (max(to_timestamp(sd1.end_time)) - min(to_timestamp(sd1.start_time))) ) *  3600 +
           EXTRACT( MINUTE FROM (max(to_timestamp(sd1.end_time)) - min(to_timestamp(sd1.start_time))) ) *    60 +
           EXTRACT( SECOND FROM (max(to_timestamp(sd1.end_time)) - min(to_timestamp(sd1.start_time))) ))/86400, 3) as order_duration_days    
FROM
    -- self join of source view for first/last operations
    now4.exp_all_transactions_view sd1, now4.exp_all_transactions_view sd2, now4.exp_all_transactions_view sd3 
WHERE
    -- join for sd2
    sd1.prod_ordercode = sd2.prod_ordercode AND sd1.item_code = sd2.item_code AND sd1.item_description = sd2.item_description
    -- criteria for getting last operation value
    AND sd2.end_time =
        -- we can't use function here (max), so we have to do it in separate select)
        (SELECT to_char(MAX(to_timestamp(end_time)), 'DD.MM.YYYY HH24:Mi:SS') FROM now4.exp_all_transactions_view
            WHERE prod_ordercode = sd1.prod_ordercode AND item_code = sd1.item_code AND item_description = sd1.item_description)
    
    -- join for sd3
    AND sd1.prod_ordercode = sd3.prod_ordercode AND sd1.item_code = sd3.item_code AND sd1.item_description = sd3.item_description
    -- criteria for getting last operation value
    AND sd3.start_time =
        -- we can't use function here (min), so we have to do it in separate select)
        (SELECT to_char(MIN(to_timestamp(start_time)), 'DD.MM.YYYY HH24:Mi:SS') FROM now4.exp_all_transactions_view
            WHERE prod_ordercode = sd1.prod_ordercode AND item_code = sd1.item_code AND item_description = sd1.item_description)
    
    -- filter by last operation value
    AND sd2.operation like '9000-%'     -- last operation should be '9000-*'
    AND sd3.operation not like '9000-%' -- first operation shouldn't be '9000-*'
    --AND sd3.operation not like '9???-%' -- first operation shouldn't be '9???-*' (templates: 9???-% or 9*)
    
GROUP BY
    sd1.prod_ordercode, sd1.item_code, sd1.item_description, 
    sd2.operation, sd3.operation;
