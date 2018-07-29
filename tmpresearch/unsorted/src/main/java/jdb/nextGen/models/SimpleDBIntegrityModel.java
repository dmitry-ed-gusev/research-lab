package jdb.nextGen.models;

import jdb.nextGen.exceptions.JdbException;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс-модель целостности БД с указанием списка ключей для всех таблиц БД. Класс почти-immutable, т.е. он не позволяет
 * менять имя БД, а только добавлять таблицы в модель БД.
 * @author Gusev Dmitry (Гусев Дмитрий)
 * @version 4.0 (DATE: 30.05.2011)
*/

// todo: может, сделать класс immutable?

public final class SimpleDBIntegrityModel implements Serializable
 {
  /** Поле для совместимости с последующими версиями класса. */
  static final long serialVersionUID = -1345660993253251L;

  //
  private final String                              dbName;
  //
  private final HashMap<String, ArrayList<Integer>> tables = new HashMap<String, ArrayList<Integer>>();

  /***/
  public SimpleDBIntegrityModel(String dbName) throws JdbException
   {
    if (!StringUtils.isBlank(dbName)) {this.dbName = dbName.toUpperCase();}
    else                              {throw new JdbException("Empty db name!");}
   }

  public String getDbName() {return dbName;}

  /**
   * Добавление таблицы в список данной модели БД. Если пусто имя добавляемой таблицы - возбуждается ИС JdbException.
   * Если пуст или NULL список ключей для таблицы, то таблица добавляется, список ключей для нее записывается как NULL.
   * Если список ключей таблицы не пуст и не NULL, но содержит хотя бы одно NULL-значение, то возбуждается ИС JdbException -
   * список ключей не может содержать такого значения. Если же список не пуст, не NULL и не содержит ни одного NULL-ключа,
   * то он добавляется в общий список данной модели БД.
  */
  public void addTable(String name, ArrayList<Integer> keys) throws JdbException
   {
    // Проверяем указанное имя таблицы
    if (!StringUtils.isBlank(name))
     {
      // Если указанный для таблицы список ключей не пуст - добавляем его.
      if ((keys != null) && (!keys.isEmpty()))
       {
        // Если непустой список ключей таблицы содержит хоть одно значение NULL - возбуждается ИС, т.к.
        // список ключей не может содержать такого значения.
        if (!keys.contains(null)) {tables.put(name.toUpperCase(), keys);}
        else {throw new JdbException("Keys list for table [" + name.toUpperCase() + "] contains NULL-key(s)!");}
       }
      // Если список ключей для таблицы пуст или NULL - таблица также добавляется, но вместо списка
      // ее ключей устанавливается значение NULL.
      else {tables.put(name.toUpperCase(), null);}
     }
    // Имя таблицы пусто
    else {throw new JdbException("Empty table name!");}
   }

  /***/
  public ArrayList<Integer> getKeysListForTable(String tableName)
   {
    ArrayList<Integer> list = null;
    if (!StringUtils.isBlank(tableName) && (tables != null) && (!tables.isEmpty()))
     {list = new ArrayList<Integer>(tables.get(tableName));}
    return list;
   }

  /***/
  public Set<String> getTablesList()
   {
    Set<String> list = null;
    if ((this.tables != null) && (!this.tables.isEmpty())) {list = new HashSet<String>(this.tables.keySet());}
    return list;
   }

  /***/
  public boolean containsTable(String tableName)
   {
    boolean result = false;
    if (!StringUtils.isBlank(tableName) && (!this.tables.isEmpty()) && this.tables.containsKey(tableName)) {result = true;}
    return result;
   }

  /***/
  public boolean isEmpty()
   {
    boolean result = true;
    if (!this.tables.isEmpty()) {result = false;}
    return result;
   }

 }