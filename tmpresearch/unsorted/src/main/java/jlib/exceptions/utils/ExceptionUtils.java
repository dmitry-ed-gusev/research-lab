package jlib.exceptions.utils;

/**
 * ����� ��������� ������� (������) ��� ������ � ��.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 13.07.2009)
*/

public class ExceptionUtils
 {
  
  /**
   * ����� ��������� ��������� (���������) ��� ���������� ��� ��. ��������� ������� �� ����� ������ �� � ��������� ��.
   * ���� ���������� �� ����� - ����� ������ ��������������� ���������.
   * @param e Exception �� ��� ������������ ���������.
  */
  public static String getExceptionMessage(Exception e)
   {
    String result;
    if (e != null) {result = "Exception class [" + e.getClass().getName() + "]. Exception message [" + e.getMessage() + "].";}
    else           {result = "Exception is NULL!";}
    return result;
   }

 }