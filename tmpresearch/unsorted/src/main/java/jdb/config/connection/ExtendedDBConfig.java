package jdb.config.connection;

import jdb.DBConsts;
import jdb.exceptions.DBModuleConfigException;
import jlib.auth.Password;
import jlib.exceptions.EmptyObjectException;
import jlib.exceptions.EmptyPassException;
import jlib.logging.InitLogger;
import jlib.utils.FSUtils;
import jlib.utils.common.Utils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * ������ ����� ��������� ���������������� �������� ������ ������������ ���������� � ���� ����� JDBC-������� (������
 * BaseJdbcConfig). � ������ ������ ����������� ������ ��������/�������� ������������ ��/� ����(�) �� �����.
 * ������ ����� ������� ������������ ������ ������ ConnectionConfig.
 * ������ ����������������� ����� ���������:<br><br>
 *
 * <b>
 * &nbsp;&lt;?xml version = '1.0'?&gt;<br>
 * &nbsp;&nbsp;&lt;[ROOT_ELEMENT]&gt;<br>
 *  &nbsp;&nbsp;&nbsp;&lt;db&gt;<br>
 *   &nbsp;&nbsp;&nbsp;&nbsp;&lt;[OPTIONAL_SECTION_NAME]&gt;<br>
 *
 *    <br><i>{1}</i><br>
 *    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dataSource&gt;[data_source_name]&lt;/dataSource&gt;<br>
 *
 *    <br><i>{2}</i><br>
 *    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;type&gt;[mysql|dbf|informix|odbc|derby_embedd|derby_server]&lt;/type&gt;<br>
 *    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;host&gt;[server_host]:[server_port]&lt;/host&gt;<br>
 *    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;server&gt;[db_server_name]&lt;/server&gt;<br>
 *    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dbname&gt;[db_name]&lt;/dbname&gt;<br>
 *    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;user&gt;[user]&lt;/user&gt;<br>
 *    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;pwd&gt;[password]&lt;/pwd&gt;<br>
 *    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;params&gt;[name1=value1;...;nameN=valueN]&lt;/params&gt;<br>
 *    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;info&gt;[name1=value1;...;nameN=valueN]&lt;/info&gt;<br> 
 *
 *    <br>
 *   &nbsp;&nbsp;&nbsp;&nbsp;&lt;/[OPTIONAL_SECTION_NAME]&gt;<br>
 *  &nbsp;&nbsp;&nbsp;&lt;/db&gt;<br>
 * &nbsp;&nbsp;&lt;/[ROOT_ELEMENT]&gt;<br><br>
 * </b><br>
 *
 * <br>- [ROOT_ELEMENT] - ����� ���� �����, �� ��������� ��� ���������� ������� ������������ �������� ���������
 * jdb.DBConsts.XML_DB_ROOT_TAG {@linkplain jdb.DBConsts#XML_DB_ROOT_TAG (������ ��������� XML_DB_ROOT_TAG)}.
 * <b>[ROOT_ELEMENT] ����������� ������ ����, ���� ��� ��� (� ������� &lt;db&gt; �������� ��������), �� ���� �� �����
 * ��������.</b>
 * <br><b>������ ������� ������� ����������� ������ ��������������, � ��������� ������
 * ����� �������� �� ������� ���������� ������ (��� ������), �� ������ ��������� �� �����.</b>
 * <br>- [OPTIONAL_SECTION_NAME] - ������������ ������, ��� ����� ��������������, ����� �������������, ���������
 * ������� ��������� ���������� ���������� � ����� ������ ������-�����. ���� ������ ����, �� ��� ������ �� ��� ����������
 * � ������ �������� ������ (��� �����. ������������) ���������� ��������� �� ���.
 * <br>- <b><i>{1}</i></b>-<b><i>{2}</i></b> - ����������� ���� ������ 1, ���� ������ 2. ������ 1 - ������������ ��� ����������
 * � �� ����� ����� ��������� ������ (DataSource) � ������ JNDI. ������ 2 - ������������ ��� ���������� � �� �������� �����
 * JDBC-�������. ��� �������� � ������� ����� ����� ������ ��������� ����� ������ 1 - �� ��������� ����� �������, � ���������
 * ������ 2 ����� ���������������. 
 * <br>- ��������� ��������� ������ �� ��������� � ������ UniversalConfig.
 * <br>- �������� �������������� ���������� ������ [OPTIONAL_SECTION_NAME] - ��� ������ �� ��� ���������� ���������� �����
 * ��������� ��� ������. ����� ��� ���� ����� �������� � �������� � ������ [ROOT_ELEMENT] - ��� �� ������ �������� �����
 * ������ �� ���������.
 *
 * @author Gusev Dmitry (019gus)
 * @version 3.1 (DATE: 21.02.2011)
 * @see jdb.DBConsts#XML_DB_ROOT_TAG XML_DB_ROOT_TAG
*/

public class ExtendedDBConfig extends BaseDBConfig
 {
  /** ���������-������ ������� ������. */
  private Logger logger = Logger.getLogger(getClass().getName());

  public String   getStringPassword()
   {
    String pass;
    if (this.getPassword() != null) {pass = this.getPassword().getPassword();}
    else                            {pass = null;}
    return pass;
   }
  
  public void setPassword(String password)
   {
    try {this.setPassword(new Password(password));}
    catch (EmptyPassException e) {logger.error(e.getMessage());}
   }

  /** ����������� �� ��������� - ������� ������ ��������� ������� ������ � ������ ������ �� ������. */
  public ExtendedDBConfig() {}

  /**
   * ����������� ��������� ������ �� ���������� ����������������� xml-�����.
   * @param fileName String xml-����, �� �������� ����� ��������� ������������.
   * @param sectionName String ��� ��������� ������ &lt;db&gt; � ������-�����, �� ������� ����� �������� ���������.
   * @param usePlainPass boolean ������������ �� �������� ������ � �� ��� ���. ���� ������������ �������� ������, ��
   * � �����. ���� ������� ����� ������ ������, ���� �� �������� ������ �� ����������, �� � �����. ���� ����� ������
   * ����, � ������� ��������� ������ (���� ������������������� �������).
   * @throws java.io.IOException �� - ������� ������ ��� �����, ���� �� ���������� � �.�.
   * @throws org.apache.commons.configuration.ConfigurationException �� - ������ ��� �������� ������������ �� xml-�����.
   * @throws jdb.exceptions.DBModuleConfigException - ������ ����������������� ����� - �������� ��� ����, ��� ���� �����������
   * � �.�. ������ (��������� � �����������������).
  */
  public ExtendedDBConfig(String fileName, String sectionName, boolean usePlainPass)
   throws DBModuleConfigException, IOException, ConfigurationException
   {this.loadFromFile(fileName, sectionName, usePlainPass);}

  /**
   * ����������� ��������� ������ �� ����������������� xml-�����. � ������������ �� ���� � ������� �������� ���
   * ����� ������ ��� �������� ������ - ������ ���������������� ������� �� �������� ��������� DBConfigConsts.USE_PLAIN_PASS.
   * @param fileName String xml-����, �� �������� ����� ��������� ������������.
   * @param sectionName String ��� ��������� ������ &lt;db&gt; � ������-�����, �� ������� ����� �������� ���������.
   * @throws java.io.IOException �� - ������� ������ ��� �����, ���� �� ���������� � �.�.
   * @throws org.apache.commons.configuration.ConfigurationException �� - ������ ��� �������� ������������ �� xml-�����.
   * @throws DBModuleConfigException �� - ������ ��� �������� ������������ �� xml-�����.
  */
  public ExtendedDBConfig(String fileName, String sectionName)
   throws DBModuleConfigException, IOException, ConfigurationException
   {this.loadFromFile(fileName, sectionName);}

  /**
   * ����������� ��������� ������ �� ����������������� xml-�����. ��������� ������ ������� �������� �� �����
   * �������� &lt;db&gt;.
   * @param fileName String xml-����, �� �������� ����� ��������� ������������.
   * @param usePlainPass boolean ������������ �� �������� ������ � �� ��� ���. ���� ������������ �������� ������, ��
   * � �����. ���� ������� ����� ������ ������, ���� �� �������� ������ �� ����������, �� � �����. ���� ����� ������
   * ����, � ������� ��������� ������ (���� ������������������� �������).
   * @throws java.io.IOException �� - ������� ������ ��� �����, ���� �� ���������� � �.�.
   * @throws org.apache.commons.configuration.ConfigurationException �� - ������ ��� �������� ������������ �� xml-�����.
   * @throws jdb.exceptions.DBModuleConfigException - ������ ����������������� ����� - �������� ��� ����, ��� ���� �����������
   * � �.�. ������ (��������� � �����������������).
  */
  public ExtendedDBConfig(String fileName, boolean usePlainPass) throws DBModuleConfigException, IOException, ConfigurationException
   {this.loadFromFile(fileName, usePlainPass);}

  /**
   * ����������� ��������� ������ �� ����������������� xml-�����. � ������������ �� ���� � ������� �������� ���
   * ����� ������ ��� �������� ������ - ������ ���������������� ������� �� �������� ��������� DBConfigConsts.USE_PLAIN_PASS.
   * ��������� ������ ������������� �������� �� �������� &lt;db&gt;.
   * @param fileName String xml-����, �� �������� ����� ��������� ������������.
   * @throws java.io.IOException �� - ������� ������ ��� �����, ���� �� ���������� � �.�.
   * @throws org.apache.commons.configuration.ConfigurationException �� - ������ ��� �������� ������������ �� xml-�����.
   * @throws jdb.exceptions.DBModuleConfigException - ������ ����������������� ����� - �������� ��� ����, ��� ���� �����������
   * � �.�. ������ (��������� � �����������������).
  */
  public ExtendedDBConfig(String fileName) throws DBModuleConfigException, IOException, ConfigurationException
   {this.loadFromFile(fileName);}

  /**
   * ����� ��������� ������ �� ���������� ����������������� xml-����� �� ������� &lt;db&gt;. � ����� ����������� ������
   * ���� �������� (ROOT) ������� ��� �������� &lt;db&gt;. �������� ������� ����� ���� �����. ������ (����) ����� ����������
   * ��� � ������ &lt;db&gt;, ��� � � ����� ��������� ��������� ������ (��� ��������� ���������� ���������� sectionName).
   * @param fileName String xml-����, �� �������� ����� ��������� ������������.
   * @param sectionName String ��� ��������� ������ &lt;db&gt; � ������-�����, �� ������� ����� �������� ���������. �.�.
   * �������� �������� � ����� ������ ����� ������������ ���������� ������������ ���������� � ���� (��� ������� �������
   * ��������� (�����������) � ������� &lt;db&gt;, ��������: db1, db2, ... , dbN). ��������� ��. javadoc ������.
   * @param usePlainPass boolean ������������ �� �������� ������ � �� ��� ���. ���� ������������ �������� ������, ��
   * � �����. ���� ������� ����� ������ ������, ���� �� �������� ������ �� ����������, �� � �����. ���� ����� ������
   * ����, � ������� ��������� ������ (���� ������������������� �������).
   * @throws java.io.IOException �� - ������� ������ ��� �����, ���� �� ���������� � �.�.
   * @throws org.apache.commons.configuration.ConfigurationException �� - ������ ��� �������� ������������ �� xml-�����.
   * @throws jdb.exceptions.DBModuleConfigException - ������ ����������������� ����� - �������� ��� ����, ��� ���� �����������
   * � �.�. ������ (��������� � �����������������).
  */
  public void loadFromFile(String fileName, String sectionName, boolean usePlainPass)
   throws IOException, ConfigurationException, DBModuleConfigException
   {
    logger.debug("WORKING ExtendedDBConfig.loadFromFile().");
    // ���� ��� ����� ����� - ������!
    if (StringUtils.isBlank(fileName))
     {throw new IOException("File name is blank!");}
    // ���� ���� �� ���������� ��� ��� �� ���� - ������! � ������ ������� � ��������� � ������ ��� �����.
    else if ((!new File(fileName).exists()) || (!new File(fileName).isFile()))
     {throw new IOException("Specified file [" + fileName + "] doesn't exists or not a file! " +
                            "Absolute file path [" + FSUtils.fixFPath(new File(fileName).getAbsolutePath()) + "].");}

    // ����� xml-������������
    XMLConfiguration config = new XMLConfiguration(fileName);
    // ��������� (������) ������ �� �����
    logger.debug("Reading XML file...");
    config.load();
    // ��������� ������� ����� (���� ���� ��� ������, �� ��� ����������, ���� �� ���, �� ������ ������ �����
    // �� ������� (������) <db></db>)
    String prefix;
    if (!StringUtils.isBlank(sectionName)) {prefix = DBConsts.XML_DB_TAG + "." + sectionName + ".";}
    else                                   {prefix = DBConsts.XML_DB_TAG + ".";}

    // ������ �� ����� ��� ��������� ������
    String dataSource = config.getString(prefix + DBConsts.DB_DATA_SOURCE);
    // ���� ��� ��������� ������ ������� - ����� ���� �� ������ - ������������� ��������!
    if (!StringUtils.isBlank(dataSource))
     {
      logger.debug("JNDI data source name specifyed [" + dataSource + "]! Using it.");
      this.setDataSource(dataSource);
     }
    // ���� �� ��� ��������� ������ �� ������� - ������ �� ����� ��������� ��� ���������� � �� ����� JDBC
    else
     {
      logger.debug("JNDI data source name not specifyed! Processing JDBC parameters.");
      // ----- ��� ���� (������ �� �������)
      String strDbType = config.getString(prefix + DBConsts.DB_TYPE);
      // ���� ����������� �������� �� ����� - ������������
      if (!StringUtils.isBlank(strDbType))
       {
        // ����������� ����������� �������� � ������ ������ DBType
        try {this.setDbType(DBConsts.DBType.valueOf(strDbType.toUpperCase()));}
        // ���� �������� �� ����� (��� ������ � ������-������������) - ��!
        catch (IllegalArgumentException e) {throw new DBModuleConfigException("Invalid DBMS type! [" + e.getMessage() + "]");}
       }
      // ���� �� �������� ����� - ������! (��� ���� �� ����� ���� ����)
      else {throw new DBModuleConfigException("DBMS type and JNDI data source name are empty !");}
      // ----- ���� ����
      this.setHost(config.getString(prefix + DBConsts.DB_HOST));
      // ----- ��� ������� ����
      this.setServerName(config.getString(prefix + DBConsts.DB_SERVER));
      // ----- ��� ��
      this.setDbName(config.getString(prefix + DBConsts.DB_NAME));
      // ----- ������������ ����
      this.setUser(config.getString(prefix + DBConsts.DB_USER));
      // ----- ������ ������������
      String strPass  = config.getString(prefix + DBConsts.DB_PWD);
      // ���� ������ �� ���� - ������������ ���
      if (!StringUtils.isBlank(strPass))
       {
        // ���� ���������� ���� "������� ������", �� � ������ ���� ��������� ������ ������
        if (usePlainPass)
         {logger.info("This database connection config use plain password!"); this.setPassword((strPass));}
        // ���� �� ���� "������� ������" ������� (false), �� � ������ ���� ��������� ��� ����� �
        // ����������� ������� (��� ��� ���������� ����)
        else
         {
          logger.debug("Usig password file [" + strPass + "].");
          try {this.setPassword((Password) FSUtils.deserializeObject(strPass, false));}
          catch (ClassNotFoundException e) {logger.error("Can't read password file! Reason: " + e.getMessage());}
          catch (ClassCastException e)     {logger.error("Can't read password file! Reason: " + e.getMessage());}
         }
       }
      // ���� �� ����������� ������ ���� - ������� �� ����
      else {logger.warn("This config [" + fileName + "] has an empty password!");}
      // ----- �������������� ��������� ���������� (����������� name1=value1;name2=value2;...)
      this.setConnParams(StringUtils.trimToNull(config.getString(prefix + DBConsts.DB_CONN_PARAMS)));
      // ----- �������������� ���������� ��� ���������� (����������� name1=value1;name2=value2;...)
      this.setConnInfo(Utils.getPropsFromString(config.getString(prefix + DBConsts.DB_CONN_INFO)));
     }
    
   }

  /**
   * ����� ��������� ������ �� ����������������� xml-�����. � ������ ������ �� ��������� �� ���� � ������� �������� ���
   * ����� ������ ��� �������� ������ - ������ ���������������� ������� �� �������� ��������� DBConfigConsts.USE_PLAIN_PASS. 
   * @param fileName String xml-����, �� �������� ����� ��������� ������������.
   * @param sectionName String ��� ��������� ������ &lt;db&gt; � ������-�����, �� ������� ����� �������� ���������.
   * @throws java.io.IOException �� - ������� ������ ��� �����, ���� �� ���������� � �.�.
   * @throws org.apache.commons.configuration.ConfigurationException �� - ������ ��� �������� ������������ �� xml-�����.
   * @throws jdb.exceptions.DBModuleConfigException - ������ ����������������� ����� - �������� ��� ����, ��� ���� �����������
   * � �.�. ������ (��������� � �����������������).
  */
  public void loadFromFile(String fileName, String sectionName)
   throws IOException, DBModuleConfigException, ConfigurationException
   {this.loadFromFile(fileName, sectionName, DBConsts.XML_USE_PLAIN_PASS);}

  /**
   * ����� ��������� ������ �� ����������������� xml-�����. ��������� ������ ������� �������� �� ����� �������� &lt;db&gt;.
   * @param fileName String xml-����, �� �������� ����� ��������� ������������.
   * @param usePlainPass boolean ������������ �� �������� ������ � �� ��� ���. ���� ������������ �������� ������, ��
   * � �����. ���� ������� ����� ������ ������, ���� �� �������� ������ �� ����������, �� � �����. ���� ����� ������
   * ����, � ������� ��������� ������ (���� ������������������� �������).
   * @throws java.io.IOException �� - ������� ������ ��� �����, ���� �� ���������� � �.�.
   * @throws org.apache.commons.configuration.ConfigurationException �� - ������ ��� �������� ������������ �� xml-�����.
   * @throws jdb.exceptions.DBModuleConfigException - ������ ����������������� ����� - �������� ��� ����, ��� ���� �����������
   * � �.�. ������ (��������� � �����������������).
  */
  public void loadFromFile(String fileName, boolean usePlainPass)
   throws DBModuleConfigException, IOException, ConfigurationException
   {this.loadFromFile(fileName, null, usePlainPass);}

  /**
   * ����� ��������� ������ �� ����������������� xml-�����. � ������ ������ �� ��������� �� ���� � ������� �������� ���
   * ����� ������ ��� �������� ������ - ������ ���������������� ������� �� �������� ��������� DBConfigConsts.USE_PLAIN_PASS.
   * ��������� ������ ������� �������� �� �������� &lt;db&gt;.
   * @param fileName String xml-����, �� �������� ����� ��������� ������������.
   * @throws java.io.IOException �� - ������� ������ ��� �����, ���� �� ���������� � �.�.
   * @throws org.apache.commons.configuration.ConfigurationException �� - ������ ��� �������� ������������ �� xml-�����.
   * @throws jdb.exceptions.DBModuleConfigException - ������ ����������������� ����� - �������� ��� ����, ��� ���� �����������
   * � �.�. ������ (��������� � �����������������).
  */
  public void loadFromFile(String fileName)
   throws DBModuleConfigException, IOException, ConfigurationException
   {this.loadFromFile(fileName, DBConsts.XML_USE_PLAIN_PASS);}

  /**
   * ����� ������� � ���������� ������ XMLConfiguration ��� ����, ����� ����� ���� ��������� ���������� �������
   * ������ � ���� ��� ���������� � ������� �������������� (� ����� ��������� � ����).
   * @param rootName String ��� ��������� (root) �������� ��� ������������ ����� ������������.
   * @param sectionName String ��� ��������� ������ &lt;db&gt; � ������-�����, � ������� ����� �������� ���������.
   * @return XMLConfiguration ��������� ������ �� ��������� ��������� ������� ���������� ������.
  */
  private XMLConfiguration getXMLConfig(String rootName, String sectionName)
   {
    logger.debug("getXMLConfig(): generating XMLConfiguration object.");
    // ������� ��������� ������ XML-������������
    XMLConfiguration config = new XMLConfiguration();
    // �������� ������� (�������� � ����������� �� ��������� rootName). ���� �������� ������ ������ - �����
    // ���, ���� �� �� ������ - �������� ������ �� ��������� ����������.
    if (!StringUtils.isBlank(rootName)) {config.setRootElementName(rootName);}
    else                                {config.setRootElementName(DBConsts.XML_DB_ROOT_TAG);}
    // ��������� �������
    String prefix;
    if (!StringUtils.isBlank(sectionName)) {prefix = DBConsts.XML_DB_TAG + "." + sectionName + ".";}
    else                                   {prefix = DBConsts.XML_DB_TAG + ".";}
    
    // ��������� ������. ���� � ������� ������� ��� ��������� ������ (���� dataSource), �� ��������� ������ ���.
    if (!StringUtils.isBlank(this.getDataSource()))
     {
      logger.debug("Adding JNDI data source name to XML config object.");
      config.addProperty(prefix + DBConsts.DB_DATA_SOURCE, this.getDataSource());
     }
    // ���� �� ���� "��� ��������� ������" �����, �� ��������� ��������� ��� ���������� ����� JDBC-�������.
    else
     {
      logger.debug("Adding JDBC parameters to XML config object.");
      // ��������������� ��������� ���������
      config.addProperty(prefix + DBConsts.DB_TYPE,        this.getDbType());
      config.addProperty(prefix + DBConsts.DB_HOST,        this.getHost());
      config.addProperty(prefix + DBConsts.DB_SERVER,      this.getServerName());
      config.addProperty(prefix + DBConsts.DB_NAME,        this.getDbName());
      config.addProperty(prefix + DBConsts.DB_USER,        this.getUser());
      // ��� ���������� ������ �� ������ ����������� � ���� (���� ������ �� ����)
      if (this.getPassword() != null)
       {
        logger.debug("Password is not empty. Processing.");
        try
         {
          String passFilePath = FSUtils.serializeObject(this.getPassword(), SystemUtils.getUserDir().getAbsolutePath());
          String passFileName = passFilePath.substring(passFilePath.lastIndexOf("/") + 1);
          config.addProperty(prefix + DBConsts.DB_PWD, passFileName);
         }
        catch (EmptyObjectException e) {logger.error("Can't save password file! Reason: " + e.getMessage());}
        catch (IOException e)          {logger.error("Can't save password file! Reason: " + e.getMessage());}
       }
      // ���� ������ ���� - ������� �� ����
      else {logger.warn("Empty password! Can't process it.");}
      // �������������� ��������� ����������
      config.addProperty(prefix + DBConsts.DB_CONN_PARAMS, this.getConnParams());
      config.addProperty(prefix + DBConsts.DB_CONN_INFO,   Utils.getStringFromProps(this.getConnInfo()));
     }
    // ���������� ���������
    return config;
   }
  
  /**
   * ����� ��������� ������� ������������ ��� ���������� (��������� ���������� ������) � ���� � ���� � ���������
   * ������ fileName.
   * @param fileName String ��� �����, � ������� ����� ��������� ������� ��������� ���������� (��������� ������).
   * @param rootName String ��� ��������� (root) �������� ��� ������������ ����� ������������.
   * @param sectionName String ��� ��������� ������ &lt;db&gt; � ������-�����, � ������� ����� �������� ���������.
   * @throws org.apache.commons.configuration.ConfigurationException �� - ������ ��� ���������� ������������ � ����.
  */
  public void saveToFile(String fileName, String rootName, String sectionName) throws ConfigurationException
   {
    logger.debug("saveToFile(): trying to save to [" + fileName + "].");
    // ��������������� ���������� �����
    this.getXMLConfig(rootName, sectionName).save(fileName);
   }

  /**
   * ����� ��������� ������� ������������ ��� ���������� (��������� ���������� ������) � ���� � ���� � ���������
   * ������ fileName.
   * @param fileName String ��� �����, � ������� ����� ��������� ������� ��������� ���������� (��������� ������).
   * @param rootName String ��� ��������� (root) �������� ��� ������������ ����� ������������.
   * @throws org.apache.commons.configuration.ConfigurationException �� - ������ ��� ���������� ������������ � ����.
  */
  public void saveToFile(String fileName, String rootName) throws ConfigurationException
   {this.saveToFile(fileName, rootName, null);}

  /**
   * ����� ��������� ������� ������������ ��� ���������� (��������� ���������� ������) � ���� � ���� � ���������
   * ������ fileName.
   * @param fileName String ��� �����, � ������� ����� ��������� ������� ��������� ���������� (��������� ������).
   * @throws org.apache.commons.configuration.ConfigurationException �� - ������ ��� ���������� ������������ � ����.
  */
  public void saveToFile(String fileName) throws ConfigurationException {this.saveToFile(fileName, null);}

  public static void main(String[] args)
   {
    InitLogger.initLoggers(new String[] {"jdb", "jlib", "org.apache.commons"});
    Logger logger = Logger.getLogger("jdb");

    try
     {
      ExtendedDBConfig config = new ExtendedDBConfig();
      config.loadFromFile("stormConn.xml", "fff");
      logger.info("->" + config);
      
      /*
      JdbcConfig config2 = new JdbcConfig();
      config2.loadFromFile("asdf", "informix", false);
      config2.setDbName("123");
      config2.saveToFile("asdf", "kkk", "new_section");
      logger.info(config2.toString());
      */
      
      /*
      config.loadFromFile("dbConfig.xml", false);
      config.password = null;
      Properties props = new Properties();
      props.setProperty("asdf", "fdsa");
      props.setProperty("ert", "tre");
      config.connInfo = props;
      logger.info(config.toString());
      config.saveToFile("asdf");
      */
     }
    catch (IOException e) {logger.error(e.getMessage());}
    catch (ConfigurationException e) {logger.error(e.getMessage());}
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    
    //catch (EmptyDBMSTypeException e) {logger.error(e.getMessage());}
    //catch (InvalidDBMSTypeException e) {logger.error(e.getMessage());}
    //catch (EmptyPassException e) {logger.error(e.getMessage());}
   }

 }