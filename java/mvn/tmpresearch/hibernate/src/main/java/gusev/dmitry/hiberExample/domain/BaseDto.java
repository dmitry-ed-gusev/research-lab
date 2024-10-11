package gusev.dmitry.hiberExample.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author Gusev Dmitry (Дмитрий)
 * @version 1.0 (DATE: 21.06.12)
*/


@MappedSuperclass
public abstract class BaseDto {

 @Id
 private int    id;

 private String name;

 public int getId() {
  return id;
 }

 public void setId(int id) {
  this.id = id;
 }

 public String getName() {
  return name;
 }

 public void setName(String name) {
  this.name = name;
 }

 @Override
 public String toString() {
  return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
          append("id", id).
          append("name", name).
          toString();
 }

}