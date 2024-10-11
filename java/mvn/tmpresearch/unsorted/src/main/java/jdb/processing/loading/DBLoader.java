package jdb.processing.loading;

import dgusev.dbpilot.config.DBConfig;
import dgusev.dbpilot.config.DBType;
import jdb.config.load.DBLoaderConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.monitoring.DBProcessingMonitor;
import jdb.monitoring.DBTestMonitor;
import jdb.processing.loading.core.DBLoadCore;
import jdb.processing.loading.core.DBUnloadCore;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Данный класс реализует методы выгрузки БД на диск и загрузки БД с диска. Класс является "фасадом" (реализация
 * шаблона "фасад") для методов загрузки/выгрузки. Загрузку/выгрузку БД рекомендуется осуществлять именно с помощью
 * данного класса, а не напрямую с помощью классов DBUnloadCore/DBLoadCore.
 *
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 20.05.2010)
 * @deprecated данный класс не рекомендуется использовать, т.к. для загрузки/выгрузки БД на диск используется класс
 * {@link jdb.nextGen.DBasesLoader DBasesLoader} вместо данного.
 */

public class DBLoader {
    /**
     * Компонент-логгер данного класса.
     */
    private static Logger logger = Logger.getLogger(DBLoader.class.getName());

    /**
     * Метод загружает с диска сериализованную БД в текущую БД (на которую указывает класс конфигурации соединения с СУБД).
     * Имя сериализованной БД (на диске) должно совпадать с именем текущей БД (на которую указывает класс конфигурации
     * подключения к СУБД).
     *
     * @param config DBLoaderConfig конфигурация для выполнения десериализации. Содержит все необходимые данные.
     * @return ArrayList[String] список возникших при работе модуля ИС. В данном списке возвращаются НЕ КРИТИЧЕСКИЕ
     * ИС - те, которые не приводят к останову модуля.
     * @throws java.sql.SQLException                  ИС при выполнении анализа таблицы БД.
     * @throws java.io.IOException                    ошибки ввода/вывода при работе с файловой системой.
     * @throws jdb.exceptions.DBModuleConfigException ошибки конфигурирования соединения с СУБД.
     * @throws jdb.exceptions.DBConnectionException   ошибки соединения с СУБД.
     * @throws jdb.exceptions.DBModelException        ошибки модели базы данных.
     */
    public static ArrayList<String> loadFromDisk(DBLoaderConfig config)
            throws DBConnectionException, DBModuleConfigException, IOException, DBModelException, SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        ArrayList<String> errorsList = null;
        // Проверяем параметры, если они корректны - работаем
        //String configErrors = DBUtils.getConfigErrors(config);
        //if (StringUtils.isBlank(configErrors)) {errorsList = DBLoadCore.load(config);}
        // Если входные параметры неверны - работа метода прекращается
        //else {logger.error(configErrors);}
        errorsList = DBLoadCore.load(config);
        return errorsList;
    }

    /**
     * Метод осуществляет выгрузку на диск БД, согласно указанной конфигурации DBLoaderConfig.
     *
     * @param config DBLoaderConfig конфигурация, согласно которой на диск будет выгружена БД.
     * @throws SQLException            ИС при выполнении анализа таблицы БД.
     * @throws IOException             ошибки ввода/вывода при работе с файловой системой.
     * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
     * @throws DBConnectionException   ошибки соединения с СУБД.
     * @throws DBModelException        ошибки модели базы данных.
     */
    public static void unloadToDisk(DBLoaderConfig config)
            throws DBConnectionException, DBModuleConfigException, IOException, DBModelException, SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        // Проверяем параметры, если они корректны - работаем
        //String configErrors = DBUtils.getConfigErrors(config);
        //if (StringUtils.isBlank(configErrors)) {DBUnloadCore.unload(config);}
        // Если входные параметры неверны - работа метода прекращается
        //else {logger.error(configErrors);}
        DBUnloadCore.unload(config);
    }

    /**
     * Метод только для тестирования данного класса.
     *
     * @param args String[] параметры данного метода.
     */
    public static void main(String[] args) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Logger logger = Logger.getLogger("jdb");

        // Тестовый монитор
        DBProcessingMonitor monitor = new DBTestMonitor();

        //DBConfig mysqlConfig1 = new DBConfig();
        //mysqlConfig1.setDbType(DBConsts.DBType.MYSQL);
        //mysqlConfig1.setHost("localhost:3306");
        //mysqlConfig1.setDbName("storm");
        //mysqlConfig1.setUser("root");
        //mysqlConfig1.setPassword("mysql");

        DBConfig ifxConfig = new DBConfig();
        ifxConfig.setDbType(DBType.INFORMIX);
        ifxConfig.setServerName("hercules");
        ifxConfig.setHost("appserver:1526");
        ifxConfig.setDbName("norm_docs");
        ifxConfig.setUser("informix");
        //ifxConfig.setPassword("ifx_dba_019");

        DBConfig mssqlConfig = new DBConfig();
        mssqlConfig.setDbType(DBType.MSSQL_JTDS);
        mssqlConfig.setHost("APP");
        mssqlConfig.setDbName("norm_docs");
        mssqlConfig.setUser("sa");
        //mssqlConfig.setPassword("adminsql245#I");

        try {
            DBLoaderConfig ifxLoaderConfig = new DBLoaderConfig();
            ifxLoaderConfig.setDbConfig(ifxConfig);
            ifxLoaderConfig.setPath("c:\\temp\\norm_docs");
            ifxLoaderConfig.setOperationsCount(100);
            ifxLoaderConfig.setMonitor(monitor);
            // Непосредственно выгрузка БД СУПИДа на диск (из Информикса)
            DBLoader.unloadToDisk(ifxLoaderConfig);

            DBLoaderConfig mssqlLoaderConfig = new DBLoaderConfig();
            mssqlLoaderConfig.setDbConfig(mssqlConfig);
            mssqlLoaderConfig.setPath("c:\\temp\\mssql");
            mssqlLoaderConfig.setClearTableBeforeLoad(true);
            //mssqlLoaderConfig.
            //DBLoader.loadFromDisk(mssqlLoaderConfig);

        } catch (DBConnectionException e) {
            logger.error("[" + e.getClass().getName() + "] " + e.getMessage());
        } catch (DBModuleConfigException e) {
            logger.error("[" + e.getClass().getName() + "] " + e.getMessage());
        } catch (IOException e) {
            logger.error("[" + e.getClass().getName() + "] " + e.getMessage());
        } catch (DBModelException e) {
            logger.error("[" + e.getClass().getName() + "] " + e.getMessage());
        } catch (SQLException e) {
            logger.error("[" + e.getClass().getName() + "] " + e.getMessage());
        }

    }

}