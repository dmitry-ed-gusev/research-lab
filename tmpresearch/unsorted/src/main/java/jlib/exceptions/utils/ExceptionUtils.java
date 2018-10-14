package jlib.exceptions.utils;

/**
 * Класс содеержит утилиты (методы) для работы с ИС.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 13.07.2009)
*/

public class ExceptionUtils
 {
  
  /**
   * Метод формирует сообщение (строковое) для переданной ему ИС. Сообщение состоит из имени класса ИС и сообщения ИС.
   * Если переданная ИС пуста - метод вернет соответствующее сообщение.
   * @param e Exception ИС для формирования сообщения.
  */
  public static String getExceptionMessage(Exception e)
   {
    String result;
    if (e != null) {result = "Exception class [" + e.getClass().getName() + "]. Exception message [" + e.getMessage() + "].";}
    else           {result = "Exception is NULL!";}
    return result;
   }

 }