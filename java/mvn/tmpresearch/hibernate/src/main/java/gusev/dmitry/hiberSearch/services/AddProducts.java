package gusev.dmitry.hiberSearch.services;

import gusev.dmitry.hiberSearch.dto.DepartmentDTO;
import gusev.dmitry.hiberSearch.dto.ProductDTO;

import javax.persistence.EntityManager;
import java.util.Date;

/**
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 16.04.12)
*/

public class AddProducts {
 public static void main(String[] args) {
  try {
   EntityManager em = HibernateEntityManagerHelper.getEntityManagerFactory().createEntityManager();
   em.getTransaction().begin();
   System.out.println("Trying to add products...");

   ProductDTO prodEO = new ProductDTO();
   prodEO.setTitle("Mike");
   prodEO.setName("zzzz");
   prodEO.setDescription("XXX company Mike");
   prodEO.setManifactureDate(new Date());
   em.persist(prodEO);
   System.out.println("First product added.");

   prodEO = new ProductDTO();
   prodEO.setTitle("Phone");
   prodEO.setDescription("YYY company Phone");
   prodEO.setManifactureDate(new Date());
   em.persist(prodEO);

   prodEO = new ProductDTO();
   prodEO.setTitle("Microphone");
   prodEO.setDescription("YYY company Microphone");
   prodEO.setManifactureDate(new Date());
   em.persist(prodEO);

   prodEO = new ProductDTO();
   prodEO.setTitle("Phone zzz");
   prodEO.setDescription("YYY company Microphone");
   prodEO.setManifactureDate(new Date());
   em.persist(prodEO);

   prodEO = new ProductDTO();
   prodEO.setTitle("Micro");
   prodEO.setDescription("Моя маленькая компания");
   prodEO.setManifactureDate(new Date());
   em.persist(prodEO);

   // search
   ProductDTO foundProduct = em.find(ProductDTO.class, 34);
   System.out.println("-> " + foundProduct);

   DepartmentDTO foundDepartment = em.find(DepartmentDTO.class, 2L);
   System.out.println("-> " + foundDepartment);

   em.getTransaction().commit();
   System.out.println("Transaction commited.");
   em.close();
   HibernateEntityManagerHelper.shutdown();
  } catch(Exception e) {
   e.printStackTrace();
  }
 }

}