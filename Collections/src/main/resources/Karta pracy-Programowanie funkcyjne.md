# Karta pracy — Programowanie funkcyjne

---

## 1. `BiFunction<T, U, R>`: dwa argumenty, jeden wynik

**Cel:** Zbudować mini-kalkulator — cztery `BiFunction` (`+`, `-`, `*`, `/`) wybierane na podstawie znaku.

**Teoria w pigułce:**
`BiFunction<T, U, R>` to `Function`, ale przyjmuje **dwa** argumenty — typu `T` i `U`. Metoda to `apply(T t, U u)`. 
Świetnie pasuje do operacji typu „dwa wejścia, jeden wynik": dodawanie, łączenie, BMI, podatek od kwoty itp.

### Kod

Utwórz plik: `src/main/java/com/example/lambdy/zad01/Kalkulator.java`

```java
package com.example.lambdy.zad01;

import javax.xml.validation.Validator;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.BiFunction;

public class Kalkulator {

    static int calculate(int a, int b, BiFunction<Integer, Integer, Integer> op) {
        return op.apply(a, b);
    }

    public static void main(String[] args) {
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        BiFunction<Integer, Integer, Integer> sub = (a, b) -> a - b;
        BiFunction<Integer, Integer, Integer> mul = (a, b) -> a * b;
        BiFunction<Integer, Integer, Integer> div = (a, b) -> a / b;
        BiFunction<Integer, Integer, Integer> div = (a, b) -> a % b;

        Scanner sc = new Scanner(System.in);
        System.out.print("Podaj a: ");
        int a = sc.nextInt();
        System.out.print("Podaj b: ");
        int b = sc.nextInt();
        System.out.print("Podaj operator (+ - * /): ");
        String op = sc.next();

        BiFunction<Integer, Integer, Integer> chosen = switch (op) {
            case "+" -> add;
            case "-" -> sub;
            case "*" -> mul;
            case "/" -> {
                
                        div;
            }
            default -> throw new IllegalArgumentException("Nieznany operator: " + op);
        };

        System.out.println("Wynik: " + calculate(a, b, chosen));
    }
}
```

### Krok po kroku

1. Skopiuj kod, uruchom. Wpisz `10`, `3`, `+` — powinno wyjść `13`.
2. Wpisz `7`, `0`, `/` — co się stanie? Jak ten case obsłużyłbyś bezpieczniej?
3. Dodaj piątą operację `mod` (`%`) i piąty case w `switch`.
4. Zmień typ z `Integer` na `Double` we wszystkich `BiFunction` i metodzie `calculate` — co musisz dopasować?

### Pytania kontrolne

1. Jakie typy są w `BiFunction<Integer, Integer, Integer>` (kolejność: argument 1, argument 2, wynik)?
2. Dlaczego w sygnaturze `BiFunction<Integer, Integer, Integer>`, a nie `BiFunction<int, int, int>`?
3. Co metoda `calculate` zyskuje na tym, że bierze `BiFunction` zamiast hardcodować `+`?

---

## 2. `UnaryOperator<T>` i `BinaryOperator<T>`

**Cel:** Zrozumieć, kiedy zamiast `Function` lub `BiFunction` używać ich „specjalizacji", w której typ wejścia i wyjścia jest taki sam.

**Teoria w pigułce:**
`UnaryOperator<T> extends Function<T, T>` — bierze `T`, zwraca `T`. Używaj, gdy nic nie zmienia się w typie (np. czyszczenie napisu, normalizacja listy). `BinaryOperator<T> extends BiFunction<T, T, T>` — dwa razy `T` na wejściu, `T` na wyjściu (np. wybór maximum, suma, sklejanie napisów).

### Kod

Utwórz plik: `src/main/java/com/example/lambdy/zad02/OperatorDemo.java`

