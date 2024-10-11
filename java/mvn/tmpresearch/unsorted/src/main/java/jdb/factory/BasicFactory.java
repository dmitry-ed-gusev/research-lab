package jdb.factory;

import java.sql.*;

//Class to demonstrate use of Factory Method
public class BasicFactory
 {
  public static ConnectionFactory cf = null;
  public static Connection conn = null;
  public static void main(String[] args)
   {
    try
     {
      //Instantiate a ConnectionFactory object
      cf = new ConnectionFactory();
      //Obtain a connection to the database
      conn = cf.connect();
      //Populate a result set and show the results
      Statement stmt = conn.createStatement();
      stmt.executeUpdate("Database norm_docs");
      Statement stmt2 = conn.createStatement();
      stmt2.executeUpdate("Database norm_docs");
      ResultSet rs = stmt.executeQuery("SELECT * FROM norm_docs order by Name");
      ResultSet rs2 = null;
      //Iterate through the result set
      while(rs.next())
       {
        //Retrieve column values and display values
        String name = rs.getString("name");
        rs2 = stmt2.executeQuery("select * from changes_journal");
        while (rs2.next()) {}
        rs2.close();
        System.out.println("Name: " + name);
       }
     }
    //Standard error handling.
    catch(SQLException se) {se.printStackTrace();}
    catch (Exception e){e.printStackTrace();}
    //Ensure all database resources are closed.
    finally
     {
      try {if (conn!=null) conn.close();} catch(SQLException se){se.printStackTrace();}
     }//end try

    System.out.println("Goodbye!");
   }
 }//end BasicFactory class
