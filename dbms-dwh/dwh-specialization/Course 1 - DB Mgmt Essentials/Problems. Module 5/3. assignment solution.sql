---------- Module 05: Practice Problems Solution ----------  

-- #1
select eventno, dateheld, eventrequest.custno, custname, eventrequest.facno, facname from eventrequest 
  inner join facility on eventrequest.facno = facility.facno
  inner join customer on customer.custno = eventrequest.custno
  where (dateheld between TO_DATE('01-Jan-2022 00:00', 'DD-Mon-YYYY HH24:MI') and 
    TO_DATE('31-Dec-2022 23:59', 'DD-Mon-YYYY HH24:MI')) 
    and city = 'Boulder';

-- #2
select customer.custno, custname, eventno, dateheld, facility.facno, facname, (estcost/estaudience) as estcostperperson from eventrequest
  inner join facility on eventrequest.facno = facility.facno
  inner join customer on eventrequest.custno = customer.custno
  where dateheld between '1-Jan-2022' and '31-Dec-2022' 
  and estcost/estaudience < 0.2;

-- #3
select customer.custno, custname, sum(estcost) as total from eventrequest
  inner join customer on customer.custno = eventrequest.custno
  where status = 'Approved'
  group by customer.custno, custname;

-- #4
select employee.empno, empname, extract(month from workdate) as month, count(planno) as eventplanscount, sum(estcost) as estcostsum from eventplan
  inner join employee on eventplan.empno = employee.empno
  inner join eventrequest on eventplan.eventno = eventrequest.eventno
  where workdate between '1-Jan-2022' and '31-Dec-2022'
  group by employee.empno, empname, extract(month from workdate);

-- #5
insert into customer(custno, custname, address, internal, contact, phone, city, state, zip) 
  values('D101', 'Dmitrii Gusev', 'CZ, Prague, City Center', 'Y', 'Dmitrii Gusev', '12345678', 'Prague', 'CZ', '15000');

-- #6
update resourcetbl set rate = rate * 1.1
  where resname = 'nurse';

-- #7
delete from customer where custno = 'D101';


---------- Module 05: Assignement Solution ---------- 


--- SELECT statements Problems

-- #1
select eventrequest.eventno, dateheld, count(planno) planscount from eventrequest
  inner join eventplan on eventrequest.eventno = eventplan.eventno
  where workdate between '1-Dec-2022' and '31-Dec-2022'
  group by eventrequest.eventno, dateheld
  having count(planno) > 1;

-- #2
select planno, eventplan.eventno, workdate, activity from eventplan
  inner join eventrequest on eventplan.eventno = eventrequest.eventno
  inner join facility on eventrequest.facno = facility.facno
  where workdate between '1-Dec-2022' and '31-Dec-2022'
  and facname = 'Basketball arena';

-- #3
select eventrequest.eventno, dateheld, status, estcost from eventrequest
  inner join eventplan on eventrequest.eventno = eventplan.eventno 
  inner join employee on eventplan.empno = employee.empno
  inner join facility on eventrequest.facno = facility.facno
  where empname = 'Mary Manager'
  and facname = 'Basketball arena';
  
-- #4
select eventplanline.planno, lineno, resname, resourcecnt, locname, timestart, timeend from eventplanline
  inner join resourcetbl on eventplanline.resno = resourcetbl.resno
  inner join location on eventplanline.locno = location.locno
  inner join facility on location.facno = facility.facno
  inner join eventplan on eventplanline.planno = eventplan.planno
  where facname = 'Basketball arena'
  and activity = 'Operation'
  and workdate between '01-Oct-2022' and '31-Dec-2022';

-- #5
select eventplanline.planno, sum(resourcecnt * rate) as resourcecost from eventplanline
  inner join resourcetbl on eventplanline.resno = resourcetbl.resno
  inner join eventplan on eventplanline.planno = eventplan.planno
  where workdate between '01-Dec-2022' and '31-Dec-2022'
  group by eventplanline.planno
  having sum(resourcecnt * rate) > 50;


--- Database Modification Problems

-- #1
insert into facility values ('SP111', 'Swimming Pool');

-- #2
insert into location values ('D01', 'SP111', 'Door');

-- #3
insert into location values ('D02', 'SP111', 'Locker Room');

-- #4
update location set locname = 'Gate' 
  where locname = 'Door';

-- #5
delete from location 
  where locno in ('D01', 'D02');


--- SQL Statements with Errors and Poor Formatting

-- #1 (syntax, redundancy, semantic - semantic)
SELECT eventrequest.eventno, dateheld, status, estcost
FROM eventrequest, employee, facility, eventplan
WHERE estaudience > 5000
 AND eventplan.empno = employee.empno
 AND eventrequest.facno = facility.facno
 AND eventplan.eventno = eventrequest.eventno -- added this line (semantic)
 AND facname = 'Football stadium'
 AND empname = 'Mary Manager';

-- #2 (syntax, redundancy, semantic - redundancy)
SELECT DISTINCT eventrequest.eventno, dateheld, status, estcost
FROM eventrequest, eventplan
WHERE estaudience > 4000
 AND eventplan.eventno = eventrequest.eventno; 
 -- GROUP BY eventrequest.eventno, dateheld, status, estcost; -- commented this line (redundancy)
 
-- #3 (syntax, redundancy, semantic - redundancy)
SELECT DISTINCT eventrequest.eventno, dateheld, status, estcost
-- FROM eventrequest, employee, facility, eventplan -- commented this line (redundancy)
FROM eventrequest, facility, eventplan
WHERE estaudience > 5000
 -- AND eventplan.empno = employee.empno -- commented this line (redundancy)
 AND eventrequest.facno = facility.facno
 AND eventplan.eventno = eventrequest.eventno
 AND facname = 'Football stadium';

-- #4 (syntax, redundancy, semantic - ?)
SELECT DISTINCT eventrequest.eventno, dateheld, status, estcost -- fixed ambigous column [eventno]
FROM eventrequest, employee, eventplan
WHERE estaudience BETWEEN 5000 AND 10000 -- fixed operator BETWEEN
AND eventplan.empno = employee.empno
 AND eventrequest.eventno = eventplan.eventno
 AND empname = 'Mary Manager';

-- #5 (identify poor coding practices)
SELECT eventplan.planno, lineno, resname, resourcecnt, timestart, timeend
 FROM eventrequest, facility, eventplan, eventplanline, resourcetbl
 WHERE estaudience = 10000
 AND eventplan.planno = eventplanline.planno 
 AND eventrequest.facno = facility.facno
 AND facname = 'Basketball arena' 
 AND eventplanline.resno = resourcetbl.resno
 AND eventrequest.eventno = eventplan.eventno;