```java
package com.example.lambdy.zad02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class OperatorDemo {

    public static void main(String[] args) {
        // UnaryOperator - usuwa liczby ujemne z listy (mutuje listę i zwraca tę samą referencję)
        UnaryOperator<List<Integer>> removeNegatives = list -> {
            list.removeIf(x -> x < 0);
            return list;
        };

        // BinaryOperator - wybiera większą z dwóch liczb
        BinaryOperator<Integer> maxOp = (a, b) -> a > b ? a : b;

        // Test
        List<Integer> nums = new ArrayList<>(Arrays.asList(3, -1, 7, -5, 10, 0));
        System.out.println("Przed: " + nums);
        removeNegatives.apply(nums);
        System.out.println("Po removeNegatives: " + nums);

        // Maximum w pętli for, BEZ stream()
        int max = nums.get(0);
        for (int i = 1; i < nums.size(); i++) {
            max = maxOp.apply(max, nums.get(i));
        }
        System.out.println("Maksimum (przez maxOp): " + max);
    }
}
```

### Krok po kroku

1. Skopiuj kod, uruchom. Po `removeNegatives` powinieneś zobaczyć `[3, 7, 10, 0]`, max = `10`.
2. Zwróć uwagę: `Arrays.asList(...)` zwraca listę, której **nie da się** modyfikować. Dlatego opakowujemy ją w `new ArrayList<>(...)`. Spróbuj usunąć opakowanie i zobacz exception.
3. Napisz `BinaryOperator<String> concat`, który łączy dwa stringi przecinkiem (np. `"A", "B" -> "A, B"`). Użyj w pętli, żeby zbudować jeden duży string z `List<String>`.
4. Zamień `BinaryOperator<Integer> maxOp = (a, b) -> ...` na referencję do metody `Integer::max`. Skompiluje się?

### Pytania kontrolne

1. Dlaczego `UnaryOperator<List<Integer>>` jest lepsze niż `Function<List<Integer>, List<Integer>>` w tym przypadku?
2. Czy `BinaryOperator<Integer>` zadziała tam, gdzie kod wymaga `BiFunction<Integer, Integer, Integer>`? (Wskazówka: dziedziczenie.)
3. Co zwróci `removeNegatives.apply(nums)` — nową listę czy tę samą? Sprawdź referencje.

---

## 3. `Predicate<T>`: walidacja w lambdzie

**Cel:** Zbudować predykat walidujący login (długość, dozwolone znaki, pierwszy znak), użyć go w pętli i policzyć ile stringów przeszło walidację.

**Teoria w pigułce:**
`Predicate<T>` ma metodę `test(T t)`, która zwraca `boolean`. Predykaty można łączyć: `p.and(q)`, `p.or(q)`, `p.negate()`. Klasyczne zastosowanie: walidacja, filtrowanie, warunki w `if` budowane dynamicznie.

### Kod

Utwórz plik: `src/main/java/com/example/lambdy/zad03/PredicateDemo.java`

```java
package com.example.lambdy.zad03;

import java.util.function.Predicate;

public class PredicateDemo {

    public static void main(String[] args) {
        // Trzy małe predykaty
        Predicate<String> minLen3      = s -> s != null && s.length() >= 3;
        Predicate<String> onlyAsciiAlnum = s -> s.matches("[A-Za-z0-9_]+");
        Predicate<String> startsWithLetter = s -> !s.isEmpty() && Character.isLetter(s.charAt(0));

        // Łączymy: AND - wszystkie warunki muszą być spełnione
        Predicate<String> isValidLogin = minLen3.and(onlyAsciiAlnum).and(startsWithLetter);

        String[] loginy = {"adam", "Ala123", "x", "User_01", "ADMIN", "gość", "1234"};

        int valid = 0;
        System.out.println("Poprawne loginy:");
        for (String login : loginy) {
            if (isValidLogin.test(login)) {
                valid++;
                System.out.println("  - " + login);
            }
        }
        System.out.println("\nPoprawnych: " + valid + " z " + loginy.length);

        // Użycie negate
        Predicate<String> isInvalid = isValidLogin.negate();
        System.out.println("\nNiepoprawne (przez negate):");
        for (String login : loginy) {
            if (isInvalid.test(login)) {
                System.out.println("  - " + login);
            }
        }
    }
}
```

