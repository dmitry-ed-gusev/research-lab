-- Скрипт создания БД системы Шторм для СУБД MSSQL 2005.
-- 
-- Версия скрипта от: 08.04.2011

use master
go

-- Дропаем БД storm_test если она существует 
if exists (select name from sys.databases where name = 'storm_test')
drop database storm_test
go

-- Создаем БД storm_test заново
create database storm_test
go

-- Переключаем контекст на созданную БД
use storm_test
go

create table ship_size
(
 id             int identity(1,1) primary key not null,
 lower_value    int                           not null,
 lower_relation int                           not null,
 upper_value    int                           not null,
 upper_relation int                           not null,
 status         int           default 1       not null,
 update_user    int           default 0       not null,
 timestamp      datetime default getdate()    not null,
 text           nvarchar(255)                 not null,
 unique(lower_value, lower_relation, upper_value, upper_relation),
 check(0 <= lower_value AND 0 <= upper_value),
 check(lower_value <= upper_value),
 check(0 <= lower_relation AND 0 <= upper_relation),
 check(0 <= update_user)
)
go

create table keel_laying
(
 id             int identity(1,1) primary key not null,
 lower_date     smalldatetime not null,
 lower_relation int           not null,
 upper_date     smalldatetime not null,
 upper_relation int           not null,
 status         int default 1 not null,
 update_user    int default 0 not null,
 timestamp      datetime default getdate() not null,
 text           nvarchar(255) not null,
 unique(lower_date, lower_relation, upper_date, upper_relation),
 check(0 <= lower_relation AND 0 <= upper_relation),
 check(0 <= update_user)
)
go

create table ship_delivery
(
 id             int identity(1,1) primary key not null,
 lower_date     smalldatetime          not null,
 lower_relation int           not null,
 upper_date     smalldatetime          not null,
 upper_relation int           not null,
 status         int default 1 not null,
 update_user    int default 0 not null,
 timestamp      datetime default getdate() not null,
 text           nvarchar(255) not null,
 unique(lower_date, lower_relation, upper_date, upper_relation),
 check(0 <= lower_relation AND 0 <= upper_relation),
 check(0 <= update_user)
)
go


create table ship_build
(
 id             int identity(1,1) primary key not null,
 lower_date     smalldatetime          not null,
 lower_relation int           not null,
 upper_date     smalldatetime          not null,
 upper_relation int           not null,
 status         int default 1 not null,
 update_user    int default 0 not null,
 timestamp      datetime default getdate() not null,
 text           nvarchar(255) not null,
 unique(lower_date, lower_relation, upper_date, upper_relation),
 check(0 <= lower_relation AND 0 <= upper_relation),
 check(0 <= update_user)
)
go


create table ship_length
(
 id             int identity(1,1) primary key not null,
 lower_value    int           not null,
 lower_relation int           not null,
 upper_value    int           not null,
 upper_relation int           not null,
 status         int default 1 not null,
 update_user    int default 0 not null,
 timestamp      datetime default getdate() not null,
 text           nvarchar(255) not null,
 unique(lower_value, lower_relation, upper_value, upper_relation),
 check(0 <= lower_value AND 0 <= upper_value),
 check(lower_value <= upper_value),
 check(0 <= lower_relation AND 0 <= upper_relation),
 check(0 <= update_user)
)
go

create table survey_action
(
 id			         int identity(1,1) primary key not null,
 survey_action       nchar(2000)   not null,
 survey_action_short nvarchar(255) not null,
 status              int default 1 not null,
 update_user         int default 0 not null,
 timestamp           datetime default getdate() not null,
 check(0 <= update_user)
)
go


create table ship_age
(
 id             int identity(1,1) primary key not null,
 lower_value    int           not null,
 lower_relation int           not null,
 upper_value    int           not null,
 upper_relation int           not null,
 status         int default 1 not null,
 update_user    int default 0 not null,
 timestamp      datetime default getdate() not null,
 text           nvarchar(255) not null,
 unique(lower_value, lower_relation, upper_value, upper_relation),
 check(0 <= lower_value AND 0 <= upper_value),
 check(lower_value <= upper_value),
 check(0 <= lower_relation AND 0 <= upper_relation),
 check(0 <= update_user)
)
go

