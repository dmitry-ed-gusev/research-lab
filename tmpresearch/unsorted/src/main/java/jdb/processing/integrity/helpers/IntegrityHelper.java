package jdb.processing.integrity.helpers;

import jdb.model.integrity.TableIntegrityModel;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 *  ласс содержит вспомогательные методы дл€ модул€ проверки целостности Ѕƒ.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 16.09.2009)
*/

public class IntegrityHelper
 {
  /**  омпонент-логгер данного класса. */
  private static Logger logger = Logger.getLogger(IntegrityHelper.class.getName());

  /**
   * ћетод возвращает список значений ключевого пол€, которые есть в таблице foreignTable, но нет в таблице currentTable.
   * ѕараметр lightCheck указывает, проводить или нет перед сравнением списка ключей сравнение количества записей. ≈сли
   * метод будет сравнивать количество записей, то при одинаковом их количестве сравнение списка проводитьс€ не будет
   * (это значение параметра lightCheck=true).
   * @param foreignTable TableIntegrityModel
   * @param currentTable TableIntegrityModel
   * @param lightCheck boolean
   * @return ArrayList[Integer]
  */
  public static ArrayList<Integer> getMissingKeys(TableIntegrityModel foreignTable,
                                                  TableIntegrityModel currentTable, boolean lightCheck)
   {
    ArrayList<Integer> keysList = null;

    // ≈сли обе указанные таблицы не пусты - работаем

    // ≈сли хот€ бы одна таблица пуста

    return keysList;
   }

 }