package jdb.processing.integrity;

import jdb.DBConsts;
import dgusev.dbpilot.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.integrity.DBIntegrityModel;
import jdb.model.integrity.TableIntegrityModel;
import jdb.monitoring.DBProcessingMonitor;
import jdb.processing.DBCommonProcessor;
import jdb.processing.spider.DBSpider;
import jdb.utils.DBUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Данный модуль реализует функцию проверки целостности всех таблиц текущей БД. Поддерживаются списки
 * "разрешенных" и "запрещенных" таблиц.
 * @author Gusev Dmitry (019gus)
 * @version 5.0 (DATE: 07.06.2010)
*/

public class DBIntegrityChecker extends DBCommonProcessor
 {
  /** Компонент-логгер данного класса. */
  private Logger logger = Logger.getLogger(this.getClass().getName());

  /**
   * Конструктор по умолчанию. Инициализирует текущую конфигурацию.
   * @param config ConnectionConfig конфигурация модуля.
   * @throws DBModuleConfigException ИС возникает, если конструктору передана пустая конфигурация.
  */
  public DBIntegrityChecker(DBConfig config) throws DBModuleConfigException {super(config);}

  /**
   * Метод приводит текущую БД к идентичности с БД, модель целостности которой передана в качестве параметра.
   * @param db DBIntegrityModel модель целостности БД, в соответствии с которой мы приводим текущую БД.
   * @param monitor ProcessMonitor класс-монитор, который реализует функциональность по мониторингу процесса десериализации.
   * @throws SQLException ИС при выполнении анализа таблицы БД или других SQL-опреаторов.
   * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД.
   * @throws DBModelException ошибки модели базы данных.
   * @return ArrayList[Strung] список возникших при работе модуля ошибок (строковых сообщений). В данном списке
   * возвращаются НЕ КРИТИЧЕСКИЕ ошибки - те, которые не приводят в останову модуля.
  */
  @SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
  public ArrayList<String> makeIntegrity(DBIntegrityModel db, DBProcessingMonitor monitor)
   throws SQLException, DBModelException, DBConnectionException, DBModuleConfigException
   {
    logger.debug("makeIntegrity(): making integrity for current DB.");
    // Если включен демо-режим - сообщаем об этом
    if (getConfig().isDemo()) {logger.warn("DEMO MODE IS ON!");}

    // Если полученная модель пуста (=null), генерируется ИС
    if (db == null) {throw new DBModelException("Received database integrity model is empty!");}

    // Возвращаемый список возникших ИС
    ArrayList<String> errorsList = null;

    // Список всех таблиц текущей БД
    DBSpider spider = new DBSpider(this.getConfig());

    // todo: вернуть строку в первоначальный вариант!!!
    //ArrayList<String> tablesList = spider.getTablesList();
    ArrayList<String> tablesList = new ArrayList<String>(Arrays.asList(""));

    // Устанавливаем соединение с текущей СУБД
    Connection    connection = DBUtils.getDBConn(getConfig());
    Statement     stmt       = connection.createStatement();

    // Проходим по всем таблицам текущей БД и сравниваем их с таблицами полученной БД (если список не пуст)
    if ((tablesList != null) && (!tablesList.isEmpty()))
     {
      int processedTablesCounter = 0;                        // <- счетчик обработанных таблиц
      int tablesCount            = tablesList.size(); // <- общее количество обрабатываемых таблиц
      for (String currentTable : tablesList)
       {
        // Если таблицу затрагивают списки ограничений - ее не обрабатываем
        if (!getConfig().isTableAllowed(currentTable)) {logger.warn("This table [" + currentTable + "] is deprecated or not allowed! Skipping.");}
        // Если же списки ограничений таблицу не затрагивают - обрабатываем ее
        else
         {
          logger.info("Checking integrity [" + currentTable + "].");
          // Получим таблицу из переданной в качестве параметра модели БД, если таблица пуста или ее нет -
          // для текущей таблицы не выполняется восстановление целостности.
          TableIntegrityModel table = db.getTable(currentTable);
          if (table != null)
           {
            String csvList = table.getCSVKeysList();
            // Если не пуст список ключей внешней таблицы - работаем
            if ((csvList != null) && (csvList.length() > 0))
             {
              StringBuilder sql = new StringBuilder("delete from ").append(currentTable).append(" where ");
              sql.append(DBConsts.FIELD_NAME_KEY).append(" not in (").append(csvList).append(")");
              // Данный отладочный вывод нужен только для глубокой отладки
              //logger.debug("Executing sql: " + sql.toString());

              // Выполнение запроса на удаление данных (если демо-режим выключен)
              if (!getConfig().isDemo())
               {
                // Конструкция try-catch нужна, чтобы один неудачно выпорлнившийся sql-запрос не обвалил весь цикл проверки
                // целостности. Каждый ошибочный запрос заносим в результирующий список ошибок для возврата в вызывающий код.
                try {stmt.executeUpdate(sql.toString());}
                catch (SQLException e)
                 {
                  // Сообщение об ошибке
                  StringBuilder errMsg = new StringBuilder("Can't execute sql [").append(sql.toString());
                  errMsg.append("]. Reason [").append(e.getMessage()).append("].");
                  logger.error(errMsg.toString());
                  // Если список возникших ошибок не инициализирован - инициализация
                  if (errorsList == null) {errorsList = new ArrayList<String>();}
                  errorsList.add(errMsg.toString());
                 }
                } // END [isDemo()] BLOCK
             }
           }
         } // Конец секции обработки текущей "разрешенной" таблицы
       
        // После обработки каждой таблицы(неважно, разрешена она или нет) выводим мониторинговое сообщение (если необходимо)
        if (monitor != null)
         {
          // Увеличиваем счетчик обработанных таблиц
          processedTablesCounter++;
          // Считаем текущий процент выполнения
          int currentProgress = (processedTablesCounter*100/tablesCount);
          // Вывод в процесс-монитор текущего прогресса выполнения ()
          monitor.processProgress(currentProgress);
          // Если включена отладка (уровень сообщений лога DEBUG), то выведем сообщение об обрабатываемой сейчас таблице.
          if (logger.getEffectiveLevel().equals(Level.DEBUG)) {monitor.processDebugInfo("[DEBUG] " + currentTable);}
         }
       } // Конец цикла for
     } // Конец секции обработки непустого списка таблиц

    // Попытаемся закрыть открытое соединение
    connection.close();
    return errorsList;
   }

  /**
   * Метод приводит текущую БД к идентичности с БД, модель целостности которой передана в качестве параметра.
   * @param db DBIntegrityModel модель целостности БД, в соответствии с которой мы приводим текущую БД.
   * @throws SQLException ИС при выполнении анализа таблицы БД или других SQL-опреаторов.
   * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД.
   * @throws DBModelException ошибки модели базы данных.
   * @return ArrayList[Strung] список возникших при работе модуля ошибок (строковых сообщений). В данном списке
   * возвращаются НЕ КРИТИЧЕСКИЕ ошибки - те, которые не приводят в останову модуля.
  */
  @SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
  public ArrayList<String> makeIntegrity(DBIntegrityModel db)
   throws DBConnectionException, DBModelException, DBModuleConfigException, SQLException {return this.makeIntegrity(db, null);}

  /**
   * Метод предназначен только для тестирования данного класса.
   * @param args String[] параметры метода main.
  */
  //public static void main(String[] args) {}
  
 }