package gusev.dmitry.hiberExample;

import gusev.dmitry.hiberExample.domain.ProductDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.EntityManager;

/**
 * @author Gusev Dmitry (Дмитрий)
 * @version 1.0 (DATE: 22.06.12)
*/

public class Main {

 public static void main(String[] args) {

  Log log = LogFactory.getLog(Main.class);
  log.info("Started...");

  EntityManager em = HibernateEntityManagerHelper.getEntityManagerFactory().createEntityManager();

  // search
  ProductDto productDto = em.find(ProductDto.class, 1);
  log.info("product -> " + productDto);

  em.close();
  HibernateEntityManagerHelper.shutdown();
 }

}