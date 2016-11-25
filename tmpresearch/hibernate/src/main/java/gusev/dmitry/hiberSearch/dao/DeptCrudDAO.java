package gusev.dmitry.hiberSearch.dao;

import gusev.dmitry.hiberSearch.dto.DepartmentDTO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class DeptCrudDAO {

 @PersistenceContext
 private EntityManager em;
 //private JpaTemplate jpaTemplate;


 //@Required
 //public void setEntityManagerFactory(EntityManagerFactory factory) {
 // jpaTemplate = new JpaTemplate(factory);
 //}

 public void create(DepartmentDTO department) {
  //department.setLastUpdated(Calendar.getInstance().getTime());
  //limitStringLengths(department);

  em.persist(department);
 }

 public void remove(DepartmentDTO department) {
  em.remove(department);
 }

 public void update(DepartmentDTO department) {
  //department.setLastUpdated(Calendar.getInstance().getTime());
  //limitStringLengths(department);

  em.merge(department);
 }

 public DepartmentDTO find(long id) {
  return em.find(DepartmentDTO.class, id);
 }

}