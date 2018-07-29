package jdb.model;

import jdb.DBConsts.DBType;
import jdb.exceptions.DBModelException;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Данный абстрактный класс реализует общую модель базы данных - она умеет только хранить свое имя (у каждой БД обязательно
 * должно быть непустое имя) и предоставлять к нему доступ. Имя хранится в верхнем регистре символов. Также данный класс
 * описывает абстрактные методы, которые должны быть во всех его потомках.
 * @author Gusev Dmitry (019gus)
 * @version 4.0 (DATE: 21.03.2011)
*/

abstract public class DBModel implements Serializable
 {
  // Поле используется для совместимости последующих версий класса с предыдущими (для механизма сериализации)
  static final long serialVersionUID = -310649003724243853L;

  /** Наименование БД. */
  private String dbName = null;
  /** Тип СУБД, в которой хранится БД, модель которой представляет данный экземпляр. */
  private DBType dbType = null;

  /**
   * Конструктор по умолчанию. Обязательно инициализирует имя БД.
   * @param dbName String имя создаваемой модели БД.
   * @throws DBModelException ИС возникает при создании модели БД с пустым именем.
  */
  public DBModel(String dbName) throws DBModelException
   {
    if (!StringUtils.isBlank(dbName)) {this.dbName = dbName.toUpperCase();}
    else {throw new DBModelException("Name of the database is empty!");}
   }

  /**
   * Метод доступа к имени БД.
   * @return String имя тукещей БД.
  */
  public String getDbName() {return this.dbName;}

  /**
   * Метод установки имени БД.
   * @param dbName String имя создаваемой модели БД.
   * @throws DBModelException ИС возникает при создании модели БД с пустым именем.
  */
  public void setDbName(String dbName) throws DBModelException
   {
    if (!StringUtils.isBlank(dbName)) {this.dbName = dbName.toUpperCase();}
    else {throw new DBModelException("Name of the database is empty! Can't set empty name!");}
   }

  public DBType getDbType()            {return dbType;}
  public void setDbType(DBType dbType) {this.dbType = dbType;}
  
  /**
   * Метод возвращает количество таблиц в данной модели БД. Каждый потомок данного класса должен иметь свою
   * реализацию данного метода.
   * @return int количество таблиц в данной модели БД.
  */
  abstract public int getTablesCount();

  /**
   * Метод возвращает значение ИСТИНА/ЛОЖЬ в зависимости от того, пуст или нет экземпляр данного класса. Каждый
   * класс-потомок данного класса должен иметь свою реализацию данного метода.
   * @return boolean ИСТИНА/ЛОЖЬ в зависимости от того пуст или нет экземпляр данного класса.
  */
  abstract public boolean isEmpty();

  /**
   * Метод поиска таблицы по имени в списке таблиц данной модели БД.
   * @param tableName String имя таблицы, по которому ищем таблицу в списке.
   * @return TableModel возвращаемая модель таблицы БД или значение NULL.
  */
  abstract public TableModel getTable(String tableName);

  /** Строковое представление экземпляра класса-модели БД. */
  abstract public String toString();

 }