package jlib.utils;

import java.util.ArrayList;

/**
 * ������������������� �������.
 * @author Gusev Dmitry (����� �������)
 * @version 1.0 (DATE: 29.03.11)
*/

public class JLibCommonUtils
 {

  /**
   * ����� ���������� ������, ����������� �������� (CSV - Comma-Separated-Values), ���������� �� �������-������.
   * ���� �������� ������ ���� ����� ������ �������� NULL.
   * @param list ArrayList[Integer] �������� ������-������, �� �������� �������� CSV-������.
   * @return String �������������� ������ ��� �������� NULL.
  */
  public static String getCSVFromArrayList(ArrayList<Integer> list)
   {
    String result = null;
    if ((list != null) && (!list.isEmpty()))
     {
      StringBuilder csv = new StringBuilder();
      for (int i = 0; i < list.size(); i++)
       {
        csv.append(list.get(i));
        if (i < (list.size() - 1)) {csv.append(", ");}
       }
      result = csv.toString();
     }
    return result;
   }

 }