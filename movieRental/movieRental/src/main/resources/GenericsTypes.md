# Lekcja: **Typy Generyczne w Javie**

## 🎯 Cel lekcji

* Zrozumieć **czym są generyki** i dlaczego zostały wprowadzone do Javy
* Poznać podstawową składnię: **klasy generyczne**, **interfejsy generyczne**, **metody generyczne**
* Nauczyć się bezpiecznej pracy z kolekcjami używając parametryzacji typów
* Zrozumieć **ograniczenia typów** (`bounded type parameters`)
* Poznać **wildcardy**: `?`, `? extends`, `? super` i regułę **PECS**
* Poznać mechanizm **Type Erasure** i jego konsekwencje
* Zrozumieć zaawansowane wzorce: **Type Token**, **CRTP**, **wildcard capture**

---

# CZĘŚĆ I: PODSTAWY GENERYKÓW

---

## 1. Po co generyki? Problem bez generyków

Przed Javą 5 kolekcje przechowywały obiekty typu `Object`. Oznaczało to konieczność ręcznego rzutowania i brak ochrony przed błędami typów.

### Problem — kod bez generyków

```java
List lista = new ArrayList();
lista.add("Java");
lista.add(42);              // kompilator nie protestuje
lista.add(3.14);            // też nie

String s = (String) lista.get(1); // BOOM! ClassCastException w runtime
```

**Co poszło nie tak?** Kompilator nie wiedział, co ma być w liście. Błąd wyszedł dopiero po uruchomieniu programu.

### Rozwiązanie — generyki

```java
List<String> lista = new ArrayList<>();
lista.add("Java");
// lista.add(42);           // BŁĄD KOMPILACJI — kompilator chroni nas od razu
// lista.add(3.14);         // BŁĄD KOMPILACJI

String s = lista.get(0);    // Bezpiecznie, bez rzutowania
```

**Generyki dają nam:**
* **Bezpieczeństwo typów** — błędy wykrywane w czasie kompilacji, nie w runtime
* **Kod wielokrotnego użytku** — klasy i metody działają dla wielu typów
* **Czytelność** — od razu widać, czego metoda oczekuje i co zwraca

➡️ **Mini-zadanie:** Stwórz `ArrayList` bez parametru typu (raw type). Dodaj do niej `String`, `Integer` i `Double`. Spróbuj odczytać drugi element jako `String`. Zaobserwuj `ClassCastException`. Następnie dodaj parametr `<String>` i sprawdź, co się zmieni.

---

## 2. Klasy generyczne — pierwsze kroki

Klasa generyczna to klasa z **parametrem typu** — zamiast sztywno wpisywać typ, używamy symbolu (np. `T`), który zostaje zastąpiony konkretnym typem przy tworzeniu obiektu.

### Definicja klasy generycznej

```java
public class Pudelko<T> {
    private T zawartosc;

    public Pudelko(T zawartosc) {
        this.zawartosc = zawartosc;
    }

    public T pobierz() {
        return zawartosc;
    }

    public void ustaw(T zawartosc) {
        this.zawartosc = zawartosc;
    }

    @Override
    public String toString() {
        return "Pudelko[" + zawartosc + "]";
    }
}
```

### Użycie

```java
Pudelko<String> pudelkoTekstu = new Pudelko<>("Cześć!");
String tekst = pudelkoTekstu.pobierz(); // "Cześć!" — bez rzutowania

Pudelko<Integer> pudelkoLiczby = new Pudelko<>(42);
int liczba = pudelkoLiczby.pobierz();   // 42

Pudelko<List<String>> pudelkoListy = new Pudelko<>(List.of("a", "b"));
List<String> lista = pudelkoListy.pobierz();
```

**Kluczowe:** `T` to **parametr typu** — zastępczy symbol, który zostaje zastąpiony konkretnym typem (`String`, `Integer`, `List<String>`, ...) przy tworzeniu instancji.

### Analogia — etykietowane pudełko

Wyobraź sobie `Pudelko<T>` jako **puste pudełko z miejscem na etykietę**. Samo pudełko nie wie, co będzie w środku — to etykieta (`T`) decyduje. Gdy piszesz `Pudelko<String>`, naklejasz etykietę "String" i od tej pory:
- Do pudełka można włożyć **tylko** String
- Wyjmując coś z pudełka, **wiesz na pewno**, że to String — nie musisz sprawdzać

### Jak to działa krok po kroku

Gdy piszesz `Pudelko<String>`, kompilator mentalnie **zastępuje każde `T` typem `String`**:

```java
// Definicja ogólna (szablon):        // Po "wklejeniu" String za T:
public class Pudelko<T> {             // public class Pudelko<String> {
    private T zawartosc;              //     private String zawartosc;
    public T pobierz() {              //     public String pobierz() {
        return zawartosc;             //         return zawartosc;
    }                                 //     }
}                                     // }
```

Dlatego `pudelkoTekstu.pobierz()` zwraca `String`, a `pudelkoLiczby.pobierz()` zwraca `Integer` — każde pudełko "wie", jaki typ przechowuje.

### Klasa z dwoma parametrami typu

```java
public class Para<L, P> {
    private final L lewy;
    private final P prawy;

    public Para(L lewy, P prawy) {
        this.lewy = lewy;
        this.prawy = prawy;
    }

    public L getLewy() { return lewy; }
    public P getPrawy() { return prawy; }

    @Override
    public String toString() {
        return "(" + lewy + ", " + prawy + ")";
    }
}

// Użycie:
Para<String, Integer> osoba = new Para<>("Ala", 25);
System.out.println(osoba); // (Ala, 25)
```

➡️ **Mini-zadanie:** Napisz klasę generyczną `Trojka<A, B, C>` przechowującą trzy wartości różnych typów. Dodaj konstruktor, gettery i `toString()`. Stwórz obiekt `Trojka<String, Integer, Boolean>` z wartościami `("Jan", 30, true)` i wypisz go.

