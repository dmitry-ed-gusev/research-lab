package dgusev.apps.mass_email_sender.spammer.dataModel.dto;

import dgusev.apps.mass_email_sender.spammer.Defaults;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.HashSet;

/**
 * Класс-DTO (Data Transfer Object) для работы с данными одной рассылки (сущность "почтовая рассылка").
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 16.12.2010)
*/

public class DeliveryDTO
 {
  private int                       id         = -1;
  private String                    subject    = null;
  private String                    text       = null;
  private Defaults.DeliveryStatus status     = Defaults.DeliveryStatus.DELIVERY_STATUS_OK;
  private Defaults.DeliveryType type       = Defaults.DeliveryType.DELIVERY_TYPE_STANDARD;
  private String                    errorText  = null;
  private String                    initiator  = null;
  private String                    timestamp  = null;
  private HashSet<DeliveryFileDTO>  files      = null;
  private HashSet<RecipientTypeDTO> recipients = null;

  public int getId() {
   return id;
  }

  public void setId(int id) {
   this.id = id;
  }

  public String getSubject() {
   return subject;
  }

  public void setSubject(String subject) {
   this.subject = subject;
  }

  public String getText() {
   return text;
  }

  public void setText(String text) {
   this.text = text;
  }

  public String getErrorText() {
   return errorText;
  }

  public void setErrorText(String errorText) {
   this.errorText = errorText;
  }

  public String getInitiator() {
   return initiator;
  }

  public void setInitiator(String initiator) {
   this.initiator = initiator;
  }

  public String getTimestamp() {
   return timestamp;
  }

  public void setTimestamp(String timestamp) {
   this.timestamp = timestamp;
  }

  public HashSet<DeliveryFileDTO> getFiles() {
   return files;
  }

  /** Экземпляр класса пуст, если пусто одно из полей subject, text, initiator. */
  public boolean isEmpty()
   {return (StringUtils.isBlank(subject) || StringUtils.isBlank(text) || StringUtils.isBlank(initiator));}

  /**
   * Метод добавляет один файл к списку файлов рассылки.
   * @param file DeliveryFileDTO файл, добавляемый к списку файлов рассылки.
  */
  public void addFile(DeliveryFileDTO file)
   {
    if ((file != null) && (!file.isEmpty()))
     {
      if (files == null) {files = new HashSet<DeliveryFileDTO>();}
      files.add(file);
     }
   }

  /**
   * Метод добавляет один тип получателей к данной рассылке.
   * @param recipient RecipientTypeDTO тип получателей, добавляемый к данной рассылке.
  */
  public void addRecipient(RecipientTypeDTO recipient)
   {
    if ((recipient != null) && (!recipient.isEmpty()))
     {
      if (recipients == null) {recipients = new HashSet<RecipientTypeDTO>();}
      recipients.add(recipient);
     }
   }

  public Defaults.DeliveryStatus getStatus() {
   return status;
  }

  public void setStatus(Defaults.DeliveryStatus status) {
   this.status = status;
  }

  public Defaults.DeliveryType getType() {
   return type;
  }

  public void setType(Defaults.DeliveryType type) {
   this.type = type;
  }

  public HashSet<RecipientTypeDTO> getRecipients() {
   return recipients;
  }

  @Override
  public String toString()
   {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
            append("id", id).
            append("subject", subject).
            append("text", text).
            append("status", status).
            append("type", type).
            append("errorText", errorText).
            append("initiator", initiator).
            append("timestamp", timestamp).
            append("files", files).
            append("recipients", recipients).
            toString();
   }

 }