package jdb.exceptions;

import java.sql.*;

/** �����, ����������� �� ��� ��������� ������ ������� SqlStatement � PSqlStatement. */
public class SqlStatementException extends SQLException
 {
  public SqlStatementException()           {super();}
  public SqlStatementException(String msg) {super(msg);}
 }