create table survey_aspect
(
 id			         int identity(1,1) primary key not null,
 survey_aspect       nvarchar(255) not null,
 survey_aspect_short nvarchar(255) not null,
 status              int default 1 not null,
 update_user         int default 0 not null,
 timestamp          datetime default getdate() not null,
 unique(survey_aspect),
 check(0 <= update_user)
)
go

create table survey_occasion
(
 id			           int identity(1,1) primary key not null,
 survey_occasion       nvarchar(255) not null,
 survey_occasion_short nvarchar(255) not null,
 status                int default 1 not null,
 update_user           int default 0 not null,
 timestamp             datetime default getdate() not null,
 unique(survey_occasion),
 check(0 <= update_user)
)
go

create table ship_type
(
 id          int identity(1,1) primary key not null,
 ship_type   nvarchar(255) not null,
 status      int default 1 not null,
 update_user int default 0 not null,
 timestamp   datetime default getdate() not null,
 unique(ship_type),
 check(0 <= update_user)
)
go

create table survey_type
(
 id			        int identity(1,1) primary key not null,
 fk_survey_aspect_id     int  not null references survey_aspect(id),
 fk_survey_occasion_id     int  not null references survey_occasion(id),
 status            int default 1 not null,
 update_user       int default 0 not null,
 timestamp         datetime default getdate() not null,
 check(0 <= update_user)
)
go

create table ship_misc
(
 id             int identity(1,1) primary key not null,
 ship_misc         nchar(1000) not null,
 status            int default 1 not null,
 update_user       int default 0 not null,
 timestamp         datetime default getdate() not null,
 check(0 <= update_user)
)
go

create table topic
(
 id          int identity(1,1) primary key not null,
 l1          int           not null,
 l2          int default 0 not null,
 l3          int default 0 not null,
 l4          int default 0 not null,
 l5          int default 0 not null,
 l6          int default 0 not null,
 l7          int default 0 not null,
 l8          int default 0 not null,
 l9          int default 0 not null,
 item_id     int default 0 not null,
 topic       nchar(1000)   not null,
 nd          nchar(500),
 status      int default 1 not null,
 update_user int default 0 not null,
 timestamp   datetime default getdate() not null,
 check(0 < l1 AND 0 <= l2 AND 0 <= l3 AND 0 <= l4 AND 0 <= l5 AND 0 <= l6 AND 0 <= l7 AND 0 <= l8 AND 0 <= l9),
 check(0 <= update_user)
)
go


create table topic_parents
(
 id             int identity(1,1) primary key not null,
 fk_topic_id int           not null references topic(id),
 fk_topic_parent_id int   default 0 not null,
 status      int default 1 not null,
 update_user int default 0 not null,
 timestamp   datetime default getdate() not null,
 unique (fk_topic_id, fk_topic_parent_id, status),
 check(0 <= update_user)
)
go


create table fleet_2_misc
(
 id             int identity(1,1) primary key not null,
 fk_ship_misc_id   int           not null references ship_misc(id),
 ncmp              nvarchar(6)   not null,
 status            int default 1 not null,
 update_user       int default 0 not null,
 timestamp         datetime default getdate() not null,
 unique(fk_ship_misc_id, ncmp, status),
 check(0 <= update_user)
)
go

create table survey
(
 id             int identity(1,1) primary key not null,
 fk_insp_id          int           not null,
 fk_place_city_id    int           not null,
 fk_place_country_id int           not null,
 fk_fleet_id         int           not null,
 ncmp                nchar(6),
 fleet_name          nvarchar(255) not null,
 start_date          smalldatetime,
 end_date            smalldatetime,
 isFinished          int           not null,
 registration_date   smalldatetime,
 registration_number nchar(20),
 comment             nchar(2000)   not null,
 generate_checklist_date   datetime  default null,
 registration_survey_date   smalldatetime,
 finish_survey_date  smalldatetime,
 sent_datetime       datetime default null,
 act_number          varchar(50),
 status              int default 1 not null,
 update_user         int default 0 not null,
 timestamp         datetime default getdate() not null,
 check(0 <= update_user)
)
go


