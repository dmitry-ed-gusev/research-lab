-- Скрипт создания БД Fleet для работы системы Шторм для СУБД MSSQL 2005.
-- 
-- Версия скрипта от: 08.04.2011

use master
go

-- Дропаем БД storm_test если она существует 
if exists (select name from sys.databases where name = 'fleet_test')
drop database fleet_test
go

-- Создаем БД fleet_test заново
create database fleet_test
go

-- Переключаем контекст на созданную БД
use fleet_test
go

CREATE
    TABLE dvig
    (
		id INTEGER DEFAULT 0 NOT NULL,
        dvig_id INTEGER,
        kod NCHAR(4),
        name NVARCHAR(50),
        s_name NVARCHAR(50),
        s_kod NVARCHAR(10),
        a_name NVARCHAR(50),
        as_name NVARCHAR(50),
        as_kod NVARCHAR(10),
        timestamp DATETIME DEFAULT GETDATE() NOT NULL 
	)
go
create index dvig_dvigid_index on dbo.dvig(dvig_id)
go
create index dvig_name_index on dbo.dvig(name)
go


CREATE
    TABLE dvizh
    (
		id INTEGER DEFAULT 0 NOT NULL,
        dvizh_id INTEGER,
        kod NCHAR(2),
        name NVARCHAR(10),
        s_name NVARCHAR(50),
        a_name NVARCHAR(50),
        as_name NVARCHAR(10),
        timestamp DATETIME DEFAULT GETDATE() NOT NULL 
	)
go
create index dvizh_dvizhid_index on dbo.dvizh(dvizh_id)
go
create index dvizh_name_index on dbo.dvizh(name)
go

