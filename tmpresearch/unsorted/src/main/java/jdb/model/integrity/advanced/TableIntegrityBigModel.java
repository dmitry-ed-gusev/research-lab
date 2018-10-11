package jdb.model.integrity.advanced;

import jdb.exceptions.DBModelException;
import jdb.model.TableModel;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.TreeMap;

/**
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 26.10.2009)
 * @deprecated код данного класса еще не реализован!
*/

// todo: реализация кода модели
public class TableIntegrityBigModel extends TableModel implements Serializable
 {
  /** Компонент-логгер данного класса */
  private transient Logger logger = Logger.getLogger(getClass().getName());

  /***/
  private TreeMap<Integer, Timestamp> keysList = null;

  public TableIntegrityBigModel(String tableName) throws DBModelException
   {super(tableName);}

  public TreeMap<Integer, Timestamp> getKeysList() {return keysList;}
  public void setKeysList(TreeMap<Integer, Timestamp> keysList) {this.keysList = keysList;}

  public String getCSVKeys() {return null;}

 }