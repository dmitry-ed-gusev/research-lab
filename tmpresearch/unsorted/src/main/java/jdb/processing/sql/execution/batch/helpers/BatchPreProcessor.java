package jdb.processing.sql.execution.batch.helpers;

import jdb.filter.sql.SqlFilter;
import jdb.utils.DBUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Класс содержит метод(ы) подготовки (препроцессинга) списка sql-запросов (sql-batch) к пакетной обработке. Подготовка
 * заключается в отсеивании пустых запросов, отсеивании SELECT-запросов (они не должны выполняться в батче) и, возможно,
 * фильтрации sql-запросов (удаление "запрещенных символов", замена кавычек и т.п.)
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 16.11.2010)
*/

public class BatchPreProcessor
 {
  /** Логгер данного модуля. */
  private static Logger logger = Logger.getLogger(BatchPreProcessor.class.getName());

  /**
   * Метод выполняем препроцессинг набора sql-запросов (батча) для удаления из него пустых запросов (null или пустая строка)
   * и select-запросов. Если обрабатываемый батч пуст или null, метод возвращает значение null.
   * @param batch ArrayList[String] батч для обработки
   * @param useSqlFilter boolean использовать или нет фильтрацию sql-запросов перед добавлением в батч.
   * @return ArrayList[String] обработанный батч или null.
  */
  public static ArrayList<String> preProcessSqlBatch(ArrayList<String> batch, boolean useSqlFilter)
   {
    logger.debug("BatchPreProcessor: preProcessSqlBatch().");
    ArrayList<String> result = null;
    // Если входящий батч не пуст - работаем
    if ((batch != null) && (!batch.isEmpty()))
     {
      logger.debug("Batch is not empty. Processing. SQL filter mode [" + useSqlFilter + "]");
      // В цикле проходим по обрабатываемому батчу и выделяем из него не пустые не select-запросы
      for (String sql : batch)
       {
        // Конструкция try...catch нужна для того, чтобы одна ИС не обрашила весь цикл
        try
         {
          // Если найден не пустой не select-запрос - в результат его!
          if (!StringUtils.isBlank(sql) && !DBUtils.isSelectQuery(sql))
           {
            // Если включена фильтрация sql-запросов, то фильтруем все запросы перед добавлением в батч
            if (useSqlFilter)
             {
              String localSql = SqlFilter.removeDeprecated(sql);
              // Если после фильтрации запрос не стал пустым - добавляем его в батч
              if (!StringUtils.isBlank(localSql))
               {
                // Если результат еще не инициализирован - инициализация
                if (result == null) {result = new ArrayList<String>();}
                result.add(localSql);
               }
              // Запрос после фильтрации стал пустым - ошибку в лог
              else {logger.error("Sql query become empty after filtering!");}
             }
            // Если фильтрация выключена - добавляем запрос в батч без фильтрации
            else
             {
              // Если результат еще не инициализирован - инициализация
              if (result == null) {result = new ArrayList<String>();}
              result.add(sql);
             }
           }
         }
        catch (SQLException e) {logger.error("Error: " + e.getMessage());}
       }
     }
    // Если батч пуст - просто сообщим об этом в лог
    else {logger.warn("Batch for processing is empty!");}
    return result;
   }

 }