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

-- #1

-- #2

-- #3

-- #4

-- #5

-- #6

-- #7
