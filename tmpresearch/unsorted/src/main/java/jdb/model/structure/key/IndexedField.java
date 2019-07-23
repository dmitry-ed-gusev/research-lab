package jdb.model.structure.key;

import dgusev.dbpilot.DBConsts;
import jdb.exceptions.DBModelException;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * Класс реализует модель одного индексируемого поля. Содержит имя индекса, имя поля, тип индекса, признак
 * уникальности индекса. Индексируемое поле обязательно должно содержать имя индекса и имя поля. Оба параметра
 * хранятся в ВЕРХНЕМ регистре символов.<br>
 *
 * 26.12.2008 Теперь класс реализует интерфейс Comparable - для того, чтобы можно было сортировать экземпляры данного
 * класса в списке ArrayList по имени поля.
 *
 * 26.01.09 Для правильного сравнения индексируемых полей имя индекса и имя поля должны храниться в ВЕРХНЕМ
 * регистре символов.
 *
 * @author Gusev Dmitry (019gus)
 * @version 3.1 (DATE: 23.07.2010)
*/

// todo: методы equals() и hashCode() не используются и поэтому закомментированы. Раскомментить по необходимости. Проверить!
 
public class IndexedField implements Comparable, Serializable
 {
  static final long serialVersionUID = -4169826276815804319L;
  
  /** Наименование индекса. */
  private String    indexName;
  /** Наименование индексируемого поля. */
  private String    fieldName;
  /** Тип индекса. */
  private DBConsts.IndexType type;
  /** Признак уникальности индекса. */
  private boolean   isUnique;
  /** Признак - является ли данное поле членом первичного ключа таблицы. */
  private boolean   isPrimaryKey;

  public IndexedField(String indexName, String fieldName) throws DBModelException
   {
    if (!StringUtils.isBlank(indexName)) {this.indexName = indexName.toUpperCase();}
    else {throw new DBModelException("Index name is empty!");}
    if (!StringUtils.isBlank(fieldName)) {this.fieldName = fieldName.toUpperCase();}
    else {throw new DBModelException("Indexed field name is empty!");}
    this.isUnique     = false;
    this.isPrimaryKey = false;
    this.type         = null;
   }

  public String getIndexName() {return indexName;}

  public void setIndexName(String indexName) throws DBModelException
   {
    if (!StringUtils.isBlank(indexName)) {this.indexName = indexName.toUpperCase();}
    else {throw new DBModelException("Index name is empty!");}
   }

  public String getFieldName() {return fieldName;}

  public void setFieldName(String fieldName) throws DBModelException
   {
    if (!StringUtils.isBlank(fieldName)) {this.fieldName = fieldName.toUpperCase();}
    else {throw new DBModelException("Indexed field name is empty!");}
   }

  public DBConsts.IndexType getType() {
   return type;
  }

  public void setType(DBConsts.IndexType type) {
   this.type = type;
  }

  public boolean isUnique() {return isUnique;}
  public void setUnique(boolean unique) {isUnique = unique;}

  public boolean isPrimaryKey() {return isPrimaryKey;}
  public void setPrimaryKey(boolean primaryKey) {isPrimaryKey = primaryKey;}

  @Override
  public String toString()
   {
    return "[" + "indexName='" + indexName + '\'' + ", fieldName='" + fieldName + '\'' +
            ", type=" + type + ", isUnique=" + isUnique + ", isPrimaryKey=" + isPrimaryKey +']';
   }

  /**
   * Сравнение двух экземпляров данного класса. Сравнение производится по имени индексируемого поля, имени
   * индекса и по признаку уникальности индекса (имена индексов на разных СУБД могут не совпадать).
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
      IndexedField foreign = (IndexedField) obj;
      // Сравниваем имена индексируемых полей (имена индексов в разных СУБД могут не совпадать) и признаки
      // уникальности индексов
      //if (fieldName.equals(foreign.fieldName) && (indexName.equals(foreign.indexName)) &&
      //    ((isUnique && foreign.isUnique) || (!isUnique && !foreign.isUnique)))
      // result = true;
      if (fieldName.equals(foreign.fieldName)) result = true;
     }
    return result;
   }
  */

  /**
  @Override
  public int hashCode()
   {
    int result = fieldName.hashCode();
    //result     = 31*result + indexName.hashCode();
    //result     = 31*result + (isUnique ? 1 : 0);
    return result;
   }
  */

  @Override
  /***/
  public int compareTo(Object o)
   {
    IndexedField field = (IndexedField)o;
    // Первая сортировка - по имени поля
    int result = this.fieldName.compareTo(field.getFieldName());
    // Если имена полей совпадают - сортируем по имени индекса
    if (result == 0) {result = this.indexName.compareTo(field.getIndexName());}
    // Возвращаем результат сравнения
    return result;
   }

  /**
   * Метод предназначен только для тестирования данного класса.
   * @param args String[] параметры метода main.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger(IndexedField.class.getName());
    Logger logger = Logger.getLogger(IndexedField.class.getName());
    try
     {
      IndexedField field1 = new IndexedField("aaa", "bbb");
      IndexedField field2 = new IndexedField("aaa", "bbb");
      IndexedField field3 = new IndexedField("aaa", "zzz");

      logger.info(field1.equals(field2));
      logger.info(field2.equals(field1));
      logger.info(field1.equals(field3));
      logger.info(field3.equals(field2));

      logger.info(field1.compareTo(field2));
      logger.info(field2.compareTo(field1));
      logger.info(field1.compareTo(field3));
      logger.info(field3.compareTo(field2));

      logger.info(field1.hashCode() + " " + field2.hashCode() + " " + field3.hashCode());
     }
    catch (DBModelException e) {logger.error(e.getMessage());}
   }

 }