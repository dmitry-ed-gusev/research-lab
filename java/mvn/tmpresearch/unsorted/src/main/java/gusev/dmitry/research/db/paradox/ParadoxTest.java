package gusev.dmitry.research.db.paradox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 19.04.13)
 */

public class ParadoxTest {
    private static Log log = LogFactory.getLog(ParadoxTest.class);

    public static void main(String[] args) {
        log.info("Starting paradox test.");
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver").newInstance();
            String workingDb = "c:/temp/d0009002.db";
            //String dbUrl = "jdbc:odbc:Driver={Microsoft Paradox Driver (*.db )};DriverID=538;DBQ=c:\\temp\\D0009002.db";
            String dbUrl = "jdbc:odbc:zzzz";

            //String dbUrl = "jdbc:odbc:Driver={Microsoft Paradox Driver (*.db )};DriverID=538;Fil=Paradox 5.X;DefaultDir=c:\\temp\\;Dbq=c:\\temp\\D0009002.db;CollatingSequence=ASCII;";
            //Properties prop = new Properties();
            //prop.put("charSet", "UTF-8");
            //prop.put("user", username);
            //prop.put("password", password);


            Connection connection = DriverManager.getConnection(dbUrl/*, prop*/);
            log.info("Connected.");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage());
        } catch (InstantiationException e) {
            log.error(e.getMessage());
        } catch (IllegalAccessException e) {
            log.error(e.getMessage());
        }
    }

}