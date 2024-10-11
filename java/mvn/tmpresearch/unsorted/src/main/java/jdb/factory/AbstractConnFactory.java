package jdb.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @version 1.0 (DATE: 03.03.11)
 */ //Abstract class that defines interface for factory objects
abstract class AbstractConnFactory
 {
  //Protected variables that hold database specific information
  protected static Connection conn;
  protected String dbType   = null;
  protected String user     = null;
  protected String password = null;
  protected String driver   = null;
  protected String jdbcUrl  = null;
  protected String database = null;
  //Close the database connection
  public void close() throws SQLException
   {
    //Check if conn is null, if not close it and set to null
    if (conn!=null)
     {
      System.out.println("Closing connection");
      System.out.println();
      conn.close();
      conn = null;
     }
   }

  //Access method to return a reference to a Connection object
  public Connection connect() throws Exception
   {
    if(conn!=null) {System.out.println("Connection exists. Returning instance...");}
    else
     {
      System.out.println("Connection not created. Opening connection phase...");
      openConnection();
     }//end if
    return conn;
   }

  //Private method to create connection.
  private void openConnection() throws Exception
   {
    //Register a driver
    Class.forName(driver).newInstance();
    //Obtain a Connection object
    System.out.println("Connecting to " + dbType + " database...");
    conn = DriverManager.getConnection(jdbcUrl, user, password);
    System.out.println("Connection successful..");
   }
 }//end AbstractConnFactory
