package jdb.factory;

import java.sql.Connection;

/**
 * @author Gusev Dmitry (Ãףסוג Äלטענטי)
 * @version 1.0 (DATE: 03.03.11)
 */ // Subclass of the AbstractConnFactory for connecting to an Oracle database.
class OracleConnFactory extends AbstractConnFactory
 {
  //Private variables
  private static OracleConnFactory ocf= null;
  //Private constructor
  private OracleConnFactory()
   {
    jdbcUrl = "jdbc:oracle:thin:@localhost:1521:";
    driver = "oracle.jdbc.driver.OracleDriver";
   }
  //Public method used to get the only instance of OracleConnFactory.
  public static synchronized AbstractConnFactory getInstance()
   {
    //If not initialized, do it here. Otherwise just return existing object.
    if(ocf==null) ocf = new OracleConnFactory();
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
 }//end OracleFactory
