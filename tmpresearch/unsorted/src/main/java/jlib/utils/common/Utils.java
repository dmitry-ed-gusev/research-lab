package jlib.utils.common;

import jlib.common.Consts;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

/**
 * Некоторые служебные методы (утилиты). Большинство методов являются статическими - не требуют создания экземпляра
 * класса для запуска.
 *
 * @author Gusev Dmitry
 * @version 3.1
 */

public class Utils {




    /**
     * Метод получает на входе набор свойств (Properties), из которого формирует строку формата
     * [name1=value1[pairsDelim]...[pairsDelim]nameN=valueN]. pairsDelim - символ-разделитель для пар значений. Если не
     * указан - используется значение по умолчанию - ; (константа PAIRS_DELIM - модуль Consts). Имя параметра от его значения
     * отделяется символом keyValueDelim - по умолчанию =. Если переданный набор свойств (Properties) пуст - метод вернет
     * значение null.
     *
     * @param map           HashMap {String, String} набор свойств (хэшированная карта), который преобразуется в результирующую строку.
     * @param pairsDelim    String символ-разделитель для пар имя/значение: name=value[DELIM]name=value.
     * @param keyValueDelim String символ-разделитель для имени и значения в паре имя/значение: name[KEY_VALUE_DELIM]value.
     * @return String строка, сформированная из полученного набора свойств.
     */
    /*
    public static String getStringFromProps_(HashMap<String, String> map, String pairsDelim, String keyValueDelim) {
        StringBuilder result = null;
        logger.debug("WORKING Utils.getStringFromProps().");
        logger.debug("RECEIVED: pairsDelim: [" + pairsDelim + "]; keyValueDelim: [" + keyValueDelim + "]");

        // Если Properties не пусто - то продолжаем работать
        if ((map != null) && (!map.isEmpty())) {
            result = new StringBuilder();

            // Выбор разделителя пар значений
            String localPairsDelim;
            if ((pairsDelim != null) && (!pairsDelim.trim().equals(""))) localPairsDelim = pairsDelim.trim();
            else localPairsDelim = Consts.PAIRS_DELIM;
            // Выбор разделителя имени параметра и его значения
            String localKeyValueDelim;
            if ((keyValueDelim != null) && (!keyValueDelim.trim().equals("")))
                localKeyValueDelim = keyValueDelim.trim();
            else localKeyValueDelim = Consts.KEY_VALUE_DELIM;
            logger.debug("USED PARAMS: pairsDelim: [" + localPairsDelim + "]; keyValuesDelim: [" + localKeyValueDelim + "].");

            // Проходим по всем парам имя/значение в наборе и формируем строку. Разделитель пар имя=значение
            // не добавляется в конец строки (после последней пары)
            Set<String> keySet = map.keySet();
            for (String mapKey : keySet) {
                // Если ключ для значения пуст - ничего не выполняем
                if ((mapKey != null) && (!mapKey.trim().equals(""))) {
                    if (result.length() > 0) result.append(localPairsDelim);
                    result.append(mapKey).append(localKeyValueDelim).append(map.get(mapKey));
                }
            }

            logger.debug("RESULT: [" + result.toString() + "].");
        } else logger.debug("Received Properties object is empty!");

        if (result == null) return null;
        else return result.toString();
    }
     */

    /**
     * Метод возвращает строку, полученную из массива строк stringArray. Разные элементы массива в результирующей строке
     * разделяются символами valuesDelim. Если исходный массив пуст (null) или не содержит ни одного элемента - результирующая
     * строка также будет пуста. Если пуст символ-разделитель - valuesDelim - элементы в строке будут разделены символом
     * по умолчанию - VALUES_DELIM (,) - см. модуль Consts данной библиотеки.
     *
     * @param stringArray String[] исходный массив для превращения в строку.
     * @param valuesDelim String разделитель элементов массива в результирующей строке.
     * @return String строка, полученная из массива строк.
     */
    /*
    public static String getStringFromArray(String[] stringArray, String valuesDelim) {
        logger.debug("WORKING Utils.getStringFromArray(String[], String).");
        StringBuilder result = null;

        // Если массив не пуст - работаем
        if ((stringArray != null) && (stringArray.length > 0)) {
            result = new StringBuilder();
            for (int i = 0; i < stringArray.length; i++) {
                result.append(stringArray[i]);
                if (i < stringArray.length - 1)
                    if ((valuesDelim != null) && (!valuesDelim.trim().equals(""))) result.append(valuesDelim);
                    else result.append(Consts.VALUES_DELIM);
            }
            logger.debug("RESULT: " + result.toString());
        } else logger.debug("Source array is empty!");

        if (result == null) return null;
        else return result.toString();
    }
    */

}