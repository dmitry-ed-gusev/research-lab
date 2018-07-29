package jlib.actions.result;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * ����� ��������� ��������������� ��������� ���������� ��������(action'a) - ������� ��������� ����������������
 * ������ ���������� java.
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 04.06.2010)
*/

public class ActionExecuteResult
 {
  /** ������ ������, ��������� ��� ���������� �������� (action'a). */
  private ArrayList<String>    errors = null;
  /**
   * ���� ��� �������� ������ ��. �� ������������ �����������. ��������� ��� ������������� � ����������� ��������.
   * @deprecated ������������� �� �������������! 
  */
  private ArrayList<Exception> exceptions = null;
  /** ������-��������� ���������� ��������. ���������� � ������� ���� ���������� �����. */
  private Object               result = null;

  public ArrayList<String> getErrors() {
   return errors;
  }

  public void setErrors(ArrayList<String> errors) {
   this.errors = errors;
  }

  public Object getResult() {
   return result;
  }

  public void setResult(Object result) {
   this.result = result;
  }

  /**
   * ����� ��������� � ������ ������ ��� ���� ��������� (�� ������).
   * @param error String ����������� ��������� �� ������.
  */
  public void addError(String error)
   {
    if (!StringUtils.isBlank(error))
     {
      if (errors == null) {errors = new ArrayList<String>();}
      errors.add(error);
     }
   }

  /**
   * ����� ���������� ���������� ������ � ������.
   * @return int ���������� ������.
  */
  public int getErrorsCount()
   {
    int result = 0;
    if ((errors != null) && (!errors.isEmpty())) {result = errors.size();}
    return result;
   }

  /**
   * ����� ���������� ���������� ��������, ��������� � ������� ������ (TRUE) ��� �� ���������� (FALSE).
   * @return boolean ������/���� � ����������� �� ������� ������ � ������.
  */
  public boolean isErrors() {return (this.getErrorsCount() > 0);}

  public ArrayList<Exception> getExceptions() {
   return exceptions;
  }

  public void setExceptions(ArrayList<Exception> exceptions) {
   this.exceptions = exceptions;
  }
  
 }