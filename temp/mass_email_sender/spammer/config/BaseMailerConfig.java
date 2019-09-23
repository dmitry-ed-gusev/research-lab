package dgusev.apps.mass_email_sender.spammer.config;

import dgusev.apps.mass_email_sender.spammer.Defaults;
import org.apache.commons.lang3.StringUtils;

/**
 * Базовый файл конфигурации системы рассылки. Не использовать отдельно! Для использования в
 * системе предусмотрнен класс-потомок.
 *
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 14.12.2010)
 */

public class BaseMailerConfig {
    /**
     * Хост почтового сервера.
     */
    private String mailHost = Defaults.MAIL_HOST;
    /**
     * Порт почтового сервера. По умолчанию указано значение параметра = 0, т.е. порт не указан.
     * Модуль отправки почты использует в таком случае порт по умолчанию = порт 25.
     */
    private int mailPort = 0;
    /**
     * Обратный адрес для системы рассылки.
     */
    private String mailFrom = Defaults.MAIL_FROM;
    /**
     * Адрес(а) для тестовой отправки рассылки. Остальные списки адресов игнорируются (если параметр не пуст).
     */
    private String testMailTo = null;
    /**
     * Путь к БД Флота. База в формате DBF.
     */
    private String fleetDBPath = Defaults.DB_FLEET_PATH;
    /**
     * Путь к БД Фирм. База в формате DBF.
     */
    private String firmDBPath = Defaults.DB_FIRM_PATH;
    /**
     * Идентификатор осуществляемой рассылки (из БД рассылок).
     */
    private int deliveryId = 0;
    /**
     * Путь к файлам, приаттаченным к рассылкам (путь к хранилищу).
     */
    private String deliveriesFilesPath = null;
    /**
     * Включен или выключен демо-режим. В демо-режиме письма не отправляются!
     */
    private boolean isDemoMode = false;
    /**
     * Кодировка темы и текста электронного письма.
     */
    private String mailEncoding = null;

    public String getMailHost() {
        return mailHost;
    }

    public void setMailHost(String mailHost) {
        this.mailHost = mailHost;
    }

    public int getMailPort() {
        return mailPort;
    }

    public void setMailPort(int mailPort) {
        this.mailPort = mailPort;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getFleetDBPath() {
        return fleetDBPath;
    }

    public void setFleetDBPath(String fleetDBPath) {
        this.fleetDBPath = fleetDBPath;
    }

    public String getFirmDBPath() {
        return firmDBPath;
    }

    public void setFirmDBPath(String firmDBPath) {
        this.firmDBPath = firmDBPath;
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getDeliveriesFilesPath() {
        return deliveriesFilesPath;
    }

    public void setDeliveriesFilesPath(String deliveriesFilesPath) {
        this.deliveriesFilesPath = deliveriesFilesPath;
    }

    public boolean isDemoMode() {
        return isDemoMode;
    }

    public void setDemoMode(boolean demoMode) {
        isDemoMode = demoMode;
    }

    public String getTestMailTo() {
        return testMailTo;
    }

    public void setTestMailTo(String testMailTo) {
        this.testMailTo = testMailTo;
    }

    public String getMailEncoding() {
        return mailEncoding;
    }

    public void setMailEncoding(String mailEncoding) {
        this.mailEncoding = mailEncoding;
    }

    public String getConfigErrors() {
        String result = null;
        // Проеверяем хост для отправки почты
        if (StringUtils.isBlank(this.mailHost)) {
            result = "Empty mail host parameter!";
        }
        // Проверяем обратный адрес
        else if (StringUtils.isBlank(this.mailFrom)) {
            result = "Empty mail from parameter!";
        }
        // Проверяем идентификатор рассылки - он должен быть больше нуля
        else if (this.deliveryId <= 0) {
            result = "Invalid delivery ID [" + deliveryId + "]!";
        }
        // Возвращаем результат
        return result;
    }

}