package jdb.model.structure;

import jdb.exceptions.DBModelException;
import jdb.model.TableModel;
import jdb.model.structure.key.IndexedField;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.TreeSet;

/**
 * Класс реализует модель абстрактной таблицы БД. Хранится список полей таблицы - список объектов типа
 * FieldStructureModel. Значения полей не хранятся. Имя таблицы сохраняется(должно сохраняться) всегда в
 * верхнем регистре (см. комментарий в классе DBStructureModel). У таблицы всегда должно быть имя. Пустое
 * имя таблицы (пустая строка или null) не допускается (возбуждается ИС).<br>
 *
 * 26.12.2008 Теперь класс реализует интерфейс Comparable - для того, чтобы можно было сортировать экземпляры данного
 * класса в списке ArrayList по имени таблицы.<br>
 *
 * 29.12.2008 У таблицы в списке полей не должно быть двух полей с одинаковыми именами (двух одинаковых экземпляров
 * класса FieldStructureModel). Также не должно быть двух одинаковых индексируемых полей - двух экземпляров класса
 * IndexedField с совпадающими именами индексов и именами полей. Также для сравнения разных таблиц важна сортировка
 * полей таблицы в списке (объектов FieldStructureModel) и индексируемых полей в списке (объектов IndexedField).
 * Для поддержания автоматической сортировки поля таблицы и индексируемые поля таблицы хранятся в списках с
 * сортировкой - TreeSet. Также указанные классы (FieldStructureModel и IndexedField) реализуют интерфейс Comparable,
 * который предусматривает метод сравнения compareTo().
 *
 * @author Gusev Dmitry (019gus)
 * @version 5.0 (DATE: 23.07.2010)
*/

// todo: методы equals() и hashCode() не используются и поэтому закомментированы. Раскомментить по необходимости. Проверить!
 
