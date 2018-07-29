package jdb.processing.spider;

import jdb.DBConsts;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 22.07.2010)
*/

public class DBMSSysCatalogHelper 
 {

  public static boolean isInSysCatalog(DBConsts.DBType dbType, String tableSchema, String tableName)
   {
    Logger logger = Logger.getLogger(DBMSSysCatalogHelper.class.getName());
    logger.debug("DBMSSysCatalogHelper.isInSysCatalog() working.");
    boolean result = false;

    // ���� ��������� ��� ���� �� ���� - ��������
    if ((dbType != null) && !StringUtils.isBlank(tableName))
     {
      logger.debug("DBMS type [" + dbType.strValue() + "] and table name [" + tableName + "] is OK! Processing.");
      switch (dbType)
       {
        // ��� ���� ��������� ����� �� ����������
        case INFORMIX: if (DBConsts.SYSCATALOG_INFORMIX.contains(tableName.toUpperCase())) {result = true;} break;
        // ��� ���� ������ ������ ����� ����������
        case MSSQL_JTDS: case MSSQL_NATIVE:
         // ���� ����� �������, ���� � ��������� ��������
         break;
       }
     }
    // ���� �� ���� ��� ���� ��� ��� ������� - ���������� ��������� ��������� ��������!
    else {logger.warn("DBMS type or table name is empty! Can't check DBMS system catalog correctly!");}

    return result;
   }

 }