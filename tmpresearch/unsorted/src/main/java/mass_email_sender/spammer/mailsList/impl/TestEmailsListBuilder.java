package mass_email_sender.spammer.mailsList.impl;

import mass_email_sender.spammer.Defaults;
import mass_email_sender.spammer.dataModel.dto.DeliveryFileDTO;
import mass_email_sender.spammer.mailsList.interfaces.EmailsListInterface;
import org.apache.log4j.Logger;

import java.util.TreeMap;

/**
 * Тестовая реализация интерфейса получения майл-адресов. Генерирует список адресов отдела 019.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 24.08.2010)
*/

public class TestEmailsListBuilder implements EmailsListInterface
 {
  /** Логгер данного модуля. */
  private Logger logger = Logger.getLogger(Defaults.LOGGER_NAME);

  /***/
  public TreeMap<String, Integer> getEmailsList()
   {
    // Результат работы метода
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
    // Освобождение ресурсов
    finally
     {
      try {if (conn != null) {conn.close();}}
      catch(SQLException e)  {logger.error(e.getMessage());}
     }
    */
   
    // Если в список не было добавлено ни одного мыльца - список должен стать NULL
    if (emailsList.size() <= 0) {emailsList = null;}
    // Возвращаем результат
    return emailsList;
   }

  /**
   * Метод только для тестирования класса!
   * @param args String[] параметры метода.
  */
  public static void main(String[] args)
   {
    Logger logger = Logger.getLogger(DeliveryFileDTO.class.getName());
    TestEmailsListBuilder builder = new TestEmailsListBuilder();
    logger.info(builder.getEmailsList().size());
   }

 }