# 🏠 Zadanie domowe – Funkcje anonimowe i interfejsy funkcyjne

## Część A – Rozgrzewka z lambdami i Runnable

### Zadanie 1 – Pierwsza lambda bez parametrów (`Runnable`)

Napisz program, w którym:

1. Tworzysz **dwie** zmienne typu `Runnable`:

    * pierwsza lambda wypisuje: `Startuję program...`
    * druga lambda wypisuje: `Kończę program.`
2. Uruchom je w metodzie `main` za pomocą:

   ```java
   runnable1.run();
   runnable2.run();
   ```
3. (Dla chętnych) Uruchom jedną z nich w nowym wątku: `new Thread(runnable2).start();`

---

## Część B – `Function`, `BiFunction`, `UnaryOperator`, `BinaryOperator`

### Zadanie 2 – Normalizacja loginu (`Function` + `andThen`)

Napisz program, który:

1. Tworzy `Function<String, String> normalizeLogin`, która:

    * usuwa spacje z początku i końca,
    * zamienia login na małe litery,
    * usuwa wszystkie spacje ze środka (np. `"  Ja n  Kow "` → `"jankow"`).
2. Zastosuj tę funkcję do tablicy:

   ```java
   String[] loginy = {"  Adam  ", "ANIA K  ", "  k o w a l "};
   ```
3. Zapisz wynik do **nowej tablicy** i wypisz wszystkie znormalizowane loginy.

> Wykorzystaj `Function` + `andThen` zamiast robić wszystko w jednej lambdzie.

---

### Zadanie 3 – Krótki raport o tekście (`Function`)

Napisz `Function<String, Integer>`:

* pierwsza funkcja: liczy długość tekstu bez spacji,
* druga funkcja: liczy liczbę samogłosek (`a, e, i, o, u, y` – także wielkie litery).

Następnie:

1. Dla podanego tekstu (wczytanego z `Scanner`) wypisz:

    * liczbę znaków bez spacji,
    * liczbę samogłosek.
2. Każda z tych operacji ma być osobną lambdą typu `Function`.

---

### Zadanie 4 – Prosty kalkulator (`BiFunction`)

Napisz:

1. `BiFunction<Integer, Integer, Integer> add`, `sub`, `mul`, `div`,
2. metodę:

   ```java
   static int calculate(int a, int b, BiFunction<Integer, Integer, Integer> op)
   ```

   która zwraca wynik `op.apply(a, b)`.

W `main`:

* wczytaj dwie liczby i znak operacji (`+`, `-`, `*`, `/`),
* wybierz odpowiednią lambdę (`BiFunction`) i wyświetl wynik.

---

### Zadanie 5 – Operacje na listach (`UnaryOperator` i `BinaryOperator`)

1. Napisz `UnaryOperator<List<Integer>> removeNegatives`, który:

    * usuwa wszystkie liczby ujemne z listy,
    * zwraca tę samą listę (po modyfikacji).
2. Napisz `BinaryOperator<Integer> maxOp` wybierający większą z dwóch liczb.
3. Dla listy:

   ```java
   List<Integer> nums = Arrays.asList(3, -1, 7, -5, 10, 0);
   ```

    * usuń liczby ujemne za pomocą `removeNegatives`,
    * znajdź maksimum korzystając z `maxOp` w pętli `for`.

> Nie używaj `stream()`, tylko zwykłe pętle.

---

## Część C – `Predicate`, `BiPredicate`

### Zadanie 6 – Walidacja danych użytkownika (`Predicate`)

Masz tablicę loginów:

```java
String[] loginy = {"adam", "Ala123", "x", "User_01", "ADMIN", "gość"};
```

1. Zbuduj `Predicate<String> isValidLogin`, który:

    * ma długość co najmniej 3,
    * zawiera **tylko** litery lub cyfry (bez polskich znaków),
    * zaczyna się literą.
2. Za pomocą pętli `for`:

    * policz ile loginów jest poprawnych,
    * wypisz tylko poprawne.

> Nie używaj `stream()`.

---

### Zadanie 7 – Porównywanie stringów (`BiPredicate`)

Napisz:

1. `BiPredicate<String, String> sameIgnoreCase` – sprawdza, czy dwa stringi są równe **bez** względu na wielkość liter.
2. `BiPredicate<String, String> isSuffix` – sprawdza, czy pierwszy string **kończy się** drugim.
3. W `main`:

    * wczytaj dwa stringi z konsoli,
    * wypisz wynik obu predykatów (`true/false`).

---

## Część D – `Consumer`, `BiConsumer`, `Supplier`

### Zadanie 8 – Numerowane logowanie (`Consumer`)

Napisz metodę:

