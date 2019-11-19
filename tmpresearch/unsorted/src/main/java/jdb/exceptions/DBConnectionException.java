package jdb.exceptions;

/**
 * Класс реализует унифицированную ИС - ошибка при соединении с СУБД.
 *
 * @deprecated
 *
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 31.01.2008)
 */
public class DBConnectionException extends Exception
 {
  public DBConnectionException() {super();}
  public DBConnectionException(String message) {super(message);}
 }