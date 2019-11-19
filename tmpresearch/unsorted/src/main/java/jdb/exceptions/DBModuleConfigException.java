package jdb.exceptions;

/**
 * Класс реализует ИС - ошибки конфигурирования.
 *
 * @deprecated
 *
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 26.04.2010)
*/

public class DBModuleConfigException extends Exception
 {
  public DBModuleConfigException() {super();}
  public DBModuleConfigException(String message) {super(message);}
 }