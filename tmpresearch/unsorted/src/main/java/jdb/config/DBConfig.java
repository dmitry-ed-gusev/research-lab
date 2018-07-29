package jdb.config;

import jdb.config.connection.ExtendedDBConfig;
import jdb.exceptions.DBModuleConfigException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * ������ ����� ��������� ������������������� ������ ������������ ��� ���������� � ����. ����� ��������� �� ������
 * ������� {@link jdb.config.connection.BaseDBConfig BaseJdbcConfig} � {@link jdb.config.connection.ExtendedDBConfig JdbcConfig}.
 * ����� ���������/��������� ���� ������������ � ����� �� �����, ����� ������� ������ ����������� � ��������� ��,
 * ������ ������ ����-������, ��������� ��������� �������� ����������. �� ���� �������� ������� ���������������� ����������
 * � ���� (BaseJdbcConfig -> JdbcConfig -> DBConfig) ������������� ������������ ������ ������ �������.
 * <br>
 * ������ ����������. ������ ����������� ��� ������ ("�����������" � "�����������" �������) ������������ ������ ������
 * � ������� ������, ��� ����� ������������ ����, ���������� ������ � �.�. ������� �� ������������� � ������ ������ ������
 * ��������� ������� � ����������� ������� - �������� � �������� ���� MS SQL Server 2005. ��������: dbo.table1 � schema1.table1
 *
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 21.12.2009)
*/

