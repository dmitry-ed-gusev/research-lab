-- Course1 Module05 Assignment Answers - PostgreSQL

-- Query Formulation Answers

1.
	
-- Condition using a default date format	
SELECT eventrequest.eventno, dateheld, COUNT(*) "Number of Plans"
FROM eventrequest, eventplan
WHERE eventplan.workdate BETWEEN '2022-12-01' AND '2022-12-31'
  AND eventrequest.eventno = eventplan.eventno
GROUP BY eventrequest.eventno, dateheld
HAVING COUNT(*) > 1;

2.

-- Condition using a default date format	
SELECT eventplan.planno, eventrequest.eventno, workdate, activity
FROM eventrequest, eventplan, facility
WHERE eventplan.workdate BETWEEN '2022-12-01' AND '2022-12-31'
  AND eventrequest.eventno = eventplan.eventno 
  AND eventrequest.facno = facility.facno
  AND facname = 'Basketball arena';

SELECT eventplan.planno, eventrequest.eventno, workdate, activity
FROM ( eventrequest INNER JOIN eventplan 
                  ON eventrequest.eventno = eventplan.eventno ) 
               INNER JOIN Facility ON eventrequest.facno = facility.facno
WHERE eventplan.workdate BETWEEN '2022-12-01' AND '2022-12-31'
  AND facname = 'Basketball arena';

SELECT eventplan.planno, eventrequest.eventno, workdate, activity
FROM eventrequest, eventplan
WHERE eventplan.workdate BETWEEN '2022-12-01' AND '2022-12-31'
  AND eventrequest.eventno = eventplan.eventno 
  AND facno IN
      ( SELECT facno FROM facility WHERE facname = 'Basketball arena' );

3.	

-- Condition using a default date format and cross product style	
SELECT eventrequest.eventno, dateheld, status, estcost
FROM eventrequest, employee, facility, eventplan
WHERE eventplan.workdate BETWEEN '2022-10-01' AND '2022-12-31'
  AND eventplan.empno = employee.empno AND eventrequest.facno = facility.facno
  AND facname = 'Basketball arena' AND empname = 'Mary Manager'
  AND eventrequest.eventno = eventplan.eventno;

-- Alternative solution with join operator style
SELECT eventrequest.eventno, dateheld, status, estcost
FROM ( ( eventrequest INNER JOIN eventplan 
                  ON eventrequest.eventno = eventplan.eventno ) 
               INNER JOIN Facility ON eventrequest.facno = facility.facno )
               INNER JOIN employee ON eventplan.empno = employee.empno
WHERE eventplan.workdate BETWEEN '2022-10-01' AND '2022-12-31'
  AND facname = 'Basketball arena' AND empname = 'Mary Manager';

-- Alternative solution with Type I nested query join style
SELECT eventrequest.eventno, dateheld, status, estcost
FROM eventrequest, eventplan
WHERE eventplan.workdate BETWEEN '2022-10-01' AND '2022-12-31'
  AND eventrequest.eventno = eventplan.eventno 
  AND facno IN
      ( SELECT facno FROM facility WHERE facname = 'Basketball arena' )
  AND empno IN
      ( SELECT empno FROM employee WHERE empname = 'Mary Manager' );

4.	
-- The first solution is preferred because it involves one less table. 
-- However, credit for the second answer as it involves different join operations.
-- Both use the cross product join style

SELECT eventplan.planno, lineno, locname, resname, resourcecnt, timestart, timeend
FROM facility, eventplan, eventplanline, resourcetbl, location
WHERE eventplan.workdate BETWEEN '2022-10-01' AND '2022-12-31'
  AND eventplan.planno = eventplanline.planno AND location.facno = facility.facno
  AND facname = 'Basketball arena' AND eventplanline.resno = resourcetbl.resno
  AND location.locno = eventplanline.locno 
  AND eventplan.activity = 'Operation';

-- Alternative solution with extra table
SELECT eventplan.planno, lineno, locname, resname, resourcecnt, timestart, timeend
FROM eventrequest, facility, eventplan, eventplanline, resourcetbl, location
WHERE eventplan.workdate BETWEEN '2022-10-01' AND '2022-12-31'
  AND eventplan.planno = eventplanline.planno AND eventrequest.facno = facility.facno
  AND facname = 'Basketball arena' AND eventplanline.resno = resourcetbl.resno
  AND eventrequest.eventno = eventplan.eventno 
  AND location.locno = eventplanline.locno 
  AND eventplan.activity = 'Operation';

5.

SELECT EventPlan.Planno, SUM(ResourceCnt * Rate) AS SumResCost
FROM EventPlan, EventPlanLine, ResourceTbl
WHERE EventPlan.PlanNo = EventPlanLine.PlanNo
  AND EventPlanLine.ResNo = ResourceTbl.ResNo
  AND WorkDate BETWEEN '2022-12-01' AND '2022-12-31'
GROUP BY EventPlan.Planno
HAVING SUM(ResourceCnt * Rate) > 50;

SELECT EventPlan.Planno, SUM(ResourceCnt * Rate) AS SumResCost
FROM EventPlan INNER JOIN EventPlanLine ON EventPlan.PlanNo = EventPlanLine.PlanNo
   INNER JOIN ResourceTbl ON EventPlanLine.ResNo = ResourceTbl.ResNo
