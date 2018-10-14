package jlib.utils.string;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Данный модуль содержит различные утилитарные методы для работы со строками.
 * @author Gusev Dmitry (Гусев Дмитрий)
 * @version 1.0 (DATE: 24.02.11)
*/

public class StrUtilities
 {
  private static Logger logger = Logger.getLogger("jlib");

  /**
   * Метод формирует имя (строковое) фиксированной длины (параметр lenght) на основе указанного в параметре
   * name имени. Указанное имя дополняется лидирующими символами, указанными в парметре symbol. Параметр lenght
   * обязательно должен быть положительным и не равным 0, иначе метод вернет значение null. Еще одно ограничение на
   * значение параметра lenght: значение данного параметра должно быть больше (строго больше) длины значения параметра
   * name - в противном случае метод не выполнит никаких действий и вернет значение параметра name (имеется в виду случай
   * непустого параметра name, при пустом name метод вернет null). Параметр symbol должен содержать отображаемый символ,
   * в противном случае результирующее имя может вызывать крах различных модулей. Параметр name должен быть непустым (и
   * не состоять из одних символов пробела, табуляции и т.п.), если же он пуст, то метод верент значение null.
   * @param lenght int необходимая длина результирующего имени.
   * @param symbol char символ, который добавляется к полученному имени name для достижения необходимой длины lenght.
   * @param name String строковое имя, которое необходимо дополнить до необходимой длины.
   * @return String результирующее строковое имя или значение null.
  */
  public static String getFixedLengthName(int lenght, char symbol, String name)
   {
    String result = null;
    // Проверяем параметр name
    if (!StringUtils.isBlank(name))
     {
      logger.debug("Name parameter is OK. Processing name [" + name + "].");
      // Проверяем параметр lenght (он должен быть положителен и не равен 0)
      if (lenght > 0)
       {
        logger.debug("Lenght [" + lenght + "] is OK. Processing.");
        // Действия выполняем только при значении lenght > name.lenght
        if (lenght > name.length())
         {
          StringBuilder resultName = new StringBuilder();
          for (int i = 0; i < (lenght - name.length()); i++) {resultName.append(symbol);}
          resultName.append(name);
          // Присвоение значения результату
          result = resultName.toString();
        }
        // Значение lenght <= name.lenght
        else {result = name;}
       }
      // Параметр lenght не подошел
      else {logger.error("Wrong lenght [" + lenght + "]!");}
     }
    // Параметр name пуст - сообщим об ошибке!
    else {logger.error("Name parameter is empty!");}
    // Возвращаем результат
    return result;
   }

  /**
   * Метод формирует имя (строковое) фиксированной длины (параметр lenght) на основе указанного в параметре
   * name имени (указывается целое положительное число). Указанное имя дополняется лидирующими символами, указанными в
   * парметре symbol. Параметр lenght обязательно должен быть положительным и не равным 0, иначе метод вернет значение null.
   * Еще одно ограничение на значение параметра lenght: значение данного параметра должно быть больше (строго больше) длины
   * значения параметра name (в символах) - в противном случае метод не выполнит никаких действий и вернет значение параметра name
   * (имеется в виду случай непустого параметра name, при пустом name метод вернет null). Параметр symbol должен содержать
   * отображаемый символ, в противном случае результирующее имя может вызывать крах различных модулей. Параметр name должен быть
   * целым положительным числом (строго больше нуля), в противном случае метод вернет значение null.
   * @param lenght int необходимая длина результирующего имени.
   * @param symbol char символ, который добавляется к полученному имени name для достижения необходимой длины lenght.
   * @param name String строковое имя, которое необходимо дополнить до необходимой длины.
   * @return String результирующее строковое имя или значение null.
  */
  public static String getFixedLengthName(int lenght, char symbol, int name)
   {
    String result = null;
    if (name > 0) {result = StrUtilities.getFixedLengthName(lenght, symbol, String.valueOf(name));}
    else {logger.error("Name value must be strictly greater than 0!");}
    return result;
   }

 }