create table survey_2_type
(
 id             int identity(1,1) primary key not null,
 fk_survey_id          int           not null references survey(id),
 fk_survey_type_id     int           not null references survey_type(id),
 status                int default 1 not null,
 update_user           int default 0 not null,
 timestamp             datetime default getdate() not null,
 check(0 <= update_user)
) 
go


create table ship_type_2_fleet
(
 id             int identity(1,1) primary key not null,
 not_equal           int default 0 not null,
 fk_ship_type_id     int           not null references ship_type(id),
 fk_tsv_id           int,
 fk_stip_id          int,
 fk_statgr_id        int,
 fk_dop_id           int,
 fk_slovo_id         int,
 fk_itip_id          int,
 fk_dvizh_id         int,
 priz                int,
 rad                 nvarchar(2),
 status              int default 1 not null,
 update_user         int default 0 not null,
 timestamp           datetime default getdate() not null,
 check(0 <= update_user)
) 
go



create table ship_dwt
(
 id             int identity(1,1) primary key not null,
 lower_value    int                           not null,
 lower_relation int                           not null,
 upper_value    int                           not null,
 upper_relation int                           not null,
 status         int           default 1       not null,
 update_user    int           default 0       not null,
 timestamp      datetime default getdate() not null,
 text           nvarchar(255)                 not null,
 unique(lower_value, lower_relation, upper_value, upper_relation),
 check(0 <= lower_value AND 0 <= upper_value),
 check(lower_value <= upper_value),
 check(0 <= lower_relation AND 0 <= upper_relation),
 check(0 <= update_user)
)
go



create table ship_length_mk
(
 id             int identity(1,1) primary key not null,
 lower_value    int           not null,
 lower_relation int           not null,
 upper_value    int           not null,
 upper_relation int           not null,
 status         int default 1 not null,
 update_user    int default 0 not null,
 timestamp      datetime default getdate() not null,
 text           nvarchar(255) not null,
 unique(lower_value, lower_relation, upper_value, upper_relation),
 check(0 <= lower_value AND 0 <= upper_value),
 check(lower_value <= upper_value),
 check(0 <= lower_relation AND 0 <= upper_relation),
 check(0 <= update_user)
)
go



create table specialization
(
 id             int identity(1,1) primary key not null,
 text       nvarchar(255) not null,
 text_short nvarchar(255) not null,
 status                int default 1 not null,
 update_user           int default 0 not null,
 timestamp             datetime default getdate() not null,
 unique(text),
 check(0 <= update_user)
)
go

create table stran
(
 id			    	   int  identity(1,1) not null,
 stran_id              int default 0 not null,
 kod                   nvarchar(2)   not null,
 name                  nvarchar(255) not null,
 a_name                nvarchar(255) not null,
 f_name                nvarchar(255) not null,
 status                int default 1 not null,
 update_user           int default 0 not null,
 timestamp             datetime default getdate() not null
)
go



create table ruleset
(
 id             int identity(1,1) primary key not null,
 fk_survey_type_id    int           not null references survey_type(id),
 fk_survey_action_id  int           not null references survey_action(id),
 fk_ship_size_id      int           not null references ship_size(id),
 fk_ship_length_id    int           not null references ship_length(id),
 fk_ship_age_id       int           not null references ship_age(id),
 fk_ship_type_id      int           not null references ship_type(id),
 fk_keel_laying_id    int           not null references keel_laying(id),
 fk_ship_build_id     int           not null references ship_build(id),
 fk_ship_delivery_id  int           not null references ship_delivery(id),
 fk_stran_id          int           not null default 0,
 fk_ship_dwt_id       int           not null references ship_dwt(id),
 fk_ship_length_mk_id int           not null references ship_length_mk(id),
 fk_gmdss             int           not null default 0,
 status              int default 1 not null,
 update_user         int default 0 not null,
 timestamp           datetime default getdate() not null,
 check(0 <= update_user)
)
go



create table ship_type_2_ruleset
(
 id             int identity(1,1) primary key not null,
 fk_ruleset_id   int              not null references ruleset(id),
 fk_ship_type_id int              not null references ship_type(id),
 status          int    default 1 not null,
 update_user     int    default 0 not null,
 timestamp       datetime default getdate() not null,
 check(0 <= update_user)
)
go



