package jdb.model.dto;

import jdb.exceptions.DBModelException;
import jdb.model.TableModel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Данный класс реализует модель таблицы из любой БД с данными (полностью или частично) - аналог курсора данных
 * java (ResultSet). Данный класс может быть сериализован. Имя таблицы хранится в ВЕРХНЕМ регистре символов.
 * Класс используется (в основном) для обновления данных в таблице по ключевому полю - см. класс DBSerializer. 
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 20.03.2008)
*/

public class TableDTOModel extends TableModel implements Serializable
 {
  static final long serialVersionUID = 6590356882080866767L;

  /** Список записей данной таблицы. */
  private ArrayList<RowDTOModel> rowModels = null;

  public TableDTOModel(String tableName) throws DBModelException {super(tableName);}

  public ArrayList<RowDTOModel> getRows() {return rowModels;}
  public void setRows(ArrayList<RowDTOModel> rowModels) {this.rowModels = rowModels;}

  /**
   * Метод добавляет одну запись (экземпляр класса TableRowDTO) к списку записей данной таблицы. Если запись пуста - она не
   * будет добавлена. Если список записей еще пуст - он будет проинициализирован.
   * @param rowModel TableRowDTO добавляемое к списку поле.
  */
  public void addRow(RowDTOModel rowModel)
   {if (rowModel != null) {if (this.rowModels == null) this.rowModels = new ArrayList<RowDTOModel>(); this.rowModels.add(rowModel);}}

  /** Метод формирует и возвращает строковое представление экземпляра данного класса. */
  public String toString() {return ("\nTABLE: " + this.getTableName() + "\nROWS: " + this.rowModels);}

 }