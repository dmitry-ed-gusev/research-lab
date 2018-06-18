package gusevdm.datatexdb;

import gusevdm.Environment;
import gusevdm.luxms.model.LuxModel;
import gusevdm.luxms.model.elements.*;
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

import static gusevdm.luxms.LuxDefaults.LUX_DATE_FORMAT;

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
                "WHERE OBJECT_TYPE = 'TABLE' AND OWNER = '%s' ORDER BY OBJECT_NAME", this.dbSchema, this.dbSchema);
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
    public LuxModel getLuxModelForReport1() throws IOException, SQLException, ParseException {
        LOGGER.debug("!!!");

        // resulting model
        LuxModel luxModel = new LuxModel();

        StringBuilder sqlBuider = new StringBuilder();
        // read sql query from external file
        try (BufferedReader sqlReader = new BufferedReader(new FileReader(new File("sql/report1.sql")))) {
            String tmpStr;
            while ((tmpStr = sqlReader.readLine()) != null) {
                sqlBuider.append(tmpStr).append("\n");
            }
        }

        //LOGGER.debug(String.format("Loaded sql query:\n%s", sqlBuider.toString()));

        long baseId = 1;

        // building LuxMS model:           "Единицы измерения" (units)
        LuxUnit unit = new LuxUnit(baseId, "цикл/дней", "ц/дн", "цикл/дней", "", "");
        Map<Long, LuxUnit> units = new HashMap<Long, LuxUnit>() {{
            put(1L, unit);}
        };
        luxModel.setUnits(units);

        // building LuxMS model: what?  -> "Средний цикл производства" (metrics)
        LuxMetric metric = new LuxMetric(baseId, "Средний цикл производства по номенклатуре",
                0, 0, false, baseId, 100);
        Map<Long, LuxMetric> metrics = new HashMap<Long, LuxMetric>() {{
            put(1L, metric);
        }};
        luxModel.setMetrics(metrics);

        // building LuxMS model: when?  -> "По месяцам" (periods)
        // todo: periods generator -> implement
        /*
        LuxPeriod period1  = new LuxPeriod(1,  "янв 2017", LUX_DATE_FORMAT.parse("2017-01-01"), LuxPeriodType.MONTH);
        LuxPeriod period2  = new LuxPeriod(2,  "фев 2017", LUX_DATE_FORMAT.parse("2017-02-01"), LuxPeriodType.MONTH);
        LuxPeriod period3  = new LuxPeriod(3,  "мар 2017", LUX_DATE_FORMAT.parse("2017-03-01"), LuxPeriodType.MONTH);
        LuxPeriod period4  = new LuxPeriod(4,  "апр 2017", LUX_DATE_FORMAT.parse("2017-04-01"), LuxPeriodType.MONTH);
        LuxPeriod period5  = new LuxPeriod(5,  "май 2017", LUX_DATE_FORMAT.parse("2017-05-01"), LuxPeriodType.MONTH);
        LuxPeriod period6  = new LuxPeriod(6,  "июн 2017", LUX_DATE_FORMAT.parse("2017-06-01"), LuxPeriodType.MONTH);
        LuxPeriod period7  = new LuxPeriod(7,  "июл 2017", LUX_DATE_FORMAT.parse("2017-07-01"), LuxPeriodType.MONTH);
        LuxPeriod period8  = new LuxPeriod(8,  "авг 2017", LUX_DATE_FORMAT.parse("2017-08-01"), LuxPeriodType.MONTH);
        LuxPeriod period9  = new LuxPeriod(9,  "сен 2017", LUX_DATE_FORMAT.parse("2017-09-01"), LuxPeriodType.MONTH);
        LuxPeriod period10 = new LuxPeriod(10, "окт 2017", LUX_DATE_FORMAT.parse("2017-10-01"), LuxPeriodType.MONTH);
        LuxPeriod period11 = new LuxPeriod(11, "ноя 2017", LUX_DATE_FORMAT.parse("2017-11-01"), LuxPeriodType.MONTH);
        LuxPeriod period12 = new LuxPeriod(12, "дек 2017", LUX_DATE_FORMAT.parse("2017-12-01"), LuxPeriodType.MONTH);
        Map<Long, LuxPeriod> periods = new HashMap<Long, LuxPeriod>() {{
            put(1L, period1); put(2L, period2);   put(3L, period3);   put(4L, period4);
            put(5L, period5); put(6L, period6);   put(7L, period7);   put(8L, period8);
            put(9L, period9); put(10L, period10); put(11L, period11); put(12L, period12);
        }};
        */
        //luxModel.setPeriods(periods);
        luxModel.setPeriods(LuxPeriod.generateMonthsPeriods("2014", "2015", "2016", "2017", "2018", "2019"));

        // connect to DBMS
        Connection conn = this.connect();
        // statement and result set (execute query)
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sqlBuider.toString());

        // parse returned result set
        if (rs.next()) { // some data exists
            LOGGER.debug("Data found, parsing...");
            Map<Long, LuxLocation>  locations  = new HashMap<>();
            Map<Long, LuxDataPoint> dataPoints = new HashMap<>();
            LuxLocation  location;
            LuxDataPoint dataPoint;
            do {
                // building LuxMS model: where? -> "Номенклатура производства" (locations)
                location = new LuxLocation(baseId, rs.getString("ITEMCODE"), 0, -1,
                        false, new BigDecimal(0), new BigDecimal(0), (int) baseId * 100);
                LOGGER.debug(String.format("Loaded location: [%s].", location));
                locations.put(baseId, location);

                // building LuxMS model:           "Данные по среднему циклу производства"
                BigDecimal value = rs.getBigDecimal("ORDER_DURATION_DAYS");
                int startMonth = rs.getInt("ORDER_START_MONTH");
                int startYear  = rs.getInt("ORDER_START_YEAR");
                // generate id for period
                long periodId = Long.parseLong(String.valueOf(startMonth) + String.valueOf(startYear));

                // create data point itself
                dataPoint = new LuxDataPoint(baseId, 1, baseId, periodId, value);
                LOGGER.debug(String.format("Loaded data point: [%s].", dataPoint));
                dataPoints.put(baseId, dataPoint);

                // base ID increment
                baseId++;

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
