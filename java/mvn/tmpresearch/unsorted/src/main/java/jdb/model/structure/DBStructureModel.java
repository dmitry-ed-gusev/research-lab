package jdb.model.structure;

import jdb.exceptions.DBModelException;
import jdb.model.DBModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Класс реализует модель структуры абстрактной базы данных. Модель содержит список таблиц (список объектов типа
 * TableStructureModel), каждый элемент списка - таблица, содержащая список полей (список объектов типа
 * FieldStructureModel). Никакие данные из БД не хранятся - хранится только информация о структуре БД - таблицы,
 * поля и т.п. Информация о маппингах типов данных (DBMS->JAVA) не хранится. Имена всех таблиц сохраняются в
 * верхнем регистре - т.к. например для dbf-файлов таблицы могут находиться в файлах с именами как строчными
 * буквами, так и прописными, и это будут РАЗНЫЕ таблицы. Имя самой БД также хранится в верхнем регистре символов.
 * БД обязательно должна иметь не пустое (пустая строка или значение null) имя. При задании пустого имени БД будет
 * генерироваться ИС.<br>
 * @author Gusev Dmitry (019gus)
 * @version 3.1 (DATE: 26.07.2010)
*/

// todo: методы equals() и hashCode() не используются и поэтому закомментированы. Раскомментить по необходимости. Проверить!
 
