package mass_email_sender.spammer.dataModel.dto;

import org.apache.commons.lang.StringUtils;

/**
 * @author Gusev Dmitry (019gus)
 * @version 1.1 (DATE: 19.01.2011)
*/

public class EmailDTO
 {
  private int    id         = -1;
  private String email      = null;
  private int    companyId  = 0;
  private int    deliveryId = 0;
  private int    status     = 0;
  private String errorText  = null;
  private int    isArchive  = 0;
  private String timestamp  = null;

  public int getId() {
   return id;
  }

  public void setId(int id) {
   this.id = id;
  }

  public String getEmail() {
   return email;
  }

  public void setEmail(String email) {
   this.email = email;
  }

  public int getCompanyId() {
   return companyId;
  }

  public void setCompanyId(int companyId) {
   this.companyId = companyId;
  }

  public int getDeliveryId() {
   return deliveryId;
  }

  public void setDeliveryId(int deliveryId) {
   this.deliveryId = deliveryId;
  }

  public int getStatus() {
   return status;
  }

  public void setStatus(int status) {
   this.status = status;
  }

  public String getErrorText() {
   return errorText;
  }

  public void setErrorText(String errorText) {
   this.errorText = errorText;
  }

  public int getArchive() {
   return isArchive;
  }

  public void setArchive(int archive) {
   isArchive = archive;
  }

  public String getTimestamp() {
   return timestamp;
  }

  public void setTimestamp(String timestamp) {
   this.timestamp = timestamp;
  }

  /**
   * Класс пуст если пусто одно из полей email, deliveryId.
   * @return boolean ИСТИНА/ЛОЖЬ в зависимости от того, пуст или нет экземпляр данного класса.
  */
  public boolean isEmpty() {return (StringUtils.isBlank(email) || (deliveryId <= 0));}

 }