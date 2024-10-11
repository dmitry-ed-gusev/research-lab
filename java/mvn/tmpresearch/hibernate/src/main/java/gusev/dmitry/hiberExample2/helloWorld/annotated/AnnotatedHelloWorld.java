package gusev.dmitry.hiberExample2.helloWorld.annotated;

import org.hibernate.Session;
import org.hibernate.Transaction;
import gusev.dmitry.hiberExample2.persistence.HibernateUtil;

import java.util.Iterator;
import java.util.List;

/**
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 28.11.11)
*/

public class AnnotatedHelloWorld
 {
  public static void main(String[] args)
   {
    // First unit of work
    Session session = HibernateUtil.getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();
    AnnotatedMessage message = new AnnotatedMessage("Hello World -> 11111111111111111");
    Long msgId = (Long) session.save(message);
    tx.commit();
    session.close();

    // Second unit of work
    Session newSession = HibernateUtil.getSessionFactory().openSession();
    Transaction newTransaction = newSession.beginTransaction();
    List messages = newSession.createQuery("from AnnotatedMessage m order by m.text asc").list();
    System.out.println( messages.size() + " message(s) found:" );
    for (Iterator iter = messages.iterator(); iter.hasNext();)
     {
      AnnotatedMessage loadedMsg = (AnnotatedMessage) iter.next();
      System.out.println(loadedMsg.getText());
     }
    newTransaction.commit();
    newSession.close();

    // Third unit of work
    Session thirdSession = HibernateUtil.getSessionFactory().openSession();
    Transaction thirdTransaction = thirdSession.beginTransaction();
    // msgId holds the identifier value of the first message
    message = (AnnotatedMessage) thirdSession.get(AnnotatedMessage.class, msgId);
    message.setText("Greetings Earthling -> 222222222222222222222222");
    message.setNextMessage(new AnnotatedMessage( "Take me to your leader (please)" ));
    thirdTransaction.commit();
    thirdSession.close();

    // Shutting down the application
    HibernateUtil.shutdown();

    //Scanner scanner = new Scanner(System.in);
    //scanner.nextLine();

   }
 }