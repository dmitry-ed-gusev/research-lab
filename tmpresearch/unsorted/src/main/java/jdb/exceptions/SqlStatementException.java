package jdb.exceptions;

import java.sql.*;

/** Класс, реализующий ИС для обработки ошибок классов SqlStatement и PSqlStatement. */
public class SqlStatementException extends SQLException
 {
  public SqlStatementException()           {super();}
  public SqlStatementException(String msg) {super(msg);}
 }

