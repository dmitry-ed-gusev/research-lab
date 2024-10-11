package gusev.dmitry.hiberSearch.dto.hierarchy;

/**
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 24.04.12)
*/

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseDTOClass {

 @Id
 private Long id;

 public Long getId() {
  return id;
 }

 public void setId(Long id) {
  this.id = id;
 }

}