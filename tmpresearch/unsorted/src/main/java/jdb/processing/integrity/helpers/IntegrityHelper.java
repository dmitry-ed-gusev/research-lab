package jdb.processing.integrity.helpers;

import jdb.model.integrity.TableIntegrityModel;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Класс содержит вспомогательные методы для модуля проверки целостности БД.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 16.09.2009)
*/

public class IntegrityHelper
 {
  /** Компонент-логгер данного класса. */
  private static Logger logger = Logger.getLogger(IntegrityHelper.class.getName());

  /**
   * Метод возвращает список значений ключевого поля, которые есть в таблице foreignTable, но нет в таблице currentTable.
   * Параметр lightCheck указывает, проводить или нет перед сравнением списка ключей сравнение количества записей. Если
   * метод будет сравнивать количество записей, то при одинаковом их количестве сравнение списка проводиться не будет
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

    // Если обе указанные таблицы не пусты - работаем

    // Если хотя бы одна таблица пуста

    return keysList;
   }

 }