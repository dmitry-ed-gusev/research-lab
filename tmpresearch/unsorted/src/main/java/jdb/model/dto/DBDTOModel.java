package jdb.model.dto;

import jdb.exceptions.DBModelException;
import jdb.model.DBModel;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Класс реализует модель БД со всеми ее таблицами, полями и данными.
 * Данный класс может быть сериализован.
 * @author Gusev Dmitry (019gus)
 * @version 3.1 (DATE: 21.03.2011)
*/

public class DBDTOModel extends DBModel implements Serializable
 {
  static final long serialVersionUID = -714595024946774739L;

  /** Список таблиц данной БД. */
  private ArrayList<TableDTOModel> tables = null;

  public DBDTOModel(String dbName) throws DBModelException {super(dbName);}

  public ArrayList<TableDTOModel> getTables() {return tables;}
  public void setTables(ArrayList<TableDTOModel> tableModels) {this.tables = tableModels;}

  @Override
  public boolean isEmpty()
   {
    boolean result = true;
    if ((tables != null) && (!tables.isEmpty())) {result = false;}
    return result;
   }

  /**
   * Метод добавляет одну таблицу (экземпляр класса TableDTO) к списку таблиц данной модели БД. Если таблица пуста - она
   * не будет добавлена. если список таблиц пуст - он будет инициализирован.
   * @param tableModel TableDTO добавляемая к списку таблица.
  */
  public void addTable(TableDTOModel tableModel)
   {
    if (tableModel != null)
     {
      if (this.tables == null) {this.tables = new ArrayList<TableDTOModel>();}
      this.tables.add(tableModel);
     }
   }

  /**
   * Метод возвращает модель таблицы БД по имени, если модель построена и такая таблица существует.
   * Метод является null-safe - корректно обрабатывает null-значения.
   * @param tableName String имя таблицы, модель которой возвращает метод.
   * @return TableModel модель таблицы или null.
  */
  public TableDTOModel getTable(String tableName)
   {
    TableDTOModel tableModel = null;
    if ((this.tables != null) && (!this.tables.isEmpty()) && !StringUtils.isBlank(tableName))
     {
      // Поиск таблицы в списке (т.к. имена таблиц хранятся в верхнем регистре, переводим полученное имя в верхний регистр)
      for (TableDTOModel table : this.tables)
       {
        if ((table != null) && (!StringUtils.isBlank(table.getTableName())) &&
            (table.getTableName().equals(tableName.toUpperCase())))
         {tableModel = table;}
       }
     }
    return tableModel;
   }

  /** Метод формирует и возвращает строковое представление экземпляра данного класса. */
  public String toString()
   {return ("\nDATABASE: " + this.getDbName() + "\nTABLES: " + this.tables);}

  @Override
  public int getTablesCount()
   {int result = 0; if ((this.tables != null) && (!this.tables.isEmpty())) {result = this.tables.size();} return result;}

 }