---

## 3. Interfejsy generyczne i operator diamentu `<>`

### Interfejs generyczny

Interfejsy też mogą mieć parametry typu. Przykład — prosty magazyn obiektów:

```java
public interface Magazyn<T> {
    void dodaj(T element);
    T pobierz(int indeks);
    int rozmiar();
}
```

### Implementacja interfejsu generycznego

```java
public class ListowyMagazyn<T> implements Magazyn<T> {
    private final List<T> elementy = new ArrayList<>();

    @Override
    public void dodaj(T element) {
        elementy.add(element);
    }

    @Override
    public T pobierz(int indeks) {
        return elementy.get(indeks);
    }

    @Override
    public int rozmiar() {
        return elementy.size();
    }
}

// Użycie:
Magazyn<String> magazynTekstow = new ListowyMagazyn<>();
magazynTekstow.dodaj("Java");
magazynTekstow.dodaj("Python");
String jezyk = magazynTekstow.pobierz(0); // "Java"
```

### Operator diamentu `<>` (Java 7+)

Od Javy 7 kompilator potrafi **wnioskować typ** po prawej stronie — nie trzeba powtarzać parametrów:

```java
// Stary sposób (Java 5–6):
Map<String, List<Integer>> mapa = new HashMap<String, List<Integer>>();

// Nowy sposób (Java 7+) — operator diamentu:
Map<String, List<Integer>> mapa = new HashMap<>();

// Java 10+ — var dla zmiennych lokalnych:
var lista = new ArrayList<String>(); // kompilator wie, że to ArrayList<String>
```

➡️ **Mini-zadanie:** Stwórz interfejs generyczny `Konwerter<Z, NA>` z metodą `NA konwertuj(Z wartosc)`. Napisz implementację `StringNaInteger`, która konwertuje `String` na `Integer` (używając `Integer.parseInt`). Przetestuj konwertując `"123"` na liczbę.

---

## 4. Metody generyczne

Metody generyczne mają **własne parametry typu** — niezależne od klasy, w której się znajdują. Parametr typu deklarujemy **przed typem zwracanym**.

### Składnia

```java
//        ↓ parametr typu metody
public static <T> T pierwszyLubDomyslny(List<T> lista, T domyslny) {
    return lista.isEmpty() ? domyslny : lista.get(0);
}
```

### Przykłady

```java
public class Narzedzia {
    private Narzedzia() {} // klasa narzędziowa, bez instancji

    public static <T> T pierwszyLubDomyslny(List<T> lista, T domyslny) {
        return lista.isEmpty() ? domyslny : lista.get(0);
    }

    public static <T> void wypiszWszystko(List<T> lista) {
        for (T element : lista) {
            System.out.println(element);
        }
    }

    public static <T> List<T> powtorz(T element, int ile) {
        List<T> wynik = new ArrayList<>();
        for (int i = 0; i < ile; i++) {
            wynik.add(element);
        }
        return wynik;
    }
}

// Użycie — kompilator sam wnioskuje T:
String s = Narzedzia.pierwszyLubDomyslny(List.of("a", "b"), "brak");  // T = String
int n = Narzedzia.pierwszyLubDomyslny(List.of(1, 2, 3), 0);          // T = Integer

List<String> lista = Narzedzia.powtorz("hej", 3); // ["hej", "hej", "hej"]
```

### Jak kompilator wnioskuje typ?

Nie musisz pisać `Narzedzia.<String>pierwszyLubDomyslny(...)` — kompilator sam **patrzy na argumenty** i dedukuje typ `T`:

1. Widzi, że pierwszy argument to `List.of("a", "b")` → czyli `List<String>`
2. Widzi, że drugi argument to `"brak"` → czyli `String`
3. Wnioskuje: `T = String`

Możesz jednak podać typ jawnie, jeśli kompilator ma problem z wnioskowaniem:

```java
// Jawne podanie typu — rzadko potrzebne, ale czasem przydatne:
String s = Narzedzia.<String>pierwszyLubDomyslny(List.of(), "domyslny");
```

### Praktyczny przykład — konwersja listy na mapę

Metody generyczne świetnie nadają się do tworzenia narzędzi wielokrotnego użytku:

```java
// Konwertuje listę obiektów na mapę, gdzie kluczem jest wynik funkcji keyExtractor
public static <T, K> Map<K, T> doMapy(List<T> lista, Function<T, K> keyExtractor) {
    Map<K, T> mapa = new HashMap<>();
    for (T element : lista) {
        K klucz = keyExtractor.apply(element);  // wyciągamy klucz z elementu
        mapa.put(klucz, element);
    }
    return mapa;
}

// Użycie — lista użytkowników → mapa po emailu:
List<User> uzytkownicy = List.of(new User("jan@wp.pl", "Jan"), new User("ola@wp.pl", "Ola"));
Map<String, User> poEmailu = doMapy(uzytkownicy, User::getEmail);
// {"jan@wp.pl" → User("Jan"), "ola@wp.pl" → User("Ola")}
```

**Jedna metoda** obsługuje dowolny typ listy i dowolny typ klucza — to właśnie siła generyków.

➡️ **Mini-zadanie:** Napisz metodę statyczną generyczną `<T> int policz(List<T> lista, T szukany)`, która zlicza ile razy dany element występuje na liście (porównując przez `equals`). Przetestuj z listą stringów i listą liczb.

---

## 5. Konwencje nazewnictwa parametrów typu

W Javie obowiązuje konwencja jednoliterowych nazw parametrów typu:

| Litera | Znaczenie | Gdzie się używa | Przykład |
|--------|-----------|-----------------|----------|
| `T` | **Type** — typ ogólny | Klasy i metody generyczne | `class Box<T>` |
| `E` | **Element** | Kolekcje | `interface List<E>` |
| `K` | **Key** — klucz | Mapy | `interface Map<K, V>` |
| `V` | **Value** — wartość | Mapy | `interface Map<K, V>` |
| `R` | **Result** — wynik | Typy funkcyjne | `interface Function<T, R>` |
| `N` | **Number** — liczba | Konteksty liczbowe | `class Stats<N extends Number>` |
| `S`, `U` | Typy pomocnicze | Gdy potrzeba więcej parametrów | `class Triple<T, U, S>` |

