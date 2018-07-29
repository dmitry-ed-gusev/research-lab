package spammer.mailsList.impl;

import jlib.logging.InitLogger;
import org.apache.log4j.Logger;
import spammer.Defaults;
import spammer.dataModel.dto.DeliveryFileDTO;
import spammer.mailsList.interfaces.EmailsListInterface;

import java.util.TreeMap;

/**
 * �������� ���������� ���������� ��������� ����-�������. ���������� ������ ������� ������ 019.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 24.08.2010)
*/

public class TestEmailsListBuilder implements EmailsListInterface
 {
  /** ������ ������� ������. */
  private Logger logger = Logger.getLogger(Defaults.LOGGER_NAME);

  /***/
  public TreeMap<String, Integer> getEmailsList()
   {
    // ��������� ������ ������
    TreeMap<String, Integer> emailsList = new TreeMap<String, Integer>();

    emailsList.put("brusakov@rs-head.spb.ru, 019gus@rs-head.spb.ru", 1);
    emailsList.put("019gus@rs-head.spb.ru",   2);
    emailsList.put("019dick@rs-head.spb.ru",  3);
    emailsList.put("019dick@rs-head.spb.ru",  4);
    emailsList.put("019els@rs-head.spb.ru",   5);
    emailsList.put("kirillov@rs-head.spb.ru", 6);
    emailsList.put("shakirov@rs-head.spb.ru", 7);
    emailsList.put("019iva@rs-head.spb.ru",   8);
    emailsList.put("dick@rs-head.spb.ru",     9);
    emailsList.put("ivanov@rs-head.spb.ru",   10);
    emailsList.put("dik@rs-head.spb.ru",      11);
    emailsList.put("nitkin@rs-head.spb.ru",   12);
    emailsList.put("019els@rs-head.spb.ru",   13);


    /**
    DBConfig config = new DBConfig();
    config.setDbType(DBConsts.DBType.DBF);
    config.setDbName("c:\\temp");
    Connection conn = null;
    try
     {
      conn = DBUtils.getDBConn(config);
      String sql = "select insp_id, email from insp100";
      ResultSet rs = conn.createStatement().executeQuery(sql);
      if (rs.next())
       {
        do
         {
          //emailsList.put(rs.getString("email"), rs.getInt("insp_id"));
         }
        while(rs.next());}
     }
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    catch (SQLException e)            {logger.error(e.getMessage());}
    catch (DBConnectionException e)   {logger.error(e.getMessage());}
    // ������������ ��������
    finally
     {
      try {if (conn != null) {conn.close();}}
      catch(SQLException e)  {logger.error(e.getMessage());}
     }
    */
   
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
    InitLogger.initLogger(DeliveryFileDTO.class.getName());
    Logger logger = Logger.getLogger(DeliveryFileDTO.class.getName());
    TestEmailsListBuilder builder = new TestEmailsListBuilder();
    logger.info(builder.getEmailsList().size());
   }

 }