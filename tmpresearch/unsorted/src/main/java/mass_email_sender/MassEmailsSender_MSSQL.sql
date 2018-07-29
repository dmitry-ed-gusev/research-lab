-- ������ �������� �� ��� ������� �������������� �������� ��������. ������ ������ ��� ���� MS SQL Server 2005.
-- ������ �������� ������� ���������� ������������, ������ ������� ����� ������� ����������� � ���. �����! :)
--
-- ������ ����������� �������:
-- 1) ��� ��������� ����� ����������� �� ���������� �������� ��� � ���� ������ � ������ ������� ������� - 
--    �������� �������� ���������� @db_name (� ������ �������) � �������� ��� �� � ��������� 
--    use [database name] ���� (������������ ��������� �� ������ ��� ��).
-- 2) � ����� ������� ���������� ����������, ����������� �������� ������ � ��������� �� - ��� ������ ����������
--    ��� ������������ ������� ��������. �� �������� ��� ���������� �� �������! :)
--
-- ������ ������� ��: 04.04.2011

-- ����������� �������� �� �� master
use master
go

-- ������� ������������ ������� � ��������� ��. ������������ ������� ���������� � ������ ������ ��� �������� 
-- ������������� ������� - ��� ��������� ����� �� (��� ����� ��������) ���������� �������� �������� ������ ����� 
-- ���������� � ����� ����� �������, � �� �������� ���������� �������� � ���������� ������, �.�. ����������� 
-- ����������� ������. �� � ������ - ��� ����������... :)
declare @drop_db_sql nvarchar(200), @create_db_sql nvarchar(200), @db_name nvarchar(50)
-- ������������� �������� ���������� "��� ��"
set @db_name       = 'MassEmailsSender'
-- ���� ���������� ���� �� - ������� �� (������������ ������ �� �������� ��, ���� ��� ����������)
set @drop_db_sql   = 'if exists(select name from sys.databases where name = ''' + @db_name + ''') drop database ' + @db_name
-- ������� �� ��� ������ Sender'a (�������� �������� ��������� � ������� ���������� ��������� �� 
-- c����������� � �������� �.�.) - ������������ ������
set @create_db_sql = 'create database ' + @db_name + ' COLLATE Cyrillic_General_CI_AS'
-- ��������� ��������� ������������ �������
exec (@drop_db_sql)
exec (@create_db_sql)
go

-- ����������� �������� �� ���� �� (� ���������, ������������ ������ USE... ������� ������, � ����!)
use MassEmailsSender
go

-- ������� ��������
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

-- ������� ��� ������� ��������, ������� ��������� �������� timestamp ���
-- ���������� ������ � ������� ��������
create trigger dbo.deliveriesUpdateTrigger on dbo.deliveries after update
as
 begin
  update deliveries set timestamp = getdate()
   where id = (select id from inserted);
 end
go

-- �������� ��������� - ��������� ������ � ������� �������� � ���������� �������������
-- ����������� ������
create procedure dbo.addDelivery
 @subject nvarchar(300), @text nvarchar(3000), @type int, @initiator nvarchar(50), @newid int output
  AS
   begin
    begin tran
     -- ��������� ������ � ������� ��������
     insert into dbo.deliveries(subject, text, type, initiator)
      values (@subject, @text, @type, @initiator)
     -- �������� ������������� ����������� ������ (���� ������� ���)
     set @newid = scope_identity()
    commit
   end
go

-- ������� ������, ������������� � ���������. � ����� �������� ������ ���� ����������� ����� ������.
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

-- ������� ����� ����������� ��������. � ������ �������� ����� ���� ��������� ����� �����������.
create table dbo.recipientsTypes
(
 id            int identity(1, 1) primary key not null,
 deliveryId    int not null references deliveries(id),
 recipientType int not null,
 -- ����������� �� �������� ���� ����������� �������� - ��� ������ ������ ���� ���� (�������� = -1 - ��� ��
 -- ��������� - ���������)
 constraint recipient_type_constraint check (recipientType > 0)
)
go
create index recipients_types_index on dbo.recipientsTypes(recipientType)
go
create unique index recipients_types_unique_index on dbo.recipientsTypes(deliveryId, recipientType)
go

-- ������, ���������� ��� ���� �� ��������� � ������������� � ��� ������ (������ ����������).
create view dbo.allDeliveriesView as
 select d.id, d.subject, d.text, d.status, d.type, d.errorText, d.initiator, d.timestamp,
        df.id as fileId, df.fileName, df.deliveryId as fileDeliveryId,
        rt.id as recipientTypeId, rt.deliveryId as recipientTypeDeliveryId, rt.recipientType
 from deliveries d
 left outer join deliveriesFiles df on (df.deliveryId = d.id)
 left outer join recipientsTypes rt on (rt.deliveryId = d.id)
go

-- ������� ����-�������, �� ������� ����������� ��������. ����� ��������
-- �������� (�������) ��������.
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

-- ������� ����-�������, �� ������� ������������ ������ ����� ��������. 
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

-- ���������� � �� �������� ������ (��� �������� �����������)
print '- adding test data to [deliveries] table'
insert into dbo.deliveries(subject, text, initiator) values('�������� ����1', '�������� �����1', '019gus')
insert into dbo.deliveries(subject, text, initiator) values('�������� ����2', '�������� �����2', '019gus')
insert into dbo.deliveries(subject, text, initiator) values('�������� ����3', '�������� �����3', '019gus')

print '- adding test data to [deliveriesFiles] table'
insert into dbo.deliveriesFiles(deliveryId, fileName) values(1, '����1')
insert into dbo.deliveriesFiles(deliveryId, fileName) values(1, '����2')
insert into dbo.deliveriesFiles(deliveryId, fileName) values(2, '����1')
insert into dbo.deliveriesFiles(deliveryId, fileName) values(2, '����3')

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

-- ����������� �� ��������� ����� ��
use master
go