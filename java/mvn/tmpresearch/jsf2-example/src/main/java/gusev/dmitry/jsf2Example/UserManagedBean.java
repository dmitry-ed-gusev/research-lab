package gusev.dmitry.jsf2Example;

import gusev.dmitry.jsf2Example.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.util.Collection;
import java.util.List;

/**
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 20.04.12)
 */

@ManagedBean
@ApplicationScoped
public class UserManagedBean {

 // module logging
 private static final Log log = LogFactory.getLog(UserManagedBean.class);

 private UserService userService = new UserService();

 private String     username;
 private String     password;
 //private String     searchUser;
 private List<User> searchUsersResults;
 //private User       selectedUser;

 public UserManagedBean() {
  log.debug("UserManagedBean.constructor() working.");
  searchUsersResults = userService.getAllUsers();
  for (User user : searchUsersResults) {
   log.debug("User -> " + user);
  }
 }

 public String getUsername() {
  return username;
 }

 public void setUsername(String username) {
  this.username = username;
 }

 public String getPassword() {
  return password;
 }

 public void setPassword(String password) {
  this.password = password;
 }

 //public User getSelectedUser() {
 // if(selectedUser == null) {
 //  selectedUser = new User();
 // }
 // return selectedUser;
 //}

 //public void setSelectedUser(User selectedUser) {
 // this.selectedUser = selectedUser;
 //}

 public Collection<User> getSearchUsersResults() {
  log.debug("UserManagedBean.getSearchUsersResults() working. Results count = [" + (searchUsersResults != null ? searchUsersResults.size() : "empty") + "]");
  return searchUsersResults;
 }

 public void setSearchUsersResults(List<User> searchUsersResults) {
  log.debug("UserManagedBean.setSearchUsersResults() working.");
  this.searchUsersResults = searchUsersResults;
 }

 //public String getSearchUser() {
 // log.debug("UserManagedBean.getSearchUser() working. SearchUser = [" + searchUser + "].");
 // return searchUser;
 //}

 //public void setSearchUser(String searchUser) {
 // log.debug("UserManagedBean.setSearchUser() working.");
 // this.searchUser = searchUser;
 //}

 /**
  * Method is called from login.xhtml (login.jsf).
 */
 public String login() {
  log.debug("UserManagedBean.login() working.");
  if("test".equalsIgnoreCase(getUsername()) && "test".equals(getPassword())) { // auth passed
   return "home"; // <- home.jsf
  } else { // auth failed
   FacesContext context = FacesContext.getCurrentInstance();
   context.addMessage("username", new FacesMessage("Invalid UserName and Password"));
   return "login"; // <- show login page again
  }
 }

 /***/
 /*
 public String searchUser() {
  log.debug("UserManagedBean.searchUser() working.");
  String username = (this.searchUser == null) ? "" : this.searchUser.trim();
  if (userService != null) {
   this.searchUsersResults = userService.searchUsers(username);
   log.debug("Search users results -> \n" + searchUsersResults);
  } else {
   log.warn("UserService is NULL!");
  }
  return "home";
 }
 */

 //public String updateUser() {
 // log.debug("UserManagedBean.updateUser() working.");
 // userService.update(this.selectedUser);
 // return "home";
 //}

 //public void onUserSelect(SelectEvent event) {
 // log.debug("UserManagedBean.onUserSelect() working.");
 //}

 //public void onUserUnselect(UnselectEvent event) {
 // log.debug("UserManagedBean.onUserUnselect() working.");
 //}

}