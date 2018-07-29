package jdb.processing;

import jdb.config.DBConfig;
import jdb.exceptions.DBModuleConfigException;
import jdb.utils.DBUtils;
import org.apache.commons.lang.StringUtils;

/**
 * ������ ����� - ������������ ��� ���� ������� ������ ������� � ��������� ����. �� ��������� �������� �
 * ������ � ������������ ���������� � ����, � ����� ������ "�����������" � "�����������" ������ ��� �������
 * ���������.
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 27.04.2010)
*/

public class DBCommonProcessor
 {
  /** ������� ������������ ������� ������. */
  private DBConfig config = null;
  
  /**
   * ����������� �� ���������. �������������� ���� config ������� ������.
   * @param config DBConfig ������������ ��� ���������� � ����.
   * @throws DBModuleConfigException �� ���������, ���� ������������ �������� ������ ������������.
  */
  public DBCommonProcessor(DBConfig config) throws DBModuleConfigException
   {
    // ������������� ������������ ������ (���������� � ����). ���� ������ ���� - ������.
    String configErrors = DBUtils.getConfigErrors(config);
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}
    else {this.config = config;}
   }

  public DBConfig getConfig() {return config;}
  public void setConfig(DBConfig config) {this.config = config;}

 }