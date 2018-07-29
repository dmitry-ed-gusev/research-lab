package jlib.exceptions;

/**
 * »— - генерируетс€ при попытке задани€ пустого парол€ (null, пуста€ строка, строка из одних пробелов).
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 10.03.2009)
*/

public class EmptyPassException extends Exception
 {
  public EmptyPassException() {super();}
  public EmptyPassException(String s) {super(s);}
 }