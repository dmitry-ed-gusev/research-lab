package jdb;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Класс содержит константы для модуля db данной библиотеки. Эти константы определяют различные
 * неизменяемые параметры соединений с СУБД.
 * @author Gusev Dmitry
 * @version 5.1 (14.03.2011)
*/

public class DBConsts
 {
  /** Тип-перечисление поддерживаемых типов СУБД. */
  public static enum DBType
   {
    UNKNOWN      ("UNKNOWN",     -1),
    INFORMIX     ("INFORMIX",     0),
    MYSQL        ("MYSQL",        1),
    ODBC         ("ODBC",         2),
    DBF          ("DBF",          3),
    MSSQL_JTDS   ("MSSQL_JTDS",   4),
    MSSQL_NATIVE ("MSSQL_NATIVE", 5);

    // Поля класса-перечисления
    private final String sValue;
    private final int    iValue;

    // Конструктор класса-перечисления
    DBType(String sValue, int iValue) {this.sValue = sValue; this.iValue = iValue;}
    
    // Методы доступа к полям класса-перечисления
    public String strValue() {return sValue;}
    public int    intValue() {return iValue;}
   }

  /** Тип-перечисление типов таблиц. */
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
    UNKNOWN          ("UNKNOWN");        // <- данное значение есть в Информиксе

    // Поле класса-перечисления
    private String sValue;
    // Конструктор класса-перечисления
    TableType(String sValue) {this.sValue = sValue;}
    // Метод доступа к полю класса-перечисления
    public String strValue() {return sValue;}
   }

  /** Тип индекса (тип-перечисление). */
  public static enum IndexType {PRIMARY, FOREIGN, CASUAL}
  
  /** Тип соединения с СУБД - через источник данных на сервере приложений. */
  //public final static String DBCONNECTION_TYPE_ENTERPRISE = "enterprise";
  /** Тип соединения с СУБД - напрямую по протоколу TCP/IP. */
  //public final static String DBCONNECTION_TYPE_DIRECT     = "direct";

  /** Константа для обозначения значения "ИМЯ JNDI-ИСТОЧНИКА ДАННЫХ". */
  public static final String DB_DATA_SOURCE = "dataSource";
  /** Константа для обозначения значения "ТИП СУБД". */
  public final static String DB_TYPE        = "type";
  /** Константа для обозначения значения "ХОСТ". */
  public final static String DB_HOST        = "host";
  /** Константа для обозначения значения "СЕРВЕР". */
  public final static String DB_SERVER      = "server";
  /** Константа для обозначения значения "ИМЯ БД". */
  public final static String DB_NAME        = "dbname";
  /** Константа для обозначения значения "ПОЛЬЗОВАТЕЛЬ СУБД". */
  public final static String DB_USER        = "user";
  /** Константа для обозначения значения "ПАРОЛЬ ДЛЯ ДОСТУПА К СУБД". */
  public final static String DB_PWD         = "pwd";
  /** Константа для обозначения значения "ДОП. ПАР-РЫ ДЛЯ СОЕДИНЕНИЯ С СУБД". */
  public final static String DB_CONN_PARAMS = "params";
  /** Константа для обозначения значения "ДОП. ИНФ-ИЯ ДЛЯ СОЕДИНЕНИЯ С СУБД". */
  public final static String DB_CONN_INFO   = "info";

  
  /** Константа для обозначения значения: "ИСП-Е ПРЕПОДГОТОВЛЕННЫХ SQL-ЗАПРОСОВ". */
  //public final static String DBCONN_USE_PREPARED    = "use_prepared";
  /**
   * Список констант для обозначения конфигурационных параметров.
   * Эти параметры используются для соединения с СУБД. Данный список
   * параметров используется для создания/чтения наборов свойств в
   * цикле.
  */
  //public final static String[] DBCONN_KEYS =
  // {
  //  DBConsts.DB_TYPE, DBConsts.DB_HOST, DBConsts.DB_SERVER,
  //  DBConsts.DB_NAME,      DBConsts.DB_USER, DBConsts.DB_PWD,
  //  DBConsts.DB_CONN_PARAMS,  DBConsts.DB_CONN_INFO, DBConsts.DBCONN_USE_PREPARED
  // };

  /**
   * Константа, определяющая тип СУБД - Informix.
   * @deprecated данная константа не рекомендуется к использованию. Возможно будет удалена в следующих релизах.
  */
  //public final static String DBTYPE_INFORMIX = "informix";
  /**
   * Константа, определяющая тип СУБД - MySQL.
   * @deprecated данная константа не рекомендуется к использованию. Возможно будет удалена в следующих релизах.
  */
  //public final static String DBTYPE_MYSQL    = "mysql";
  /**
   * Константа, определяющая тип СУБД - ODBC-источник данных.
   * @deprecated данная константа не рекомендуется к использованию. Возможно будет удалена в следующих релизах.
  */
  //public final static String DBTYPE_ODBC     = "odbc";
  /**
   * Константа, определяющая тип СУБД - DBF-файл.
   * @deprecated данная константа не рекомендуется к использованию. Возможно будет удалена в следующих релизах. 
  */
  //public final static String DBTYPE_DBF      = "dbf";
  /**
   * Константа, определяющая тип СУБД - текстовый файл. Для данного типа БД нужен только один параметр -
   * наименование БД - имя текстового файла.
  */
  //public final static String DBCONN_DB_TYPE_TXT     = "txt";
  /**
   * Список допустимых типов СУБД для работы данной библиотеки.
   * @deprecated данная константа не рекомендуется к использованию. Возможно будет удалена в следующих релизах. 
  */
  //public final static String[] DBCONN_DB_TYPES_LIST_ =
  // {
  //  DBConsts.DBTYPE_INFORMIX,  DBConsts.DBTYPE_MYSQL,
  //  DBConsts.DBTYPE_ODBC, DBConsts.DBTYPE_DBF
  // };

  /**
   * Информация для соединения (connection info) для соединения с СУБД Информикс в кодировке win1251 и с форматом даты
   * дд/мм/гггг. Значение: [CLIENT_LOCALE=RU_RU.CP1251;SERVER_LOCALE=RU_RU.CP1251;DB_LOCALE=RU_RU.CP1251;DBLANG=RU_RU.CP1251;
   * GL_DATE=%d/%m/%iY]. Описание всех переменных окружения для соединения с СУБД Informix см. в документации к СУБД.
  */
  public final static String DBCONN_INFO_IFX1251   =
   "CLIENT_LOCALE=RU_RU.CP1251;SERVER_LOCALE=RU_RU.CP1251;DB_LOCALE=RU_RU.CP1251;DBLANG=RU_RU.CP1251;GL_DATE=%d/%m/%iY";

  /**
   * Информация для соединения (connection info) для соединения с dbf-файлами в формате DBASE4. Указывается кодировка CP866.
   * Значение: [charSet=Cp866]. Если не указать данное значение, то полученные из dbf-файла данные будут в неправильной
   * кодировке.
  */
  public final static String DBCONN_INFO_DBF_DBASE4 = "charSet=Cp866";

  /** СООБЩЕНИЕ ОБ ОШИБКЕ: "набор входных параметров пуст". */
  //public final static String DBCONN_ERR_ALL_PARAMS       = "Input parameters are invalid (is null or size()=0)!";
  /** СООБЩЕНИЕ ОБ ОШИБКЕ: "неверный параметр <db_type> для соединения с СУБД". */
  //public final static String DBCONN_ERR_DB_TYPE          = "[db_type] parameter is invalid!";
  /** СООБЩЕНИЕ ОБ ОШИБКЕ: "неверный параметр <host> для соединения с СУБД". */
  //public final static String DBCONN_ERR_HOST             = "[host]    parameter is invalid!";
  /** СООБЩЕНИЕ ОБ ОШИБКЕ: "неверный параметр <sever> для соединения с СУБД". */
  //public final static String DBCONN_ERR_SERVER           = "[server]  parameter is invalid!";
  /** СООБЩЕНИЕ ОБ ОШИБКЕ: "неверный параметр <db> для соединения с СУБД". */
  //public final static String DBCONN_ERR_DB               = "[db] parameter is invalid!";
  /** СООБЩЕНИЕ ОБ ОШИБКЕ: "пустой sql-запрос". */
  //public final static String DBCONN_ERR_EMPTY_SQL        = "SQL-query is empty!";
  /** СООБЩЕНИЕ ОБ ОШИБКЕ: "не инициализирован объект SqlStatement". */
  //public final static String DBCONN_ERR_SQLSTMT_INIT     = "Object SqlStatement is not initialized!";
  /** СООБЩЕНИЕ ОБ ОШИБКЕ: "не инициализирован объект PSqlStatement". */
  //public final static String DBCONN_ERR_PSQLSTMT_INIT    = "Object PSqlStatement is not initialized!";
  /** СООБЩЕНИЕ ОБ ОШИБКЕ: "ошибочные входные параметры". */
  //public final static String DBCONN_ERR_INIT_PARAMS      = "Input parameters are invalid!";
  /** СООБЩЕНИЕ ОБ ОШИБКЕ: "неверный тип результирующего курсора". */
  //public final static String DBCONN_ERR_RESULT_SET_TYPE  = "Invalid ResultSet type!";
  /** СООБЩЕНИЕ ОБ ОШИБКЕ: "не могу загрузить драйвер СУБД". */
  //public final static String DBCONN_ERR_CANT_LOAD_DRIVER = "Can't unload DBMS driver!";
  /** СООБЩЕНИЕ ОБ ОШИБКЕ: "пул соединений пуст (не инициализирован)". */
  //public final static String DBCONN_ERR_CONNPOOL_INIT    = "Connection pool is empty!";

  /** Класс драйвера для соединения с СУБД Informix. */
  public static final String DBDRIVER_INFORMIX            = "com.informix.jdbc.IfxDriver";
  /** Класс драйвера для соединения с СУБД MySQL. */
  public static final String DBDRIVER_MYSQL               = "com.mysql.jdbc.Driver";
  /** Класс драйвера для соединения с ODBC-источником. */
  public static final String DBDRIVER_ODBC                = "sun.jdbc.odbc.JdbcOdbcDriver";
  /** Класс драйвера для соединения с DBF-файлом. */
  public static final String DBDRIVER_DBF                 = "com.hxtt.sql.dbf.DBFDriver";
  /** Класс драйвера для соединения с СУБД MS SQL с использованием драйверов JTDS. */
  public static final String DBDRIVER_MSSQL_JTDS          = "net.sourceforge.jtds.jdbc.Driver";
  /** Класс драйвера для соединения с СУБД MS SQL с использованием родных драйверов от Microsoft. */
  public static final String DBDRIVER_MSSQL_NATIVE        = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

  /**
   * Для многопоточных методов - максимально возможное количество одновременных соединений с СУБД. Данное значение (50)
   * достаточно велико и на меделнных машинах будет вызывать ошибки. Для каждой машины это значение (кол-во соединений)
   * должно выбираться индивидуально, но значения бОльшие данного - не должны использоваться вообще (рекомендация)! 
  */
  public static final int    MAX_DBMS_CONNECTIONS         = 50;
  /**
   * Для многопоточных методов - минимально возможное количество одновременных соединений с СУБД. Значения меньшие
   * данного 0 и 1 не имеют смысла - многопоточная обработка в 1 поток бессмысленна, так же как и в 0 потоков.
  */
  public static final int    MIN_DBMS_CONNECTIONS         = 2;

  /**
   * Для операций с мониторингом - максимальный шаг выполненных операций для мониторинга, после достижения которого
   * производится вывод сообщения для монитора. 
  */
  public static final int    MAX_MONITOR_OPERATIONS_COUNT = 5000;
  /**
   * Для операций с мониторингом - минимальный шаг выполненных операций для мониторинга, после достижения которого
   * производится вывод сообщения для монитора.
  */
  public static final int    MIN_MONITOR_OPERATIONS_COUNT = 50;

  /**
   * Максимальное количество потоков для многопотоковых задач, не связанных с подключениями к СУБД. Для таких задач
   * (связанных с подключением к СУБД) предусмотрена константа MAX_DBMS_CONNECTIONS. Также как и константа
   * MAX_DBMS_CONNECTIONS слишком большое значение данной константы будет вызывать большое число ошибок.
  */
  public static final int    MAX_THREADS                  = 50;

  /** Статус записи в таблице - удалена. */
  public static final int    RECORD_STATUS_DELETED        = 1;
  /** Статус записи в таблице - активна. */
  public static final int    RECORD_STATUS_ACTIVE         = 0;

  /**
   * Количество записей одной таблицы для сериализации в один файл. Этот параметр влияет и на модуль десериализации
   * данных - при слишком большой его величине десериализация становится очень требовательной к памяти ПК, что приводит
   * к неработоспособности при малом ее количестве (ошибка OutOfMemory). Также величина параметра влияет на размер
   * одного файла с данными таблицы, но т.к. файл архивируется (ZIP), то размер растет не сильно. Слишком маленькое
   * значение данного параметра приведет к формированию большого количества файлов (при сериализации) и к существенным
   * потерям производительности за счет обработки большого количества файлов (и при сериализации, и при десериализации).
   * Рекомендуемое значение данного параметра лежит в диапазоне 250 - 2000 (записей). Также при выборе значения данного
   * параметра рекомендуется учитывать длину одной записи - если длина небольшая, то можно сериализовать больше строк в
   * один файл без существенного увеличения его (файла) размера, если же длина одной записи существенна - 30Кб и более,
   * рекомендуется выбирать меньшее значение (это просто общие соображения).
  */
  public static final int    SERIALIZATION_TABLE_FRACTION = 1000;

  /** Наименование корневого элемента конфигурационного xml-файла. */
  public static final String XML_DB_ROOT_TAG        = "db-settings";
  /** Наименование элемента второго уровня конфигурационного xml-файла. */
  public static final String XML_DB_TAG             = "db";
  
  /** Наименование параметра конфигурации обработки данных: кол-во соединений с СУБД. */
  public static final String XML_DBMS_CONN_NUMBER   = "connNumber";
  /** Наименование параметра конфигурации обработки данных: шаг количества операций обработки для выдачи сообщения. */
  public static final String XML_OPERATIONS_COUNTER = "opsCounter";
  /** Наименование параметра конфигурации обработки данных: вкл/выкл многопотоковая обработка данных. */
  public static final String XML_MULTI_THREADS      = "multiThreads";

  /**
   * Константа, указывающая как в файле конфигурации хранится пароль - в открытом виде или в виде зашифрованного
   * файла. Истина(true) - в открытом виде (значение по умолчанию), ложь(false) - в открытом виде.
  */
  public static final boolean XML_USE_PLAIN_PASS   = true;

  /**
   * Константа для метода многопоточного выполнения sql-батча. Означает следующее: сколько минимально sql-запросов
   * из батча должно приходиться на одно соединение с СУБД (один поток). Если соотношение будет меньше - многопотоковое
   * выполнение sql-батча не имеет смысла (одно соединение-один sql-запрос - бессмысленная ситуация).
  */
  public static final int     MIN_RATIO_DBMS_CONN_TO_SQL = 5;

  /**
   * Таблицы системного каталога СУБД Informix. Версия 9.4 IDS.2000. Данные таблицы являются системными и работать с ними
   * не рекомендуется - за исключением случаев написания менеджеров БД. Схема данных для этих таблиц не используется, т.к.
   * Информикс не позволяет создать в разных схемах таблицы с одинаковыми именами.
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
   * Системный каталог СУБД MS SQL 2005. Кроме указанного, в системный каталог СУБД входят все представления (VIEW),
   * находящиеся в двух схемах SYS и INFORMATION_SCHEMA (они будут отсечены по имени схемы).
  */
  public final static ArrayList<String> SYSCATALOG_MSSQL = new ArrayList<String>(Arrays.asList("DBO.SYSDIAGRAMS"));

  /** Тег метаинформации о базе данных (DatabaseMetaData) для JDBC-драйвера. */
  public static final String META_DATA_TABLE_CAT        = "TABLE_CAT";
  /** Тег метаинформации о базе данных (DatabaseMetaData) для JDBC-драйвера. */
  public static final String META_DATA_TABLE_SCHEM      = "TABLE_SCHEM";
  /** Тег метаинформации о базе данных (DatabaseMetaData) для JDBC-драйвера. */
  public static final String META_DATA_TABLE_NAME       = "TABLE_NAME";
  /** Тег метаинформации о базе данных (DatabaseMetaData) для JDBC-драйвера. */
  public static final String META_DATA_TABLE_TYPE       = "TABLE_TYPE";

  /** Тег метаинформации о базе данных (DatabaseMetaData) для JDBC-драйвера. */
  public static final String META_DATA_COLUMN_NAME      = "COLUMN_NAME";
  /** Тег метаинформации о базе данных (DatabaseMetaData) для JDBC-драйвера. */
  public static final String META_DATA_COLUMN_DATA_TYPE = "DATA_TYPE";
  /** Тег метаинформации о базе данных (DatabaseMetaData) для JDBC-драйвера. */
  public static final String META_DATA_COLUMN_TYPE_NAME = "TYPE_NAME";
  /** Тег метаинформации о базе данных (DatabaseMetaData) для JDBC-драйвера. */
  public static final String META_DATA_COLUMN_SIZE      = "COLUMN_SIZE";
  /** Тег метаинформации о базе данных (DatabaseMetaData) для JDBC-драйвера. */
  public static final String META_DATA_COLUMN_NULLABLE  = "NULLABLE";
  /** Тег метаинформации о базе данных (DatabaseMetaData) для JDBC-драйвера. */
  public static final String META_DATA_COLUMN_DEFAULT   = "COLUMN_DEF";
  /** Тег метаинформации о базе данных (DatabaseMetaData) для JDBC-драйвера. */
  public static final String META_DATA_COLUMN_PRECISION = "PRECISION";
  
  /** Тег метаинформации о базе данных (DatabaseMetaData) для JDBC-драйвера. */
  public static final String META_DATA_INDEX_NAME      = "INDEX_NAME";
  /** Тег метаинформации о базе данных (DatabaseMetaData) для JDBC-драйвера. */
  public static final String META_DATA_NON_UNIQUE      = "NON_UNIQUE";

  /** Наименование поля с датой/временем последнего обновления записи (timestamp). */
  public static final String FIELD_NAME_TIMESTAMP      = "TIMESTAMP";
  /** Наименование ключевого поля для любой таблицы (primary key). */
  public static final String FIELD_NAME_KEY            = "ID";
  /** Наименование поля с отметкой об активности/удаленности записи. */
  public static final String FIELD_NAME_DELETED        = "DELETED";

  /**
   * Параметр для системы многопотокового выполнения sql-запросов. Смотри информация о классе
   * {@link jdb.processing.sql.execution.batch.executors.MultiThreadsSqlBatchExecutor MultiThreadsSqlBatchExecutor}.
   * Количество "пустых" итераций цикла ожидания окончания всех потоков в группе, после которого будет выведена
   * отладочная информация и сообщение монитору о количестве обработанных запросов во всех потоках.
  */
  public static final int    WAIT_CYCLE_STEPS_COUNT     = 50000;
  /**
   * Параметр для системы многопотокового выполнения sql-запросов. Шаг количества выполненных в одном потоке sql-запросов,
   * для которого выполняется обновление общего для всех потоков счетчика выполненных запросов (обновление из выполняемого
   * потока). Более подробно - см. класс
   * {@link jdb.processing.sql.execution.batch.executors.multiThreadsHelpers.SqlBatchRunnable SqlBatchRunnable}.
  */
  public static final int    THREAD_COUNTER_UPDATE_STEP = 10;

  /**
   * Данная константа используется методами фильтрации sql-запросов. Восьмеричные коды специальных(неотображаемых) символов.
   * Эти символы должны быть удалены из sql-запроса. Некоторые символы пропущены в данном списке - вот они: \11 - символ
   * табуляции, \12 - новая строка(перевод каретки, line feed), \15 - аналог новой строки (form-feed), \40 - символ пробела -
   * первый неспециальный отображаемый символ.
  */
  public static final String[] SQL_DEPRECATED_SYMBOLS = {"\00", "\01", "\02", "\03", "\04", "\05", "\06", "\07", "\10", "\13", "\14",
                                             "\16", "\17", "\20", "\21", "\22", "\23", "\24", "\25", "\26", "\27", "\30",
                                             "\31", "\32", "\33", "\34", "\35", "\36", "\37"};

  /**
   * Данная константа используется методами фильтрации sql-запросов. Список символов кавычек, которые будут заменены
   * на кавычку по умолчанию - SQL_DEFAULT_QUOTE.
  */
  public static final String[] SQL_DEPRECATED_QUOTES = {"“", "”", "'"};

  /**
   * Данная константа используется методами фильтрации sql-запросов. Символ кавычки по умолчанию. На него заменяются все
   * остальные символы кавычек (они перечислены в списке quotes).
  */
  public static final String SQL_DEFAULT_QUOTE = "\"";

  /**
   * Метод только для отладки и тестирования.
   * @param args String[] параметры метода main.
  */
  public static void main(String[] args)
   {
    DBType db = DBType.INFORMIX;
    System.out.println(db);
   }

  
 }