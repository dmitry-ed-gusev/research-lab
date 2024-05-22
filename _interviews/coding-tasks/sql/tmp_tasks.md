Выведите все счета, у которых у которых есть более 100 платежных операций

```sql
-- Create the "account" table
CREATE TABLE account (
                         account_id INT PRIMARY KEY,
                         account_name VARCHAR(255),
                         balance DECIMAL(10, 2)
);

-- Create the "payments" table
CREATE TABLE payments (
                          payment_id INT PRIMARY KEY,
                          account_id INT,
                          payment_amount DECIMAL(10, 2),
                          payment_date DATE,
                          FOREIGN KEY (account_id) REFERENCES account(account_id)
);
```

Решение:
select a.account_name, count(*)
from account a, payments p
where p.account_id = a.id
group by a.account_name 
having count(*) > 100;