public class DBStructureModel extends DBModel implements Serializable
 {
  // Поле используется для совместимости последующих версий класса с предыдущими (для механизма сериализации)
  static final long serialVersionUID = 6101916045951918449L;

  /** Компонент-логгер данного класса. */
  //private transient Logger logger = Logger.getLogger(getClass().getName());

  /** Список таблиц БД. */
  private TreeSet<TableStructureModel> tables = null;

  /**
   * Конструктор по умолчанию. Обязательно инициализируется имя БД.
   * @param dbName String имя создаваемой модели БД. Обязательно не пустое!
   * @throws DBModelException ИС возникает, если мы пытаемся создать БД с пустым именем.
  */
  public DBStructureModel(String dbName) throws DBModelException {super(dbName);}

  @Override
  public boolean isEmpty()
   {
    boolean result = true;
    if ((tables != null) && (!tables.isEmpty())) {result = false;}
    return result;
   }

  public TreeSet<TableStructureModel> getTables() {return tables;}
  public void setTables(TreeSet<TableStructureModel> tables) {this.tables = tables;}

  /**
   * Метод возвращает модель таблицы БД по имени, если модель построена и такая таблица существует.
   * Метод является null-safe - корректно обрабатывает null-значения.
   * @param tableName String имя таблицы, модель которой возвращает метод.
   * @return TableModel модель таблицы или null.
  */
  public TableStructureModel getTable(String tableName)
   {
    TableStructureModel tableModel = null;
    if ((this.tables != null) && (!this.tables.isEmpty()) && !StringUtils.isBlank(tableName))
     {
      // Поиск таблицы в списке (т.к. имена таблиц хранятся в верхнем регистре, переводим полученное имя в верхний регистр)
      for (TableStructureModel table : this.tables)
       {
        if ((table != null) && (!StringUtils.isBlank(table.getTableName())) &&
            (table.getTableName().equals(tableName.toUpperCase())))
         {tableModel = table;}
       }
     }
    return tableModel;
   }

  /**
   * Метод добавляет одну таблицу к списку таблиц данной модели БД. Таблица добавляется к списку только если
   * она не пустая - объект TableModel не равен null.
   * @param table TableModel добавляемая к списку таблица.
  */
  public void addTable(TableStructureModel table)
   {
    if (table != null)
     {if (this.tables == null) this.tables = new TreeSet<TableStructureModel>(); this.tables.add(table);}
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
      dbString.append(this.tables.size()).append("\nTABLES LIST: \n").append("----------\n\n");
      for (TableStructureModel table : this.tables) {dbString.append(table).append("\n");}
      dbString.append("----------\n");
     }
    // Если список таблиц пуст - просто скажем об ентом!
    else {dbString.append(0).append("\nTABLES LIST IS EMPTY!\n");}
    return dbString.toString();
   }

  /**
   * Метод сравнения двух объектов(экземпляров) данного класса. Сравниваются только списки таблиц, имена баз
   * данных НЕ сравниваются. 
  */
  /**
  @Override
  public boolean equals(Object obj)
   {
    //logger.debug("***[EQUALS] Working equals() method."); // <- данный вывод нужен только для глубокой отладки
    
    // Результат сравнения экземпляров данного класса
    boolean result = false;
    // Быстрая проверка идентичности экземпляров
    if (this == obj) result = true;
    // Если быстрая проверка не прошла - проверяем далее - если явный параметр null или классы не совпадают
    // (данные экземпляры от разных классов) - возвращается значение false и проверки прекращаются. Если же это
    // экземпляры одного класса - приводим внешний объект к данному классу и проверяем соответствие списков таблиц.
    else if (obj != null && this.getClass() == obj.getClass())
     {
      // Приводим тип (теперь мы знаем, что объект имеет тип DatabaseModel и не является нулевым)
      DBStructureModel foreign = (DBStructureModel) obj;

      // Если списки таблиц баз данных не пусты - тогда проверим их содержимое
      if ((tables != null) && (!tables.isEmpty()) && (foreign.tables != null) && (!foreign.tables.isEmpty()))
       {
        //logger.debug("***[EQUALS] tables lists are not empty!"); // <- данный вывод нужен только для глубокой отладки
        // Проверим содержимое полей - если оно совпадает, то классы эквивалентны. Не сравниваем имена баз
        // данных - т.к. базы с разными именами могут иметь одинаковую структуру.
        // Сравнивать списки таблиц в базах данных необходимо поэлементно, т.к. при простом сравнении при
        // одинаковом размере списков метод возвращает true
        if (this.getTables().equals(foreign.getTables()))
         {
          //logger.debug("***[EQUALS] simple equals -> true. (result->" + result + ")"); // <- данный вывод нужен только для глубокой отладки
          result = true;

          // Поэлементное сравнение списков таблиц. Вначале сравниваем список таблиц текущей БД со
          // списком таблиц внешней БД.
          Iterator currentIterator = this.tables.iterator();
          //logger.debug("***[EQUALS] processing current-to-foreign"); // <- данный вывод нужен только для глубокой отладки
          while (currentIterator.hasNext() && result)
           {
            boolean found = false;
            TableStructureModel currentTable = (TableStructureModel)currentIterator.next();
            Iterator foreignIterator = foreign.getTables().iterator();
            while (foreignIterator.hasNext() && !found)
             {
              TableStructureModel foreignTable = (TableStructureModel)foreignIterator.next();
              if (currentTable.equals(foreignTable)) found = true;

              // Данный вывод нужен только для глубокой отладки
              //logger.debug("***[EQUALS] current[" + currentTable.getTableName() + "]->foreign[" +
              //        foreignTable.getTableName() + "] (found -> " + found + ")" + currentTable.equals(foreignTable));
   
             }
            if (!found) result = false;
           }
          
          // Если предыдущий цикл завершился успешно (все таблицы текущей БД найдены во внешней), то сравниваем
          // список таблиц внешней БД со списком таблиц текущей БД
          Iterator foreignIterator = foreign.getTables().iterator();
          //logger.debug("***[EQUALS] processing foreign-to-current"); // <- данный вывод нужен только для глубокой отладки
          while (foreignIterator.hasNext() && result)
           {
            boolean found = false;
            TableStructureModel foreignTable = (TableStructureModel)foreignIterator.next();
            currentIterator = this.tables.iterator();
            while (currentIterator.hasNext() && !found)
             {
              TableStructureModel currentTable = (TableStructureModel)currentIterator.next();
              if (foreignTable.equals(currentTable)) found = true;

              // Данный вывод нужен только для глубокой отладки
              //logger.debug("***[EQUALS] foreign[" + foreignTable.getTableName() + "]->current[" +
              //        currentTable.getTableName() + "] (found -> " + found + ")" + foreignTable.equals(currentTable));
              
             }
            if (!found) result = false;

           }
         }
        // Простая проверка не прошла
        else
         {
          //logger.debug("***[EQUALS] simple equals -> false. (result->" + result + ")"); // <- данный вывод нужен только для глубокой отладки
         }
       }
      // Если списки таблиц баз данных пусты одновременно - базы данных также эквивалентны
      else if (((tables == null) || (tables.isEmpty())) && ((foreign.tables == null) || (foreign.tables.isEmpty())))
       {result = true;}
     }
    return result;
   }
  */

  /**
   * Метод возвращает хэш-код объекта. Хэш-код - положительное или отрицательное целое число. Эквивалентным
   * объектам должны соответствовать одинаковые хэш-коды.
  */
  //@Override
  //public int hashCode() {return tables.hashCode();}

  /**
   * Метод возвращает список таблиц данной БД. Список содержит только строковые имена таблиц, а не сами объекты
   * типа TableModel. Если в данной модели БД нет таблиц, будет возвращено значение null.
   * @return ArrayList<String> список имен таблиц данной БД или значение null.
  */
  public ArrayList<String> getTablesList()
   {
    ArrayList<String> tablesList = null;
    if ((this.tables != null) && (!this.tables.isEmpty()))
     {
      for (TableStructureModel table : this.tables)
       {
        if (table != null)
         {if (tablesList == null) {tablesList = new ArrayList<String>();} tablesList.add(table.getTableName());}
       }
     }
    return tablesList;
   }

  /**
   * Метод возвращает список (разделенный запятыми) таблиц текущей БД.
   * @return String полученный список таблиц.
  */
  public String getCSVTablesList()
   {
    StringBuilder csvList = null;
    if ((this.tables != null) && (!this.tables.isEmpty()))
     {
      Iterator iterator = this.tables.iterator();
      csvList = new StringBuilder();
      while (iterator.hasNext())
       {
        TableStructureModel table = (TableStructureModel) iterator.next();
        if (table != null)
         {
          csvList.append(table.getTableName());
          if (iterator.hasNext()) csvList.append(", ");
         }
       }
     }
    String result;
    if (csvList == null) result = null; else result = csvList.toString();
    return result;
   }

  @Override
  public int getTablesCount()
   {int result = 0; if ((this.tables != null) && (!this.tables.isEmpty())) {result = this.tables.size();} return result;}

  /**
   * Данный метод предназначен только для тестирования класса.
   * @param args String[] параметры метода main.
  */
  public static void main(String[] args)
   {
    Logger logger = Logger.getLogger(DBStructureModel.class.getName());

    try
     {
      TableStructureModel table = new TableStructureModel("ffff");
      FieldStructureModel field  = new FieldStructureModel("123", 0, "", 0);
      FieldStructureModel field2 = new FieldStructureModel("456", 0, "", 0);
      table.addField(field);
      table.addField(field2);
      TableStructureModel table2 = new TableStructureModel("ffff2");
      FieldStructureModel field3 = new FieldStructureModel("987", 0, "", 0);
      FieldStructureModel field4 = new FieldStructureModel("654", 0, "", 0);
      table2.addField(field3);
      table2.addField(field4);
      DBStructureModel database = new DBStructureModel("dddd");
      database.addTable(table);
      database.addTable(table2);
      logger.info(database);
     }
    catch (DBModelException e) {logger.error(e.getMessage());}

   }

 }