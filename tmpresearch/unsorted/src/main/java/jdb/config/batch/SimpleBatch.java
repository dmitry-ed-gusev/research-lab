package jdb.config.batch;

import jdb.filter.sql.SqlFilter;
import jdb.processing.sql.execution.batch.helpers.BatchPreProcessor;
import jdb.utils.DBUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Простой sql-батч (набор строковых sql-запросов). Также класс содержит несколько методов для работы со списком
 * запросов. Эти методы автоматизируют некоторые задачи. Класс создан для использования его в большом классе конфигурации
 * sql-батча - см. класс BatchConfig.
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 16.11.2010)
*/

public class SimpleBatch
 {
  /** Компонент-логгер данного класса. */
  private Logger logger = Logger.getLogger(getClass().getName());
  /** Набор sql-запросов данного батча. */
  private ArrayList<String> batch = null;

  /**
   * Конструктор. Добавляет один запрос в батч.
   * @param sql String запрос, добавляемый в батч.
   * @param useSqlFilter boolean использовать ли фильтрацию (запроса) при добавлении запроса.
  */
  public SimpleBatch(String sql, boolean useSqlFilter)
   {this.addSqlToBatch(sql, useSqlFilter);}

  /**
   * Конструктор. Добавляет набор запросов в батч.
   * @param batch ArrayList[String] запросы, добавляемый в батч.
   * @param useSqlFilter boolean использовать ли фильтрацию (запросов) при добавлении запросов.
  */
  public SimpleBatch(ArrayList<String> batch, boolean useSqlFilter)
   {this.setBatch(batch, useSqlFilter);}

  public ArrayList<String> getBatch() {
   return batch;
  }

  /**
   * Установка значения sql-батча. При установке батча присходит его обработка (препроцессинг) - удаление пустых и
   * select-запросов. Если после обработки батч окажется пустым, присвоения значения не происходит и выводится
   * соотв. запись в лог. Если батч данного экземпляра конфига уже содержал какие-либо запросы (уже был инициализирован),
   * то эти запросы будут затерты устанавливаемым батчем.
   * @param batch ArrayList[String] устанавливаемое значение батча.
   * @param useSqlFilter boolean использовать ли фильтрацию (запросов).
  */
  public void setBatch(ArrayList<String> batch, boolean useSqlFilter)
   {
    // Сразу же выполняем препроцессинг sql-батча
    ArrayList<String> tmpBatch = BatchPreProcessor.preProcessSqlBatch(batch, useSqlFilter);
    // Если полученный результат не пуст - работаем (если результат пуст, то ничего не делаем)
    if ((tmpBatch != null) && (!tmpBatch.isEmpty())) {this.batch = tmpBatch;}
    // Если батч пуст - сообщим об этом в лог
    else {logger.error("Trying to set empty batch!");}
   }

  /**
   * Метод добавляет один sql-запрос к батчу данного конфига (добавдяется запрос в конец батча). Если батч еще не
   * инициализирован - происходит инициализация. Перед добавлением запрос проверяется на валидность - он не должен быть
   * пуст, не должен быть select-запросом. Также, в зависимости от настроек, выполняется фильтрация запроса.
   * @param sql String добавляемый в тело батча запрос.
   * @param useSqlFilter boolean использовать ли фильтрацию (запроса) при добавлении запроса.
  */
  public void addSqlToBatch(String sql, boolean useSqlFilter)
   {
    // Если запрос не пуст - работаемс
    if (!StringUtils.isBlank(sql))
     {
      try
       {
        // Если запрос не-select - продолжаем
        if (!DBUtils.isSelectQuery(sql))
         {
          // Если установлена фильтрация запросов - выполняем
          if (useSqlFilter)
           {
            String localSql = SqlFilter.removeDeprecated(sql);
            // Если после фильтрации запрос не пуст - работаемс
            if (!StringUtils.isBlank(localSql))
             {
              // Инициализация батча
              if (this.batch == null) {batch = new ArrayList<String>();}
              batch.add(localSql);
             }
            // Если же после фильтрации запрос стал пустым - ошибку в лог
            else {logger.error("Sql query become empty after filtering!");}
           }
          // Если нет фильтрации sql-запросов, просто добавляем запрос в батч
          else
           {
            // Инициализация батча
            if (this.batch == null) {batch = new ArrayList<String>();}
            batch.add(sql);
           }
         }
        // Если же запрос является select-запросом, не добавляем его!
        else {logger.error("Trying to add SELECT-query to batch!");}
       }
      catch (SQLException e) {logger.error(e.getMessage());}
     }
    // Если указанный запрос пуст - сообщаем в лог
    else {logger.error("Trying to add empty query to batch!");}
   }

  /**
   * Метод добавляет целый sql-батч к батчу данного конфига (новый батч добавляется в конец старого батча). Если батч
   * данного конфига еще не инициализирован - происходит инициализация. Перед добавлением батча происходит его обработка
   * (препроцессинг) - удаление пустых и select-запросов. Если после обработки новый батч окажется пустым, добавления не
   * происходит, выполняется запись ошибки в лог.
   * @param batch ArrayList[String]
   * @param useSqlFilter boolean использовать ли фильтрацию (запросов) при добавлении запросов.
  */
  public void addBatch(ArrayList<String> batch, boolean useSqlFilter)
   {
    // Сразу же выполняем препроцессинг sql-батча
    ArrayList<String> tmpBatch = BatchPreProcessor.preProcessSqlBatch(batch, useSqlFilter);
    // Если полученный результат не пуст - работаем (если результат пуст, то ничего не делаем)
    if ((tmpBatch != null) && (!tmpBatch.isEmpty())) {this.batch.addAll(tmpBatch);}
    // Если батч пуст - сообщим об этом в лог
    else {logger.error("Trying to add empty batch!");}
   }

  /**
   * Метод возвращает размер батча.
   * @return int размер батча. Если батч еще не инициализирован, метод вернет значение 0.
  */
  public int getBatchSize()
   {
    int result = 0;
    if ((batch != null) && (!batch.isEmpty())) {result = batch.size();}
    return result;
   }

  /**
   * Метод помогает узнать - пуст ли данный экземпляр класса-батча.
   * @return boolean ИСТИНА/ЛОЖЬ в зависимости от того, пуст или нет данный экземпляр батча.
  */
  public boolean isEmpty() {return ((batch == null) || (batch.isEmpty()));}

  /** Строковое представление батча. */
  @Override
  public String toString()
   {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
            append("batch", batch).
            toString();
   }
  
 }