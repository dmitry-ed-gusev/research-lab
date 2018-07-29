package jlib.utils.common;

import jlib.common.Consts;
import jlib.logging.InitLogger;
import org.apache.log4j.Logger;

import java.util.*;

/**
 ��������� ��������� ������ (�������). ����������� ������� �������� ������������ - �� ������� �������� ����������
 ������ ��� �������.
 @author Gusev Dmitry
 @version 3.1
*/

public class Utils
 {
  /* ���������-������ ������� ������. */
  private static Logger logger = Logger.getLogger(Utils.class.getName());

  /**
   * ����� �������� �� ����� ������ ������� [name1=value1[pairsDelim]....[pairsDelim]nameN=valueN], �� ������� ��������� �����
   * ������� (Properties). ��� ���� ��������� �������������� ������ ���������� �� ������������� �������:
   * [name1=name2=name3=value1] ��� [name1=value1=value2]- ����� ������ ����� ������������� � ���� �������
   * [name1 = name2] ��� [name1=value1] ��������������. pairsDelim - ������-����������� ��� ��� ��������. ��� ���������
   * �� ��� �������� ���������� �������� keyValueDelim - �� ��������� =. ���� �������� ������ �������� ������ - �����
   * ���������� �������� null. ���� ���� ������ pairsDelim, �� ������������ �������� �� ��������� - ; (���������
   * PAIRS_DELIM - ������ Consts).
   * @param str String �������� ������ � ������� �������.
   * @param pairsDelim String ������-����������� ��� ���=��������.
   * @param keyValueDelim String ������-����������� ����� ��������� � ��� ��������.
   * @return Properties �������������� ����� �������, ���������� �� ���������� ������.
  */
  public static Properties getPropsFromString(String str, String pairsDelim, String keyValueDelim)
   {
    Properties result = null;
    logger.debug("WORKING Utils.getPropsFromString().");
    logger.debug("RECEIVED: string: [" + str + "]; pairsDelim: [" + pairsDelim + "]; keyValueDelim: [" + keyValueDelim + "]");

    // ���� ������ �� ������ - �������� ������
    if ((str != null) && (!str.trim().equals("")))
     {

      // ����� ����������� ��� ��������
      String localPairsDelim;
      if ((pairsDelim != null) && (!pairsDelim.trim().equals(""))) localPairsDelim = pairsDelim.trim();
      else localPairsDelim = Consts.PAIRS_DELIM;
      // ����� ����������� ����� ��������� � ��� ��������
      String localKeyValueDelim;
      if ((keyValueDelim != null) && (!keyValueDelim.trim().equals(""))) localKeyValueDelim = keyValueDelim.trim();
      else localKeyValueDelim = Consts.KEY_VALUE_DELIM;
      logger.debug("USED: pairsDelim: [" + localPairsDelim + "]; keyValuesDelim: [" + localKeyValueDelim + "].");

      String[] keyValueArray, keyValueSplitted;
      result = new Properties();
      // ��������� ������ �� ������ ��� ���=��������. ���� �������� ����������� �������� <pairsDelim>, ���, ���� ��� ��
      // �������� ���� ������, �� �������� �� ��������� - ; (PAIRS_DELIM). ��� ���������� �������� �������� �����, �������
      // ��������� ������ �� ������ ������� ����� ����� pairsDelim (� ������ 1=2=3;4=5;6=7 �� ������ ��������: key={1}, value={2=3;4=5;6=7}).
      keyValueArray = str.trim().split(localPairsDelim);
      
      // � ����� ������ ���� ���=�������� ��������� �� ��� � ��������
      for (String keyValuePair : keyValueArray)
       {

        //logger.debug("keyValuePair: " + keyValuePair); // <- ����� ��� ��������� ��� ������� ������

        if ((keyValuePair != null) && (!keyValuePair.trim().equals("")))
         {
          // �������� ����������� �������� localKeyValueDelim
          keyValueSplitted = keyValuePair.split(localKeyValueDelim);

          //if (keyValueSplitted.length >= 2)                                    // <- ����� ��� ��������� ��� ������� ������
          // logger.debug("splitted pair: key=[" + keyValueSplitted[0] + "] " +  // <- ����� ��� ��������� ��� ������� ������
          //              "value=[" + keyValueSplitted[1] + "]");                // <- ����� ��� ��������� ��� ������� ������

          // ���� �� �������� ��������� ���������� (����� 2-� ��� �������), �� ������ �� ��� - ��� �������� (name), � ������
          // - ���� �������� (value), ��������� �������� ������������ (��� - �������� ����� 0, �������� - �������� ����� 1).
          if ((keyValueSplitted.length >= 2) && (!keyValueSplitted[0].trim().equals("")) && (!keyValueSplitted[1].trim().equals("")))
           {

            //logger.debug("Result splitted pair: key=[" + keyValueSplitted[0] + "] " +  // <- ����� ��� ��������� ��� ������� ������
            //             "value=[" + keyValueSplitted[1] + "]");                       // <- ����� ��� ��������� ��� ������� ������

            result.put(keyValueSplitted[0].trim(), keyValueSplitted[1].trim());
           }
         }
       } // end of for
      
     } // end of if
    else {logger.debug("Source string is empty!");}
    
    return result;
   }

  /**
   * ������ ����� ��������� ��������, ����������� ��������� ������ getPropsFromString(String, String, String). �������
   * � ���, ��� � �������� ����������� ��� �������� � ����� � �������� � ���� ������������ ������� �� ��������� -
   * ������ [;] ��� ��� �������� � ������ [=] ��� ����� � �������� � ����.
   * @param str String �������� ������ � ������� �������.
   * @return Properties �������������� ����� �������, ���������� �� ���������� ������.
  */
  public static Properties getPropsFromString(String str) {return Utils.getPropsFromString(str, null, null);}

  /**
   * ����� �������� �� ����� ����� ������� (Properties), �� �������� ��������� ������ �������
   * [name1=value1[pairsDelim]...[pairsDelim]nameN=valueN]. pairsDelim - ������-����������� ��� ��� ��������. ���� ��
   * ������ - ������������ �������� �� ��������� - ; (��������� PAIRS_DELIM - ������ Consts). ��� ��������� �� ��� ��������
   * ���������� �������� keyValueDelim - �� ��������� =. ���� ���������� ����� ������� (Properties) ���� - ����� ������
   * �������� null.
   * @param props Properties ����� �������, ������� ������������� � �������������� ������.
   * @param pairsDelim String ������-����������� ��� ��� ���/��������: name=value[DELIM]name=value.
   * @param keyValueDelim String ������-����������� ��� ����� � �������� � ���� ���/��������: name[KEY_VALUE_DELIM]value.
   * @return String ������, �������������� �� ����������� ������ �������.
  */
  public static String getStringFromProps(Properties props, String pairsDelim, String keyValueDelim)
   {
    StringBuilder result   = null;
    logger.debug("WORKING Utils.getStringFromProps().");
    logger.debug("RECEIVED: pairsDelim: [" + pairsDelim + "]; keyValueDelim: [" + keyValueDelim + "]");

    // ���� Properties �� ����� - �� ���������� ��������
    if ((props != null) && (!props.isEmpty()))
     {
      String key;
      result = new StringBuilder();

      // ����� ����������� ��� ��������
      String localPairsDelim;
      if ((pairsDelim != null) && (!pairsDelim.trim().equals(""))) localPairsDelim = pairsDelim.trim();
      else localPairsDelim = Consts.PAIRS_DELIM;
      // ����� ����������� ����� ��������� � ��� ��������
      String localKeyValueDelim;
      if ((keyValueDelim != null) && (!keyValueDelim.trim().equals(""))) localKeyValueDelim = keyValueDelim.trim();
      else localKeyValueDelim = Consts.KEY_VALUE_DELIM;
      logger.debug("USED PARAMS: pairsDelim: [" + localPairsDelim + "]; keyValuesDelim: [" + localKeyValueDelim + "].");

      // �������� �� ���� ����� ���/�������� � ������ � ��������� ������
      Enumeration e = props.keys();
      while (e.hasMoreElements())
       {
        key = e.nextElement().toString();
        // ���� ���=�������� �� ����������� � ������, ���� ���� ������
        if (!key.trim().equals(""))
         {result.append(key).append(localKeyValueDelim).append(props.getProperty(key)).append(localPairsDelim);}
       }

      logger.debug("RESULT: [" + result.toString() + "].");
     }
    else logger.debug("Received Properties object is empty!");

    if (result == null) return null; else return result.toString();
   }

  /**
   * ����� �������� �� ����� ����� ������� (Properties), �� �������� ��������� ������ �������
   * [name1=value1[pairsDelim]...[pairsDelim]nameN=valueN]. pairsDelim - ������-����������� ��� ��� ��������. ���� ��
   * ������ - ������������ �������� �� ��������� - ; (��������� PAIRS_DELIM - ������ Consts). ��� ��������� �� ��� ��������
   * ���������� �������� keyValueDelim - �� ��������� =. ���� ���������� ����� ������� (Properties) ���� - ����� ������
   * �������� null.
   * @param map HashMap {String, String} ����� ������� (������������ �����), ������� ������������� � �������������� ������.
   * @param pairsDelim String ������-����������� ��� ��� ���/��������: name=value[DELIM]name=value.
   * @param keyValueDelim String ������-����������� ��� ����� � �������� � ���� ���/��������: name[KEY_VALUE_DELIM]value.
   * @return String ������, �������������� �� ����������� ������ �������.
  */
  public static String getStringFromProps(HashMap<String, String> map, String pairsDelim, String keyValueDelim)
   {
    StringBuilder result   = null;
    logger.debug("WORKING Utils.getStringFromProps().");
    logger.debug("RECEIVED: pairsDelim: [" + pairsDelim + "]; keyValueDelim: [" + keyValueDelim + "]");

    // ���� Properties �� ����� - �� ���������� ��������
    if ((map != null) && (!map.isEmpty()))
     {
      result = new StringBuilder();

      // ����� ����������� ��� ��������
      String localPairsDelim;
      if ((pairsDelim != null) && (!pairsDelim.trim().equals(""))) localPairsDelim = pairsDelim.trim();
      else localPairsDelim = Consts.PAIRS_DELIM;
      // ����� ����������� ����� ��������� � ��� ��������
      String localKeyValueDelim;
      if ((keyValueDelim != null) && (!keyValueDelim.trim().equals(""))) localKeyValueDelim = keyValueDelim.trim();
      else localKeyValueDelim = Consts.KEY_VALUE_DELIM;
      logger.debug("USED PARAMS: pairsDelim: [" + localPairsDelim + "]; keyValuesDelim: [" + localKeyValueDelim + "].");

      // �������� �� ���� ����� ���/�������� � ������ � ��������� ������. ����������� ��� ���=��������
      // �� ����������� � ����� ������ (����� ��������� ����)
      Set<String> keySet = map.keySet();
      for (String mapKey : keySet)
       {
        // ���� ���� ��� �������� ���� - ������ �� ���������
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
   * ����� ��������� ��������, ����������� ��������� ������ getStringFromProps(String, String, String). ������� � ���,
   * ��� � �������� ����������� ��� �������� � ����� � �������� � ���� ������������ ������� �� ��������� - ������ [;] ���
   * ��� �������� � ������ [=] ��� ����� � �������� � ����.
   * @param props Properties ����� �������, ������� ������������� � �������������� ������.
   * @return String �������������� ������, ���������� �� ������ �������.
  */
  public static String getStringFromProps(Properties props) {return Utils.getStringFromProps(props, null, null);}

  /**
   * ����� ���������� ������, ���������� �� ������� ����� stringArray. ������ �������� ������� � �������������� ������
   * ����������� ��������� valuesDelim. ���� �������� ������ ���� (null) ��� �� �������� �� ������ �������� - ��������������
   * ������ ����� ����� �����. ���� ���� ������-����������� - valuesDelim - �������� � ������ ����� ��������� ��������
   * �� ��������� - VALUES_DELIM (,) - ��. ������ Consts ������ ����������.
   * @param stringArray String[] �������� ������ ��� ����������� � ������.
   * @param valuesDelim String ����������� ��������� ������� � �������������� ������.
   * @return String ������, ���������� �� ������� �����.
  */
  public static String getStringFromArray(String[] stringArray, String valuesDelim)
   {
    logger.debug("WORKING Utils.getStringFromArray(String[], String).");
    StringBuilder result = null;

    // ���� ������ �� ���� - ��������
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
   * ����� ���������� ������, ���������� �� ������� ����� stringArray. ������ �������� ������� � �������������� ������
   * ����������� �������� �� ��������� - VALUES_DELIM (,) - ��. ������ Consts ������ ����������. ���� �������� ������
   * ���� (null) ��� �� �������� �� ������ �������� - �������������� ������ ����� ����� �����.
   * @param stringArray String[] �������� ������ ��� ����������� � ������.
   * @return String ������, ���������� �� ������� �����.
  */
  public static String getStringFromArray(String[] stringArray)
   {
    logger.debug("WORKING Utils.getStringFromArray(String[]). Calling Utils.getStringFromArray(String[], String).");
    return Utils.getStringFromArray(stringArray, null);
   }

  /**
   ������ ������� ���������� ������, ���������� ������� ���� � �����.
   �������� ������� � �������� ���� ��������� ��������� timeDelim �
   dateDelim ��������������.
   @param  timeDelim ����������� ��� ��������� ������� (�,�,�).
   @param  dateDelim ����������� ��� ��������� ���� (�,�,�).
   @return ������ �� ��������� ������� ��������� ������� � ����.
  */
  public static String getDateTimeString(String timeDelim, String dateDelim)
   {
    int num;
    StringBuilder res;
    logger.debug("WORKING Utils.getDateTimeString().");
    Calendar cal = new GregorianCalendar();

    // ����� ����������� ��� ����
    String localDateDelim;
    if ((dateDelim != null) && (!dateDelim.trim().equals(""))) localDateDelim = dateDelim.trim();
    else localDateDelim = Consts.DATE_DEF_DELIM;
    // ����� ����������� ��� �������
    String localTimeDelim;
    if ((timeDelim != null) && (!timeDelim.trim().equals(""))) localTimeDelim = timeDelim.trim();
    else localTimeDelim = Consts.TIME_DEF_DELIM;
    
    // �������� ������ � �����, ��������� ������ �������
    res = new StringBuilder(Utils.getDateString(localDateDelim)).append("  ");

    // ������ ��������� ������ �� ��������
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
   ���������� ������ �� ��������� ������� � ���� (�������). ��������
   ��������� ��������� �� ���������.
   @return ������ �� ��������� ������� ��������� ������� � ����.
  */
  public static String getDateTimeString() {return getDateTimeString(Consts.TIME_DEF_DELIM, Consts.DATE_DEF_DELIM);}

  /**
   * ���������� ������, ���������� ������� ����, � ������� dd[dateDelim]mm[dateDelim]yyyy. �������� ���� ���������
   * �������� dateDelim. ���� ������ ������ �� ������, �� ������������ ������ �� ��������� - ��������� Consts.DATE_DEF_DELIM.
   * @param  dateDelim ����������� ��� ��������� ���� (�,�,�).
   * @return String ������ �� ��������� ��������� ����.
  */
  public static String getDateString(String dateDelim)
   {
    logger.debug("WORKING Utils.getDateString().");
    int num;
    StringBuilder res = new StringBuilder();
    Calendar      cal = new GregorianCalendar();

    // ����� ����������� ��� ����
    String localDateDelim;
    if ((dateDelim != null) && (!dateDelim.trim().equals(""))) localDateDelim = dateDelim.trim();
    else localDateDelim = Consts.DATE_DEF_DELIM;
    
    num = cal.get(Calendar.DAY_OF_MONTH);
    if ((num > 0) && (num < 10)) res.append("0").append(num).append(localDateDelim);
    else                         res.append(num).append(localDateDelim);

    num = cal.get(Calendar.MONTH) + 1; //<-�������� �������� �� 0 �� 11, ������� +1.
    if ((num > 0) && (num < 10)) res.append("0"); 
    res.append(num).append(localDateDelim).append(cal.get(Calendar.YEAR));
    logger.debug("RESULT: " + res.toString());
    return res.toString();
   }
  
  /**
   * ���������� ������, ���������� ������� ����. �������� ���� ��������� ��������� �� ���������.
   * @return ������ �� ��������� ��������� ����.
  */
  public static String getDateString() {return getDateString(Consts.DATE_DEF_DELIM);}

  /**
   * ����� ���������� ������, ������� �������� ������ ���� ��������� �������, ����������� �������� � ������ (CSV-������).
   * @return String ������ �������, �������� � ������� ��������� ��������.
  */
  public static String getCSVLocalesList()
   {
    logger.debug("WORKING Utils.getCSVLocalesList().");
    StringBuilder list = null;

    // ��������� ������ ���� ��������� �������
    Locale[] locales = Locale.getAvailableLocales();
    // ���� ������ ������� �� ���� - �������� �� ������ � ��������� ���������
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
   * ������ ����� ��������� ������ stringForSplitting �� ����� �������� ����������� ��������� regex, �� � ������� ��
   * ����������� ������� split � �������������� ������ String[] �� �������� ������ ������. ���� �������� ������ �����
   * - ����� �������� null ��� ������ �������� - ����� ���������� �������� null. ���� ����� ��������� ������ ���� - �����
   * ���������� �������� null. �.�. ����� ���������� ���� �������� null, ���� ������ ����� ������ ���� (������� �
   * ����� ���������).
   * @param stringForSplitting String ������, �� ������� �������� �������� ������.
   * @param regex String ���������� ���������, �� ��������� �������� ������ ����� ����������� �� �����.
   * @return String[] �������������� ������, ���������� �� �������� ������.
  */
  public static String[] trimSplit(String stringForSplitting, String regex)
   {
    logger.debug("WORKING Utils.trimSplit.");
    String[] result = null;
    // ���� �������� ������ �� ����� � ���������� ��������� ���� - ��������� ���������
    if ((stringForSplitting != null) && (!stringForSplitting.trim().equals("")) && (regex != null) && (!regex.trim().equals("")))
     {
      result = stringForSplitting.trim().split(regex);
      StringBuilder resultString = new StringBuilder();
      // ���������, �� ����� ������� ������� ������ ������ �� ��������������� �������
      for (int i = 0; i < result.length; i++)
       if (!result[i].trim().equals(""))
        {resultString.append(result[i].trim()); if (i < result.length - 1) resultString.append(regex);}
      logger.debug("Result of trim: [" + resultString.toString() + "]");
      // ���� � ���������� ��������� ���������� ������ ������ - ��������� ����� null
      if (resultString.toString().trim().equals("")) result = null;
      else                                           result = resultString.toString().trim().split(regex);
     }
    // ���� �������� ������ ����� (��� ���������� ���������) - �������� �� ����, ��������� �� ����������
    else logger.debug("Source string is empty!");
    return result;
   }

  /**
   * ����� main ����� ������ ��� ������������ ������� ������.
   * @param args String[] ������ ���������� ������ main.
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