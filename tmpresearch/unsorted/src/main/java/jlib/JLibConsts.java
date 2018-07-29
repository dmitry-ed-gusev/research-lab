package jlib;

import org.apache.log4j.Level;

/**
 * ������ ������ �������� ��� ��������� ���������� JLIB.
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 14.04.2011)
*/

public interface JLibConsts
 {

  /** �������� ���� MAILER ��� ������ ��������� (JMAIL). */
  public final static String JMAIL_MAILER   = "JMAIL-MAILER-BY-GUS";
  /** ��������� �� ��������� ��� ������ ���� � ������ ������. */
  public static final String JMAIL_ENCODING = "windows-1251";

  /** ���������� ����� � ��������������� �������� (�����). */
  public static final String SERIALIZED_OBJECT_EXTENSION_1  = "_1_";
  /** ���������� ����� � ��������������� �������� (�����). */
  public static final String SERIALIZED_OBJECT_EXTENSION_2  = "_2_";

  /** ���������� ������ � ��������������� �������� (�����). */
  public static final String ZIPPED_OBJECT_EXTENSION        = ".zip";

  /** ������ (� ������) ������ ������/������ ��� ������ � �������. */
  public static final int    FILE_BUFFER                    = 32768;

  /** ����������� �� ��������� ��� ������ ����� � �����. */
  public static final char   DEFAULT_DIR_DELIMITER          = '/';
  /** ����������� �� ��������� ��� ����� ����� � ���������� ���� � �����/�������� (��� �� ��������� Windows). */
  public static final char   DEFAULT_DRIVE_LETTER_DELIMITER = ':';
  /** ����������� ��� ����� � ������, ������� ������ ���� �������� �� ����������� �� ���������. */
  public static final char[] DEPRECATED_DELIMITERS          = {'\\'};

  /** ������ ������� ��� ������� - ������ �� ���������. */
  public static final String LOGGER_PATTERN_DEFAULT        = "%d{HH:mm:ss}(%rms) %-5p [%c{1}] %m%n";
  /** ������ ������� ��� ������� - ���������������� ������. */
  public final static String LOGGER_PATTERN_DETAILED       = "%d{dd/MM/yyyy HH:mm:ss}(%-5rms) {%17M} [%t] %p %c %x - %m%n";
  /** ������ ������� ��� ������� - ������� ������. */
  public final static String LOGGER_PATTERN_SHORT          = "%d{HH:mm:ss} %p [%c{1}] %m%n";
  /** ������ ���� ��� DailyRollingFileAppender - ����� ���-����� � ������� ������ ����. */
  public static final String LOGGER_FAPPENDER_DATE_PATTERN = "'.'dd-MM-yyyy";
  /** �������� �� ��������� ��� ������ ���������� ���������. */
  public final static Level LOG_LEVEL_DEFAULT             = Level.DEBUG;

 }