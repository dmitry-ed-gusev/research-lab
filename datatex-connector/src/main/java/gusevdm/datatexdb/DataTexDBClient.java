package gusevdm.datatexdb;

import gusevdm.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/** DataTex DB client. */
public class DataTexDBClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataTexDBClient.class);

    // internal state
    private final String dbHost;
    private final int    dbPort;
    private final String dbUser;
    private final String dbPass;
    private final String dbSchema;
    private final String dbSID;

    /** Constructor. Init internal state from Environment. */
    public DataTexDBClient() {
        LOGGER.debug("DataTexDBClient constructor() is working.");
        Environment env = Environment.getInstance();
        this.dbHost   = env.getDataTexHost();
        this.dbPort   = Integer.parseInt(env.getDataTexPort());
        this.dbUser   = env.getDataTexUser();
        this.dbPass   = env.getDataTexPass();
        this.dbSchema = env.getDataTexSchema();
        this.dbSID    = env.getDataTexSID();
    }

    /***/
    private Connection connect() throws SQLException {
        LOGGER.debug("DataTexClient.connect() is working.");
        // create connection URL
        String url = String.format("jdbc:oracle:thin:@%s:%s:%s", this.dbHost, this.dbPort, this.dbSID);
        return DriverManager.getConnection(url, this.dbUser, this.dbPass);
    }

    /***/
    public String getTablesList() throws SQLException {
        LOGGER.debug("DataTexClient.getTablesList() is working.");

        String query = "";

        // connect to DBMS
        Connection conn = this.connect();
        // statement and result set (execute query)
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        String tablesList = null;
        // parse returned result set
        if (rs.next()) { // some data exists
            LOGGER.debug("Data found, parsing...");
            StringBuilder builder = new StringBuilder();
            do {
                builder.append(rs.getString(1)).append("%n");
            } while(rs.next());
            tablesList = builder.toString();

        } else { // no data
            LOGGER.warn(String.format("No data found for query [%s]!", query));
        } // END if

        return tablesList;
    }

}
