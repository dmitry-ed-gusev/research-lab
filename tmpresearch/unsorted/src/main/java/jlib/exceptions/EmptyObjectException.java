package jlib.exceptions;

/**
 * ИС - полученный/обрабатываемый объект пуст (null).
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 20.05.2008)
*/

public class EmptyObjectException extends Exception
 {
  public EmptyObjectException() {super();}
  public EmptyObjectException(String message) {super(message);}
 }