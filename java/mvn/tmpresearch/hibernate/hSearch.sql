-- DB SQL script for hSearch sample application (script was written for MYSQL 5.x.x).

-- create database if not exists hiberSearch character set cp1251;
create database if not exists hiberSearch character set utf8;

use hiberSearch;

-- products
create table product
 (
  id               int(5)       not null auto_increment,
  title            varchar(100) not null,
  name             varchar(100) default null,
  description      varchar(250) not null,
  manufacture_date datetime,
  primary key(id)
 );
create index product_title_index on product(title);
create index product_name_index on  product(name);
create index product_desc_index  on product(description);
-- data for product table
insert into product(title, name, description) values('phone', 'mobile phone', 'efjgw efhgwe fh');
insert into product(title, name, description) values('iphone', 'apple phone 4s', 'qeh ehweh wethweth');
insert into product(title, name, description) values('mouse', 'pc mouse device (hid)','wethw werhth wth');
insert into product(title, name, description) values('key board', 'keys for programmers :)', 'werh wrheth werhweth wth');

-- departments
create table departments
(
 id         int          not null auto_increment,
 name       varchar(250) not null,
 code       varchar(50)  default null,
 comment    varchar(200) default null,
 timestamp  timestamp    default now() not null,
 deleted    int          default 0,
 primary key(id)
);
create        index dept_name_index        on departments(name);
create        index dept_code_index        on departments(code);
-- data for departments table
insert into departments(name, code, comment) values('HR dept', '001', 'sdfg sdfgdfsh sdhfdhsdh');
insert into departments(name, code, comment) values('Юр отдел', '023', 'egdfhg dsfgsfdrg sehf');
insert into departments(name, code, comment) values('Fin dept il', '021', 'peryioe we;l;ma phsdh');
insert into departments(name, code, comment) values('Dev dept', '777', 'zxxcs wqrtmjcoefn gfioqweng wegowerhg gg');
insert into departments(name, code, comment) values('HR dept 4s', '001', 'sdfg sdfgdfsh sdhfdhsdh');
insert into departments(name, code, comment) values('direction', '000', ' twrweref opg8785hj 734h hfdhsdh');

-- employees
create table employees
(
 id         int auto_increment,
 name       varchar(250) not null,
 family     varchar(250),
 patronymic varchar(250),
 birthDate  datetime,
 sex        int not null default 0,
 position   varchar(250),
 comment    varchar(200) default null,
 timestamp  timestamp    default now() not null,
 deleted    int          default 0,
 primary key(id)
);
create index employee_name_index       on employees(name);
create index employee_family_index     on employees(family);
create index employee_position_index   on employees(position);
create index employee_patronymic_index on employees(patronymic);
-- data for table
insert into employees(name, family, position, comment) values('Dmitriy', 'Gusev', 'developer', 'Nice guy! :)');
insert into employees(name, family, position, comment) values('Сергей', 'Иванов', 'разработчик', 'Nice guy! :)');
insert into employees(name, family, position, comment) values('Иван', 'Петров', 'тестировщик', 'Nice guy! :)');