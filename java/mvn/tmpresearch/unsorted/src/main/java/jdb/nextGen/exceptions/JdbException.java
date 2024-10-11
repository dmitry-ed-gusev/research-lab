package jdb.nextGen.exceptions;

/**
 * Класс ИС для различных ошибок данной библиотеки. Класс терминальный (не расширяемый).
 * @author Gusev Dmitry (Гусев Дмитрий)
 * @version 2.0 (DATE: 04.04.11)
 *
 * @deprecated
*/

public final class JdbException extends Exception
 {
  public JdbException(String message)                {super(message);}
  public JdbException(String s, Throwable throwable) {super(s, throwable);}
  public JdbException(Throwable throwable)           {super(throwable);}
 }