### Krok po kroku

1. Skopiuj kod, uruchom. Sprawdź, które loginy zostały zakwalifikowane jako poprawne, a które nie.
2. Dla każdego niepoprawnego loginu — który warunek (z trzech) zawiódł? (Tip: rozbij `if` na trzy osobne i wypisz `false`.)
3. Dodaj czwarty warunek: `noConsecutiveDigits` (np. `"User__01"` jest OK, ale `"abc__"` nie). Pomyśl jak go napisać.
4. Sprawdź zachowanie dla `null`: dodaj `null` do tablicy. Czy `isValidLogin.test(null)` rzuci NPE? Co naprawia `s != null` w `minLen3`?

### Pytania kontrolne

1. Jaka jest sygnatura metody `Predicate#test`?
2. Co zwróci `p.and(q).test(x)`, jeśli `p.test(x)` to `false`? (Pytanie o krótkie spięcie.)
3. Czy `p.negate()` modyfikuje `p`?

---

## 4. `BiPredicate<T, U>`: porównanie dwóch wartości

**Cel:** Napisać dwa `BiPredicate` na stringach (`sameIgnoreCase`, `isSuffix`) i przetestować je na danych z `Scanner`.

**Teoria w pigułce:**
`BiPredicate<T, U>` to `Predicate`, ale bierze **dwa** argumenty. Naturalnie pasuje do relacji między dwiema wartościami: równość, kończenie się czymś, „A jest dzielnikiem B".

### Kod

Utwórz plik: `src/main/java/com/example/lambdy/zad04/BiPredicateDemo.java`

```java
package com.example.lambdy.zad04;

import java.util.Scanner;
import java.util.function.BiPredicate;

public class BiPredicateDemo {

    public static void main(String[] args) {
        BiPredicate<String, String> sameIgnoreCase =
                (a, b) -> a != null && a.equalsIgnoreCase(b);

        BiPredicate<String, String> isSuffix =
                (a, b) -> a != null && b != null && a.endsWith(b);

        Scanner sc = new Scanner(System.in);
        System.out.print("Podaj string A: ");
        String a = sc.nextLine();
        System.out.print("Podaj string B: ");
        String b = sc.nextLine();

        System.out.println("sameIgnoreCase(A, B) = " + sameIgnoreCase.test(a, b));
        System.out.println("isSuffix(A, B)       = " + isSuffix.test(a, b));
    }
}
```

### Krok po kroku

1. Skopiuj, uruchom. Spróbuj: `Java`/`java` (oba `true`), `Programming.java`/`.java` (sameIgnoreCase=false, isSuffix=true).
2. Dodaj trzeci `BiPredicate<String, String> isPrefix` analogicznie.
3. Co stanie się jeśli przekażesz `null` do `sameIgnoreCase.test(null, "abc")`? Sprawdź. (Bez `a != null` byłaby NPE.)
4. Zamień `(a, b) -> a.equalsIgnoreCase(b)` na referencję do metody `String::equalsIgnoreCase`. Czy się kompiluje? Dlaczego tak / dlaczego nie?

### Pytania kontrolne

1. Czym `BiPredicate<T, U>` różni się od `Predicate<T>`?
2. Czy `BiPredicate` ma metody `and`, `or`, `negate`?
3. Dlaczego ważne jest sprawdzenie `a != null` zanim wywołasz `a.endsWith(b)`?

---

## 5. `Consumer<T>`: operacja uboczna na elemencie

**Cel:** Napisać metodę, która iteruje po `List<String>` indeksowaną pętlą `for` i numeruje wypisywane elementy, korzystając z przekazanej `Consumer<String>` lambdy.

**Teoria w pigułce:**
`Consumer<T>` ma metodę `accept(T t)` — bierze coś, **nic nie zwraca**. Modeluje „efekt uboczny": wypisanie, zalogowanie, dodanie do kolekcji. Klasyczne miejsce użycia: `List.forEach(Consumer)`.