WHERE WorkDate BETWEEN '2022-12-01' AND '2022-12-31'
GROUP BY EventPlan.Planno
HAVING SUM(ResourceCnt * Rate) > 50;

Database Modification Answers

1.	
INSERT INTO Facility ( FacNo, FacName )
VALUES ('F107', 'Swimming Pool');

2.	
INSERT INTO Location ( LocNo, FacNo, LocName )
VALUES ('L107', 'F107', 'Door');

3.	
INSERT INTO Location ( LocNo, FacNo, LocName )
VALUES ('L108', 'F107', 'Locker Room');

4.	
UPDATE Location SET LocName = 'Gate'
WHERE LocNo = 'L107'; 

5.
DELETE Location
WHERE LocNo = 'L108';

SQL statements with errors and poor formatting

1.
-- Original statement
-- Semantic error: missing join condition (eventrequest-eventplan)

SELECT eventrequest.eventno, dateheld, status, estcost
FROM eventrequest, employee, facility, eventplan
WHERE estaudience > 5000
  AND eventplan.empno = employee.empno 
  AND eventrequest.facno = facility.facno
  AND facname = 'Football stadium' 
  AND empname = 'Mary Manager';

-- Corrected statement

SELECT eventrequest.eventno, dateheld, status, estcost
FROM eventrequest, employee, facility, eventplan
WHERE estaudience > 5000
  AND eventplan.empno = employee.empno 
  AND eventrequest.facno = facility.facno
  AND eventrequest.eventno = eventplan.eventno
  AND facname = 'Football stadium' 
  AND empname = 'Mary Manager';

2.
-- Original statement
-- Redundancy error: GROUP BY clause not needed

SELECT DISTINCT eventrequest.eventno, dateheld, status, estcost
FROM eventrequest, eventplan
WHERE estaudience > 4000
  AND eventplan.eventno = eventrequest.eventno 
GROUP BY eventrequest.eventno, dateheld, status, estcost;

-- Corrected statement with no GROUP BY clause

SELECT DISTINCT eventrequest.eventno, dateheld, status, estcost
FROM eventrequest, eventplan
WHERE estaudience > 4000
  AND eventplan.eventno = eventrequest.eventno;

3.
-- Original statement
-- Redundancy error: extra table (employee)

SELECT DISTINCT eventrequest.eventno, dateheld, status, estcost
FROM eventrequest, employee, facility, eventplan
WHERE estaudience > 5000
  AND eventplan.empno = employee.empno 
  AND eventrequest.facno = facility.facno
  AND eventplan.eventno = eventrequest.eventno 
  AND facname = 'Football stadium';

-- Corrected statement

SELECT DISTINCT eventrequest.eventno, dateheld, status, estcost
FROM eventrequest, facility, eventplan
WHERE estaudience > 5000
  AND eventrequest.facno = facility.facno
  AND eventplan.eventno = eventrequest.eventno 
  AND facname = 'Football stadium';

4.
-- Original statement
-- Syntax errors: misspelled keyword (BETWEN) and unqualified name (eventno)

SELECT DISTINCT eventno, dateheld, status, estcost
FROM eventrequest, employee, eventplan
WHERE estaudience BETWEN 5000 AND 10000
  AND eventplan.empno = employee.empno 
  AND eventrequest.eventno = eventplan.eventno
  AND empname = 'Mary Manager';

-- Corrected statement
-- Note: eventno can be qualified with either eventrequest or eventplan table

SELECT DISTINCT eventrequest.eventno, dateheld, status, estcost
FROM eventrequest, employee, eventplan
WHERE estaudience BETWEEN 5000 AND 10000
  AND eventplan.empno = employee.empno 
  AND eventrequest.eventno = eventplan.eventno
  AND empname = 'Mary Manager';

5.
-- Original statement with poor coding practices
-- Poor clause and condition alignment
-- Incompatible constant in WHERE condition: estaudience = '10000'

      SELECT eventplan.planno, lineno, resname, 
resourcecnt, timestart, timeend
    FROM eventrequest, facility, eventplan, 
eventplanline, resourcetbl
     WHERE estaudience = '10000' AND eventplan.planno = 
eventplanline.planno AND eventrequest.facno 
= facility.facno
      AND facname = 
'Basketball arena' AND 
   eventplanline.resno = resourcetbl.resno
      AND eventrequest.eventno = eventplan.eventno;

-- Rewritten statement with improved coding practices
-- Note: many ways to reformat the statement
-- Should align clauses and conditions
-- Numeric constant in WHERE condition: estaudience = 10000

  SELECT eventplan.planno, lineno, resname, resourcecnt, timestart, timeend
    FROM eventrequest, facility, eventplan, eventplanline, resourcetbl
    WHERE estaudience = 10000
      AND eventplan.planno = eventplanline.planno 
      AND eventrequest.facno = facility.facno
      AND facname = 'Basketball arena' 
      AND eventplanline.resno = resourcetbl.resno
      AND eventrequest.eventno = eventplan.eventno;
