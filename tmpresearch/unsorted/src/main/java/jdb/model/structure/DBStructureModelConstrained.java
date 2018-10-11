package jdb.model.structure;

import java.util.ArrayList;
import java.io.Serializable;

/**
 * Данный класс содержит модель струтуры БД (DBStructureModel) и ограничения, использованные при
 * создании данной модели - списки "разрешенных" и "запрещенных" таблиц. Данный класс нужен для системы
 * обновления и синхронизации данных системы Storm.
 * @author Gusev Dmitry (gusev)
 * @version 1.0 (DATE: 03.10.2008)
*/

public class DBStructureModelConstrained implements Serializable
 {
  static final long serialVersionUID = 5843959363113209891L;

  /** Модель структуры базы данных. */
  private DBStructureModel  dbModel;
  /** Список "разрешенных" таблиц, использованный модулем DBSpider при создании данной модели БД. */
  private ArrayList<String> allowedTables;
  /** Список "запрещенных" таблиц, использованный модулем DBSpider при создании данной модели БД. */
  private ArrayList<String> deprecatedTables;

  public DBStructureModelConstrained()
   {
    this.dbModel          = null;
    this.allowedTables    = null;
    this.deprecatedTables = null;
   }

  public DBStructureModel getDbModel() {
   return dbModel;
  }

  public void setDbModel(DBStructureModel dbModel) {
   this.dbModel = dbModel;
  }

  public ArrayList<String> getAllowedTables() {
   return allowedTables;
  }

  public void setAllowedTables(ArrayList<String> allowedtables) {
   this.allowedTables = allowedtables;
  }

  public ArrayList<String> getDeprecatedTables() {
   return deprecatedTables;
  }

  public void setDeprecatedTables(ArrayList<String> deprecatedTables) {
   this.deprecatedTables = deprecatedTables;
  }

 }