package org.yellowflash;

public interface TwoKeyMap<K1, K2, V> extends Iterable<TwoKeyMap.Entry<K1, K2, V>> {
    // podstawowe
    V put(K1 k1, K2 k2, V value);
    V get(K1 k1, K2 k2);
    V remove(K1 k1, K2 k2);

    // Zapytania/inspekcja
    boolean containsKeys(K1 k1, K2 k2);
    boolean containsValue(V value);
    int size();
    boolean isEmpty();

    // Widoki i iteracja
    java.util.Set<Entry<K1, K2, V>> entrySet();
    java.util.Set<Pair<K1, K2>> keySet();
    java.util.Collection<V> values();

    // Operacje masowe
    void putAll(TwoKeyMap<? extends K1, ? extends K2, ? extends V> other);
    void clear();

    // Widoki częściowe (opcjonalne, ale punktowane)
    java.util.Map<K2, V> row(K1 k1);     // Wszystkie (k2 → v) dla danego k1
    java.util.Map<K1, V> column(K2 k2);  // Wszystkie (k1 → v) dla danego k2

    // Typy pomocnicze
    interface Entry<K1, K2, V> {
        K1 getKey1();
        K2 getKey2();
        V getValue();
        V setValue(V value);
    }

}
