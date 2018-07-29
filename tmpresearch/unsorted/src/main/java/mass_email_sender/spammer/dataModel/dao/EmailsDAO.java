package spammer.dataModel.dao;

import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.applied.dao.DBConfigCommonDAO;
import jdb.processing.sql.execution.SqlExecutor;
import jlib.logging.InitLogger;
import org.apache.log4j.Logger;
import spammer.Defaults;
import spammer.dataModel.dto.EmailDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Действия с майл-адресами для данного приложения.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 23.08.2010)
*/

public class EmailsDAO extends DBConfigCommonDAO
 {
  /** Логгер класса. */
  private Logger logger = Logger.getLogger(Defaults.LOGGER_NAME);

  /** Конструктор. */
  public EmailsDAO() {super(Defaults.LOGGER_NAME, Defaults.DBCONFIG_FILE);}

  /**
   * Поиск всех мыл, по которым были разосланы письма в рамках рассылки с идентифкатором ID. Если такой рассылки нет
   * или не найдено мылов - метод возвращает NULL.
   * @param id int идентификатор рассылки.
   * @return ArrayList[EmailDTO] список мыл или NULL.
  */
  public ArrayList<EmailDTO> findByDeliveryId(int id)
   {
    logger.debug("EmailsDAO: findByDeliveryId()");
    Connection          conn       = null;
    ResultSet           rs         = null;
    ArrayList<EmailDTO> emailsList = null;
    String              sql        = "select id, email, companyId, deliveryId, status, errorText, timestamp from " +
                                     "dbo.emails where deliveryId = ";
    // Если идентификатор положителен - работаем
    if (id > 0)
     {
      sql += id;
      logger.debug("Generated sql: " + sql);
      try
       {
        conn = this.getConnection();
        rs = SqlExecutor.executeSelectQuery(conn, sql);
        // Что-то нашли
        if (rs.next())
         {
          logger.debug("Result set is not empty! Processing.");
          emailsList = new ArrayList<EmailDTO>();
          do
           {
            EmailDTO email = new EmailDTO();
            email.setId(rs.getInt("id"));
            email.setEmail(rs.getString("email"));
            email.setCompanyId(rs.getInt("companyId"));
            email.setDeliveryId(rs.getInt("deliveryId"));
            email.setStatus(rs.getInt("status"));
            email.setErrorText(rs.getString("errorText"));
            email.setTimestamp(rs.getString("timestamp"));
            emailsList.add(email);
           }
          while (rs.next());
         }
        // Ничо не нашли
        else {logger.warn("Result set is empty! Emails for delivery [" + id + "] not found!");}
       }
      catch (DBModuleConfigException e) {logger.error(e.getMessage());}
      catch (SQLException e)            {logger.error(e.getMessage());}
      catch (DBConnectionException e)   {logger.error(e.getMessage());}
      // Освобождаем ресурсы
      finally
       {
        try {if(rs != null) {rs.close();} if(conn != null) {conn.close();}}
        catch (SQLException e) {logger.error("Can't free resources! Reason: " + e.getMessage());}
       }
     }
    // Если идентификатор не подходит - ошибка
    else {logger.error("Delivery ID is negative!");}
    return emailsList;
   }

  /**
   * Метод создает или изменяет запись в таблице мылов (dbo.emails) на основе данных из указанного параметра -
   * экземпляра класса EmailDTO. Если в экземпляре указан положительный идентификатор метод ваполняет изменение
   * данных (update), если же идентификатор ноли или меньше - метод выполняет добавление записи (insert).
   * @param email EmailDTO экземпляр класса, на основе данных которого выполняется пробивание данных в БД.
  */
  @SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
  public void change(EmailDTO email)
   {
    logger.debug("EmailsDAO: change().");
    Connection        conn      = null;
    PreparedStatement stmt      = null;
    String            createSql = "insert into dbo.emails(email, companyId, deliveryId, status, errorText) values(?, ?, ?, ?, ?)";
    String            updateSql = "update dbo.emails set email = ?, companyId = ?, deliveryId = ?, status = ?, errorText = ? where id = ?";
    try
     {
      // Если полученный объект не пуст - создаем/изменяем запись.
      if ((email != null) && (!email.isEmpty()))
       {
        conn = this.getConnection();
        // Выбираем тип действия. Если есть идентификатор, то обновляем запись
        if (email.getId() > 0)
         {
          logger.debug("Processing update.");
          stmt = conn.prepareStatement(updateSql);
          stmt.setString(1, email.getEmail());
          stmt.setInt(2,    email.getCompanyId());
          stmt.setInt(3,    email.getDeliveryId());
          stmt.setInt(4,    email.getStatus());
          stmt.setString(5, email.getErrorText());
          stmt.setInt(6,    email.getId());
         }
        // Если идентификатора нет - добавляем запись
        else
         {
          logger.debug("Processing create.");
          stmt = conn.prepareStatement(createSql);
          stmt.setString(1, email.getEmail());
          stmt.setInt(2,    email.getCompanyId());
          stmt.setInt(3,    email.getDeliveryId());
          stmt.setInt(4,    email.getStatus());
          stmt.setString(5, email.getErrorText());
         }
        // Выполняем запрос
        stmt.executeUpdate();
       }
      // Если объект пуст - ошибка!
      else {logger.error("Can't process empty object.");}
     }
    // Перехват ИС
    catch (SQLException e) {logger.error(e.getMessage());}
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    catch (DBConnectionException e)   {logger.error(e.getMessage());}
    // Освобождение ресурсов
    finally
     {
      try {if(stmt != null) {stmt.close();} if(conn != null) {conn.close();}}
      catch (SQLException e) {logger.error("Can't free resources! Reason: " + e.getMessage());}
     }
   }

  /**
   * Метод для тестирования.
   * @param args String[] параметры метода.
  */
  public static void main(String[] args)
   {
    InitLogger.initLoggers(new String[] {"jdb", "org", "jlib", Defaults.LOGGER_NAME});
    Logger logger = Logger.getLogger(Defaults.LOGGER_NAME);
    logger.info(new EmailsDAO().findByDeliveryId(11).size());
   }

 }