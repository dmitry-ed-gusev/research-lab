package gusev.dmitry.hiberSearch.services;

import gusev.dmitry.hiberSearch.dto.DepartmentDTO;
import gusev.dmitry.hiberSearch.dto.ProductDTO;
import gusev.dmitry.hiberSearch.dto.hierarchy.StaffMemberDTO;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 16.04.12)
*/

public class ProductService {

 public static void main(String[] args) {
  try {
   // entity manager
   EntityManager em = HibernateEntityManagerHelper.getEntityManagerFactory().createEntityManager();
   // full text entity manager
   FullTextEntityManager fullTextEntityManager =  Search.getFullTextEntityManager(em);

   // indexing existed data in db (we use JPA)
   fullTextEntityManager.createIndexer().startAndWait();

   // we need to manual index our database if we turned auto indexing off (hibernate.search.indexing_strategy=manual)
   /*
   List<ProductDTO> products = em.createQuery("select product  from ProductDTO as  product").getResultList();
   for (ProductDTO product : products) {
    fullTextEntityManager.index(product);
    System.out.println("Product " + product.getTitle());
   }
   */

   // we manual index our instances


   fullTextEntityManager.getTransaction().begin();

   // search for one table - product
   String[] fields = new String[]{"title", "description"};

   //MultiFieldQueryParser gusev = new MultiFieldQueryParser(Version.LUCENE_35, fields, new StandardAnalyzer(Version.LUCENE_35));
   MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_31, fields, new StandardAnalyzer(Version.LUCENE_31));

   // gusev.setDefaultOperator(QueryParser.AND_OPERATOR);
   parser.setAllowLeadingWildcard(true);
   // Search only by lowercase
   org.apache.lucene.search.Query query = parser.parse("phone");
   // wrap Lucene query in a javax.persistence.Query
   javax.persistence.Query persistenceQuery =  fullTextEntityManager.createFullTextQuery(query, ProductDTO.class);
   // execute search
   List<ProductDTO> result = persistenceQuery.getResultList();
   System.out.println("result :: " + result);
   for (ProductDTO product : result) {
    System.out.println("product :: " + product.getTitle() + " (" + product.getDescription() + ")");
   }

   System.out.println("\n----------------------------------\n");

   // search for two tables - product and departments
   String[] someTablesFields = new String[] {"title", "description", "name", "code", "comment", "family", "position"};

   //MultiFieldQueryParser someTablesParser = new MultiFieldQueryParser(Version.LUCENE_35, someTablesFields, new StandardAnalyzer(Version.LUCENE_35));
   MultiFieldQueryParser someTablesParser = new MultiFieldQueryParser(Version.LUCENE_31, someTablesFields, new StandardAnalyzer(Version.LUCENE_31));

   someTablesParser.setAllowLeadingWildcard(true);
   //Query someTablesQuery = someTablesParser.parse("*4s*");
   Query someTablesQuery = someTablesParser.parse("*ю* OR *нь* OR *раб* OR *tr*");
   FullTextQuery ftQuery = fullTextEntityManager.createFullTextQuery(someTablesQuery, ProductDTO.class, DepartmentDTO.class, StaffMemberDTO.class);
   List secondResult = ftQuery.getResultList();
   System.out.println("secondResult result size -> " + secondResult.size());
   // results print
   for (Object object : secondResult) {
    if (object instanceof ProductDTO) {
     System.out.println("product -> [" + object + "]");
    } else if (object instanceof DepartmentDTO) {
     System.out.println("department -> [" + object + "]");
    } else if (object instanceof StaffMemberDTO) {
     StaffMemberDTO staffer = (StaffMemberDTO) object;
     System.out.println("staffer -> " + staffer.getName() + " " + staffer.getFamily() + " (" + staffer.getPosition() + ")");
    } else {
     System.err.println("Unknown class!");
    }
   }

   System.out.println("\n----------------------------------\n");

   // --- Sample from Hibernate Search doc. ---
   // Create native Lucene query unsing the query DSL. Alternatively you can write the Lucene query using the Lucene query gusev
   // or the Lucene programmatic API. The Hibernate Search DSL is recommended though.
   QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(ProductDTO.class).get();
   Query sampleQuery = qb.keyword().onFields("title", "name", "description").matching("4s").createQuery();
   // wrap Lucene query in a javax.persistence.Query
   javax.persistence.Query samplePersistenceQuery = fullTextEntityManager.createFullTextQuery(sampleQuery, ProductDTO.class);
   // execute search
   List<ProductDTO> sampleResult = samplePersistenceQuery.getResultList();
   for (ProductDTO product : sampleResult) {
    System.out.println("product :: " + product);
   }

   fullTextEntityManager.getTransaction().commit();

   em.close();
   HibernateEntityManagerHelper.shutdown();
  } catch(Exception e) {
   e.printStackTrace();
  }

 }

}