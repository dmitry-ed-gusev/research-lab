package gusev.dmitry.hiberExample2.persistence;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 29.11.11)
*/

public class HibernateUtil
 {
  private static SessionFactory sessionFactory;

  static
   {
    try {sessionFactory = new Configuration().configure().buildSessionFactory();}
    catch (Throwable ex) {throw new ExceptionInInitializerError(ex);}
   }

  public static SessionFactory getSessionFactory()
   {
    // Alternatively, you could look up in JNDI here
    return sessionFactory;
   }

  public static void shutdown()
   {
    // Close caches and connection pools
    getSessionFactory().close();
   }

 }