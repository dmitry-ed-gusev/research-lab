package jdb.factory;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author Gusev Dmitry (Ãףסוג Äלטענטי)
 * @version 1.0 (DATE: 03.03.11)
 */ //Factory class
class ConnectionFactory
 {
  public ConnectionFactory() {}
  //Factory method to return a Connection object
  public Connection connect()throws Exception
   {
    //Load a driver
    String driver = "com.informix.jdbc.IfxDriver";
    Class.forName(driver).newInstance();
    //Set connection parameters
    System.out.println("Connecting to database...");
    String jdbcUrl = "jdbc:informix-sqli://" + "10.1.254.2:1526" + ":INFORMIXSERVER=" +
                 "edu" + ";user=" + "informix" + ";" + "password=" + "123456";
    //String jdbcUrl = "jdbc:oracle:thin:@localhost:1521:ORCL";
    //String user = "toddt";
    //String pwd = "mypwd";
    //Create a Connection object
    //Connection conn = DriverManager.getConnection(jdbcUrl,user,pwd);
    Connection conn = DriverManager.getConnection(jdbcUrl);
    System.out.println("Connection successful...");
    //Return Connection object
    return conn;
   }
 }//end ConnectionFactory
