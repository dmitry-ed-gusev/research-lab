package jdb.processing.data.helpers;

import org.apache.commons.lang.StringUtils;

/**
 * �����-��������, ����������� ��������������� ������ ��� ������� ��������� (���������/��������) ������.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 16.02.2010)
*/

public class DataProcessingHelper
 {

  /**
   * ����� ��������� ��������� ��� ������ ������� - ��� �������, ��� ��������� ���� � ��� ���� � ����������� �������.
   * �������� ���� ����� ������ ���� �� �������. ���� �� ��� �� ��� (�����-�� �������� ����� ��� ��� �����) �����
   * ���������� ������ � ��������� ������ � ����������.
   * @param tableName String ����������� ��� �������.
   * @param keyFieldName String ����������� ���  ��������� ����.
   * @param dataFieldName String ����������� ��� ���� � �������.
   * @return String �������� null, ���� ��� ����� � ������� ��� ������ � ��������� ���������� �����.
  */
  public static String checkParams(String tableName, String keyFieldName, String dataFieldName)
   {
    String result = null;
    if (StringUtils.isBlank(tableName))          {result = "Table name is empty!";}
    else if (StringUtils.isBlank(keyFieldName))  {result = "Key field name is empty!";}
    else if (StringUtils.isBlank(dataFieldName)) {result = "Data field name is empty!";}
    return result;
   }

  /**
   * ����� ��������� ��������� ��� ������ ������� - ��� ������� � ��� ��������� ����. �������� ���� ����� ������
   * ���� �� �������. ���� �� ��� �� ��� (�����-�� �������� ����� ��� ��� �����) ����� ���������� ������ �
   * ��������� ������ � ����������.
   * @param tableName String ����������� ��� �������.
   * @param keyFieldName String ����������� ���  ��������� ����.
   * @return String �������� null, ���� ��� ����� � ������� ��� ������ � ��������� ���������� �����.
  */
  public static String checkParams(String tableName, String keyFieldName)
   {
    String result = null;
    if (StringUtils.isBlank(tableName))          {result = "Table name is empty!";}
    else if (StringUtils.isBlank(keyFieldName))  {result = "Key field name is empty!";}
    return result;
   }

 }