package jdb.exceptions;

/**
 * ИС для ошибок моделей баз данных, таблиц, полей индексов и т.п.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 14.07.2009)
*/

public class DBModelException extends Exception
 {
  public DBModelException() {super();}
  public DBModelException(String message) {super(message);}
 }