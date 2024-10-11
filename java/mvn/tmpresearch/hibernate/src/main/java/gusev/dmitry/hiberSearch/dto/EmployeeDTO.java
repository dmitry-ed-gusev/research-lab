package gusev.dmitry.hiberSearch.dto;

import org.hibernate.search.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Employee entity.
 * @author Gusev Dmitry
 * @version 1.0 (DATE: 10.04.12)
*/

@SuppressWarnings({"InstanceVariableMayNotBeInitialized"})
@Entity
@Table(name="EMPLOYEES")
//@Indexed(index = "indexes/employees") // <- we can define index name
@Indexed
public class EmployeeDTO {
 @Id
 private int    id;

 //@Field(index = Index.YES, store = Store.YES, analyze = Analyze.YES) // hib search 4
 @Field(index = Index.TOKENIZED, store = Store.YES) // hib search 3.x.x
 private String name;

 //@Field(index = Index.YES, store = Store.YES, analyze = Analyze.YES)
 @Field(index = Index.TOKENIZED, store = Store.YES) // hib search 3.x.x
 private String family;

 //@Field(index = Index.YES, store = Store.YES, analyze = Analyze.YES)
 @Field(index = Index.TOKENIZED, store = Store.YES) // hib search 3.x.x
 private String comment;

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