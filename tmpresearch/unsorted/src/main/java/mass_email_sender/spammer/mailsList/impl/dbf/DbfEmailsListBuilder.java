package mass_email_sender.spammer.mailsList.impl.dbf;

import jdb.DBConsts;
import jdb.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.utils.DBUtils;
import jlib.logging.InitLogger;
import mass_email_sender.spammer.Defaults;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.sql.*;
import java.util.TreeMap;

/**
 * Основной модуль для формирования списков мейл-адресов по данным БД Флот и БД Фирмы Регистра Судоходства.
 * Формат баз - DBF (dBase III или IV). Выбор типа адресов осуществляется с помощью значений типа-перечисления
 * RecipientType (см. модуль Defaults). Данный модуль использует только два значения из типа-перечисления:
 * RECIPIENT_TYPE_SHIPOWNERS_ENG и  RECIPIENT_TYPE_SHIPOWNERS_RUS.
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 17.12.2010)
*/

@SuppressWarnings({"JpaQueryApiInspection"})
public class DbfEmailsListBuilder
 {
  /** Логгер данного модуля. */
  private Logger logger      = Logger.getLogger(Defaults.LOGGER_NAME);
  /** Путь к БД Флот. */
  private String fleetDbPath = null;
  /** Путь к БД Фирм. */
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
   * Метод возвращает список мыло-адресофф из БД Фирмы-Флот. Пути к базам должны быть указаны в конструкторе
   * модуля или установлены с помощью методов setXXX(). Если пути к базам не будут указаны или произойдут другие
   * ошибки - метод вернет значение NULL.
   * @param recipientType RecipientType тип адресатов, для которого должен быть сформирован список адресатов. Данный метод
   * понимает только два типа адресатов - RECIPIENT_TYPE_SHIPOWNERS_ENG и RECIPIENT_TYPE_SHIPOWNERS_RUS. Если указать
   * значение NULL, то метод вернет общий список адресатов, соединяющий в себе оба указанных выше типа.
   * @return TreeMap[String, Integer] возвращаемый список email-адресов. Каждому адресу соответствует идентификатор
   * компании. Мылы не совпадают - т.е. они уникальны в пределах списка - это обеспечивается реализацией списка. 
  */
  @SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
  public TreeMap<String, Integer> getEmailsList(Defaults.RecipientType recipientType)
   {
    // Результат работы метода - список мылоф и идентификаторов компаний. Ключ (первый параметр) - мыло, ключ не
    // допускает пустых и повторяющихся значений. Значение (второй параметр) - идентификатор компании, значение также
    // не может быть пустым, но может повторяться. Если будет необходимость в уникальности идентификаторов компаний -
    // необходимо сделать TreeSet<Integer, String>
    TreeMap<String, Integer> emailsList = new TreeMap<String, Integer>();

    // Перед работой с базами проверяем пути к базам флота и фирм - они не должны быть пусты
    // и должны существовать! Также проверим, являются ли они каталогами.
    if (!StringUtils.isBlank(firmDbPath) && !StringUtils.isBlank(fleetDbPath))
     {
      logger.debug("DB FLEET and DB FIRMS databases paths not empty. Processing.");
      // Проверяем существование каталогов и что это именно каталоги
      File fleetDBFile = new File(fleetDbPath);
      File firmDBFile  = new File(firmDbPath);
      if (fleetDBFile.exists() && fleetDBFile.isDirectory() && firmDBFile.exists() && firmDBFile.isDirectory())
       {
        // Все в порядке с путями - работаем
        logger.debug("DB FLEET and DB FIRMS databases paths are ok. Processing.");
        // Конфиг для соединения с ДБФ базой фирм
        DBConfig firmConfig = new DBConfig();
        firmConfig.setDbType(DBConsts.DBType.DBF);
        firmConfig.setDbName(firmDbPath);
        // Конфиг для соединения с ДБФ базой флота
        DBConfig fleetConfig = new DBConfig();
        fleetConfig.setDbType(DBConsts.DBType.DBF);
        fleetConfig.setDbName(fleetDbPath);
        // Выборка из БД флот
        String fleetSql = "select firm_id7 as operatorId, firm_id2 as shipownerId, firm_id1 as ownerId from fleet where " +
                          "sreg = 1 or sreg = 2";
        // Переменные для хранения классов для соединения с СУБД и выборки данных
        Connection        fleetConn = null;
        Connection        firmConn  = null;
        Statement         fleetStmt;
        PreparedStatement firmStmt;
        ResultSet         fleetRs;
        ResultSet         firmRs;
        // Преподготовленный запрос для выборки данных из БД Фирм. Формируем в зависимости от входного параметра.
        String firmSql = "select email from firm where firm_id = ?";
        if (recipientType != null)
         {
          switch (recipientType)
           {
            case RECIPIENT_TYPE_SHIPOWNERS_ENG: firmSql += " and stran_id <> 102"; break;
            case RECIPIENT_TYPE_SHIPOWNERS_RUS: firmSql += " and stran_id = 102"; break;
           }
         }
        // Отладочный вывод полученного запроса
        logger.debug("Generated firm-DB query: [" + firmSql + "].");
        try
         {
          fleetConn = DBUtils.getDBConn(fleetConfig);
          fleetStmt = fleetConn.createStatement();
          fleetRs   = fleetStmt.executeQuery(fleetSql);
          // Если есть такие суда - работаем
          if (fleetRs.next())
           {
            logger.debug("Ships found. Processing emails search.");
            // Открываем соединение с базой фирм
            firmConn = DBUtils.getDBConn(firmConfig);
            firmStmt = firmConn.prepareStatement(firmSql);

            // Счетчик для выборки из БД флота
            int counter = 0;
            // Переменные для хранения значений идентификаторов и емайлов
            int operatorId;
            int shipownerId;
            int ownerId;
            String operatorEmail;
            String shipownerEmail;
            String ownerEmail;

            // В цикле обрабатываем данные
            do
             {

              // Идентификатор оператора
              operatorId     = fleetRs.getInt("operatorId");
              // Мыло оператора
              if (operatorId > 0)
               {
                firmStmt.setInt(1, operatorId);
                firmRs = firmStmt.executeQuery();
                if (firmRs.next()) {operatorEmail = firmRs.getString("email");}
                else               {operatorEmail = null;}
               }
              else {operatorEmail = null;}

              // Идентификатор судовладельца
              shipownerId    = fleetRs.getInt("shipownerId");
              // Мыло судовладельца
              if (shipownerId > 0)
               {
                firmStmt.setInt(1, shipownerId);
                firmRs = firmStmt.executeQuery();
                if (firmRs.next()) {shipownerEmail = firmRs.getString("email");}
                else               {shipownerEmail = null;}
               }
              else {shipownerEmail = null;}

              // Идентификатор собственника
              ownerId        = fleetRs.getInt("ownerId");
              // Мыло собственника
              if (ownerId > 0)
               {
                firmStmt.setInt(1, ownerId);
                firmRs = firmStmt.executeQuery();
                if (firmRs.next()) {ownerEmail = firmRs.getString("email");}
                else               {ownerEmail = null;}
               }
              else {ownerEmail = null;}

              // Непосредственно выбор значений и формирование списка
              if ((operatorId > 0) && (!StringUtils.isBlank(operatorEmail)))
               {emailsList.put(operatorEmail, operatorId);}
              else if ((shipownerId > 0) && (!StringUtils.isBlank(shipownerEmail)))
               {emailsList.put(shipownerEmail, shipownerId);}
              else if ((ownerId > 0) && (!StringUtils.isBlank(ownerEmail)))
               {emailsList.put(ownerEmail, ownerId);}
              // Увеличиваем счетчик
              counter++;
             }
            while (fleetRs.next());
            // Количество выбранных записей
            logger.debug("FLEET database result set count (all selected from fleet): " + counter);
            logger.debug("emailsList size:                                           " + emailsList.size());
           }
          // Если судов воопче не найдено - сообщаем в лог
          else {logger.warn("No ships! Can't process.");}
         }
        catch (DBModuleConfigException e) {logger.error(e.getMessage());}
        catch (DBConnectionException e)   {logger.error(e.getMessage());}
        catch (SQLException e)            {logger.error(e.getMessage());}
        // Освобождение ресурсов
        finally
         {
          try {if (fleetConn != null) {fleetConn.close();} if (firmConn != null) {firmConn.close();}}
          catch (SQLException e) {logger.error(e.getMessage());}
         }
       }
      // Если какой-то изи каталогов не существует или не является каталогом - пишем
      // в лог ошибку и возвращаем NULL
      else
       {
        logger.error("DB FLEETE path [" + fleetDbPath + "] or DB FIRMS path [" + firmDbPath + "] " +
                     "not exists or not a directory! Can't process!");}
     }
    // Если пути пусты - сообщаем в лог об ошибке и возвращаем NULL
    else {logger.error("DB FLEET path [" + fleetDbPath + "] or DB FIRMS path [" + firmDbPath + "] is empty! Can't process!");} 

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
    InitLogger.initLoggers(new String[] {"spammer", "jdb"});
    Logger logger = Logger.getLogger("spammer");

    DbfEmailsListBuilder builder = new DbfEmailsListBuilder("\\\\rshead\\db002\\new\\fleet", "\\\\rshead\\db002\\new\\firm");
    TreeMap<String, Integer> map = builder.getEmailsList(Defaults.RecipientType.RECIPIENT_TYPE_SHIPOWNERS_ENG);
    logger.info("\n" + map);
    if (map != null) {logger.info("\n" + map.size());}
   }

 }