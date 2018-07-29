package jdb.model;

import jdb.exceptions.DBModelException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

/**
 * Данный класс реализует простейшую модель таблицы БД. Данная модель умеет только хранить свое непустое
 * имя в врехнем регистре символов. Все остальные модели таблиц унаследованы от данной модели.
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 23.07.2010)
*/

public class TableModel implements Serializable
 {
  static final long serialVersionUID = -982570254006000243L;

  /** Схема данных, которой принадлежит таблица. */
  private String tableSchema = null;
  /** Наименование таблицы. */
  private String tableName   = null;
  /** Тип таблицы. */
  private String tableType   = null; 

  /**
   * Конструктор по умолчанию. Обязательно инициализирует имя БД.
   * @param tableName String имя создаваемой модели БД.
   * @throws DBModelException ИС возникает при создании модели БД с пустым именем.
  */
  public TableModel(String tableName) throws DBModelException
   {
    if (!StringUtils.isBlank(tableName)) {this.tableName = tableName.toUpperCase();}
    else {throw new DBModelException("Name of the table is empty!");}
   }

  /**
   * Метод доступа к имени таблицы.
   * @return String имя текущей таблицы.
  */
  public String getTableName() {return tableName;}

  /**
   * Метод установки имени таблицы.
   * @param tableName String имя создаваемой модели БД.
   * @throws DBModelException ИС возникает при создании модели БД с пустым именем.
  */
  public void setTableName(String tableName) throws DBModelException
   {
    if (!StringUtils.isBlank(tableName)) {this.tableName = tableName.toUpperCase();}
    else {throw new DBModelException("Name of the table is empty!");}
   }

  public String getTableSchema() {
   return tableSchema;
  }

  public void setTableSchema(String tableSchema)
   {
    if (!StringUtils.isBlank(tableSchema)) {this.tableSchema = tableSchema.toUpperCase();}
    else {this.tableSchema = tableSchema;}
  }

  public String getTableType() {
   return tableType;
  }

  public void setTableType(String tableType) {
   this.tableType = tableType;
  }

  @Override
  public String toString()
   {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
            append("tableSchema", tableSchema).
            append("tableName", tableName).
            append("tableType", tableType).
            toString();
   }
  
 }