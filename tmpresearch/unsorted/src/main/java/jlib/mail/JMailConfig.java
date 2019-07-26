package jlib.mail;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 09.12.2010)
 */

public class JMailConfig {

    private static final String JMAIL_ENCODING = "windows-1251";

    /**
     * Поле "кому" - список адресов/адрес. Обязательное поле.
     */
    private String to = null;
    /**
     * Поле для хранения имени майл-сервера. Формат имени: host[:port] Обязательное поле.
     */
    private String mailHost = null;
    /**
     * Поле для хранения порта мейл-сервера. Необязятельно.
     */
    private int mailPort = 0;
    /**
     * Кодировка для темы и текста письма. По умолчанию - windows-1251 (см. константу в модуле JLibConsts).
     */
    private String encoding = JMAIL_ENCODING;
    /**
     * Поле для хранения информации для поля FROM письма. Обязательное поле.
     */
    private String from = null;
    /**
     * Поле для хранения текста сообщения. Обязательное поле.
     */
    private String text = null;
    /**
     * Поле для хранения информации для поля SUBJECT письма.
     */
    private String subject = null;
    /**
     * Список прикрепленных к письму файлов (массив строк с именами - список).
     */
    private ArrayList<String> filesList = null;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public ArrayList<String> getFilesList() {
        return filesList;
    }

    public void setFilesList(ArrayList<String> filesList) {
        this.filesList = filesList;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Экземпляр класса считается пустым, если пусто одно из полей (или больше): to, mailHost, from, text.
     */
    public boolean isEmpty() {
        return (StringUtils.isBlank(to) || StringUtils.isBlank(mailHost) || StringUtils.isBlank(from) || StringUtils.isBlank(text));
    }

    public void addFile(String file) {
        if (!StringUtils.isBlank(file)) {
            if (filesList == null) {
                filesList = new ArrayList<String>();
            }
            filesList.add(file);
        }
    }

}