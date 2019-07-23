package jdb.processing.loading.helpers;

import dgusev.dbpilot.DBConsts;
import jdb.exceptions.DBModelException;
import jdb.model.integrity.TableIntegrityModel;
import jdb.model.structure.FieldStructureModel;
import jdb.model.structure.TableStructureModel;
import jdb.model.time.TableTimedModel;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Класс содержит методы генерации sql-запросов для экспорта (сериализации) данных из БД. Поддерживает схемы данных. 
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 30.07.2010)
*/

public class DataExportSQLBuilder
 {
  /** Логгер класса. */
  private static Logger logger = Logger.getLogger(DataExportSQLBuilder.class.getName());

  /**
   * Метод генерирует sql-запрос для экспорта (сериализации) одной таблицы БД. Запрос генерируется на основе нескольких
   * моделей таблиц - TableStructureModel, TableIntegrityModel, TableTimedModel. В зависимости от их наличия запрос будет
   * сгенерирован по-разному. Основная модель - TableStructureModel, если ее нет, то  метод вернет значение null.
   * @param structureTable TableStructureModel
   * @param integrityTable TableIntegrityModel
   * @param timedTable TableTimedModel
   * @return String сгенерированный запрос или значение null. 
  */
  public static String getExportTableSQL(TableStructureModel structureTable, TableIntegrityModel integrityTable,
                                         TableTimedModel timedTable)
   {
    String        result = null;
    StringBuilder sql    = null;

    // Если модель таблицы не пуста и имя таблицы не пусто - работаем
    if ((structureTable != null) && (!StringUtils.isBlank(structureTable.getTableName())))
     {
      // Получаем csv-список полей выгружаемой таблицы. Если он не пуст - работаем!
      String csvList = structureTable.getCSVFieldsList();
      if (!StringUtils.isBlank(csvList))
       {
        logger.debug("Structure table is not empty and fields list is not empty! Processing.");
        // Начинаем конструировать sql-запрос для выгрузки таблицы. Если в модели структуры таблицы указана схема
        // данных - используем ее.
        sql = new StringBuilder("select ").append(csvList).append(" from ");
        if (!StringUtils.isBlank(structureTable.getTableSchema())) {sql.append(structureTable.getTableSchema()).append(".");}
        sql.append(structureTable.getTableName());
        //logger.debug("Pregenerated SQL: " + sql.toString());

        // Флаг наличия модели целостности таблицы. Если модель целостности указана, то sql для выгрузки по
        // таймштампу будет сформирован по-другому
        boolean isIntegrityPresent = false;
        // Флаг наличия модели таблицы с указанием времени. Если такая модель есть, то в sql-запросе будет сортировка по
        // таймштампу, если же модели нет, то будет сортировка по идентификатору (ключевому полю) - если оно есть в списке полей.
        boolean isTimedPresent     = false;

        // Флаг наличия ключевого поля в выгружаемой таблице
        boolean isKeyFieldPresent = false;
        if (structureTable.getField(DBConsts.FIELD_NAME_KEY) != null) {isKeyFieldPresent = true;}
        // Флаг наличия поля "таймштамп" ("timestamp")
        boolean isTimestampPresent = false;
        if (structureTable.getField(DBConsts.FIELD_NAME_TIMESTAMP) != null) {isTimestampPresent = true;}

        // Если есть модель целостности таблицы (т.е. она не пуста) и не пуст список ее ключей - выгружаем
        // только те записи, ключи которых есть в списке модели целостности. Также проверяется наличие ключевого поля -
        // без него не имеет смысла добавлять в запрос модель целостности.
        if (isKeyFieldPresent && (integrityTable != null) && (!StringUtils.isBlank(integrityTable.getTableName())) &&
            (!StringUtils.isBlank(integrityTable.getCSVKeysList())))
         {
          logger.debug("Integrity model exists! Adding to loading SQL.");
          sql.append(" where ").append(DBConsts.FIELD_NAME_KEY).append(" in (");
          sql.append(integrityTable.getCSVKeysList()).append(")");
          //logger.debug("Pregenerated SQL: " + sql.toString());
          isIntegrityPresent = true;
         }

        // Если есть модель таблицы с указанием времени - выгружаем дельту по таймштампу. Также по таймштампу
        // сортируем выгружаемые записи. Также проверяется наличие поля "таймштамп" - без него не имеет смысла
        // добавлять в запрос модель таблицы с указанием целостности.
        if (isTimestampPresent && (timedTable != null) && (!StringUtils.isBlank(timedTable.getTableName())) &&
            (timedTable.getTimeStamp() != null))
         {
          logger.debug("Timed model exists! Adding to loading SQL.");
          Timestamp timeStamp = timedTable.getTimeStamp();
          // Поле типа timestamp в java имеет в конце указание фракций -> '2008-07-07 12:10:32.0', это не переваривает
          // информикс (и многие другие СУБД) - поэтому необходимо преобразование формата (чтобы эти фракции убрать).
          // Если преобразование не удалось - не добавляем к оператору sql критерий выбора по таймштампу.
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          try
           {
            String strTimeStamp  = sdf.format(sdf.parse(timeStamp.toString()));
            
            // Если преобразование удалось - выполнится нижеследующий код. Если не удалось - то к sql опреатору
            // не будет добавлена выборка и сортировка по тайштампу.

            // Если модель целостности была добавлена к запросу, то добавляем критерий таймштампа так
            if (isIntegrityPresent) {sql.append(" and ");}
            // Если же модели целостности не было - добавляем критерий тайимштампа так
            else                    {sql.append(" where ");}
            // Добавляем полностью критерий выборки по таймштампу
            sql.append(DBConsts.FIELD_NAME_TIMESTAMP).append(" > '").append(strTimeStamp).append("'");
            // Добавляем сортировку по таймштампу
            sql.append(" order by ").append(DBConsts.FIELD_NAME_TIMESTAMP);
            // После успешной работы с моделью таблицы с указанием времени - выставим флажок наличия такой модели
            isTimedPresent = true;
           }
          catch (ParseException e) {logger.error("Can't parse timestamp [" + timeStamp + "]. Reason: " + e.getMessage());}
         }

        // Если нет модели с казанием времени - добавляем сортировку по ключевому полю (идентификатору). Сортировку
        // добавляем только если в таблице есть поле "таймштамп"
        if (!isTimedPresent && isTimestampPresent)
         {
          logger.debug("No timed model for table! Adding ordering by key field.");
          sql.append(" order by ").append(DBConsts.FIELD_NAME_KEY);
         }

        // Отладочный вывод получившегося sql-запроса
        logger.debug("Generated loading table SQL: " + sql.toString());
       }
      // Если список полей данной таблицы пуст - ошибка! Метод вернет NULL.
      else {logger.error("CSV fields for table [" + structureTable.getTableName() + "] is empty!");}
     }
    // Если модель таблицы пуста - сообщим в лог
    else {logger.error("TableStructureModel is NULL or table name is empty!");}

    // Преобразование результата в строку
    if (sql != null) {result = sql.toString();}
    return result;
   }

  /**
   * Данный метод предназначен только для тестирования класса.
   * @param args String[] параметры метода main.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger(DataExportSQLBuilder.class.getName());
    Logger logger = Logger.getLogger(DataExportSQLBuilder.class.getName());
    try
     {
      TableStructureModel structureTable = new TableStructureModel("structure");
      structureTable.addField(new FieldStructureModel("id", 0, "", 0));
      structureTable.addField(new FieldStructureModel("field1", 0, "", 0));

      TableIntegrityModel integrityTable = new TableIntegrityModel("integrity");
      integrityTable.addKey(10);
      integrityTable.addKey(345);
      integrityTable.addKey(8);

      TableTimedModel     timedTable     = new TableTimedModel("timed", Timestamp.valueOf("2008-07-07 12:10:32"));

      logger.debug("\n SQL: \n" + DataExportSQLBuilder.getExportTableSQL(structureTable, integrityTable, timedTable));
      
     }
    catch (DBModelException e) {logger.error(e.getMessage());}

   }

 }