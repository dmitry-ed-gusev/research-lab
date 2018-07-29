package jlib.utils.common;

import jlib.common.Consts;
import jlib.logging.InitLogger;
import org.apache.log4j.Logger;

import java.util.*;

/**
 Некоторые служебные методы (утилиты). Большинство методов являются статическими - не требуют создания экземпляра
 класса для запуска.
 @author Gusev Dmitry
 @version 3.1
*/

public class Utils
 {
  /* Компонент-логгер данного класса. */
  private static Logger logger = Logger.getLogger(Utils.class.getName());

  /**
   * Метод получает на входе строку формата [name1=value1[pairsDelim]....[pairsDelim]nameN=valueN], из которой формирует набор
   * свойств (Properties). При этом корректно обрабатываются разные отклонения от канонического формата:
   * [name1=name2=name3=value1] или [name1=value1=value2]- такие строки будут преобразованы в пару свойств
   * [name1 = name2] или [name1=value1] соответственно. pairsDelim - символ-разделитель для пар значений. Имя параметра
   * от его значения отделяется символом keyValueDelim - по умолчанию =. Если передана пустая исходная строка - метод
   * возвращает значение null. Если пуст символ pairsDelim, то используется значение по умолчанию - ; (константа
   * PAIRS_DELIM - модуль Consts).
   * @param str String исходная строка с набором свойств.
   * @param pairsDelim String символ-разделитель пар имя=значение.
   * @param keyValueDelim String символ-разделитель имени параметра и его значения.
   * @return Properties результирующий набор свойств, полученный из переданной строки.
  */
  public static Properties getPropsFromString(String str, String pairsDelim, String keyValueDelim)
   {
    Properties result = null;
    logger.debug("WORKING Utils.getPropsFromString().");
    logger.debug("RECEIVED: string: [" + str + "]; pairsDelim: [" + pairsDelim + "]; keyValueDelim: [" + keyValueDelim + "]");

    // Если строка не пустая - работаем дальше
    if ((str != null) && (!str.trim().equals("")))
     {

      // Выбор разделителя пар значений
      String localPairsDelim;
      if ((pairsDelim != null) && (!pairsDelim.trim().equals(""))) localPairsDelim = pairsDelim.trim();
      else localPairsDelim = Consts.PAIRS_DELIM;
      // Выбор разделителя имени параметра и его значения
      String localKeyValueDelim;
      if ((keyValueDelim != null) && (!keyValueDelim.trim().equals(""))) localKeyValueDelim = keyValueDelim.trim();
      else localKeyValueDelim = Consts.KEY_VALUE_DELIM;
      logger.debug("USED: pairsDelim: [" + localPairsDelim + "]; keyValuesDelim: [" + localKeyValueDelim + "].");

      String[] keyValueArray, keyValueSplitted;
      result = new Properties();
      // Разделяем строку на массив пар имя=значение. Пары значений разделяются символом <pairsDelim>, или, если нам не
      // передали этот символ, то символом по умолчанию - ; (PAIRS_DELIM). Нам необходимо получить значение ключа, которое
      // находится справа от самого первого слева знака pairsDelim (в строке 1=2=3;4=5;6=7 мы должны получить: key={1}, value={2=3;4=5;6=7}).
      keyValueArray = str.trim().split(localPairsDelim);
      
      // В цикле каждую пару имя=значение разбиваем на имя и значение
      for (String keyValuePair : keyValueArray)
       {

        //logger.debug("keyValuePair: " + keyValuePair); // <- вывод был необходим для отладки метода

        if ((keyValuePair != null) && (!keyValuePair.trim().equals("")))
         {
          // Значения разделяются символом localKeyValueDelim
          keyValueSplitted = keyValuePair.split(localKeyValueDelim);

          //if (keyValueSplitted.length >= 2)                                    // <- вывод был необходим для отладки метода
          // logger.debug("splitted pair: key=[" + keyValueSplitted[0] + "] " +  // <- вывод был необходим для отладки метода
          //              "value=[" + keyValueSplitted[1] + "]");                // <- вывод был необходим для отладки метода

          // Если мы получили несколько параметров (более 2-х как минимум), то первый из них - имя значения (name), а второй
          // - само значение (value), остальные парметры игнорируются (имя - параметр номер 0, значение - параметр номер 1).
          if ((keyValueSplitted.length >= 2) && (!keyValueSplitted[0].trim().equals("")) && (!keyValueSplitted[1].trim().equals("")))
           {

            //logger.debug("Result splitted pair: key=[" + keyValueSplitted[0] + "] " +  // <- вывод был необходим для отладки метода
            //             "value=[" + keyValueSplitted[1] + "]");                       // <- вывод был необходим для отладки метода

            result.put(keyValueSplitted[0].trim(), keyValueSplitted[1].trim());
           }
         }
       } // end of for
      
     } // end of if
    else {logger.debug("Source string is empty!");}
    
    return result;
   }

  /**
   * Данный метод выполняет действия, аналогичные действиям метода getPropsFromString(String, String, String). Отличие
   * в том, что в качестве разделителя пар значений и имени и значения в паре используются символы по умолчанию -
   * символ [;] для пар значений и символ [=] для имени и значения в паре.
   * @param str String исходная строка с набором свойств.
   * @return Properties результирующий набор свойств, полученный из переданной строки.
  */
  public static Properties getPropsFromString(String str) {return Utils.getPropsFromString(str, null, null);}

  /**
   * Метод получает на входе набор свойств (Properties), из которого формирует строку формата
   * [name1=value1[pairsDelim]...[pairsDelim]nameN=valueN]. pairsDelim - символ-разделитель для пар значений. Если не
   * указан - используется значение по умолчанию - ; (константа PAIRS_DELIM - модуль Consts). Имя параметра от его значения
   * отделяется символом keyValueDelim - по умолчанию =. Если переданный набор свойств (Properties) пуст - метод вернет
   * значение null.
   * @param props Properties набор свойств, который преобразуется в результирующую строку.
   * @param pairsDelim String символ-разделитель для пар имя/значение: name=value[DELIM]name=value.
   * @param keyValueDelim String символ-разделитель для имени и значения в паре имя/значение: name[KEY_VALUE_DELIM]value.
   * @return String строка, сформированная из полученного набора свойств.
  */
  public static String getStringFromProps(Properties props, String pairsDelim, String keyValueDelim)
   {
    StringBuilder result   = null;
    logger.debug("WORKING Utils.getStringFromProps().");
    logger.debug("RECEIVED: pairsDelim: [" + pairsDelim + "]; keyValueDelim: [" + keyValueDelim + "]");

    // Если Properties не пусто - то продолжаем работать
    if ((props != null) && (!props.isEmpty()))
     {
      String key;
      result = new StringBuilder();

      // Выбор разделителя пар значений
      String localPairsDelim;
      if ((pairsDelim != null) && (!pairsDelim.trim().equals(""))) localPairsDelim = pairsDelim.trim();
      else localPairsDelim = Consts.PAIRS_DELIM;
      // Выбор разделителя имени параметра и его значения
      String localKeyValueDelim;
      if ((keyValueDelim != null) && (!keyValueDelim.trim().equals(""))) localKeyValueDelim = keyValueDelim.trim();
      else localKeyValueDelim = Consts.KEY_VALUE_DELIM;
      logger.debug("USED PARAMS: pairsDelim: [" + localPairsDelim + "]; keyValuesDelim: [" + localKeyValueDelim + "].");

      // Проходим по всем парам имя/значение в наборе и формируем строку
      Enumeration e = props.keys();
      while (e.hasMoreElements())
       {
        key = e.nextElement().toString();
        // Пара имя=значение не добавляется к строке, если ключ пустой
        if (!key.trim().equals(""))
         {result.append(key).append(localKeyValueDelim).append(props.getProperty(key)).append(localPairsDelim);}
       }

      logger.debug("RESULT: [" + result.toString() + "].");
     }
    else logger.debug("Received Properties object is empty!");

    if (result == null) return null; else return result.toString();
   }

  /**
   * Метод получает на входе набор свойств (Properties), из которого формирует строку формата
   * [name1=value1[pairsDelim]...[pairsDelim]nameN=valueN]. pairsDelim - символ-разделитель для пар значений. Если не
   * указан - используется значение по умолчанию - ; (константа PAIRS_DELIM - модуль Consts). Имя параметра от его значения
   * отделяется символом keyValueDelim - по умолчанию =. Если переданный набор свойств (Properties) пуст - метод вернет
   * значение null.
   * @param map HashMap {String, String} набор свойств (хэшированная карта), который преобразуется в результирующую строку.
   * @param pairsDelim String символ-разделитель для пар имя/значение: name=value[DELIM]name=value.
   * @param keyValueDelim String символ-разделитель для имени и значения в паре имя/значение: name[KEY_VALUE_DELIM]value.
   * @return String строка, сформированная из полученного набора свойств.
  */
  public static String getStringFromProps(HashMap<String, String> map, String pairsDelim, String keyValueDelim)
   {
    StringBuilder result   = null;
    logger.debug("WORKING Utils.getStringFromProps().");
    logger.debug("RECEIVED: pairsDelim: [" + pairsDelim + "]; keyValueDelim: [" + keyValueDelim + "]");

    // Если Properties не пусто - то продолжаем работать
    if ((map != null) && (!map.isEmpty()))
     {
      result = new StringBuilder();

      // Выбор разделителя пар значений
      String localPairsDelim;
      if ((pairsDelim != null) && (!pairsDelim.trim().equals(""))) localPairsDelim = pairsDelim.trim();
      else localPairsDelim = Consts.PAIRS_DELIM;
      // Выбор разделителя имени параметра и его значения
      String localKeyValueDelim;
      if ((keyValueDelim != null) && (!keyValueDelim.trim().equals(""))) localKeyValueDelim = keyValueDelim.trim();
      else localKeyValueDelim = Consts.KEY_VALUE_DELIM;
      logger.debug("USED PARAMS: pairsDelim: [" + localPairsDelim + "]; keyValuesDelim: [" + localKeyValueDelim + "].");

      // Проходим по всем парам имя/значение в наборе и формируем строку. Разделитель пар имя=значение
      // не добавляется в конец строки (после последней пары)
      Set<String> keySet = map.keySet();
      for (String mapKey : keySet)
       {
        // Если ключ для значения пуст - ничего не выполняем
        if ((mapKey != null) && (!mapKey.trim().equals("")))
         {
          if (result.length() > 0) result.append(localPairsDelim);
          result.append(mapKey).append(localKeyValueDelim).append(map.get(mapKey));
         } 
       }

      logger.debug("RESULT: [" + result.toString() + "].");
     }
    else logger.debug("Received Properties object is empty!");

    if (result == null) return null; else return result.toString();
   }

  /**
   * Метод выполняет действия, аналогичные действиям метода getStringFromProps(String, String, String). Отличие в том,
   * что в качестве разделителя пар значений и имени и значения в паре используются символы по умолчанию - символ [;] для
   * пар значений и символ [=] для имени и знаечния в паре.
   * @param props Properties набор свойств, который преобразуется в результирующую строку.
   * @return String результирующая строка, полученная из набора свойств.
  */
  public static String getStringFromProps(Properties props) {return Utils.getStringFromProps(props, null, null);}

  /**
   * Метод возвращает строку, полученную из массива строк stringArray. Разные элементы массива в результирующей строке
   * разделяются символами valuesDelim. Если исходный массив пуст (null) или не содержит ни одного элемента - результирующая
   * строка также будет пуста. Если пуст символ-разделитель - valuesDelim - элементы в строке будут разделены символом
   * по умолчанию - VALUES_DELIM (,) - см. модуль Consts данной библиотеки.
   * @param stringArray String[] исходный массив для превращения в строку.
   * @param valuesDelim String разделитель элементов массива в результирующей строке.
   * @return String строка, полученная из массива строк.
  */
  public static String getStringFromArray(String[] stringArray, String valuesDelim)
   {
    logger.debug("WORKING Utils.getStringFromArray(String[], String).");
    StringBuilder result = null;

    // Если массив не пуст - работаем
    if ((stringArray != null) && (stringArray.length > 0))
     {
      result = new StringBuilder();
      for (int i = 0; i < stringArray.length; i++)
       {
        result.append(stringArray[i]);
        if (i < stringArray.length - 1)
         if ((valuesDelim != null) && (!valuesDelim.trim().equals(""))) result.append(valuesDelim);
         else result.append(Consts.VALUES_DELIM);
       }
      logger.debug("RESULT: " + result.toString());
     }
    else logger.debug("Source array is empty!");

    if (result == null) return null; else return result.toString();
   }

  /**
   * Метод возвращает строку, полученную из массива строк stringArray. Разные элементы массива в результирующей строке
   * разделяются символом по умолчанию - VALUES_DELIM (,) - см. модуль Consts данной библиотеки. Если исходный массив
   * пуст (null) или не содержит ни одного элемента - результирующая строка также будет пуста.
   * @param stringArray String[] исходный массив для превращения в строку.
   * @return String строка, полученная из массива строк.
  */
  public static String getStringFromArray(String[] stringArray)
   {
    logger.debug("WORKING Utils.getStringFromArray(String[]). Calling Utils.getStringFromArray(String[], String).");
    return Utils.getStringFromArray(stringArray, null);
   }

  /**
   Данная функция возвращает строку, содержащую текущие дату и время.
   Элементы времени и элементы даты разделены символами timeDelim и
   dateDelim соответственно.
   @param  timeDelim разделитель для элементов времени (ч,м,с).
   @param  dateDelim разделитель для элементов даты (д,м,г).
   @return строка со значением текущих системных времени и даты.
  */
  public static String getDateTimeString(String timeDelim, String dateDelim)
   {
    int num;
    StringBuilder res;
    logger.debug("WORKING Utils.getDateTimeString().");
    Calendar cal = new GregorianCalendar();

    // Выбор разделителя для даты
    String localDateDelim;
    if ((dateDelim != null) && (!dateDelim.trim().equals(""))) localDateDelim = dateDelim.trim();
    else localDateDelim = Consts.DATE_DEF_DELIM;
    // Выбор разделителя для времени
    String localTimeDelim;
    if ((timeDelim != null) && (!timeDelim.trim().equals(""))) localTimeDelim = timeDelim.trim();
    else localTimeDelim = Consts.TIME_DEF_DELIM;
    
    // Получаем строку с датой, используя другую функцию
    res = new StringBuilder(Utils.getDateString(localDateDelim)).append("  ");

    // Теперь формируем строку со временем
    num = cal.get(Calendar.HOUR_OF_DAY);
    if ((num > 0) && (num < 10)) res.append("0"); res.append(num).append(localTimeDelim);
    num = cal.get(Calendar.MINUTE);
    if ((num > 0) && (num < 10)) res.append("0"); res.append(num).append(localTimeDelim);
    num = cal.get(Calendar.SECOND);
    if ((num > 0) && (num < 10)) res.append("0"); res.append(num);
    logger.debug("RESULT: " + res.toString());
    return res.toString();
   }
  
  /**
   Возвращает строку со значением времени и даты (текущих). Элементы
   разделены символами по умолчанию.
   @return строка со значением текущих системных времени и даты.
  */
  public static String getDateTimeString() {return getDateTimeString(Consts.TIME_DEF_DELIM, Consts.DATE_DEF_DELIM);}

  /**
   * Возвращает строку, содержащую текущую дату, в формате dd[dateDelim]mm[dateDelim]yyyy. Элементы даты разделены
   * символом dateDelim. Если данный символ не указан, то используется символ по умолчанию - константа Consts.DATE_DEF_DELIM.
   * @param  dateDelim разделитель для элементов даты (д,м,г).
   * @return String строка со значением системной даты.
  */
  public static String getDateString(String dateDelim)
   {
    logger.debug("WORKING Utils.getDateString().");
    int num;
    StringBuilder res = new StringBuilder();
    Calendar      cal = new GregorianCalendar();

    // Выбор разделителя для даты
    String localDateDelim;
    if ((dateDelim != null) && (!dateDelim.trim().equals(""))) localDateDelim = dateDelim.trim();
    else localDateDelim = Consts.DATE_DEF_DELIM;
    
    num = cal.get(Calendar.DAY_OF_MONTH);
    if ((num > 0) && (num < 10)) res.append("0").append(num).append(localDateDelim);
    else                         res.append(num).append(localDateDelim);

    num = cal.get(Calendar.MONTH) + 1; //<-Получаем значение от 0 до 11, поэтому +1.
    if ((num > 0) && (num < 10)) res.append("0"); 
    res.append(num).append(localDateDelim).append(cal.get(Calendar.YEAR));
    logger.debug("RESULT: " + res.toString());
    return res.toString();
   }
  
  /**
   * Возвращает строку, содержащую текущую дату. Элементы даты разделены символами по умолчанию.
   * @return строка со значением системной даты.
  */
  public static String getDateString() {return getDateString(Consts.DATE_DEF_DELIM);}

  /**
   * Метод возвращает строку, которая содержит список всех доступных локалей, разделенных запятыми в строке (CSV-список).
   * @return String список локалей, значения в котором разделены запятыми.
  */
  public static String getCSVLocalesList()
   {
    logger.debug("WORKING Utils.getCSVLocalesList().");
    StringBuilder list = null;

    // Получение списка всех доступных локалей
    Locale[] locales = Locale.getAvailableLocales();
    // Если список локалей не пуст - проходим по списку и формируем результат
    if ((locales != null) && (locales.length > 0))
     {
      list = new StringBuilder();
      for (int i=0; i < locales.length; i++)
       {
        // Get the 2-letter language code
        String language = locales[i].getLanguage();
        // Get the 2-letter country code; may be equal to ""
        String country = locales[i].getCountry();
        // Get localized name suitable for display to the user
        String locName = locales[i].getDisplayName();
        list.append(language).append("_").append(country).append("_").append(locName);
        if (i < locales.length - 1) list.append(",");
       }
     }

    if (list == null) return null; else return list.toString();
   }

  /**
   * Данный метод разбивает строку stringForSplitting на части согласно регулярному выражению regex, но в отличие от
   * стандартной функции split в результирующий массив String[] не попадают пустые строки. Если исходная строка пуста
   * - имеет значение null или пустое значение - метод возвращает значение null. Если после обработки массив пуст - метод
   * возвращает значение null. Т.о. метод возвращает либо значение null, либо массив длины больше нуля (минимум с
   * одним элементом).
   * @param stringForSplitting String строка, из которой пытаемся получить массив.
   * @param regex String регулярное выражение, на основании которого строка будет разбиваться на части.
   * @return String[] результирующий массив, полученный из исходной строки.
  */
  public static String[] trimSplit(String stringForSplitting, String regex)
   {
    logger.debug("WORKING Utils.trimSplit.");
    String[] result = null;
    // Если исходная строка не пуста и регулярное выражение тоже - выполняем обработку
    if ((stringForSplitting != null) && (!stringForSplitting.trim().equals("")) && (regex != null) && (!regex.trim().equals("")))
     {
      result = stringForSplitting.trim().split(regex);
      StringBuilder resultString = new StringBuilder();
      // Обработка, во время которой убираем пустые строки из результирующего массива
      for (int i = 0; i < result.length; i++)
       if (!result[i].trim().equals(""))
        {resultString.append(result[i].trim()); if (i < result.length - 1) resultString.append(regex);}
      logger.debug("Result of trim: [" + resultString.toString() + "]");
      // Если в результате обработки получилась пустая строка - результат будет null
      if (resultString.toString().trim().equals("")) result = null;
      else                                           result = resultString.toString().trim().split(regex);
     }
    // Если исходная строка пуста (или регулярное выражение) - сообщаем об этом, обработку не производим
    else logger.debug("Source string is empty!");
    return result;
   }

  /**
   * Метод main нужен только для тестирования данного класса.
   * @param args String[] список параметров метода main.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger(Utils.class.getName());

    logger.info(Utils.getDateTimeString());
    /*
    Properties props = new Properties();
    props.setProperty("1", "2");
    props.setProperty("3", "4");
    props.setProperty("", "6");
    logger.info("RESULT: " + Utils.getStringFromProps(props));
    */

    //logger.debug("RESULT: " + Utils.getStringFromProps(Utils.getPropsFromString(";==;;3==4;=5;;;1=2;;", ";", "=")));

    //HashMap<String, String> map = new HashMap<String, String>();
    //map.put("1", "2");
    //map.put("3", "4");
    //map.put("5", null);
    //logger.info("RESULT: " + Utils.getStringFromProps(map, "&", "="));
   }

 }