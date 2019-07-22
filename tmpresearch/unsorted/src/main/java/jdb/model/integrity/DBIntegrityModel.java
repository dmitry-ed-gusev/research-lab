package jdb.model.integrity;

import jdb.DBConsts;
import dgusev.dbpilot.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.DBModel;
import jdb.monitoring.DBProcessingMonitor;
import jdb.processing.DBEngineer;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Класс реализует модель БД для проверки ее целостности. Модель содержит список аналогичных моделей таблиц -
 * моделей таблиц для проверки их целостности. Все имена таблиц хранятся в ВЕРХНЕМ регистре символов.
 * @author Gusev Dmitry (019gus)
 * @version 3.1 (DATE: 21.03.2011)
*/

public class DBIntegrityModel extends DBModel implements Serializable
 {
  // Поле используется для совместимости последующих версий класса с предыдущими (для механизма сериализации)
  static final long serialVersionUID = -8598456500297919139L;

  /** Наименование разностной БД при сравнении двух БД. */
  private static final String DIFFERENCES_DB_NAME = "DIFFERENCES_DB";
  /** Компонент-логгер данного класса. Компонент транзитный - не сериализуется. */
  private transient Logger logger = Logger.getLogger(getClass().getName());
  /** Список таблиц БД. */
  private ArrayList<TableIntegrityModel> tables = null;

  public DBIntegrityModel(String dbName) throws DBModelException {super(dbName);}

  @Override
  public boolean isEmpty()
   {
    boolean result = true;
    if ((tables != null) && (!tables.isEmpty())) {result = false;}
    return result;
   }

  public ArrayList<TableIntegrityModel> getTables() {return tables;}
  
  public void setTables(ArrayList<TableIntegrityModel> tables) {this.tables = tables;}

  /**
   * Метод добавляет к списку таблиц данной модели БД еще одну. Если указанная таблица пуста - она добавлена не будет.
   * @param table TableIntegrityModel добавляемая к списку модель таблицы.
  */
  public void addTable(TableIntegrityModel table)
   {
    if (table != null)
     {
      if (this.tables == null) {this.tables = new ArrayList<TableIntegrityModel>();}
      this.tables.add(table);
     }
   }

  /**
   * Получение модели таблицы по имени из данной модели БД. Имя таблицы может указываться в любом регистре символов. Если
   * указанное имя пусто или таблица не найдена - метод возвращает значение null.
   * Метод является null-safe - корректно обрабатывает null-значения.
   * @param tableName String имя искомой таблицы.
   * @return TableIntegrityModel найденная таблица или значение null.
  */
  public TableIntegrityModel getTable(String tableName)
   {
    TableIntegrityModel table = null;
    if ((this.tables != null) && (!this.tables.isEmpty()) && (!StringUtils.isBlank(tableName)))
     {
      // Проходим по списку таблиц текущей БД и ищем по указанному имени
      for (TableIntegrityModel localTable : this.tables)
       {
        // Если полученная таблицы не null, ее имя не пусто и совпадает с указанным, то получаем результат.
        if ((localTable != null) && (!StringUtils.isBlank(localTable.getTableName())) && 
            (tableName.toUpperCase().equals(localTable.getTableName())))
         {table = localTable;}
       }
     }
    return table;
   }

  /**
   * Метод сравнивает текущую базу данных с базой данных, переданной в качестве параметра. Результат работы метода -
   * модель целостности БД (DBIntegrityModel), которая содержит таблицы со списками ключей, которые есть в таблицах
   * текущей БД, но которых нет в соответствующих по именам таблицах БД-параметра метода. Если БД-параметр
   * имеет значение null - метод вернет знаечние null. Если список таблиц БД-параметра пуст, то метод вернет БД со
   * списком таблиц текущей БД, если он не пуст. Если же в такой ситуации список таблиц текущей БД пуст, то метод
   * вернет значение null. Дополнительный параметр lightCheck управляет режимом проверки - простой режим (lightCheck=true)
   * и расширенный (lightCheck=false).
   * @param db DBIntegrityModel модель БД для сравнения.
   * @param lightCheck boolean простая/расширенная проверка - ИСТИНА/ЛОЖЬ.
   * @param monitor ProcessMonitor класс-монитор, который реализует функциональность по мониторингу процесса сравнения.
   * @param processedCount int шаг выдачи сообщения для монитора процесса (количество обработанных таблиц/строк, через
   * которое будет передано сообщение монитору).
   * @return DBIntegrityModel БД-разность сравнения текущей БД и БД-параметра.
  */
  public DBIntegrityModel compareTo(DBIntegrityModel db, boolean lightCheck, DBProcessingMonitor monitor, int processedCount)
   {
    // Если логгер не инициализирован - инициализируем. Логгер может быть null, если объект был сериализован, а
    // затем десериализован (логгер объявлен как transient поле - оно не сериализуется).
    if (logger == null) {logger = Logger.getLogger(getClass().getName());}

    logger.debug("DBIntegrityModel.compareTo() starting.");
    DBIntegrityModel result = null;
    // Если список таблиц текущей БД пуст - нечего и сравнивать, сразу возвращаем значение null. Если же
    // список таблиц текущей БД не пуст - работаем далее
    if ((this.tables != null) && (!this.tables.isEmpty()))
     {
      // Если БД-парметр имеет значение null, то также нечего сравнивать - возвращаем null.
      if (db != null)
       {

        // Если список таблиц БД-параметра пуст, то возвращаем список таблиц текущей БД
        if ((db.tables == null) || (db.tables.isEmpty()))
         {
          try
           {
            // Модель БД-результата
            result = new DBIntegrityModel(DIFFERENCES_DB_NAME);
            // Список таблиц результата - список таблиц текущей БД
            result.tables = this.tables;
           }
          // При ошибке пишем в лог и возвращаем null. Хотя здесь ошибка маловероятна. 
          catch (DBModelException e) {logger.error(e.getMessage());}
         }
        // Если же список таблиц БД-параметра не пуст, то сравниваем поочередно каждую таблицу баз данных
        else
         {
          logger.debug("Parameter DB tables list is not empty. Processing.");
          // В цикле проходим по списку таблиц текущей БД 
          for (TableIntegrityModel currentTable : this.tables)
           {
            // Если текущая таблица текущей БД не null - работаем
            if (currentTable != null)
             {
              TableIntegrityModel foreignTable = db.getTable(currentTable.getTableName());
              logger.debug("Processing table [" + currentTable.getTableName() + "].");
              // Если есть монитор - сообщим ему об обрабатываемой таблице
              if (monitor != null) {monitor.processMessage(currentTable.getTableName());}

              // Сравниваем текущую таблицу с аналогичной по имени таблицей БД_параметра
              ArrayList<Integer> keysList = currentTable.compareTo(foreignTable, lightCheck, monitor, processedCount);
              // Если в результате сравнения получен непустой список ключей - формируем таблицу разности
              if ((keysList != null) && (!keysList.isEmpty()))
               {
                // Создаем таблицу результат и добавляем ее в БД-результат. Конструкция try-catch - для того, чтобы
                // при ошибке обработки одной таблицы не оборвался весь цикл.
                try
                 {
                  // Если БД-результат еще не инициализирована - инициализация
                  if (result == null) {result = new DBIntegrityModel(DIFFERENCES_DB_NAME);}
                  // Теперь создадим таблицу-результат с именем, аналогичным имени текущей таблицы
                  TableIntegrityModel table = new TableIntegrityModel(currentTable.getTableName());
                  // Установка списка списка индексов в таблице-результате
                  table.setKeysList(keysList);
                  // Добавление таблицы в результирующую БД
                  result.addTable(table);
                 }
                // Перехват исключения при работе с текущей таблицей
                catch (DBModelException e)
                 {logger.error("Error processing table! [current: " + currentTable.getTableName() + "]" +
                               "[foreign: " + foreignTable.getTableName() + "] Message: " + e.getMessage());}
               }
              // Если же полученный список пуст - сообщим в лог
              else {logger.debug("Tables [" + currentTable.getTableName() + "] are equal (differences keys list is empty)!");}
             }
            // Если текущая таблица текущей БД имеет значение null - сообщим в лог
            else {logger.warn("Current processing table is NULL!");}
           }

         }

       }
     }
    
    return result;
   }

  /**
   * Метод сравнивает текущую базу данных с базой данных, переданной в качестве параметра. Результат работы метода -
   * модель целостности БД (DBIntegrityModel), которая содержит таблицы со списками ключей, которые есть в таблицах
   * текущей БД, но которых нет в соответствующих по именам таблицах БД-параметра метода. Если БД-параметр
   * имеет значение null - метод вернет знаечние null. Если список таблиц БД-параметра пуст, то метод вернет БД со
   * списком таблиц текущей БД, если он не пуст. Если же в такой ситуации список таблиц текущей БД пуст, то метод
   * вернет значение null.
   * @param db DBIntegrityModel
   * @return DBIntegrityModel
  */
  public DBIntegrityModel compareTo(DBIntegrityModel db, boolean lightCheck)
   {return this.compareTo(db, lightCheck, null, -1);}

  /**
   * Метод сравнивает текущую базу данных с базой данных, переданной в качестве параметра. Результат работы метода -
   * модель целостности БД (DBIntegrityModel), которая содержит таблицы со списками ключей, которые есть в таблицах
   * текущей БД, но которых нет в соответствующих по именам таблицах БД-параметра метода. Если БД-параметр
   * имеет значение null - метод вернет знаечние null. Если список таблиц БД-параметра пуст, то метод вернет БД со
   * списком таблиц текущей БД, если он не пуст. Если же в такой ситуации список таблиц текущей БД пуст, то метод
   * вернет значение null.
   * @param db DBIntegrityModel
   * @return DBIntegrityModel
  */
  public DBIntegrityModel compareTo(DBIntegrityModel db)
   {return this.compareTo(db, false, null, -1);}

  /**
   * Метод сравнивает текущую базу данных с базой данных, переданной в качестве параметра. Результат работы метода -
   * модель целостности БД (DBIntegrityModel), которая содержит таблицы со списками ключей, которые есть в таблицах
   * текущей БД, но которых нет в соответствующих по именам таблицах БД-параметра метода. Если БД-параметр
   * имеет значение null - метод вернет знаечние null. Если список таблиц БД-параметра пуст, то метод вернет БД со
   * списком таблиц текущей БД, если он не пуст. Если же в такой ситуации список таблиц текущей БД пуст, то метод
   * вернет значение null. Дополнительный параметр lightCheck управляет режимом проверки - простой режим (lightCheck=true)
   * и расширенный (lightCheck=false).<br>
   * По сравнению с однопоточным методом данный метод дает незначительный прирост быстродействия (~5-7%) на мощных
   * машинах (два ядра) с большим объемом памяти (>512MB). <b>Метод рекомендуется к применению ТОЛЬКО на многоядерных или
   * многопроцессорных машинах с большим объемом оперативной памяти.</b><br>
   * Данный метод реализует простую модель многопоточности - обработка списка всех таблиц БД идет последовательно
   * (в один поток), а уже обработка каждой таблицы многопоточна.
   * @param db DBIntegrityModel модель БД для сравнения.
   * @param lightCheck boolean простая/расширенная проверка - ИСТИНА/ЛОЖЬ.
   * @param threadsNumber int максимальное количество потоков для данного метода и всех им вызываемых.
   * @param monitor ProcessMonitor класс-монитор, который реализует функциональность по мониторингу процесса сравнения.
   * @param processedCount int шаг выдачи сообщения для монитора процесса (количество обработанных таблиц/строк, через
   * которое будет передано сообщение монитору).
   * @return DBIntegrityModel БД-разность сравнения текущей БД и БД-параметра.
  */
  public DBIntegrityModel simpleMultiThreadsCompareTo(DBIntegrityModel db, boolean lightCheck, int threadsNumber,
   DBProcessingMonitor monitor, int processedCount)
   {
    // Если логгер не инициализирован - инициализируем. Логгер может быть null, если объект был сериализован, а
    // затем десериализован (логгер объявлен как transient поле - оно не сериализуется).
    if (logger == null) {logger = Logger.getLogger(getClass().getName());}

    logger.debug("DBIntegrityModel.compareTo() starting.");
    DBIntegrityModel result = null;
    // Если список таблиц текущей БД пуст - нечего и сравнивать, сразу возвращаем значение null. Если же
    // список таблиц текущей БД не пуст - работаем далее
    if ((this.tables != null) && (!this.tables.isEmpty()))
     {
      // Если БД-парметр имеет значение null, то также нечего сравнивать - возвращаем null.
      if (db != null)
       {
        // Если список таблиц БД-параметра пуст, то возвращаем список таблиц текущей БД
        if ((db.tables == null) || (db.tables.isEmpty()))
         {
          try
           {
            // Модель БД-результата
            result = new DBIntegrityModel(DIFFERENCES_DB_NAME);
            // Список таблиц результата - список таблиц текущей БД
            result.tables = this.tables;
           }
          // При ошибке пишем в лог и возвращаем null. Хотя здесь ошибка маловероятна.
          catch (DBModelException e) {logger.error(e.getMessage());}
         }
        // Если же список таблиц БД-параметра не пуст, то сравниваем поочередно каждую таблицу баз данных
        else
         {
          logger.debug("Parameter DB tables list is not empty. Processing.");
          // В цикле проходим по списку таблиц текущей БД
          for (TableIntegrityModel currentTable : this.tables)
           {
            // Если текущая таблица текущей БД не null - работаем
            if (currentTable != null)
             {
              TableIntegrityModel foreignTable = db.getTable(currentTable.getTableName());
              logger.debug("Processing table [" + currentTable.getTableName() + "].");
              // Если есть монитор - сообщим ему об обрабатываемой таблице
              if (monitor != null) {monitor.processMessage(currentTable.getTableName());}

              // Сравниваем текущую таблицу с аналогичной по имени таблицей БД_параметра
              ArrayList<Integer> keysList = currentTable.multiThreadsCompareTo(foreignTable, lightCheck, threadsNumber, monitor, processedCount);
              // Если в результате сравнения получен непустой список ключей - формируем таблицу разности
              if ((keysList != null) && (!keysList.isEmpty()))
               {
                // Создаем таблицу результат и добавляем ее в БД-результат. Конструкция try-catch - для того, чтобы
                // при ошибке обработки одной таблицы не оборвался весь цикл.
                try
                 {
                  // Если БД-результат еще не инициализирована - инициализация
                  if (result == null) {result = new DBIntegrityModel(DIFFERENCES_DB_NAME);}
                  // Теперь создадим таблицу-результат с именем, аналогичным имени текущей таблицы
                  TableIntegrityModel table = new TableIntegrityModel(currentTable.getTableName());
                  // Установка списка списка индексов в таблице-результате
                  table.setKeysList(keysList);
                  // Добавление таблицы в результирующую БД
                  result.addTable(table);
                 }
                // Перехват исключения при работе с текущей таблицей
                catch (DBModelException e)
                 {logger.error("Error processing table! [current: " + currentTable.getTableName() + "]" +
                               "[foreign: " + foreignTable.getTableName() + "] Message: " + e.getMessage());}
               }
              // Если же полученный список пуст - сообщим в лог
              else {logger.debug("Tables [" + currentTable.getTableName() + "] are equal (differences keys list is empty)!");}
             }
            // Если текущая таблица текущей БД имеет значение null - сообщим в лог
            else {logger.warn("Current processing table is NULL!");}
           }

         }

       }
     }

    return result;
   }

  /**
   * Метод сравнивает текущую базу данных с базой данных, переданной в качестве параметра. Результат работы метода -
   * модель целостности БД (DBIntegrityModel), которая содержит таблицы со списками ключей, которые есть в таблицах
   * текущей БД, но которых нет в соответствующих по именам таблицах БД-параметра метода. Если БД-параметр
   * имеет значение null - метод вернет знаечние null. Если список таблиц БД-параметра пуст, то метод вернет БД со
   * списком таблиц текущей БД, если он не пуст. Если же в такой ситуации список таблиц текущей БД пуст, то метод
   * вернет значение null. Дополнительный параметр lightCheck управляет режимом проверки - простой режим (lightCheck=true)
   * и расширенный (lightCheck=false).<br>
   * По сравнению с однопоточным методом данный метод дает незначительный прирост быстродействия (~5-7%) на мощных
   * машинах (два ядра) с большим объемом памяти (>512MB). <b>Метод рекомендуется к применению ТОЛЬКО на многоядерных или
   * многопроцессорных машинах с большим объемом оперативной памяти.</b><br>
   * Данный метод реализует продвинутую модель многопоточности - обработка списка всех таблиц БД идет параллельно в
   * несколько потоков, каждый из которых, в свою очередь, вызывает многопоточный метод обработки одной таблицы. Однако
   * суммарное количество потоков, порождаемых данным методом не превышает значения min(threadsNumber, DBConsts.MAX_THREADS).
   * @param db DBIntegrityModel модель БД для сравнения.
   * @param lightCheck boolean простая/расширенная проверка - ИСТИНА/ЛОЖЬ.
   * @param threadsNumber int максимальное количество потоков для данного метода и всех им вызываемых.
   * @param monitor ProcessMonitor класс-монитор, который реализует функциональность по мониторингу процесса сравнения.
   * @param processedCount int шаг выдачи сообщения для монитора процесса (количество обработанных таблиц/строк, через
   * которое будет передано сообщение монитору).
   * @return DBIntegrityModel БД-разность сравнения текущей БД и БД-параметра.
   * @deprecated метод не рекомендуется к использованию, т.к. производительность при полном разбиении процесса сравнения баз
   * данных на низком уровне - ниже, чем полностью последовательный метод. Вместо данного метода следует использовать метод
   * simpleMultiThreadsCompareTo() - в данном методе многопотоковый процесс - только сравнение таблиц, перебор же таблиц
   * осуществляется последовательно.
  */
  public DBIntegrityModel advancedMultiThreadsCompareTo(final DBIntegrityModel db, final boolean lightCheck,
   int threadsNumber, final DBProcessingMonitor monitor, final int processedCount)
   {
    // Если логгер не инициализирован - инициализируем. Логгер может быть null, если объект был сериализован, а
    // затем десериализован (логгер объявлен как transient поле - оно не сериализуется).
    if (logger == null) {logger = Logger.getLogger(getClass().getName());}

    logger.debug("DBIntegrityModel.compareTo() starting.");
    DBIntegrityModel result = null;
    // Если список таблиц текущей БД пуст - нечего и сравнивать, сразу возвращаем значение null. Если же
    // список таблиц текущей БД не пуст - работаем далее
    if ((this.tables != null) && (!this.tables.isEmpty()))
     {
      // Если БД-парметр имеет значение null, то также нечего сравнивать - возвращаем null.
      if (db != null)
       {
        // Если список таблиц БД-параметра пуст, то возвращаем список таблиц текущей БД
        if ((db.tables == null) || (db.tables.isEmpty()))
         {
          try
           {
            // Модель БД-результата
            result = new DBIntegrityModel(DIFFERENCES_DB_NAME);
            // Список таблиц результата - список таблиц текущей БД
            result.tables = this.tables;
           }
          // При ошибке пишем в лог и возвращаем null. Хотя здесь ошибка маловероятна.
          catch (DBModelException e) {logger.error(e.getMessage());}
         }
        // Если же список таблиц БД-параметра не пуст, то сравниваем поочередно каждую таблицу баз данных
        else
         {
          logger.debug("Parameter DB tables list is not empty. Processing.");

          // Коэффициент соотношения количества потоков в данном методе и в методах им вызываемых - непосредственно
          // в методах TableIntegrityModel.advancedMultiThreadsCompareTo() - соответственно в этих вызываемых методах количество
          // потоков должно быть больше. Также суммарное количество потоков для данного метода не может быть меньше этого
          // коэффициента.
          int threadsRelation = 2;

          // Для начала выберем максимальное суммарное количество потоков для данного метода и других многопотоковых
          // методов, вызываемых данным (TableIntegrityModel.advancedMultiThreadsCompareTo()). Если указанное количество потоков
          // не подходит - потоков будет максимальное количество (см. соотв. константу в DBConsts).
          int threadsCount;
          if (threadsNumber >= threadsRelation) {threadsCount = Math.min(threadsNumber, DBConsts.MAX_THREADS);}
          else                                  {threadsCount = DBConsts.MAX_THREADS;}

          // Теперь рассчитываем количество потоков для данного метода и для методов сравнения таблиц. Определим, что
          // количество потоков в методах сравнения таблиц (TableIntegrityModel.advancedMultiThreadsCompareTo()) должно быть
          // в threadsRelation раз(а) больше, чем количество потоков в данном методе.
          int currentMethodThreads = ((Double)(Math.sqrt(threadsCount/threadsRelation))).intValue();

          // Делим на части список обрабатываемых таблиц данной модели
          final int partSize;
          final int remainder;
          if (this.tables.size() >= currentMethodThreads)
           {
            partSize  = this.tables.size()/currentMethodThreads;
            remainder = this.tables.size()%currentMethodThreads;
           }
          else
           {
            currentMethodThreads = this.tables.size();
            partSize = 1;
            remainder = 0;
           }
          
          // Для уменьшения потерь общего количества потоков вычисляем кол-во потоков во внутренних методах так (однако,
          // при таком способе вычисления реальное соотношение будет больше значения threadsRelation, если же надо чтобы
          // реальное соотношение совпадало с указанным, вычислять надо так: threadsRelation*currentMethodThreads, но при
          // этом будут большие потери результирующего (суммарного) количества потоков):
          final int internalMethodThreads; // <- (final) - для доступа из внутреннего класса-потока
          if (currentMethodThreads > 1) {internalMethodThreads = threadsCount/threadsRelation;}
          else                          {internalMethodThreads = threadsCount;}

          // Группа для всех потоков данного метода
          ThreadGroup group = new ThreadGroup("DBIntegrityModelThreads");
          // Финальная переменная для доступа к общему количеству потоков из самого потока (любого)
          final int allThreadsCount = currentMethodThreads;
          // Копия списка таблиц текущей модели БД - для доступа к этому списку из потоков
          final ArrayList<TableIntegrityModel> currentTablesList = new ArrayList<TableIntegrityModel>();
          currentTablesList.addAll(this.tables);
          try
           {
            // Результирующая модель БД для сохранения результатов работы всех потоков
            final DBIntegrityModel differencesDB = new DBIntegrityModel(DIFFERENCES_DB_NAME);

            // В цикле создаем необходимое количество потоков для данного метода
            for (int i = 1; i <= currentMethodThreads; i++)
             {
              // Финальная переменная для доступа к номеру потока из самого потока
              final int currentThreadNumber    = i;
              // Непосредственно внутренний класс с реализацией кода одного потока
              new Thread
               (group,
                new Runnable()
                 {
                  public void run()
                   {
                    logger.debug("Thread # " + currentThreadNumber + " started!");
                    // Начальная и конечная позиции (номера) в списке таблиц текущей модели БД для данного потока. Если при
                    // разделении списка таблиц на потоки остался остаток (обычно он меньше количества потоков), то этот остаток
                    // достается последнему потоку (всегда).
                    int start  = partSize*(currentThreadNumber - 1);
                    int finish;
                    // Если есть остаток и данный поток создается последним - пересчитываем конечную позицию
                    if ((remainder > 0) && (currentThreadNumber == allThreadsCount))
                     {finish = (partSize*currentThreadNumber) - 1 + remainder;}
                    // Если остатка нет или данный поток создается не последним - конечная позиция стандартна
                    else
                     {finish = (partSize*currentThreadNumber) - 1;}
                    // Счетчик выполнения цикла
                    int counter = 0;
                    int lastCounter = 0;
                    for (int count = start; count <= finish; count++)
                     {
                      TableIntegrityModel currentTable = currentTablesList.get(count);
                      // Если текущая таблица не пуста - обрабатываем ее
                      if (currentTable != null)
                       {
                        TableIntegrityModel foreignTable = db.getTable(currentTable.getTableName());
                        logger.debug("Processing table [" + currentTable.getTableName() + "].");
                        // Сравниваем текущую таблицу с аналогичной по имени таблицей БД_параметра
                        ArrayList<Integer> keysList = currentTable.multiThreadsCompareTo(foreignTable,
                         lightCheck, internalMethodThreads, monitor, processedCount);
                        // Если в результате сравнения получен непустой список ключей - формируем таблицу разности
                        if ((keysList != null) && (!keysList.isEmpty()))
                         {
                          // Создаем таблицу результат и добавляем ее в БД-результат. Конструкция try-catch - для того,
                          // чтобы при ошибке обработки одной таблицы не оборвался весь цикл.
                          try
                           {
                            // Создадим таблицу-результат с именем, аналогичным имени текущей таблицы
                            TableIntegrityModel table = new TableIntegrityModel(currentTable.getTableName());
                            // Установка списка списка индексов в таблице-результате
                            table.setKeysList(keysList);
                            // Добавление таблицы в результирующую БД
                            synchronized (differencesDB) {differencesDB.addTable(table);}
                           }
                          // Перехват исключения при работе с текущей таблицей
                          catch (DBModelException e)
                           {logger.error("Error processing table! [current: " + currentTable.getTableName() + "]" +
                             "[foreign: " + foreignTable.getTableName() + "] Message: " + e.getMessage());}
                         }
                        // Если же полученный список пуст - сообщим в лог
                        else
                         {logger.debug("Tables [" + currentTable.getTableName() + "] are equal (differences keys list is empty)!");}
                       }
                      // Если текущая таблица пуста - сообщим об этом - возможная проблема
                      else {logger.warn("Empty table! Check current model!");}
                      // Место для системы, где возможна приостановка потока
                      Thread.yield();
                     }
                   } // END OF RUN METHOD
                 }
               ).start();
             } // END OF FOR STATEMENT

            // Ждем остановки всех потоков в группе
            logger.info("WAITING FOR ALL THREADS STOP...");
            // Счетчик итераций цикла ожидания окончания всех потоков
            int counter = 0;
            // Цикл ожидания завершения всех потоков и вывода информации о прогрессе выполнения
            do
             {
              //if (counter%10000 == 0) {logger.debug("Processing...");}
              counter++;
             }
            while (group.activeCount() > 0);
            // Вот в этом месте все потоки завершены
            logger.info("ALL THREADS CLOSED SUCCESSFULLY.");

            logger.debug("\n\n -> \n\n" + differencesDB);

            // Если в БД-результат были занесены какие-либо таблицы, то перенесем их в результат метода
            if ((differencesDB.tables != null) && !differencesDB.tables.isEmpty())
             {
              result = new DBIntegrityModel(DIFFERENCES_DB_NAME);
              result.tables = new ArrayList<TableIntegrityModel>();
              result.tables.addAll(differencesDB.tables);
             }

           } // END OF TRY
          // Перехват ИС
          catch (DBModelException e) {logger.error(e.getMessage());}
         }

       }
     }

    // Возвращаем результат
    return result;
   }

  /** Строковое представление данной модели БД. */
  @Override
  public String toString()
   {
    StringBuilder dbString = new StringBuilder();
    dbString.append("\nDATABASE: ").append(this.getDbName());
    dbString.append("\nTABLES COUNT: ");
    // Если список таблиц не пуст в цикле его формируем
    if ((this.tables != null) && (!this.tables.isEmpty()))
     {
      dbString.append(this.tables.size()).append("\n TABLES LIST: \n----------\n\n");
      for (TableIntegrityModel table : this.tables) {dbString.append(table).append("\n");}
      dbString.append("----------\n");
     }
    // Если список таблиц пуст - сообщим об этом
    else {dbString.append(0).append("\nTABLES LIST IS EMPTY!\n");}
    return dbString.toString();
   }

  @Override
  public int getTablesCount()
   {
    int result = 0;
    if ((this.tables != null) && (!this.tables.isEmpty()))
     {result = this.tables.size();}
    return result;
   }

  /**
   * Метод только для тестирования класса!
   * @param args String[] параметры метода.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger("jdb");
    Logger logger = Logger.getLogger(TableIntegrityModel.class.getName());

    DBConfig mysqlConfig1 = new DBConfig();
    mysqlConfig1.setDbType(DBConsts.DBType.MYSQL);
    mysqlConfig1.setHost("localhost:3306");
    mysqlConfig1.setDbName("storm");
    mysqlConfig1.setUser("root");
    mysqlConfig1.setPassword("mysql");
    //mysqlConfig1.addAllowedTable("items");
    //mysqlConfig1.addAllowedTable("ruleset");

    DBConfig ifxConfig1 = new DBConfig();
    ifxConfig1.setDbType(DBConsts.DBType.INFORMIX);
    ifxConfig1.setServerName("hercules");
    ifxConfig1.setHost("appserver:1526");
    ifxConfig1.setDbName("storm");
    ifxConfig1.setUser("informix");
    ifxConfig1.setPassword("ifx_dba_019");
    //ifxConfig1.addAllowedTable("items");
    //ifxConfig1.addAllowedTable("ruleset");

    try
     {
      DBEngineer serverEngineer = new DBEngineer(ifxConfig1);
      DBIntegrityModel serverModel = serverEngineer.getDBIntegrityModel();

      DBEngineer clientEngineer = new DBEngineer(mysqlConfig1);
      DBIntegrityModel clientModel = clientEngineer.getDBIntegrityModel();

      if ((serverModel != null) && (clientModel != null))
       {
        logger.debug("\n\n-----------------------------------------------------------------------\n\n");
        logger.debug("multi multiThreadsHelpers -> " + serverModel.simpleMultiThreadsCompareTo(clientModel, false, 130, null, 2000));
        logger.debug("\n\n-----------------------------------------------------------------------\n\n");
        logger.debug("standart -> " + serverModel.compareTo(clientModel, false, null, 2000));
       }

     }
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    catch (DBConnectionException e) {logger.error(e.getMessage());}
    catch (DBModelException e) {logger.error(e.getMessage());}
    catch (SQLException e) {logger.error(e.getMessage());}

   }

 }