package gusev.dmitry.hiberExample2.helloWorld.jpa;

import gusev.dmitry.hiberExample2.helloWorld.annotated.AnnotatedMessage;
import gusev.dmitry.hiberExample2.helloWorld.hibernate.HiberMessage;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

/**
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 02.12.11)
*/

public class JpaHelloWorld
 {
  public static void main(String[] args)
   {
    // Start EntityManagerFactory
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("helloworld_full");
    // First unit of work
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    HiberMessage message = new HiberMessage("Hello World. I'm from JPA!!!!");
    em.persist(message);
    tx.commit();
    em.close();

    // Second unit of work
    EntityManager newEm = emf.createEntityManager();
    EntityTransaction newTx = newEm.getTransaction();
    newTx.begin();
    List messages = newEm.createQuery("select m from HiberMessage m order by m.text asc").getResultList();
    System.out.println( messages.size() + " message(s) found" );
    for (Object m : messages)
     {
      HiberMessage loadedMsg = (HiberMessage) m;
      System.out.println(loadedMsg.getText());
     }
    newTx.commit();
    newEm.close();

    // third unit of work
    EntityManager manager = emf.createEntityManager();
    EntityTransaction transaction = manager.getTransaction();
    transaction.begin();
    AnnotatedMessage aMessage = new AnnotatedMessage("Hello world! I'm annotated message from JPA!");
    manager.persist(aMessage);
    transaction.commit();
    manager.close();

    // Shutting down the application
    emf.close();
   }
 }