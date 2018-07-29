package jlib;

import org.apache.log4j.Level;

/**
 * Данный модуль содержит все константы библиотеки JLIB.
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 14.04.2011)
*/

public interface JLibConsts
 {

  /** Значение поля MAILER для модуля почтовика (JMAIL). */
  public final static String JMAIL_MAILER   = "JMAIL-MAILER-BY-GUS";
  /** Кодировка по уиолчанию для текста темы и текста письма. */
  public static final String JMAIL_ENCODING = "windows-1251";

  /** Расширение файла с сериализованным объектом (любым). */
  public static final String SERIALIZED_OBJECT_EXTENSION_1  = "_1_";
  /** Расширение файла с сериализованным объектом (любым). */
  public static final String SERIALIZED_OBJECT_EXTENSION_2  = "_2_";

  /** Расширение архива с сериализованным объектом (любым). */
  public static final String ZIPPED_OBJECT_EXTENSION        = ".zip";

  /** Размер (в байтах) буфера чтения/записи при работе с файлами. */
  public static final int    FILE_BUFFER                    = 32768;

  /** Разделитель по умолчанию для полных путей к файлу. */
  public static final char   DEFAULT_DIR_DELIMITER          = '/';
  /** Разделитель по умолчанию для буквы диска и остального пути к файлу/каталогу (для ОС семейства Windows). */
  public static final char   DEFAULT_DRIVE_LETTER_DELIMITER = ':';
  /** Разделители для путей к файлам, которые должны быть заменены на разделитель по умолчанию. */
  public static final char[] DEPRECATED_DELIMITERS          = {'\\'};

  /** Шаблон записей для журнала - шаблон по умолчанию. */
  public static final String LOGGER_PATTERN_DEFAULT        = "%d{HH:mm:ss}(%rms) %-5p [%c{1}] %m%n";
  /** Шаблон записей для журнала - детализированный шаблон. */
  public final static String LOGGER_PATTERN_DETAILED       = "%d{dd/MM/yyyy HH:mm:ss}(%-5rms) {%17M} [%t] %p %c %x - %m%n";
  /** Шаблон записей для журнала - краткий шаблон. */
  public final static String LOGGER_PATTERN_SHORT          = "%d{HH:mm:ss} %p [%c{1}] %m%n";
  /** Шаблон даты для DailyRollingFileAppender - смена лог-файла в полночь каждый день. */
  public static final String LOGGER_FAPPENDER_DATE_PATTERN = "'.'dd-MM-yyyy";
  /** Значение по умолчанию для уровня отладочных сообщений. */
  public final static Level LOG_LEVEL_DEFAULT             = Level.DEBUG;

 }