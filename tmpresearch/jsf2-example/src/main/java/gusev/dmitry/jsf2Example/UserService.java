package gusev.dmitry.jsf2Example;

import gusev.dmitry.jsf2Example.domain.User;

import java.util.*;

/**
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 20.04.12)
*/

public class UserService {

 private static final List<User> USERS_TABLE = new ArrayList<User>();

 static {
  USERS_TABLE.add(new User(1, "Administrator", "admin@gmail.com", "9000510456", new Date(), "M", "Hyderabad"));
  USERS_TABLE.add(new User(2, "Guest", "guest@gmail.com", "9247469543", new Date(), "M", "Hyderabad"));
  USERS_TABLE.add(new User(3, "John", "John@gmail.com", "9000510456", new Date(), "M", "Hyderabad"));
  USERS_TABLE.add(new User(4, "Paul", "Paul@gmail.com", "9247469543", new Date(), "M", "Hyderabad"));
  USERS_TABLE.add(new User(5, "raju", "raju@gmail.com", "9000510456", new Date(), "M", "Hyderabad"));
  USERS_TABLE.add(new User(6, "raghav", "raghav@gmail.com", "9247469543", new Date(), "M", "Hyderabad"));
  USERS_TABLE.add(new User(7, "caren", "caren@gmail.com", "9000510456", new Date(), "M", "Hyderabad"));
  USERS_TABLE.add(new User(8, "Mike", "Mike@gmail.com", "9247469543", new Date(), "M", "Hyderabad"));
  USERS_TABLE.add(new User(9, "Steve", "Steve@gmail.com", "9000510456", new Date(), "M", "Hyderabad"));
  USERS_TABLE.add(new User(10, "Polhman", "Polhman@gmail.com", "9247469543", new Date(), "M", "Hyderabad"));
  USERS_TABLE.add(new User(11, "Rogermoor", "Rogermoor@gmail.com", "9000510456", new Date(), "M", "Hyderabad"));
  USERS_TABLE.add(new User(12, "Robinhood", "Robinhood@gmail.com", "9247469543", new Date(), "M", "Hyderabad"));
  USERS_TABLE.add(new User(13, "Sean", "Sean@gmail.com", "9000510456", new Date(), "M", "Hyderabad"));
  USERS_TABLE.add(new User(14, "Gabriel", "Gabriel@gmail.com", "9247469543", new Date(), "M", "Hyderabad"));
  USERS_TABLE.add(new User(15, "raman", "raman@gmail.com", "9000510456", new Date(), "M", "Hyderabad"));
 }

 public Integer create(User user) {
  if(user == null) {
   throw new RuntimeException("Unable to create User. User object is null.");
  }
  Integer userId = this.getMaxUserId();
  user.setUserId(userId);
  USERS_TABLE.add(user);
  return userId;
 }

 public void delete(User user) {
  if(user == null) {
   throw new RuntimeException("Unable to delete User. User object is null.");
  }
  USERS_TABLE.remove(user.getUserId());
 }

 public List<User> getAllUsers() {
  return USERS_TABLE;
 }

 public User getUser(Integer userId) {
  return USERS_TABLE.get(userId);
 }

 public List<User> searchUsers(String username) {
  String searchCriteria = (username == null) ? "" : username.toLowerCase().trim();
  List<User> searchResults = new ArrayList<User>();
  for (User user : USERS_TABLE) {
   if(user.getUsername() != null && user.getUsername().toLowerCase().trim().startsWith(searchCriteria)) {
    searchResults.add(user);
   }
  } // end of for
  return searchResults;
 }

 public void update(User user) {
  if(user == null || !USERS_TABLE.contains(user)) {
   throw new RuntimeException("Unable to update User. User object is null or User Id ["+user.getUserId()+"] is invalid." );
  }
  USERS_TABLE.add(user);
 }

 protected Integer getMaxUserId() {
  Integer maxId = 1;
  for (User user : USERS_TABLE) {
   if(user.getUserId() > maxId) {
    maxId = user.getUserId();
   }
  }
  return maxId + 1;
 }

}