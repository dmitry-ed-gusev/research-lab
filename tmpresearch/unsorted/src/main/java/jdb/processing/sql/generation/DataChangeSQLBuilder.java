package jdb.processing.sql.generation;

import jdb.DBConsts;
import jdb.filter.sql.SqlFilter;
import jdb.model.dto.FieldDTOModel;
import jdb.model.dto.RowDTOModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Types;

/**
 * Класс содержит вспомогательные методы для построения INSERT и UPDATE запросов по модели данных
 * строки таблицы.
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 22.06.2010)
*/

public class DataChangeSQLBuilder
 {
  /** Компонент-логгер для данного модуля. */
  private static Logger logger = Logger.getLogger(DataChangeSQLBuilder.class.getName());

  /**
   * Метод строит sql-запрос INSERT по модели данных строки таблицы. Метод может использовать фильтрацию sql-запросов и
   * строковых значений.
   * @param tableName String имя таблицы.
   * @param rowModel RowDTOModel модель данных строки таблицы.
   * @param useSqlFilter boolean использовать или нет фильтрацию данных (в основном для строковых значений).
   * @return String сгенерированный запрос или значение null. 
  */
  public static String getDataInsertSql(String tableName, RowDTOModel rowModel, boolean useSqlFilter)
   {
    //logger.debug("Creating data insert sql."); // <- метод генерирует слишком много отладочного вывода

    // Переменные для сохранения и возвращения результата
    String        result = null;
    StringBuilder sql    = null;

    // Если имя таблицы пусто или пуста модель записи - метод вернет значение null
    if ((!StringUtils.isBlank(tableName)) && (rowModel != null) && (!rowModel.isEmpty()))
     {
      sql = new StringBuilder("insert into ");
      sql.append(tableName).append("(").append(rowModel.getCSVFieldsList()).append(") values (");
      // Проходим по всем полям данной записи и формируем sql-запрос
       for (int i = 0; i < rowModel.getFields().size(); i++)
        {
         // Получаем текущее обрабатываемое поле с данными
         FieldDTOModel fieldDTOModel = rowModel.getFields().get(i);
         // Если полученное поле не пусто - обрабатываем его
         if (fieldDTOModel != null)
          {
           // Если тип обрабатываемого поля символьный или дата/время - заключаем его в кавычки
           int fType = fieldDTOModel.getFieldType();
           if ((fType == Types.DATE) || (fType == Types.TIMESTAMP) || (fType == Types.CHAR) ||
               (fType == Types.VARCHAR) || (fType == Types.LONGVARCHAR))
            {
             // Если значение поля = NULL оно и должно остаться таким, а не превратиться в 'NULL' (последнее - строка!)
             String fieldValue = fieldDTOModel.getFieldValue();
             if (fieldValue == null) {sql.append("null");}
             else
              {
               // Если значение символьное и используется фильтрация - проводим фильтрацию
               if (((fType == Types.CHAR) || (fType == Types.VARCHAR) || (fType == Types.LONGVARCHAR)) && (useSqlFilter)) 
                {sql.append("'").append(SqlFilter.changeQuotes(fieldValue)).append("'");}
               // Если же значение не строковое или выключена фильтрация - используем обычное значение
               else
                {sql.append("'").append(fieldValue).append("'");}
              }
            }
           // Если тип поля не символьный - значеие не заключается в кавычки
           else {sql.append(fieldDTOModel.getFieldValue());}
           // Ставим запятую в нужном месте
           if (i < rowModel.getFields().size() - 1) {sql.append(",");}
          }
         // Если же полученное поле пусто - выведем в лог ошибку обработки
         else {logger.error("Processing error - empty field in row!");}
        }
       sql.append(")");
     }
    // Если же параметры не в порядке - сообщим об ошибке
    else {logger.error("Table name or table row model is empty!");}

    // Преобразуем результат в строку (если результат не пуст) и покажем его в отладочном выводе
    if (sql != null)
     {
      result = sql.toString();
      // logger.debug("Generated SQL: " + sql); // <- метод генерирует слишком много отладочного вывода
     }
    // Если результирующий запрос пуст - сообщим об этом
    else             {logger.warn("Result sql is empty!");}

    // Непосредственно возвращение результата
    return result;
   }

  /**
   * Метод строит sql-запрос UPDATE по модели данных строки таблицы. Метод может использовать фильтрацию sql-запросов и
   * строковых значений.
   * @param tableName String имя таблицы.
   * @param rowModel RowDTOModel модель данных строки таблицы.
   * @param id int значение ключевого поля для обновления нужной записи.
   * @param useSqlFilter boolean использовать или нет фильтрацию данных (в основном для строковых значений).
   * @return String сгенерированный запрос или значение null.
  */
  public static String getDataUpdateSql(String tableName, RowDTOModel rowModel, int id, boolean useSqlFilter)
   {
    //logger.debug("Creating data update sql."); // <- метод генерирует слишком много отладочного вывода

    // Переменные для сохранения и возвращения результата
    String        result = null;
    StringBuilder sql    = null;

    // Если имя таблицы пусто или пуста модель записи - метод вернет null.
    if (!StringUtils.isBlank(tableName) && (rowModel != null) && (!rowModel.isEmpty()) && (id > 0))
     {
      sql = new StringBuilder("update ").append(tableName).append(" set");
      // Проходим по всем полям данной записи и формируем sql-запрос
      for (int i = 0; i < rowModel.getFields().size(); i++)
       {
        // Текущее обрабытываемое поле
        FieldDTOModel fieldDTOModel = rowModel.getFields().get(i);
        // Если полученное поле не пусто - обрабатываем его
        if (fieldDTOModel != null)
         {
          sql.append(" ").append(fieldDTOModel.getFieldName()).append(" = ");
          // Если тип обрабатываемого поля символьный или дата/время - заключаем его в кавычки
          int fType = fieldDTOModel.getFieldType();
          if ((fType == Types.DATE) || (fType == Types.TIMESTAMP) || (fType == Types.CHAR) ||
              (fType == Types.VARCHAR) || (fType == Types.LONGVARCHAR))
           {
            // Если значение поля = NULL оно и должно остаться таким, а не превратиться в 'NULL'
            String fieldValue = fieldDTOModel.getFieldValue();
            if (fieldValue == null) {sql.append("null");}
            else
             {
              // Если значение символьное и используется фильтрация - проводим фильтрацию
              if (((fType == Types.CHAR) || (fType == Types.VARCHAR) || (fType == Types.LONGVARCHAR)) && (useSqlFilter))
               {sql.append("'").append(SqlFilter.changeQuotes(fieldValue)).append("'");}
              // Если же значение не строковое или выключена фильтрация - используем обычное значение
              else
               {sql.append("'").append(fieldValue).append("'");}
             }
           }
          else {sql.append(fieldDTOModel.getFieldValue());}
          // Ставим запятую в нужном месте
          if (i < rowModel.getFields().size() - 1) {sql.append(",");}
         }
        // Если же полученное поле пусто - выведем в лог ошибку обработки
        else {logger.error("Processing error - empty field in row!");}
       }
      sql.append(" where ").append(DBConsts.FIELD_NAME_KEY).append(" = ").append(id);
     }
    // Если же параметры не в порядке - сообщим об ошибке
    else {logger.error("Table name, table row model or id is empty!");}

    // Преобразуем результат в строку (если результат не пуст) и покажем его в отладочном выводе
    if (sql != null)
     {
      result = sql.toString();
      //logger.debug("Generated SQL: " + sql); // <- метод генерирует слишком много отладочного вывода
     }
    // Если результирующий запрос пуст - сообщим об этом
    else             {logger.warn("Result sql is empty!");}
    
    // Непосредственно возвращение результата
    return result;
   }

 }