### Kod

Utwórz plik: `src/main/java/com/example/lambdy/zad05/ConsumerDemo.java`

```java
package com.example.lambdy.zad05;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ConsumerDemo {

    static void printWithNumbers(List<String> lines, Consumer<String> consumer) {
        for (int i = 0; i < lines.size(); i++) {
            String numbered = (i + 1) + ") " + lines.get(i);
            consumer.accept(numbered);
        }
    }

    public static void main(String[] args) {
        List<String> lines = Arrays.asList("alpha", "beta", "gamma", "delta");

        Consumer<String> logger = s -> System.out.println(s);
        Consumer<String> shouter = s -> System.out.println(">>> " + s.toUpperCase());

        System.out.println("=== logger ===");
        printWithNumbers(lines, logger);

        System.out.println("\n=== logger.andThen(shouter) ===");
        printWithNumbers(lines, logger.andThen(shouter));
    }
}
```

### Krok po kroku

1. Skopiuj, uruchom. Pierwsza pętla wypisuje `1) alpha` itd. Druga — wypisuje numer + wariant z `andThen`.
2. Zauważ: `Consumer` ma `andThen` — jeden element przejdzie przez **oba** consumery po kolei.
3. Zamień `s -> System.out.println(s)` na referencję do metody `System.out::println`.
4. Wymyśl trzeci consumer: `Consumer<String> auditor`, który dopisuje element do `List<String> log = new ArrayList<>();`. Wypisz log po pętli.

### Pytania kontrolne

1. Jaki typ zwraca `Consumer#accept`?
2. Czym `Consumer<String>` różni się od `Function<String, Void>`?
3. Jak działa `c1.andThen(c2)` na pojedynczym elemencie?

---

## 6. `BiConsumer<T, U>`: idealny do `Map`

**Cel:** Wypisać raport sprzedaży z `Map<String, Integer>`, używając `BiConsumer<String, Integer>` przekazanej do własnej metody.

**Teoria w pigułce:**
`BiConsumer<T, U>` ma metodę `accept(T t, U u)`. Najczęstszy scenariusz: `Map.forEach(BiConsumer)` — biblioteczna metoda, która przejdzie po kluczach i wartościach.

### Kod

Utwórz plik: `src/main/java/com/example/lambdy/zad06/BiConsumerDemo.java`

```java
package com.example.lambdy.zad06;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class BiConsumerDemo {

    static void printReport(Map<String, Integer> map, BiConsumer<String, Integer> consumer) {
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            consumer.accept(e.getKey(), e.getValue());
        }
    }

    public static void main(String[] args) {
        Map<String, Integer> productToQty = new HashMap<>();
        productToQty.put("Laptop", 5);
        productToQty.put("Mysz", 12);
        productToQty.put("Klawiatura", 7);

        BiConsumer<String, Integer> reporter =
                (name, qty) -> System.out.println("Produkt: " + name + ", sztuk: " + qty);

        System.out.println("=== Raport (przez własną printReport) ===");
        printReport(productToQty, reporter);

        System.out.println("\n=== To samo przez Map.forEach ===");
        productToQty.forEach(reporter);
    }
}
```

### Krok po kroku

1. Skopiuj, uruchom. Oba bloki powinny dać ten sam wynik (kolejność może być inna — `HashMap` nie gwarantuje kolejności).
2. Zamień `HashMap` na `LinkedHashMap` — czy kolejność jest teraz przewidywalna?
3. Dodaj drugi `BiConsumer<String, Integer> warehouse`, który ostrzega gdy `qty < 10`. Połącz go z `reporter` przez `andThen`.
4. Spróbuj nieprawnie wywołać `consumer.accept(e.getKey())` (z jednym argumentem). Co mówi kompilator?

### Pytania kontrolne

