package jdb.processing.integrity;

import jdb.DBConsts;
import jdb.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.integrity.DBIntegrityModel;
import jdb.model.integrity.TableIntegrityModel;
import jdb.monitoring.DBProcessingMonitor;
import jdb.processing.DBCommonProcessor;
import jdb.processing.spider.DBSpider;
import jdb.utils.DBUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * ������ ������ ��������� ������� �������� ����������� ���� ������ ������� ��. �������������� ������
 * "�����������" � "�����������" ������.
 * @author Gusev Dmitry (019gus)
 * @version 5.0 (DATE: 07.06.2010)
*/

public class DBIntegrityChecker extends DBCommonProcessor
 {
  /** ���������-������ ������� ������. */
  private Logger logger = Logger.getLogger(this.getClass().getName());

  /**
   * ����������� �� ���������. �������������� ������� ������������.
   * @param config ConnectionConfig ������������ ������.
   * @throws DBModuleConfigException �� ���������, ���� ������������ �������� ������ ������������.
  */
  public DBIntegrityChecker(DBConfig config) throws DBModuleConfigException {super(config);}

  /**
   * ����� �������� ������� �� � ������������ � ��, ������ ����������� ������� �������� � �������� ���������.
   * @param db DBIntegrityModel ������ ����������� ��, � ������������ � ������� �� �������� ������� ��.
   * @param monitor ProcessMonitor �����-�������, ������� ��������� ���������������� �� ����������� �������� ��������������.
   * @throws SQLException �� ��� ���������� ������� ������� �� ��� ������ SQL-����������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
   * @throws DBModelException ������ ������ ���� ������.
   * @return ArrayList[Strung] ������ ��������� ��� ������ ������ ������ (��������� ���������). � ������ ������
   * ������������ �� ����������� ������ - ��, ������� �� �������� � �������� ������.
  */
  @SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
  public ArrayList<String> makeIntegrity(DBIntegrityModel db, DBProcessingMonitor monitor)
   throws SQLException, DBModelException, DBConnectionException, DBModuleConfigException
   {
    logger.debug("makeIntegrity(): making integrity for current DB.");
    // ���� ������� ����-����� - �������� �� ����
    if (getConfig().isDemo()) {logger.warn("DEMO MODE IS ON!");}

    // ���� ���������� ������ ����� (=null), ������������ ��
    if (db == null) {throw new DBModelException("Received database integrity model is empty!");}

    // ������������ ������ ��������� ��
    ArrayList<String> errorsList = null;

    // ������ ���� ������ ������� ��
    DBSpider spider = new DBSpider(this.getConfig());

    // todo: ������� ������ � �������������� �������!!!
    //ArrayList<String> tablesList = spider.getTablesList();
    ArrayList<String> tablesList = new ArrayList<String>(Arrays.asList(""));

    // ������������� ���������� � ������� ����
    Connection    connection = DBUtils.getDBConn(getConfig());
    Statement     stmt       = connection.createStatement();

    // �������� �� ���� �������� ������� �� � ���������� �� � ��������� ���������� �� (���� ������ �� ����)
    if ((tablesList != null) && (!tablesList.isEmpty()))
     {
      int processedTablesCounter = 0;                        // <- ������� ������������ ������
      int tablesCount            = tablesList.size(); // <- ����� ���������� �������������� ������
      for (String currentTable : tablesList)
       {
        // ���� ������� ����������� ������ ����������� - �� �� ������������
        if (!getConfig().isTableAllowed(currentTable)) {logger.warn("This table [" + currentTable + "] is deprecated or not allowed! Skipping.");}
        // ���� �� ������ ����������� ������� �� ����������� - ������������ ��
        else
         {
          logger.info("Checking integrity [" + currentTable + "].");
          // ������� ������� �� ���������� � �������� ��������� ������ ��, ���� ������� ����� ��� �� ��� -
          // ��� ������� ������� �� ����������� �������������� �����������.
          TableIntegrityModel table = db.getTable(currentTable);
          if (table != null)
           {
            String csvList = table.getCSVKeysList();
            // ���� �� ���� ������ ������ ������� ������� - ��������
            if ((csvList != null) && (csvList.length() > 0))
             {
              StringBuilder sql = new StringBuilder("delete from ").append(currentTable).append(" where ");
              sql.append(DBConsts.FIELD_NAME_KEY).append(" not in (").append(csvList).append(")");
              // ������ ���������� ����� ����� ������ ��� �������� �������
              //logger.debug("Executing sql: " + sql.toString());

              // ���������� ������� �� �������� ������ (���� ����-����� ��������)
              if (!getConfig().isDemo())
               {
                // ����������� try-catch �����, ����� ���� �������� �������������� sql-������ �� ������� ���� ���� ��������
                // �����������. ������ ��������� ������ ������� � �������������� ������ ������ ��� �������� � ���������� ���.
                try {stmt.executeUpdate(sql.toString());}
                catch (SQLException e)
                 {
                  // ��������� �� ������
                  StringBuilder errMsg = new StringBuilder("Can't execute sql [").append(sql.toString());
                  errMsg.append("]. Reason [").append(e.getMessage()).append("].");
                  logger.error(errMsg.toString());
                  // ���� ������ ��������� ������ �� ��������������� - �������������
                  if (errorsList == null) {errorsList = new ArrayList<String>();}
                  errorsList.add(errMsg.toString());
                 }
                } // END [isDemo()] BLOCK
             }
           }
         } // ����� ������ ��������� ������� "�����������" �������
       
        // ����� ��������� ������ �������(�������, ��������� ��� ��� ���) ������� �������������� ��������� (���� ����������)
        if (monitor != null)
         {
          // ����������� ������� ������������ ������
          processedTablesCounter++;
          // ������� ������� ������� ����������
          int currentProgress = (processedTablesCounter*100/tablesCount);
          // ����� � �������-������� �������� ��������� ���������� ()
          monitor.processProgress(currentProgress);
          // ���� �������� ������� (������� ��������� ���� DEBUG), �� ������� ��������� �� �������������� ������ �������.
          if (logger.getEffectiveLevel().equals(Level.DEBUG)) {monitor.processDebugInfo("[DEBUG] " + currentTable);}
         }
       } // ����� ����� for
     } // ����� ������ ��������� ��������� ������ ������

    // ���������� ������� �������� ����������
    connection.close();
    return errorsList;
   }

  /**
   * ����� �������� ������� �� � ������������ � ��, ������ ����������� ������� �������� � �������� ���������.
   * @param db DBIntegrityModel ������ ����������� ��, � ������������ � ������� �� �������� ������� ��.
   * @throws SQLException �� ��� ���������� ������� ������� �� ��� ������ SQL-����������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
   * @throws DBModelException ������ ������ ���� ������.
   * @return ArrayList[Strung] ������ ��������� ��� ������ ������ ������ (��������� ���������). � ������ ������
   * ������������ �� ����������� ������ - ��, ������� �� �������� � �������� ������.
  */
  @SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
  public ArrayList<String> makeIntegrity(DBIntegrityModel db)
   throws DBConnectionException, DBModelException, DBModuleConfigException, SQLException {return this.makeIntegrity(db, null);}

  /**
   * ����� ������������ ������ ��� ������������ ������� ������.
   * @param args String[] ��������� ������ main.
  */
  //public static void main(String[] args) {}
  
 }