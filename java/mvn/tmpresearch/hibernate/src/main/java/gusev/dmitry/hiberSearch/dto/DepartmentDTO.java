package gusev.dmitry.hiberSearch.dto;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.search.annotations.*;

import javax.persistence.*;

/**
 * Department entity.
 * @author Gusev Dmitry
 * @version 1.0 (DATE: 10.04.12)
*/

@SuppressWarnings({"InstanceVariableMayNotBeInitialized"})

@Entity
@Table(name = "DEPARTMENTS")
//@Indexed(index = "indexes/departments")
@Indexed
public class DepartmentDTO {

 //@DocumentId // id key for hibernate search
 @Id
 @GeneratedValue(strategy = GenerationType.AUTO)
 private long   id;

 //@Field(index = Index.YES, store = Store.YES, analyze = Analyze.YES)
 @Field(index = Index.TOKENIZED, store = Store.YES) // hib search 3.x.x
 private String name;

 //@Field(index = Index.YES, store = Store.YES, analyze = Analyze.YES)
 @Field(index = Index.TOKENIZED, store = Store.YES) // hib search 3.x.x
 private String code;

 //@Field(index = Index.YES, store = Store.YES, analyze = Analyze.YES)
 @Field(index = Index.TOKENIZED, store = Store.YES) // hib search 3.x.x
 private String comment;

 public DepartmentDTO() {
 }

 public DepartmentDTO(String name, String code, String comment) {
  this.name = name;
  this.code = code;
  this.comment = comment;
 }

 public long getId() {
  return id;
 }

 public void setId(long id) {
  this.id = id;
 }

 //@Field(name = "name", analyze = Analyze.YES, store = Store.YES)
 @Field(index = Index.TOKENIZED, store = Store.YES) // hib search 3.x.x
 public String getName() {
  return name;
 }

 public void setName(String name) {
  this.name = name;
 }

 //@Field(name = "code", analyze = Analyze.YES, store = Store.YES)
 @Field(index = Index.TOKENIZED, store = Store.YES) // hib search 3.x.x
 public String getCode() {
  return code;
 }

 public void setCode(String code) {
  this.code = code;
 }

 //@Field(name = "comment", analyze = Analyze.YES, store = Store.YES)
 @Field(index = Index.TOKENIZED, store = Store.YES) // hib search 3.x.x
 public String getComment() {
  return comment;
 }

 public void setComment(String comment) {
  this.comment = comment;
 }

 @Override
 public String toString() {
  return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).
          append("id", id).
          append("name", name).
          append("code", code).
          append("comment", comment).
          toString();
 }

}