CREATE
    TABLE fleet
    (
		id INTEGER DEFAULT 0 NOT NULL,
        fleet_id INTEGER,
        ncmp NCHAR(6),
        rnom NCHAR(8),
        pwrst1 NCHAR(1),
        nimo NCHAR(7),
        noff NCHAR(10),
        klsves_id INTEGER,
        insp_id1 INTEGER,
        insp_id2 INTEGER,
        firm_id3 INTEGER,
        ndsgn NVARCHAR(10),
        insp_id3 INTEGER,
        nappr NVARCHAR(19),
        dappr SMALLDATETIME,
        radcmp NVARCHAR(10),
        namer NVARCHAR(45),
        namel NVARCHAR(45),
        namh NVARCHAR(45),
        namex NVARCHAR(45),
        yex NCHAR(4),
        namrb NVARCHAR(45),
        firm_id1 INTEGER,
        firm_id2 INTEGER,
        firm_id7 INTEGER,
        stran_id1 INTEGER,
        gorod_id1 INTEGER,
        stran_id2 INTEGER,
        gorod_id2 INTEGER,
        firm_id4 INTEGER,
        nbld NVARCHAR(10),
        dbld SMALLDATETIME,
        drgspr SMALLDATETIME,
        dkil SMALLDATETIME,
        datbld SMALLDATETIME,
        datoss SMALLDATETIME,
        tip_id INTEGER,
        stip_id1 INTEGER,
        stip_id2 INTEGER,
        stip_id3 INTEGER,
        stip_id4 INTEGER,
        statgr_id INTEGER,
        stip_id5 INTEGER,
        tip_id1 INTEGER,
        stip_id11 INTEGER,
        stip_id21 INTEGER,
        stip_id31 INTEGER,
        stip_id41 INTEGER,
        stip_id51 INTEGER,
        statgr_id1 INTEGER,
        osnkls_id INTEGER,
        icecat NVARCHAR(11),
        unsink NCHAR(1),
        rajon_id INTEGER,
        zauto NVARCHAR(8),
        dop_id1 INTEGER,
        dop_id2 INTEGER,
        dop_id3 INTEGER,
        dop_id4 INTEGER,
        atom NCHAR(1),
        slovo_id INTEGER,
        osnote NVARCHAR(100),
        osnkls_id1 INTEGER,
        osnkls_id2 INTEGER,
        icecat1 NVARCHAR(11),
        unsink1 NCHAR(1),
        rajon_id1 INTEGER,
        zauto1 NVARCHAR(8),
        dop_id11 INTEGER,
        dop_id21 INTEGER,
        dop_id31 INTEGER,
        dop_id41 INTEGER,
        atom1 NCHAR(1),
        slovo_id1 INTEGER,
        osnote1 NVARCHAR(100),
        dosnkl2 SMALLDATETIME,
        valcnt INTEGER,
        valcns NCHAR(1),
        clrcnt INTEGER,
        clrcns NCHAR(1),
        clrtm INTEGER,
        valtm INTEGER,
        dedv INTEGER,
        ndispl INTEGER,
        lngthd DECIMAL(13,2),
        lngthc DECIMAL(13,2),
        lngmk DECIMAL(13,2),
        wdthd DECIMAL(13,2),
        wdthd2 DECIMAL(13,2),
        dno NCHAR(2),
        bort NCHAR(2),
        sootv NCHAR(2),
        hgthb DECIMAL(13,2),
        sbsid DECIMAL(13,2),
        speed DECIMAL(13,2),
        deck INTEGER,
        "bulk" INTEGER,
        valct INTEGER,
        valcs NCHAR(1),
        clrct INTEGER,
        clrcs NCHAR(1),
        dev INTEGER,
        ndisp INTEGER,
        lngth DECIMAL(13,2),
        lnmk DECIMAL(13,2),
        hgth DECIMAL(13,2),
        sbsd DECIMAL(13,2),
        tsv_id INTEGER,
        ybmg1 NVARCHAR(4),
        ybmg2 NVARCHAR(4),
        ybmg3 NVARCHAR(4),
        ybmg4 NVARCHAR(4),
        stran_id3 INTEGER,
        stran_id4 INTEGER,
        stran_id5 INTEGER,
        stran_id6 INTEGER,
        gorod_id3 INTEGER,
        gorod_id4 INTEGER,
        gorod_id5 INTEGER,
        gorod_id6 INTEGER,
        firm_id5 INTEGER,
        firm_id6 INTEGER,
        firm_id8 INTEGER,
        firm_id9 INTEGER,
        dvig_id1 INTEGER,
        dvig_id2 INTEGER,
        dvig_id3 INTEGER,
        dvig_id4 INTEGER,
        qmg1 INTEGER,
        qmg2 INTEGER,
        qmg3 INTEGER,
        qmg4 INTEGER,
        pmg1 INTEGER,
        pmg2 INTEGER,
        pmg3 INTEGER,
        pmg4 INTEGER,
        dvizh_id INTEGER,
        ql INTEGER,
        qv INTEGER,
        qmk INTEGER,
        tmk_id INTEGER,
        pmk DECIMAL(13,2),
        stmk INTEGER,
        qpem1 INTEGER,
        pem1 INTEGER,
        qpem2 INTEGER,
        pem2 INTEGER,
        qpst1 INTEGER,
        pst1 INTEGER,
        qpst2 INTEGER,
        pst2 INTEGER,
        qpst3 INTEGER,
        pst3 INTEGER,
        qpst4 INTEGER,
        pst4 INTEGER,
        rad NVARCHAR(200),
        gmdss NCHAR(1),
        klcold_id INTEGER,
        klcold NVARCHAR(15),
        tmp NVARCHAR(3),
        tcold_id1 INTEGER,
        tcold_id2 INTEGER,
        qtr1 INTEGER,
        ctr1 INTEGER,
        qtr2 INTEGER,
        ctr2 INTEGER,
        qtr3 INTEGER,
        ctr3 INTEGER,
        qtr4 INTEGER,
        ctr4 INTEGER,
        qtr5 INTEGER,
        ctr5 INTEGER,
        qtr6 INTEGER,
        ctr6 INTEGER,
        qtr7 INTEGER,
        ctr7 INTEGER,
        qtr8 INTEGER,
        ctr8 INTEGER,
        qntr INTEGER,
        cntr INTEGER,
        qotr INTEGER,
        cotr INTEGER,
        tgrl1 NCHAR(1),
        qgrl1 INTEGER,
        lgrl1 DECIMAL(13,2),
        wgrl1 DECIMAL(13,2),
        tgrl2 NCHAR(1),
        qgrl2 INTEGER,
        lgrl2 DECIMAL(13,2),
        wgrl2 DECIMAL(13,2),
        tgrl3 NCHAR(1),
        qgrl3 INTEGER,
        lgrl3 DECIMAL(13,2),
        wgrl3 DECIMAL(13,2),
        tgrl4 NVARCHAR(1),
        qgrl4 INTEGER,
        lgrl4 DECIMAL(13,2),
        wgrl4 DECIMAL(13,2),
        tgrl5 NCHAR(1),
        qgrl5 INTEGER,
        lgrl5 DECIMAL(13,2),
        wgrl5 DECIMAL(13,2),
        sk1 NCHAR(1),
        qsk1 INTEGER,
        csk1 DECIMAL(13,2),
        sk2 NCHAR(1),
        qsk2 INTEGER,
        csk2 DECIMAL(13,2),
        sk3 NCHAR(1),
        qsk3 INTEGER,
        csk3 DECIMAL(13,2),
        sk4 NCHAR(1),
        qsk4 INTEGER,
        csk4 DECIMAL(13,2),
        sk5 NCHAR(1),
        qsk5 INTEGER,
        csk5 DECIMAL(13,2),
        psk1 NCHAR(1),
        qpsk1 INTEGER,
        cpsk1 INTEGER,
        psk2 NCHAR(1),
        qpsk2 INTEGER,
        cpsk2 INTEGER,
        cub1 INTEGER,
        cub2 INTEGER,
        ckat_id INTEGER,
        dscrps INTEGER,
        qchn DECIMAL(13,2),
        topl_id1 INTEGER,
        topl_id2 INTEGER,
        topl_id3 INTEGER,
        qfuel INTEGER,
        mfuel NCHAR(2),
        qwbal INTEGER,
        mwbal NCHAR(2),
        warm NVARCHAR(6),
        qspec INTEGER,
        qpassk INTEGER,
        qpassp INTEGER,
        pbk INTEGER,
        sost_id INTEGER,
        dsost SMALLDATETIME,
        insp_id4 INTEGER,
        pass2 INTEGER,
        dsostrb SMALLDATETIME,
        renov NCHAR(1),
        drenov SMALLDATETIME,
        ready INTEGER,
        preg NCHAR(1),
        sreg NCHAR(1),
        dinput SMALLDATETIME,
        note NVARCHAR(MAX),
        d_kont_b SMALLDATETIME,
        class_with NCHAR(1),
        timestamp DATETIME DEFAULT GETDATE() NOT NULL 
	)
