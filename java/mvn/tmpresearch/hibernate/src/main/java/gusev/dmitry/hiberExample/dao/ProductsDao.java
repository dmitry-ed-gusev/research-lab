package gusev.dmitry.hiberExample.dao;

import gusev.dmitry.hiberExample.domain.ProductDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Gusev Dmitry (Дмитрий)
 * @version 1.0 (DATE: 21.06.12)
*/

public class ProductsDao {

 @PersistenceContext
 private EntityManager em;


 //public void create(DepartmentDTO department) {
 // em.persist(department);
 //}

 // public void remove(DepartmentDTO department) {
 //  em.remove(department);
 // }

  //public void update(DepartmentDTO department) {
  // em.merge(department);
  //}

 public ProductDto find(long id) {
  return em.find(ProductDto.class, id);
 }

}