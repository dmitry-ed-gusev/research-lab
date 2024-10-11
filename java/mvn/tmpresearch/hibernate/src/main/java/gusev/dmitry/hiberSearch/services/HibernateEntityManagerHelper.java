package gusev.dmitry.hiberSearch.services;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 16.04.12)
*/

public class HibernateEntityManagerHelper {

 private static EntityManagerFactory emf;

 static {
  try {
   emf = Persistence.createEntityManagerFactory("defaultManager");
  } catch(Throwable tw) {
  throw new ExceptionInInitializerError(tw);
  }
 }

 public static EntityManagerFactory getEntityManagerFactory() {
  return emf;
 }

 public static void shutdown() {
  emf.close();
 }

}