-- Скрипт создания БД для системы автоматических почтовых рассылок. Скрипт создан для СУБД MS SQL Server 2005.
-- Скрипт содержит большое количество комментариев, чтение которых очень поможет разобраться в нем. Удачи! :)
--
-- ВАЖНЫЕ ОСОБЕННОСТИ СКРИПТА:
-- 1) Для изменения имени создаваемой БД необходимо поменять его в двух местах в начале данного скрипта - 
--    изменить значение переменной @db_name (в начале скрипта) и изменить имя БД в операторе 
--    use [database name] ниже (переключение контекста на нужную нам БД).
-- 2) В конце скрипта содержатся инструкции, добавляющие тестовые данные в созданную БД - эти данные необходимы
--    для тестирования системы рассылок. Не удаляйте эти инструкции из скрипта! :)
--
-- Версия скрипта от: 04.04.2011

-- Переключаем контекст на БД master
use master
go

-- Создаем динамические запросы и выполняем их. Динамические запросы необходимы в данном случае для удобства 
-- использования скрипта - при изменении имени БД (для теста например) необходимо поменять значение только ОДНОЙ 
-- переменной в ОДНОМ месте скрипта, а не значения параметров запросов в нескольких местах, т.о. уменьшается 
-- вероятность ошибки. Ну и вообще - так прикольней... :)
declare @drop_db_sql nvarchar(200), @create_db_sql nvarchar(200), @db_name nvarchar(50)
-- Устанавливаем значение переменной "имя БД"
set @db_name       = 'MassEmailsSender'
-- Если существует наша БД - грохаем ее (динамический запрос на удаление БД, если она существует)
set @drop_db_sql   = 'if exists(select name from sys.databases where name = ''' + @db_name + ''') drop database ' + @db_name
-- Создаем БД для нашего Sender'a (указание варианта кодировки и порядка сортировки добавлено по 
-- cогласованию с Ниткиным А.Б.) - динамический запрос
set @create_db_sql = 'create database ' + @db_name + ' COLLATE Cyrillic_General_CI_AS'
-- Выполняем созданные динамические запросы
exec (@drop_db_sql)
exec (@create_db_sql)
go

-- Переключаем контекст на нашу БД (к сожалению, динамический запрос USE... создать нельзя, а жаль!)
use MassEmailsSender
go

-- Таблица рассылок
create table dbo.deliveries
(
 id         int            identity(1, 1) primary key not null,
 subject    nvarchar(300)  not null,
 text       nvarchar(3000) not null,
 status     int            not null default 0,
 type       int            not null default 0,
 errorText  nvarchar(1500) default null,
 initiator  nvarchar(50)   not null,
 timestamp  datetime       default getdate()
)
go
create index deliveries_subject_index on dbo.deliveries(subject)
go
create index deliveries_initiator_index on dbo.deliveries(initiator)
go

-- Триггер для таблицы рассылок, который обновляет значение timestamp при
-- обновлении данных в таблице рассылок
create trigger dbo.deliveriesUpdateTrigger on dbo.deliveries after update
as
 begin
  update deliveries set timestamp = getdate()
   where id = (select id from inserted);
 end
go

-- Хранимая процедура - добавляет запись в таблицу рассылок и возвращает идентификатор
-- добавленной записи
create procedure dbo.addDelivery
 @subject nvarchar(300), @text nvarchar(3000), @type int, @initiator nvarchar(50), @newid int output
  AS
   begin
    begin tran
     -- добавляем запись в таблицу рассылок
     insert into dbo.deliveries(subject, text, type, initiator)
      values (@subject, @text, @type, @initiator)
     -- получаем идентификатор вставленной записи (штоп вернуть его)
     set @newid = scope_identity()
    commit
   end
go

-- Таблица файлов, приаттаченных к рассылкам. К одной рассылке меожет быть приаттачено много файлов.
create table dbo.deliveriesFiles
(
 id         int           identity(1, 1) primary key not null,
 deliveryId int           not null references deliveries(id),
 fileName   nvarchar(200) not null
)
go
create index deliveries_files_filename_index on dbo.deliveriesFiles(fileName)
go
create unique index deliveries_files_unique_index on dbo.deliveriesFiles(deliveryId, fileName)
go

-- Таблица типов получателей рассылок. У каждой рассылки может быть множество типов получателей.
create table dbo.recipientsTypes
(
 id            int identity(1, 1) primary key not null,
 deliveryId    int not null references deliveries(id),
 recipientType int not null,
 -- Ограничения на значение типа получателей рассылки - тип всегда больше либо ноль (значение = -1 - тип не
 -- определен - запрещено)
 constraint recipient_type_constraint check (recipientType > 0)
)
go
create index recipients_types_index on dbo.recipientsTypes(recipientType)
go
create unique index recipients_types_unique_index on dbo.recipientsTypes(deliveryId, recipientType)
go

-- Вьюшка, содержащая всю инфу по рассылкам и прикрепленным к ним файлам (полная информация).
create view dbo.allDeliveriesView as
 select d.id, d.subject, d.text, d.status, d.type, d.errorText, d.initiator, d.timestamp,
        df.id as fileId, df.fileName, df.deliveryId as fileDeliveryId,
        rt.id as recipientTypeId, rt.deliveryId as recipientTypeDeliveryId, rt.recipientType
 from deliveries d
 left outer join deliveriesFiles df on (df.deliveryId = d.id)
 left outer join recipientsTypes rt on (rt.deliveryId = d.id)
go

-- Таблица мейл-адресов, по которым проводилась рассылка. Также является
-- журналом (архивом) рассылки.
create table dbo.emails
(
 id         int            identity(1, 1) primary key not null,
 email      nvarchar(250)  not null,
 companyId  int            not null,
 deliveryId int            not null references deliveries(id),
 status     int            default 0,
 errorText  nvarchar(1000) default null,
 isArchive  int            default 0,
 timestamp  datetime       default getdate()
)
go
create index emails_email_index on dbo.emails(email)
go

-- Таблица майл-адресов, по которым отправляются письма ЛЮБОЙ рассылки. 
create table dbo.mandatoryEmails
(
 id      int            identity(1, 1) primary key not null,
 email   nvarchar(250)  not null,
 comment nvarchar(1000),
 deleted int            default 0
)
go
create index mandatory_emails_email_index on dbo.mandatoryEmails(email)
go
create unique index mandatory_emails_email_unique_index on dbo.mandatoryEmails(email)
go

-- Добавление в БД тестовых данных (для проверки функционала)
print '- adding test data to [deliveries] table'
insert into dbo.deliveries(subject, text, initiator) values('тестовая тема1', 'тестовый текст1', '019gus')
insert into dbo.deliveries(subject, text, initiator) values('тестовая тема2', 'тестовый текст2', '019gus')
insert into dbo.deliveries(subject, text, initiator) values('тестовая тема3', 'тестовый текст3', '019gus')

print '- adding test data to [deliveriesFiles] table'
insert into dbo.deliveriesFiles(deliveryId, fileName) values(1, 'файл1')
insert into dbo.deliveriesFiles(deliveryId, fileName) values(1, 'файл2')
insert into dbo.deliveriesFiles(deliveryId, fileName) values(2, 'файл1')
insert into dbo.deliveriesFiles(deliveryId, fileName) values(2, 'файл3')

print '- adding test data to [recipientsTypes] table'
insert into dbo.recipientsTypes(deliveryId, recipientType) values(1, 1)
insert into dbo.recipientsTypes(deliveryId, recipientType) values(1, 2)
insert into dbo.recipientsTypes(deliveryId, recipientType) values(3, 1)
insert into dbo.recipientsTypes(deliveryId, recipientType) values(3, 2)
insert into dbo.recipientsTypes(deliveryId, recipientType) values(3, 3)
go

print '- adding test data to [emails] table'
insert into dbo.emails(email, companyId, deliveryId, status) values('email1', 1, 1, 0)
insert into dbo.emails(email, companyId, deliveryId, status) values('email2', 1, 1, 0)
insert into dbo.emails(email, companyId, deliveryId, status) values('email3', 1, 1, 0)
insert into dbo.emails(email, companyId, deliveryId, status) values('email4', 1, 1, 0)
insert into dbo.emails(email, companyId, deliveryId, status) values('email5', 1, 1, 0)

-- Отключаемся от контекста нашей БД
use master
go