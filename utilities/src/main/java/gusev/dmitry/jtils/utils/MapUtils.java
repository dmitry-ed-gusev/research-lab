package gusev.dmitry.jtils.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static gusev.dmitry.jtils.utils.MapUtils.SortType.ASC;

/**
 * Utilities for sorting maps.
 * Created by gusevdm on 6/13/2017.
 */

// https://www.mkyong.com/java/how-to-sort-a-map-in-java/
// https://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values-java

public final class MapUtils {

    private static final Log LOG = LogFactory.getLog(MapUtils.class);

    /** Sort type ASC/DESC. */
    public enum SortType {
        ASC, DESC
    }

    private MapUtils() { // non-instanceable
    }

    /**
     * Return top of map as a string. If map is null/empty - return null.
     * If count <= 0 or >= input map size - return a whole map.
     */
    public static <K, V> String getTopFromMap(Map<K, V> map, int topCount) {
        LOG.debug("IPinYou.getTopFromMap() is working.");

        if (map == null || map.isEmpty()) { // fast checks for map (and return)
            return null;
        }

        int upperBound;
        if (topCount <= 0 || topCount >= map.size()) { // fast checks for count
            upperBound = map.size();
        } else {
            upperBound = topCount;
        }

        int counter = 0;
        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        Map.Entry<K, V> entry;

        // iterate over map and convert it to string
        while (iterator.hasNext() && counter < upperBound) {
            entry = iterator.next();
            builder.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
            counter++;
        }

        return builder.toString();
    }

    /***/
    /*
    public static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        //classic iterator example
//        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
//            Map.Entry<String, Integer> entry = it.next();
//            sortedMap.put(entry.getKey(), entry.getValue());
//        }

        return sortedMap;
    }
    */

    /**
     * Sort input Map by values. Map values should be comparable. Method uses generics.
     * Warning! Resulting type of map is LinkedHashMap (method may return different map type/implementation!)
     * todo: add a parameter for selecting order
     * todo: method changes the source map!!! add a key - change or not source map?
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map, SortType type) {

        if (map == null) { // fast check and return null
            return null;
        }

        Map<K, V> result = new LinkedHashMap<>();
        if (map.isEmpty()) { // if source empty - return empty result
            return result;
        }
        if (map.size() == 1) { // if source size is 1 - don't sort
            result.putAll(map);
            return result;
        }

        if (type == null) { // fail-fast check
            throw new IllegalArgumentException("Sort type mustn't be NULL!");
        }

        // convert map to list of entries <Key, Value>
        //List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());

        // just move all map entries to list (modify source map, consume memory)
        List<Map.Entry<K, V>> list = new LinkedList<>();
        Iterator<Map.Entry<K, V>> sourceMapIterator = map.entrySet().iterator();
        //Map.Entry<K, V> sourceMapEntry;
        while (sourceMapIterator.hasNext()) {
            list.add(sourceMapIterator.next());
            //sourceMapEntry = sourceMapIterator.next();
            //result.put(sourceMapEntry.getKey(), sourceMapEntry.getValue());
            sourceMapIterator.remove();
        }

        // sort list of entries by values with specified comparator
        // (switch the o1 o2 position for a different order)
        list.sort((o1, o2) -> {
            if (ASC == type) {
                return (o1.getValue()).compareTo(o2.getValue()); // <- ASC
            } else {
                return (o2.getValue()).compareTo(o1.getValue()); // <- DESC
            }
        });

        // version with lambda
        //list.sort(Comparator.comparing(o -> (o.getValue())));

        // loop the sorted list and put it into a new insertion ordered LinkedHashMap.
        // not effective if map is very big (fails with out of memory)
        //for (Map.Entry<K, V> entry : list) {
        //    result.put(entry.getKey(), entry.getValue());
        //}

        // version with lambda. not effective if map is very big (fails with out of memory)
        //list.forEach(entry -> result.put(entry.getKey(), entry.getValue()));

        // iterate over list, put each map entry from list to resulting map and remove entry from list
        // (we need it due to memory saving reasons)
        Iterator<Map.Entry<K, V>> iterator = list.iterator();
        Map.Entry<K, V> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            result.put(entry.getKey(), entry.getValue());
            iterator.remove();
        }

        return result;
    }

    /**
     * Java 8 Version. This will sort according to the value in ascending order; for descending order,
     * it is just possible to uncomment the call to Collections.reverseOrder().
     * todo: add a parameter for selecting order
     * todo: add unit tests for method
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValueByLambda(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(/*Collections.reverseOrder()*/))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /***/
    public static <K, V> Map<K, V> removeFromMapByValue(Map<K, V> map, V value) {
        LOG.debug("MapUtils.removeFromMapByValue() is working.");

        if (map == null) { // fast check and return null
            return null;
        }

        if (map.isEmpty()) { // fast check and return original
            return map;
        }

        // iterate over map and remove unnecessary entries
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        Map.Entry<K, V> entry;
        V entryValue;
        while (iterator.hasNext()) {
            entry = iterator.next();
            entryValue = entry.getValue();

            // check condition and remove entry from map
            if ((value == null && entryValue == null) ||
                    (value != null && value.equals(entryValue))) {
                iterator.remove();
            }
        } // end of WHILE

        return map;
    }
}