1. Jaka jest sygnatura `BiConsumer#accept`?
2. Po co istnieje `Map.forEach(BiConsumer)` skoro można `entrySet().forEach(...)`?
3. Czy w `BiConsumer<String, Integer> r = (k, v) -> ...` typy są w kolejności klucz, wartość?

---

## 7. `Supplier<T>`: generator wartości

**Cel:** Napisać `Supplier<String>`, który generuje 6-znakowy losowy kod (A–Z + 0–9), wypełnić nim tablicę 10 kodów w pętli `for`.

**Teoria w pigułce:**
`Supplier<T>` ma metodę `get()` — **bez argumentów**, zwraca `T`. Klasyczne zastosowania: leniwe generowanie wartości, fabryki, wstrzykiwanie czasu/UUID/randomów do testów.

### Kod

Utwórz plik: `src/main/java/com/example/lambdy/zad07/SupplierDemo.java`

```java
package com.example.lambdy.zad07;

import java.util.Random;
import java.util.function.Supplier;

public class SupplierDemo {

    public static void main(String[] args) {
        final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        final Random rng = new Random();

        Supplier<String> codeSupplier = () -> {
            StringBuilder sb = new StringBuilder(6);
            for (int i = 0; i < 6; i++) {
                sb.append(alphabet.charAt(rng.nextInt(alphabet.length())));
            }
            return sb.toString();
        };

        // Wypełnienie tablicy 10 kodów - klasyczna pętla, BEZ Stream.generate
        String[] codes = new String[10];
        for (int i = 0; i < codes.length; i++) {
            codes[i] = codeSupplier.get();
        }

        System.out.println("Wygenerowane kody:");
        for (String c : codes) {
            System.out.println("  " + c);
        }
    }
}
```

### Krok po kroku

