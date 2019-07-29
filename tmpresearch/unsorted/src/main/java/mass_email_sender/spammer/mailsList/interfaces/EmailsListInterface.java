package mass_email_sender.spammer.mailsList.interfaces;

import java.util.TreeMap;

/**
 * Интерфейс, который должны реализовывать модули получения email-адресов для рассылки почты. Ссылку
 * на реализацию данного интерфейса содержит класс конфигурирования модуля почтовой рассылки (класс
 * MailerConfig).
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 24.08.2010)
*/

public interface EmailsListInterface
 {
  public TreeMap<String, Integer> getEmailsList() throws IllegalAccessException, ClassNotFoundException, InstantiationException;
 }