-- таблица аутентификации пользователей
create table authTable 
(
 login varchar(50) not null,
 password varchar(50) not null,
 fullName varchar(255) not null,
 personnelID int 
)
go


-- таблицы снимка системы на момент формирования чек-листа
create table survey_2_ship_misc
(
 id             int identity(1,1) primary key not null,
 fk_survey_id    int              not null references survey(id),
 fk_ship_misc_id int              not null references ship_misc(id),
 status          int    default 1 not null,
 update_user     int    default 0 not null,
 timestamp       datetime default getdate() not null,
 check(0 <= update_user)
)
go


create table survey_2_ship_type
(
 id             int identity(1,1) primary key not null,
 fk_survey_id    int              not null references survey(id),
 fk_ship_type_id int              not null references ship_type(id),
 status          int    default 1 not null,
 update_user     int    default 0 not null,
 timestamp       datetime default getdate() not null,
 check(0 <= update_user)
)
go


create table survey_2_ship_data
(
 id             int identity(1,1) primary key not null,
 fk_survey_id    int              not null references survey(id),
 dkil            smalldatetime, 
 datbld          smalldatetime, 
 dbld            smalldatetime,
 valcnt          int,
 dedv            int, 
 stran_id1       int, 
 lngthc          decimal(13,2), 
 lngmk           decimal(13,2), 
 status          int    default 1 not null,
 update_user     int    default 0 not null,
 timestamp       datetime default getdate() not null,
 check(0 <= update_user)
)
go



-- версионность системы
create table version_modules
(
 id             int identity(1,1) primary key not null,
 module          nvarchar(255) not null,
 filename        nvarchar(255) not null, 
 timestamp       datetime default getdate() not null
)
go



create table version_client
(
 id             int identity(1,1) primary key not null,
 version         nvarchar(30)  not null,
 builddate       smalldatetime, 
 status          int    default 1 not null,
 timestamp       datetime default getdate() not null
)
go


create table version_client_content
(
 id             int identity(1,1) primary key not null,
 fk_client_id    int           not null references version_client(id),
 fk_module_id    int           not null references version_modules(id),
 crc             varchar(50)   not null,
 version         nvarchar(50),
 filedate        smalldatetime, 
 timestamp       datetime default getdate() not null
)
go


-- загрузки освидетельствований в ГУР
create table survey_upload
(
 id             int identity(1,1) primary key not null,
 fk_survey_id    int,
 filename        varchar(100),
 result          int,
 status          int    default 1 not null,
 update_user     int    default 0 not null,
 timestamp       datetime default getdate() not null,
 check(0 <= update_user)
)
go



-- список пользователей - соавторов освидетельствования
create table survey_users
(
 id             int identity(1,1) primary key not null,
 personnelid           int           not null,
 fullname              varchar(255), 
 login                 varchar(50), 
 status                int default 1 not null,
 update_user           int default 0 not null,
 timestamp             datetime default getdate() not null
)
go
 


-- таблица sql-скриптов для модификации структуры БД
create table sys_sql_table
(
 id             int identity(1,1) primary key not null,
query		      nchar(1500)   not null,
update_user           int default 0 not null,
timestamp             datetime default getdate() not null
)
go


create table survey_2_spec
(
 id             int identity(1,1) primary key not null,
 fk_survey_id          int           not null references survey(id),
 fk_specialization_id  int           not null references specialization(id),
 status                int default 1 not null,
 update_user           int default 0 not null,
 timestamp             datetime default getdate() not null,
 unique(fk_survey_id, fk_specialization_id),
 check(0 <= update_user)
)
go
 

create table misc_2_ruleset
(
 id             int identity(1,1) primary key not null,
 fk_ruleset_id   int              not null references ruleset(id),
 fk_ship_misc_id int              not null references ship_misc(id),
 status          int    default 1 not null,
 update_user     int    default 0 not null,
 timestamp       datetime default getdate() not null,
 check(0 <= update_user)
)
go