**Dlaczego wielkie litery?** To konwencja, która pozwala od razu odróżnić parametr typu (`T`) od nazwy klasy (`String`, `Integer`).

---

## 6. Ograniczenia typów (Bounded Type Parameters)

Czasem chcemy, żeby `T` nie był „dowolnym typem", ale typem z określonej hierarchii. Służą do tego **ograniczenia**.

### Dlaczego potrzebujemy ograniczeń?

Bez ograniczenia `T` może być **czymkolwiek** — `String`, `Boolean`, `Object`. Kompilator nie pozwoli Ci wtedy wywołać żadnej metody specyficznej dla konkretnego typu:

```java
// BEZ ograniczenia — nie zadziała:
public class Statystyka<T> {                      // T = cokolwiek
    public double srednia() {
        for (T liczba : liczby) {
            suma += liczba.doubleValue();          // BŁĄD! T może być String — String nie ma doubleValue()
        }
    }
}

// Z ograniczeniem — działa:
public class Statystyka<T extends Number> {       // T = Number lub podklasa
    public double srednia() {
        for (T liczba : liczby) {
            suma += liczba.doubleValue();          // OK! Kompilator wie, że T na pewno ma doubleValue()
        }
    }
}
```

**Ograniczenie `extends` to obietnica**: „T nie będzie byle czym — będzie co najmniej `Number`". Dzięki temu kompilator udostępnia metody z `Number`.

### Górne ograniczenie — `<T extends ...>`

```java
public class Statystyka<T extends Number> {
    private final List<T> liczby;

    public Statystyka(List<T> liczby) {
        if (liczby.isEmpty()) {
            throw new IllegalArgumentException("Lista nie może być pusta");
        }
        this.liczby = liczby;
    }

    public double srednia() {
        double suma = 0.0;
        for (T liczba : liczby) {
            suma += liczba.doubleValue(); // możemy to wywołać, bo T extends Number
        }
        return suma / liczby.size();
    }
}

// Użycie:
var statInt = new Statystyka<>(List.of(1, 2, 3, 4));
System.out.println(statInt.srednia()); // 2.5

var statDouble = new Statystyka<>(List.of(1.5, 2.5, 3.5));
System.out.println(statDouble.srednia()); // 2.5

// var statString = new Statystyka<>(List.of("a", "b")); // BŁĄD — String nie extends Number
```

**Kluczowe:** `<T extends Number>` oznacza: „T musi być Number lub jego podklasą" (`Integer`, `Double`, `Long`, ...). Dzięki temu kompilator wie, że na `T` można wywołać metody z `Number` (np. `doubleValue()`).

### Wielokrotne ograniczenia

Typ może spełniać kilka wymagań naraz — jedną klasę i wiele interfejsów:

```java
// T musi być liczbą ORAZ porównywalny
public static <T extends Number & Comparable<T>> T znajdzMaksimum(List<T> lista) {
    if (lista.isEmpty()) {
        throw new IllegalArgumentException("Lista pusta");
    }
    T max = lista.get(0);
    for (T element : lista) {
        if (element.compareTo(max) > 0) {
            max = element;
        }
    }
    return max;
}

// Użycie:
int max = znajdzMaksimum(List.of(3, 1, 7, 2)); // 7
```

**Uwaga:** Jeśli jest klasa, musi być **pierwsza**: `<T extends Number & Comparable<T>>` (nie odwrotnie).

➡️ **Mini-zadanie:** Napisz metodę generyczną `<T extends Number> double suma(List<T> liczby)`, która zwraca sumę wszystkich liczb z listy (używając `doubleValue()`). Przetestuj z `List<Integer>`, `List<Double>` i `List<Long>`.

---

# CZĘŚĆ II: WILDCARDY I ZASADA PECS

---

## 7. Problem inwariancji — dlaczego `List<Integer>` to nie `List<Number>`

Intuicyjnie mogłoby się wydawać, że skoro `Integer` jest podtypem `Number`, to `List<Integer>` powinien być podtypem `List<Number>`. **Ale tak nie jest.** Generyki w Javie są **inwariantne**.

### Dlaczego? Bezpieczeństwo typów

```java
List<Integer> listaInteger = new ArrayList<>();
listaInteger.add(1);
listaInteger.add(2);

// Gdyby to było dozwolone:
// List<Number> listaNumber = listaInteger;  // ← BŁĄD KOMPILACJI (i dobrze!)
// listaNumber.add(3.14);                    // Double jest Number — ok?
// Integer i = listaInteger.get(2);          // KATASTROFA! Dostalibyśmy Double zamiast Integer
```

Kompilator blokuje to przypisanie, bo pozwolenie na nie otworzyłoby drogę do wstawienia niekompatybilnych typów.

### Analogia — automat z napojami

Wyobraź sobie `List<Integer>` jako **automat, który wydaje tylko Integer**. Gdyby Java pozwoliła przypisać go do `List<Number>`, ktoś mógłby powiedzieć: „Hej, Number obejmuje też Double, wrzucę Double!" — ale automat wewnątrz dalej jest ustawiony na Integer. Gdy ktoś później wyciągnie element oczekując Integer, dostanie Double i program się wysypie.

### Krok po kroku — dlaczego kompilator mówi NIE

```java
List<Integer> oryginalna = new ArrayList<>(List.of(1, 2, 3));

// Załóżmy, że kompilator BY POZWOLIŁ na to przypisanie (w rzeczywistości NIE pozwala):
List<Number> alias = oryginalna;     // krok 1: tworzymy "alias" do tej samej listy

alias.add(3.14);                     // krok 2: dodajemy Double — bo Number obejmuje Double

Integer i = oryginalna.get(3);       // krok 3: KATASTROFA! Pod indeksem 3 siedzi Double (3.14)
                                     //         a my próbujemy przypisać do Integer → ClassCastException
```

