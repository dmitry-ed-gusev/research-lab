--alter session set current_schema = now4;
select
    sd.itemcode, round(avg(sd.order_duration_days), 3) as order_duration_days,
    --min(sd.order_start) as order_start,
    extract(month from min(sd.order_start)) as value_month,
    extract(year from min(sd.order_start)) as value_year,
    fi.summarizeddescription as item_description
from
(with source_data as
 (select
    -- start and end time
    --min(to_char(progressstartprocessdate, 'DD.MM.YYYY')||' '||to_char(progressstartprocesstime, 'HH24:Mi:SS')) as STARTTIME,
    stprog.START_TIME,
    enprog.END_TIME,
    -- current step/operation duration
    (to_timestamp(enprog.END_TIME) - to_timestamp(stprog.START_TIME)) as STEP_DURATION,
    -- operation start month number (numbers starting from 1 -> January)
    EXTRACT(month FROM to_date(stprog.START_TIME, 'DD.MM.YYYY HH24:Mi:SS')) as STEP_START_MONTH,
    -- operation year
    to_char(pprogress.creationdatetime, 'YYYY') as STEP_YEAR,
    pprogress.groupstepnumber                   as STEPNUMBER,
    pprogress.operationcode                     as OPERATION,
    tabl.art                                    as ITEMCODE,
    pprogress.machinecode                       as MACHINE,
    pprogress.productionordercode               as PROD_ORDERCODE,
    round(pprog.qty)                            as STEPQUANTITY,
    pprog.uom                                   as UOM,
    tabl.custorder                              as CUSTOMERORDER
 from
    -- baseline table for query
    productionprogress pprogress

    -- alias STPROG: self join on [productionordercode, groupstepnumber] -> get operation start timestamp
    left join (
        select productionordercode, groupstepnumber,
            --min(to_char(progressstartprocessdate, 'DD.MM.YYYY')||' '||to_char(progressstartprocesstime, 'HH24:Mi:SS')) as START_TIME
            to_char(min(progressstartprocessdate), 'DD.MM.YYYY')||' '||to_char(min(progressstartprocesstime), 'HH24:Mi:SS') as START_TIME
                from productionprogress
                    where progresstemplatecode = 'S1'
                        group by productionordercode, groupstepnumber
        ) STPROG on pprogress.productionordercode = stprog.productionordercode and pprogress.groupstepnumber = stprog.groupstepnumber

    -- alias ENPROG: self join on [productionordercode, groupstepnumber] -> get operation end timestamp
    left join (
        select productionordercode, groupstepnumber,
            --max(to_char(progressenddate, 'DD.MM.YYYY')||' '||to_char(progressendtime, 'HH24:Mi:SS')) as END_TIME
              to_char(min(progressenddate), 'DD.MM.YYYY')||' '||to_char(min(progressendtime), 'HH24:Mi:SS') as END_TIME
                from productionprogress
                    where progresstemplatecode = 'E1'
                        group by productionordercode, groupstepnumber
    ) ENPROG on pprogress.productionordercode = enprog.productionordercode and pprogress.groupstepnumber = enprog.groupstepnumber

    -- alias PPROG: self join on [productionordercode, groupstepnumber] -> for operation total quantity (sum)
    left join (
        select productionordercode, groupstepnumber, primaryuomcode as uom, sum(primaryqty) as qty
        from productionprogress
        where progresstemplatecode in ('P1', 'E1')
        group by productionordercode, primaryuomcode, groupstepnumber
    ) pprog on pprogress.productionordercode = pprog.productionordercode and pprogress.groupstepnumber = pprog.groupstepnumber

    -- alias TABL: join with other tables on [productionordercode] -> for customer order and article number
    left join (
        select a.productionordercode, b.dlvsalorderlinesalesordercode as custorder,
            b.subcode01||'-'||b.subcode02||'-'||b.subcode03||'-'||b.subcode04||'-'||b.subcode05||'-'||b.subcode06 as art
        from productiondemandstep a, productiondemand b
        where a.productiondemandcode = b.code and a.productiondemandcountercode = b.countercode
        group by a.productionordercode, b.dlvsalorderlinesalesordercode,
            b.subcode01||'-'||b.subcode02||'-'||b.subcode03||'-'||b.subcode04||'-'||b.subcode05||'-'||b.subcode06
    ) tabl on tabl.productionordercode = pprogress.productionordercode

 -- get dispatching transactions for concrete year
 where pprogress.progresstemplatecode = 'S1' and to_char(pprogress.creationdatetime, 'YYYY') in ('2017', '2018', '2019')
    -- start timestamp always should be filled with reasonable value
    and progressstartprocessdate is not null and progressstartprocesstime is not null
    -- !!!!!! remove line below !!!!!!
    --and  pprogress.productionordercode = '0002749Z0'

 group by stprog.START_TIME, enprog.END_TIME, pprogress.groupstepnumber, pprogress.operationcode, tabl.art,
    pprogress.machinecode, pprogress.productionordercode, round(pprog.qty), pprog.uom, tabl.custorder,
    to_char(pprogress.creationdatetime, 'YYYY')

 order by start_time desc, customerorder) -- end of WITH

-- select from result set "all transactions for specific year(s)"
select
    -- what? ordercode/itemcode
    sd1.prod_ordercode, sd1.itemcode,
    -- start and end timestamps
    min(to_timestamp(sd1.start_time)) as order_start, max(to_timestamp(sd1.end_time)) as order_end,
    -- extract month number (starting from 1) from order start date
    EXTRACT(MONTH FROM min(to_timestamp(sd1.start_time))) as order_start_month,
    -- duration of the order (data type -> interval)
    (max(to_timestamp(sd1.end_time)) - min(to_timestamp(sd1.start_time))) as order_duration,

    -- calculation for duration - converting interval to (seconds / 86400) = days and then round (3 digits after point)
    ROUND((EXTRACT( DAY    FROM (max(to_timestamp(sd1.end_time)) - min(to_timestamp(sd1.start_time))) ) * 86400 +
     EXTRACT( HOUR   FROM (max(to_timestamp(sd1.end_time)) - min(to_timestamp(sd1.start_time))) ) *  3600 +
     EXTRACT( MINUTE FROM (max(to_timestamp(sd1.end_time)) - min(to_timestamp(sd1.start_time))) ) *    60 +
     EXTRACT( SECOND FROM (max(to_timestamp(sd1.end_time)) - min(to_timestamp(sd1.start_time))) ))/86400, 3) as order_duration_days,

    -- last operation for current production order
    sd2.operation
from
    -- self join of source table
    source_data sd1, source_data sd2
where
    -- join for sd2
    sd1.prod_ordercode = sd2.prod_ordercode and sd1.itemcode = sd2.itemcode
    -- criteria for getting last operation value
    and sd2.end_time =
        -- we can't use function here (max), so we have to do it in separate select)
        (select to_char(max(to_timestamp(end_time)), 'DD.MM.YYYY HH24:Mi:SS') from source_data
            where prod_ordercode = sd1.prod_ordercode and itemcode = sd1.itemcode)
    and sd2.operation like '9000%'
group by
    sd1.prod_ordercode, sd1.itemcode, sd2.operation
order by
    sd1.itemcode, sd1.prod_ordercode) sd

-- get human-readable description of item
left join
    (select subcode01||'-'||subcode02||'-'||subcode03||'-'||subcode04||'-'||subcode05||'-'||subcode06 as code, summarizeddescription
        from fullitemkeydecoder 
            where itemtypecode in ('100', '110', '115', '200')) fi on fi.code = sd.itemcode

group by sd.itemcode, fi.summarizeddescription
order by sd.itemcode