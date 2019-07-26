package jdb.nextGen;

import dgusev.dbpilot.DBConsts;
import dgusev.dbpilot.config.DBConfig;
import dgusev.io.MyIOUtils;
import gusev.dmitry.utils.MyCommonUtils;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.monitoring.DBProcessingMonitor;
import jdb.nextGen.exceptions.JdbException;
import jdb.nextGen.models.SimpleDBIntegrityModel;
import jdb.nextGen.models.SimpleDBTimedModel;
import jdb.nextGen.serialization.Descriptor;
import jdb.nextGen.serialization.SerializableResultSet;
import jdb.utils.DBUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Модуль-ядро системы вызгрузки/загрузки БД на диск/с диска. Содержит основные методы, выполняющие обработку данных.
 * Почти все методы объявлены как private (ядро системы загрузки/выгрузки), некоторые объявлены как protected - эти
 * методы являют собой интерфейс ядра загрузки/выгрузки. Интерфейс используется только текущей реализацией данного модуля.
 * @author Gusev Dmitry (Гусев Дмитрий)
 * @version 6.0 (DATE: 18.05.11)
*/

// todo: все пути пропускать через fixFPath() и все имена каталогов переводить в ВЕРХНИЙ регистр символов (toUpperCase())

@SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
public final class DBasesLoaderCore
 {
  /** Тип-перечисление объектов выгрузки БД на диск - это может быть таблица или БД целиком (всего 2 элемента). */
  public static enum ObjectType {TABLE, DATABASE}

  /** Логгер модуля. */
  private static Logger logger = Logger.getLogger(DBasesLoaderCore.class.getName());

  // Предотвращаем инстанцирование и наследование, т.к. класс утилитарный
  private DBasesLoaderCore() {}

  /**
   * Стандартная длина имени файла с выгруженными из БД данными. К этой длине приводятся все имена выгружаемых файлов -
   * например имя 12 приводится к 0000000012 (при значении данного параметра = 10).
   * Не рекомендую ставить слишком большие значения - это скажется на быстродействии системы.
  */
  private static final int    FILE_NAME_LENGTH       = 10;
  /**
   * Указанным ниже символом дополняется имя файла до необходимой длины (дополняется в начало файла). Этот символ - ноль.
   * Другие символы (особенно цифры) ставить не надо - это нарушит порядок сортировки файлов в файловой системе и нарушит
   * порядок их (файлов) обработки методами загрузки данных из файлов в БД.
  */
  private static final char   FILE_NAME_FILL_SYMBOL = '0';
  /** Имя файла-дескриптора для сериализованной таблицы или БД. */
  private static final String DESCRIPTOR_FILE_NAME  = "descriptor";
  /** Расширение файла-дескриптора для сериализованной таблицы. */
  private static final String EXTENSION_TABLE       = "tbl";
  /** Расширение файла-дескриптора для сериализованной БД. */
  private static final String EXTENSION_DB          = "db";

  /**
   * Метод выполняет выгрузку указанной таблицы на диск.
   * @param conn Connection
   * @param path String
   * @param tableName String
   * @param lowerTimestamp Timestamp
   * @param upperTimestamp Timestamp
   * @param keysList ArrayList[Integer]
   * @return boolean возвращается значение ИСТИНА/ЛОЖЬ в зависимости от результата выгрузки таблицы на диск. Если
   * хоть какие-то данные были выгружены (хоть одна строка таблицы), метод вернет значение ИСТИНА, если же данные
   * выгружены не были - метод вернет значение ЛОЖЬ.
   * @throws JdbException ИС
  */
  private static boolean unloadTableToDisk(Connection conn, String path, String tableName, Timestamp lowerTimestamp,
   Timestamp upperTimestamp, ArrayList<Integer> keysList) throws JdbException
   {
    boolean result = false;
    logger.debug("DBasesLoaderCore.unloadTableToDisk().");
    // Проверка соединения с СУБД
    if (conn != null)
     {
      // Проверка имени таблицы
      if (!StringUtils.isBlank(tableName))
       {
        // Проверка каталога назначения
        if (!StringUtils.isBlank(path) && new File(path).exists() && new File(path).isDirectory())
         {
          // Проверки пройдены - непосредственно выполнение действий
          StringBuilder sql = new StringBuilder("select * from ").append(tableName);
          // Добавляем в запрос список ключей, в который должны входить выбираемые из таблицы ключи
          if ((keysList != null) && (!keysList.isEmpty()))
           {
            sql.append(" where ").append(DBConsts.FIELD_NAME_KEY).append("in (");
            sql.append(MyCommonUtils.getCSVFromArrayList(keysList)).append(")");
           }
          // Добавляем в запрос нижнюю границу по времени (по таймштампу)
          if (lowerTimestamp != null)
           {
            logger.debug("Lower timestamp for table [" + tableName + "]: " + lowerTimestamp);
            if ((keysList != null) && (!keysList.isEmpty())) {sql.append(" and ");}
            else                                             {sql.append(" where ");}
            sql.append(DBConsts.FIELD_NAME_TIMESTAMP).append(" >= ?");
           }
          else {logger.debug("There is no lower timestamp for table [" + tableName + "].");}
          // Добавляем в запрос верхнюю границу по времени (по таймштампу)
          if (upperTimestamp != null)
           {
            logger.debug("Upper timestamp for table [" + tableName + "]: " + upperTimestamp);
            if (((keysList != null) && (!keysList.isEmpty())) || (lowerTimestamp != null)) {sql.append(" and ");}
            else                                                                           {sql.append(" where ");}
            sql.append(DBConsts.FIELD_NAME_TIMESTAMP).append(" <= ?");
           }
          else {logger.debug("There is no upper timestamp for table [" + tableName + "].");}

          // Сортировка по таймштампу (выполняется всегда!)
          sql.append(" order by ").append(DBConsts.FIELD_NAME_TIMESTAMP);

          // Отладочный вывод
          logger.debug("Generated SQL: [" + sql.toString() + "]");
          // Выполнение запроса и получение данных
          PreparedStatement stmt = null;
          ResultSet         rs   = null;
          // Конструкция try-catch-finally необходима для того, чтобы возможно было в блоке finally освободить ресурсы.
          // Также все возникшие ИС оборачиваются в ИС JdbException - для того, чтобы вызывающий код получил одну ИС и,
          // перехватив ее мог удалить каталог с данными неудавшейся сериализации таблицы.
          try
           {
            stmt = conn.prepareStatement(sql.toString());
            // Добавление таймштампов в преподготовленный запрос. Номера устанавливаемых параметров варьируются
            // в зависимости от их (параметров) количества.
            if (lowerTimestamp != null)
             {
              stmt.setTimestamp(1, lowerTimestamp);
              if (upperTimestamp != null) {stmt.setTimestamp(2, upperTimestamp);}
             }
            else if (upperTimestamp != null) {stmt.setTimestamp(1, upperTimestamp);}

            rs = stmt.executeQuery();
            logger.debug("Data from DB received! Processing it.");

            //*** Выгрузка и сериализация курсора данных частями. Перед выгрузкой данных оператор rs.next() не выполняем,
            // т.к. это сдвигает указатель в курсоре на 1 позицию, а в конструкторе класса SerializableResultSet указатель
            // также сдвигается на 1 позицию (оператором rs.next()) и возникает ошибка (пропускаем одну строку из курсора).
            boolean iterationFlag = true;
            int counter = 1;
            // Дескриптор (описатель) для сериализуемой таблицы
            Descriptor descriptor = null;
            do
             {
              SerializableResultSet srs = new SerializableResultSet(rs, tableName, DBConsts.SERIALIZATION_TABLE_FRACTION);
              // Если в результате обработки получен непустой SRS - записываем его на диск
              if ((srs != null) && !srs.isEmpty())
               {
                String fileName = MyCommonUtils.getFixedLengthName(FILE_NAME_LENGTH, FILE_NAME_FILL_SYMBOL, String.valueOf(counter));
                MyIOUtils.serializeObject(srs, path, fileName, EXTENSION_TABLE, false);
                // Запись данных в дескриптор выгружаемой таблицы
                if (descriptor == null) {descriptor = new Descriptor(tableName, DBasesLoaderCore.ObjectType.TABLE);}
                descriptor.addItem(fileName + "." + EXTENSION_TABLE);
                // Т.к. произошла выгрузка данных на диск - результат выгрузки таблицы - положителен!
                if (!result) {result = true;}
                // Увеличение счетчика выгружаемых файлов
                counter++;
                // todo: Глубокая отладка. РЕАЛЬНО записанный файл на диск. В продакшене можно удалить!
                logger.debug("Writed file [" + MyIOUtils.fixFPath(path, true) + fileName + "." + EXTENSION_TABLE + "].");
               }
              // Если последний выгруженный SRS пуст - итерации прекращаем!
              else {iterationFlag = false;}
             }
            while(iterationFlag);
            // После выполнения всех операций с данными - записываем на диск дескриптор сериализованной таблицы.
            // Дескриптор записывается только в том случае, если он был проинициализирован, т.е. если были
            // записаны на диск какие-либо данные.
            if (descriptor != null) {MyIOUtils.serializeObject(descriptor, path, DESCRIPTOR_FILE_NAME, EXTENSION_TABLE, false);}
            // Если дескриптор не инициализирован, то просто сообщим в лог, что ничего не выгружено
            else {logger.info("No data was serialized for table [" + tableName + "].");}
           }
          // Перехватываемые ИС обертываются в одну - JdbException. Зачем? См. коммент *** выше.
          catch (SQLException e)         {throw new JdbException(e);}
          catch (IOException e)          {throw new JdbException(e);}
          // Освобождение ресурсов
          finally
           {
            try {if (rs != null)   {rs.close();} if (stmt != null) {stmt.close();}}
            catch (SQLException e) {logger.error("Can't free resources! Message: [" + e.getMessage() + "].");}
           }
         }
        else {throw new JdbException("Path [" + path + "] is empty, not exists or not a directory!");}
       }
      else {throw new JdbException("Empty table name [" + tableName + "]!");}
     }
    else {throw new JdbException("Empty db connection!");}
    // Возвращение результата выгрузки таблицы на диск
    return result;
   }

   /**
   *
   * @return boolean возвращается значение ИСТИНА/ЛОЖЬ в зависимости от результата выгрузки БД на диск. Если
   * хоть какие-то данные были выгружены (хоть одна строка любой таблицы), метод вернет значение ИСТИНА, если же данные
   * выгружены не были - метод вернет значение ЛОЖЬ.
  */
  protected static boolean unloadDBToDisk(Connection conn, String path, String dbName, ArrayList<String> tablesList,
   SimpleDBTimedModel timedModel, SimpleDBIntegrityModel integrityModel) throws JdbException
   {
    boolean result = false;
    logger.debug("DBasesLoaderCore.unloadDBToDisk().");
    // Проверка соединения с СУБД
    if (conn != null)
     {
      // Проверяем список таблиц (должен быть не пуст)
      if (!StringUtils.isBlank(dbName) && (tablesList != null) && (!tablesList.isEmpty()))
       {
        // Обработка каталога. Путь должен быть не пустым, существовать и быть именно каталогом.
        if (!StringUtils.isBlank(path) && (new File(path).exists()) && (new File(path).isDirectory()))
         {
          // После обработки каталога назначения - работаем далее. Создаем дескриптор БД и в цикле
          // обрабатываем весь полученный список таблиц
          Descriptor descriptor = null;
          for (String tableName : tablesList)
           {
            if (!StringUtils.isBlank(tableName))
             {
              // Создаем каталог для выгрузки конкретной таблицы. Если создание каталога не
              // удалось - возбуждается ИС и обработка прерывается
              if (new File(MyIOUtils.fixFPath(path, true) + tableName.toUpperCase()).mkdir())
               {
                // Получаем таймштамп для данной таблицы
                Timestamp timestamp = null;
                if (timedModel != null) {timestamp = timedModel.getTimestampForTable(tableName);}
                // Получаем список ключей для выгрузки из данной таблицы
                ArrayList<Integer> keysList = null;
                if (integrityModel != null) {keysList = integrityModel.getKeysListForTable(tableName);}
                // Конструкция try-catch - ошибки при выгрузке одной таблицы не прерывают обработку всего списка.
                // Также при обработке таблицы и возникновении ошибки ИС перехватывается и удаляется каталог
                // с "недосериализованной" таблицей (в методе сериализации одной таблицы сделано обертывание всех ИС
                // в одну - JdbException для удобства обработки в данном модуле - для удаления каталога с ошибочно
                // сериализованной таблицей).
                try
                 {
                  // Непосредственно выгрузка таблицы на диск
                  boolean unloadTableResult = DBasesLoaderCore.unloadTableToDisk(conn, MyIOUtils.fixFPath(path, true) + tableName,
                                               tableName, timestamp, null, keysList);

                  // Инициализация дескриптора и запись в него выгруженной таблицы, только если что-то РЕАЛЬНО было выгружено
                  if (unloadTableResult)
                   {
                    if (descriptor == null) {descriptor = new Descriptor(dbName, DBasesLoaderCore.ObjectType.DATABASE);}
                    descriptor.addItem(tableName);
                    // Т.к. произошла выгрузка (удачная) таблицы - хотя бы одной - результат выгрузки БД положителен
                    if (!result) {result = true;}
                   }
                  // Если же ничего выгружено не было - удалим созданный для выгрузки таблицы пустой каталог
                  else
                   {
                    // Очистка после пустой выгрузки таблицы на диск (удаление каталога)
                    MyIOUtils.delTree(MyIOUtils.fixFPath(path, true) + tableName.toUpperCase());
                   }
                 }
                catch (JdbException e)
                 {
                  // Очистка после неудачной выгрузки таблицы на диск
                  MyIOUtils.delTree(MyIOUtils.fixFPath(path, true) + tableName.toUpperCase());
                  // Вывод сообщения ИС.
                  logger.error("Error processing table [" + tableName + "]! Message [" + e.getMessage() + "].");
                 }
               }
              else {logger.error("Can't create catalog [" + (MyIOUtils.fixFPath(path, true) + tableName) + "] for " +
                                 "table [" + tableName + "]! Table skipped.");}
             }
            // Пустое имя таблицы - сообщим в лог (WARN)
            else {logger.warn("Empty table name! Programmer error?");}

           } // END OF FOR

          // По окончании цикла обработки данных - запись дескриптора на диск (если он был проинициализирован)
          if (descriptor != null)
           {
            // Конструкция try-catch и "обертывание" ИС в JdbException необходимы для того, чтобы вызывающий код мог
            // корректно очистить диск после неудачной выгрузки БД - удалить папку, созданную для выгрузки БД.
            // (в противном случае пришлось бы писать catch() для каждой ИС и в каждом модуле были бы одни и те же действия)
            try {MyIOUtils.serializeObject(descriptor, path, DESCRIPTOR_FILE_NAME, EXTENSION_DB, false);}
            catch (IOException e)          {throw new JdbException("Can't serialize db descriptor!", e);}
           }
          // Если дескриптор проинициализирован не был - это означает, что никакие данные вообще не были выгружены.
          // Сообщим об этом в лог.
          else {logger.info("No data was serialized for DB [" + dbName + "].");}
         }
        // Ошибочный путь для выгрузки
        else {throw new JdbException("Path [" + path + "] is empty, not exists or not a directory!");}
       }
      // Ошибочное имя БД или пустой список таблиц
      else {throw new JdbException("Empty db name [" + dbName + "] or tables list [" + tablesList + "]!");}
     }
    // Пустое соединение с СУБД
    else {throw new JdbException("Empty db connection!");}
    // Возврат результата
    return result;
   }

  /**
   * Загрузка сериализуемого курсора данных (SerializableResultSet) в таблицу БД.
   * @param conn Connection соединение с СУБД.
   * @param srs SerializableResultSet непосредственно сериализуемый курсор для записи в БД.
   * @param tableName String имя таблицы, куда надо вставить данные - оно заменяет имя таблицы, указанное в
   * сериализуемом курсоре, т.о. обеспечивая гибкость модуля.
  */
  private static void loadSrsToDB(Connection conn, SerializableResultSet srs, String tableName) throws JdbException, SQLException
   {
    // Проверяем соединение с СУБД
    if (conn != null)
     {
      // Проверяем полученный SRS
      if ((srs != null) && (!srs.isEmpty()))
       {
        // Запросы (update, insert и запрос для проверки существования записи - select)
        String        preparedInsert = srs.getPreparedInsertSql(tableName);
        String        preparedUpdate = srs.getPreparedUpdateSql(tableName);
        StringBuilder checkRecord    = new StringBuilder("select ").append(DBConsts.FIELD_NAME_KEY).append(" from ");
        // Если указано имя таблицы - используем его, если же не указано - используем имя таблицы из SerializableResultSet
        if (!StringUtils.isBlank(tableName)) {checkRecord.append(tableName);}
        else                                 {checkRecord.append(srs.getTableName());}
        checkRecord.append(" where ").append(DBConsts.FIELD_NAME_KEY).append(" = ?");

        // todo: Вывод для глубокой отладки
        //logger.debug("INSERT -> " + preparedInsert);
        //logger.debug("UPDATE -> " + preparedUpdate);
        //logger.debug("CHECK  -> " + checkRecord);

        // Statement-объекты
        PreparedStatement insertStatement = conn.prepareStatement(preparedInsert);
        PreparedStatement updateStatement = conn.prepareStatement(preparedUpdate);
        PreparedStatement checkRecordStatement = conn.prepareStatement(checkRecord.toString());
        // Получим и запомним значение индекса ключевого поля в курсоре данных
        int keyFieldIndex = srs.getKeyFieldIndex();
        // Получаем данные из курсора
        ArrayList<ArrayList<String>> data = srs.getData();
        // Обрабатываем данные и заносим их в БД
        for (ArrayList<String> row : data)
         {
          // Проверка существования записи
          checkRecordStatement.setObject(1, row.get(keyFieldIndex));
          ResultSet rs = checkRecordStatement.executeQuery();
          // Запись с данным ключом существует - формируем оператор UPDATE (данные)
          if (rs.next())
           {
            // logger.debug("[" + row.get(keyFieldIndex) + "] -> UPDATE"); // <- ТОЛЬКО ДЛЯ ОТЛАДКИ!
            int j = 0;
            // Основные данные
            for (int i = 1; i < row.size(); i++)
             {
              if (j == keyFieldIndex) {j++;}
              updateStatement.setObject(i, row.get(j));
              j++;
             }
            // Значение ключевого поля
            updateStatement.setObject(row.size(), row.get(keyFieldIndex));
            // Полученный запрос добавляем в батч
            updateStatement.addBatch();
           }
          // Запись с данным ключом не существует - формируем оператор INSERT (данные)
          else
           {
            // logger.debug("[" + row.get(keyFieldIndex) + "] -> INSERT"); // <- ТОЛЬКО ДЛЯ ОТЛАДКИ!
            for (int i = 1; i <= row.size(); i++) {insertStatement.setObject(i, row.get(i - 1));}
            insertStatement.addBatch();
           }
         }
        // Запуск сгенерированных батчей
        logger.debug("DBasesLoaderCore.loadSrsToDB(): executing updates batch.");
        updateStatement.executeBatch();
        logger.debug("DBasesLoaderCore.loadSrsToDB(): executing inserts batch.");
        insertStatement.executeBatch();
       }
      // Получен пустой SRS
      else {throw new JdbException("Empty SerializableResultSet object instance!");}
     }
    // Соединение с СУБД пусто
    else {throw new JdbException("Empty DBMS connection!");}
   }

  /***/
  private static void loadTableFromDisk(Connection conn, String path, String tableName,
   boolean useTableNameCheck, DBProcessingMonitor monitor)
   throws JdbException, ClassNotFoundException, IOException, SQLException
   {
    logger.debug("DBasesLoaderCore.loadTableFromDisk().");
    // Проверка соединения с СУБД
    if (conn != null)
     {
      // Проверка имени таблицы - оно должно быть непустым
      if (!StringUtils.isBlank(tableName))
       {
        // Проверка каталога назначения (должен существовать и быть каталогом)
        if (!StringUtils.isBlank(path) && new File(path).exists() && new File(path).isDirectory())
         {
          // Вначале - из каталога загружаем дескриптор таблицы. Если не получилось - возбуждаем ИС.
          String descriptorPath = MyIOUtils.fixFPath(path, true) + DESCRIPTOR_FILE_NAME + "." + EXTENSION_TABLE;
          Descriptor tableDescriptor = (Descriptor) MyIOUtils.deserializeObject(descriptorPath, false, false);

          // После успешного прочтения дескриптора - проверяем имя таблицы и тип объекта
          if ((ObjectType.TABLE.equals(tableDescriptor.getObjectType()))
               && (!useTableNameCheck || tableName.toUpperCase().equals(tableDescriptor.getObjectName())))
           {
            // Дескриптор прочитан - обрабатываем файлы, перечисленные в нем
            if (!tableDescriptor.isEmpty())
             {
              ArrayList<String> tableFiles = tableDescriptor.getObjectItems();
              // Счетчик количества загруженных/обработанных файлов
              int processedFilesCounter = 0;
              // Сообщение монитору об обрабатываемом в данный момент файле (начало обработки)
              if (monitor != null) {monitor.processMessage("[" + tableName + "] [0 / " + tableFiles.size() + "]");}

              // Проходим по списку объектов дескриптора и загружаем их в БД
              for (String tableFile : tableFiles)
               {
                // Ошибка при загрузке одного файла данных ведет к полному останову загрузки данной таблицы (т.е. мы не
                // перехватываем ИС при десериализации объекта SRS). При этом, т.к. данные отсортированы (должны быть!)
                // по таймштампу, то при обрыве загрузки все последующие файлы и данные (для данной таблицы) загружены не
                // будут и, соотв., не должно возникать "дыр" в данных!
                SerializableResultSet srs =
                 (SerializableResultSet)MyIOUtils.deserializeObject(MyIOUtils.fixFPath(path, true) + tableFile, false, false);
                // Если прочитанный SRS не пуст и имя таблицы совпадает с указанным - загружаем его (SRS) в БД
                if ((!srs.isEmpty()) && (!useTableNameCheck || tableName.toUpperCase().equals(srs.getTableName())))
                 {
                  logger.debug("Data ok in file: [" + (MyIOUtils.fixFPath(path, true) + tableFile) + "]. Rows: " + srs.getRowsCount());
                  DBasesLoaderCore.loadSrsToDB(conn, srs, tableName);
                  // После УСПЕШНОЙ загрузки данных из очередного файла выполняем COMMIT - запись изменений в БД
                  conn.commit();
                 }
                // Прочитанный SRS пуст или имя таблицы в нем не совпадает с указанным - возбуждается ИС!
                // Соответственно обработка - прерывается.
                else
                 {
                  throw new JdbException("SRS in file [" + (MyIOUtils.fixFPath(path, true) + tableFile) +
                   "] is empty or invalid table name [" + srs.getTableName() + "] (must be: " + tableName + ")!");
                 }
                // Увеличим счетчик кол-ва обработанных файлов
                processedFilesCounter++;
                // Сообщение монитору об обрабатываемом в данный момент файле
                if (monitor != null)
                 {monitor.processMessage("[" + tableName + "]  [" + processedFilesCounter + " / " + tableFiles.size() + "]");}
               } // END OF FOR
             }
            // Пустой дескриптор - нет списка объектов
            else {throw new JdbException("Empty table descriptor (no objects in list)!");}
           }
          // Неверный тип объекта или неверное имя таблицы (не совпало)
          else
           {throw new JdbException("Invalid object type [" + tableDescriptor.getObjectType() + "] (must be: " +
             ObjectType.TABLE + ") or table name [" + tableDescriptor.getObjectName() + "] (must be: " +
             tableName.toUpperCase() + ")!");}
         }
        // Неверный путь к файлам таблицы
        else {throw new JdbException("Path [" + path + "] is empty, not exists or not a directory!");}
       }
      // Указано пустое имя таблицы
      else {throw new JdbException("Empty table name!");}
     }
    // Соединение с СУБД пусто
    else {throw new JdbException("Empty DBMS connection!");}
   }

  /**
   *
   * @param useIdentityInsert boolean включить (ИСТИНА) или выключить (ЛОЖЬ) выполнение инструкций
   * SET IDENTITY_INSERT ... ON|OFF перед|после загрузки очередной таблицы с диска. Опция имеет смысл только для
   * СУБД MS SQL - включение данной опции для других СУБД вызовет ошибку выполнения данного метода.
  */
  protected static void loadDBFromDisk(Connection conn, String path, ArrayList<String> tablesList,
   DBProcessingMonitor monitor, boolean useIdentityInsert)
   throws JdbException, ClassNotFoundException, IOException, SQLException
   {
    logger.debug("DBasesLoaderCore.loadDBFromDisk().");
    // Проверка соединения с СУБД
    if (conn != null)
     {
      // Проверка каталога, из которого загружаем БД (должен существовать, быть каталогом и быть непустым)
      if (!StringUtils.isBlank(path) && new File(path).exists() && new File(path).isDirectory() && !MyIOUtils.isEmptyDir(path))
       {
        // Проверяем список таблиц (должен быть не пуст)
        if ((tablesList != null) && (!tablesList.isEmpty()))
         {
          // Вначале - из каталога загружаем дескриптор БД. Если не получилось - возбуждаем ИС.
          String descriptorPath = MyIOUtils.fixFPath(path, true) + DESCRIPTOR_FILE_NAME + "." + EXTENSION_DB;
          // Ошибка при загрузке дескриптора БД приводит к останову. Мы не перехватываем ИС при десериализации объекта SRS.
          Descriptor dbDescriptor = (Descriptor) MyIOUtils.deserializeObject(descriptorPath, false, false);

          // После успешного прочтения дескриптора - проверяем тип объекта. Имя БД в данной версии не проверяется, т.е.
          // данные возможно загружать в любую БД - необходимо лишь наличие в ней (в БД) соотв. таблиц.
          if (ObjectType.DATABASE.equals(dbDescriptor.getObjectType()))
           {
            // Дескриптор прочитан - выполняем загрузку дальше
            if (!dbDescriptor.isEmpty())
             {
              // Отключаем автокоммит запросов в БД - это очень сильно влияет на скорость работы (положительно -
              // скорость обработки запросов возрастает в разы!). Если возникла ИС - обработка прерывается, т.к.
              // если значение autocommit=false не удалось установить, то обработка будет слишком длительной.
              conn.setAutoCommit(false);

              // Счетчик обработанных таблиц
              int progressTablesCounter = 0;
              //
              double onePercent = 100/tablesList.size();
              //int progress;

              // Проходим по указанному списку таблиц и если для таблицы есть запись в дескрипторе и непустой каталог
              // на диске - пытаемся загрузить таблицу с диска в БД
              for (String table : tablesList)
               {
                // Если соблюдены все условия - загружаем таблицу с диска
                if ((!StringUtils.isBlank(table) && dbDescriptor.containsItem(table)))
                 {
                  // Сообщение об обрабатываемой таблице классу-монитору
                  if (monitor != null) {monitor.processMessage("[" + table + "]");}

                  String tablePath = MyIOUtils.fixFPath(path, true) + table.toUpperCase();
                  if (new File(tablePath).exists() && new File(tablePath).isDirectory() && !MyIOUtils.isEmptyDir(tablePath))
                   {
                    // Конструкция try-catch применена для того, чтобы ошибка загрузки одной таблицы не оборвал загрузку
                    // всей БД в целом. Возникающие ИС не оборачиваем в свою ИС (JdbException) - это будет делать вызывающий
                    // метод.
                    try
                     {
                      // Выполнение инструкции SET IDENTITY_INSERT ... и отключение триггеров таблицы
                      if (useIdentityInsert)
                       {
                        // Разрешаем вставку в поля с автоинкрементным идентификатором (только для MS SQL 2005+)
                        conn.createStatement().executeUpdate("SET IDENTITY_INSERT " + table + " ON");
                        // Отключаем все триггеры таблицы, куда загружаем данные (только для MS SQL 2005+)
                        conn.createStatement().executeUpdate("ALTER TABLE " + table + " DISABLE TRIGGER ALL");
                       }
                      // Непосредственно загрузка таблицы с диска в БД. Контроль совпадения указанного имени таблицы с
                      // записанным в дескрипторе всегда включен.
                      DBasesLoaderCore.loadTableFromDisk(conn, tablePath, table, true, monitor);
                     }
                    // Перехват ИС для того, чтобы не оборвать загрузку БД.
                    catch (JdbException e)           {logger.error("Error loading table [" + table + "]! Message: " + e.getMessage());}
                    catch (ClassNotFoundException e) {logger.error("Error loading table [" + table + "]! Message: " + e.getMessage());}
                    catch (IOException e)            {logger.error("Error loading table [" + table + "]! Message: " + e.getMessage());}
                    catch (SQLException e)           {logger.error("Error loading table [" + table + "]! Message: " + e.getMessage());}
                    // Выполнение инструкции SET IDENTITY_INSERT [table] OFF
                    finally
                     {
                      // Выполнение инструкции SET IDENTITY_INSERT ...
                      if (useIdentityInsert)
                       {
                        try
                         {
                          // Разрешаем вставку в поля с автоинкрементным идентификатором (только для MS SQL 2005+)
                          conn.createStatement().executeUpdate("SET IDENTITY_INSERT " + table + " OFF");
                          // Включаем все триггеры таблицы, куда загрузили данные (только для MS SQL 2005+)
                          conn.createStatement().executeUpdate("ALTER TABLE " + table + " ENABLE TRIGGER ALL");
                         }
                        catch (SQLException e)
                         {logger.error("Can't execute query [SET IDENTITY_INSERT " + table + " OFF]! Reason: " + e.getMessage());}
                       }
                     }
                   }
                  // Путь не существует, не является каталогом или каталог пуст. Ошибка записывается в лог,
                  // обработка всей БД не прерывается.
                  else {logger.error("Table [" + table + "] skipped! Reason: table path [" + tablePath +
                          "] not exists, not a directory or is empty!");}
                 }
                // Имя таблицы из списка пусто или не содержится в дескрипторе БД. Ошибка записывается в лог,
                // обработка всей БД не прерывается.
                else {logger.error("Table [" + table + "] skipped! Reason: table name is empty or not present in db descriptor!");}

                // Приращение прогресса выполнения после обработки очередной таблицы
                progressTablesCounter++;
                // Установка значения прогресса для класса-монитора
                if (monitor != null) {monitor.processProgress((int) (progressTablesCounter*onePercent));}
               } // END OF FOR
              // По окончании обработки таблиц установим значение прогресса в 100%
              if (monitor != null) {monitor.processProgress(100);}
             }
            // Пустой дескриптор - в нем нет списка объектов
            else {throw new JdbException("Empty db descriptor (no objects in list)!");}
           }
          // Неверный тип объекта
          else
           {throw new JdbException("Invalid object type [" + dbDescriptor.getObjectType() +
                                   "] (must be: " + ObjectType.DATABASE + ")!");}
         }
        // Указанный список таблиц для загрузки пуст
        else {throw new JdbException("Empty tables list [" + tablesList + "]!");}
       }
      // Неверный путь к файлам таблицы
      else {throw new JdbException("Path [" + path + "] is empty, or not exists, or not a directory, or is empty directory!");}
     }
    // Соединение с СУБД пусто
    else {throw new JdbException("Empty DBMS connection!");}
   }

  public static void main(String[] args) throws org.apache.commons.configuration2.ex.ConfigurationException {
    Logger logger = Logger.getLogger("jdb");

    try
     {
      DBConfig supidConfig         = new DBConfig("resources/ifxNormDocsConfig.xml");
      DBConfig supidAppMSSQLConfig = new DBConfig("resources/mssqlAppSupidConfig.xml");
      Connection supidConn         = DBUtils.getDBConn(supidConfig);
      Connection supidAppMSSQLConn = DBUtils.getDBConn(supidAppMSSQLConfig);

      // Список таблиц
      ArrayList<String> tables = new ArrayList<String>(Arrays.asList(new String[] {"docTypes", "norm_docs", "norm_docs_parts",
       "changes_journal", "files"}));
      // Выгрузка на диск
      String basePath = "c:\\temp\\supid";
      new File(basePath).mkdirs();
      MyIOUtils.clearDir(basePath);
      DBasesLoaderCore.unloadDBToDisk(supidConn, basePath, "supid", tables, null, null);

      // Загрузка данных в БД
      DBasesLoaderCore.loadDBFromDisk(supidAppMSSQLConn, basePath, tables, null, true);

     }
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    catch (DBConnectionException e)   {logger.error(e.getMessage());}
    catch (IOException e)             {logger.error(e.getMessage());}
    catch (JdbException e)            {logger.error(e.getMessage());}
    catch (ClassNotFoundException e)  {logger.error(e.getMessage());}
    catch (SQLException e)            {logger.error(e.getMessage());}
   }

 }