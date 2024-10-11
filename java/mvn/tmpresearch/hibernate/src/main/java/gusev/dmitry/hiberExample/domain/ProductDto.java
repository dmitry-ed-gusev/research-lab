package gusev.dmitry.hiberExample.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.List;

/**
 * @author Gusev Dmitry (Дмитрий)
 * @version 1.0 (DATE: 21.06.12)
*/

@Entity
@Table(name = "PRODUCTS")
public class ProductDto extends BaseDto {

 private String title;
 private String description;

 @OneToMany
 @JoinColumn(name = "productId")
 private List<PartDto> partsList;

 public String getTitle() {
  return title;
 }

 public void setTitle(String title) {
  this.title = title;
 }

 public String getDescription() {
  return description;
 }

 public void setDescription(String description) {
  this.description = description;
 }

 public List<PartDto> getPartsList() {
  return partsList;
 }

 public void setPartsList(List<PartDto> partsList) {
  this.partsList = partsList;
 }

 @Override
 public String toString() {
  return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
          appendSuper(super.toString()).
          append("title", title).
          append("description", description).
          append("partsList", partsList).
          toString();
 }

}