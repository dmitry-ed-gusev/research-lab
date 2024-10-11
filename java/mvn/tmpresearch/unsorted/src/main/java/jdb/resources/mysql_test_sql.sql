-- MYSQL скрипт создания тестовой БД (для отладки различных модулей библиотеки) 
create database if not exists test;

use test;

create table test
(
 id      int auto_increment primary key,
 string1 varchar(255) not null,
 number1 int          not null,
 string2 varchar(255) not null,
 number2 int          not null
);

create        index string1_index        on test(string1);
create        index string2_index        on test(string2);