package jlib.exceptions;

/**
 * �� - ������������ ��� ������� ������� ������� ������ (null, ������ ������, ������ �� ����� ��������).
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 10.03.2009)
*/

public class EmptyPassException extends Exception
 {
  public EmptyPassException() {super();}
  public EmptyPassException(String s) {super(s);}
 }