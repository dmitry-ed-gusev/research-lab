package jdb.model.integrity;

import jdb.DBConsts;
import dgusev.dbpilot.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.TableModel;
import jdb.monitoring.DBProcessingMonitor;
import jdb.processing.DBEngineer;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Класс реализует модель таблицы БД для проверки ее целостности - модель содержит список значений ключевого
 * поля для всех записей (primary key). Имя таблицы хранится только в ВЕРХНЕМ регистре символов. Имя таблицы не
 * может быть пустым (пустая строка или null).
 * @author Gusev Dmitry (019gus)
 * @version 4.0 (DATE: 27.07.2010)
*/

public class TableIntegrityModel extends TableModel implements Serializable
 {
  static final long serialVersionUID = 8194532538337172710L;

  /** Компонент-логгер данного класса */
  private transient Logger logger = Logger.getLogger(getClass().getName());

  /** Список значений ключевых полей для всех записей таблицы. */
  private           ArrayList<Integer> keysList = null;

  public TableIntegrityModel(String tableName) throws DBModelException {super(tableName);}

  public ArrayList<Integer> getKeysList() {return keysList;}
  public void setKeysList(ArrayList<Integer> keysList) {this.keysList = keysList;}

  /**
   * Метод возвращает csv-список всех ключей, которые хранятся в данной модели целостности таблицы. Если список
   * состоит только из null-ключей, то метод вернет значение null.
   * Данный метод является null-safe - корректно обрабатывает null-значения.
   * @return String CSV-список ключей таблицы или значение null.
  */
  public String getCSVKeysList()
   {
    String        result  = null;
    StringBuilder csvList = null;
    if ((this.keysList != null) && (!this.keysList.isEmpty()))
     {
      for (Integer key : this.keysList)
       {
        // Если очередное полученное значение не пусто - работаем
        if (key != null)
         {
          // Если еще не инициализирован результат - инициализация
          if (csvList == null) {csvList = new StringBuilder();}
          // Если же результат уже инициализирован - в нем уже есть поля, добавим к ним запятую
          else {csvList.append(", ");}
          csvList.append(key);
         }
       }
     }
    if (csvList != null) {result = csvList.toString();}
    // Возвращаем результат
    return result;
   }

  /**
   * Метод добавляет еще один целочисленный ключ к списку ключей данной таблицы.
   * @param key int добавляемый к списку ключ.
  */
  public void addKey(int key)
   {
    if (this.keysList == null) {this.keysList = new ArrayList<Integer>();} 
    this.keysList.add(key);
   }

  /**
   * Метод сравнивает текущую таблицу с таблицей, переданной в качестве параметра. Результат работы метода - список
   * ключей, которые есть в текущей таблице, но которых нет в таблице-параметре. Ключи, которые есть в таблице-параметре,
   * но которых нет в текущей таблице - в результат не попадают. Если таблица-параметр=null, то метод вернет значение null.
   * Если список ключей таблицы-параметра пуст - метод возвращает список ключей текущей таблицы, если он не пуст, если
   * же пуст, то возвращается значение null. Дополнительный параметр lightCheck управляет режимом проверки - простой
   * режим (lightCheck=true) и расширенный (lightCheck=false). В простом режиме сравнивается количество ключей и, если
   * количество одинаковое, то таблицы считаются одинаковыми и метод возвращает значение null, если же количество разное,
   * то метод проверит списки ключей на совпадение и вернет разницу (описано выше). В расширенном режиме списки ключей
   * сравниваются всегда, назависимо от того, совпадает ли их количество.
   * Метод является null-safe - он корректно обрабатывает null ключи в списке ключей - эти ключи просто игнорируются.
   * @param table TableIntegrityModel модель таблицы для сравнения с текущей моделью.
   * @param lightCheck boolean включение/выключение расширенного/простого режима сравнения таблиц.
   * @param monitor ProcessMonitor класс-монитор, который реализует функциональность по мониторингу процесса сравнения.
   * @param processedCount int шаг выдачи сообщения для монитора процесса (количество обработанных строк, через которое
   * будет передано сообщение монитору). 
   * @return ArrayList[Integer] список ключей, которые есть в текущей таблице, но которых нет в таблице параметре или
   * значение null, если такие ключи не найдены.
  */
  public ArrayList<Integer> compareTo(TableIntegrityModel table, boolean lightCheck, DBProcessingMonitor monitor, int processedCount)
   {
    // Если логгер не инициализирован - инициализируем. Логгер может быть null, если объект был сериализован, а
    // затем десериализован (логгер объявлен как transient поле - оно не сериализуется).
    if (logger == null) {logger = Logger.getLogger(getClass().getName());}
    
    ArrayList<Integer> result = null;
    // Если в текущей таблице нет ключей - нечего и сравнивать, сразу возвращаем значение null. Если же в
    // текущей таблице есть ключи, то работаем дальше.
    if ((this.keysList != null) && (!this.keysList.isEmpty()))
     {
      // Если таблица-параметр имеет значение null - сравнивать нечего, возвращаем null.
      if (table != null)
       {
        // Если таблица-параметр не пуста (не null), но пуст список ее ключей, то возвращаем список ключей
        // текущей таблицы
        if ((table.keysList == null) || (table.keysList.isEmpty())) {result = this.keysList;}
        // Если же таблица-параметр не null и не пуст список ее ключей - тогда работаем
        else
         {
          // Если включена простая проверка (lightCheck=true), то сравниваем длину списков ключей таблиц. Если длина совпадает,
          // то при простой проверке - выходим со значением null (совпадение). Если же простая проверка выключена
          // (lightCheck=false), то всегда сравниваем между собой списки значений ключей таблиц (не важно, совпадает ли их количество).
          if (!(lightCheck && (table.keysList.size() == this.keysList.size())))
           {
            // Для логгирования и мониторинга выберем шаг выполненных операций для вывода
            int processedStep;
            if ((processedCount >= DBConsts.MIN_MONITOR_OPERATIONS_COUNT) && (processedCount <= DBConsts.MAX_MONITOR_OPERATIONS_COUNT))
             {processedStep = processedCount;}
            else
             {processedStep = DBConsts.MAX_MONITOR_OPERATIONS_COUNT;}
            // Счетчик итераций цикла
            int counter = 0;
            // В цикле проходим по списку ключей текущей таблицы и сравниваем его со списком ключей таблицы-параметра.
            for (int currentKey : this.keysList)
             {
              if (/**(currentKey != null) && */(!table.keysList.contains(currentKey)))
               {
                if (result == null) {result = new ArrayList<Integer>();}
                result.add(currentKey);
               }
              // Отладочный вывод и вывод в монитор (если он есть)
              if (counter%processedStep == 0)
               {
                logger.debug("Processed [" + counter + "/" + this.keysList.size() + "]");
                // Если есть монитор - выводим в него запись
                if (monitor != null) {monitor.processMessage(table.getTableName() + ": " + counter + " / " + this.keysList.size());}
               }
              counter++;
             }
            // После окончания цикла сообщим об обработке всех записей талицы
            logger.debug("Processed [" + counter + "/" + this.keysList.size() + "]");
            // Если есть монитор - выводим в него запись
            if (monitor != null) {monitor.processMessage(table.getTableName() + ": " + counter + " / " + this.keysList.size());}
            
           } // блок if с циклом for

         }
       }
     }
    return result;
   }

  /**
   * Метод сравнивает текущую таблицу с таблицей, переданной в качестве параметра. Результат работы метода - список
   * ключей, которые есть в текущей таблице, но которых нет в таблице-параметре. Ключи, которые есть в таблице-параметре,
   * но которых нет в текущей таблице - в результат не попадают. Если таблица-параметр=null, то метод вернет значение null.
   * Если список ключей таблицы-параметра пуст - метод возвращает список ключей текущей таблицы, если он не пуст, если
   * же пуст, то возвращается значение null. Дополнительный параметр lightCheck управляет режимом проверки - простой
   * режим (lightCheck=true) и расширенный (lightCheck=false). В простом режиме сравнивается количество ключей и, если
   * количество одинаковое, то таблицы считаются одинаковыми и метод возвращает значение null, если же количество разное,
   * то метод проверит списки ключей на совпадение и вернет разницу (описано выше). В расширенном режиме списки ключей
   * сравниваются всегда, назависимо от того, совпадает ли их количество.
   * Метод является null-safe - он корректно обрабатывает null ключи в списке ключей - эти ключи просто игнорируются.
   * @param table TableIntegrityModel модель таблицы для сравнения с текущей моделью.
   * @param lightCheck boolean включение/выключение расширенного/простого режима сравнения таблиц.
   * @return ArrayList[Integer] список ключей, которые есть в текущей таблице, но которых нет в таблице параметре или
   * значение null, если такие ключи не найдены.
  */
  public ArrayList<Integer> compareTo(TableIntegrityModel table, boolean lightCheck)
   {return this.compareTo(table, lightCheck, null, -1);}

  /**
   * Метод сравнивает текущую таблицу с таблицей, переданной в качестве параметра. Результат работы метода - список
   * ключей, которые есть в текущей таблице, но которых нет в таблице-параметре. Ключи, которые есть в таблице-параметре,
   * но которых нет в текущей таблице - в результат не попадают. Если таблица-параметр=null, то метод вернет значение null.
   * Если список ключей таблицы-параметра пуст - метод возвращает список ключей текущей таблицы, если он не пуст, если
   * же пуст, то возвращается значение null. При сравнении таблиц с помощью данного метода всегда сравниваются списки ключей,
   * вне зависимости от совпадения их (ключей) количества.
   * @param table TableIntegrityModel модель таблицы для сравнения с текущей моделью.
   * @return ArrayList[Integer] список ключей, которые есть в текущей таблице, но которых нет в таблице параметре или
   * значение null, если такие ключи не найдены.
  */
  public ArrayList<Integer> compareTo(TableIntegrityModel table)
   {return this.compareTo(table, false);}

  /**
   * Многопоточный метод сравнения содержимого двух таблиц. По сравнению с однопоточным методом данный метод дает
   * незначительный прирост быстродействия (~5-7%) на мощных машинах (два ядра) с большим объемом памяти (>512MB).
   * @param table TableIntegrityModel модель таблицы для сравнения с текущей моделью.
   * @param lightCheck boolean включение/выключение расширенного/простого режима сравнения таблиц.
   * @param threadsNumber int
   * @param monitor DBProcessingMonitor
   * @param processedCount int
   * @return ArrayList[Integer] список ключей, которые есть в текущей таблице, но которых нет в таблице параметре или
   * значение null, если такие ключи не найдены.
  */
  public ArrayList<Integer> multiThreadsCompareTo(final TableIntegrityModel table, boolean lightCheck,
   int threadsNumber, DBProcessingMonitor monitor, int processedCount)
   {
    // Если логгер не инициализирован - инициализируем. Логгер может быть null, если объект был сериализован, а
    // затем десериализован (логгер объявлен как transient поле - оно не сериализуется).
    if (logger == null) {logger = Logger.getLogger(getClass().getName());}

    ArrayList<Integer> result = null;
    // Если в текущей таблице нет ключей - нечего и сравнивать, сразу возвращаем значение null. Если же в
    // текущей таблице есть ключи, то работаем дальше.
    if ((this.keysList != null) && (!this.keysList.isEmpty()))
     {
      // Если таблица-параметр имеет значение null - сравнивать нечего, возвращаем null.
      if (table != null)
       {
        // Если таблица-параметр не пуста (не null), но пуст список ее ключей, то возвращаем список ключей
        // текущей таблицы
        if ((table.keysList == null) || (table.keysList.isEmpty())) {result = this.keysList;}
        // Если же таблица-параметр не null и не пуст список ее ключей - тогда работаем
        else
         {
          // Если включена простая проверка (lightCheck=true), то сравниваем длину списков ключей таблиц. Если длина совпадает,
          // то при простой проверке - выходим со значением null (совпадение). Если же простая проверка выключена
          // (lightCheck=false), то всегда сравниваем между собой списки значений ключей таблиц (не важно, совпадает ли их количество).
          if (!(lightCheck && (table.keysList.size() == this.keysList.size())))
           {
            // Для логгирования и мониторинга выберем шаг выполненных операций для вывода
            //int processedStep;
            //if ((processedCount >= DBConsts.MIN_MONITOR_OPERATIONS_COUNT) && (processedCount <= DBConsts.MAX_MONITOR_OPERATIONS_COUNT))
            // {processedStep = processedCount;}
            //else
            // {processedStep = DBConsts.MAX_MONITOR_OPERATIONS_COUNT;}

            // Посчитаем количество необходимых потоков. Максимально - DBConsts.MAX_THREADS. 
            int threadsCount = Math.min(DBConsts.MAX_THREADS, threadsNumber) ;
            int currentListSize = this.keysList.size();
            int foreignListSize = table.keysList.size();
            // Если количество итераций (перемножение длин списков) имеет значение меньше максимального количества потоков,
            // то нет смысла в потоках и поток будет один. Если же больше - количество потоков будет равно минимально
            // длине одного из двух списков.
            if (currentListSize*foreignListSize <= threadsCount) {threadsCount = 1;}
            else
             {
              threadsCount = Math.min(Math.min(currentListSize, foreignListSize), threadsCount);
              // Если получившееся количество потоков больше максимума - скорректируем его
              //if (threadsCount > DBConsts.MAX_THREADS) {threadsCount = DBConsts.MAX_THREADS;}
             }

            // Размер части списка ключей таблицы параметра, которую(часть) будет обрабатывать один поток
            final int partSize;
            partSize = currentListSize/threadsCount;
            // Остаток списка ключей таблицы-параметра, который будет обрабатываться последним потоком
            final int remainder;
            remainder = currentListSize%threadsCount;

            // Копия списка ключей текущей таблицы для доступа из всех потоков
            final ArrayList<Integer> currentKeysList = new ArrayList<Integer>();
            currentKeysList.addAll(this.keysList);

            // Внутренний класс для накопления количества обработанных запросов во всех потоках вместе
            class Total
             {
              // Здесь храним общее число
              private int total = 0;
              // Увеличение общего числа обработанных запросов
              public void addTotal(int count) {total += count;}
              // Получение общего числа обработанных запросов
              public int  getTotal() {return total;}
             }
            // Объектная переменная для хранения и доступа из потоков к счетчику обработанных запросов
            final Total total = new Total();

            // Группа для всех создаваемых потоков
            ThreadGroup group = new ThreadGroup("tableIntegrityThreads");

            // Общий результирующий список ключей. В нем будут результаты всех потоков.
            final ArrayList<Integer> allThreadsResultList = new ArrayList<Integer>();

            // В цикле создаем необходимое количество потоков для обработки
            for (int i = 1; i <= threadsCount; i++) 
             {
              // Финальная переменная для доступа к общему количеству потоков из самого потока (любого)
              final int allThreadsCount = threadsCount;
              // Финальная переменная для доступа к номеру потока из самого потока
              final int threadNumber    = i;
              // Копия списка ключей таблицы-параметра для данного потока (на каждый поток своя копия)
              final ArrayList<Integer> foreignKeysList = new ArrayList<Integer>();
              foreignKeysList.addAll(table.keysList);
              // Результирующий список ключей, которые есть в текущей таблице, но нет в той части списка ключей
              // таблицы-параметра, которую (часть) обрабатывает данный поток
              final ArrayList<Integer> currentThreadResult = new ArrayList<Integer>();
              // Создание нового потока
              new Thread
               (group,
                new Runnable()
                 {
                  // Метод запуска потока на выполнение
                  public void run()
                   {
                    logger.debug("Thread # " + threadNumber + " started!");
                    // Начальная и конечная позиции (номера) в списке ключей таблицы-параметра для данного потока. Если при
                    // разделении списка на потоки остался остаток (обычно он меньше количества потоков), то этот остаток
                    // достается последнему потоку (всегда).
                    int start  = partSize*(threadNumber - 1);
                    int finish;
                    // Если есть остаток и данный поток создается последним - пересчитываем конечную позицию
                    if ((remainder > 0) && (threadNumber == allThreadsCount)) {finish = (partSize*threadNumber) - 1 + remainder;}
                    // Если остатка нет или данный поток создается не последним - конечная позиция стандартна
                    else               {finish = (partSize*threadNumber) - 1;}
                    // Счетчик выполнения цикла
                    int counter = 0;
                    int lastCounter = 0;
                    // В цикле поток проходит по своей части списка ключей внешней таблицы-параметра и проверяет их
                    // наличие в своей копии списка ключей текущей таблицы
                    for (int count = start; count <= finish; count++)
                     {
                      // Получаем ключ из списка ключей текущей таблицы
                      Integer currentKey = currentKeysList.get(count);
                      // Если такого ключа нет в списке ключей таблицы-параметра - добавляем ключ к результату
                      if ((currentKey != null) && (!foreignKeysList.contains(currentKey)))
                       {currentThreadResult.add(currentKey);}
                      // Увеличиваем счетчик итераций
                      counter++;
                      // Если достигнут необходимый для вывода сообщения шаг итераций - вывод сообщений и добавление
                      // количества обработанных запросов к общему счетчику.
                      if (counter%100 == 0)
                       {
                        int step = counter - lastCounter;
                        //logger.debug("Thread #" + threadNumber + ". Processed: " + counter + "/" + (finish-start));
                        // Увеличиваем общий счетчик выполненных во всех потоках запросов. Объект блокируется на время
                        // доступа из каждого потока (оператор synchronized)
                        synchronized (total) {total.addTotal(step);}
                        lastCounter = counter;
                       }
                      // Для того, чтобы данный поток не сожрал все ресурсы, необходим спец. метод, который позволит системе
                      // прерывать данный поток. Это возможно на каждой итерации цикла. При этом данный метод (yield()) означает
                      // только место возможного прерывания, но не прерывает поток. Прерывание (засыпание) потока выполняется
                      // другим методом - sleep(ms).
                      Thread.yield();
                      //try {Thread.sleep(5);}
                      //catch(InterruptedException e) {logger.error(e.getMessage());}

                     } // КОНЕЦ ЦИКЛА FOR
                    // После завершения выполнения цикла добавим к общему счетчику оставшиеся итерации
                    int step = counter - lastCounter;
                    synchronized (total) {total.addTotal(step);}
                    // Если что-то было заненсено в результирующий лист текущего потока, то надо добавить
                    // эти данные в общий результирующий список
                    synchronized (allThreadsResultList)
                     {if (!currentThreadResult.isEmpty()) {allThreadsResultList.addAll(currentThreadResult);}}
                   }
                 }
               ).start();
             }

            // Ждем остановки всех потоков в группе
            logger.info("WAITING FOR ALL THREADS STOP...");
            // Счетчик итераций цикла ожидания окончания всех потоков
            int counter = 0;
            // Переменная для запоминания последнего значения счетчика обработанных запросов
            int lastCounter = -1;
            // Шаг (количество обработанных запросов), через который будет выдано сообщение монитору.
            int monitorMessageStep;
            if ((processedCount >= DBConsts.MIN_MONITOR_OPERATIONS_COUNT) &&
                (processedCount <= DBConsts.MAX_MONITOR_OPERATIONS_COUNT)) {monitorMessageStep = processedCount;}
            else {monitorMessageStep = DBConsts.MAX_MONITOR_OPERATIONS_COUNT;}

            // Цикл ожидания завершения всех потоков и вывода информации о прогрессе выполнения
            do
             {
              if (counter%25000 == 0)
               {
                int totalProcessed = total.getTotal();
                if ((lastCounter != totalProcessed) && ((totalProcessed - lastCounter) > monitorMessageStep))
                 {
                  logger.debug("TOTAL processed : [" + totalProcessed + "/" + currentListSize + "]");
                  // Если есть монитор данного процесса - выдадим ему сообщение
                  if (monitor != null) {monitor.processMessage(table.getTableName() + ": " + totalProcessed + " / " + currentListSize);}
                  lastCounter = totalProcessed;
                 }
               }
              counter++;
             }
            while(group.activeCount() > 0);
            // Вот в этом месте все потоки завершены
            logger.info("ALL THREADS CLOSED SUCCESSFULLY.");

            // После окончания выполнения всех потоков еще раз соберем инфу с монитора. Если добавились еще записи -
            // выведем сообщение монитора (последнее).
            int totalProcessed = total.getTotal();
            if (lastCounter != totalProcessed)
             {
              logger.debug("TOTAL processed : [" + totalProcessed + "/" + currentListSize + "]");
              // Если есть монитор данного процесса - выдадим ему сообщение
              if (monitor != null) {monitor.processMessage(table.getTableName() + ": " + totalProcessed + " / " + currentListSize);}
             }

            // Если в результате работы всех потоков били добавлены данные в общий результирующий список всех потоков,
            // то перенесем эти данные в конечный результат
            if (!allThreadsResultList.isEmpty()) {result = allThreadsResultList;}
            
          }
         }
       }
     }
    return result;
   }

  /** Строковое представление данной таблицы. */
  @Override
  public String toString()
   {
    StringBuilder tableString = new StringBuilder();
    tableString.append(" TABLE: ");
    // Если есть схема - укажем ее
    if (!StringUtils.isBlank(this.getTableSchema())) {tableString.append(this.getTableSchema()).append(".");}
    // Тип таблицы
    tableString.append(this.getTableName()).append(" (TYPE: ").append(this.getTableType()).append(")");
    // Список ключей таблицы
    tableString.append("; KEYS COUNT: ");
    if ((this.keysList != null) && (!this.keysList.isEmpty()))
     {
      tableString.append(this.keysList.size()).append("\n KEYS LIST:\n  ");
      for (int i = 0; i < this.keysList.size(); i++)
       {
        tableString.append(this.keysList.get(i));
        // Запятая после значения
        if (i < this.keysList.size() - 1) {tableString.append(",");}
        // Перевод строки (для формирования удобочитаемой "матрицы" ключей)
        if ((i + 1)%30 == 0) {tableString.append("\n  ");}
       }
      // Дополнительный перевод строки
      tableString.append("\n");
     }
    // Если список ключей пуст - просто сообщим об этом
    else {tableString.append(0);}

    return tableString.toString();
   }

  /**
   * Метод предназначен только для тестирования данного класса.
   * @param args String[] параметры метода main.
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

    /**
    DBConfig mysqlConfig2 = new DBConfig();
    mysqlConfig2.setDbType(DBConsts.DBType.MYSQL);
    mysqlConfig2.setHost("localhost:3306");
    mysqlConfig2.setDbName("storm");
    mysqlConfig2.setUser("root");
    mysqlConfig2.setPassword("mysql");
    mysqlConfig2.addAllowedTable("items");
    */

    DBConfig ifxConfig1 = new DBConfig();
    ifxConfig1.setDbType(DBConsts.DBType.INFORMIX);
    ifxConfig1.setServerName("hercules");
    ifxConfig1.setHost("appserver:1526");
    ifxConfig1.setDbName("storm");
    ifxConfig1.setUser("informix");
    ifxConfig1.setPassword("ifx_dba_019");
    //ifxConfig1.addAllowedTable("items");

    try
     {
      DBEngineer serverEngineer = new DBEngineer(ifxConfig1);
      DBIntegrityModel serverModel = serverEngineer.getDBIntegrityModel();

      DBEngineer clientEngineer = new DBEngineer(mysqlConfig1);
      DBIntegrityModel clientModel = clientEngineer.getDBIntegrityModel();

      // Получаем таблицы
      TableIntegrityModel serverTable = serverModel.getTable("ruleset");
      if (serverTable != null) {logger.debug("server table ok!");}
      else {logger.debug("server table is null!");}
      
      TableIntegrityModel clientTable = clientModel.getTable("ruleset");
      if (clientTable != null) {logger.debug("client table ok!");}
      else {logger.debug("client table is null!");}

      if ((serverTable != null) && (clientTable != null))
       {
        logger.debug("standart -> " + serverTable.compareTo(clientTable, false, null, 2000));
        logger.debug("multi multiThreadsHelpers -> " + serverTable.multiThreadsCompareTo(clientTable, false, 20, null, 2000));
       }

     }
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    catch (DBConnectionException e) {logger.error(e.getMessage());}
    catch (DBModelException e) {logger.error(e.getMessage());}
    catch (SQLException e) {logger.error(e.getMessage());}

   }

 }