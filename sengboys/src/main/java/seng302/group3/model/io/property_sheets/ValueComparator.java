package seng302.group3.model.io.property_sheets;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by cjmarffy on 31/07/15.
 * Used to compare the values of a map so you can sort by value.
 */
class ValueComparator implements Comparator {

    Map map;

    public ValueComparator(Map map) {
        this.map = map;
    }

    public int compare(Object keyA, Object keyB) {
        Comparable valueA = (Comparable) map.get(keyA);
        Comparable valueB = (Comparable) map.get(keyB);
        return valueA.compareTo(valueB);
    }
}
