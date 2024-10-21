package gusev.dmitry.research.net.ad;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

/**
 * @author Gusev Dmitry (DGusev)
 * @version 1.0 (DATE: 11.07.12)
*/

public class Main {

 public static void main(String[] args) {
  Log log = LogFactory.getLog(Main.class);
  log.info("Starting...");

  StringBuffer output = new StringBuffer();

  Hashtable authEnv = new Hashtable(11);
  String userName = "MESSystem";
  String password = "tky4582e";
  String base     = "ou=test,dc=kzgroup,dc=local"; // “ou=people,o=banner.iit.edu,o=iit”;
  String dn       = "uid=" + userName + "," + base; //“uid=” + userName + “,” + base;
  //String ldapURL = "ldap://194.186.82.194:389/ou=test";
  String ldapURL = "ldap://localhost:10389";

  authEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
  authEnv.put(Context.PROVIDER_URL,            ldapURL);
  authEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
  authEnv.put(Context.SECURITY_PRINCIPAL,      dn);
  authEnv.put(Context.SECURITY_CREDENTIALS,    password);

  try {
   //String url = "ldap://194.186.82.194:389/o=Cornell%20University,c=US";
   // "uid=MESSystem,ou=test,dc=kzgroup,dc=local", "tky4582e"

   //Hashtable env = new Hashtable();
   //env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
   //env.put(Context.PROVIDER_URL, url);
   DirContext context = new InitialDirContext(authEnv);
   log.info("connected!");

   //SearchControls ctrl = new SearchControls();
   //ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);

   //NamingEnumeration all = context.search(base, "name=" + name, getSearchControl());
   // NamingEnumeration enumeration = context.search("", "", ctrl);
   /*
   while (enumeration.hasMore()) {
    SearchResult result = (SearchResult) enumeration.next();
    Attributes attribs = result.getAttributes();
    NamingEnumeration values = ((BasicAttribute) attribs.get("cn")).getAll();
    while (values.hasMore()) {
     if (output.length() > 0) {
      output.append("|");
     }
     output.append(values.next().toString());
    }
   }
   */
  } catch (Exception e) {
   e.printStackTrace();
  }
  System.out.print(output.toString());

  //LDAPConnection connection = new LDAPConnection("localhost", 10389, "uid=gus,ou=system", "123456789");
  //LDAPConnection connection = new LDAPConnection("194.186.82.194", 389, "uid=MESSystem,ou=test,dc=kzgroup,dc=local", "tky4582e");

  /*
  if (connection != null) {
   log.info("OK, connected!");
   SearchResult searchResults = connection.search("uid=gus,ou=system", SearchScope.SUB, "(uid=gus)", "cn");
   String desc;
   if (searchResults.getEntryCount() > 0) {
    SearchResultEntry entry = searchResults.getSearchEntries().get(0);
    desc = entry.getAttributeValue("cn");
    log.info("DESC -> " + desc);
   } else {
    log.info("Nothing found.");
   }
  } else {
   log.error("Empty connection!");
  }
  */

 }

}