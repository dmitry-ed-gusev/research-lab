package gusev.dmitry.jtils.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utilities for sorting maps.
 * Created by gusevdm on 6/13/2017.
 */

// https://www.mkyong.com/java/how-to-sort-a-map-in-java/
// https://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values-java

public final class SortMapUtils {

    private SortMapUtils() { // non-instanceable
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
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map) {

        if (map == null) { // fast check and return
            return null;
        }

        Map<K, V> result = new LinkedHashMap<>();
        if (map.isEmpty()) { // fast check and return
            return result;
        }

        // convert map to list of entries <Key, Value>
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());

        // sort list of entries by values with specified comparator
        // (switch the o1 o2 position for a different order)
        /*
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
        */

        // version with lambda
        list.sort(Comparator.comparing(o -> (o.getValue())));

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

}
