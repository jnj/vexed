package vexed;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Containers {

    public static <K, V> Map<K, V> newHashMap() {
        return new HashMap<K, V>();
    }

    public static <T> Set<T> newHashSet(Collection<? extends T> collection) {
        return new HashSet<T>(collection);
    }

    public static <T> Set<T> newHashSet() {
        return new HashSet<T>();
    }

    public static <T> LinkedList<T> newLinkedList() {
        return new LinkedList<T>();
    }

    private Containers() {
    }
}
