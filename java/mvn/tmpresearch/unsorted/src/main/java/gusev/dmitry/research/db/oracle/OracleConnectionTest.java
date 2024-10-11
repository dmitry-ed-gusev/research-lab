package gusev.dmitry.research.db.oracle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 10.10.12)
 */

public class OracleConnectionTest {

    @SuppressWarnings("JDBCResourceOpenedButNotSafelyClosed")
    public static void main(String[] args) {
        Log log = LogFactory.getLog(OracleConnectionTest.class);
        Connection connection = null;
        try {
            // Load the JDBC driver
            //String driverName = "oracle.jdbc.driver.OracleDriver";
            //Class.forName(driverName);
            //log.info("Loaded driver [" + driverName + "].");

            // Create a connection to the database
            String serverName = "pst-ora2";
            String portNumber = "1521";
            String sid        = "pstora21";
            String url        = "jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + sid;
            String username   = "pru";
            String password   = "ISam18Dr";
            connection = DriverManager.getConnection(url, username, password);
            if (connection != null) {
                log.info("Successfully connected to DB!");
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("select distinct(operator) from pru001td001");
                if (rs.next()) { // some data exists
                    do {
                        log.info("Operator -> " + rs.getString(1));
                    } while(rs.next());
                } else { // no data
                    log.info("No data found!");
                }
            } else {
                log.fatal("Connection not established!");
            }
        } /*catch (ClassNotFoundException e) {
            // Could not find the database driver
            log.error("Could not find the database driver! Reason: " + e.getMessage());
        } */catch (SQLException e) {
            // Could not connect to the database or something else
            log.error("SQL error occured: " + e.getMessage());
        } finally { // we should close all used resources!
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.fatal("Can't close connection to DBMS! Reason: " + e.getMessage());
                }
            }
        }
    }

}