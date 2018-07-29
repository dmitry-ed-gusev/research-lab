package jlib.utils.string;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * ������ ������ �������� ��������� ����������� ������ ��� ������ �� ��������.
 * @author Gusev Dmitry (����� �������)
 * @version 1.0 (DATE: 24.02.11)
*/

public class StrUtilities
 {
  private static Logger logger = Logger.getLogger("jlib");

  /**
   * ����� ��������� ��� (���������) ������������� ����� (�������� lenght) �� ������ ���������� � ���������
   * name �����. ��������� ��� ����������� ����������� ���������, ���������� � �������� symbol. �������� lenght
   * ����������� ������ ���� ������������� � �� ������ 0, ����� ����� ������ �������� null. ��� ���� ����������� ��
   * �������� ��������� lenght: �������� ������� ��������� ������ ���� ������ (������ ������) ����� �������� ���������
   * name - � ��������� ������ ����� �� �������� ������� �������� � ������ �������� ��������� name (������� � ���� ������
   * ��������� ��������� name, ��� ������ name ����� ������ null). �������� symbol ������ ��������� ������������ ������,
   * � ��������� ������ �������������� ��� ����� �������� ���� ��������� �������. �������� name ������ ���� �������� (�
   * �� �������� �� ����� �������� �������, ��������� � �.�.), ���� �� �� ����, �� ����� ������ �������� null.
   * @param lenght int ����������� ����� ��������������� �����.
   * @param symbol char ������, ������� ����������� � ����������� ����� name ��� ���������� ����������� ����� lenght.
   * @param name String ��������� ���, ������� ���������� ��������� �� ����������� �����.
   * @return String �������������� ��������� ��� ��� �������� null.
  */
  public static String getFixedLengthName(int lenght, char symbol, String name)
   {
    String result = null;
    // ��������� �������� name
    if (!StringUtils.isBlank(name))
     {
      logger.debug("Name parameter is OK. Processing name [" + name + "].");
      // ��������� �������� lenght (�� ������ ���� ����������� � �� ����� 0)
      if (lenght > 0)
       {
        logger.debug("Lenght [" + lenght + "] is OK. Processing.");
        // �������� ��������� ������ ��� �������� lenght > name.lenght
        if (lenght > name.length())
         {
          StringBuilder resultName = new StringBuilder();
          for (int i = 0; i < (lenght - name.length()); i++) {resultName.append(symbol);}
          resultName.append(name);
          // ���������� �������� ����������
          result = resultName.toString();
        }
        // �������� lenght <= name.lenght
        else {result = name;}
       }
      // �������� lenght �� �������
      else {logger.error("Wrong lenght [" + lenght + "]!");}
     }
    // �������� name ���� - ������� �� ������!
    else {logger.error("Name parameter is empty!");}
    // ���������� ���������
    return result;
   }

  /**
   * ����� ��������� ��� (���������) ������������� ����� (�������� lenght) �� ������ ���������� � ���������
   * name ����� (����������� ����� ������������� �����). ��������� ��� ����������� ����������� ���������, ���������� �
   * �������� symbol. �������� lenght ����������� ������ ���� ������������� � �� ������ 0, ����� ����� ������ �������� null.
   * ��� ���� ����������� �� �������� ��������� lenght: �������� ������� ��������� ������ ���� ������ (������ ������) �����
   * �������� ��������� name (� ��������) - � ��������� ������ ����� �� �������� ������� �������� � ������ �������� ��������� name
   * (������� � ���� ������ ��������� ��������� name, ��� ������ name ����� ������ null). �������� symbol ������ ���������
   * ������������ ������, � ��������� ������ �������������� ��� ����� �������� ���� ��������� �������. �������� name ������ ����
   * ����� ������������� ������ (������ ������ ����), � ��������� ������ ����� ������ �������� null.
   * @param lenght int ����������� ����� ��������������� �����.
   * @param symbol char ������, ������� ����������� � ����������� ����� name ��� ���������� ����������� ����� lenght.
   * @param name String ��������� ���, ������� ���������� ��������� �� ����������� �����.
   * @return String �������������� ��������� ��� ��� �������� null.
  */
  public static String getFixedLengthName(int lenght, char symbol, int name)
   {
    String result = null;
    if (name > 0) {result = StrUtilities.getFixedLengthName(lenght, symbol, String.valueOf(name));}
    else {logger.error("Name value must be strictly greater than 0!");}
    return result;
   }

 }