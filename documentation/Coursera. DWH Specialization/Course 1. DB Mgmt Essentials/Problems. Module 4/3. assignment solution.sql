---------- Practice Problems Solution ----------  

-- #1
select custno, custname, phone, city from customer;

-- #2
select custno, custname, phone, city, state from customer
    where state = 'CO';
    
-- #3
select * from eventrequest where estcost >= 4000 order by dateheld;

-- #4
select eventno, dateheld, estaudience from eventrequest
    where (status = 'Approved' and estaudience > 9000) or 
        (status = 'Pending' and estaudience > 7000);
        
-- #5
select eventno, dateheld, customer.custno, custname 
  from eventrequest inner join customer 
  on eventrequest.custno = customer.custno
  where city = 'Boulder'
  and dateheld between '01-Dec-2022' and '31-Dec-2022';
  
-- option #1 (from solution)
SELECT EventNo, DateHeld, Customer.CustNo, CustName
 FROM EventRequest INNER JOIN Customer
 ON EventRequest.CustNo = Customer.CustNo
 WHERE City = 'Boulder'
 AND DateHeld BETWEEN '1-Dec-2022' AND '31-Dec-2022' ;
 
-- option #2 (from solution)
SELECT EventNo, DateHeld, Customer.CustNo, CustName
 FROM EventRequest, Customer
 WHERE City = 'Boulder'
 AND DateHeld BETWEEN '1-Dec-2022' AND '31-Dec-2022'
 AND EventRequest.CustNo = Customer.CustNo;
 
-- #6
select planno, avg(resourcecnt) as avgnumresources 
 from eventplanline
 where locno = 'L100'
 group by planno;

-- #7
select planno, avg(resourcecnt) as avgnumresources, count(*) as numeventsline
 from eventplanline
 where locno = 'L100'
 group by planno
 having count(*) > 1;


---------- Module 04 Assignement Solution ---------- 

-- #1
select distinct city, state, zip from customer;

-- #2
select empname, department, phone, email from employee
  where phone like '3-%';

-- #3
select * from resourcetbl 
  where rate >= 10
  and rate <= 20 
  order by rate;

-- #4
select eventno, dateauth, status from eventrequest
  where status in ('Approved', 'Denied')
  and dateauth between '01-Jul-2022' and '31-Jul-2022';

-- #5
select locno, locname from facility
  inner join location on facility.facno = location.facno
  where facname = 'Basketball arena';

-- #6
select planno, count(lineno) as linescount, sum(resourcecnt) as resourcessum from eventplanline
  group by planno
  having count(lineno) >= 1;

-- #7
select planno, count(lineno) as linescount, sum(resourcecnt) as resourcesum from eventplanline
  where timestart between TO_DATE('01-Oct-2022 00:00', 'DD-Mon-YYYY HH24:MI') and 
    TO_DATE('31-Oct-2022 23:59', 'DD-Mon-YYYY HH24:MI')
  group by planno
  having sum(resourcecnt) >= 10;