**Wniosek:** Java blokuje krok 1, żeby kroki 2 i 3 nigdy nie mogły się wydarzyć. To właśnie jest **inwariancja** — ochrona przed cichym wstawieniem niekompatybilnego typu.

### Ale tablice SĄ kowariantne (i to jest problem)

```java
Number[] tablicaNumber = new Integer[3]; // OK w kompilacji
tablicaNumber[0] = 3.14;                // ArrayStoreException w RUNTIME!
```

**Wniosek:** generyki są bezpieczniejsze od tablic — błąd widać od razu przy kompilacji, a nie dopiero po uruchomieniu.

**Jak rozwiązać?** Potrzebujemy **wildcardów** — mechanizmu, który pozwala elastycznie pracować z hierarchią typów.

---

## 8. Wildcard `?` — nieznany typ

`List<?>` oznacza „lista **czegoś** — nie wiemy czego". Możemy z niej **czytać** (jako `Object`), ale **nie możemy dodawać** (poza `null`), bo nie wiemy, jakiego typu elementy lista przechowuje.

```java
public static void wypiszInformacje(List<?> lista) {
    System.out.println("Rozmiar: " + lista.size());
    System.out.println("Pusta? " + lista.isEmpty());

    for (Object element : lista) {   // czytamy jako Object
        System.out.println("  - " + element);
    }

    // lista.add("coś");  // BŁĄD KOMPILACJI — nie wiemy co jest w liście
}

// Działa z dowolną listą:
wypiszInformacje(List.of("a", "b", "c"));
wypiszInformacje(List.of(1, 2, 3));
wypiszInformacje(List.of(true, false));
```

**Kiedy używać `?`?** Gdy metoda tylko **czyta** z kolekcji i nie potrzebuje znać konkretnego typu.

➡️ **Mini-zadanie:** Napisz metodę `boolean saRowneDlugosci(List<?> a, List<?> b)`, która sprawdza czy dwie listy (dowolnych typów) mają tę samą liczbę elementów. Przetestuj z `List<String>` i `List<Integer>`.

---

## 9. `? extends` — kowariancja (producent danych)

`List<? extends Number>` oznacza „lista Number-ów **lub dowolnego podtypu** Number". Możemy bezpiecznie **czytać** elementy jako `Number`, ale **nie możemy dodawać** (bo nie wiemy, czy lista to `List<Integer>`, `List<Double>`, czy `List<Long>`).

### Przykład — sumowanie różnych typów liczbowych

```java
public static double suma(List<? extends Number> liczby) {
    double wynik = 0.0;
    for (Number n : liczby) {       // czytamy jako Number — bezpieczne
        wynik += n.doubleValue();
    }
    return wynik;
}

// Działa dla WSZYSTKICH podtypów Number:
System.out.println(suma(List.of(1, 2, 3)));           // List<Integer> → 6.0
System.out.println(suma(List.of(1.5, 2.5)));           // List<Double>  → 4.0
System.out.println(suma(List.of(1L, 2L, 3L)));         // List<Long>    → 6.0
```

### Dlaczego nie można dodawać?

```java
public static void nieMozna(List<? extends Number> lista) {
    Number n = lista.get(0);      // OK — czytanie bezpieczne
    // lista.add(42);             // BŁĄD! Co jeśli to List<Double>? 42 to Integer!
    // lista.add(3.14);           // BŁĄD! Co jeśli to List<Integer>?
}
```

**Zapamiętaj:** `? extends` = **mogę czytać, nie mogę dodawać**. Lista jest **producentem** danych.

### Tabela — co wolno z `? extends`?

| Operacja | Dozwolone? | Dlaczego |
|----------|------------|----------|
| Odczyt elementu jako `Number` | TAK | Każdy element **na pewno jest** Number (lub podklasą) |
| Odczyt elementu jako `Integer` | NIE | Lista może być `List<Double>` — nie wiadomo jaki dokładnie podtyp |
| Dodanie `Integer` | NIE | Lista może być `List<Double>` — Integer by nie pasował |
| Dodanie `null` | TAK | `null` pasuje do każdego typu referencyjnego |
| Sprawdzenie `size()`, `isEmpty()` | TAK | Te metody nie zależą od typu elementów |

➡️ **Mini-zadanie:** Napisz metodę `Number znajdzMinimum(List<? extends Number> liczby)`, która znajduje najmniejszą wartość na liście (porównuj przez `doubleValue()`). Przetestuj z `List<Integer>` i `List<Double>`.

---

## 10. `? super` — kontrawariancja (konsument danych)

`List<? super Integer>` oznacza „lista Integer-ów **lub dowolnego nadtypu** Integer" (`List<Integer>`, `List<Number>`, `List<Object>`). Możemy bezpiecznie **dodawać** `Integer`, ale **czytać** możemy tylko jako `Object`.

### Przykład — dodawanie elementów do listy

```java
public static void dodajLiczby(List<? super Integer> lista) {
    lista.add(1);    // OK — Integer na pewno pasuje
    lista.add(2);
    lista.add(3);

    // Integer i = lista.get(0);  // BŁĄD! Może to być List<Object>
    Object obj = lista.get(0);    // OK — zawsze możemy czytać jako Object
}

// Działa dla Integer i wszystkich nadtypów:
List<Integer> listaInt = new ArrayList<>();
List<Number> listaNum = new ArrayList<>();
List<Object> listaObj = new ArrayList<>();

dodajLiczby(listaInt);  // OK
dodajLiczby(listaNum);  // OK
dodajLiczby(listaObj);  // OK
```

**Zapamiętaj:** `? super` = **mogę dodawać, czytam jako Object**. Lista jest **konsumentem** danych.

### Tabela — co wolno z `? super`?

