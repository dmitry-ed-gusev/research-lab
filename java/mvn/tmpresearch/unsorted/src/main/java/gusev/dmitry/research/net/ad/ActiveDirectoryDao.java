package gusev.dmitry.research.net.ad;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.*;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 16.08.12)
*/

public class ActiveDirectoryDao {

 private static Log log = LogFactory.getLog(ActiveDirectoryDao.class);

 private DirContext ldapContext;
 private String     host     = "194.186.82.194";
 private String     port     = "389";
 private String     username = "MESSystem";
 private String     password = "tky4582e";
 // Distinguished Name
 private String baseDn = "DC=kzgroup,DC=local";
 //private String baseDn = "DC=local";

 public ActiveDirectoryDao(String username, String password) {
  try {
   Hashtable ldapEnv = new Hashtable(5);
   ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
   ldapEnv.put(Context.PROVIDER_URL, "ldap://" + host + ":" + port);
   ldapEnv.put(Context.SECURITY_PRINCIPAL, username + "@kzgroup.local");
   ldapEnv.put(Context.SECURITY_CREDENTIALS, password);
   ldapContext = new InitialDirContext(ldapEnv);
  } catch (NamingException e) {
   log.error("Some error occured!", e);
  }
 }

 /**
  * Closing Active Directory context.
 */
 public void close() {
  if (ldapContext != null) {
   try {
    ldapContext.close();
   } catch (NamingException e) {
    throw new RuntimeException(e);
   }
  }
 }

 public boolean hasUser(String name) {
  try {
   NamingEnumeration all = ldapContext.search(baseDn, "name=" + name, getSearchControl());
   // NamingEnumeration all = ldapContext.search(baseDn, "(objectClass=user)", getSearchControl());
   return all.hasMoreElements();
  } catch (NamingException e) {
   throw new RuntimeException(e);
  }
 }

 public Map getUser(String name) {
  try {
   NamingEnumeration all = ldapContext.search(baseDn, "name=" + name, getSearchControl());
    while (all.hasMoreElements()) {
     SearchResult each = (SearchResult) all.nextElement();
     Map result = new HashMap();
     NamingEnumeration attributes = each.getAttributes().getAll();
     while (attributes.hasMoreElements()) {
      Attribute attribute = (Attribute) attributes.nextElement();
      result.put(attribute.getID(), attribute.get());
     }
     return result;
    }
   return Collections.EMPTY_MAP;
  } catch (NamingException e) {
   throw new RuntimeException(e);
  }
 }

 protected SearchControls getSearchControl() {
  SearchControls result = new SearchControls();
  result.setCountLimit(1);
  result.setSearchScope(SearchControls.SUBTREE_SCOPE);
  String objAttribs[] = {"displayName"};
  //  result.setReturningAttributes(objAttribs);
  return result;
 }

 public static void main(String[] args) throws Exception {
  log.info("Starting Active Directory testing.");
  ActiveDirectoryDao dao = new ActiveDirectoryDao("MESSystem","tky4582e");
  log.info("Successfully connected to AD.");

  ArrayList<String> usersList = new ArrayList<String>(Arrays.asList
   ("testmes1", "testmes2", "testmes3", "testmes4", "testmes5", "testmes6", "testmes7"));

  for (String user : usersList) {
   log.info("User [" + user + "] exists -> " + dao.hasUser(user));
  }

  //System.out.println(dao.hasUser("testmes1"));
  //System.out.println(dao.getUser("testmes4").get("description"));
  dao.close();
 }

}