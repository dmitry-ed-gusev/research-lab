package jdb;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * ����� �������� ��������� ��� ������ db ������ ����������. ��� ��������� ���������� ���������
 * ������������ ��������� ���������� � ����.
 * @author Gusev Dmitry
 * @version 5.1 (14.03.2011)
*/

public class DBConsts
 {
  /** ���-������������ �������������� ����� ����. */
  public static enum DBType
   {
    UNKNOWN      ("UNKNOWN",     -1),
    INFORMIX     ("INFORMIX",     0),
    MYSQL        ("MYSQL",        1),
    ODBC         ("ODBC",         2),
    DBF          ("DBF",          3),
    MSSQL_JTDS   ("MSSQL_JTDS",   4),
    MSSQL_NATIVE ("MSSQL_NATIVE", 5);

    // ���� ������-������������
    private final String sValue;
    private final int    iValue;

    // ����������� ������-������������
    DBType(String sValue, int iValue) {this.sValue = sValue; this.iValue = iValue;}
    
    // ������ ������� � ����� ������-������������
    public String strValue() {return sValue;}
    public int    intValue() {return iValue;}
   }

  /** ���-������������ ����� ������. */
  public static enum TableType
   {
    ALL              ("ALL"),
    TABLE            ("TABLE"),
    VIEW             ("VIEW"),
    SYSTEM_TABLE     ("SYSTEM TABLE"),
    GLOBAL_TEMPORARY ("GLOBAL TEMPORARY"),
    LOCAL_TEMPORARY  ("LOCAL TEMPORARY"),
    ALIAS            ("ALIAS"),
    SYNONYM          ("SYNONYM"),
    UNKNOWN          ("UNKNOWN");        // <- ������ �������� ���� � ����������

    // ���� ������-������������
    private String sValue;
    // ����������� ������-������������
    TableType(String sValue) {this.sValue = sValue;}
    // ����� ������� � ���� ������-������������
    public String strValue() {return sValue;}
   }

  /** ��� ������� (���-������������). */
  public static enum IndexType {PRIMARY, FOREIGN, CASUAL}
  
  /** ��� ���������� � ���� - ����� �������� ������ �� ������� ����������. */
  //public final static String DBCONNECTION_TYPE_ENTERPRISE = "enterprise";
  /** ��� ���������� � ���� - �������� �� ��������� TCP/IP. */
  //public final static String DBCONNECTION_TYPE_DIRECT     = "direct";

  /** ��������� ��� ����������� �������� "��� JNDI-��������� ������". */
  public static final String DB_DATA_SOURCE = "dataSource";
  /** ��������� ��� ����������� �������� "��� ����". */
  public final static String DB_TYPE        = "type";
  /** ��������� ��� ����������� �������� "����". */
  public final static String DB_HOST        = "host";
  /** ��������� ��� ����������� �������� "������". */
  public final static String DB_SERVER      = "server";
  /** ��������� ��� ����������� �������� "��� ��". */
  public final static String DB_NAME        = "dbname";
  /** ��������� ��� ����������� �������� "������������ ����". */
  public final static String DB_USER        = "user";
  /** ��������� ��� ����������� �������� "������ ��� ������� � ����". */
  public final static String DB_PWD         = "pwd";
  /** ��������� ��� ����������� �������� "���. ���-�� ��� ���������� � ����". */
  public final static String DB_CONN_PARAMS = "params";
  /** ��������� ��� ����������� �������� "���. ���-�� ��� ���������� � ����". */
  public final static String DB_CONN_INFO   = "info";

  
  /** ��������� ��� ����������� ��������: "���-� ����������������� SQL-��������". */
  //public final static String DBCONN_USE_PREPARED    = "use_prepared";
  /**
   * ������ �������� ��� ����������� ���������������� ����������.
   * ��� ��������� ������������ ��� ���������� � ����. ������ ������
   * ���������� ������������ ��� ��������/������ ������� ������� �
   * �����.
  */
  //public final static String[] DBCONN_KEYS =
  // {
  //  DBConsts.DB_TYPE, DBConsts.DB_HOST, DBConsts.DB_SERVER,
  //  DBConsts.DB_NAME,      DBConsts.DB_USER, DBConsts.DB_PWD,
  //  DBConsts.DB_CONN_PARAMS,  DBConsts.DB_CONN_INFO, DBConsts.DBCONN_USE_PREPARED
  // };

  /**
   * ���������, ������������ ��� ���� - Informix.
   * @deprecated ������ ��������� �� ������������� � �������������. �������� ����� ������� � ��������� �������.
  */
  //public final static String DBTYPE_INFORMIX = "informix";
  /**
   * ���������, ������������ ��� ���� - MySQL.
   * @deprecated ������ ��������� �� ������������� � �������������. �������� ����� ������� � ��������� �������.
  */
  //public final static String DBTYPE_MYSQL    = "mysql";
  /**
   * ���������, ������������ ��� ���� - ODBC-�������� ������.
   * @deprecated ������ ��������� �� ������������� � �������������. �������� ����� ������� � ��������� �������.
  */
  //public final static String DBTYPE_ODBC     = "odbc";
  /**
   * ���������, ������������ ��� ���� - DBF-����.
   * @deprecated ������ ��������� �� ������������� � �������������. �������� ����� ������� � ��������� �������. 
  */
  //public final static String DBTYPE_DBF      = "dbf";
  /**
   * ���������, ������������ ��� ���� - ��������� ����. ��� ������� ���� �� ����� ������ ���� �������� -
   * ������������ �� - ��� ���������� �����.
  */
  //public final static String DBCONN_DB_TYPE_TXT     = "txt";
  /**
   * ������ ���������� ����� ���� ��� ������ ������ ����������.
   * @deprecated ������ ��������� �� ������������� � �������������. �������� ����� ������� � ��������� �������. 
  */
  //public final static String[] DBCONN_DB_TYPES_LIST_ =
  // {
  //  DBConsts.DBTYPE_INFORMIX,  DBConsts.DBTYPE_MYSQL,
  //  DBConsts.DBTYPE_ODBC, DBConsts.DBTYPE_DBF
  // };

  /**
   * ���������� ��� ���������� (connection info) ��� ���������� � ���� ��������� � ��������� win1251 � � �������� ����
   * ��/��/����. ��������: [CLIENT_LOCALE=RU_RU.CP1251;SERVER_LOCALE=RU_RU.CP1251;DB_LOCALE=RU_RU.CP1251;DBLANG=RU_RU.CP1251;
   * GL_DATE=%d/%m/%iY]. �������� ���� ���������� ��������� ��� ���������� � ���� Informix ��. � ������������ � ����.
  */
  public final static String DBCONN_INFO_IFX1251   =
   "CLIENT_LOCALE=RU_RU.CP1251;SERVER_LOCALE=RU_RU.CP1251;DB_LOCALE=RU_RU.CP1251;DBLANG=RU_RU.CP1251;GL_DATE=%d/%m/%iY";

  /**
   * ���������� ��� ���������� (connection info) ��� ���������� � dbf-������� � ������� DBASE4. ����������� ��������� CP866.
   * ��������: [charSet=Cp866]. ���� �� ������� ������ ��������, �� ���������� �� dbf-����� ������ ����� � ������������
   * ���������.
  */
  public final static String DBCONN_INFO_DBF_DBASE4 = "charSet=Cp866";

  /** ��������� �� ������: "����� ������� ���������� ����". */
  //public final static String DBCONN_ERR_ALL_PARAMS       = "Input parameters are invalid (is null or size()=0)!";
  /** ��������� �� ������: "�������� �������� <db_type> ��� ���������� � ����". */
  //public final static String DBCONN_ERR_DB_TYPE          = "[db_type] parameter is invalid!";
  /** ��������� �� ������: "�������� �������� <host> ��� ���������� � ����". */
  //public final static String DBCONN_ERR_HOST             = "[host]    parameter is invalid!";
  /** ��������� �� ������: "�������� �������� <sever> ��� ���������� � ����". */
  //public final static String DBCONN_ERR_SERVER           = "[server]  parameter is invalid!";
  /** ��������� �� ������: "�������� �������� <db> ��� ���������� � ����". */
  //public final static String DBCONN_ERR_DB               = "[db] parameter is invalid!";
  /** ��������� �� ������: "������ sql-������". */
  //public final static String DBCONN_ERR_EMPTY_SQL        = "SQL-query is empty!";
  /** ��������� �� ������: "�� ��������������� ������ SqlStatement". */
  //public final static String DBCONN_ERR_SQLSTMT_INIT     = "Object SqlStatement is not initialized!";
  /** ��������� �� ������: "�� ��������������� ������ PSqlStatement". */
  //public final static String DBCONN_ERR_PSQLSTMT_INIT    = "Object PSqlStatement is not initialized!";
  /** ��������� �� ������: "��������� ������� ���������". */
  //public final static String DBCONN_ERR_INIT_PARAMS      = "Input parameters are invalid!";
  /** ��������� �� ������: "�������� ��� ��������������� �������". */
  //public final static String DBCONN_ERR_RESULT_SET_TYPE  = "Invalid ResultSet type!";
  /** ��������� �� ������: "�� ���� ��������� ������� ����". */
  //public final static String DBCONN_ERR_CANT_LOAD_DRIVER = "Can't unload DBMS driver!";
  /** ��������� �� ������: "��� ���������� ���� (�� ���������������)". */
  //public final static String DBCONN_ERR_CONNPOOL_INIT    = "Connection pool is empty!";

  /** ����� �������� ��� ���������� � ���� Informix. */
  public static final String DBDRIVER_INFORMIX            = "com.informix.jdbc.IfxDriver";
  /** ����� �������� ��� ���������� � ���� MySQL. */
  public static final String DBDRIVER_MYSQL               = "com.mysql.jdbc.Driver";
  /** ����� �������� ��� ���������� � ODBC-����������. */
  public static final String DBDRIVER_ODBC                = "sun.jdbc.odbc.JdbcOdbcDriver";
  /** ����� �������� ��� ���������� � DBF-������. */
  public static final String DBDRIVER_DBF                 = "com.hxtt.sql.dbf.DBFDriver";
  /** ����� �������� ��� ���������� � ���� MS SQL � �������������� ��������� JTDS. */
  public static final String DBDRIVER_MSSQL_JTDS          = "net.sourceforge.jtds.jdbc.Driver";
  /** ����� �������� ��� ���������� � ���� MS SQL � �������������� ������ ��������� �� Microsoft. */
  public static final String DBDRIVER_MSSQL_NATIVE        = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

  /**
   * ��� ������������� ������� - ����������� ��������� ���������� ������������� ���������� � ����. ������ �������� (50)
   * ���������� ������ � �� ��������� ������� ����� �������� ������. ��� ������ ������ ��� �������� (���-�� ����������)
   * ������ ���������� �������������, �� �������� ������� ������� - �� ������ �������������� ������ (������������)! 
  */
  public static final int    MAX_DBMS_CONNECTIONS         = 50;
  /**
   * ��� ������������� ������� - ���������� ��������� ���������� ������������� ���������� � ����. �������� �������
   * ������� 0 � 1 �� ����� ������ - ������������� ��������� � 1 ����� ������������, ��� �� ��� � � 0 �������.
  */
  public static final int    MIN_DBMS_CONNECTIONS         = 2;

  /**
   * ��� �������� � ������������ - ������������ ��� ����������� �������� ��� �����������, ����� ���������� ��������
   * ������������ ����� ��������� ��� ��������. 
  */
  public static final int    MAX_MONITOR_OPERATIONS_COUNT = 5000;
  /**
   * ��� �������� � ������������ - ����������� ��� ����������� �������� ��� �����������, ����� ���������� ��������
   * ������������ ����� ��������� ��� ��������.
  */
  public static final int    MIN_MONITOR_OPERATIONS_COUNT = 50;

  /**
   * ������������ ���������� ������� ��� �������������� �����, �� ��������� � ������������� � ����. ��� ����� �����
   * (��������� � ������������ � ����) ������������� ��������� MAX_DBMS_CONNECTIONS. ����� ��� � ���������
   * MAX_DBMS_CONNECTIONS ������� ������� �������� ������ ��������� ����� �������� ������� ����� ������.
  */
  public static final int    MAX_THREADS                  = 50;

  /** ������ ������ � ������� - �������. */
  public static final int    RECORD_STATUS_DELETED        = 1;
  /** ������ ������ � ������� - �������. */
  public static final int    RECORD_STATUS_ACTIVE         = 0;

  /**
   * ���������� ������� ����� ������� ��� ������������ � ���� ����. ���� �������� ������ � �� ������ ��������������
   * ������ - ��� ������� ������� ��� �������� �������������� ���������� ����� �������������� � ������ ��, ��� ��������
   * � ������������������� ��� ����� �� ���������� (������ OutOfMemory). ����� �������� ��������� ������ �� ������
   * ������ ����� � ������� �������, �� �.�. ���� ������������ (ZIP), �� ������ ������ �� ������. ������� ���������
   * �������� ������� ��������� �������� � ������������ �������� ���������� ������ (��� ������������) � � ������������
   * ������� ������������������ �� ���� ��������� �������� ���������� ������ (� ��� ������������, � ��� ��������������).
   * ������������� �������� ������� ��������� ����� � ��������� 250 - 2000 (�������). ����� ��� ������ �������� �������
   * ��������� ������������� ��������� ����� ����� ������ - ���� ����� ���������, �� ����� ������������� ������ ����� �
   * ���� ���� ��� ������������� ���������� ��� (�����) �������, ���� �� ����� ����� ������ ����������� - 30�� � �����,
   * ������������� �������� ������� �������� (��� ������ ����� �����������).
  */
  public static final int    SERIALIZATION_TABLE_FRACTION = 1000;

  /** ������������ ��������� �������� ����������������� xml-�����. */
  public static final String XML_DB_ROOT_TAG        = "db-settings";
  /** ������������ �������� ������� ������ ����������������� xml-�����. */
  public static final String XML_DB_TAG             = "db";
  
  /** ������������ ��������� ������������ ��������� ������: ���-�� ���������� � ����. */
  public static final String XML_DBMS_CONN_NUMBER   = "connNumber";
  /** ������������ ��������� ������������ ��������� ������: ��� ���������� �������� ��������� ��� ������ ���������. */
  public static final String XML_OPERATIONS_COUNTER = "opsCounter";
  /** ������������ ��������� ������������ ��������� ������: ���/���� �������������� ��������� ������. */
  public static final String XML_MULTI_THREADS      = "multiThreads";

  /**
   * ���������, ����������� ��� � ����� ������������ �������� ������ - � �������� ���� ��� � ���� ��������������
   * �����. ������(true) - � �������� ���� (�������� �� ���������), ����(false) - � �������� ����.
  */
  public static final boolean XML_USE_PLAIN_PASS   = true;

  /**
   * ��������� ��� ������ �������������� ���������� sql-�����. �������� ���������: ������� ���������� sql-��������
   * �� ����� ������ ����������� �� ���� ���������� � ���� (���� �����). ���� ����������� ����� ������ - ��������������
   * ���������� sql-����� �� ����� ������ (���� ����������-���� sql-������ - ������������� ��������).
  */
  public static final int     MIN_RATIO_DBMS_CONN_TO_SQL = 5;

  /**
   * ������� ���������� �������� ���� Informix. ������ 9.4 IDS.2000. ������ ������� �������� ���������� � �������� � ����
   * �� ������������� - �� ����������� ������� ��������� ���������� ��. ����� ������ ��� ���� ������ �� ������������, �.�.
   * ��������� �� ��������� ������� � ������ ������ ������� � ����������� �������.
  */
  public final static ArrayList<String> SYSCATALOG_INFORMIX =
   new ArrayList<String>(Arrays.asList
    (
     "SYSAGGREGATES", "SYSAMS", "SYSATTRTYPES", "SYSBLOBS", "SYSCASTS", "SYSCHECKS", "SYSCOLATTRIBS",
     "SYSCOLAUTH", "SYSCOLDEPEND", "SYSCOLUMNS", "SYSCONSTRAINTS", "SYSDEFAULTS", "SYSDEPEND", "SYSDISTRIB",
     "SYSERRORS", "SYSEXTCOLS", "SYSEXTDFILES", "SYSEXTERNAL", "SYSFRAGAUTH", "SYSFRAGMENTS", "SYSINDEXES",
     "SYSINDICES", "SYSINHERITS", "SYSLANGAUTH", "SYSLOGMAP", "SYSNEWDEPEND", "SYSOBJSTATE", "SYSOPCLASSES",
     "SYSOPCLSTR", "SYSPROCAUTH", "SYSPROCBODY", "SYSPROCEDURES", "SYSPROCPLAN", "SYSREFERENCES", "SYSREPOSITORY",
     "SYSROLEAUTH", "SYSROUTINELANGS", "SYSSYNONYMS", "SYSSYNTABLE", "SYSTABAMDATA", "SYSTABAUTH", "SYSTABLES",
     "SYSTRACECLASSES", "SYSTRACEMSGS", "SYSTRIGBODY", "SYSTRIGGERS", "SYSUSERS", "SYSVIEWS", "SYSVIOLATIONS",
     "SYSXTDDESC", "SYSXTDTYPEAUTH", "SYSXTDTYPES", "SYSSEQUENCES", "SYSDOMAINS", "GL_COLLATE", "GL_CTYPE", "VERSION"
    ));

  /**
   * ��������� ������� ���� MS SQL 2005. ����� ����������, � ��������� ������� ���� ������ ��� ������������� (VIEW),
   * ����������� � ���� ������ SYS � INFORMATION_SCHEMA (��� ����� �������� �� ����� �����).
  */
  public final static ArrayList<String> SYSCATALOG_MSSQL = new ArrayList<String>(Arrays.asList("DBO.SYSDIAGRAMS"));

  /** ��� �������������� � ���� ������ (DatabaseMetaData) ��� JDBC-��������. */
  public static final String META_DATA_TABLE_CAT        = "TABLE_CAT";
  /** ��� �������������� � ���� ������ (DatabaseMetaData) ��� JDBC-��������. */
  public static final String META_DATA_TABLE_SCHEM      = "TABLE_SCHEM";
  /** ��� �������������� � ���� ������ (DatabaseMetaData) ��� JDBC-��������. */
  public static final String META_DATA_TABLE_NAME       = "TABLE_NAME";
  /** ��� �������������� � ���� ������ (DatabaseMetaData) ��� JDBC-��������. */
  public static final String META_DATA_TABLE_TYPE       = "TABLE_TYPE";

  /** ��� �������������� � ���� ������ (DatabaseMetaData) ��� JDBC-��������. */
  public static final String META_DATA_COLUMN_NAME      = "COLUMN_NAME";
  /** ��� �������������� � ���� ������ (DatabaseMetaData) ��� JDBC-��������. */
  public static final String META_DATA_COLUMN_DATA_TYPE = "DATA_TYPE";
  /** ��� �������������� � ���� ������ (DatabaseMetaData) ��� JDBC-��������. */
  public static final String META_DATA_COLUMN_TYPE_NAME = "TYPE_NAME";
  /** ��� �������������� � ���� ������ (DatabaseMetaData) ��� JDBC-��������. */
  public static final String META_DATA_COLUMN_SIZE      = "COLUMN_SIZE";
  /** ��� �������������� � ���� ������ (DatabaseMetaData) ��� JDBC-��������. */
  public static final String META_DATA_COLUMN_NULLABLE  = "NULLABLE";
  /** ��� �������������� � ���� ������ (DatabaseMetaData) ��� JDBC-��������. */
  public static final String META_DATA_COLUMN_DEFAULT   = "COLUMN_DEF";
  /** ��� �������������� � ���� ������ (DatabaseMetaData) ��� JDBC-��������. */
  public static final String META_DATA_COLUMN_PRECISION = "PRECISION";
  
  /** ��� �������������� � ���� ������ (DatabaseMetaData) ��� JDBC-��������. */
  public static final String META_DATA_INDEX_NAME      = "INDEX_NAME";
  /** ��� �������������� � ���� ������ (DatabaseMetaData) ��� JDBC-��������. */
  public static final String META_DATA_NON_UNIQUE      = "NON_UNIQUE";

  /** ������������ ���� � �����/�������� ���������� ���������� ������ (timestamp). */
  public static final String FIELD_NAME_TIMESTAMP      = "TIMESTAMP";
  /** ������������ ��������� ���� ��� ����� ������� (primary key). */
  public static final String FIELD_NAME_KEY            = "ID";
  /** ������������ ���� � �������� �� ����������/����������� ������. */
  public static final String FIELD_NAME_DELETED        = "DELETED";

  /**
   * �������� ��� ������� ��������������� ���������� sql-��������. ������ ���������� � ������
   * {@link jdb.processing.sql.execution.batch.executors.MultiThreadsSqlBatchExecutor MultiThreadsSqlBatchExecutor}.
   * ���������� "������" �������� ����� �������� ��������� ���� ������� � ������, ����� �������� ����� ��������
   * ���������� ���������� � ��������� �������� � ���������� ������������ �������� �� ���� �������.
  */
  public static final int    WAIT_CYCLE_STEPS_COUNT     = 50000;
  /**
   * �������� ��� ������� ��������������� ���������� sql-��������. ��� ���������� ����������� � ����� ������ sql-��������,
   * ��� �������� ����������� ���������� ������ ��� ���� ������� �������� ����������� �������� (���������� �� ������������
   * ������). ����� �������� - ��. �����
   * {@link jdb.processing.sql.execution.batch.executors.multiThreadsHelpers.SqlBatchRunnable SqlBatchRunnable}.
  */
  public static final int    THREAD_COUNTER_UPDATE_STEP = 10;

  /**
   * ������ ��������� ������������ �������� ���������� sql-��������. ������������ ���� �����������(��������������) ��������.
   * ��� ������� ������ ���� ������� �� sql-�������. ��������� ������� ��������� � ������ ������ - ��� ���: \11 - ������
   * ���������, \12 - ����� ������(������� �������, line feed), \15 - ������ ����� ������ (form-feed), \40 - ������ ������� -
   * ������ ������������� ������������ ������.
  */
  public static final String[] SQL_DEPRECATED_SYMBOLS = {"\00", "\01", "\02", "\03", "\04", "\05", "\06", "\07", "\10", "\13", "\14",
                                             "\16", "\17", "\20", "\21", "\22", "\23", "\24", "\25", "\26", "\27", "\30",
                                             "\31", "\32", "\33", "\34", "\35", "\36", "\37"};

  /**
   * ������ ��������� ������������ �������� ���������� sql-��������. ������ �������� �������, ������� ����� ��������
   * �� ������� �� ��������� - SQL_DEFAULT_QUOTE.
  */
  public static final String[] SQL_DEPRECATED_QUOTES = {"�", "�", "'"};

  /**
   * ������ ��������� ������������ �������� ���������� sql-��������. ������ ������� �� ���������. �� ���� ���������� ���
   * ��������� ������� ������� (��� ����������� � ������ quotes).
  */
  public static final String SQL_DEFAULT_QUOTE = "\"";

  /**
   * ����� ������ ��� ������� � ������������.
   * @param args String[] ��������� ������ main.
  */
  public static void main(String[] args)
   {
    DBType db = DBType.INFORMIX;
    System.out.println(db);
   }

  
 }