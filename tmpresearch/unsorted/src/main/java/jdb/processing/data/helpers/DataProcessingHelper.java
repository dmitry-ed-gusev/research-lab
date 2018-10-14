package jdb.processing.data.helpers;

import org.apache.commons.lang.StringUtils;

/**
 * Класс-помощник, реализующий вспомогательные методы для классов обработки (изменения/проверки) данных.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 16.02.2010)
*/

public class DataProcessingHelper
 {

  /**
   * Метод проверяет параметры для других методов - имя таблицы, имя ключевого поля и имя поля с изменяемыми данными.
   * Значения этих полей должны быть не пустыми. Если же это не так (какое-то значение пусто или все пусты) метод
   * возвращает строку с описанием ошибки в параметрах.
   * @param tableName String проверяемое имя таблицы.
   * @param keyFieldName String проверяемое имя  ключевого поля.
   * @param dataFieldName String проверяемое имя поля с данными.
   * @return String значение null, если все имена в порядке или строка с описанием ошибочного имени.
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
   * Метод проверяет параметры для других методов - имя таблицы и имя ключевого поля. Значения этих полей должны
   * быть не пустыми. Если же это не так (какое-то значение пусто или все пусты) метод возвращает строку с
   * описанием ошибки в параметрах.
   * @param tableName String проверяемое имя таблицы.
   * @param keyFieldName String проверяемое имя  ключевого поля.
   * @return String значение null, если все имена в порядке или строка с описанием ошибочного имени.
  */
  public static String checkParams(String tableName, String keyFieldName)
   {
    String result = null;
    if (StringUtils.isBlank(tableName))          {result = "Table name is empty!";}
    else if (StringUtils.isBlank(keyFieldName))  {result = "Key field name is empty!";}
    return result;
   }

 }