go
create index fleet_fleetid_index on dbo.fleet(fleet_id)
go
create index fleet_ncmp_index on dbo.fleet(ncmp)
go
create index fleet_namer_index on dbo.fleet(namer)
go


CREATE
    TABLE gorod
    (
		id INTEGER DEFAULT 0 NOT NULL,
        gorod_id INTEGER,
        kod NCHAR(4),
        name NVARCHAR(50),
        a_name NVARCHAR(50),
        timestamp DATETIME DEFAULT GETDATE() NOT NULL 
	)
go
create index gorod_gorodid_index on dbo.gorod(gorod_id)
go
create index gorod_name_index on dbo.gorod(name)
go

CREATE
    TABLE insp
    (
		id INTEGER DEFAULT 0 NOT NULL,
        insp_id INTEGER,
        kod NCHAR(3),
        name NVARCHAR(100),
        s_name NVARCHAR(10),
        tel NVARCHAR(50),
        fax NVARCHAR(50),
        l_name NVARCHAR(150),
        fio NVARCHAR(50),
        email NVARCHAR(50),
        r_name NVARCHAR(150),
        l_fio NVARCHAR(50),
        timestamp DATETIME DEFAULT GETDATE() NOT NULL 
	)
go
create index insp_inspid_index on dbo.insp(insp_id)
go
create index insp_name_index on dbo.insp(name)
go