create table items
(
 id             int identity(1,1) primary key not null,
 text        nvarchar(MAX) not null,
 nd          nchar(500),
 answer_req  int default 0 not null,
 chapter     nchar(500),
 comment     nvarchar(MAX),
 after_id    int default 0,
 instead_id  int default 0,
 fk_specialization_id int default 2 not null references specialization(id),
 answer_default       int default 1 not null,
 update_user_activate int default 0,
 timestamp_activate   datetime default getdate(),
 validity_start_date smalldatetime default null,
 validity_finish_date smalldatetime default null,
 status      int default 4 not null,
 update_user int default 0 not null,
 timestamp   datetime default getdate() not null,
 check(0 <= update_user)
)
go


create table item_2_ruleset
(
 id             int identity(1,1) primary key not null,
 fk_ruleset_id  int              not null references ruleset(id),
 fk_item_id     int              not null references items(id),
 status         int    default 1 not null,
 update_user    int    default 0 not null,
 timestamp      datetime default getdate() not null,
 unique(fk_ruleset_id),
 check(0 <= update_user)
)
go



create table check_list
(
 id             int identity(1,1) primary key not null,
 fk_items_id       int           not null references items(id),
 fk_survey_id      int           not null references survey(id),
 fk_survey_action_id  int        not null references survey_action(id),
 fk_survey_type_id int           not null references survey_type(id),
 answer            int default 1 not null,
 ready_sign   	   int default 0 not null,
 data              nvarchar(MAX),
 comment           nvarchar(MAX),
 comment_rs        int default 0 not null,
 comment_type      int default 0 not null,
 status            int default 1 not null,
 update_user       int default 0 not null,
 timestamp         datetime default getdate() not null,
 unique(fk_items_id, fk_survey_id, fk_survey_action_id, fk_survey_type_id),
 check(0 <= update_user)
)
go


-- индексы
create index idx_keel_laying_values   on keel_laying(lower_date, lower_relation, upper_date, upper_relation);
create index idx_ship_delivery_values  on ship_delivery (lower_date, lower_relation, upper_date, upper_relation);
create index idx_ship_build_values  on ship_build (lower_date, lower_relation, upper_date, upper_relation);
create index idx_ship_length_values   on ship_length(lower_value, lower_relation, upper_value, upper_relation);
create index idx_ship_dwt_values   on ship_dwt(lower_value, lower_relation, upper_value, upper_relation);
create index idx_ship_size_values   on ship_size(lower_value, lower_relation, upper_value, upper_relation);
create index idx_ship_age_values   on ship_age(lower_value, lower_relation, upper_value, upper_relation);
create index idx_ship_length_mk_values   on ship_length_mk(lower_value, lower_relation, upper_value, upper_relation);
create index idx_stran_id   on stran(stran_id);
create index idx_topic_lx   on topic(l1, l2, l3, l4, l5, l6, l7, l8, l9);
create index idx_topic_item_id on topic(item_id);
create index idx_topic_parent_id  on topic_parents(fk_topic_parent_id);

-- индексы на поля статус
create index idx_items_status           on items(status);
create index idx_ship_length_status     on ship_length(status);
create index idx_ship_age_status        on ship_age(status);
create index idx_ship_size_status       on ship_size(status);
create index idx_ship_type_status       on ship_type(status);
create index idx_ship_build_status      on ship_build(status);
create index idx_ship_delivery_status   on ship_delivery(status);
create index idx_ship_misc_status       on ship_misc(status);
create index idx_keel_laying_status     on keel_laying(status);
create index idx_survey_type_status     on survey_type(status);
create index idx_survey_aspect_status   on survey_aspect(status);
create index idx_survey_occasion_status on survey_occasion(status);
create index idx_survey_action_status   on survey_action(status);
create index idx_item_2_ruleset_status  on item_2_ruleset(status);
create index idx_misc_2_ruleset_status  on misc_2_ruleset(status);
create index idx_ruleset_status         on ruleset(status);
create index idx_topic_status           on topic(status);
create index idx_ship_type_2_fleet_status on ship_type_2_fleet(status);
create index idx_topic_parents_status   on topic_parents(status);
create index idx_check_list_status      on check_list(status);


use master
go
