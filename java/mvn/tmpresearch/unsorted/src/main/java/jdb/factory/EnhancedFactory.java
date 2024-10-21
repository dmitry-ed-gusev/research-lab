package jdb.factory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Gusev Dmitry (Гусев Дмитрий)
 * @version 1.0 (DATE: 03.03.11)
 */ //Class to demonstrate the enhanced Factory Method
public class EnhancedFactory
 {
  //Only reference to ConnectionManager
  static public ConnectionManager cm = null;
  //Main method
  public static void main(String[] args)
   {
    try
     {
      //Retrieve the only instance of ConnectionManager
      cm = cm.getInstance();
      //Create and close a connection to the Oracle database to
      //demonstrate that it works.
      Connection conn = cm.connect(cm.ORACLE, "toddt", "mypwd", "ORCL");
      cm.close();
      //Open a connection to an Access database using ODBC
      conn = cm.connect(cm.ODBC, null, null, "employees");
      cm.close();
      //Catch all the relevant errors
     }
    catch(ConnectionManagerException cme) {cme.printStackTrace();}
    catch(SQLException se) {se.printStackTrace();}
    catch(Exception e){e.printStackTrace();}
    //Use finally block to ensure database resources are closed
    finally
     {
      if(cm!=null)
      try {cm.close();} catch(SQLException se){se.printStackTrace();}
     }
   }//end main()
 }//end EnhancedFactory

//Class that manages database connections
class ConnectionManager
 {
  //Constants to represent database types
  public static final int ORACLE = 100;
  public static final int ODBC = 200;
  //Variables to hold only instance of ConnectionManager class
  private static ConnectionManager connMgr = null;
  //Holds reference to the specific connection factory
  private static AbstractConnFactory acf =null;;
  //Private constructor
  private ConnectionManager() {}
  //Method that provides connection logic
  public Connection connect (int dbType, String user, String password, String db)
   throws Exception
   {
    //Examine the dbType parameter and assign the appropriate
    //factory to the
    //acf, which is the base class type.
    switch(dbType)
     {
      //Specific factories are Singletons so get the only
      //instance and set the appropriate connection values.
      case ORACLE:
       acf = OracleConnFactory.getInstance();
       acf.dbType = "Oracle";
      break;
      case ODBC:
       acf = OdbcConnFactory.getInstance();
       acf.dbType="ODBC";
      break;
      //Error handling for unsupported database types.
      default: throw new SQLException("Type not supported");
     }//end switch
    acf.database=db;
    acf.user=user;
    acf.password=password;
    //Connect to the database and return reference.
    Connection conn = acf.connect();
    return conn;
   }
  //Close the database connection.
  public void close() throws SQLException {acf.close();}
  //Public method used to get the only instance of ConnectionManager.
  public static synchronized ConnectionManager getInstance()
   {
    if(connMgr==null) connMgr = new ConnectionManager();
    return connMgr;
   }
 }//end ConnectionManager

//Used to handle ConnectionManager specific errors
class ConnectionManagerException extends SQLException
 {
  //default constructor
  public ConnectionManagerException() {super();}
  //Constructor that allows you to specify your own error messages.
  public ConnectionManagerException(String msg) {super(msg);}
 }// end ConnectionManagerException