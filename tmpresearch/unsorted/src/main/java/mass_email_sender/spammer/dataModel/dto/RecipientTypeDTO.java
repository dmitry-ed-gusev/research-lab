package spammer.dataModel.dto;

import jlib.logging.InitLogger;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;
import spammer.Defaults;

/**
 *  ласс-DTO (Data Transfer Object) дл€ работы с данными об одном типе получателей рассылки.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 17.12.10)
*/

public class RecipientTypeDTO
 {
  private int                    id            = -1;
  private int                    deliveryId    = 0;
  private Defaults.RecipientType recipientType = null;

  public RecipientTypeDTO(int id, int deliveryId, Defaults.RecipientType recipientType)
   {
    this.id            = id;
    this.deliveryId    = deliveryId;
    this.recipientType = recipientType;
   }

  public RecipientTypeDTO(int id, int deliveryId, int recipientType)
   {
    this.id            = id;
    this.deliveryId    = deliveryId;
    this.recipientType = Defaults.RecipientType.findByIntValue(recipientType);
   }

  public RecipientTypeDTO(Defaults.RecipientType recipientType)
   {this.recipientType = recipientType;}

  public RecipientTypeDTO(int recipientType)
   {this.recipientType = Defaults.RecipientType.findByIntValue(recipientType);}

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

  public Defaults.RecipientType getRecipientType() {
   return recipientType;
  }

  public void setRecipientType(Defaults.RecipientType recipientType) {
   this.recipientType = recipientType;
  }

  @Override
  @SuppressWarnings({"QuestionableName", "ParameterNameDiffersFromOverriddenParameter",
                     "RedundantIfStatement", "MethodWithMultipleReturnPoints"})
  public boolean equals(Object o)
   {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RecipientTypeDTO that = (RecipientTypeDTO) o;
    if (deliveryId != that.deliveryId) return false;
    if (recipientType != that.recipientType) return false;
    return true;
   }

  @Override
  public int hashCode()
   {
    int result = deliveryId;
    result = 31 * result + (recipientType != null ? recipientType.hashCode() : 0);
    return result;
   }

  /**
   * Ёкземпл€р класса считаетс€ пустым, если поле recipientType пусто или имеет тип RECIPIENT_TYPE_UNKNOWN (см.
   * модуль констант - Defaults).
   * @return boolean »—“»Ќј/Ћќ∆№ в зависимости от того, пуст или нет данный экземпл€р класса.
  */
  public boolean isEmpty() {return (recipientType == null);}

  @Override
  public String toString()
   {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
            append("id", id).
            append("deliveryId", deliveryId).
            append("recipientType", recipientType).
            toString();
   }

  /**
   * ћетод только дл€ тестировани€ класса!
   * @param args String[] параметры метода.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger(DeliveryFileDTO.class.getName());
    Logger logger = Logger.getLogger(DeliveryFileDTO.class.getName());

    RecipientTypeDTO type1 = new RecipientTypeDTO(1, 1, 10);
    RecipientTypeDTO type2 = new RecipientTypeDTO(1, 1, null);

    logger.info("1->2 " + type1.equals(type2));
    logger.info("2->1 " + type2.equals(type1));
    logger.info(type1.isEmpty());
   }

 }