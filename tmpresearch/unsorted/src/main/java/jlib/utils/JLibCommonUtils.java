package jlib.utils;

import java.util.ArrayList;

/**
 * Общеупотребительные утилиты.
 * @author Gusev Dmitry (Гусев Дмитрий)
 * @version 1.0 (DATE: 29.03.11)
*/

public class JLibCommonUtils
 {

  /**
   * Метод возвращает список, разделенный запятыми (CSV - Comma-Separated-Values), полученный из массива-списка.
   * Если исходный список пуст метод вернет значение NULL.
   * @param list ArrayList[Integer] исходный массив-список, по которому строится CSV-список.
   * @return String сформированный список или значение NULL.
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