package jdb.processing.spider;

import dgusev.dbpilot.DBConsts;
import dgusev.dbpilot.config.DBType;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 22.07.2010)
*/

public class DBMSSysCatalogHelper 
 {

  public static boolean isInSysCatalog(DBType dbType, String tableSchema, String tableName)
   {
    Logger logger = Logger.getLogger(DBMSSysCatalogHelper.class.getName());
    logger.debug("DBMSSysCatalogHelper.isInSysCatalog() working.");
    boolean result = false;

    // Если указанный тип СУБД не пуст - работаем
    if ((dbType != null) && !StringUtils.isBlank(tableName))
     {
      logger.debug("DBMS type [" + dbType.getStrValue() + "] and table name [" + tableName + "] is OK! Processing.");
      switch (dbType)
       {
        // Для СУБД Информикс схемы не используем
        case INFORMIX: if (DBConsts.SYSCATALOG_INFORMIX.contains(tableName.toUpperCase())) {result = true;} break;
        // Для СУБД Сиквел сервер схемы используем
        case MSSQL_JTDS: case MSSQL_NATIVE:
         // Если схема указана, ищем в системном каталоге
         break;
       }
     }
    // Если же пуст тип СУБД или имя таблицы - невозможно корректно выполнить проверку!
    else {logger.warn("DBMS type or table name is empty! Can't check DBMS system catalog correctly!");}

    return result;
   }

 }