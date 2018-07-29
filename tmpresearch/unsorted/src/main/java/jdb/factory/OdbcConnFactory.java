package jdb.factory;

import java.sql.Connection;

/**
 * @author Gusev Dmitry (Ãףסוג Äלטענטי)
 * @version 1.0 (DATE: 03.03.11)
 */ //Subclass of the AbstractConnFactory for connecting to an ODBC database.
class OdbcConnFactory extends AbstractConnFactory
 {
  //Private variables
  private static OdbcConnFactory ocf = null;
  //Private constructor
  private OdbcConnFactory()
   {
    jdbcUrl ="jdbc:odbc:";
    driver = "sun.jdbc.odbc.JdbcOdbcDriver";
   }

  //Public method used to get the only instance of OdbcConnFactory.
  public static synchronized AbstractConnFactory getInstance()
   {
    //If not initialized, do it here. Otherwise return existing object.
    if(ocf==null) ocf = new OdbcConnFactory();
    return ocf;
   }

  //Overridden method to open a database connection
  public Connection connect() throws Exception
   {
    //Configure the JDBC URL
    jdbcUrl = jdbcUrl + database;
    //Call the base class method to provide the connection
    return super.connect();
   }
 }//end OdbcConnFactory