| Operacja | Dozwolone? | Dlaczego |
|----------|------------|----------|
| Dodanie `Integer` | TAK | Lista przechowuje Integer lub nadtyp — Integer na pewno pasuje |
| Dodanie `Number` | NIE | Lista może być `List<Integer>` — Number nie musi być Integer |
| Odczyt elementu jako `Integer` | NIE | Lista może być `List<Object>` — element mógłby być czymkolwiek |
| Odczyt elementu jako `Object` | TAK | Wszystko w Javie jest `Object` — zawsze bezpieczne |

### Jakie listy pasują do `List<? super Integer>`?

`? super Integer` oznacza: „Integer **lub dowolny nadtyp** Integer w hierarchii dziedziczenia":

```
Object              ← List<Object>           ✅ pasuje
  └── Number        ← List<Number>           ✅ pasuje
        └── Integer ← List<Integer>          ✅ pasuje
        └── Double  ← List<Double>           ❌ NIE pasuje (Double to nie nadtyp Integer)
  └── String        ← List<String>           ❌ NIE pasuje
```

➡️ **Mini-zadanie:** Napisz metodę `void dodajParzyste(List<? super Integer> cel, List<Integer> zrodlo)`, która kopiuje ze źródła do celu tylko liczby parzyste. Przetestuj dodając z `List<Integer>` do `List<Number>`.

---

## 11. Reguła PECS — Producer Extends, Consumer Super

**PECS** to mnemonik, który mówi kiedy używać jakiego wildcardu:

* **Producer Extends** — jeśli kolekcja **produkuje** dane (czytamy z niej) → `? extends T`
* **Consumer Super** — jeśli kolekcja **konsumuje** dane (piszemy do niej) → `? super T`

### Klasyczny przykład — kopiowanie kolekcji

```java
public static <T> void kopiuj(List<? extends T> zrodlo, List<? super T> cel) {
//                                  ↑ PRODUCENT                ↑ KONSUMENT
//                              czytamy z niej             piszemy do niej
    for (T element : zrodlo) {
        cel.add(element);
    }
}

// Użycie:
List<Integer> zrodlo = List.of(1, 2, 3);
List<Number> cel = new ArrayList<>();
kopiuj(zrodlo, cel); // Integer extends Number — działa!
System.out.println(cel); // [1, 2, 3]
```

### Tabela podsumowująca

| Sytuacja | Wildcard | Można czytać jako | Można dodawać | Przykład |
|----------|----------|-------------------|---------------|----------|
| **Producent** (czytamy) | `? extends T` | `T` | NIE (poza `null`) | `suma(List<? extends Number>)` |
| **Konsument** (piszemy) | `? super T` | `Object` | `T` i podtypy | `dodaj(List<? super Integer>)` |
| **Nie znamy typu** | `?` | `Object` | NIE (poza `null`) | `rozmiar(List<?>)` |
| **Czytamy i piszemy** | bez wildcardu | `T` | `T` | `sort(List<T>)` |

### Praktyczna wskazówka

Gdy projektujesz API metody:
* Parametr, z którego **tylko czytasz** → `? extends T`
* Parametr, do którego **tylko piszesz** → `? super T`
* Parametr, z którym robisz **jedno i drugie** → po prostu `T` (bez wildcardu)

➡️ **Mini-zadanie:** Napisz metodę `<T> void filtrujIDodaj(List<? extends T> zrodlo, Predicate<T> warunek, List<? super T> cel)`, która kopiuje ze źródła do celu tylko elementy spełniające warunek. Przetestuj: filtruj `List<Integer>` (liczby > 5) do `List<Number>`.

---

# CZĘŚĆ III: ZAAWANSOWANE TEMATY

---

## 12. Type Erasure — wymazywanie typów

**Type Erasure** to mechanizm, dzięki któremu **generyki w Javie istnieją tylko w czasie kompilacji**. Po kompilacji informacja o parametrach typu jest **usuwana**.

### Dlaczego tak jest?

Generyki pojawiły się w Javie 5 (2004). Żeby stary kod (bez generyków) dalej działał na tym samym JVM, zdecydowano o **kompatybilności wstecznej** — parametry typu znikają w bajtkodzie.

### Jak to działa?

Kod źródłowy:
```java
List<String> lista = new ArrayList<>();
lista.add("Java");
String s = lista.get(0);
```

Po kompilacji (uproszczenie — tak widzi to JVM):
```java
List lista = new ArrayList();       // <String> zniknął — JVM widzi tylko "List"
lista.add("Java");                  // JVM nie sprawdza typu — ufa kompilatorowi
String s = (String) lista.get(0);   // kompilator automatycznie wstawił rzutowanie
```

### Pełniejszy przykład — klasa generyczna przed i po erasure

```java
// KOD ŹRÓDŁOWY (co piszesz):
public class Sorter<T extends Comparable<T>> {
    private T wartosc;

    public void ustaw(T wartosc) {
        this.wartosc = wartosc;
    }

    public int porownajZ(T inny) {
        return wartosc.compareTo(inny);     // możemy wywołać, bo T extends Comparable
    }
}

// PO ERASURE (co widzi JVM):
public class Sorter {                       // parametr <T> zniknął
    private Comparable wartosc;             // T zamienione na GÓRNE OGRANICZENIE (Comparable)

    public void ustaw(Comparable wartosc) { // T → Comparable
        this.wartosc = wartosc;
    }

    public int porownajZ(Comparable inny) { // T → Comparable
        return wartosc.compareTo(inny);     // wywołanie na Comparable — ok
    }
}
```

**Zasada erasure:** `T` zostaje zastąpione przez **górne ograniczenie** (`Comparable` w tym przypadku). Gdyby nie było ograniczenia (`<T>` bez `extends`), `T` zostałoby zastąpione przez `Object`.

### Konsekwencje Type Erasure

