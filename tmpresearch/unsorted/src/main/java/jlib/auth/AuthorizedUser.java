package jlib.auth;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Класс авторизованного пользователя. Содержит ФИО пользователя из БД "Кадры", идентификатор
 * пользователя из БД "Кадры" и код (строку) ошибки (используется, если не удалось получить
 * ФИО и идентификатор).
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 08.10.2008)
*/

public class AuthorizedUser implements Serializable
 {
  static final long serialVersionUID = 4720090046144350344L;
  
  private String     fullName;
  private int        personnelID;
  private String     errorCode;
  private SimpleUser simpleUser;

  public AuthorizedUser()
   {
    this.fullName    = null;
    this.personnelID = -1;
    this.errorCode   = null;
    this.simpleUser  = null;  
   }

  public AuthorizedUser(String fullName, int personnelID, String errorCode, SimpleUser simpleUser)
   {
    this.fullName    = fullName;
    this.personnelID = personnelID;
    this.errorCode   = errorCode;
    this.simpleUser  = simpleUser;
   }

  public String getFullName() {return fullName;}
  public void setFullName(String fullName) {this.fullName = fullName;}

  public int getPersonnelID() {return personnelID;}
  public void setPersonnelID(int personnelID) {this.personnelID = personnelID;}

  public String getErrorCode() {return errorCode;}
  public void setErrorCode(String errorCode) {this.errorCode = errorCode;}

  public SimpleUser getSimpleUser() {return simpleUser;}
  public void setSimpleUser(SimpleUser simpleUser) {this.simpleUser = simpleUser;}

  public String toString()
   {return "AuthorizedUser{" + "fullName='" + fullName + '\'' + ", personnelID=" + personnelID +
           ", errorCode='" + errorCode + '\'' + ", simpleUser=" + simpleUser + '}';}

  /**
   * Проверка, не является ли данный объект пустым. Пустым данный объект признается, если пусто хотя бы одно
   * значимое поле - кроме поля errorCode, т.е. если поле errorCode заполнено, а остальные поля пусты - объект
   * считается пустым.
   * @return boolean ИСТИНА/ЛОЖЬ в зависимости от того, пуст данный объект или нет.
  */
  public boolean isEmpty()
   {return (StringUtils.isBlank(this.fullName) || (this.personnelID <= 0) ||
            (this.simpleUser == null) || (this.simpleUser.isEmpty()));}

  /**
   * Метод предназначен только для тестирования данного класса.
   * @param args String[] параметры метода.
  */
  public static void main(String[] args) {}

 }