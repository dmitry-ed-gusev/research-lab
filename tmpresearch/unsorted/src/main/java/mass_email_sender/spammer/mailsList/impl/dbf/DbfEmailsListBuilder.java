package spammer.mailsList.impl.dbf;

import jdb.DBConsts;
import jdb.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.utils.DBUtils;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import spammer.Defaults;

import java.io.File;
import java.sql.*;
import java.util.TreeMap;

/**
 * �������� ������ ��� ������������ ������� ����-������� �� ������ �� ���� � �� ����� �������� �����������.
 * ������ ��� - DBF (dBase III ��� IV). ����� ���� ������� �������������� � ������� �������� ����-������������
 * RecipientType (��. ������ Defaults). ������ ������ ���������� ������ ��� �������� �� ����-������������:
 * RECIPIENT_TYPE_SHIPOWNERS_ENG �  RECIPIENT_TYPE_SHIPOWNERS_RUS.
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 17.12.2010)
*/

@SuppressWarnings({"JpaQueryApiInspection"})
public class DbfEmailsListBuilder
 {
  /** ������ ������� ������. */
  private Logger logger      = Logger.getLogger(Defaults.LOGGER_NAME);
  /** ���� � �� ����. */
  private String fleetDbPath = null;
  /** ���� � �� ����. */
  private String firmDbPath  = null;

  //public DbfEmailsListBuilder() {}

  public DbfEmailsListBuilder(String fleetDbPath, String firmDbPath)
   {
    this.fleetDbPath = fleetDbPath;
    this.firmDbPath = firmDbPath;
   }

  public String getFleetDbPath() {
   return fleetDbPath;
  }

  public void setFleetDbPath(String fleetDbPath) {
   this.fleetDbPath = fleetDbPath;
  }

  public String getFirmDbPath() {
   return firmDbPath;
  }

  public void setFirmDbPath(String firmDbPath) {
   this.firmDbPath = firmDbPath;
  }

  /**
   * ����� ���������� ������ ����-�������� �� �� �����-����. ���� � ����� ������ ���� ������� � ������������
   * ������ ��� ����������� � ������� ������� setXXX(). ���� ���� � ����� �� ����� ������� ��� ���������� ������
   * ������ - ����� ������ �������� NULL.
   * @param recipientType RecipientType ��� ���������, ��� �������� ������ ���� ����������� ������ ���������. ������ �����
   * �������� ������ ��� ���� ��������� - RECIPIENT_TYPE_SHIPOWNERS_ENG � RECIPIENT_TYPE_SHIPOWNERS_RUS. ���� �������
   * �������� NULL, �� ����� ������ ����� ������ ���������, ����������� � ���� ��� ��������� ���� ����.
   * @return TreeMap[String, Integer] ������������ ������ email-�������. ������� ������ ������������� �������������
   * ��������. ���� �� ��������� - �.�. ��� ��������� � �������� ������ - ��� �������������� ����������� ������. 
  */
  @SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
  public TreeMap<String, Integer> getEmailsList(Defaults.RecipientType recipientType)
   {
    // ��������� ������ ������ - ������ ����� � ��������������� ��������. ���� (������ ��������) - ����, ���� ��
    // ��������� ������ � ������������� ��������. �������� (������ ��������) - ������������� ��������, �������� �����
    // �� ����� ���� ������, �� ����� �����������. ���� ����� ������������� � ������������ ��������������� �������� -
    // ���������� ������� TreeSet<Integer, String>
    TreeMap<String, Integer> emailsList = new TreeMap<String, Integer>();

    // ����� ������� � ������ ��������� ���� � ����� ����� � ���� - ��� �� ������ ���� �����
    // � ������ ������������! ����� ��������, �������� �� ��� ����������.
    if (!StringUtils.isBlank(firmDbPath) && !StringUtils.isBlank(fleetDbPath))
     {
      logger.debug("DB FLEET and DB FIRMS databases paths not empty. Processing.");
      // ��������� ������������� ��������� � ��� ��� ������ ��������
      File fleetDBFile = new File(fleetDbPath);
      File firmDBFile  = new File(firmDbPath);
      if (fleetDBFile.exists() && fleetDBFile.isDirectory() && firmDBFile.exists() && firmDBFile.isDirectory())
       {
        // ��� � ������� � ������ - ��������
        logger.debug("DB FLEET and DB FIRMS databases paths are ok. Processing.");
        // ������ ��� ���������� � ��� ����� ����
        DBConfig firmConfig = new DBConfig();
        firmConfig.setDbType(DBConsts.DBType.DBF);
        firmConfig.setDbName(firmDbPath);
        // ������ ��� ���������� � ��� ����� �����
        DBConfig fleetConfig = new DBConfig();
        fleetConfig.setDbType(DBConsts.DBType.DBF);
        fleetConfig.setDbName(fleetDbPath);
        // ������� �� �� ����
        String fleetSql = "select firm_id7 as operatorId, firm_id2 as shipownerId, firm_id1 as ownerId from fleet where " +
                          "sreg = 1 or sreg = 2";
        // ���������� ��� �������� ������� ��� ���������� � ���� � ������� ������
        Connection        fleetConn = null;
        Connection        firmConn  = null;
        Statement         fleetStmt;
        PreparedStatement firmStmt;
        ResultSet         fleetRs;
        ResultSet         firmRs;
        // ����������������� ������ ��� ������� ������ �� �� ����. ��������� � ����������� �� �������� ���������.
        String firmSql = "select email from firm where firm_id = ?";
        if (recipientType != null)
         {
          switch (recipientType)
           {
            case RECIPIENT_TYPE_SHIPOWNERS_ENG: firmSql += " and stran_id <> 102"; break;
            case RECIPIENT_TYPE_SHIPOWNERS_RUS: firmSql += " and stran_id = 102"; break;
           }
         }
        // ���������� ����� ����������� �������
        logger.debug("Generated firm-DB query: [" + firmSql + "].");
        try
         {
          fleetConn = DBUtils.getDBConn(fleetConfig);
          fleetStmt = fleetConn.createStatement();
          fleetRs   = fleetStmt.executeQuery(fleetSql);
          // ���� ���� ����� ���� - ��������
          if (fleetRs.next())
           {
            logger.debug("Ships found. Processing emails search.");
            // ��������� ���������� � ����� ����
            firmConn = DBUtils.getDBConn(firmConfig);
            firmStmt = firmConn.prepareStatement(firmSql);

            // ������� ��� ������� �� �� �����
            int counter = 0;
            // ���������� ��� �������� �������� ��������������� � �������
            int operatorId;
            int shipownerId;
            int ownerId;
            String operatorEmail;
            String shipownerEmail;
            String ownerEmail;

            // � ����� ������������ ������
            do
             {

              // ������������� ���������
              operatorId     = fleetRs.getInt("operatorId");
              // ���� ���������
              if (operatorId > 0)
               {
                firmStmt.setInt(1, operatorId);
                firmRs = firmStmt.executeQuery();
                if (firmRs.next()) {operatorEmail = firmRs.getString("email");}
                else               {operatorEmail = null;}
               }
              else {operatorEmail = null;}

              // ������������� �������������
              shipownerId    = fleetRs.getInt("shipownerId");
              // ���� �������������
              if (shipownerId > 0)
               {
                firmStmt.setInt(1, shipownerId);
                firmRs = firmStmt.executeQuery();
                if (firmRs.next()) {shipownerEmail = firmRs.getString("email");}
                else               {shipownerEmail = null;}
               }
              else {shipownerEmail = null;}

              // ������������� ������������
              ownerId        = fleetRs.getInt("ownerId");
              // ���� ������������
              if (ownerId > 0)
               {
                firmStmt.setInt(1, ownerId);
                firmRs = firmStmt.executeQuery();
                if (firmRs.next()) {ownerEmail = firmRs.getString("email");}
                else               {ownerEmail = null;}
               }
              else {ownerEmail = null;}

              // ��������������� ����� �������� � ������������ ������
              if ((operatorId > 0) && (!StringUtils.isBlank(operatorEmail)))
               {emailsList.put(operatorEmail, operatorId);}
              else if ((shipownerId > 0) && (!StringUtils.isBlank(shipownerEmail)))
               {emailsList.put(shipownerEmail, shipownerId);}
              else if ((ownerId > 0) && (!StringUtils.isBlank(ownerEmail)))
               {emailsList.put(ownerEmail, ownerId);}
              // ����������� �������
              counter++;
             }
            while (fleetRs.next());
            // ���������� ��������� �������
            logger.debug("FLEET database result set count (all selected from fleet): " + counter);
            logger.debug("emailsList size:                                           " + emailsList.size());
           }
          // ���� ����� ������ �� ������� - �������� � ���
          else {logger.warn("No ships! Can't process.");}
         }
        catch (DBModuleConfigException e) {logger.error(e.getMessage());}
        catch (DBConnectionException e)   {logger.error(e.getMessage());}
        catch (SQLException e)            {logger.error(e.getMessage());}
        // ������������ ��������
        finally
         {
          try {if (fleetConn != null) {fleetConn.close();} if (firmConn != null) {firmConn.close();}}
          catch (SQLException e) {logger.error(e.getMessage());}
         }
       }
      // ���� �����-�� ��� ��������� �� ���������� ��� �� �������� ��������� - �����
      // � ��� ������ � ���������� NULL
      else
       {
        logger.error("DB FLEETE path [" + fleetDbPath + "] or DB FIRMS path [" + firmDbPath + "] " +
                     "not exists or not a directory! Can't process!");}
     }
    // ���� ���� ����� - �������� � ��� �� ������ � ���������� NULL
    else {logger.error("DB FLEET path [" + fleetDbPath + "] or DB FIRMS path [" + firmDbPath + "] is empty! Can't process!");} 

    // ���� � ������ �� ���� ��������� �� ������ ������ - ������ ������ ����� NULL
    if (emailsList.size() <= 0) {emailsList = null;}
    // ���������� ���������
    return emailsList;
   }

  /**
   * ����� ������ ��� ������������ ������!
   * @param args String[] ��������� ������.
  */
  public static void main(String[] args)
   {
    InitLogger.initLoggers(new String[] {"spammer", "jdb"});
    Logger logger = Logger.getLogger("spammer");

    DbfEmailsListBuilder builder = new DbfEmailsListBuilder("\\\\rshead\\db002\\new\\fleet", "\\\\rshead\\db002\\new\\firm");
    TreeMap<String, Integer> map = builder.getEmailsList(Defaults.RecipientType.RECIPIENT_TYPE_SHIPOWNERS_ENG);
    logger.info("\n" + map);
    if (map != null) {logger.info("\n" + map.size());}
   }

 }