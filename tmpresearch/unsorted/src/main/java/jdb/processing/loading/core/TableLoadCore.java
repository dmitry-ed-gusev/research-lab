package jdb.processing.loading.core;

import jdb.DBConsts;
import dgusev.dbpilot.config.DBConfig;
import jdb.config.batch.BatchConfig;
import jdb.config.load.DBLoaderConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.monitoring.DBProcessingMonitor;
import jdb.processing.loading.helpers.SqlBatchBuilder;
import jdb.processing.sql.execution.batch.SqlBatcher;
import jdb.utils.DBUtils;
import jlib.logging.InitLogger;
import jlib.utils.FSUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Данный класс реализует десериализацию одной таблицы БД из одного файла/из набора фйалов на диске. При десериализации
 * данных используется большое число модулей данной библиотеки. Процесс десериализации данных очень требователен к ресурсам
 * системы (память, производительность процессора).
 * @author Gusev Dmitry (019gus)
 * @version 4.0 (DATE: 15.11.2010)
*/

// todo: неплохо бы сравнивать формат записи в файле и в таблице в БД (кол-во, типы и наименования полей и т.п.)
// todo: В МОДУЛЕ ЗАКОММЕНЧЕНЫ ВАЖНЫЕ СТРОКИ КОДА. НЕОБХОДИМА ДОРАБОТКА МОДУЛЯ!!!!!

@SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
public class TableLoadCore
 {
  /** Логгер данного класса. */
  private static Logger logger = Logger.getLogger(TableLoadCore.class.getName());

  /**
   * Метод десериализует и записывает в БД информацию из сериализованной таблицы, находящейся в указанном в конфиге
   * каталоге. Метод последовательно обрабатывает все файлы, находящиеся в указанном каталоге и, формируя для каждого файла
   * sql-batch, тут же его выполняет. При десериализации проверяется имя таблицы - если файл с информацией содержит отличное
   * от указанного имя таблицы - файл пропускается (этот механизм контроля обеспечивает десериализацию данных только для
   * указанной таблицы и, если таблица "запрещена" для данной БД (см. параметры соединения с СУБД), то ее данные точно не будут
   * изменены методом).
   * @param config SerializationConfig конфигурация метода десериализации одной таблицы.
   * @param tableName String имя десериализуемой таблицы.
   * @return ArrayList[String] список некритических ошибок, возникших при десериализации данных одной таблицы.
   * @throws DBModuleConfigException ошибки конфигурирования метода/модуля.
   * @throws IOException ошибки чтения данных с диска.
   * @throws SQLException критические ошибки работы с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД.
  */
  public static ArrayList<String> load(DBLoaderConfig config, String tableName)
   throws DBModuleConfigException, DBConnectionException, IOException, SQLException
   {
    logger.debug("WORKING TableLoadCore.load().");
    // Возвращаемый список возникших некритических ИС (при десериализации таблицы)
    ArrayList<String> errorsList   = null;
    // Полный sql-batch, который формируется и выполняется, если указанный параметр useFullScript=true (т.е. если
    // мы не выполняем частичных батчей - для каждого файла)
    ArrayList<String> fullSqlBatch = null;

    // Если нам передан конфиг с ошибками, возбуждаем ИС!
    String configErrors = DBUtils.getConfigErrors(config);
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}
    // Если же конфиг в порядке - выведем отладочной сообщение
    else {logger.debug("Serialization config is OK. Processing.");}
    // Проверка соединения с СУБД с помощью конфига (без соединения с СУБД невозможна десериализация данных)
    if (!DBUtils.isConnectionValid(config.getDbConfig())) {throw new DBConnectionException("Can't connect to DBMS!");}
    else {logger.debug("Connection to DBMS is OK! Processing.");}
    // Проверяем указанное имя таблицы - если оно пусто - ошибка
    if (StringUtils.isBlank(tableName)) {throw new SQLException("Specifyied table name is empty!");}
    else {logger.debug("Table name [" + tableName + "] is OK.");}

    // Путь к каталогу с данными сериализованной таблицы
    String tablePath = config.getPath();
    // Проверка переданного пути к каталогу - проверка существования каталога, проверка что это именно каталог,
    // проверка что каталог не пуст и содержит файлы.
    if (!new File(tablePath).exists())           {throw new IOException("Catalog [" + tablePath + "] doesn't exists!");}
    else if (!new File(tablePath).isDirectory()) {throw new IOException("Path [" + tablePath + "] not a catalog!");}
    else if (!FSUtils.containFiles(tablePath))   {throw new IOException("Catalog [" + tablePath + "] doesn't contain files!");}
    // Сообщим о том, что каталог все проверки прошел
    else {logger.debug("Catalog [" + tablePath + "] is OK. Processing.");}
    // Локальная копия параметра "путь к сериализованной таблице" (сразу корректируем отсутствие символа / в конце пути)
    String localTablePath = FSUtils.fixFPath(tablePath, true);
    
    // Ссылка на класс-монитор процесса обработки данных
    DBProcessingMonitor monitor = config.getMonitor();
    // Префикс для сообщений монитора. Если префикс не указан, то в качестве префикса используем
    // имя обрабатываемой таблицы.
    String monitorMessagePrefix;
    if (StringUtils.isBlank(config.getMonitorMsgPrefix())) {monitorMessagePrefix = "[" + tableName.toUpperCase() + "]";}
    else                                                   {monitorMessagePrefix= config.getMonitorMsgPrefix();}

    // Параметр - используется полный скрипт (true) или нет (false)
    boolean useFullScript = config.isUseFullScript();

    // Если таблица не подпадает под ограничения указанного соединения с СУБД - обрабатываем ее. Проверяются именно
    // ограничения БД, куда будут импортированы данные из 
    if (config.isTableAllowed(tableName))
     {
      logger.debug("Processing table import. Table [" + tableName + "]. Path [" + localTablePath + "]");
      // Если есть монитор - укажем обрабатываемую таблицу
      //if (monitor != null) {monitor.processMessage(tableName);}

      // Получаем список файлов и поочередно их (файлы) распаковываем (в цикле, см. ниже)
      File[] files = new File(localTablePath).listFiles();
      
      // Если список файлов не пуст, то начинаем обработку
      if ((files != null) && (files.length > 0))
       {
        logger.debug("Catalog [" + localTablePath + "] contain [" + files.length + "] objects. Processing.");

        // Общая конфигурация для выполнения sql-батча. Как полного, так и частичного. Конфигурация батча
        // создается на основе конфигурации сериализации. Непосредственно сам батч добавляется к конфигу далее.
        BatchConfig batchConfig = new BatchConfig(config);
        
        // Счетчик обрабатываемых файлов
        int processedFilesCounter = 1;
        // Непосредственно цикл обработки файлов в указанном каталоге
        for (File file : files)
         {
          // Сообщение монитору перед каждым обрабатываемым файлом. Даже отклоненный файл считается обработанным.
          if (monitor != null)
           {monitor.processMessage(monitorMessagePrefix + " [FILE: " +
             processedFilesCounter + " / " + files.length + "] [PROCESSING]");}

          // Если текущий файл не пуст ( != NULL), обрабатываем его
          if (file != null)
           {
            // Обрабатываем текущий файл только если это действительно файл и он существует
            if ((file.exists()) && (file.isFile()))
             {
              logger.debug("Processing [" + file.getAbsolutePath() + "]");
              // Непосредственно обработка текущего файла. Чтобы ошибка обработки одного файла не приводила к остановке
              // процесса применена конструкция try...catch
              try
               {
                // Список sql-запросов для выполнения для одного файла одной таблицы
                ArrayList<String> sqlBatch = SqlBatchBuilder.getBatchFromTableFile(config.getDbConfig(),
                 (localTablePath + file.getName()), config.isDeleteSource(), tableName, config.isUseSqlFilter());

                // Если используем метод частичных sql-батчей (параметр useFullScript=false), то полученный батч сразу
                // выполняем, если же формируем полный батч - то добавляем полученный батч (если он не пуст) к общему
                // батчу для выполнения после обработки всех файлов в каталоге с таблицей
                if ((sqlBatch != null) && (!sqlBatch.isEmpty()))
                 {
                  // Сообщим о том, что полученный для данного файла батч не пуст
                  logger.debug("Batch for table [" + tableName + "] in file [" + (localTablePath + file.getName()) + "] is not" +
                               " empty. Processing.");
                  // Если метод частичных батчей (useFullScript=false) - выполняем набор запросов сразу
                  if (!useFullScript)
                   {
                    logger.debug("USING PART SCRIPT METHOD. EXECUTING BATCH FOR FILE [" + file.getAbsolutePath() + "].");

                    // todo: данный отладочный вывод необходимо удалить в продакшн-версии
                    //for (String sql : sqlBatch) {logger.debug("-> " + sql);} // <- отладочный вывод (не для продакшена)

                    // Сначала добавляем в батч опцию выбора контекста БД
                    switch (config.getDbConfig().getDbType())
                     {
                      case INFORMIX:
                       //batchConfig.addSqlToBatch("DATABASE " + config.getDbConfig().getDbName());
                       break;
                      case MSSQL_NATIVE: case MSSQL_JTDS: case MYSQL:
                       //batchConfig.addSqlToBatch("USE " + config.getDbConfig().getDbName());
                       break;
                     }
                    // Если включена опция очистки таблицы перед загрузкой в нее данных (isClearTableBeforeLoad) -
                    // добавляем запрос для очистки таблицы
                    //if (config.isClearTableBeforeLoad()) {batchConfig.addSqlToBatch("DELETE FROM " + config.getDbConfig().getDbName());}
                    
                    // Если включена опция "SET IDENTITY_INSERT..." (useSetIdentityInsert), то необходимо выполнить sql-инструкцию
                    // по отключению контроля идентификаторов. Данная опция используется только сервера MS SQL (тестировалось
                    // с версией 2005). Также в конец батча необходимо добавить sql-инструкцию, включающую контроль идентификаторов.
                    DBConsts.DBType dbType = config.getDbConfig().getDbType();
                    logger.debug("-> " + dbType);
                    if (config.isUseSetIdentityInsert() &&
                     (DBConsts.DBType.MSSQL_JTDS.equals(dbType) || DBConsts.DBType.MSSQL_NATIVE.equals(dbType)))
                     {
                      logger.debug("DBMS type = MSSQL, useIdentityInsert = true. Processing.");
                      //batchConfig.addSqlToBatch("SET IDENTITY_INSERT " + tableName + " ON");
                     }
                    // Добавляем исполняемый батч к конфигурации
                    //batchConfig.addBatch(sqlBatch);
                    // Если опция IDENTITY_INSERT была включена - необходимо ее выключить
                    if (config.isUseSetIdentityInsert() &&
                     (DBConsts.DBType.MSSQL_JTDS.equals(dbType) || DBConsts.DBType.MSSQL_NATIVE.equals(dbType)))
                     {
                      logger.debug("DBMS type = MSSQL, useIdentityInsert = true. Processing.");
                      //batchConfig.addSqlToBatch("SET IDENTITY_INSERT " + tableName + " OFF");
                     }

                    //logger.debug("-> \n" + batchConfig.getBatch());
                    System.exit(0);
                    
                    // Меняем префикс сообщения монитора для батча
                    batchConfig.setMonitorMsgPrefix(monitorMessagePrefix + " [FILE " + processedFilesCounter + " / " + files.length + "]");
                     // Выполняем батч и получаем список ошибок, возникших в результате выполнения одного sql-батча (для
                    // одного файла). Многопоточность/однопоточность выполнения батча определяется конфигурацией для сериализации.
                    ArrayList<String> errors = SqlBatcher.execute(batchConfig);
                    // Если есть ошибки, то добавим их к результирующему списку
                    if ((errors != null) && (!errors.isEmpty()))
                     {
                      logger.warn("There are errors during processing part batch for table [" + tableName + "]. See log!");
                      if (errorsList == null) {errorsList = new ArrayList<String>();}
                      errorsList.addAll(errors);
                     }
                    // Если при обработке текущего батча данной таблицы не было ошибок - также сообщим об этом
                    else {logger.debug("Executing part batch for table [" + tableName + "] finished without errors.");}
                   }
                  
                  // Если же используем метод полного батча - формируем результирующий батч
                  else
                   {
                    logger.debug("USING FULL SCRIPT METHOD. ADDING BATCH FOR FILE [" + file.getAbsolutePath() + "] TO RESULT SQL-BATCH.");
                    // Если полный батч еще не инициализирован - инициализация
                    if (fullSqlBatch == null) {fullSqlBatch = new ArrayList<String>();}
                    // Добавляем в полный батч инфу из батча текущего файла
                    fullSqlBatch.addAll(sqlBatch);
                   }
                 }
                // Если же полученный батч пуст - сообщим в лог
                else {logger.warn("Batch for table [" + tableName + "] in file [" + (localTablePath + file.getName()) + "] is empty!");}
                
               }
              // Все возникающие ошибки добавляем в результирующий список
              catch (IOException e)
               {if (errorsList == null) {errorsList = new ArrayList<String>();} errorsList.add(e.getMessage());}
              catch (SQLException e)
               {if (errorsList == null) {errorsList = new ArrayList<String>();} errorsList.add(e.getMessage());}
              catch (DBModuleConfigException e)
               {if (errorsList == null) {errorsList = new ArrayList<String>();} errorsList.add(e.getMessage());}
              catch (DBModelException e)
               {if (errorsList == null) {errorsList = new ArrayList<String>();} errorsList.add(e.getMessage());}
             }
           }
          // Если текущий файл оказался не файлом (каталогом и т.п.), то не обрабатываем его
          else {logger.warn("Object [" + file.getAbsolutePath() + "] doesn't exists or not a file! Skipped!");}

          // Увеличиваем счетчик обработанных файлов. Счетчик увеличиваем вне зависимости от того был обработан файл или нет -
          // т.е. если файл был пропущен, то счетчик все равно увеличим (даже пропуск файла квалифицируется как его обработка).
          processedFilesCounter++;
          
         } // END OF FOR CYCLE (конец обработки всех файлов в каталоге)

         // Если используется метод "полного" скрипта (генерится большой sql-batch для всех файлов), то теперь
         // необходимо выполнить полученный скрипт
         if (useFullScript)
          {
           logger.debug("USING FULL SCRIPT METHOD. EXECUTING FULL SQL-BATCH.");
           // Если полный sql-batch не пуст, выполняем его
           if ((fullSqlBatch != null) && (!fullSqlBatch.isEmpty()))
            {
             logger.debug("Full sql-batch not empty. Executing.");
             // Добавляем исполняемый батч к конфигурации
             //batchConfig.setBatch(fullSqlBatch);
             // Выполняем батч и получаем список ошибок, возникших в результате выполнения одного sql-батча (для одного
             // файла). Многопоточность/однопоточность выполнения батча о пределяется конфигурацией для сериализации.
             ArrayList<String> errors = SqlBatcher.execute(batchConfig);
             // Если есть ошибки, то добавим их к результирующему списку
             if ((errors != null) && (!errors.isEmpty()))
              {
               logger.warn("There are errors during executing full batch for table [" + tableName + "]. See log!");
               if (errorsList == null) {errorsList = new ArrayList<String>();}
               errorsList.addAll(errors);
              }
             // Если при обработке данной таблицы не было ошибок - также сообщим об этом
             else {logger.debug("Executing full batch for table [" + tableName + "] finished without errors.");}
            }
           // Если же полный sql-batch оказался пустым, ничего не делаем (просто сообщим в лог)
           else {logger.warn("Full sql-batch is empty! Nothing to execute!");}
          }

       }
      // Если полученный список файлов пуст 
      else {logger.warn("File list is empty (no files in [" + localTablePath + "])!");}
      // Сообщим об окончании обработки текущей таблицы (вне зависимости от успешности этой обработки)
      logger.debug("Table [" + tableName + "] processed successfully. See log for results.");
     }
    // Если текущая таблица подпадает под ограничения - наффик!
    else {logger.warn("Current table [" + tableName + "] is deprecated or not allowed! Skipping.");}
    // Возвращаем список ИС, возникших при обработке данных текущей таблицы
    return errorsList;
   }

  /**
   * Метод для тестирования и отладки данного класса.
   * @param args String[] параметры метода main. 
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger("jdb");
    InitLogger.initLogger("jlib", Level.INFO);
    Logger logger = Logger.getLogger("jdb");

    DBConfig mysqlConfig1 = new DBConfig();
    mysqlConfig1.setDbType(DBConsts.DBType.MYSQL);
    mysqlConfig1.setHost("localhost:3306");
    mysqlConfig1.setDbName("storm");
    mysqlConfig1.setUser("root");
    mysqlConfig1.setPassword("mysql");
    mysqlConfig1.addAllowedTable("items");

    DBConfig mssqlConfig = new DBConfig();
    mssqlConfig.setDbType(DBConsts.DBType.MSSQL_JTDS);
    mssqlConfig.setHost("APP");
    mssqlConfig.setDbName("norm_docs");
    mssqlConfig.setUser("sa");
    mssqlConfig.setPassword("adminsql245#I");

    try
     {
      //DataChanger.cleanupTable(mssqlConfig1, "items");
      DBLoaderConfig config = new DBLoaderConfig();
      config.setDbConfig(mssqlConfig);
      config.setDeleteSource(false);
      //config.setClearTableBeforeLoad(true);
      //config.setMultiThreads(true);
      //config.setDbmsConnNumber(10);

      //config.setPath("c:\\temp\\norm_docs\\docTypes");
      //TableLoadCore.load(config, "doctypes");
      //config.setPath("c:\\temp\\norm_docs\\norm_docs");
      //TableLoadCore.load(config, "norm_docs");
      
      config.setPath("c:\\temp\\norm_docs\\norm_docs_parts");
      TableLoadCore.load(config, "norm_docs_parts");
      
      //config.setPath("c:\\temp\\norm_docs\\changes_journal");
      //TableLoadCore.load(config, "changes_journal");
      //config.setPath("c:\\temp\\norm_docs\\files");
      //TableLoadCore.load(config, "files");

      //SerializationConfig mysqlSerConfig = new SerializationConfig();
      //mysqlSerConfig.setDbConfig(mysqlConfig1);
      //mysqlSerConfig.setPath("c:\\temp\\storm");
      //mysqlSerConfig.setDeleteSource(false);

      // Загружаем таблицу в другую БД
      //DBDeserializer.deserializeDB(mysqlSerConfig);
     }
    catch (DBConnectionException e) {logger.error(e.getMessage());}
    catch (IOException e) {logger.error(e.getMessage());}
    //catch (DBModelException e) {logger.error(e.getMessage());}
    catch (SQLException e) {logger.error(e.getMessage());}
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}

   }

 }