package jdb.processing;

import dgusev.dbpilot.config.DBConfig;
import jdb.exceptions.DBModuleConfigException;

/**
 * Данный класс - родительский для всех классов движка анализа и обработки СУБД. Он реализует хранение и
 * доступ к конфигурации соединения с СУБД, а также списки "разрешенных" и "запрещенных" таблиц для модулей
 * обработки.
 *
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 27.04.2010)
 */

public class DBCommonProcessor {
    /**
     * Текущая конфигурация данного модуля.
     */
    private DBConfig config = null;

    /**
     * Конструктор по умолчанию. Инициализирует поле config данного класса.
     *
     * @param config DBConfig конфигурация для соединения с СУБД.
     * @throws DBModuleConfigException ИС возникает, если конструктору передана пустая конфигурация.
     */
    public DBCommonProcessor(DBConfig config) throws DBModuleConfigException {

        // Инициализация конфигурации модуля (соединение с СУБД). Если конфиг пуст - ошибка.
        //String configErrors = DBUtils.getConfigErrors(config);
        //if (!StringUtils.isBlank(configErrors)) {
        //    throw new DBModuleConfigException(configErrors);
        //} else {
        //    this.config = config;
        //}

        this.config = config;
    }

    public DBConfig getConfig() {
        return config;
    }

    public void setConfig(DBConfig config) {
        this.config = config;
    }

}