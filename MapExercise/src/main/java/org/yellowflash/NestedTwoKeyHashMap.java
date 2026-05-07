package org.yellowflash;

import java.util.*;
import java.util.stream.Collectors;

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
        Map<K2,V> mapToFind = map.get(k1);
        if(mapToFind == null) return null;
        return mapToFind.remove(k2);
    }

    @Override
    public boolean containsKeys(K1 k1, K2 k2) {
        if(!map.containsKey(k1)) return  false;
        Map<K2,V> mapToFind = map.get(k1);
        return mapToFind.containsKey(k2);
    }

    @Override
    public boolean containsValue(V value) {
        for(Map<K2,V> innerMap : map.values())
            if(innerMap.containsValue(value)) return true;
        return false;
    }

    @Override
    public int size() {
        return map.values().stream().mapToInt(Map::size).sum();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public Set<Entry<K1, K2, V>> entrySet() {
        Set<Entry<K1,K2,V>> entrySet = new HashSet<>();
        for(Map.Entry<K1,Map<K2,V>> set : map.entrySet()){
           for(Map.Entry<K2,V> innerMap : set.getValue().entrySet()){
               entrySet.add(new helperEntry<>(set.getKey(),innerMap.getKey(),innerMap.getValue()));
           }
        }
        return entrySet;
    }

    @Override
    public Set<Pair<K1, K2>> keySet() {
        Set<Pair<K1,K2>> pairSet = new HashSet<>();
        for(K1 k1 : map.keySet()){
            for(K2 k2 : map.get(k1).keySet()){
                pairSet.add(new Pair<>(k1,k2));
            }
        }
        return pairSet;
    }

    @Override
    public Collection<V> values() {
        List<V> returnList = new ArrayList<>();
        for(Map.Entry<K1,Map<K2,V>> set : map.entrySet())
            returnList.addAll(set.getValue().values());
        return returnList;
    }

    @Override
    public void putAll(TwoKeyMap<? extends K1, ? extends K2, ? extends V> other) {
        for (Entry<? extends K1, ? extends K2, ? extends V> entry : other){
            put(entry.getKey1(),entry.getKey2(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Map<K2, V> row(K1 k1) {
       return  map.getOrDefault(k1,new HashMap<>());
    }

    @Override
    public Map<K1, V> column(K2 k2) {
        Map<K1, V> mapToReturn = new HashMap<>();
        for (K1 k : map.keySet()){
            V value = map.get(k).get(k2);
            if(value != null) mapToReturn.put(k,value);
        }
        return mapToReturn;
    }

    @Override
    public Iterator<Entry<K1, K2, V>> iterator() {
        return entrySet().iterator();
    }
    private static class helperEntry<K1,K2,V> implements TwoKeyMap.Entry<K1,K2,V>{
        private final K1 k1;
        private final K2 k2;
        private V value;

        public helperEntry(K1 k1, K2 k2,V value) {
            this.k1 = k1;
            this.k2 = k2;
            this.value = value;
        }

        @Override
        public K1 getKey1() {
            return k1;
        }

        @Override
        public K2 getKey2() {
            return k2;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            return this.value = value;
        }
    }
}
