-- Пояснения к запросу:
-- * запрос выдает результат для постановки https://bfg-integral.atlassian.net/wiki/spaces/BFGCON/pages/137363457
--      (производительность отделки)
-- * данные получены по дням за 2016-2018 годы
-- * по периоду из контрольного примера (июнь 2017) бОльшая часть данных совпадает, 
--      там где есть отличия - они незначительны
-- * агрегаты по неделям-месяцам-кварталам надо считать
-- * результирующие столбцы:
--      day_end        -> день для которого подсчитана производительность
--      month_end      -> месяц для которого подсчитана производительность
--      year_end       -> год для которого подсчитана производительность
--      total_quantity -> суммарная производительность за указанный день
-- * БД очень медленно обрабатывает данный запрос (иногда до 15 минут), видимо что-то не так с индексами
--      по полям. Если убрать поля quantity/summarizeddescription производительность в разы выше...

SELECT
    sd.day_end, sd.month_end, sd.year_end, sum(quantity) as total_quantity
FROM (
WITH source_data as (
-- sql for get all operations with quantity/article/name/timestamps
SELECT
    stprogress.start_time,                                -- start timestamp for each operation
    endprogress.end_time,                                 -- end timestamp for each operation
    prodprogress.operationcode       as operation,        -- operation code (code 9000* -> the latest operation in cycle)
    proddemand.article               as item_code,        -- article for item
    fullitem.summarizeddescription   as item_description, -- human-readable name for item
    prodprogress.productionordercode as prod_ordercode,   -- production order code
    --proddemand.custorder             as customerorder,    -- customer order code
    round(quantity.qty, 3)           as quantity         -- quatity for this step
    --quantity.uom                     as units             -- units of measurement   
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
    prodprogress.progresstemplatecode = 'S1' AND to_char(prodprogress.creationdatetime, 'YYYY') in ('2016', '2017', '2018')
    -- start and end times shouldn't be null
    AND stprogress.start_time IS NOT NULL AND endprogress.end_time IS NOT NULL AND fullitem.summarizeddescription IS NOT NULL
    
GROUP BY 
    prodprogress.productionordercode, proddemand.article, prodprogress.operationcode, stprogress.start_time, endprogress.end_time, 
        proddemand.custorder, fullitem.summarizeddescription, prodprogress.machinecode, to_char(prodprogress.creationdatetime, 'YYYY'),
            quantity.qty, quantity.uom
--order by start_time desc, customerorder
--ORDER BY item_code, prod_ordercode, start_time, operation -- do we need ordering here?
) -- end of WITH clause (internal SQL query)
    
    -- query for getting cycles by months and years (all cycles separately), in next query we will group them by months and years
    SELECT
        sd1.prod_ordercode,                                                       -- production order code (cycle/party) 
        sd1.item_code,                                                            -- item code (article)
        sd1.item_description,                                                     -- item description (human-readable)
        EXTRACT(DAY   FROM MAX(to_timestamp(sd1.end_time)))   AS day_end,         -- day number for cycle end timestamp
        EXTRACT(MONTH FROM MAX(to_timestamp(sd1.end_time)))   AS month_end,       -- month number (from 1) for cycle end timestamp
        EXTRACT(YEAR  FROM MAX(to_timestamp(sd1.end_time)))   AS year_end,        -- year number for cycle end timestamp
        first_op.operation                                    AS first_operation, -- first operation for current production order
        last_op.operation                                     AS last_operation,  -- last operation for current production order
        last_op.quantity
    FROM
        source_data sd1, source_data last_op, source_data first_op -- self join of source table for first/last operations
    WHERE
        -- join for last_op (get last operation)
        sd1.prod_ordercode = last_op.prod_ordercode AND sd1.item_code = last_op.item_code AND sd1.item_description = last_op.item_description
        -- criteria for getting last operation value
        AND last_op.end_time =
            -- we can't use function here (max), so we have to do it in separate select)
            (SELECT to_char(MAX(to_timestamp(end_time)), 'DD.MM.YYYY HH24:Mi:SS') FROM source_data
                WHERE prod_ordercode = sd1.prod_ordercode AND item_code = sd1.item_code AND item_description = sd1.item_description)
                
        -- join for first_op (get first operation)
        AND sd1.prod_ordercode = first_op.prod_ordercode AND sd1.item_code = first_op.item_code AND sd1.item_description = first_op.item_description
        -- criteria for getting last operation value
        AND first_op.start_time =
            -- we can't use function here (min), so we have to do it in separate select)
            (SELECT to_char(MIN(to_timestamp(start_time)), 'DD.MM.YYYY HH24:Mi:SS') FROM source_data
                WHERE prod_ordercode = sd1.prod_ordercode AND item_code = sd1.item_code AND item_description = sd1.item_description)
                
        AND last_op.operation like '9000-%'        -- last operation should be '9000-*'
        AND first_op.operation not like '9000-%'   -- first operation shouldn't be '9000-*'
        --AND first_op.operation not like '9???-%'   -- first operation shouldn't be '9???-*' (templates: 9???-% or 9*)
    GROUP BY
        sd1.prod_ordercode, sd1.item_code, sd1.item_description, 
        last_op.operation, first_op.operation, last_op.quantity
    --ORDER BY day_end, month_end, year_end -- do we need ordering here?
) sd
GROUP BY sd.day_end, sd.month_end, sd.year_end
ORDER BY sd.month_end, sd.year_end, sd.day_end  -- order first by month, then by year, and in the end - by day
    
