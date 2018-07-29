package jlib.actions.result;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * Класс реализует унифицированный результат выполнения действия(action'a) - некоего логически взаимосвязанного
 * набора операторов java.
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 04.06.2010)
*/

public class ActionExecuteResult
 {
  /** Список ошибок, возникших при выполнении действия (action'a). */
  private ArrayList<String>    errors = null;
  /**
   * Поле для хранения списка ИС. Не используется библиотекой. Сохранено для совместимости с предыдущими версиями.
   * @deprecated использование не рекомендовано! 
  */
  private ArrayList<Exception> exceptions = null;
  /** Объект-результат выполнения действия. Приводится к нужному типу вызывающим кодом. */
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
   * Метод добавляет к списку ошибок еще одно сообщение (не пустое).
   * @param error String добавляемое сообщение об ошибке.
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
   * Метод возвращает количество ошибок в списке.
   * @return int количество ошибок.
  */
  public int getErrorsCount()
   {
    int result = 0;
    if ((errors != null) && (!errors.isEmpty())) {result = errors.size();}
    return result;
   }

  /**
   * Метод возвращает логическое значение, говорящее о наличии ошибок (TRUE) или их отсутствии (FALSE).
   * @return boolean ИСТИНА/ЛОЖЬ в зависимости от наличия ошибок в списке.
  */
  public boolean isErrors() {return (this.getErrorsCount() > 0);}

  public ArrayList<Exception> getExceptions() {
   return exceptions;
  }

  public void setExceptions(ArrayList<Exception> exceptions) {
   this.exceptions = exceptions;
  }
  
 }