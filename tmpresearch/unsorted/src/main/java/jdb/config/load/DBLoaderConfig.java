package jdb.config.load;

import jdb.DBConsts;
import jdb.config.common.CommonModuleConfig;
import jdb.config.common.ConfigInterface;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.integrity.DBIntegrityModel;
import jdb.model.time.DBTimedModel;
import jdb.utils.DBUtils;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

/**
 * ������ ������������ ��� ������ ������� ������������/�������������� ������. ������ ������ ��� ����, ����� ������
 * ������������/�������������� �� ���� ��������������������� (������ ������ ������� ��������� ���� ����������).
 * @author Gusev Dmitry (019gus)
 * @version 7.0 (DATE: 11.11.2010)
 *
 * @deprecated ������ ����� �� ������������� ������������, �.�. ��� ��������/�������� �� �� ���� ������������ �����
 * {@link jdb.nextGen.DBasesLoader DBasesLoader} ������ ������ {@link jdb.processing.loading.DBLoader}, ������� �
 * ����������� ������ ������ ��� ����� ������.
*/

public class DBLoaderConfig extends CommonModuleConfig implements ConfigInterface
 {
  /** ���������-������ ������� ������. */
  private Logger logger = Logger.getLogger(getClass().getName());

  /**
   * ���� � �������� � ��������������� �� ��� �������� (��� ������ ��������� � ���� ��������� ������������ ���
   * �������������� - ��� ������ ������ � ��������� ��� ���� � �������� ����� �������, ��� ������ ������ � �� - ��� ����
   * � �������� ��). ����� ����������� ���������� ������� ���� � ����������� �� ���������� ������ (������������/��������������) -
   * ��� ������� ������������ - � ���� ������� ����� ������������� �������� ������, ��� ������� �������������� - � ����
   * �������� ����� (������ ������) ��������������� ������.
  */
  private String           path                = null;
  /** ������� �������� ����� ��� ���. ������������ ������ �������� ��������������. �� ��������� �������� ����� �� ���������. */
  private boolean          isDeleteSource      = false;
  /** ������ ����������� ��. ������������ ��� ������������ ������ �� ��. */
  private DBIntegrityModel dbIntegrityModel    = null;
  /** ������ �� � ��������� �������. ������������ ��� ������������ ������ �� ��. */
  private DBTimedModel     dbTimedModel        = null;
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
   * ������ �������� ������ ������ �� ������������ ��.
   * ���� ��� ���������� ������������ �������� �� ������� (��� <= 0), �� ������������ �������� �� ��������� - ��. ���������
   * SERIALIZATION_TABLE_FRACTION � ������ DBConsts.
  */
  private int              serializeFraction   = 0;
  /**
   * �������� ���������, ������������ ��� ���������������� �������� ������ ������ ��� ��������� ������ (������� � ����
   * ����������� ��� �������� ������ � ������� sql-���� ��� sql-�����). ��� ������������� ������� ������� ��� ����� ���
   * ����� ������� ���������� � ���� ������� sql-������(����) � ����� ���� ������ �����������. ����������� ������� ������
   * �������� ���������� � ������ ��������� ������ (��� ������� ������� ������ ����� ��������� �� OutOfMemory). ���
   * ������������� ���������� ������� ������ ���� ������������� � ������ � ����� �� �����������. ���������� � ������ ��
   * ����� ���������. ������������������ � ����� ������� �������� ���������. ������������ � ������������� - ����� ����������
   * �������, ���� ��� ������ ������ ����� ������ ���� ������������.
  */
  private boolean          useFullScript       = false;
  /**
   * ������������ ��������� ����, �� ��������� �������� ������������ �������� � ����� ������ � ������� ��. ���� ������
   * ���� ����� �������� ��������, �� ������ �������� ������ ����� ��������� ������ � �������, ���� ����������� ������
   * �� ������ �� �����.
  */
  private String           keyFieldName        = DBConsts.FIELD_NAME_KEY;
  /** ������� �� ������� �� ����� ��������� � ��� ������ � �����. �� ��������� - ���������. */
  private boolean          isClearTableBeforeLoad = false;
  /**
   * ����� ������������ ������ ��� ���������� � ���� MSSQL. ���� ��������, �� ����� ��������� ������ � �� � ����� (���
   * ���� MS SQL Server) ����� ��������� ���������� "SET IDENTITY_INSERT [TABLENAME] ON", ������� ��������� ���������
   * �������� � ������� ���������� ����� � ���������������. ����� ���������� �������� ������ � ������� ����� ���������
   * ���������� "SET IDENTITY_INSERT [TABLENAME] OFF". ���� �� ������ ����� ���������, �� ��������� ���������� �����������
   * �� �����.
  */
  private boolean          useSetIdentityInsert   = true;

  /** ����������� �� ���������. */
  public DBLoaderConfig() {}

  /**
   * ����������� ������� ��������� ������� ������ �� ������ ��� ������������� (���� �� �� �������� ������).
   * @param config DBLoaderConfig �����, �� ������ ������ �������� ��������� ������ ���������.
  */
  public DBLoaderConfig(DBLoaderConfig config)
   {
    String configErrors = DBUtils.getConfigErrors(config);
    // ���� � ��������� �������� ��� � ������� - ��������
    if (StringUtils.isBlank(configErrors))
     {
      this.setDbConfig(config.getDbConfig()); // ��� ���������� ������� ����� ������ �� ���� ��������� ������ DBConfig!
      this.setMonitor(config.getMonitor());   // ��� ���������� ������� ����� ������ �� ���� ��������� ������ DBProcessingMonitor
      this.setMonitorMsgPrefix(config.getMonitorMsgPrefix());
      this.setOperationsCount(config.getOperationsCount());
      this.setDbmsConnNumber(config.getDbmsConnNumber());
      this.setMultiThreads(config.isMultiThreads());
      this.serializeFraction = config.getSerializeFraction();
      this.dbIntegrityModel  = config.getDbIntegrityModel(); // ��� ���������� ������� ����� ������ �� ���� ��������� ������ ������!
      this.dbTimedModel      = config.getDbTimedModel(); // ��� ���������� ������� ����� ������ �� ���� ��������� ������ ������!
      this.isDeleteSource    = config.isDeleteSource();
      this.path              = config.getPath();
      this.useFullScript     = config.isUseFullScript();
      this.isClearTableBeforeLoad = config.isClearTableBeforeLoad();
      this.useSetIdentityInsert   = config.isUseSetIdentityInsert();
     }
    // ���� ��������� ������ �������� - �������� � ���
    else {logger.error("BatchConfig() constructor: can't get data from SerializationConfig! Reason: " + configErrors);}
   }

  public String getPath() {
   return path;
  }

  public void setPath(String path) {
   this.path = path;
  }

  public boolean isDeleteSource() {
   return isDeleteSource;
  }

  public void setDeleteSource(boolean deleteSource) {
   isDeleteSource = deleteSource;
  }

  public DBIntegrityModel getDbIntegrityModel() {
   return dbIntegrityModel;
  }

  public void setDbIntegrityModel(DBIntegrityModel dbIntegrityModel) {
   this.dbIntegrityModel = dbIntegrityModel;
  }

  public DBTimedModel getDbTimedModel() {
   return dbTimedModel;
  }

  public void setDbTimedModel(DBTimedModel dbTimedModel) {
   this.dbTimedModel = dbTimedModel;
  }

  public int getSerializeFraction() {
   return serializeFraction;
  }

  public void setSerializeFraction(int serializeFraction) {
   this.serializeFraction = serializeFraction;
  }

  public boolean isUseFullScript() {
   return useFullScript;
  }

  public void setUseFullScript(boolean useFullScript) {
   this.useFullScript = useFullScript;
  }

  public String getKeyFieldName() {
   return keyFieldName;
  }

  public void setKeyFieldName(String keyFieldName) {
   this.keyFieldName = keyFieldName;
  }

  public boolean isClearTableBeforeLoad() {
   return isClearTableBeforeLoad;
  }

  public void setClearTableBeforeLoad(boolean clearTableBeforeLoad) {
   isClearTableBeforeLoad = clearTableBeforeLoad;
  }

  public boolean isUseSetIdentityInsert() {
   return useSetIdentityInsert;
  }

  public void setUseSetIdentityInsert(boolean useSetIdentityInsert) {
   this.useSetIdentityInsert = useSetIdentityInsert;
  }

  /**
   * ����� ���������� �������� ������/���� � ����������� �� ����, "���������" �� ��������� ������� ��� ����������� �
   * ����, ������ �� ������� �������� � ����� �� ����� ������� ������ (������ �������� getDBConfig() � setDBConfig()).
   * ������ ����� ��� �������� ���������� ����������� ����� ������ DBConfig.
   * @param tableName String ��� ����������� �� ���������� �������.
   * @return boolean ������/���� � ����������� �� ����, "���������" �� ��������� ������� ��� ������� ����������� � ����.
   * @throws DBModuleConfigException �� - ������ ���������������� ���������� � ����, ��� �������� ����������� ����������
   * ������� (� ������ ������ - ������ ����� ������� ������ ��� �� ������ ������ ��� ���� (��������� ��� �������� �������
   * �� ���������� �������� ���������� ���� ����)).
  */
  public boolean isTableAllowed(String tableName) throws DBModuleConfigException
   {
    boolean result;
    String configErrors = DBUtils.getConfigErrors(this.getDbConfig());
    if (StringUtils.isBlank(configErrors)) {result = this.getDbConfig().isTableAllowed(tableName);}
    else {throw new DBModuleConfigException(configErrors);}
    return result;
   }

  /**
   * ����� ��������� ���� ������� � ������ "�����������" ������. ������� ����������� ������ ���� �������
   * �������� ���.
   * @param tableName String ��� ����������� �������.
   * @throws DBModuleConfigException ������ ������������ ������� ������ (������ ����� ����� ���� DBConfig)
  */
  public void addAllowedTable(String tableName) throws DBModuleConfigException
   {
    String configErrors = DBUtils.getConfigErrors(this.getDbConfig());
    if (StringUtils.isBlank(configErrors)) {this.getDbConfig().addAllowedTable(tableName);}
    else {throw new DBModuleConfigException(configErrors);}
   }

  /**
   * ������ ����� ��������� ���� ������� � ������ "�����������" ������. ������� �����������, ������ ���� ���������
   * ��� ������� �� ������.
   * @param tableName String ��� ����������� �������.
   * @throws DBModuleConfigException ������ ������������ ������� ������ (������ ����� ����� ���� DBConfig)
  */
  public void addDeprecatedTable(String tableName) throws DBModuleConfigException
   {
    String configErrors = DBUtils.getConfigErrors(this.getDbConfig());
    if (StringUtils.isBlank(configErrors)) {this.getDbConfig().addDeprecatedTable(tableName);}
    else {throw new DBModuleConfigException(configErrors);}
   }

  /**
   * ����� ������ "�����������" ������.
   * @throws DBModuleConfigException ������ ������������ ������� ������ (������ ����� ����� ���� DBConfig)
  */
  public void resetAllowedTables() throws DBModuleConfigException
   {
    String configErrors = DBUtils.getConfigErrors(this.getDbConfig());
    if (StringUtils.isBlank(configErrors)) {this.getDbConfig().resetAllowedTables();}
    else {throw new DBModuleConfigException(configErrors);}
   }

  /**
   * ����� ������ "�����������" ������.
   * @throws DBModuleConfigException ������ ������������ ������� ������ (������ ����� ����� ���� DBConfig)
  */
  public void resetDeprecatedTables() throws DBModuleConfigException
   {
    String configErrors = DBUtils.getConfigErrors(this.getDbConfig());
    if (StringUtils.isBlank(configErrors)) {this.getDbConfig().resetDeprecatedTables();}
    else {throw new DBModuleConfigException(configErrors);}
   }

  /**
   * ����� ���� ����������� (������� "�����������" � "�����������" ������).
   * @throws DBModuleConfigException ������ ������������ ������� ������ (������ ����� ����� ���� DBConfig) 
  */
  public void resetConstraints() throws DBModuleConfigException
   {
    String configErrors = DBUtils.getConfigErrors(this.getDbConfig());
    if (StringUtils.isBlank(configErrors)) {this.getDbConfig().resetConstraints();}
    else {throw new DBModuleConfigException(configErrors);}
   }

  /**
   * ����� ���������� �������� ������ ������������ ���������� ������. ���� ������ ������������ ���, �� ����� ������ NULL.
   * @return String �������� ������ ������������ ��� NULL.
  */
  @Override
  public String getConfigErrors()
   {
    String result = null;
    String errors = super.getConfigErrors();
    if (!StringUtils.isBlank(errors))   {result = errors;}
    else if (StringUtils.isBlank(path)) {result = "Path to data catalog is empty (or null)!";}
    return result;
   }

  @Override
  public String toString()
   {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
            appendSuper(super.toString()).
            append("path", path).
            append("isDeleteSource", isDeleteSource).
            append("dbIntegrityModel", dbIntegrityModel).
            append("dbTimedModel", dbTimedModel).
            toString();
   }

  /**
   * ����� ������ ��� ������������ ������� ������.
   * @param args String[] ��������� ������.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger("jdb");
    Logger logger = Logger.getLogger("jdb");
    DBLoaderConfig config = new DBLoaderConfig();
    logger.debug(config.toString());
   }

 }