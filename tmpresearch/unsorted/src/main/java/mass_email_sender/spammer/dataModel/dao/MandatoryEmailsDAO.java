package spammer.dataModel.dao;

import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.applied.dao.DBConfigCommonDAO;
import jdb.processing.sql.execution.SqlExecutor;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import spammer.Defaults;
import spammer.mailsList.interfaces.EmailsListInterface;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;

/**
 * DAO-��������� ��� ������ � �������� mandatoryEmails (������������ ����-������).
 * @author Gusev Dmitry (�������)
 * @version 1.0 (DATE: 02.04.11)
*/

public class MandatoryEmailsDAO extends DBConfigCommonDAO implements EmailsListInterface
 {
  /** ������ ������. */
  private Logger logger = Logger.getLogger(Defaults.LOGGER_NAME);

  /** �����������. ���� ���������������� ���������� �� ���������. */
  public MandatoryEmailsDAO() {super(Defaults.LOGGER_NAME, Defaults.DBCONFIG_FILE);}

  public TreeMap<String, Integer> getEmailsList()
   {
    logger.debug("MandatoryEmailsDAO: getEmailsList().");
    Connection               conn = null;
    ResultSet                rs   = null;
    TreeMap<String, Integer> list = null;
    String                   sql  = "select id, email from dbo.mandatoryEmails where deleted = 0";
    try
     {
      conn = this.getConnection();
      rs = SqlExecutor.executeSelectQuery(conn, sql);
      // ���-�� �����
      if (rs.next())
       {
        logger.debug("Result set is not empty! Processing.");
        list = new TreeMap<String, Integer>();
        String  email;
        Integer id;
        do
         {
          email = rs.getString("email");
          id    = rs.getInt("id");
          if (!StringUtils.isBlank(email)) {list.put(email, id);}
         }
        while (rs.next());
       }
      // ���� �� �����
      else {logger.warn("Result set is empty! Deliveries not found!");}
     }
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    catch (SQLException e)            {logger.error(e.getMessage());}
    catch (DBConnectionException e)   {logger.error(e.getMessage());}
    // ����������� �������
    finally
     {
      try {if(rs != null) {rs.close();} if(conn != null) {conn.close();}}
      catch (SQLException e) {logger.error("Can't free resources! Reason: " + e.getMessage());}
     }
    return list;
   }

 }