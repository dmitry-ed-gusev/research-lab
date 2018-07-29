package jdb.model.structure;

import jdb.exceptions.DBModelException;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * Класс реализует модель одного абстрактного поля из абстрактной таблицы БД. Значение поля не хранится, хранятся
 * следующие параметры поля: имя, тип поля (java) - значение типа int, тип поля (СУБД) - строковое наименование (String),
 * размерность поля. Имя поля хранится в ВЕРХНЕМ регистре символов - для обеспечения унификации поиска. Имя поля не
 * может быть пустым. При задании пустого имени поля (пустая строка или значение null) генерируется ИС.<br>
 *
 * 26.12.2008 Теперь класс реализует интерфейс Comparable - для того, чтобы можно было сортировать экземпляры данного
 * класса в списке ArrayList по имени поля.<br>
 *
 * @author Gusev Dmitry (019gus)
 * @version 3.1 (DATE: 23.07.2010)
*/

// todo: методы equals() и hashCode() не используются и поэтому закомментированы. Раскомментить по необходимости. Проверить!

public class FieldStructureModel implements Serializable, Comparable
 {
  static final long serialVersionUID = 3603083484070873288L;

  /** Наименование поля таблицы. */
  private String  name;
  /** Тип данных поля таблицы - тип JAVA. */
  private int     javaDataType;
  /**
   * Строковое наименование типа данных поля таблицы - тип СУБД. Для разных СУБД одни и теже типы данных
   * (совпадающие по типу JAVA) могут иметь различное строковое наименование.
  */
  private String  dbmsDataType;
  /** Размерность поля таблицы. */
  private int     size;
  /** Признак - может ли данное поле принимать значение NULL. */
  private boolean isNullable;
  /** Значение данного поля по умолчанию. Может быть пусто. */
  private String  defaultValue;

  /**
   * Конструктор по умолчанию.
   * @param name String имя, назначаемое новому созданному экземпляру данного класса.
   * @param javaDataType int тип поля данных.
   * @param dbmsDataType String наименование типа поля данных (строковое наименование).
   * @param size int размерность поля.
   * @throws DBModelException ИС возникает, если создается поле с пустым именем.
  */
  public FieldStructureModel(String name, int javaDataType, String dbmsDataType, int size) throws DBModelException
   {
    if (!StringUtils.isBlank(name)) {this.name = name.toUpperCase();}
    else {throw new DBModelException("Name of the field is empty!");}
    this.javaDataType = javaDataType;
    this.dbmsDataType = dbmsDataType;
    this.size         = size;
    this.isNullable   = true;
    this.defaultValue = null;
   }

  public String getName() {return name;}

  public void setName(String name) throws DBModelException
   {
    if (!StringUtils.isBlank(name)) {this.name = name.toUpperCase();}
    else {throw new DBModelException("Name of the field is empty!");}
   }

  public int getJavaDataType() {return javaDataType;}
  public void setJavaDataType(int javaDataType) {this.javaDataType = javaDataType;}
  public String getDbmsDataType() {return dbmsDataType;}
  public void setDbmsDataType(String dbmsDataType) {this.dbmsDataType = dbmsDataType;}
  public int getSize() {return size;}
  public void setSize(int size) {this.size = size;}
  public boolean isNullable() {return isNullable;}
  public void setNullable(boolean nullable) {isNullable = nullable;}
  public String getDefaultValue() {return defaultValue;}
  public void setDefaultValue(String defaultValue) {this.defaultValue = defaultValue;}

  /** Строковое представление экземпляра данного класса (модели поля таблицы). */
  @Override
  public String toString()
   {
    return "FieldStructureModel{name='" + name + '\'' + ", javaDataType=" + javaDataType + ", dbmsDataType='"
            + dbmsDataType + '\'' + ", size=" + size + ", isNullable=" + isNullable + ", defaultValue='"
            + defaultValue + '\'' + '}';
   }

  /** Метод сравнения двух объектов(экземпляров) данного класса. Экземпляры сравниваются только по имени поля. */
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
      // Приводим тип (теперь мы знаем, что объект имеет тип FieldStructureModel и не является нулевым)
      FieldStructureModel foreign = (FieldStructureModel) obj;
      // Сравниваем имена полей - если совпали, то проверим тип данных java - если это символьный тип, сравним
      // размерность - если совпала, то поля эквивалентны. Если же поля не символьного типа - то они сравниваются
      // только по именам.
      if (name.equals(foreign.name))
       {
        // Если оба поля имеют символьный тип данных (тьип данных JAVA) - сравним их размерность
        if (((this.javaDataType == Types.CHAR) || (this.javaDataType == Types.LONGNVARCHAR) ||
             (this.javaDataType == Types.LONGVARCHAR) || (this.javaDataType == Types.NCHAR) ||
             (this.javaDataType == Types.NVARCHAR) || (this.javaDataType == Types.VARCHAR)) &&
            ((foreign.javaDataType == Types.CHAR) || (foreign.javaDataType == Types.LONGNVARCHAR) ||
             (foreign.javaDataType == Types.LONGVARCHAR) || (foreign.javaDataType == Types.NCHAR) ||
             (foreign.javaDataType == Types.NVARCHAR) || (foreign.javaDataType == Types.VARCHAR)))
         {if (this.size == foreign.size) result = true;}
        // Если поля не символьного типа - они оказались эквивалентны по именам.
        else {result = true;}
       }
     }
    return result;
   }
  */

  /**
   * Метод возвращает хэш-код объекта. Хэш-код - положительное или отрицательное целое число. Эквивалентным
   * объектам должны соответствовать одинаковые хэш-коды. В генерации хэш-кода должны участвовать те поля,
   * которые участвуют в операции сравнения - метод equals() данного класса.
  */
  /**
  public int hashCode()
   {
    int result;
    result = name.hashCode();
    // Если поле символьное - в генерации хэша должен участвовать размер поля
    if ((this.javaDataType == Types.CHAR) || (this.javaDataType == Types.LONGNVARCHAR) ||
        (this.javaDataType == Types.LONGVARCHAR) || (this.javaDataType == Types.NCHAR) ||
        (this.javaDataType == Types.NVARCHAR) || (this.javaDataType == Types.VARCHAR))
     {result = 31 * result + size;}
    //result = 31 * result + javaDataType;
    //result = 31 * result + dbmsDataType.hashCode();
    return result;
   }
  */
  
  @Override
  /** Сравнение двух экземпляров данного класса на предмет больше-меньше. */
  public int compareTo(Object o)
   {
    FieldStructureModel field = (FieldStructureModel)o;
    return this.name.compareTo(field.getName()); // <- сортировка в прямом алфавитном порядке
    // return field.getName().compareTo(this.name); // <- сортировка будет в обратном алфавитном порядке
   }

  /**
   * Метод предназначен только для тестирования данного класса.
   * @param args String[] параметры метода main.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger(FieldStructureModel.class.getName());
    Logger logger = Logger.getLogger(FieldStructureModel.class.getName());
    try
     {
      FieldStructureModel field1 = new FieldStructureModel("aaa", 0, "", 10);
      FieldStructureModel field2 = new FieldStructureModel("aaa", 0, "", 10);
      FieldStructureModel field3 = new FieldStructureModel("bbb", 0, "", 10);

      logger.info(field1.equals(field2));
      logger.info(field2.equals(field1));
      logger.info(field1.equals(field3));

      logger.info(field1.compareTo(field2));
      logger.info(field2.compareTo(field1));
      logger.info(field3.compareTo(field3));

      logger.info(field1.hashCode() + " " + field2.hashCode() + " " + field3.hashCode());
     }
    catch (DBModelException e) {logger.error(e.getMessage());}

   }

 }