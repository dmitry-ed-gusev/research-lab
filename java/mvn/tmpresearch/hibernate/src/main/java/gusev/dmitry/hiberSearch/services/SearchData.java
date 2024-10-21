package gusev.dmitry.hiberSearch.services;

import gusev.dmitry.hiberSearch.dto.ProductDTO;
import gusev.dmitry.hiberSearch.dto.hierarchy.StaffMemberDTO;

import javax.persistence.EntityManager;

/**
 * @author Gusev Dmitry (Дмитрий)
 * @version 1.0 (DATE: 18.04.12)
*/

public class SearchData {

 public static void main(String[] args) {
   try {
    EntityManager em = HibernateEntityManagerHelper.getEntityManagerFactory().createEntityManager();
    //em.getTransaction().begin();

    // search
    ProductDTO foundProduct = em.find(ProductDTO.class, 3);
    System.out.println("product -> " + foundProduct);

    StaffMemberDTO staffer = em.find(StaffMemberDTO.class, 2L);
    System.out.println("staffer -> " + staffer.getFamily() + " (" + staffer.getPosition() + ")");

    //DepartmentDTO foundDepartment = em.find(DepartmentDTO.class, 2L);
    //System.out.println("-> " + foundDepartment);

    //em.getTransaction().commit();
    //System.out.println("Transaction commited.");
    em.close();
    HibernateEntityManagerHelper.shutdown();
   } catch(Exception e) {
    e.printStackTrace();
   }
  }

}
