package gusevdm.datatexdb;

import gusevdm.config.Environment;
import gusevdm.luxms.model.LuxModel;
import gusevdm.luxms.model.elements.LuxDataPoint;
import gusevdm.luxms.model.elements.LuxLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/** DataTex DB client. */
public class DataTexDBClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataTexDBClient.class);

    // environment link
    private final Environment env;
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
        this.env      = env;
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
        // obtain connection
        Connection connection = DriverManager.getConnection(url, this.dbUser, this.dbPass);
        // switch to necessary db schema
        connection.createStatement().executeUpdate(String.format("alter session set current_schema = %s", this.dbSchema));

        return connection;
    }

    /***/
    public String getTablesList() throws SQLException {
        LOGGER.debug("DataTexClient.getTablesList() is working.");

        String query = String.format("SELECT DISTINCT OWNER, OBJECT_NAME FROM ALL_OBJECTS " +
                "WHERE OBJECT_TYPE = 'TABLE' AND OWNER = '%s' ORDER BY OBJECT_NAME", this.dbSchema);
        LOGGER.debug(String.format("Generated SQL query [%s].", query));
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
                builder.append(rs.getString(2)).append("\n");
            } while(rs.next());
            tablesList = builder.toString();

        } else { // no data
            LOGGER.warn(String.format("No data found for query [%s]!", query));
        } // END if

        return tablesList;
    }

    /***/
    private static String loadSqlFromFile(String sqlFilePath) throws IOException {
        LOGGER.debug(String.format("DataTexDBClient.loadSqlFromFile() is working. Sql file: [%s].", sqlFilePath));
        StringBuilder sqlBuider = new StringBuilder();
        // try-with-resources
        try (BufferedReader sqlReader = new BufferedReader(new FileReader(new File(sqlFilePath)))) {
            String tmpStr;
            while ((tmpStr = sqlReader.readLine()) != null) {
                sqlBuider.append(tmpStr).append("\n");
            }
        }
        // return loaded SQL query
        return sqlBuider.toString();
    }

    /***/
    public LuxModel loadLuxModelData(LuxModel luxModel) throws IOException, SQLException, ParseException {
        LOGGER.debug("DataTexClient.loadLuxModelData() is working.");

        // load sql from file provided
        String sql = DataTexDBClient.loadSqlFromFile(this.env.getReportsDir() + "/" + luxModel.getSqlFile());
        //LOGGER.debug(String.format("Loaded sql query:\n%s", sql)); // <- too much output

        // connect to DBMS
        Connection conn = this.connect();
        // statement and result set (execute query)
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        // parse returned result set
        if (rs.next()) { // some data exists
            LOGGER.debug("Data found, parsing...");
            Map<Long, LuxLocation>  locations  = new HashMap<>();
            Map<Long, LuxDataPoint> dataPoints = new HashMap<>();
            LuxLocation  location;
            LuxDataPoint dataPoint;

            String locationTitleColumn   = luxModel.getLocationsTitlesCols()[0];
            String dataPointValuesColumn = luxModel.getDataValuesCols()[0];
            int    dataPointMetricId     = luxModel.getDataValuesMetricsIds()[0];
            long   locationId;
            long   dataPointId;
            String locationTitleValue;
            int sortOrder = 1;
            do {
                locationTitleValue = rs.getString(locationTitleColumn);
                // id -> hash code from title value
                locationId         = locationTitleValue.hashCode();

                // building LuxMS model: where? -> locations
                location = new LuxLocation(locationId, locationTitleValue, 0, -1,
                        false, new BigDecimal(0), new BigDecimal(0), sortOrder);
                LOGGER.debug(String.format("Loaded location: [%s].", location));
                locations.put(locationId, location);

                // building LuxMS model: data point
                BigDecimal value = rs.getBigDecimal(dataPointValuesColumn);
                // load and generate id for period
                int startMonth = rs.getInt("VALUE_MONTH");
                int startYear  = rs.getInt("VALUE_YEAR");
                long periodId = Long.parseLong(String.valueOf(startMonth) + String.valueOf(startYear));

                // create data point. id -> hash code of all params combination
                dataPointId = (String.valueOf(dataPointMetricId) + String.valueOf(locationId) +
                        String.valueOf(periodId) + String.valueOf(value)).hashCode();
                dataPoint = new LuxDataPoint(dataPointId, dataPointMetricId, locationId, periodId, value);
                LOGGER.debug(String.format("Loaded data point: [%s].", dataPoint));
                dataPoints.put(dataPointId, dataPoint);

                sortOrder++; // increment sort order value

            } while(rs.next());
            LOGGER.debug("Data parsed. All OK.");
            luxModel.setLocations(locations);
            luxModel.setDataPoints(dataPoints);

        } else { // no data
            LOGGER.warn("No data found for this query!");
        } // END if

        return luxModel;
    }

}