1. **Brak informacji o typie w runtime:**
```java
List<String> a = new ArrayList<>();
List<Integer> b = new ArrayList<>();
System.out.println(a.getClass() == b.getClass()); // true! Obie to po prostu ArrayList
```

2. **Nie można używać `instanceof` z parametryzacją:**
```java
// if (obj instanceof List<String>) {} // BŁĄD KOMPILACJI
   if (obj instanceof List<?>) {}      // OK — wildcard dozwolony
```

3. **Nie można tworzyć instancji ani tablic typu `T`:**
```java
// T obiekt = new T();           // BŁĄD
// T[] tablica = new T[10];      // BŁĄD
```

### Bridge methods (metody pomostowe)

Gdy klasa implementuje interfejs generyczny, kompilator czasem generuje dodatkowe metody „pomostowe", żeby zachować polimorfizm po erasure:

```java
public class NazwaComparable implements Comparable<NazwaComparable> {
    private final String nazwa;

    public NazwaComparable(String nazwa) { this.nazwa = nazwa; }

    @Override
    public int compareTo(NazwaComparable o) {
        return nazwa.compareTo(o.nazwa);
    }
}
// W bajtkodzie powstaje dodatkowy "bridge": compareTo(Object) → compareTo(NazwaComparable)
```

---

## 13. Surowe typy (Raw Types) — dlaczego ich unikać

**Surowy typ** to typ generyczny użyty **bez parametru**: `List` zamiast `List<String>`. Istnieją dla kompatybilności ze starym kodem sprzed generyków.

### Dlaczego są niebezpieczne?

```java
List surowa = new ArrayList();    // raw type — brak parametru
surowa.add("tekst");
surowa.add(123);                  // kompilator nie protestuje
surowa.add(3.14);

List<String> bezpieczna = surowa; // unchecked warning — kompilator ostrzega
String s = bezpieczna.get(1);     // ClassCastException w runtime!
```

### Porównanie: raw type vs parametryzowany vs wildcard

```java
List surowa = new ArrayList();         // RAW — brak ochrony, nie używaj
List<String> typowana = new ArrayList<>();  // PARAMETRYZOWANA — bezpieczna
List<?> wildcard = new ArrayList<>();  // WILDCARD — bezpieczna, elastyczna
```

**Zasada:** **ZAWSZE** podawaj parametr typu. Jeśli nie znasz typu, użyj `<?>`, a nie surowego typu.

---

## 14. Ograniczenia generyków w Javie

Lista rzeczy, których **nie można** robić z generykami (ze względu na Type Erasure):

### 1. Nie można tworzyć instancji typu `T`

```java
// ❌ BŁĄD:
// T obiekt = new T();

// ✅ Rozwiązanie — przekaż Supplier:
public static <T> T stworzDomyslny(Supplier<T> fabryka) {
    return fabryka.get();
}
String s = stworzDomyslny(String::new); // ""
```

### 2. Nie można tworzyć tablic typów generycznych

```java
// ❌ BŁĄD:
// List<String>[] tablica = new List<String>[10];

// ✅ Rozwiązanie — użyj listy list:
List<List<String>> listaList = new ArrayList<>();
```

### 3. Nie można używać `instanceof` z parametryzacją

```java
// ❌ BŁĄD:
// if (obj instanceof List<String>) {}

// ✅ Dozwolone — wildcard:
if (obj instanceof List<?>) {
    List<?> lista = (List<?>) obj;
}
```

### 4. Nie można deklarować wyjątków generycznych

```java
// ❌ BŁĄD:
// class MojWyjatek<T> extends Exception {}
```

### 5. Metody nie mogą się różnić TYLKO parametryzacją

```java
// ❌ BŁĄD — po erasure obie to m(List):
// void m(List<String> a) {}
// void m(List<Integer> b) {}

// ✅ Rozwiązanie — zmień nazwę:
void przetworzStringi(List<String> a) {}
void przetworzLiczby(List<Integer> b) {}
```

### 6. Pola statyczne nie mogą używać parametru typu klasy

```java
class Pudelko<T> {
    // ❌ BŁĄD:
    // private static T domyslna;

    // ✅ OK — metoda statyczna z WŁASNYM parametrem:
    public static <U> Pudelko<U> puste() {
        return new Pudelko<>(null);
    }
}
```

---

## 15. Wildcard Capture — przechwycenie wildcardu

### Motywacja — po co to?

Wyobraź sobie sytuację: dostajesz z zewnętrznego API obiekt typu `List<?>`. Wiesz, że wszystkie elementy na liście są **tego samego typu** (np. same Stringi albo same Integer), ale kompilator tego nie wie — widzi tylko `?`. Wildcard capture pozwala Ci **nadać temu nieznanemu typowi tymczasową nazwę** (`T`), dzięki czemu możesz wykonywać operacje wymagające znajomości typu.

### Problem

Mamy `List<?>` i chcemy zamienić dwa elementy. Ale `List<?>` nie pozwala na zapis (bo nie znamy typu):

```java
public static void zamien(List<?> lista, int i, int j) {
    // Object temp = lista.get(i);
    // lista.set(i, lista.get(j));  // BŁĄD! Nie możemy pisać do List<?>
    // lista.set(j, temp);
}
```

### Rozwiązanie — metoda pomocnicza z `<T>`

Delegujemy do prywatnej metody z własnym parametrem typu. Kompilator „przechwytuje" (capture) konkretny typ z `?` i przypisuje go do `T`:

```java
public static void zamien(List<?> lista, int i, int j) {
    zamienHelper(lista, i, j); // kompilator "łapie" typ z ?
}

private static <T> void zamienHelper(List<T> lista, int i, int j) {
    T temp = lista.get(i);       // OK — znamy typ T
    lista.set(i, lista.get(j));  // OK
    lista.set(j, temp);          // OK
}

// Działa:
List<String> nazwy = new ArrayList<>(List.of("Ala", "Ola", "Ewa"));
zamien(nazwy, 0, 2);
System.out.println(nazwy); // [Ewa, Ola, Ala]
```

