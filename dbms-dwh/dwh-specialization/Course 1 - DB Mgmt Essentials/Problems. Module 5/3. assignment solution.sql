---------- Module 05: Practice Problems Solution ----------  

-- #1

-- #2

-- #3

-- #4

-- #5

-- #6

-- #7

---------- Module 05: Assignement Solution ---------- 

-- #1
select eventno, dateheld, eventrequest.custno, custname, eventrequest.facno, facname from eventrequest 
  inner join facility on eventrequest.facno = facility.facno
  inner join customer on customer.custno = eventrequest.custno
  where (dateheld between TO_DATE('01-Jan-2022 00:00', 'DD-Mon-YYYY HH24:MI') and 
    TO_DATE('31-Dec-2022 23:59', 'DD-Mon-YYYY HH24:MI')) 
    and city = 'Boulder';

-- #2
select 

-- #3

-- #4

-- #5

-- #6

-- #7
