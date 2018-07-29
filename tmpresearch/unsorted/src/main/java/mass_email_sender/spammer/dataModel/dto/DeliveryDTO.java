package spammer.dataModel.dto;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import spammer.Defaults.DeliveryStatus;
import spammer.Defaults.DeliveryType;

import java.util.HashSet;

/**
 *  ласс-DTO (Data Transfer Object) дл€ работы с данными одной рассылки (сущность "почтова€ рассылка").
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 16.12.2010)
*/

public class DeliveryDTO
 {
  private int                       id         = -1;
  private String                    subject    = null;
  private String                    text       = null;
  private DeliveryStatus            status     = DeliveryStatus.DELIVERY_STATUS_OK;
  private DeliveryType              type       = DeliveryType.DELIVERY_TYPE_STANDARD;
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

  /** Ёкземпл€р класса пуст, если пусто одно из полей subject, text, initiator. */
  public boolean isEmpty()
   {return (StringUtils.isBlank(subject) || StringUtils.isBlank(text) || StringUtils.isBlank(initiator));}

  /**
   * ћетод добавл€ет один файл к списку файлов рассылки.
   * @param file DeliveryFileDTO файл, добавл€емый к списку файлов рассылки.
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
   * ћетод добавл€ет один тип получателей к данной рассылке.
   * @param recipient RecipientTypeDTO тип получателей, добавл€емый к данной рассылке.
  */
  public void addRecipient(RecipientTypeDTO recipient)
   {
    if ((recipient != null) && (!recipient.isEmpty()))
     {
      if (recipients == null) {recipients = new HashSet<RecipientTypeDTO>();}
      recipients.add(recipient);
     }
   }

  public DeliveryStatus getStatus() {
   return status;
  }

  public void setStatus(DeliveryStatus status) {
   this.status = status;
  }

  public DeliveryType getType() {
   return type;
  }

  public void setType(DeliveryType type) {
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