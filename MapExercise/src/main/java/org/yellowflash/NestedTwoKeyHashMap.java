package org.yellowflash;

import java.util.*;

public class NestedTwoKeyHashMap<K1,K2,V> implements TwoKeyMap<K1,K2, V>{
    private final Map<K1, Map<K2, V>> map; // K1 to key map to value

    public NestedTwoKeyHashMap() {
        this.map = new HashMap<>();
    }

    @Override
    public V put(K1 k1, K2 k2, V value) {
        return map.computeIfAbsent(k1, k -> new HashMap<>()).put(k2, value);
    }

    @Override
    public V get(K1 k1, K2 k2) {
        Map<K2,V> mapToFind = map.get(k1);
        if(mapToFind == null) return null;
        return mapToFind.get(k2);
    }

    @Override
    public V remove(K1 k1, K2 k2) {
        return null;
    }

    @Override
    public boolean containsKeys(K1 k1, K2 k2) {
        return false;
    }

    @Override
    public boolean containsValue(V value) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Set<Entry<K1, K2, V>> entrySet() {
        return Set.of();
    }

    @Override
    public Collection<V> values() {
        return List.of();
    }

    @Override
    public void putAll(TwoKeyMap<? extends K1, ? extends K2, ? extends V> other) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Map<K2, V> row(K1 k1) {
        return Map.of();
    }

    @Override
    public Map<K1, V> column(K2 k2) {
        return Map.of();
    }

    @Override
    public Iterator<Entry<K1, K2, V>> iterator() {
        return null;
    }
}