**Kiedy potrzebujesz capture?** Gdy masz `List<?>` i musisz wykonać operację **zapisu**, która wymaga znajomości typu elementów.

➡️ **Mini-zadanie:** Napisz metodę `odwroc(List<?> lista)` używając wildcard capture, która odwraca kolejność elementów na liście. Użyj prywatnej metody pomocniczej `<T> odwrocHelper(List<T>)`.

---

## 16. Type Token — zachowanie informacji o typie w runtime

Przez Type Erasure tracimy informację o typie w runtime. Ale czasem jej potrzebujemy (np. przy deserializacji JSON). Rozwiązaniem jest **Type Token** — przekazanie `Class<T>` jako parametru.

### Prosty przykład — typowany magazyn

```java
public class MagazynTypow {
    private final Map<Class<?>, Object> mapa = new HashMap<>();

    public <T> void zapisz(Class<T> typ, T obiekt) {
        mapa.put(typ, obiekt);
    }

    public <T> T pobierz(Class<T> typ) {
        return typ.cast(mapa.get(typ)); // bezpieczne rzutowanie przez Class.cast()
    }

    public <T> boolean zawiera(Class<T> typ) {
        return mapa.containsKey(typ);
    }
}

// Użycie:
MagazynTypow magazyn = new MagazynTypow();
magazyn.zapisz(String.class, "Java");
magazyn.zapisz(Integer.class, 42);

String s = magazyn.pobierz(String.class);  // "Java" — bezpiecznie
Integer n = magazyn.pobierz(Integer.class); // 42
```

### TypeReference w bibliotekach

Popularne biblioteki (Jackson, Gson) używają wzorca `TypeReference` / `TypeToken` do zachowania informacji o złożonych typach generycznych:

```java
// Jackson — odczyt JSON z pełną informacją o typie:
List<User> users = objectMapper.readValue(json, new TypeReference<List<User>>() {});

// Gson:
List<User> users = gson.fromJson(json, new TypeToken<List<User>>() {}.getType());
```

**Jak to działa?** Tworząc anonimową podklasę (`new TypeReference<...>() {}`), informacja o parametrze typu jest zachowana w metadanych klasy — bo dotyczy **klasy**, nie **instancji**.

➡️ **Mini-zadanie:** Rozszerz `MagazynTypow` o metodę `<T> T pobierzLubDomyslny(Class<T> typ, T domyslny)`, która zwraca zapisany obiekt lub wartość domyślną, jeśli nie ma wpisu dla danego typu.

---

## 17. Rekurencyjne ograniczenia typów (CRTP)

Wzorzec **CRTP** (Curiously Recurring Template Pattern) polega na tym, że klasa bazowa ma parametr typu ograniczony do samej siebie. W Javie najczęściej spotykany w **builderach z fluent API**.

### Problem — builder bez CRTP

```java
class BazowyBuilder {
    protected String nazwa;

    public BazowyBuilder ustawNazwe(String nazwa) {
        this.nazwa = nazwa;
        return this; // zwraca BazowyBuilder, nie podklasę!
    }
}

class ProduktBuilder extends BazowyBuilder {
    private double cena;

    public ProduktBuilder ustawCene(double cena) {
        this.cena = cena;
        return this;
    }
}

// Problem:
// new ProduktBuilder().ustawNazwe("Laptop").ustawCene(3999);
// ↑ ustawNazwe() zwraca BazowyBuilder, nie ProduktBuilder → nie ma metody ustawCene()!
```

### Rozwiązanie — CRTP

```java
abstract class BazowyBuilder<T extends BazowyBuilder<T>> {
    protected String nazwa;

    public T ustawNazwe(String nazwa) {
        this.nazwa = nazwa;
        return self(); // zwraca T, czyli podklasę
    }

    protected abstract T self();
}

class ProduktBuilder extends BazowyBuilder<ProduktBuilder> {
    private double cena;

    public ProduktBuilder ustawCene(double cena) {
        this.cena = cena;
        return this;
    }

    @Override
    protected ProduktBuilder self() {
        return this;
    }

    public String build() {
        return nazwa + " za " + cena + " zł";
    }
}

// Teraz działa:
String produkt = new ProduktBuilder()
    .ustawNazwe("Laptop")  // zwraca ProduktBuilder (nie BazowyBuilder)
    .ustawCene(3999)
    .build();
System.out.println(produkt); // "Laptop za 3999.0 zł"
```

**Zastosowanie:** CRTP to zaawansowany wzorzec — warto go znać, ale na co dzień rzadko się go pisze od zera.

### Kiedy używać CRTP w praktyce?

CRTP przydaje się, gdy budujesz **hierarchię klas z fluent API** (łańcuchowe wywoływanie metod), gdzie metody bazowe muszą zwracać typ podklasy. Przykłady z prawdziwego świata:

- **Lombok `@SuperBuilder`** — automatycznie generuje builder z CRTP, żeby podklasy mogły korzystać z metod buildera klasy bazowej
- **Hibernate Criteria API** — zapytania bazodanowe budowane łańcuchowo
- **Protobuf Builders** — generowane klasy Message używają CRTP do fluent API

**Reguła kciuka:** Jeśli nie budujesz hierarchii builderów lub frameworka z fluent API — prawdopodobnie nie potrzebujesz CRTP. Ale gdy spotkasz go w cudzym kodzie (np. w bibliotece), będziesz wiedział co się dzieje.

---

# CZĘŚĆ IV: PRAKTYKA I DOBRE PRAKTYKI

---

## 18. Dobre praktyki

