package gusev.dmitry.research.db.hsql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 23.10.12)
 */
public class HsqlDbEngine {

    //private Log log = LogFactory.getLog(HsqlDbEngine.class);

    private static final String   HSQL_DRIVER_NAME = "org.hsqldb.jdbcDriver";
    private static final String   DB_PATH          = System.getProperty("user.dir") + "\\HsqlDB";
    private static final String   DB_NAME          = "myHsqlDb";
    private static final String   TABLE_NAME       = "simpleTable";
    private static final String   USER             = "root";
    private static final String   PASS             = "root";

    // additional parameters for connection url (this parameters could be added to the end of url, separator ;):
    //  ifexists=true - connect only to existing db, if db doesn't exist, connection will throw exception
    //  shutdown=true - shutdown the database when the last connection is closed (not recommended for apps,
    //                   only for tests)
    private static final String   CONN_URL         = "jdbc:hsqldb:file:" + DB_PATH + "/" + DB_NAME;

    private static final String   CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " (id IDENTITY , value VARCHAR(32))";
    private static final String   INSERT_DATA_SQL  = "INSERT INTO " + TABLE_NAME + "(value) VALUES(?)";
    private static final String   SELECT_DATA_SQL  = "SELECT id, value FROM " + TABLE_NAME;
    private static final String   SHUTDOWN_SQL     = "SHUTDOWN";
    private static final String[] TABLE_DATA       = {"Москва", "С-Петербург", "Киев", "Минск", "Новосибирск", "Севастополь"};

    @SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed", "CallToDriverManagerGetConnection"})
    public static void main(String[] args) {

        Log log = LogFactory.getLog(HsqlDbEngine.class);
        log.info("Starting...");
        Connection connection = null;
        Statement  statement  = null;
        try {
            Class.forName(HSQL_DRIVER_NAME); // Loading driver class
            log.debug("Database driver loaded.");
            connection = DriverManager.getConnection(CONN_URL, USER, PASS);
            log.debug("Successfully connected to database.");
            statement = connection.createStatement();

            // If our table doesn't exists - we will create it
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tablesRs = metaData.getTables("PUBLIC", "PUBLIC", TABLE_NAME.toUpperCase(), null);
            if (!tablesRs.next()) { // our table doesn't exist in db
                statement.executeUpdate(CREATE_TABLE_SQL);
                log.debug("Table [" + TABLE_NAME + "] created!");
            } else {
                log.debug("Table [" + TABLE_NAME + "] already exists!");
            }

            // Fill our table with data
            PreparedStatement pstatement = connection.prepareStatement(INSERT_DATA_SQL);
            for (String value : TABLE_DATA) {
                pstatement.setString(1, value);
                pstatement.execute();
            }

            // Check data in our table (query)
            ResultSet rs = statement.executeQuery(SELECT_DATA_SQL);
            while (rs.next()) {
                log.info(rs.getInt(1) + " | " + rs.getString(2));
            }

            // correct shut down database
            statement.execute(SHUTDOWN_SQL);
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch(SQLException e) {
                    log.error("Can't free resources!", e);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("Can't free resources!", e);
                }
            }
        }

    }
}