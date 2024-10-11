package gusev.dmitry.hiberSearch.dto;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 16.04.12)
*/

@Entity
@Table(name="PRODUCT")
//@Indexed(index = "indexes/products")
@Indexed
public class ProductDTO {

 //@DocumentId // id key for hibernate search. We use JPA @Id annotation - we can omit @DocumentId Lucene annotation.
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 @Column(name="id")
 private Integer id;

 //@Field(index = Index.YES, store = Store.YES, analyze = Analyze.YES)
 // Index.YES - field value will be indexed, Store.YES - field value will be stored in Lucene index,
 // Analyze.YES - field value will be analyzed (Hiber search doc: Whether or not you want to analyze a property
 // depends on whether you wish to search the element as is, or by the words it contains. It make sense to analyze a
 // text field, but probably not a date field).

 @Field(index = Index.TOKENIZED, store = Store.YES) // hib search 3.x.x
 @Column(name="title")
 private String title;

 //@Field(index = Index.YES, store = Store.YES, analyze = Analyze.YES)
 @Field(index = Index.TOKENIZED, store = Store.YES) // hib search 3.x.x
 private String name;

 //@Field(index = Index.YES, store = Store.YES, analyze = Analyze.YES)
 @Field(index = Index.TOKENIZED, store = Store.YES) // hib search 3.x.x
 @Column(name="description")
 private String description;

 @Column(name="manufacture_date")
 private Date manifactureDate;

 public Integer getId() {
  return id;
 }

 public void setId(Integer id) {
  this.id = id;
 }

 public String getTitle() {
  return title;
 }

 public void setTitle(String title) {
  this.title = title;
 }

 public String getName() {
  return name;
 }

 public void setName(String name) {
  this.name = name;
 }

 public String getDescription() {
  return description;
 }

 public void setDescription(String description) {
  this.description = description;
 }

 public Date getManifactureDate() {
  return manifactureDate;
 }

 public void setManifactureDate(Date manifactureDate) {
  this.manifactureDate = manifactureDate;
 }

 @Override
 public String toString() {
  return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).
          append("id", id).
          append("title", title).
          append("name", name).
          append("description", description).
          append("manifactureDate", manifactureDate).
          toString();
 }

}