1. **Zawsze parametryzuj typy** — unikaj raw types (`List` → `List<String>`)
2. **Stosuj PECS** — Producer Extends, Consumer Super
3. **Preferuj metody generyczne nad rzutowanie** — zamiast `(String) obj` napisz metodę z `<T>`
4. **Nazywaj parametry zgodnie z konwencją** — `T`, `E`, `K`, `V`, `R`
5. **Używaj bounded types, gdy masz wymagania** — `<T extends Comparable<T>>` zamiast rzutowań
6. **Oznaczaj bezpieczne varargs `@SafeVarargs`** — ale tylko w metodach `static`, `final` lub konstruktorach
7. **Minimalizuj zakres wildcardów** — metody pomocnicze z capture są czytelniejsze niż skomplikowane sygnatury

---

## 19. Typowe błędy i pułapki

### 1. `List<Integer>` to podtyp `List<Number>` — NIE!
Generyki są **inwariantne**. Użyj `List<? extends Number>` jeśli potrzebujesz czytać.

### 2. Próba dodawania do `List<? extends T>`
To **producent** — można czytać, nie można dodawać. Użyj `? super T` dla konsumenta.

### 3. Używanie raw types „dla szybkości"
Tracisz całe bezpieczeństwo typów. **Zawsze** podawaj `<>`.

### 4. Tworzenie tablic typów generycznych
`new List<String>[10]` nie zadziała. Użyj `List<List<String>>`.

### 5. Przeciążanie metod różniące się tylko parametryzacją
Po erasure `m(List<String>)` i `m(List<Integer>)` to ta sama sygnatura `m(List)`.

### 6. Rzutowanie z wildcard zamiast capture
Zamiast `(List<String>) listaWildcard` użyj metody pomocniczej z `<T>`.

---

## 20. 📝 Zadania końcowe

### Zadanie 1: Klasa generyczna `Skrzynka<T>`

Zaprojektuj klasę `Skrzynka<T>` opakowującą listę elementów. Zaimplementuj metody:
- `void dodaj(T element)` — dodaje element
- `boolean usun(T element)` — usuwa element, zwraca czy się udało
- `boolean zawiera(T element)` — sprawdza czy element jest w skrzynce
- `List<T> pobierzWszystkie()` — zwraca niemutowalną kopię
- `int rozmiar()` — zwraca liczbę elementów
- `String toString()` — czytelna reprezentacja

Przetestuj z `Skrzynka<String>` i `Skrzynka<Integer>`.

---

### Zadanie 2: Metoda z PECS

Napisz metodę:
```java
public static <T> List<T> filtruj(
    List<? extends T> zrodlo,
    Predicate<? super T> warunek
)
```

Która zwraca **nową listę** zawierającą tylko elementy spełniające warunek.

Przetestuj: filtruj `List<Integer>` zostawiając tylko liczby parzyste.

---

### Zadanie 3: `StatystykaLiczb<T extends Number>`

Zaimplementuj klasę ze statystykami dla listy liczb:
- `double srednia()` — średnia arytmetyczna
- `double suma()` — suma
- `T maksimum()` — największy element (wymaga `Comparable`)
- `T minimum()` — najmniejszy element

**Wskazówka:** Do porównywania użyj `doubleValue()` lub wymagaj `T extends Number & Comparable<T>`.

Przetestuj z `List<Integer>` → `[1, 5, 3, 9, 2]` i `List<Double>` → `[1.5, 2.7, 0.3]`.

---

### Zadanie 4: `Para<L, P>` implementująca `Comparable`

Rozbuduj klasę `Para<L extends Comparable<L>, P extends Comparable<P>>`:
- Zaimplementuj `Comparable<Para<L, P>>` — porównuj najpierw lewy, potem prawy element
- Dodaj `equals()` i `hashCode()`

Stwórz listę par i posortuj ją za pomocą `Collections.sort()`.

---

### Zadanie 5: Wildcard capture — tasowanie

Napisz metodę `void tasuj(List<?> lista)`, która miesza elementy w losowej kolejności. Użyj wildcard capture (metoda pomocnicza z `<T>`) i `Collections.shuffle()`.

---

### Zadanie 6 (zaawansowane): Uniwersalny konwerter list

Napisz metodę:
```java
public static <Z, NA> List<NA> przeksztalc(
    List<? extends Z> zrodlo,
    Function<? super Z, ? extends NA> konwerter
)
```

Która przekształca listę jednego typu na listę innego typu.

Przetestuj: `List<String>` → `List<Integer>` (konwersja `Integer::parseInt`).

---

## 📌 Podsumowanie

Na tej lekcji nauczyliśmy się:

* **Czym są generyki** i dlaczego zwiększają bezpieczeństwo kodu
* Jak tworzyć **klasy**, **interfejsy** i **metody** generyczne
* Jak działają **ograniczenia typów** (`extends`)
* Czym jest **inwariancja** i dlaczego `List<Integer>` to nie `List<Number>`
* Jak używać **wildcardów**: `?`, `? extends`, `? super`
* Zasadę **PECS**: Producer Extends, Consumer Super
* Mechanizm **Type Erasure** i jego konsekwencje
* Zaawansowane wzorce: **wildcard capture**, **Type Token**, **CRTP**
* **Dobre praktyki** i typowe pułapki

**Kluczowe do zapamiętania:**
* **PECS** — Producer extends, Consumer super
* **Type Erasure** — parametry typów znikają w runtime
* **Inwariancja** — `List<Integer>` ≠ `List<Number>`, ale wildcardy dają elastyczność
* **Zawsze parametryzuj** — nigdy raw types

---

## 📖 Dodatkowe materiały

* [Oracle Tutorial – Generics](https://docs.oracle.com/javase/tutorial/java/generics/)
* [Baeldung – Java Generics](https://www.baeldung.com/java-generics)
* [Effective Java – Joshua Bloch, rozdział o generykach](https://www.oreilly.com/library/view/effective-java-3rd/9780134686097/)
* [Angelika Langer – Java Generics FAQ](http://www.angelikalanger.com/GenericsFAQ/JavaGenericsFAQ.html)
* [GeeksforGeeks – Generics in Java](https://www.geeksforgeeks.org/generics-in-java/)

---

✅ **Koniec lekcji – gotowa notatka**
