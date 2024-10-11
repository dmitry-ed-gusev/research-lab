create database if not exists hiberExample character set utf8;

use hiberExample;

-- products
create table products
(
 id               int(5)       not null auto_increment,
 title            varchar(100) not null,
 name             varchar(100) not null,
 description      varchar(250) not null,
 primary key(id)
);
-- data for product table
insert into products(id, title, name, description) values(1, 'phone',     'mobile phone', 'efjgw efhgwe fh');
insert into products(id, title, name, description) values(2, 'iphone',    'apple phone 4s', 'qeh ehweh wethweth');
insert into products(id, title, name, description) values(3, 'mouse',     'pc mouse device (hid)','wethw werhth wth');
insert into products(id, title, name, description) values(4, 'key board', 'keys for programmers :)', 'werh wrheth werhweth wth');

-- product parts
create table productsParts
(
 id        int          not null auto_increment,
 productId int          not null,
 name      varchar(100) not null,
 primary key(id)
);
-- parts data
insert into productsParts(id, productId, name) values(1, 1, 'screen');
insert into productsParts(id, productId, name) values(2, 1, 'joystick');
insert into productsParts(id, productId, name) values(3, 1, 'keys');
insert into productsParts(id, productId, name) values(4, 2, 'glass');
insert into productsParts(id, productId, name) values(5, 2, 'dynamic');
insert into productsParts(id, productId, name) values(6, 2, 'button');
insert into productsParts(id, productId, name) values(7, 2, 'case');
insert into productsParts(id, productId, name) values(8, 3, 'receiver');