1. Skopiuj, uruchom. Powinno wypaść 10 losowych kodów po 6 znaków.
2. Przekaż seeda do `Random`: `new Random(42)` — uruchom dwa razy. Czy kody są te same?
3. Zamień `Supplier<String>` na `Supplier<java.util.UUID>` z lambdą `java.util.UUID::randomUUID`. Wygeneruj 5 UUID-ów.
4. Dlaczego `final String alphabet` i `final Random rng`? Co by się stało, gdyby były lokalnymi zmiennymi nie-finalnymi i były zmieniane po stworzeniu lambdy? (Słowo klucz: „effectively final".)

### Pytania kontrolne

1. Jaki jest typ wyniku `codeSupplier.get()`?
2. Po co `Supplier`, skoro można po prostu wywołać metodę?
3. Czy `Supplier#get` rzuca jakieś sprawdzane wyjątki? (Spójrz w dokumentację.)

---

## 8. Własny interfejs funkcyjny

**Cel:** Zdefiniować własny `@FunctionalInterface StringFormatter`, stworzyć trzy lambdy go implementujące i przetestować je w jednej metodzie pomocniczej.

**Teoria w pigułce:**
Każdy interfejs z **dokładnie jedną** metodą abstrakcyjną może być targetem lambdy. Adnotacja `@FunctionalInterface` to dobra praktyka — kompilator zweryfikuje, że to faktycznie interfejs funkcyjny i ostrzeże, jeśli ktoś doda drugą metodę abstrakcyjną.

### Kod

Utwórz plik: `src/main/java/com/example/lambdy/zad08/StringFormatterDemo.java`

```java
package com.example.lambdy.zad08;

public class StringFormatterDemo {

    @FunctionalInterface
    interface StringFormatter {
        String format(String input);
    }

    static String applyFormat(String text, StringFormatter formatter) {
        return formatter.format(text);
    }

    public static void main(String[] args) {
        StringFormatter upper   = s -> s.toUpperCase();
        StringFormatter prefix  = s -> ">>> " + s;
        StringFormatter reverse = s -> new StringBuilder(s).reverse().toString();

        String text = "Java";

        System.out.println("upper:   " + applyFormat(text, upper));
        System.out.println("prefix:  " + applyFormat(text, prefix));
        System.out.println("reverse: " + applyFormat(text, reverse));
    }
}
```

### Krok po kroku

1. Skopiuj, uruchom. Trzy linie wyniku.
2. Spróbuj usunąć `@FunctionalInterface` — czy kod dalej się kompiluje? (Tak — adnotacja jest kontraktem, nie wymogiem.)
3. Dodaj **drugą** metodę abstrakcyjną do interfejsu `StringFormatter`. Co się stanie z lambdami i `@FunctionalInterface`?
4. Zamień `s -> s.toUpperCase()` na referencję do metody `String::toUpperCase`.

### Pytania kontrolne

1. Co dokładnie sprawdza `@FunctionalInterface`?
2. Czy interfejs funkcyjny może mieć metody `default` i `static`?
3. Dlaczego `applyFormat` bierze `StringFormatter`, a nie konkretną klasę?

---

## 9. `TriFunction`: własny interfejs z trzema argumentami

**Cel:** Zdefiniować generyczny `TriFunction<T, U, V, R>`, użyć go do liczenia ważonego udziału oceny i zsumować w pętli średnią ważoną.

**Teoria w pigułce:**
Java standardowo daje `Function` (1 arg) i `BiFunction` (2 args). Dla 3+ argumentów albo opakuj dane w obiekt/rekord, albo zdefiniuj własny `TriFunction`. Składnia jest dosłownie generalizacją `BiFunction`.

### Kod

Utwórz plik: `src/main/java/com/example/lambdy/zad09/TriFunctionDemo.java`

```java
package com.example.lambdy.zad09;

public class TriFunctionDemo {

    @FunctionalInterface
    interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }

    public static void main(String[] args) {
        // ocena * waga / sumaWag (czyli udział tej oceny w finałowej średniej ważonej)
        TriFunction<Double, Double, Double, Double> weightedShare =
                (ocena, waga, sumaWag) -> ocena * waga / sumaWag;

        double[] oceny = {3.0, 4.5, 5.0};
        double[] wagi  = {1.0, 2.0, 3.0};

        // 1) sumujemy wagi
        double sumaWag = 0;
        for (double w : wagi) sumaWag += w;

        // 2) sumujemy udziały - każdy udział to weightedShare.apply(ocena, waga, sumaWag)
        double srednia = 0;
        for (int i = 0; i < oceny.length; i++) {
            srednia += weightedShare.apply(oceny[i], wagi[i], sumaWag);
        }

        System.out.println("Średnia ważona: " + srednia);
        // Spodziewany wynik: (3*1 + 4.5*2 + 5*3) / (1 + 2 + 3) = (3 + 9 + 15) / 6 = 27/6 = 4.5
    }
}
```

### Krok po kroku

1. Skopiuj, uruchom. Powinno wyjść `4.5`.
2. Zmień wagi na `{2.0, 2.0, 2.0}` (równe) — czy wynik to średnia arytmetyczna `(3+4.5+5)/3 = 4.166...`?
3. Dodaj walidację: rzuć `IllegalArgumentException`, jeśli `oceny.length != wagi.length`.
4. Spróbuj zdefiniować analogicznie `QuadFunction<A, B, C, D, R>` z 4 argumentami.

### Pytania kontrolne

1. Dlaczego Java nie ma wbudowanego `TriFunction`?
2. Jakie typy oznaczają parametry `T, U, V, R`?
3. Kiedy zamiast `TriFunction` lepiej użyć rekordu/obiektu jako wejścia?

---

## 10. `Comparator` jako interfejs funkcyjny

**Cel:** Posortować listę produktów po cenie rosnąco, a w razie remisu po ratingu malejąco — całość z `Comparator.comparing` + `thenComparing` + `reversed`.

**Teoria w pigułce:**
`Comparator<T>` to interfejs funkcyjny z metodą `int compare(T a, T b)`. Zwraca `<0` jeśli `a < b`, `0` przy równości, `>0` jeśli `a > b`. Najlepiej **nie** pisać tej metody ręcznie, a używać fabryk: `Comparator.comparing(klucz)`, `comparingInt`, `comparingDouble`, `thenComparing`, `reversed`, `nullsFirst`, `nullsLast`.

### Kod

Utwórz plik: `src/main/java/com/example/lambdy/zad10/ComparatorDemo.java`

```java
package com.example.lambdy.zad10;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ComparatorDemo {

    record Product(String name, double price, int rating) {}

    public static void main(String[] args) {
        List<Product> products = new ArrayList<>(List.of(
                new Product("Mysz",        89.99, 4),
                new Product("Klawiatura", 199.00, 5),
                new Product("Mysz pro",    89.99, 5),
                new Product("Słuchawki",  349.00, 3),
                new Product("Mata",        89.99, 3)
        ));

        // Cena rosnąco, w razie remisu rating MALEJĄCO (najwyższy rating na górze)
        Comparator<Product> byPriceThenRatingDesc =
                Comparator.comparingDouble(Product::price)
                          .thenComparing(Comparator.comparingInt(Product::rating).reversed());

        products.sort(byPriceThenRatingDesc);

        System.out.println("Posortowane (cena rosnąco, rating malejąco):");
        for (Product p : products) {
            System.out.println("  " + p);
        }
    }
}
```

### Krok po kroku

1. Skopiuj, uruchom. Wszystkie produkty po cenie `89.99` powinny iść grupą — w niej najpierw rating `5`, potem `4`, na końcu `3`.
2. Zamień `byPriceThenRatingDesc` na sortowanie tylko po `name` rosnąco — `Comparator.comparing(Product::name)`.
3. Dodaj trzeci klucz: po remisie ceny i ratingu sortuj po nazwie alfabetycznie.
4. Spróbuj napisać ten sam komparator „ręcznie" jako lambdę `(a, b) -> { ... }`. Porównaj ile linii.

### Pytania kontrolne

1. Co zwraca `Comparator#compare`, gdy `a < b`?
2. Czym `Comparator.comparing(...)` różni się od `Comparator.comparingInt(...)`? Po co istnieje wariant prymitywny?
3. Czy `thenComparing` modyfikuje istniejący komparator, czy zwraca nowy?

---

## 11. Mini-projekt: System zniżek (Strategy z lambdami)

**Cel:** Zaimplementować wzorzec **Strategy** używając własnego interfejsu funkcyjnego `PriceStrategy`. Cztery strategie zniżkowe + jedna metoda licząca.

**Teoria w pigułce:**
Wzorzec **Strategy** = wymienialny algorytm jako obiekt. W klasycznej Javie potrzebujesz interfejsu i kilku klas. Z lambdami: jedna lambda = jedna strategia. Idealne dla cenników, polityk walidacji, reguł rabatowych, formuł podatkowych.

### Kod

Utwórz plik: `src/main/java/com/example/lambdy/zad11/PriceStrategyDemo.java`

```java
package com.example.lambdy.zad11;

public class PriceStrategyDemo {

    @FunctionalInterface
    interface PriceStrategy {
        double apply(double basePrice);
    }

    static double calculatePrice(double basePrice, PriceStrategy strategy) {
        return strategy.apply(basePrice);
    }

    public static void main(String[] args) {
        PriceStrategy normal      = p -> p;
        PriceStrategy student     = p -> p * 0.90;
        PriceStrategy vip         = p -> p * 0.80;
        PriceStrategy blackFriday = p -> p * 0.70;

        double[] ceny = {100.0, 250.0, 399.0};

        System.out.printf("%-12s %-8s %-8s %-8s %-12s%n",
                "Cena bazowa", "normal", "student", "vip", "blackFriday");
        for (double cena : ceny) {
            System.out.printf("%-12.2f %-8.2f %-8.2f %-8.2f %-12.2f%n",
                    cena,
                    calculatePrice(cena, normal),
                    calculatePrice(cena, student),
                    calculatePrice(cena, vip),
                    calculatePrice(cena, blackFriday));
        }
    }
}
```

### Krok po kroku

1. Skopiuj, uruchom. Powinieneś zobaczyć ładną tabelkę z 3 wierszami x 5 kolumn.
2. Dodaj piątą strategię `loyalty` — 15% zniżki **plus** dodatkowo 5 zł zniżki, ale wynik nie może być niższy niż `0`.
3. Wyciągnij strategie do `Map<String, PriceStrategy>` i pozwól wybierać po nazwie wczytanej z `Scanner`.
4. Spróbuj złożyć dwie strategie: `student.andThen(blackFriday)`. Co dostaniesz? (Uwaga — `PriceStrategy` to **twój** interfejs, więc `andThen` nie istnieje. Dodaj go jako `default` metodę i zaimplementuj.)

### Pytania kontrolne

1. Dlaczego ten kod jest „Strategy"? Co jest enkapsulowane w `PriceStrategy`?
2. Gdzie poszłaby logika strategii w klasycznym (bez lambd) Strategy z lat 90.?
3. Czy `PriceStrategy` mógłby być po prostu `Function<Double, Double>`? Jakie są plusy i minusy własnego interfejsu vs `Function`?

---

## 12. Referencje do metod (`Type::method`)

**Cel:** Zamienić proste lambdy na referencje do metod — statycznych, instancyjnych, konstruktorów. Czytelniej, krócej, mniej szumu.

**Teoria w pigułce:**
Kiedy lambda **tylko deleguje** do istniejącej metody (`s -> s.toUpperCase()`), można ją zastąpić referencją (`String::toUpperCase`). Cztery formy:
- statyczna metoda: `Integer::parseInt`
- metoda instancyjna konkretnego obiektu: `System.out::println`
- metoda instancyjna „dowolnej instancji typu T": `String::toUpperCase`
- konstruktor: `ArrayList::new`

### Kod

Utwórz plik: `src/main/java/com/example/lambdy/zad12/MethodReferenceDemo.java`

```java
package com.example.lambdy.zad12;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MethodReferenceDemo {

    public static void main(String[] args) {
        // 1) Statyczna metoda
        Function<String, Integer> parse1 = s -> Integer.parseInt(s);   // lambda
        Function<String, Integer> parse2 = Integer::parseInt;          // referencja
        System.out.println(parse2.apply("42"));

        // 2) Metoda instancyjna konkretnego obiektu
        Consumer<String> print1 = s -> System.out.println(s);          // lambda
        Consumer<String> print2 = System.out::println;                 // referencja
        print2.accept("Hello");

        // 3) Metoda instancyjna typu T (bierze T jako parametr "this")
        Function<String, String> upper1 = s -> s.toUpperCase();        // lambda
        Function<String, String> upper2 = String::toUpperCase;         // referencja
        System.out.println(upper2.apply("java"));

        // 4) Konstruktor
        Supplier<List<String>> factory1 = () -> new ArrayList<>();     // lambda
        Supplier<List<String>> factory2 = ArrayList::new;              // referencja
        List<String> list = factory2.get();
        list.add("element");
        System.out.println(list);
    }
}
```

### Krok po kroku

1. Skopiuj, uruchom — wszystkie 4 pary powinny dać taki sam wynik.
2. Otwórz IntelliJ, ustaw kursor na lambdzie `s -> System.out.println(s)`. Wciśnij `Alt+Enter` → IntelliJ zaproponuje „Replace with method reference". Sprawdź, kiedy ta podpowiedź się pojawia (gdy lambda **tylko** wywołuje jedną metodę).
3. Spróbuj zamienić `(a, b) -> a + b` (BiFunction Integer Integer Integer) na referencję — `Integer::sum`. Działa.
4. Spróbuj zamienić `s -> "PREFIX " + s` na referencję. Nie da się — dlaczego?

### Pytania kontrolne

1. Wymień cztery formy referencji do metod.
2. Czym `String::toUpperCase` różni się od `s -> s.toUpperCase()`?
3. Dlaczego `s -> "Hello " + s` **nie** może być zapisane jako referencja?