public class DBConfig extends ExtendedDBConfig
 {
  /** ���������-������ ������� ������. */
  private Logger                logger           = Logger.getLogger(getClass().getName());
  /** ������ ����������� � ��������� ������. */
  private ArrayList<String> allowedTables    = null;
  /** ������ ����������� � ��������� ������. */
  private ArrayList<String> deprecatedTables = null;
  /** ������� ��� �������� ����-�����. */
  private boolean               isDemo           = false;

  public ArrayList<String> getAllowedTables() {
   return allowedTables;
  }

  public void setAllowedTables(ArrayList<String> allowedTables) {
   this.allowedTables = allowedTables;
  }

  public ArrayList<String> getDeprecatedTables() {
   return deprecatedTables;
  }

  public void setDeprecatedTables(ArrayList<String> deprecatedTables) {
   this.deprecatedTables = deprecatedTables;
  }

  public boolean isDemo() {
   return isDemo;
  }

  public void setDemo(boolean demo) {
   isDemo = demo;
  }

  /** ����� ������ "�����������" ������. */
  public void              resetAllowedTables()                                    {this.allowedTables = null;}
  /** ����� ������ "�����������" ������. */
  public void              resetDeprecatedTables()                                 {this.deprecatedTables = null;}
  /** ����� ���� ����������� (������� "�����������" � "�����������" ������). */
  public void              resetConstraints()                                      {this.resetAllowedTables(); this.resetDeprecatedTables();}
  
  /** ����������� �� ��������� - ������� ������ ��������� ������� ������ � ������ ������ �� ������. */
  public DBConfig() {}

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
  public DBConfig(String fileName, String sectionName, boolean usePlainPass)
   throws DBModuleConfigException, IOException, ConfigurationException
   {this.loadFromFile(fileName, sectionName, usePlainPass);}

  /**
   * ����������� ��������� ������ �� ����������������� xml-�����. � ������������ �� ���� � ������� �������� ���
   * ����� ������ ��� �������� ������ - ������ ���������������� ������� �� �������� ��������� DBConfigConsts.USE_PLAIN_PASS.
   * @param fileName String xml-����, �� �������� ����� ��������� ������������.
   * @param sectionName String ��� ��������� ������ &lt;db&gt; � ������-�����, �� ������� ����� �������� ���������.
   * @throws java.io.IOException �� - ������� ������ ��� �����, ���� �� ���������� � �.�.
   * @throws org.apache.commons.configuration.ConfigurationException �� - ������ ��� �������� ������������ �� xml-�����.
   * @throws jdb.exceptions.DBModuleConfigException - ������ ����������������� ����� - �������� ��� ����, ��� ���� �����������
   * � �.�. ������ (��������� � �����������������).
  */
  public DBConfig(String fileName, String sectionName) throws DBModuleConfigException, IOException, ConfigurationException
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
  public DBConfig(String fileName, boolean usePlainPass) throws DBModuleConfigException, IOException, ConfigurationException
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
  public DBConfig(String fileName) throws DBModuleConfigException, IOException, ConfigurationException
   {this.loadFromFile(fileName);}
  
  /**
   * ������ ����� ��������� ���� ������� � ������ "�����������" ������. ������� �����������, ������ ���� ���������
   * ��� ������� �� ������.
   * @param tableName String ��� ����������� �������.
  */
  public void addDeprecatedTable(String tableName)
   {
    if (!StringUtils.isBlank(tableName))
     {
      if (this.deprecatedTables == null) {this.deprecatedTables = new ArrayList<String>();}
      this.deprecatedTables.add(tableName.toUpperCase());
     }
    else {logger.warn("addDeprecatedTable(): can't add empty deprecated name!");}
   }

  /**
   * ����� ��������� ����� ������ ������ � ������ "�����������". ������ ����������� ������ ���� �� �� ����.
   * @param tablesNames String[] ������ ������, ����������� � ������ "�����������".
  */
  public void addDeprecatedTables(String[] tablesNames)
   {
    if ((tablesNames != null) && (tablesNames.length > 0))
     {
      if (this.deprecatedTables == null) {this.deprecatedTables = new ArrayList<String>();}
      this.deprecatedTables.addAll(Arrays.asList(tablesNames));
     }
    else {logger.warn("addDeprecatedTables(): can't add empty deprecated list!");}
   }

  /**
   * ����� ��������� ���� ������� � ������ "�����������" ������. ������� ����������� ������ ���� �������
   * �������� ���.
   * @param tableName String ��� ����������� �������.
  */
  public void addAllowedTable(String tableName)
   {
    if (!StringUtils.isBlank(tableName))
     {
      if (this.allowedTables == null) {this.allowedTables = new ArrayList<String>();}
      this.allowedTables.add(tableName.toUpperCase());
     }
    else {logger.warn("addAllowedTable(): can't add empty allowed name!");}
   }

  /**
   * ����� ��������� ����� ������ ������ � ������ "�����������". ������ ����������� ������ ���� �� �� ����.
   * @param tablesNames String[] ������ ������, ����������� � ������ "�����������".
  */
  public void addAllowedTables(String[] tablesNames)
   {
    if ((tablesNames != null) && (tablesNames.length > 0))
     {
      if (this.allowedTables == null) {this.allowedTables = new ArrayList<String>();}
      this.allowedTables.addAll(Arrays.asList(tablesNames));
     }
    else {logger.warn("addAllowedTables(): can't add empty allowed list!");}
   }

  /**
   * ����� ������������� ��� ����������� �� ������� ��� ������� ������ - ��� ����������� � ��� �����������
   * ������. ����������� ���������������, ������ ���� ��� �� ����� (��������������� ������ � �����������).
   * @param allowedTables ArrayList ����������� - ������ ����������� ������.
   * @param deprecatedTables ArrayList ����������� - ������ ����������� ������.
  */
  public void setConstraints(ArrayList<String> allowedTables, ArrayList<String> deprecatedTables)
   {
    if ((allowedTables != null) && (!allowedTables.isEmpty()))       {this.allowedTables = allowedTables;}
    if ((deprecatedTables != null) && (!deprecatedTables.isEmpty())) {this.deprecatedTables = deprecatedTables;}
   }

  /**
   * �������� ���������� ��������� ������� � ������ tableName. ���� ��� ������� ����� - ����� ���������� �������� false.
   * ��� �������� ������� � ���������� ��������� ��������� ������ ����������� � ����������� ������, � ����� ������
   * ������ ���������� �������� ��� ������� ���� ����. ���� �� �� ������������� ������� ������ ���� ������ dbType ��
   * ����� �������� - ����� ���������� �� - DBModuleConfigException.
   * @param tableName String ��� �������, ���������� ��������� ������� �� ���������.
   * @return boolean ��������� �������� ���������� ��������� �������.
   * @throws DBModuleConfigException �� - ����� �������������� ������� ������ �� ������ ��� ���� ���
   * ������� �������.
  */
  public boolean isTableAllowed(String tableName) throws DBModuleConfigException
   {
    boolean result = false;
    // ��������� ��������� ��� �������� ��� - ���� ��� �� �����, �� ��������
    if (!StringUtils.isBlank(tableName))
     {
      // ��������� ��� ���� - ���� �� ����, �� ���������� ��
      if (getDbType() == null) {throw new DBModuleConfigException("Empty DBMS type!");}
      // ���� �� ��� ���� �� ���� - �������� �����
      else
       {
        if
          // ���� ������ "�����������" ���� ��� ������� � ��� ��� - ��
          (((deprecatedTables == null) || (!deprecatedTables.contains(tableName.toUpperCase()))) &&
          // ���� ������ "�����������" ���� ��� ������� � ��� ���� - ��
          ((allowedTables == null) || (allowedTables.contains(tableName.toUpperCase()))))
         // ��������� �����������
         {
          logger.debug("isTableAllowed(): table [" + tableName + "] is allowed.");
          result = true;
         }
       }
     }
    // ���� ��� ����� - �������� �� ���� � ��� � ��� (���������� false)
    else {logger.warn("isTableAllowed: empty table name!");}
    // ���������� ���������
    return result;
   }

  @Override
  public String toString()
   {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
            appendSuper(super.toString()).
            append("allowedTables", allowedTables).
            append("deprecatedTables", deprecatedTables).
            append("isDemo", isDemo).
            toString();
   }

 }