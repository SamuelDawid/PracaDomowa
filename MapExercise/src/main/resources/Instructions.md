# Zadanie domowe: dwu‑kluczowa mapa w Javie (kolekcje)

## Cel

Zaimplementuj własną strukturę danych **TwoKeyMap<K1, K2, V>**, która przechowuje wartości pod parą kluczy `(k1, k2)`. Celem jest zrozumienie interfejsów kolekcji Javy, projektowania API oraz kompromisów pomiędzy różnymi reprezentacjami wewnętrznymi.

## Kontekst

W standardowej bibliotece Javy nie ma gotowej mapy przyjmującej dwa klucze. Najczęściej rozwiązuje się to przez:

1. użycie mapy złożonej `Map<K1, Map<K2, V>>`, albo
2. spakowanie dwóch kluczy w jeden klucz kompozytowy `Map<Pair<K1, K2>, V>`.
   W tym zadaniu zaimplementujesz własną strukturę z jasno zdefiniowanym API.

---

## Wymagania funkcjonalne

1. Struktura powinna przechowywać co najwyżej jedną wartość dla danej pary `(k1, k2)`.
2. Klucze i wartości mogą być `null` **tylko jeśli** jasno to obsłużysz (opisz decyzję w komentarzu klasy). Domyślnie przyjmij brak wsparcia dla `null` → rzuć `NullPointerException`.
3. Implementacja powinna być **niesynchronizowana** (single‑threaded). Dla chętnych: wariant thread‑safe.
4. Równość par kluczy musi opierać się na `equals`/`hashCode` kluczy.

---

## Interfejs: metody do zaimplementowania

Zaprojektuj własny interfejs:

```java
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
```

**Minimalny zestaw metod do zaliczenia:** `put`, `get`, `remove`, `containsKeys`, `size`, `isEmpty`, `clear`, `entrySet`, `keySet`, `values`, `iterator()`.

> Możesz też dodać adapter zgodny z `java.util.Map<Pair<K1,K2>,V>` (bonus), ale **nie zastępuje** on wymaganego interfejsu.

---

## Przykład użycia

```java
TwoKeyMap<String, String, Integer> grades = new NestedTwoKeyHashMap<>();
grades.put("Alice", "Math", 5);
grades.put("Alice", "CS", 4);
System.out.println(grades.get("Alice", "Math")); // 5
System.out.println(grades.row("Alice"));           // {Math=5, CS=4}
```