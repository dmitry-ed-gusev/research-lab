package gusev.dmitry.hiberSearch.dto.hierarchy;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;

import javax.persistence.MappedSuperclass;

/**
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 24.04.12)
*/

@MappedSuperclass
public class BaseEmployeeDTO extends BaseDTOClass {

 //@Field(index = Index.YES, store = Store.YES, analyze = Analyze.YES) // hib search 4.x.x
 @Field(index = Index.TOKENIZED, store = Store.YES) // hib search 3.x.x
 private String name;

 //@Field(index = Index.YES, store = Store.YES, analyze = Analyze.YES) // hib search 4
 @Field(index = Index.TOKENIZED, store = Store.YES) // hib search 3.x.x
 private String family;

 //@Field(index = Index.YES, store = Store.YES, analyze = Analyze.YES) // hib search 4
 @Field(index = Index.TOKENIZED, store = Store.YES) // hib search 3.x.x
 private String comment;

 public String getName() {
  return name;
 }

 public void setName(String name) {
  this.name = name;
 }

 public String getFamily() {
  return family;
 }

 public void setFamily(String family) {
  this.family = family;
 }

 public String getComment() {
  return comment;
 }

 public void setComment(String comment) {
  this.comment = comment;
 }

}