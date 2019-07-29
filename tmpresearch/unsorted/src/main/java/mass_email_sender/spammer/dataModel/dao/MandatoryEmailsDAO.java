package mass_email_sender.spammer.dataModel.dao;

import jdb.exceptions.DBConnectionException;
import jdb.model.applied.dao.DBConfigCommonDAO;
import jdb.processing.sql.execution.SqlExecutor;
import mass_email_sender.spammer.Defaults;
import mass_email_sender.spammer.mailsList.interfaces.EmailsListInterface;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;

/**
 * DAO-компонент для работы с таблицей mandatoryEmails (обязательные майл-адреса).
 *
 * @author Gusev Dmitry (Дмитрий)
 * @version 1.0 (DATE: 02.04.11)
 */

public class MandatoryEmailsDAO extends DBConfigCommonDAO implements EmailsListInterface {
    /**
     * Логгер класса.
     */
    private Logger logger = Logger.getLogger(Defaults.LOGGER_NAME);

    /**
     * Конструктор. Поля инициализируются значениями по умолчанию.
     */
    public MandatoryEmailsDAO() throws ConfigurationException {
        super(Defaults.LOGGER_NAME, Defaults.DBCONFIG_FILE);
    }

    public TreeMap<String, Integer> getEmailsList() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        logger.debug("MandatoryEmailsDAO: getEmailsList().");
        Connection conn = null;
        ResultSet rs = null;
        TreeMap<String, Integer> list = null;
        String sql = "select id, email from dbo.mandatoryEmails where deleted = 0";
        try {
            conn = this.getConnection();
            rs = SqlExecutor.executeSelectQuery(conn, sql);
            // Что-то нашли
            if (rs.next()) {
                logger.debug("Result set is not empty! Processing.");
                list = new TreeMap<String, Integer>();
                String email;
                Integer id;
                do {
                    email = rs.getString("email");
                    id = rs.getInt("id");
                    if (!StringUtils.isBlank(email)) {
                        list.put(email, id);
                    }
                }
                while (rs.next());
            }
            // Ничо не нашли
            else {
                logger.warn("Result set is empty! Deliveries not found!");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        } catch (DBConnectionException e) {
            logger.error(e.getMessage());
        }
        // Освобождаем ресурсы
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.error("Can't free resources! Reason: " + e.getMessage());
            }
        }
        return list;
    }

}