```java
static void printWithNumbers(List<String> lines, Consumer<String> consumer)
```

która:

* wypisuje każdą linię w formacie:
  `1) <tekst>`, `2) <tekst>`, `3) <tekst>` ...

W `main`:

1. Stwórz listę 4 dowolnych stringów.
2. Zdefiniuj lambdę `Consumer<String> logger`, która po prostu wyświetla przekazany tekst (`System.out.println(s)`).
3. Wywołaj `printWithNumbers(lines, logger)`.

> W środku metody użyj pętli `for (int i = 0; i < ...; i++)`.

---

### Zadanie 9 – Raport sprzedaży (`BiConsumer`)

Załóż, że masz:

```java
Map<String, Integer> productToQty = new HashMap<>();
// kilka produktów + ilości
```

1. Stwórz `BiConsumer<String, Integer> reporter`, który wypisuje:
   `Produkt: <nazwa>, sztuk: <ilość>`.
2. Napisz metodę:

   ```java
   static void printReport(Map<String, Integer> map, BiConsumer<String, Integer> consumer)
   ```

   która przechodzi po `entrySet()` i wywołuje `consumer.accept(key, value)`.

---

### Zadanie 10 – Generator kodów (`Supplier`)

1. Zdefiniuj `Supplier<String> codeSupplier`, który generuje 6-znakowy losowy kod z liter A–Z i cyfr 0–9.
2. W `main`:

    * utwórz tablicę `String[] codes = new String[10];`
    * wypełnij ją 10 wygenerowanymi kodami (pętla `for` + `supplier.get()`),
    * wypisz wszystkie kody.

> Bez `Stream.generate`, tylko `Supplier` + zwykła pętla.

---

## Część E – Własne interfejsy funkcyjne (`@FunctionalInterface`, `TriFunction`)

### Zadanie 11 – Formatowanie tekstu (własny interfejs)

1. Zdefiniuj własny interfejs funkcyjny:

   ```java
   @FunctionalInterface
   interface StringFormatter {
       String format(String input);
   }
   ```

2. W `main` utwórz **3 różne** lambdy:

    * zamieniająca tekst na wielkie litery,
    * dodająca prefix `">>> "`,
    * odwracająca tekst (`"Java"` → `"avaJ"`).

3. Zaimplementuj metodę:

   ```java
   static String applyFormat(String text, StringFormatter formatter)
   ```

   i przetestuj ją na tych trzech lambdach.

---

### Zadanie 12 – Średnia ważona (`TriFunction`)

1. Zdefiniuj (jak na lekcji) interfejs:

   ```java
   @FunctionalInterface
   interface TriFunction<T, U, V, R> {
       R apply(T t, U u, V v);
   }
   ```

2. Utwórz lambdę:

   ```java
   TriFunction<Double, Double, Double, Double> weightedAverage = (ocena, waga, maxWaga) -> ...;
   ```

   która liczy ważony udział: `ocena * waga / maxWaga`.

3. Dla tablic:

   ```java
   double[] oceny = {3.0, 4.5, 5.0};
   double[] wagi  = {1.0, 2.0, 3.0};
   ```

   policz **łączną średnią ważoną** wszystkich ocen (pętla + wywołania `weightedAverage`).

---

## Część F – `Comparator` jako interfejs funkcyjny

### Zadanie 13 – Sortowanie obiektów z lambdą `Comparator`

1. Zdefiniuj klasę/rekord:

   ```java
   class Product {
       String name;
       double price;
       int rating; // 1–5
       // konstruktor, gettery / pola publiczne – jak wolisz
   }
   ```
2. Stwórz listę kilku produktów.
3. Użyj `Comparator<Product>` w postaci lambdy do:

    * posortowania **rosnąco po cenie**,
    * a potem **malejąco po ocenie (rating)**,
      z użyciem `thenComparing`.

> Użyj `Comparator.comparing(...)` i `.reversed()` – wszystko jako lambdy/metod referencyjnych.

---

## Część G – Mini-projekt: System zniżek (Strategy z lambdami)

### Zadanie 14 – Strategie cenowe (`Function` / własny interfejs)

1. Zdefiniuj interfejs:

   ```java
   @FunctionalInterface
   interface PriceStrategy {
       double apply(double basePrice);
   }
   ```

2. W `main` zdefiniuj kilka strategii jako lambdy:

    * `normal` – zwraca cenę bez zmian,
    * `student` – 10% zniżki,
    * `vip` – 20% zniżki,
    * `blackFriday` – 30% zniżki.

3. Napisz metodę:

   ```java
   static double calculatePrice(double basePrice, PriceStrategy strategy)
   ```

4. Dla kilku przykładowych cen (np. 100, 250, 399) wypisz wynik każdej strategii.