public class TableStructureModel extends TableModel implements Serializable, Comparable
 {
  // Поле используется для совместимости последующих версий класса с предыдущими (для механизма сериализации)
  static final long serialVersionUID = 2562343871344731251L;

  /** Компонент-логгер данного класса. */
  //private transient Logger logger = Logger.getLogger(this.getClass().getName());

  /** Список полей таблицы - список объектов типа TableFieldModel. */
  private TreeSet<FieldStructureModel> fields  = null;
  /** Список индексируемых полей таблицы - список объектов типа IndexedField. */
  private TreeSet<IndexedField>        indexes = null;

  /**
   * Конструктор сразу инициализирует имя таблицы. Имя таблицы переводится в ВЕРХНИЙ регистр!
   * @param tableName String имя создаваемой таблицы (ее модели).
   * @throws DBModelException ИС возникает если при создании таблицы указано пустое имя таблицы.
  */
  public TableStructureModel(String tableName) throws DBModelException {super(tableName);}

  public TreeSet<FieldStructureModel> getFields()            {return fields;}
  public void setFields(TreeSet<FieldStructureModel> fields) {this.fields = fields;}
  public TreeSet<IndexedField> getIndexes()                  {return indexes;}
  public void setIndexes(TreeSet<IndexedField> indexes)      {this.indexes = indexes;}

  /**
   * Метод добавляет одно поле (объект FieldStructureModel) к списку полей таблицы (поле добавляется, только если оно не пусто).
   * @param field FieldStructureModel добавляемое к списку поле.
  */
  public void addField(FieldStructureModel field)
   {
    if ((field != null) && (!StringUtils.isBlank(field.getName())))
     {
      if (this.fields == null) {this.fields = new TreeSet<FieldStructureModel>();}
      this.fields.add(field);
     }
   }

  /**
   * Метод добавляет один индекс (объект IndexedField) к списку индексов таблицы (добавляется только непустой индекс).
   * @param index IndexedField добавляемый индекс.
  */
  public void addIndex(IndexedField index)
   {
    if (index != null)
     {
      if (this.indexes == null) {this.indexes = new TreeSet<IndexedField>();}
      this.indexes.add(index);
     }
   }

  /**
   * Метод возвращает модель поля TableFieldModel по его имени fieldName. Если указанное имя поля пусто или такого
   * поля не найдено - метод возвращает значение null.
   * @param fieldName String имя искомого поля.
   * @return FieldStructureModel найденная по имени модель поля или значение null.
  */
  public FieldStructureModel getField(String fieldName)
   {
    FieldStructureModel field = null;
    if (!StringUtils.isBlank(fieldName))
     {
      for (FieldStructureModel localField : this.fields)
       {if (fieldName.toUpperCase().equals(localField.getName())) {field = localField;}}
     }
    return field;
   }

  /** Строковое представление данного объекта (модели таблицы). */
  @Override
  public String toString()
   {
    StringBuilder tableString = new StringBuilder();
    tableString.append(" TABLE: ");
    // Если есть схема - укажем ее
    if (!StringUtils.isBlank(this.getTableSchema())) {tableString.append(this.getTableSchema()).append(".");}
    tableString.append(this.getTableName()).append(" (TYPE: ").append(this.getTableType()).append(")");
    // Поля модели таблицы
    tableString.append("; FIELDS COUNT: ");
    // Добавляем поля (если они есть, если нет - сообщим об этом)
    if ((this.fields != null) && (!this.fields.isEmpty()))
     {
      tableString.append(this.fields.size()).append("\n");
      tableString.append("  FIELDS LIST:\n");
      for (FieldStructureModel field : this.fields) {tableString.append("   ").append(field).append("\n");}
     }
    // Сообщаем об отсутствии полей в таблице 
    else {tableString.append(0).append("\n  FIELDS LIST IS EMPTY!\n");}

    // Добавляем индексы (если они есть, если нет - сообщим об этом)
    if ((this.indexes != null) && (!this.indexes.isEmpty()))
     {
      tableString.append("  INDEXES LIST:\n");
      for (IndexedField index : this.indexes) {tableString.append("   ").append(index).append("\n");}
     }
    // Сообщаем об отсутствии индексов в таблице
    else {tableString.append("  INDEXES LIST IS EMPTY!\n");}
    // Возвращаем результат
    return tableString.toString();
   }

  /**
   * Метод возвращает CSV-список (CSV - comma separated values - список, разделенный запятыми) полей данной таблицы
   * или значение null, если список пуст. Все поля, имеющие значения null будут проигнорированы. Если же список полей
   * состоит только из null-полей, то метод также вернет значение null.
   * Данный метод является null-safe - корректно обрабатывает null-значения.
   * @return String CSV-список полей данной таблицы или значение null.
  */
  public String getCSVFieldsList()
   {
    StringBuilder csvList = null;
    if ((this.fields != null) && (!this.fields.isEmpty()))
     {
      for (FieldStructureModel field : this.fields)
       {
        // Если очередное полученное из списка поле не пусто - добавляем его к результирующему списку
        if ((field != null) && (!StringUtils.isBlank(field.getName())))
         {
          // Если еще не инициализирован результат - инициализация
          if (csvList == null) {csvList = new StringBuilder();}
          // Если же результат уже инициализирован - в нем уже есть поля, добавим к ним запятую
          else {csvList.append(", ");}
          csvList.append(field.getName());
         }
       }
     }
    // Такая конструкция нужна для того, чтобы была одна точка выхода из метода (один оператор return).
    String result;
    if (csvList == null) {result = null;} else {result = csvList.toString();}
    return result;
   }

  /**
   * Метод возвращает CSV-список (CSV - comma separated values - список, разделенный запятыми) индексируемых полей
   * данной таблицы или значение null, если список пуст. Все индексируемые поля, имеющие значение null будут
   * проигнорированы. Если же список индексируемых полей состоит только из null-полей, то метод также вернет
   * значение null.
   * Данный метод является null-safe - корректно обрабатывает null-значения.
   * @return String CSV-список индексируемых полей данной таблицы или значение null.
  */
  public String getCSVIndexedFieldsList()
   {
    StringBuilder csvList = null;
    if ((this.indexes != null) && (!this.indexes.isEmpty()))
     {
      for (IndexedField field : this.indexes)
       {
        // Если полученное поле не пусто - добавляем его к результату
        if ((field != null) && (!StringUtils.isBlank(field.getFieldName())))
         {
          // Если еще не инициализирован результат - инициализация
          if (csvList == null) {csvList = new StringBuilder();}
          // Если же результат уже инициализирован - в нем уже есть поля, добавим к ним запятую
          else {csvList.append(", ");}
          csvList.append(field.getFieldName());
         }
       }
     }
    // Такая конструкция нужна для того, чтобы была одна точка выхода из метода (один оператор return).
    String result;
    if (csvList == null) result = null; else result = csvList.toString();
    return result;
   }

  /**
   * Метод возвращает разделенный запятыми список полей, участвующих в первичном ключе данной таблицы.
   * @return String список полей первичного ключа.
  */
  public String getCSVPKFieldsList()
   {
    StringBuilder csvList = null;
    // Если есть индексы - среди них может быть и первичный ключ
    if ((this.indexes != null) && (!this.indexes.isEmpty()))
     {
      for (IndexedField field : this.indexes)
       {
        // Если полученное поле не пусто и является первичным ключом - работаем
        if ((field != null) && (!StringUtils.isBlank(field.getFieldName())) && (field.isPrimaryKey()))
         {
          // Если есть первичный ключ - инициализируем список
          if (csvList == null) {csvList = new StringBuilder();}
          // Если же результат уже инициализирован - в нем уже есть поля, добавим запятую
          else {csvList.append(", ");}
          csvList.append(field.getFieldName());
         }
       }
     }
    // Такая конструкция нужна для того, чтобы была одна точка выхода из метода (один оператор return).
    String result;
    if (csvList == null) result = null; else result = csvList.toString();
    return result;
   }

  /**
   * Метод сравнения двух объектов(экземпляров) данного класса. В данной версии сравниваются схемы, которым принадлежат
   * таблицы, имена таблиц, списки полей таблиц (списки объектов класса FieldStrucutureModel), списки индексируемых
   * полей (списки объектов класса IndexedField).
  */
  /**
  @Override
  public boolean equals(Object obj)
   {
    // Результат сравнения экземпляров данного класса
    boolean result = false;
    // Быстрая проверка идентичности экземпляров
    if (this == obj) result = true;
    // Если быстрая проверка не прошла - проверяем далее - если явный параметр null или классы не совпадают
    // (данные экземпляры от разных классов) - возвращается значение false и проверки прекращаются. Если же это
    // экземпляры одного класса - приводим внешний объект к данному классу и проверяем соответствие имен полей.
    else if (obj != null && this.getClass() == obj.getClass())
     {
      // Приводим тип (теперь мы знаем, что объект имеет тип TableModel и не является нулевым)
      TableStructureModel foreign = (TableStructureModel) obj;

      // Сравним списки полей таблиц, списки индексов таблиц и имена таблиц - если совпадают, значит таблицы идентичны
      if (this.getTableName().equals(foreign.getTableName())) // <- сравниваем имена таблиц
       {
        // Теперь сравниваем схемы двух таблиц. Если схемы равны (обе НУЛЛ или (обе не НУЛЛ и равны)), то продолжаем сравнение.
        String thisSchema    = this.getTableSchema();
        String foreignSchema = foreign.getTableSchema();
        if (
            (StringUtils.isBlank(thisSchema) && StringUtils.isBlank(foreignSchema)) ||
            (!StringUtils.isBlank(thisSchema) && !StringUtils.isBlank(foreignSchema) && thisSchema.equals(foreignSchema))
           )
         {
          // Сравниваем списки полей. Списки могут быть пустыми - нужна обработка этой ситуации. Если списки не
          // совпали - дальнейшая проверка не нужна. Также если оба списка полей пусты - нет смысла сравнивать индексы.

          // Если оба списка полей не пусты - сравниваем их содержимое. Также сравниваем индексы.
          if ((fields != null) && (!fields.isEmpty()) && (foreign.fields != null) && (!foreign.fields.isEmpty()))
           {
            // Если содержимое непустых списков полей совпадает - сравниваем индексы
            if (fields.equals(foreign.fields))
             {
              // Если оба списка индексов не пусты - сравниваем их содержимое.
              if ((indexes != null) && (!indexes.isEmpty()) && (foreign.indexes != null) && (!foreign.indexes.isEmpty()))
               {
                // Списки индексов требуют тщательной сверки - если напрямую списки индексов не совпали, это может означать,
                // что сравниваются модели разных баз данных - тогда генерируем и сравниваем простые списки индексируемых
                // полей - все поля индексируемые в одной таблице, должны быть индуксируемыми и в другой
                if (indexes.equals(foreign.indexes)) {result = true;} // <- простое сравнение списков индексов
                // Если напрямую списки индексов не совпали - проводим суррогатную проверку по списку индексируемых полей
                else if (this.getUniqueIndexesList().equals(foreign.getUniqueIndexesList())) {result = true;}
               }
              // Проверим - если списки индексов пусты одновременно - они совпадают.
              else if (((indexes == null) || (indexes.isEmpty())) && ((foreign.indexes == null) || (foreign.indexes.isEmpty())))
               {result = true;}
             }
           }
          // Если пуст хотя бы один список - проверим, пусты они вместе или нет. Если списки пусты вместе -
          // значит они совпадают.
          else if (((fields == null) || (fields.isEmpty())) && ((foreign.fields == null) || (foreign.fields.isEmpty())))
           {result = true;}
         } // конец блока сравнения схем двух таблиц
       } // конец блока сравнения имен двух таблиц

     }
    return result;
   }
  */

  /**
   * Метод возвращает хэш-код объекта. Хэш-код - положительное или отрицательное целое число. Эквивалентным
   * объектам должны соответствовать одинаковые хэш-коды.
  */
  /**
  @Override
  public int hashCode()
   {
    int result;
    result = this.getTableName().hashCode();
    if (this.getTableSchema() != null) {result = 31*result + this.getTableSchema().hashCode();}
    result = 31*result + (fields != null ? fields.hashCode() : 0);
    result = 31*result + (indexes != null ? indexes.hashCode() : 0);
    return result;
   }
  */
  
  /** Метод помогает сортировать таблицы в списке по именам. */
  @Override
  public int compareTo(Object o)
   {
    TableStructureModel table = (TableStructureModel) o;
    int result;
    if (!StringUtils.isBlank(this.getTableSchema()) && !StringUtils.isBlank(table.getTableSchema()))
     {result = (this.getTableSchema() + "." + this.getTableName()).compareTo(table.getTableSchema() + "." + table.getTableName());}
    else
     {result = this.getTableName().compareTo(table.getTableName());}
    return result;
   }

  /**
   * Метод формирует и возвращает список имен индексируемых полей. Список без повторений (на основе коллекции TreeSet).
   * @return TreeSet<String> простой строковый список индексируемых полей.
  */
  public TreeSet<String> getUniqueIndexesList()
   {
    TreeSet<String> indexesList = null;
    // Если список индексов пуст - результат тоже будет пуст
    if ((this.indexes != null) && (!this.indexes.isEmpty()))
     {
      indexesList = new TreeSet<String>();
      for (IndexedField index : this.indexes) indexesList.add(index.getFieldName());
     }
    return indexesList;
   }

  /**
   * @param foreign TableStructureModel
   * @return String 
  */
  public String getDifferenceReport(TableStructureModel foreign)
   {
    // Результирующий отчет
    StringBuilder report;
    // Если переданный в качестве параметра объект не пуст - обрабатываем его
    if (foreign != null)
     {
      report = new StringBuilder("\n---> Tables [" + foreign.getTableName() + "] difference report. ---\n");
      report.append("----> SIMPLE EQUALS: ").append(this.equals(foreign)).append("\n");
      
      // Список полей текущей таблицы
      report.append("----> FIELDS [").append(this.getTableName()).append("] current: ");
      // Если список полей пуст, то надо предотвратить NullPointerException
      if ((this.fields == null) || (this.fields.isEmpty())) report.append("NO FIELDS.\n");
      else report.append("(").append(this.fields.size()).append(") ").append(this.getCSVFieldsList()).append("\n");
      // Список полей внешней таблицы
      report.append("----> FIELDS [").append(foreign.getTableName()).append("] foreign: ");
      // Если список полей пуст, то надо предотвратить NullPointerException
      if ((foreign.fields == null) || (foreign.fields.isEmpty())) report.append("NO FIELDS.\n");
      else report.append("(").append(foreign.fields.size()).append(") ").append(foreign.getCSVFieldsList()).append("\n");
      // Простое сравнение списков полей текущей и внешней таблиц.
      report.append("----> FIELDS simple current.equals(foreign) = ");
      // Если оба списка не пусты - сравним их содержимое.
      if (this.fields != null && !this.fields.isEmpty() && foreign.getFields() != null && !foreign.getFields().isEmpty())
       {report.append(this.fields.equals(foreign.getFields())).append("\n");}
      // Если оба списка пусты одновременно - они эквивалентны
      else if (((this.fields == null) || (this.fields.isEmpty())) && ((foreign.getFields() == null) || (foreign.getFields().isEmpty())))
       {report.append(true).append("\n");}
      // Если же ни одна из проверок не прошла - списки разные
      else {report.append(false).append("\n");} 

      // Список индексируемых полей тукещей таблицы
      report.append("----> INDEXES [").append(this.getTableName()).append("] current: ");
      // Если список индексов пуст, то надо предотвратить NullPointerException
      if ((this.indexes == null) || (this.indexes.isEmpty())) report.append("NO INDEXES.\n");
      else report.append("(").append(this.indexes.size()).append(") ").append(this.getCSVIndexedFieldsList()).append("\n");
      // Список индексируемых полей внешней таблицы
      report.append("----> INDEXES [").append(foreign.getTableName()).append("] foreign: ");
      // Если список индексов пуст, то надо предотвратить NullPointerException
      if ((foreign.indexes == null) || (foreign.indexes.isEmpty())) report.append("NO INDEXES.\n");
      else report.append("(").append(foreign.indexes.size()).append(") ").append(foreign.getCSVIndexedFieldsList()).append("\n");
      // Простое сравнение списков индексов текущей и внешней таблиц
      report.append("----> INDEXES simple current.equals(foreign) = ");
      // Если оба списка не пусты - сравним их содержимое.
      if (this.indexes != null && !this.indexes.isEmpty() && foreign.getIndexes() != null && !foreign.getIndexes().isEmpty())
       {
        // Списки индексов требуют тщательной сверки - если напрямую списки индексов не совпали, это может означать,
        // что сравниваются модели разных баз данных - тогда генерируем и сравниваем простые списки индексируемых
        // полей - все поля индексируемые в одной таблице, должны быть индуксируемыми и в другой
        if (this.indexes.equals(foreign.getIndexes())) report.append(true).append("\n");
        // Если напрямую списки индексов не совпали - проводим суррогатную проверку по списку индексируемых полей
        else if (this.getUniqueIndexesList().equals(foreign.getUniqueIndexesList())) {report.append(true).append("\n");}
        // Если же и так индексы не совпали - значит они все-таки разные :)
        else report.append(false).append("\n");
       }
      // Если оба списка пусты одновременно - они эквивалентны
      else if (((this.indexes == null) || (this.indexes.isEmpty())) &&
               ((foreign.getIndexes() == null) || (foreign.getIndexes().isEmpty())))
       {report.append(true).append("\n");}
      // Если же ни одна из проверок не прошла - списки разные
      else {report.append(false).append("\n");}
     }
    // Если переданный параметр пуст - скажем об этом в отчете
    else {report = new StringBuilder("Foreign table object is NULL!");}
    return report.toString();
   }

  /**
   * Данный метод предназначен только для тестирования класса.
   * @param args String[] параметры метода main.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger(TableStructureModel.class.getName());
    Logger logger = Logger.getLogger(TableStructureModel.class.getName());
    try
     {
      TableStructureModel table  = new TableStructureModel("ffff");
      FieldStructureModel field  = new FieldStructureModel("123", 0, "", 0);
      FieldStructureModel field2 = new FieldStructureModel("456", 0, "", 0);
      IndexedField        index1 = new IndexedField("aaa", "bbb");
      IndexedField        index2 = new IndexedField("xxx", "bbb");
      table.addField(field);
      table.addField(field2);
      table.addIndex(index1);
      table.addIndex(index2);
      logger.info(table + " \nFIELDS: " + table.getCSVFieldsList());
      logger.info(index1.compareTo(index2) + " " + index1.equals(index2));
     }
    //catch (EmptyFieldNameException e)      {logger.error(e.getMessage());}
    //catch (EmptyIndexNameException e)      {logger.error(e.getMessage());}
    //catch (EmptyIndexFieldNameException e) {logger.error(e.getMessage());}
    catch (DBModelException e) {logger.error(e.getMessage());}
   }

 }