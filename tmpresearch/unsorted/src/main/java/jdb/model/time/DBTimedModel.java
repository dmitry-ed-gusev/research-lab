package jdb.model.time;

import jdb.exceptions.DBModelException;
import jdb.model.DBModel;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Модель БД с указанием времени последнего обновления каждой таблицы - макс. значение поля timestamp.
 * Список таблиц данной модели БД - список объектов типа TableTimeModel. Имя БД хранится в врехнем регистре.
 * @author Gusev Dmitry (019gus)
 * @version 4.0 (DATE: 29.07.2010)
 *
 * @deprecated вместо данного класса рекомендуется использовать класс
 * {@link jdb.nextGen.models.SimpleDBIntegrityModel SimpleDBIntegrityModel}
*/

public class DBTimedModel extends DBModel implements Serializable
 {
  static final long serialVersionUID = -6414707109042975340L;

  /** Компонент-логгер данного класса. */
  private transient Logger           logger = Logger.getLogger(getClass().getName());
  /** Список таблиц данной БД с указанием даты обновления. */
  private ArrayList<TableTimedModel> tables = null;

  /**
   * Конструктор по умолчанию. Обязательно инициализирует имя БД.
   * @param dbName String имя создаваемой модели БД.
   * @throws DBModelException ИС возникает при создании модели БД с пустым именем.
  */
  public DBTimedModel(String dbName) throws DBModelException {super(dbName);}

  public ArrayList<TableTimedModel> getTables() {
   return tables;
  }

  public void setTables(ArrayList<TableTimedModel> tables) {
   this.tables = tables;
  }

  @Override
  public boolean isEmpty()
   {
    boolean result = true;
    if ((tables != null) && (!tables.isEmpty())) {result = false;}
    return result;
   }

  /**
   * Метод добавляет модель таблицы со временем к списку.
   * @param table TableTimeModel добавляемая к списку таблица.
  */
  public void addTable(TableTimedModel table)
   {if (table != null) {if (this.tables == null) {this.tables = new ArrayList<TableTimedModel>();} this.tables.add(table);}}

  /**
   * Метод возвращает модель таблицы с указанием времени по указанному имени. Если список таблиц данной модели БД пуст,
   * или имя искомой таблицы пусто или таблица не найдена - метод возвращает значение null.
   * @param tableName String наименование искомой таблицы.
   * @return TableTimeModel найденная модель таблицы или null.
  */
  public TableTimedModel getTable(String tableName)
   {
    TableTimedModel result = null;
    if ((this.tables != null) && (!this.tables.isEmpty()) && !StringUtils.isBlank(tableName))
     {
      for (TableTimedModel table : this.tables)
       {
        if ((table != null) && (!StringUtils.isBlank(table.getTableName())) && (tableName.equals(table.getTableName())))
         {result = table;}
       }
     }
    return result;
   }

  /** Строковое представление данного объекта. */
  @Override
  public String toString()
   {
    StringBuilder dbString = new StringBuilder();
    dbString.append("\nDATABASE: ").append(this.getDbName());
    // Добавим инфу о количестве таблиц в БД и сам список
    dbString.append("\nTABLES COUNT: ");
    // Если список таблиц не пуст в цикле его формируем
    if ((this.tables != null) && (!this.tables.isEmpty()))
     {
      dbString.append(this.tables.size()).append("\nTABLES LIST: \n").append("----------\n");
      for (TableTimedModel table : this.tables) {dbString.append(table).append("\n");}
      dbString.append("----------\n");
     }
    // Если список таблиц пуст - просто скажем об ентом!
    else {dbString.append(0).append("\nTABLES LIST IS EMPTY!\n");}
    return dbString.toString();
   }

  /**
   * Метод сравнивает две модели БД с указанием времени и возвращает третий объект, который содержит разницу между
   * двумя данными.
   * @param dbTimedModel DatabaseTimeModel модель БД, с которой сравниваем текущую модель.
   * @return DatabaseTimeModel модель, содержащая разницу между указанными двумя.
   * @throws DBModelException ИС может возникать внутри метода при попытке инициализации внутренних
   * объектов-моделей баз данных с пустыми именами.
   */
  public DBTimedModel compareTo(DBTimedModel dbTimedModel) throws DBModelException
   {
    // Если логгер не инициализирован - инициализируем. Логгер может быть null, если объект был сериализован, а
    // затем десериализован (логгер объявлен как transient поле - оно не сериализуется).
    if (logger == null) {logger = Logger.getLogger(getClass().getName());}
    
    logger.debug("WORKING DatabaseTimeModel.compareTo().");
    DBTimedModel resultModel = null;
    // Если список таблиц текущей модели пуст - возвращаем null
    if ((this.tables != null) && (!this.tables.isEmpty()))
     {
      logger.debug("Tables list of current model is not empty.");
      // Если указанная в качестве параметра модель пуста или список ее таблиц пуст - возвращаем текущую модель
      if (dbTimedModel == null || dbTimedModel.getTables() == null || dbTimedModel.getTables().isEmpty())
       {
        logger.debug("Tables list of parameter-model is empty");
        resultModel = this;
       }
      else
       {
        logger.debug("Tables list of parameter-model is not empty.");

        // Проходим по списку таблиц текущей иодели БД и сравниваем их с моделью-параметром
        for (TableTimedModel table : this.tables)
         {
          TableTimedModel paramTable = dbTimedModel.getTable(table.getTableName());
          // Если такая таблица в БД-параметре найдена - сравним значение timestamp
          if(paramTable != null)
           {
            // Вначале проверим - не эквивалентны ли обе данные таблицы. Если они не эквивалентны, то выполняем
            // действия по анализу их "таймштампов"
            if (!table.equals(paramTable))
             // Если у таблицы-параметра нет даты (timestamp=null), то данная таблица должна быть
             // добавлена полностью (со значением timestamp=null)
             if (paramTable.getTimeStamp() == null)
              {
               // Если результат еще не инициализирован - инициализация
               if (resultModel == null) {resultModel = new DBTimedModel(this.getDbName());}
               resultModel.addTable(paramTable);
              }
             // Если дата есть - сравниваем ее с датой таблицы текущей модели
             else
              {
               // Если дата текущей таблицы пуста (timestamp=null), то такая таблица добавляется к результату
               // целиком - со значением timestamp=null
               if (table.getTimeStamp() == null)
                {
                 // Если результат еще не инициализирован - инициализация
                 if (resultModel == null) {resultModel = new DBTimedModel(this.getDbName());}
                 resultModel.addTable(table);
                }
               //
               else
                {
                 int compareResult = table.getTimeStamp().compareTo(paramTable.getTimeStamp());
                 if (compareResult > 0)
                  {
                   // Если результат еще не инициализирован - инициализация
                   if (resultModel == null) {resultModel = new DBTimedModel(this.getDbName());}
                   resultModel.addTable(paramTable);
                  }
                }
              }

           }
          // Если же такой таблицы в БД-параметре нет - ее надо добавить в результат с параметром
          // timestamp=null - т.е. данной таблицы просто нет
          else
           {
            // Если результат еще не инициализирован - инициализация
            if (resultModel == null) {resultModel = new DBTimedModel(this.getDbName());}
            resultModel.addTable(table);
           }
         } // END OF FOR CYCLE

       }
     }
    else logger.debug("Tables list of current model is empty.");

    return resultModel;
   }

  @Override
  public int getTablesCount()
   {int result = 0; if ((this.tables != null) && (!this.tables.isEmpty())) {result = this.tables.size();} return result;}

  /**
   * Метод предназначен только для тестирования данного класса.
   * @param args String[] параметры метода main.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger(DBTimedModel.class.getName());
    Logger logger = Logger.getLogger(DBTimedModel.class.getName());
    try
     {
      DBTimedModel model1 = new DBTimedModel("model1");
      DBTimedModel model2 = new DBTimedModel("model2");
      model1.addTable(new TableTimedModel("table1", Timestamp.valueOf("2001-08-01 00:00:00")));
      model1.addTable(new TableTimedModel("table2", Timestamp.valueOf("2001-03-02 00:00:00")));
      model1.addTable(new TableTimedModel("table3", Timestamp.valueOf("2001-09-01 00:00:00")));
      model1.addTable(new TableTimedModel("table4", Timestamp.valueOf("2001-05-04 00:00:00")));
      model1.addTable(new TableTimedModel("table5", Timestamp.valueOf("2001-01-01 00:00:00")));
      model1.addTable(new TableTimedModel("table6", null));
      model1.addTable(new TableTimedModel("table8", null));
      model2.addTable(new TableTimedModel("table1", Timestamp.valueOf("2001-07-01 00:00:00")));
      model2.addTable(new TableTimedModel("table2", Timestamp.valueOf("2001-03-01 00:00:00")));
      model2.addTable(new TableTimedModel("table3", Timestamp.valueOf("2001-09-01 00:00:00")));
      model2.addTable(new TableTimedModel("table4", Timestamp.valueOf("2001-05-03 00:00:00")));
      model2.addTable(new TableTimedModel("table5", null));
      model2.addTable(new TableTimedModel("table7", null));
      model2.addTable(new TableTimedModel("table8", Timestamp.valueOf("2001-01-01 00:00:00")));
      logger.info("RESULT: " + model2.compareTo(model1));
     } 
    catch (DBModelException e) {logger.error(e.getMessage());}

   }

 }