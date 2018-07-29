package jdb.nextGen.exceptions;

/**
 * ����� �� ��� ��������� ������ ������ ����������. ����� ������������ (�� �����������).
 * @author Gusev Dmitry (����� �������)
 * @version 2.0 (DATE: 04.04.11)
*/

public final class JdbException extends Exception
 {
  public JdbException(String message)                {super(message);}
  public JdbException(String s, Throwable throwable) {super(s, throwable);}
  public JdbException(Throwable throwable)           {super(throwable);}
 }