package mass_email_sender.spammer;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Значения по умолчанию для всего приложения. Также данный модуль содержит набор классов-перечислений, используемых в
 * приложении в качестве справочников.
 * @author Gusev Dmitry (Дмитрий)
 * @version 4.1 (DATE: 20.12.2010)
*/

public interface Defaults
 {
  /** Весрия системы почтовой рассылки. */
  public static final String SYSTEM_VERSION = "MassEmailsSender system, v. 3.1.0 (22.12.2010), (C) RMRS";

  /** Тип-перечисление возможных статусов рассылки. */
  public static enum DeliveryStatus
   {
    DELIVERY_STATUS_UNKNOWN              ("Статус рассылки неизвестен!",                          -1),
    DELIVERY_STATUS_OK                   ("Рассылка успешно завершена.",                           0),
    DELIVERY_STATUS_IN_PROCESS           ("Рассылка в процессе выполнения.",                       1),
    DELIVERY_STATUS_FAILED               ("Рассылка завершилась неудачно (досрочно). Смотри лог.", 2),
    DELIVERY_STATUS_FINISHED_WITH_ERRORS ("Рассылка завершена с ошибками. Смотри лог.",            3);
    // Поля класса-перечисления
    private final String strValue;
    private final int intValue;
    // Конструктор класса-перечисления
    DeliveryStatus(String strValue, int intValue) {this.strValue = strValue; this.intValue = intValue;}
    // Методы доступа к полям класса-перечисления
    public String getStrValue() {return strValue;}
    public int    getIntValue() {return intValue;}
    // Поиск статуса рассылки по целочисленному значению
    public static DeliveryStatus findByIntValue(int i)
     {
      // По умолчанию - статус не известен
      DeliveryStatus status = DELIVERY_STATUS_UNKNOWN;
      // Перебираем значения статусов рассылки
      if      (DELIVERY_STATUS_OK.intValue                   == i) {status = DELIVERY_STATUS_OK;}
      else if (DELIVERY_STATUS_IN_PROCESS.intValue           == i) {status = DELIVERY_STATUS_IN_PROCESS;}
      else if (DELIVERY_STATUS_FAILED.intValue               == i) {status = DELIVERY_STATUS_FAILED;}
      else if (DELIVERY_STATUS_FINISHED_WITH_ERRORS.intValue == i) {status = DELIVERY_STATUS_FINISHED_WITH_ERRORS;}
      // Возвращаем результат
      return status;
     }
   }

  /** Тип-перечисление возможных типов рассылки. */
  public static enum DeliveryType
   {
    DELIVERY_TYPE_UNKNOWN  ("Тип рассылки неизвестен!", -1),
    DELIVERY_TYPE_STANDARD ("Стандартная рассылка.",     0),
    DELIVERY_TYPE_TEST     ("Тестовая рассылка.",        1);
    // Поля класса-перечисления
    private final String strValue;
    private final int    intValue;
    // Конструктор класса-перечисления
    DeliveryType(String strValue, int intValue) {this.strValue = strValue; this.intValue = intValue;}
    // Методы доступа к полям класса-перечисления
    public String getStrValue() {return strValue;}
    public int    getIntValue() {return intValue;}
    // Поиск типа рассылки по целочисленному значению
    public static DeliveryType findByIntValue(int i)
     {
      // По умолчанию - тип неизвестен
      DeliveryType type = DELIVERY_TYPE_UNKNOWN;
      // Перебираем значения типов рассылки
      if      (DELIVERY_TYPE_STANDARD.intValue == i) {type = DELIVERY_TYPE_STANDARD;}
      else if (DELIVERY_TYPE_TEST.intValue     == i) {type = DELIVERY_TYPE_TEST;}
      // Возвращаем результат
      return type;
     }
   }

  /** Тип-перечисление возможных типов получателей рассылки. */
  public static enum RecipientType
   {
    RECIPIENT_TYPE_TEST          ("Тестовый список рассылки (отдел 019).",                  1),
    RECIPIENT_TYPE_SHIPOWNERS_RUS("Судовладельцы, зарегистрированные на территории РФ.",    2),
    RECIPIENT_TYPE_SHIPOWNERS_ENG("Судовладельцы, зарегистрированные не на территории РФ.", 3);
    // Поля
    private final String strValue;
    private final int    intValue;
    // Конструктор
    RecipientType(String strValue, int intValue) {this.strValue = strValue; this.intValue = intValue;}
    // Методы доступа к полям
    public String getStrValue() {return strValue;}
    public int    getIntValue() {return intValue;}
    // Поиск типа получателей по целочисленному значению
    public static RecipientType findByIntValue(int i)
     {
      RecipientType type = null;
      if      (RECIPIENT_TYPE_TEST.intValue           == i) {type = RECIPIENT_TYPE_TEST;}
      else if (RECIPIENT_TYPE_SHIPOWNERS_RUS.intValue == i) {type = RECIPIENT_TYPE_SHIPOWNERS_RUS;}
      else if (RECIPIENT_TYPE_SHIPOWNERS_ENG.intValue == i) {type = RECIPIENT_TYPE_SHIPOWNERS_ENG;}
      return type;
     }
    // Возвращение всего списка типов получателей рассылки
    public static ArrayList<RecipientType> findAllTypes()
     {
      ArrayList<RecipientType> list = new ArrayList<RecipientType>();
      list.addAll(Arrays.asList(RecipientType.values()));
      return list;
     }
   }

  /** Логгер данного приложения. Не конфигуриццо из командной строки. */
  public static final String LOGGER_NAME     = "spammer";
  /** Шаблон для сообщений логгера приложения. Не конфигуриццо из командной строки. */
  public static final String LOGGER_PATTERN  = "%d{dd/MM/yyyy HH:mm:ss} %p [%c{1}] %m%n";
  /** Файл журнала данного приложения. Не конфигуриццо из командной строки. */
  public static final String LOGGER_FILE     = "spammer.log";
  /**
   * Файл конфигурации соединения с СУБД для приложения (используется DAO-компонентами). Не конфигуриццо из
   * командной строки. Если необходимо будет конфигурить из командной строки - осторожно, используется в DAO!
  */
  public static final String DBCONFIG_FILE   = "spammer.xml";

  /** Путь к ДБФ базе флота. Конфигуриццо из командной строки. */
  public static final String DB_FLEET_PATH                 = "c:\\temp\\fleet";
  /** Параметр командной строки для пути к БД Флот. */
  public static final String CMDLINE_DB_FLEET_PATH         = "fleetPath";
  /** Путь к ДБФ базе фирм. Конфигуриццо из командной строки. */
  public static final String DB_FIRM_PATH                  = "c:\\temp\\firm";
  /** Параметр командной строки для пути к БД Фирм. */
  public static final String CMDLINE_DB_FIRM_PATH          = "firmPath";
  /** Хост почтового сервера. Конфигуриццо из командной строки. */
  public static final String MAIL_HOST                     = "smtp.rs-head.spb.ru";
  /** Параметр командной строки для хоста почтового сервера. */
  public static final String CMDLINE_MAIL_HOST             = "mailHost";
  /** Параметр командной строки - порт почтового сервера. */
  public static final String CMDLINE_MAIL_PORT             = "mailPort";
  /** Обратный адрес для писем рассылки. Конфигуриццо из командной строки. */
  public static final String MAIL_FROM                     = "info_na@rs-head.spb.ru";
  /** Параметр командной строки для обратного адреса рассылки. */
  public static final String CMDLINE_MAIL_FROM             = "mailFrom";
  /** Параметр командной строки - идентификатор рассылки из БД Рассылок. */
  public static final String CMDLINE_DELIVERY_ID           = "deliveryId";
  /** Параметр командной строки - путь к файлам рассылок (к файловому репозиторию). */
  public static final String CMDLINE_DELIVERIES_FILES_PATH = "deliveriesFiles";

  /** Опция командной строки - показать хелп (описание опций приложения). */
  public static final String CMDLINE_SHOW_HELP             = "help";
  /** Опция командной строки - включение демо-режима. */
  public static final String CMDLINE_DEMO_MODE             = "demo";
  /** Опция командной строки - показать версию системы. */
  public static final String CMDLINE_SHOW_VERSION          = "version";

  /** Тестовая отправка рассылки на указанный адрес (или список адресов, разделенных запятыми). */
  public static final String CMDLINE_TEST_MAIL_TO          = "testMailTo";
  /***/
  public static final String CMDLINE_MAIL_ENCODING         = "encoding";

  /***/
  public static final String[] EMAILS_DELIMITERS_WRONG = {";"};
  /***/
  public static final String   EMAILS_DELIMITER_RIGHT  = ",";
 }