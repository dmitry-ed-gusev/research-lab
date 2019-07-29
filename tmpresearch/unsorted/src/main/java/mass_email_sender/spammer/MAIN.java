package mass_email_sender.spammer;

import dgusev.dbpilot.config.DBConfig;
import dgusev.dbpilot.utils.DBUtilities;
import jdb.exceptions.DBConnectionException;
import mass_email_sender.spammer.config.MailerConfig;
import mass_email_sender.spammer.mailsProcessor.Mailer;
import org.apache.commons.cli.HelpFormatter;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Главный запускаемый модуль данного приложения. Для построения запускаемого jar-ника следует использовать данный
 * класс в качестве MAIN-класса. Класс может обрабатывать параметры командной строки, для описания опций следует
 * запустить данный класс (или запускаемый jar-архив с ключом -help (см. модуль констант Defaults).
 *
 * @author Gusev Dmitry (Дмитрий)
 * @version 2.0 (DATE: 17.12.2010)
 */

// todo: Проблема с кодировкой имени приаттаченного к письму файла - русское имя выводится кракозябрами :)

public class MAIN {

    public static void main(String[] args) throws org.apache.commons.configuration2.ex.ConfigurationException, IllegalAccessException, InstantiationException, ClassNotFoundException {

        // Берем логгер приложения
        Logger logger = Logger.getLogger(Defaults.LOGGER_NAME);
        logger.info("Starting project. Checking DBMS connection. Config [" + Defaults.DBCONFIG_FILE + "].");

        // Получение и разбор параметров командной строки. Инициализация конфига приложения.
        MailerConfig mailerConfig = new MailerConfig(args);
        logger.info("Mailer module config initialized OK.");

        // Если указана опция показать версию - покажем
        if (mailerConfig.isShowVersion()) {
            logger.info(Defaults.SYSTEM_VERSION);
        }
        // Если же указана опция показать хелп - тоже покажем
        else if (mailerConfig.isShowHelp()) {
            logger.info("Showing help screen...");
            new HelpFormatter().printHelp("java -jar MassEmailsSender.jar", null,
                    mailerConfig.getCmdLineOptions(), Defaults.SYSTEM_VERSION, true);
        }
        // Если не указаны опции показа хелпа или версии - работаем по обычной схеме.
        else {
            // Перед запуском проверим соединение с СУБД. Если оно есть - все ок!
            // Также необходимо проверить параметры командной строки.
            Connection connection = null;
            try {
                // Загрузка конфига из файла
                DBConfig dbConfig = new DBConfig(Defaults.DBCONFIG_FILE);
                // Проверка соединения с СУБД
                connection = DBUtilities.getDBConn(dbConfig);
                // Если соединение не удалось получить - ошибка. Дальше не работаем.
                if (connection == null) {
                    throw new DBConnectionException("Can't connect to DBMS! Config file [" + Defaults.DBCONFIG_FILE + "].");
                }
                // Если соединение получено - закрываем его
                else {
                    connection.close();
                }
                // Соединение получено - продолжаем
                logger.info("DBMS connection OK (dbConfig [" + Defaults.DBCONFIG_FILE + "]). Processing.");

                // После выполнения всех проверок непосредственно выполняем рассылку. Выбор интерфейса для формирования списка
                // адресов осуществляется уже в модуле почтовика (Mailer).
                Mailer mailer = new Mailer(mailerConfig);
                mailer.startSpam();
            }
            // Перехват ИС
            catch (IOException e) {
                logger.error(e.getMessage());
            } catch (DBConnectionException e) {
                logger.error(e.getMessage());
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
            // Освобождение ресурсов
            finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
        }

        /**
         // Рассылка - заполняем параметрами
         DeliveryDTO delivery = new DeliveryDTO();
         delivery.setSubject("Тестовая рассылка. Модуль автоматической рассылки.");
         delivery.setText("Тестовое письмо. Тест почтовой рассылки. Отдел 019, ГУР.");
         delivery.setInitiator("019gus");
         delivery.addFile(new DeliveryFileDTO("2-040202-020_1.pdf"));
         delivery.addFile(new DeliveryFileDTO("2-040202-020_2.pdf"));
         delivery.addFile(new DeliveryFileDTO("2-040202-020_3.pdf"));
         // Пробиваем в БД данные
         new DeliveriesDAO().change(delivery);
         */

    }

}