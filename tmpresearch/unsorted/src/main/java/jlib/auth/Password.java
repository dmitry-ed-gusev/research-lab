package jlib.auth;

import jlib.exceptions.EmptyPassException;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 10.03.2009)
*/

public class Password implements Serializable
 {
  /***/
  private final static int XOR_MODULE = 13;

  /**
   * Ќепосредственно сохран€емый пароль. ’ранитс€ в виде байтового массива, который получен путем побайтового
   * применени€ операции XOR к исходному массиву (String или char[]).
  */
  private byte[] bytePassword = null;

  /**
   *  онструкторы класса.
   * @param password String значение парол€ дл€ хранени€ в данном классе.
   * @throws EmptyPassException »— - при создании экземпл€ра класса указано пустое значение дл€ парол€.
  */
  public Password(String password) throws EmptyPassException
  {
    // –аботаем, только если указанный пароль не пуст
    if (!StringUtils.isBlank(password))
     {
      byte[] foreignPass = password.getBytes();
      bytePassword = new byte[foreignPass.length];
      // ѕобайтно ксорим (XOR) исходный пароль по нашему модулю и полученные значени€ сохран€ем
      for (int i = 0; i < foreignPass.length; i++) {bytePassword[i] = (byte) (foreignPass[i]^XOR_MODULE);}
     }
    // ≈сли указан пустой пароль - ошибка (генерируем »—)
    else {throw new EmptyPassException("Password cannot be empty!");}
   }

  /**
   * ћетод получени€ парол€.
   * @return String хран€щийс€ в данном классе пароль.
  */
  public String getPassword()
   {
    byte[] byteResult = new byte[bytePassword.length];
    // ƒл€ превращени€ заксоренного парол€ в обычный вид - снова ксорим его по нашему модулю
    for (int i = 0; i < bytePassword.length; i++) {byteResult[i] = (byte) (bytePassword[i]^XOR_MODULE);}
    return new String(byteResult);
   }

  /**
   * ћетод установки парол€ дл€ хранени€.
   * @param password String значение парол€ дл€ хранени€ в данном классе.
   * @throws EmptyPassException »— - дл€ установки парол€ указано пустое значение.
  */
  public void setPassword(String password) throws EmptyPassException
   {
    // –аботаем, только если указанный пароль не пуст
    if (!StringUtils.isBlank(password))
     {
      byte[] foreignPass = password.getBytes();
      bytePassword = new byte[foreignPass.length];
      // ѕобайтно ксорим (XOR) исходный пароль по нашему модулю и полученные значени€ сохран€ем
      for (int i = 0; i < foreignPass.length; i++) {bytePassword[i] = (byte) (foreignPass[i]^XOR_MODULE);}
     }
    // ≈сли указан пустой пароль - ошибка (генерируем »—)
    else {throw new EmptyPassException("Password cannot be empty!");}
   }

  @Override
  public String toString() {return ("PASSWORD [" + this.getPassword() + "]");}
  
 }