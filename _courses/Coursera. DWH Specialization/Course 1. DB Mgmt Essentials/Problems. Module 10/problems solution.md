# Module 10 - Problems Solution

## Problem #1

-------------------------------------------------------------------------------

Customer(**CustNo**, CustFirstName, CustLastName, CustCity, CustState, CustZip, CustBal)

Employee(**EmpNo**, supempno, EmpFirstName, EmpLastName, EmpPhone, EmpEmail, EmpDeptName, EmpCommRate)
        FOREIGN KEY(supempno) REFERENCES Employee

Product(**ProdNo**, ProdName, ProdQOH, ProdPrice, ProdNextShipDate)

Order(**OrdNo**, CustNo, EmpNo, OrdDate, OrdName, OrdCity, OrdZip)
        FOREIGN KEY(custno) REFERENCES customer
        FOREIGN KEY(empno) REFERENCES employee
        CustNo NOT NULL

Contains(**OrdNo**, **ProdNo**, Qty)
        FOREIGN KEY(ordno) REFERENCES order
        FOREIGN KEY(prodno) REFERENCES product

## Problem #2

-------------------------------------------------------------------------------

OrderLine(**OrdNo**, **ProdNo**, Qty)
                FOREIGN KEY(ordno) REFERENCES order
                FOREIGN KEY(prodno) REFERENCES product
