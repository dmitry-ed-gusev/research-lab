package jlib.common;

/**
 * Данный класс объединяет общие константы для всех частей библиотеки.
 * @author Gusev Dmitry
 * @version 2.0
*/

public interface Consts
 {
  /** Разделитель для значений параметра в списке-строке. Используется в модулях IniFileReader, Utils, JMail. */
  public static final String VALUES_DELIM = ",";

  /** Разделитель для пар значений в списке-строке. */
  public static final String PAIRS_DELIM  = ";";

  /** Разделитель для имени и значения в паре имя/значение: name[KEY_VALUE_DELIM]value (для строк). */
  public static final String KEY_VALUE_DELIM = "=";

  /**
   * Константа для обозначения значения "УРОВЕНЬ СООБЩЕНИЙ ДЛЯ ВЕДЕНИЯ ЖУРНАЛА КЛАССА".
   * Возможные значения для параметра, обозначаемого данной константой
   * см. в классе org.apache.log4j.Level.
  */
  public final static String LOGGER_LOG_LEVEL   = "log_level";
  /**  */
  public final static String LOGGER_FILE_NAME   = "log_file_name";
  /**  */
  public final static String LOGGER_LOG_FORMAT  = "log_format";
  /***/
  public final static String LOGGER_PARENT_NAME = "logger_parent_name";

  /** Разделитель по умолчанию для элементов времени. Текущее значение: ":".*/
  public final static String TIME_DEF_DELIM = ":";
  /** Разделитель по умолчанию для элементов даты. Текущее значение: "/".*/
  public final static String DATE_DEF_DELIM = "/";
  
  /**
   * Формат по умолчанию для вывода диагностических сообщений - ведение лога.
   * Более детальное описание см. в документации к классу log4j.<br>
   * Текущее значение: "%d{dd/MM/yyyy HH:mm:ss}(%rms) [%t] %p %c %x - %m%n" <br>
   * %d{dd/MM/yyyy HH:mm:ss} - дата и время, формат очевиден <br>
   * (%rms) - время в миллисекундах, прошедшее со старта приложения до генерации
   * сообщения <br>
   * {%M} - метод, из которого пришло сообщение (медленная опция...) <br>
   * [%t] - наименование потока, инициировавшего вывод сообщения <br>
   * %p   - приоритет сообшения (INFO,DEBUG......) <br>
   * %c   - категория сообщения <br>
   * %x   - вывод сообщения NDC(nested diagnostic context), если оно есть <br>
   * %m   - вывод пользовательского сообщения <br>
   * %n   - универсальный символ перехода на новую строку (кроссплатформенный) <br>
  */
  public final static String LOGGER_LOG_FORMAT_DEFAULT = "%d{dd/MM/yyyy HH:mm:ss}(%-5rms) {%17M} [%t] %p %c %x - %m%n";

 }