CREATE
    TABLE klcold
    (
		id INTEGER DEFAULT 0 NOT NULL,
        klcold_id INTEGER,
        kod NCHAR(1),
        name NVARCHAR(4),
        a_n_name NVARCHAR(10),
        timestamp DATETIME DEFAULT GETDATE() NOT NULL 
	)
go
create index klcold_klcoldid_index on dbo.klcold(klcold_id)
go
create index klcold_name_index on dbo.klcold(name)
go


CREATE
    TABLE sost
    (
		id INTEGER DEFAULT 0 NOT NULL,
        sost_id INTEGER,
        kod NCHAR(2),
        name NVARCHAR(100),
        s_name NVARCHAR(254),
        a_name NVARCHAR(100),
        notes NVARCHAR(200),
        st_rus NVARCHAR(100),
        st_eng NVARCHAR(100),
        timestamp DATETIME DEFAULT GETDATE() NOT NULL 
	)
go
create index sost_sostid_index on dbo.sost(sost_id)
go
create index sost_name_index on dbo.sost(name)
go


CREATE
    TABLE statgr
    (
		id INTEGER DEFAULT 0 NOT NULL,
        statgr_id INTEGER,
        kod NCHAR(2),
        name NVARCHAR(50),
        a_name NVARCHAR(50),
        kod_stat_t NVARCHAR(254),
        timestamp DATETIME DEFAULT GETDATE() NOT NULL 
	)
go
create index statgr_statgrid_index on dbo.statgr(statgr_id)
go
create index statgr_name_index on dbo.statgr(name)
go


CREATE
    TABLE stip
    (
		id INTEGER DEFAULT 0 NOT NULL,
        stip_id INTEGER,
        kod NCHAR(3),
        name NVARCHAR(50),
        s_name NVARCHAR(50),
        s_kod NCHAR(3),
        a_name NVARCHAR(50),
        as_kod NCHAR(3),
        timestamp DATETIME DEFAULT GETDATE() NOT NULL 
	)
go
create index stip_stipid_index on dbo.stip(stip_id)
go
create index stip_name_index on dbo.stip(name)
go


CREATE
    TABLE stran
    (
		id INTEGER DEFAULT 0 NOT NULL,
        stran_id INTEGER,
        kod NCHAR(2),
        a_name NVARCHAR(50),
        name NVARCHAR(50),
        f_name NVARCHAR(50),
        adr NVARCHAR(200),
        fax NVARCHAR(200),
        boss NVARCHAR(200),
        timestamp DATETIME DEFAULT GETDATE() NOT NULL 
	)
go
create index stran_stranid_index on dbo.stran(stran_id)
go
create index stran_name_index on dbo.stran(name)
go


CREATE
    TABLE tip
    (
		id INTEGER DEFAULT 0 NOT NULL,
        tip_id INTEGER,
        kod NCHAR(5),
        name NVARCHAR(100),
        name1 NVARCHAR(100),
        s_name NVARCHAR(3),
        s_kod NCHAR(2),
        a_name NVARCHAR(100),
        as_name NVARCHAR(3),
        timestamp DATETIME DEFAULT GETDATE() NOT NULL 
	)
go
create index tip_tipid_index on dbo.tip(tip_id)
go
create index tip_name_index on dbo.tip(name)
go
