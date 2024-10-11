package gusev.dmitry.hiberExample.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Gusev Dmitry (Дмитрий)
 * @version 1.0 (DATE: 21.06.12)
*/

@Entity
@Table(name = "PRODUCTSPARTS")
public class PartDto extends BaseDto {

 private int productId;

 public int getProductId() {
  return productId;
 }

 public void setProductId(int productId) {
  this.productId = productId;
 }
}
