package jdb.config.common;

import jdb.DBConsts;
import jdb.config.DBConfig;
import jdb.exceptions.DBModuleConfigException;
import jdb.monitoring.DBProcessingMonitor;
import jdb.utils.DBUtils;
import jlib.logging.InitLogger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * ����� �����-������ ��� ������� ���������������� ������� ������� ��������� ������. ������������� ������� ������ ��������
 * �������������� ����������������.
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 21.06.2010)
*/

public class CommonModuleConfig implements ConfigInterface
 {
  /** ���������-������ ������� ������. */
  private Logger logger = Logger.getLogger(getClass().getName());

  /** ������������ ��� ���������� � ����. */
  private DBConfig            dbConfig            = null;
  /**
   * ������ �������� ������������ ������ ������� �������������� ��������� sql-�����. ���������� ���������� � ����,
   * ���� ���������� - ���� �����. ��������! ���������� ������� - ���� ����� - ��� ���� ���������� � ����, ������
   * ������� � ������������ ���������� ����������� � ����! � ������ ���������������� ������ ������������/��������������
   * ���� �������� ����� �������������� ��� �������� ������ ������������� ��������� sql-�����.
  */
  private int                 dbmsConnNumber      = 0;
  /** ��������� ��������-�������� ��� ����������� ���� ������ ������ ������������/��������������. */
  private DBProcessingMonitor monitor             = null;
  /**
   * ���������� ����������� �������� ��� �������� �� �������/������� ������, �� ���������� �������� (� �������
   * ��� ��������) ����� ������ ���������� ���������� ��������-�������� � �������� ���������� ��������� [DEBUG] � ���.
   */
  private int                 operationsCount     = 0;
  /**
   * ���������� ��������� ��� ��������� ��������. ��������� ����� ��������� ����� ���������� ��������
   * (��������� �������� ����� ����� ���: [[�������] + [��������� ��������]])
  */
  private String              monitorMsgPrefix    = null;
  /** ��������/��������� �������������� ���������� sql-�����. */
  private boolean             isMultiThreads      = false;
  /** ������������� ���������� sql-��������. �� ��������� - ��������. */
  private boolean             useSqlFilter        = true;

  public DBConfig getDbConfig() {
   return dbConfig;
  }

  public void setDbConfig(DBConfig dbConfig) {
   this.dbConfig = dbConfig;
  }

  public int getDbmsConnNumber() {
   return dbmsConnNumber;
  }

  public void setDbmsConnNumber(int dbmsConnNumber) {
   this.dbmsConnNumber = dbmsConnNumber;
  }

  public DBProcessingMonitor getMonitor() {
   return monitor;
  }

  public void setMonitor(DBProcessingMonitor monitor) {
   this.monitor = monitor;
  }

  public int getOperationsCount() {
   return operationsCount;
  }

  public void setOperationsCount(int operationsCount) {
   this.operationsCount = operationsCount;
  }

  public String getMonitorMsgPrefix() {
   return monitorMsgPrefix;
  }

  public void setMonitorMsgPrefix(String monitorMsgPrefix) {
   this.monitorMsgPrefix = monitorMsgPrefix;
  }

  public boolean isMultiThreads() {
   return isMultiThreads;
  }

  public void setMultiThreads(boolean multiThreads) {
   isMultiThreads = multiThreads;
  }

  public boolean isUseSqlFilter() {
   return useSqlFilter;
  }

  public void setUseSqlFilter(boolean useSqlFilter) {
   this.useSqlFilter = useSqlFilter;
  }

  /**
   * ����� ��������� ��������� ����� ���������� �� ���������� �������. ��������� ����������� �� ������� &lt;db&gt;, ��
   * ��������� ������ sectionName. ���� ��� ������ �� ������� - ��������� �������� �� ������� &lt;db&gt;. ������ ������
   * �� ������ ���� �������� - �� ������ ���������� ������ ������� ������� (��������� ��� ROOT-�������). ��������
   * loadDBConfig ���������, ���� �� ��������� ������������ ��� ���������� � ���� �� ������� ������� ������� (������ -
   * ���������, ���� - ���). ��������� ��� ���������� � ���� ����� ��������� �� ���� �� ������� &lt;db&gt; � ��� ��
   * ������ sectionName.
   * @param fileName String xml-����, �� �������� ����� ��������� ������������.
   * @param sectionName String ��� ��������� ������ &lt;db&gt; � ������-�����, �� ������� ����� �������� ���������. �.�.
   * �������� �������� � ����� ������ ����� ������������ ���������� ������������ (��� ������� ������� ��������� (�����������)
   * � ������� &lt;db&gt;). ���� ������ �������� loadDBConfig=TRUE, �� �� ���� �� ��������� ����� ��������� ��������� ���
   * ���������� � ����.
   * @param loadDBConfig boolean �������� ���������, ���������� �� ������ �� ������� ��������� ��� ���������� � ����.
   * @throws java.io.IOException �� - ������� ������ ��� �����, ���� �� ���������� � �.�.
   * @throws org.apache.commons.configuration.ConfigurationException �� - ������ ��� �������� ������������ �� xml-�����.
   * @throws jdb.exceptions.DBModuleConfigException - ������ ����������������� ����� - �������� ��� ����, ��� ���� �����������
   * � �.�. ������ (��������� � �����������������).
  */
  public void loadFromFile(String fileName, String sectionName, boolean loadDBConfig)
   throws IOException, ConfigurationException, DBModuleConfigException
   {
    logger.debug("WORKING CommonModuleConfig.loadFromFile().");
    // ���� ��� ����� ����� - ������!
    if (StringUtils.isBlank(fileName))
     {throw new IOException("File name is blank!");}
    // ���� ���� �� ���������� ��� ��� �� ���� - ������!
    else if ((!new File(fileName).exists()) || (!new File(fileName).isFile()))
     {throw new IOException("File [" + fileName + "] doesn't exists or not a file!");}
    // ���� ������ �������� loadDBConfig=true, �� ��������� ������ ��� ���������� � ����. ������ �����������
    // �� ��� �� ��������� ������� <db> �������.
    if (loadDBConfig)
     {
      logger.debug("MODE: loading DB connection config from xml-file.");
      this.dbConfig = new DBConfig();
      this.dbConfig.loadFromFile(fileName, sectionName);
     }
    else {logger.debug("MODE: skipping DB connection config loading.");}

    // ����� xml-������������
    XMLConfiguration config = new XMLConfiguration(fileName);
    // ��������� (������) ������ �� �����
    config.load();
    // ��������� ������� ����� (���� ���� ��� ������, �� ��� ����������, ���� �� ���, �� ������ ������ �����
    // �� ������� (������) <db></db>)
    String prefix;
    if (!StringUtils.isBlank(sectionName)) {prefix = DBConsts.XML_DB_TAG + "." + sectionName + ".";}
    else                                   {prefix = DBConsts.XML_DB_TAG + ".";}
    // ������ �� ����� ������� ���������� ���������� dbmsConnNumber
    String dbmsConnNumberString = config.getString(prefix + DBConsts.XML_DBMS_CONN_NUMBER);
    try {this.dbmsConnNumber = Integer.valueOf(dbmsConnNumberString);}
    catch (NumberFormatException e) {logger.warn(e.getMessage() + ". Config file property [" + DBConsts.XML_DBMS_CONN_NUMBER + "]");}
    

    // ������ �� ������� ���������� �������� ��� ������ ��������� ��������
    String opsCounterString = config.getString(prefix + DBConsts.XML_OPERATIONS_COUNTER);
    try {this.operationsCount = Integer.valueOf(opsCounterString);}
    catch (NumberFormatException e) {logger.warn(e.getMessage() + ". Config file property [" + DBConsts.XML_OPERATIONS_COUNTER + "]");}
    // ������ ����� ���/���� ���������������
    this.isMultiThreads = Boolean.valueOf(config.getString(prefix + DBConsts.XML_MULTI_THREADS));
   }

  /**
   * ����� ��������� ��������� ����� ���������� �� ���������� �������. ��������� ����������� ������ �� ������� &lt;db&gt;.
   * ������ ������ �� ������ ���� �������� - �� ������ ���������� ������ ������� ������� (��������� ��� ROOT-�������).
   * �������� LoadDBConfig ���������, ���� �� ��������� ������������ ��� ���������� � ���� �� ������� ������� �������
   * (������ - ���������, ���� - ���). ��������� ��� ���������� � ���� ����� ��������� �� ���� �� ������� &lt;db&gt;.
   * @param fileName String xml-����, �� �������� ����� ��������� ������������.
   * @param loadDBConfig boolean �������� ���������, ���������� �� ������ �� ������� ��������� ��� ���������� � ����.
   * @throws java.io.IOException �� - ������� ������ ��� �����, ���� �� ���������� � �.�.
   * @throws org.apache.commons.configuration.ConfigurationException �� - ������ ��� �������� ������������ �� xml-�����.
   * @throws jdb.exceptions.DBModuleConfigException - ������ ����������������� ����� - �������� ��� ����, ��� ���� �����������
   * � �.�. ������ (��������� � �����������������).
  */
  public void loadFromFile(String fileName, boolean loadDBConfig)
   throws DBModuleConfigException, IOException, ConfigurationException
   {this.loadFromFile(fileName, null, loadDBConfig);}

  @Override
  public String getConfigErrors()
   {
    String result = null;
    String dbConfigErrors = DBUtils.getConfigErrors(dbConfig);
    if (!StringUtils.isBlank(dbConfigErrors))      {result = dbConfigErrors;}
    return result;
   }

  @Override
  public String toString() {
   return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
           append("dbConfig", dbConfig).
           append("dbmsConnNumber", dbmsConnNumber).
           append("monitor", monitor).
           append("operationsCount", operationsCount).
           append("monitorMsgPrefix", monitorMsgPrefix).
           append("isMultiThreads", isMultiThreads).
           toString();
  }

 public static void main(String[] args)
  {
   InitLogger.initLoggers(new String[] {"jdb", "jlib", "org.apache.commons"});
   Logger logger = Logger.getLogger("jdb");

    try
     {
      CommonModuleConfig config = new CommonModuleConfig();
      config.loadFromFile("stormConn.xml", null, false);
      logger.info("->" + config);
     }
    catch (IOException e) {logger.error(e.getMessage());}
    catch (ConfigurationException e) {logger.error(e.getMessage());}
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
   }

 }