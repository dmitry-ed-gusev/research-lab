package dgusev.apps.mass_email_sender.spammer.mailsList.impl.dbf;

import dgusev.apps.mass_email_sender.spammer.Defaults;
import dgusev.apps.mass_email_sender.spammer.mailsList.interfaces.EmailsListInterface;
import org.apache.log4j.Logger;

import java.util.TreeMap;

/**
 * Реализация интерфейса получения списка почтовых адресов. Данный модуль возвращает список мейл-адресов
 * англоязычных (не российских) судовладельцев. Модуль основан движке модуля DbfEmailsListBuilder.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 17.12.10)
*/

public class DbfShipownersEng implements EmailsListInterface
 {
  /** Логгер данного модуля. */
  private Logger logger      = Logger.getLogger(Defaults.LOGGER_NAME);
  /** Путь к БД Флот. */
  private String fleetDbPath = null;
  /** Путь к БД Фирм. */
  private String firmDbPath  = null;

  public DbfShipownersEng(String fleetDbPath, String firmDbPath)
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

  @Override
  public TreeMap<String, Integer> getEmailsList() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
    DbfEmailsListBuilder builder = new DbfEmailsListBuilder(fleetDbPath, firmDbPath);
    return builder.getEmailsList(Defaults.RecipientType.RECIPIENT_TYPE_SHIPOWNERS_ENG);
   }

 }
