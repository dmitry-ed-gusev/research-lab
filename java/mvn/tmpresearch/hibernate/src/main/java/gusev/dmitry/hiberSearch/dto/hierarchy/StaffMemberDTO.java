package gusev.dmitry.hiberSearch.dto.hierarchy;

import org.hibernate.search.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 24.04.12)
*/

@Entity
@Table(name = "employees")
@Indexed
public class StaffMemberDTO extends BaseEmployeeDTO {

 //@Field(index = Index.YES, store = Store.YES, analyze = Analyze.YES)
 @Field(index = Index.TOKENIZED, store = Store.YES) // hib search 3.x.x
 private String position;

 public String getPosition() {
  return position;
 }

 public void setPosition(String position) {
  this.position = position;
 }
}