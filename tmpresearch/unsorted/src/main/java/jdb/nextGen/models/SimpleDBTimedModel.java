package jdb.nextGen.models;

import jdb.nextGen.exceptions.JdbException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;

/**
 * Класс простой модели БД с указанием времени для каждой таблицы (указывается максимальное значение поля timestamp).
 * Класс является почти-immutable - неизменным, т.е. для созданного экземпляра класса нельзя поменять состояние,
 * возможно только добавление таблиц во внутренний список.
 * @author Gusev Dmitry (Гусев Дмитрий)
 * @version 3.0 (DATE: 31.05.2011)
*/

public final class SimpleDBTimedModel implements Serializable
 {
  /** Для совместимости с последующими версиями данного класса (при сериализации/десериализации). */
  static final long serialVersionUID = -6220865439038989908L;

  // Имя БД
  private final String                     dbName;
  // Карта пар значений (имя таблицы, время). Поле инициализируется при создании экземпляра класса.
  private final HashMap<String, Timestamp> tables = new HashMap<String, Timestamp>();

  /***/
  public SimpleDBTimedModel(String dbName) throws JdbException
   {
    // Имя БД - всегда непустое!
    if (!StringUtils.isBlank(dbName)) {this.dbName = dbName.toUpperCase();}
    // Имя БД пусто - ошибка!
    else {throw new JdbException("Empty db name!");}
   }

  public String getDbName() {return dbName;}

  /***/
  public void addTable(String name, Timestamp timestamp) throws JdbException
   {
    if (!StringUtils.isBlank(name)) {tables.put(name.toUpperCase(), timestamp);}
    else                            {throw new JdbException("Empty table name!");}
   }

  /***/
  public Timestamp getTimestampForTable(String tableName)
   {
    Timestamp result = null;
    if (!StringUtils.isBlank(tableName) && (tables != null) && (!tables.isEmpty())) {result = tables.get(tableName);}
    return result;
   }

  @Override
  public String toString()
   {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
            append("dbName", dbName).
            append("tables", tables).
            toString();
   }

 }