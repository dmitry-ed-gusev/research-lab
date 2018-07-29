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
   * ��������������� ����������� ������. �������� � ���� ��������� �������, ������� ������� ����� �����������
   * ���������� �������� XOR � ��������� ������� (String ��� char[]).
  */
  private byte[] bytePassword = null;

  /**
   * ������������ ������.
   * @param password String �������� ������ ��� �������� � ������ ������.
   * @throws EmptyPassException �� - ��� �������� ���������� ������ ������� ������ �������� ��� ������.
  */
  public Password(String password) throws EmptyPassException
  {
    // ��������, ������ ���� ��������� ������ �� ����
    if (!StringUtils.isBlank(password))
     {
      byte[] foreignPass = password.getBytes();
      bytePassword = new byte[foreignPass.length];
      // �������� ������ (XOR) �������� ������ �� ������ ������ � ���������� �������� ���������
      for (int i = 0; i < foreignPass.length; i++) {bytePassword[i] = (byte) (foreignPass[i]^XOR_MODULE);}
     }
    // ���� ������ ������ ������ - ������ (���������� ��)
    else {throw new EmptyPassException("Password cannot be empty!");}
   }

  /**
   * ����� ��������� ������.
   * @return String ���������� � ������ ������ ������.
  */
  public String getPassword()
   {
    byte[] byteResult = new byte[bytePassword.length];
    // ��� ����������� ������������ ������ � ������� ��� - ����� ������ ��� �� ������ ������
    for (int i = 0; i < bytePassword.length; i++) {byteResult[i] = (byte) (bytePassword[i]^XOR_MODULE);}
    return new String(byteResult);
   }

  /**
   * ����� ��������� ������ ��� ��������.
   * @param password String �������� ������ ��� �������� � ������ ������.
   * @throws EmptyPassException �� - ��� ��������� ������ ������� ������ ��������.
  */
  public void setPassword(String password) throws EmptyPassException
   {
    // ��������, ������ ���� ��������� ������ �� ����
    if (!StringUtils.isBlank(password))
     {
      byte[] foreignPass = password.getBytes();
      bytePassword = new byte[foreignPass.length];
      // �������� ������ (XOR) �������� ������ �� ������ ������ � ���������� �������� ���������
      for (int i = 0; i < foreignPass.length; i++) {bytePassword[i] = (byte) (foreignPass[i]^XOR_MODULE);}
     }
    // ���� ������ ������ ������ - ������ (���������� ��)
    else {throw new EmptyPassException("Password cannot be empty!");}
   }

  @Override
  public String toString() {return ("PASSWORD [" + this.getPassword() + "]");}
  
 }