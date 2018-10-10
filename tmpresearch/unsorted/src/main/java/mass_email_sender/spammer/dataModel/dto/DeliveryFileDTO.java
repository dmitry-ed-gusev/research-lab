package mass_email_sender.spammer.dataModel.dto;

import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

/**
 * Класс-DTO (Data Transfer Object) для работы с данными об одном прикрепленном к рассылке файле.
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 17.12.2010)
*/

public class DeliveryFileDTO
 {
  private int    id         = -1;
  private int    deliveryId = 0;
  private String fileName   = null;

  public DeliveryFileDTO(int id, int deliveryId, String fileName)
   {
    this.id         = id;
    this.deliveryId = deliveryId;
    this.fileName   = fileName;
   }

  public DeliveryFileDTO(String fileName)
   {
    this.fileName = fileName;
   }

  public int getId() {
   return id;
  }

  public void setId(int id) {
   this.id = id;
  }

  public int getDeliveryId() {
   return deliveryId;
  }

  public void setDeliveryId(int deliveryId) {
   this.deliveryId = deliveryId;
  }

  public String getFileName() {
   return fileName;
  }

  public void setFileName(String fileName) {
   this.fileName = fileName;
  }


  @Override
  @SuppressWarnings({"QuestionableName", "ParameterNameDiffersFromOverriddenParameter",
                     "RedundantIfStatement", "MethodWithMultipleReturnPoints"})
  public boolean equals(Object o)
   {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DeliveryFileDTO that = (DeliveryFileDTO) o;
    if (deliveryId != that.deliveryId) return false;
    if (fileName != null ? !fileName.equals(that.fileName) : that.fileName != null) return false;
    return true;
   }

  @Override
  public int hashCode()
   {
    int result = deliveryId;
    result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
    return result;
   }

  @Override
  public String toString()
   {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
            append("id", id).
            append("deliveryId", deliveryId).
            append("fileName", fileName).
            toString();
   }

  /**
   * Экземпляр класса считается пустым, если пусто поле fileName.
   * @return boolean ИСТИНА/ЛОЖЬ в зависимости от того, пуст или нет данный экземпляр класса.
  */
  public boolean isEmpty() {return (StringUtils.isBlank(fileName));}

  /**
   * Метод только для тестирования класса!
   * @param args String[] параметры метода.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger(DeliveryFileDTO.class.getName());
    Logger logger = Logger.getLogger(DeliveryFileDTO.class.getName());

    DeliveryFileDTO file1 = new DeliveryFileDTO(1, 1, null);
    DeliveryFileDTO file2 = new DeliveryFileDTO(2, 1, null);

    logger.info("1->2 " + file1.equals(file2));
    logger.info("2->1 " + file2.equals(file1));
   }

 }