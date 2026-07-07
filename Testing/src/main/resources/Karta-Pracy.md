# Karta pracy -- Testowanie w Javie (JUnit 5, Mockito, AssertJ, TDD)

---

## Jak korzystać z tej karty pracy

Każde zadanie zawiera **gotowy kod produkcyjny** do skopiowania oraz **opis testów do napisania**. Twoim zadaniem jest samodzielnie napisać klasę testową dla każdego scenariusza -- rozwiązania nie są tu pokazane.

**Przygotowanie projektu:**

1. Utwórz nowy projekt Maven w IntelliJ IDEA (File → New → Project → Maven)
2. Skopiuj `pom.xml` podany niżej
3. Dla każdego zadania utwórz odpowiedni pakiet w `src/main/java/` (kod produkcyjny) oraz `src/test/java/` (testy)
4. Uruchamiaj testy przez `mvn test` lub zielony przycisk "Run" w IDE

**Wspólny pom.xml:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>testing-homework</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- JUnit 5 -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.2</version>
            <scope>test</scope>
        </dependency>

        <!-- JUnit 5 - Parametryzowane testy -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-params</artifactId>
            <version>5.10.2</version>
            <scope>test</scope>
        </dependency>

        <!-- Mockito -->
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>5.11.0</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-junit-jupiter</artifactId>
            <version>5.11.0</version>
            <scope>test</scope>
        </dependency>

        <!-- AssertJ -->
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <version>3.25.3</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

**Kluczowe skróty (ściąga):**

| Akcja | Skrót (Windows/Linux) | Skrót (macOS) |
|-------|-----------------------|---------------|
| Uruchom test | `Ctrl+Shift+F10` | `⌃⇧R` |
| Uruchom wszystkie testy w klasie | `Ctrl+Shift+F10` (kursor poza metodą) | `⌃⇧R` |
| Generowanie klasy testowej | `Ctrl+Shift+T` | `⌘⇧T` |
| Importy (Optimize) | `Ctrl+Alt+O` | `⌃⌥O` |

---

## Część teoretyczna -- ściąga przed zadaniami

> Przeczytaj tę sekcję przed rozpoczęciem zadań. Wszystkie pojęcia będą Ci tu potrzebne.

### A. Po co w ogóle testujemy?

**Test automatyczny** = kod, który sprawdza inny kod. Uruchamiany na żądanie (lokalnie, w CI), powtarzalny, niezależny od człowieka.

Bez testów:
- bug wychodzi na produkcji, kosztuje **10-100x więcej** niż gdyby został złapany przy commit
- bałeś się refaktoringu, bo "może coś się zepsuje"
- nowy człowiek w zespole nie wie, **jak używać** klasy, którą napisałeś

Z testami:
- **wczesne wykrywanie błędów** -- czerwony test po `Ctrl+S`
- **dokumentacja** -- czytasz testy, widzisz scenariusze użycia
- **refaktoring bez strachu** -- testy chronią przed regresją
- **lepsza architektura** -- testowalny kod = luźno powiązany kod (loosely coupled)

### B. Trzy poziomy testów

| Typ | Co testuje | Przykład | Szybkość | Liczba |
|-----|------------|----------|----------|--------|
| **Jednostkowy (Unit)** | Pojedyncza klasa/metoda **w izolacji** | `Calculator.add(2, 3)` | milisekundy | dużo (setki/tysiące) |
| **Integracyjny** | Współpraca komponentów | `OrderService` + `Repository` + baza | sekundy | średnio (dziesiątki) |
| **End-to-End (E2E)** | Cała aplikacja od UI po bazę | Logowanie przez Selenium | minuty | mało (kilka) |

**Piramida testów:**

```
            /\
           /  \
          / E2E \         ← Mało (drogie, wolne, kruche)
         /------\
        /        \
       / Integr.  \       ← Średnio
      /------------\
     /              \
    /  Jednostkowe   \    ← Dużo (tanie, szybkie, stabilne)
   /------------------\
```

**Zasada:** Im niżej w piramidzie, tym **więcej** testów powinieneś mieć. Test jednostkowy uruchamiasz w 5 ms, test E2E przez Selenium w 30 sekund. Daje 6000x różnicy w cyklu feedbacku.

### C. Anatomia testu -- wzorce AAA i Given-When-Then

Każdy test ma trzy sekcje, oddzielone **pustą linią**:

**Wzorzec AAA (Arrange-Act-Assert):**

```java
@Test
void shouldCalculateDiscountForVipCustomer() {
    // ARRANGE - przygotowanie
    DiscountCalculator calculator = new DiscountCalculator();
    double originalPrice = 100.0;

    // ACT - wykonanie JEDNEJ operacji
    double result = calculator.calculate(originalPrice, "VIP");

    // ASSERT - weryfikacja
    assertEquals(80.0, result, 0.01);
}
```

**Wzorzec Given-When-Then (styl BDD)** -- to samo, inne słowa:

```java
@Test
void givenVipCustomer_whenCalculatingDiscount_thenApplies20PercentOff() {
    // Given (warunki początkowe)
    // When (akcja)
    // Then (rezultat)
}
```

**Zasada:** jeden test = jeden scenariusz. Może mieć kilka asercji, **jeśli dotyczą tego samego zachowania**.

### D. Konwencje nazewnictwa

Wybierz **jedną** konwencję i trzymaj się jej w projekcie:

| Konwencja | Przykład |
|-----------|----------|
| `should...when...` | `shouldThrowException_whenDividingByZero` |
| `given...when...then...` | `givenZeroDivisor_whenDividing_thenThrowsException` |
| `methodName_state_expected` | `divide_byZero_throwsIllegalArgumentException` |
| Prosta nazwa + `@DisplayName` | `@DisplayName("Dzielenie przez zero rzuca wyjątek")` |

Nazwa testu ma odpowiadać na pytanie: **"Co się dzieje i kiedy?"** -- nie "co robi metoda".

### E. JUnit 5 -- ściąga adnotacji

```java
@Test                       // pojedynczy test
@BeforeEach                 // wykonuje się PRZED każdym testem (instancja)
@AfterEach                  // wykonuje się PO każdym teście
@BeforeAll  (static!)       // wykonuje się RAZ przed wszystkimi testami
@AfterAll   (static!)       // wykonuje się RAZ po wszystkich testach
@Nested                     // grupowanie powiązanych testów (klasa wewnętrzna)
@DisplayName("Polski opis") // czytelna nazwa w raporcie
@Disabled("powód")          // tymczasowo wyłączony test
@RepeatedTest(5)            // powtórz test 5 razy
@Tag("slow")                // tagowanie do selektywnego uruchamiania
```

**Cykl życia w skrócie:**

```
@BeforeAll  -- raz na początku
  @BeforeEach -- przed test1
    test1
  @AfterEach -- po test1
  @BeforeEach -- przed test2
    test2
  @AfterEach -- po test2
@AfterAll   -- raz na końcu
```

### F. Asercje JUnit -- najczęściej używane

```java
import static org.junit.jupiter.api.Assertions.*;

assertEquals(expected, actual);                    // równość
assertEquals(expected, actual, delta);             // równość dla double (tolerancja)
assertNotEquals(unexpected, actual);

assertTrue(condition);
assertFalse(condition);

assertNull(object);
assertNotNull(object);

assertSame(expected, actual);                      // ten sam obiekt (==, nie equals)
assertNotSame(unexpected, actual);

assertArrayEquals(expectedArray, actualArray);
assertIterableEquals(expectedList, actualList);

// Wyjątki
assertThrows(IllegalArgumentException.class, () -> calc.divide(10, 0));
assertDoesNotThrow(() -> calc.divide(10, 2));

// Wszystkie asercje wykonają się NIEZALEŻNIE od tego, czy poprzednia się powiodła
assertAll(
    () -> assertEquals(5, calc.add(2, 3)),
    () -> assertEquals(1, calc.subtract(3, 2))
);

// Z komunikatem przy błędzie
assertEquals(5, result, "Suma 2+3 powinna być 5, a była " + result);
```

### G. Testowanie wyjątków

```java
// Wariant 1: sprawdzamy tylko typ
assertThrows(IllegalArgumentException.class, () -> calc.divide(10, 0));

// Wariant 2: przechwytujemy i sprawdzamy komunikat
IllegalArgumentException ex = assertThrows(
    IllegalArgumentException.class,
    () -> calc.divide(10, 0)
);
assertEquals("Nie można dzielić przez zero", ex.getMessage());
assertTrue(ex.getMessage().contains("zero"));

// Sprawdzenie, że NIE rzuca wyjątku
assertDoesNotThrow(() -> calc.divide(10, 2));
```

### H. Testy parametryzowane

Zamiast pisać 7 prawie identycznych testów, piszesz **jeden** z różnymi danymi:

```java
@ParameterizedTest
@ValueSource(strings = {"kajak", "Anna", "abba"})
void shouldDetectPalindrome(String text) {
    assertTrue(StringUtils.isPalindrome(text));
}

@ParameterizedTest
@CsvSource({
    "Password1, true",
    "pass,      false",
    "password,  false"
})
void shouldValidatePassword(String password, boolean expected) {
    assertEquals(expected, PasswordValidator.isValid(password));
}

@ParameterizedTest
@NullAndEmptySource
@ValueSource(strings = {" ", "\t"})
void shouldReturnFalseForBlank(String value) {
    assertFalse(Validator.isValid(value));
}
```

Źródła danych:
- `@ValueSource` -- jedna kolumna prostych wartości
- `@CsvSource` -- wiele kolumn jako CSV
- `@MethodSource` -- statyczna metoda dostarcza złożone obiekty
- `@EnumSource` -- wszystkie wartości z enuma
- `@NullAndEmptySource` -- `null` i pusty string

### I. Mocki -- po co i jak

**Problem:** Twoja klasa `OrderService` używa `EmailService`. Nie chcesz wysyłać prawdziwych maili w testach. Nie chcesz też pisać "drugiej fałszywej implementacji" `EmailService`.

**Rozwiązanie:** Mockito tworzy **automatyczny fałszywy obiekt**, który możesz zaprogramować w teście.

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock                          // automatyczny fałszywy obiekt
    private EmailService emailService;

    @Mock
    private OrderRepository repository;

    @InjectMocks                   // prawdziwy OrderService z wstrzykniętymi mockami
    private OrderService service;

    @Test
    void shouldSendEmailWhenOrderCreated() {
        // Arrange - programujemy mock
        when(repository.save(any())).thenReturn(new Order(1L, "test"));

        // Act
        service.createOrder("test");

        // Assert - weryfikujemy, że metoda została wywołana
        verify(emailService).sendConfirmation("test");
    }
}
```

**Słownik Mockito (zapamiętaj):**

| Polecenie | Co robi |
|-----------|---------|
| `when(mock.method(...)).thenReturn(value)` | "kiedy ktoś wywoła to na mocku, zwróć tę wartość" |
| `when(mock.method(...)).thenThrow(new Ex())` | "kiedy ktoś wywoła to, rzuć wyjątek" |
| `when(mock.method(...)).thenAnswer(inv -> ...)` | dynamiczna odpowiedź (czyta argumenty) |
| `verify(mock).method(arg)` | "sprawdź, że metoda została wywołana z tym argumentem" |
| `verify(mock, times(2)).method(...)` | "...dokładnie 2 razy" |
| `verify(mock, never()).method(...)` | "...NIGDY" |
| `verifyNoInteractions(mock)` | "nikt nigdy nie ruszał tego mocka" |
| `verifyNoMoreInteractions(mock)` | "poza tym co już sprawdziłem, nic więcej" |
| `any()`, `anyString()`, `anyLong()` | matcher: dopasuj cokolwiek danego typu |
| `eq("X")` | matcher: dopasuj dokładnie `"X"` (potrzebne gdy mieszasz `any()` z konkretem) |

**ArgumentCaptor** -- gdy chcesz **zobaczyć**, co zostało przekazane do mocka:

```java
@Captor
ArgumentCaptor<Order> orderCaptor;

@Test
void shouldSaveOrderWithCorrectData() {
    service.createOrder("klawiatura", 150.0);

    verify(repository).save(orderCaptor.capture());

    Order captured = orderCaptor.getValue();
    assertEquals("klawiatura", captured.getName());
    assertEquals(150.0, captured.getPrice());
}
```

**InOrder** -- gdy ważna jest **kolejność** wywołań:

```java
InOrder inOrder = inOrder(repository, emailService);
inOrder.verify(repository).save(any());        // najpierw save
inOrder.verify(emailService).send(any());      // potem send
```

### J. AssertJ -- czytelniejsze asercje

JUnit jest OK, ale przy bardziej złożonych asercjach robi się gadatliwy. **AssertJ** ma **fluent API**:

```java
import static org.assertj.core.api.Assertions.*;

// String
assertThat(name)
    .isNotNull()
    .startsWith("Jan")
    .hasSize(12)
    .containsIgnoringCase("kowalski");

// Liczby
assertThat(price)
    .isPositive()
    .isBetween(10.0, 100.0)
    .isCloseTo(19.99, within(0.01));

// Kolekcje
assertThat(users)
    .hasSize(3)
    .extracting(User::getName)
    .containsExactlyInAnyOrder("Jan", "Anna", "Piotr");

// Wyjątki
assertThatThrownBy(() -> calc.divide(10, 0))
    .isInstanceOf(IllegalArgumentException.class)
    .hasMessageContaining("zero");

// Optional
assertThat(repository.findById(1L))
    .isPresent()
    .get()
    .extracting(User::getName)
    .isEqualTo("Jan");
```

**Zalety AssertJ:**
- czytelność (czyta się jak zdanie)
- IDE auto-completes wszystkie możliwe asercje po `assertThat(...).`
- lepsze komunikaty błędów

### K. TDD w pigułce -- Red, Green, Refactor

**Test-Driven Development** = napisz test **PRZED** implementacją.

```
┌─ 1. RED ─────────────────────────────┐
│ Napisz test, który NIE przechodzi    │  ← bo nie ma jeszcze kodu
│ (kompilacja może nawet się nie udać) │
└──────────────┬───────────────────────┘
               ▼
┌─ 2. GREEN ───────────────────────────┐
│ Dopisz MINIMUM kodu produkcyjnego,   │  ← brzydki kod jest OK
│ żeby test przeszedł                  │
└──────────────┬───────────────────────┘
               ▼
┌─ 3. REFACTOR ────────────────────────┐
│ Posprzątaj kod produkcyjny I testy,  │  ← testy nadal mają przechodzić
│ usuń duplikację, popraw nazwy        │
└──────────────┬───────────────────────┘
               │
               └─→ wróć do RED z kolejnym testem
```

**Reguły TDD (Uncle Bob):**
1. Nie wolno pisać kodu produkcyjnego bez testu, który się **nie powiódł**
2. Nie wolno pisać więcej testu niż wystarczy, żeby się **nie powiódł** (kompilacja = niepowodzenie)
3. Nie wolno pisać więcej kodu produkcyjnego niż wystarczy, żeby test **przeszedł**

**Co daje TDD:**
- 100% sensownego pokrycia kodu (każda linia kodu istnieje, bo była potrzebna do testu)
- lepsze zrozumienie wymagań (zanim zaczniesz pisać kod, musisz wiedzieć, jak go używać)
- testy stają się dokumentacją

### L. Zasady F.I.R.S.T. -- dobry test jest...

| Litera | Znaczenie | Co to znaczy w praktyce |
|--------|-----------|-------------------------|
| **F**ast | Szybki | Cała sucha jednostkowa < 10 sekund |
| **I**ndependent | Niezależny | Test A nie zależy od testu B; mogą biec w dowolnej kolejności |
| **R**epeatable | Powtarzalny | Ten sam wynik za każdym razem (nie używaj `LocalDateTime.now()` w asercji!) |
| **S**elf-validating | Samowalidujący | Zielony albo czerwony -- bez czytania logów przez człowieka |
| **T**imely | Na czas | Pisany razem z kodem produkcyjnym (najlepiej PRZED -- TDD) |

### M. Dobre i złe praktyki -- szybka lista

**✅ Rób:**
- Jeden test = jedno zachowanie
- Czytelne nazwy testów (przeczytaj je głośno -- powinny brzmieć jak zdanie)
- `@BeforeEach` do wspólnego setupu
- Wzorzec AAA z pustymi liniami
- Testuj zachowania publiczne, nie szczegóły implementacji
- Testuj przypadki brzegowe: `null`, `0`, `""`, ujemne, pustą listę, max wartość

**❌ Nie rób:**
- Logiki w teście (jeśli musisz pisać `if/for/while`, masz w teście **drugi bug**)
- `Thread.sleep(...)` w asercji
- Testów zależnych od siebie (wynik jednego potrzebny w drugim)
- Mockowania klas, których nie posiadasz (mockuj **interfejsy** swojej domeny, nie `LocalDateTime`)
- 100% coverage jako celu samego w sobie -- coverage **bez asercji** jest bezwartościowy
- Testów z `assertTrue(true)` lub bez żadnej asercji ("test, który zawsze zielony")

---

## Zadanie 1: Kategoryzacja testów

**Ćwiczone zagadnienia:** rodzaje testów (jednostkowy / integracyjny / E2E), piramida testów.

### Polecenie

Przypisz każdy z poniższych testów do odpowiedniej kategorii (**jednostkowy** / **integracyjny** / **E2E**) i krótko uzasadnij wybór:

1. Test sprawdzający czy metoda `reverse("abc")` zwraca `"cba"`
2. Test sprawdzający czy `UserService` poprawnie zapisuje użytkownika do bazy danych
3. Test logowania użytkownika przez Selenium w przeglądarce Chrome
4. Test sprawdzający czy `OrderService` wysyła email po złożeniu zamówienia (z mockiem `EmailService`)
5. Test sprawdzający przepływ "zamówienie → płatność → wysyłka" przez wszystkie warstwy aplikacji
6. Test pojedynczej metody `Calculator.divide(10, 2)`

### Pytania

1. Który poziom piramidy testów powinien mieć ich **najwięcej**? Dlaczego?
2. Dlaczego testy E2E są wolne i kruche?
3. W jakiej sytuacji test jednostkowy może stać się testem integracyjnym (bez zmiany kodu testu)?

---

## Zadanie 2: Testy dla `Calculator.subtract()`

**Ćwiczone zagadnienia:** `@Test`, `assertEquals`, wzorzec AAA (Arrange-Act-Assert).

### Kod produkcyjny

Utwórz plik: `src/main/java/com/example/Calculator.java`

```java
package com.example;

public class Calculator {

    public int add(int a, int b) {
        return a + b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    public int divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("Nie można dzielić przez zero");
        }
        return a / b;
    }
}
```

### Polecenie

Utwórz klasę testową `SubtractionTest` w `src/test/java/com/example/SubtractionTest.java` i napisz **5 testów** dla metody `subtract()`:

1. Odejmowanie dwóch liczb dodatnich (5 - 3 = 2)
2. Odejmowanie gdy wynik jest ujemny (3 - 5 = -2)
3. Odejmowanie zera (5 - 0 = 5)
4. Odejmowanie od zera (0 - 5 = -5)
5. Odejmowanie liczb ujemnych (-3 - (-2) = -1)

**Wymagania:**
- Każdy test ma osobną metodę z adnotacją `@Test`
- Każda nazwa testu w konwencji `shouldXxx_whenYyy` (np. `shouldReturnPositive_whenSubtractingSmallerFromLarger`)
- Stosuj wzorzec AAA z pustymi liniami oddzielającymi sekcje
- Użyj `@BeforeEach` żeby inicjalizować `Calculator` raz dla wszystkich testów

### Pytania

1. Po co używamy `@BeforeEach` zamiast tworzyć `new Calculator()` w każdym teście?
2. Jaka jest różnica między `@BeforeEach` a `@BeforeAll`?

---

## Zadanie 3: `StringUtils` i palindromy

**Ćwiczone zagadnienia:** projektowanie API, testowanie przypadków brzegowych, `null` i pusty string.

### Polecenie

1. Utwórz klasę `StringUtils` w pakiecie `com.example` z **publiczną statyczną metodą**:

   ```java
   public static boolean isPalindrome(String text)
   ```

   **Wymagania:**
    - Zwraca `true` jeśli tekst jest palindromem
    - Ignoruje wielkość liter (`"Kajak"` to palindrom)
    - `null` i pusty string traktuj jako palindrom
    - (Opcjonalnie) Ignoruje białe znaki

2. Napisz klasę testową `StringUtilsTest` z **5 testami**:
    - Dla palindromu `"kajak"` → `true`
    - Dla palindromu z różną wielkością liter `"Kajak"` → `true`
    - Dla nie-palindromu `"java"` → `false`
    - Dla pustego stringa `""` → `true`
    - Dla `null` → `true`

### Pytania

1. Dlaczego `null` i `""` warto przetestować osobnymi przypadkami?
2. Jak nazwałbyś szósty test dla palindromu z białymi znakami (np. `"A ja"`)?

---

## Zadanie 4: Refaktoryzacja nazw testów

**Ćwiczone zagadnienia:** konwencje nazewnictwa testów (Given-When-Then).

### Polecenie

Przepisz poniższe nazwy testów na konwencję `given_when_then` (np. `givenX_whenY_thenZ`):

```java
void testAdd()                    → ?
void testNullPointer()            → ?
void userLoginTest()              → ?
void shouldCalculatePrice()       → ?
void emptyListSizeTest()          → ?
```

Wypełnij tabelę:

| Oryginalna nazwa | Nowa nazwa (Given-When-Then) |
|------------------|------------------------------|
| `testAdd()` | ? |
| `testNullPointer()` | ? |
| `userLoginTest()` | ? |
| `shouldCalculatePrice()` | ? |
| `emptyListSizeTest()` | ? |

### Pytania

1. Która konwencja jest najczytelniejsza dla nowego członka zespołu?
2. Czy `@DisplayName("Dzielenie przez zero rzuca wyjątek")` może zastąpić długą nazwę metody?

---

## Zadanie 5: `UserValidator` -- testowanie wyjątków

**Ćwiczone zagadnienia:** `assertThrows`, `assertDoesNotThrow`, `@Nested`, weryfikacja komunikatu wyjątku.

### Polecenie

1. Utwórz klasę `UserValidator` w pakiecie `com.example.validation` z metodą:

   ```java
   public void validateEmail(String email)
   ```

   **Wymagania:**
    - Rzuca `NullPointerException` z komunikatem `"Email nie może być null"` gdy email jest `null`
    - Rzuca `IllegalArgumentException` z komunikatem zawierającym `"pusty"` gdy email jest pusty (`""`)
    - Rzuca `IllegalArgumentException` z komunikatem zawierającym `"@"` gdy email nie zawiera znaku `@`
    - Nic nie robi (przechodzi) gdy email jest poprawny

2. Napisz klasę testową `UserValidatorTest` z **4 testami**:
    - Test dla poprawnego emaila `"jan@example.com"` → nie rzuca wyjątku
    - Test dla emaila bez `@` → rzuca `IllegalArgumentException`
    - Test dla pustego emaila `""` → rzuca `IllegalArgumentException`
    - Test dla `null` → rzuca `NullPointerException`

**Wymagania techniczne:**
- W teście dla emaila bez `@` przechwyć wyjątek i sprawdź, że jego komunikat zawiera znak `"@"` (użyj `exception.getMessage().contains(...)`)
- Pogrupuj testy używając `@Nested` z `@DisplayName("Walidacja emaila")`

### Pytania

1. Czym różni się `assertThrows` z `assertTrue(ex.getMessage().contains("..."))` od użycia `hasMessageContaining(...)` w AssertJ?
2. Czy lepiej rzucać `IllegalArgumentException` czy własny `InvalidEmailException`? Uzasadnij.

---

## Zadanie 6: Testy parametryzowane dla `PasswordValidator`

**Ćwiczone zagadnienia:** `@ParameterizedTest`, `@CsvSource`, `@ValueSource`, `@NullAndEmptySource`.

### Polecenie

1. Utwórz klasę `PasswordValidator` w pakiecie `com.example.validation` z metodą:

   ```java
   public static boolean isValid(String password)
   ```

   **Reguły walidacji (wszystkie muszą być spełnione):**
    - Minimum 8 znaków
    - Zawiera przynajmniej jedną cyfrę
    - Zawiera przynajmniej jedną wielką literę

2. Napisz klasę testową `PasswordValidatorTest`.

   **Pierwszy test (parametryzowany z `@CsvSource`)** -- pokrywa wszystkie przypadki:

   ```
   "Password1", true
   "pass",      false   (za krótkie)
   "password",  false   (brak cyfry)
   "password1", false   (brak wielkiej litery)
   "PASSWORD1", true
   "Pass1234",  true
   "Aa1",       false   (za krótkie)
   ```

   **Drugi test** -- `@ParameterizedTest` z `@NullAndEmptySource` sprawdzający, że `null` i `""` zwracają `false`.

   **Trzeci test** -- `@ParameterizedTest` z `@ValueSource(strings = {...})` sprawdzający 3 dowolne nieprawidłowe hasła.

### Pytania

1. Jakie są zalety testów parametryzowanych w porównaniu z pisaniem 7 osobnych metod testowych?
2. Kiedy lepiej użyć `@MethodSource` zamiast `@CsvSource`?

---

## Zadanie 7: Mockowanie `NotificationService`

**Ćwiczone zagadnienia:** `@Mock`, `@InjectMocks`, `when().thenReturn()`, `verify()`, `verify(never())`, `ArgumentCaptor`.

### Kod produkcyjny

Utwórz pakiet `com.example.notification` z następującymi klasami i interfejsami:

```java
// User.java
package com.example.notification;

public class User {
    private final Long id;
    private final String email;
    private final String phoneNumber;

    public User(Long id, String email, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
}
```

```java
// UserRepository.java
package com.example.notification;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
}
```

```java
// EmailSender.java
package com.example.notification;

public interface EmailSender {
    void send(String email, String message);
}
```

```java
// SmsSender.java
package com.example.notification;

public interface SmsSender {
    void send(String phoneNumber, String message);
}
```

```java
// UserNotFoundException.java
package com.example.notification;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("Użytkownik nie istnieje: " + userId);
    }
}
```

```java
// NotificationService.java
package com.example.notification;

public class NotificationService {

    private final UserRepository userRepository;
    private final EmailSender emailSender;
    private final SmsSender smsSender;

    public NotificationService(UserRepository userRepository,
                               EmailSender emailSender,
                               SmsSender smsSender) {
        this.userRepository = userRepository;
        this.emailSender = emailSender;
        this.smsSender = smsSender;
    }

    public void notifyUser(Long userId, String message) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getEmail() != null) {
            emailSender.send(user.getEmail(), message);
        }

        if (user.getPhoneNumber() != null) {
            smsSender.send(user.getPhoneNumber(), message);
        }
    }
}
```

### Polecenie

Napisz klasę `NotificationServiceTest` z czterema testami:

1. **Gdy użytkownik ma email i telefon** -- oba powiadomienia (email + SMS) są wysłane
2. **Gdy użytkownik ma tylko email** -- tylko email zostaje wysłany, SMS NIE jest wysłany (`verify(smsSender, never())...`)
3. **Gdy użytkownik nie istnieje** -- metoda rzuca `UserNotFoundException`, ani email, ani SMS nie są wysyłane
4. **Sprawdzenie treści wiadomości** -- użyj `ArgumentCaptor<String>` żeby przechwycić argument przekazany do `emailSender.send(...)` i zweryfikować, że to faktycznie ta sama wiadomość, którą podałeś

**Wymagania:**
- Adnotuj klasę testową `@ExtendWith(MockitoExtension.class)`
- Użyj `@Mock` na zależnościach i `@InjectMocks` na `NotificationService`

### Pytania

1. Po co używać `verify(never())` zamiast po prostu pominąć asercję?
2. Dlaczego mockujemy `EmailSender` zamiast użyć prawdziwego serwisu mailowego?

---

## Zadanie 8: Koszyk z rabatem -- mockowanie podstawowe (Beginner)

**Ćwiczone zagadnienia:** `@Mock`, `@InjectMocks`, `when/thenReturn`, `verify`, `verifyNoInteractions`.

### Struktura plików

```
src/
├── main/java/com/example/cart/
│   ├── Product.java
│   ├── CartItem.java
│   ├── ProductRepository.java
│   ├── DiscountService.java
│   ├── ProductNotFoundException.java
│   └── ShoppingCartService.java
└── test/java/com/example/cart/
    └── ShoppingCartServiceTest.java   ← Twój plik do napisania
```

### Kod produkcyjny

```java
// Product.java
package com.example.cart;

public class Product {

    private final String id;
    private final String name;
    private final double price;

    public Product(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
}
```

```java
// CartItem.java
package com.example.cart;

public class CartItem {

    private final String productId;
    private final int quantity;

    public CartItem(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
}
```

```java
// ProductRepository.java
package com.example.cart;

import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(String productId);
}
```

```java
// DiscountService.java
package com.example.cart;

public interface DiscountService {
    /**
     * Zwraca procentowy rabat dla klienta (0.0 - 1.0).
     * Np. 0.1 oznacza 10% rabatu.
     */
    double getDiscountForCustomer(String customerId);
}
```

```java
// ProductNotFoundException.java
package com.example.cart;

public class ProductNotFoundException extends RuntimeException {

    private final String productId;

    public ProductNotFoundException(String productId) {
        super("Produkt nie znaleziony: " + productId);
        this.productId = productId;
    }

    public String getProductId() { return productId; }
}
```

```java
// ShoppingCartService.java
package com.example.cart;

import java.util.List;

public class ShoppingCartService {

    private final ProductRepository productRepository;
    private final DiscountService discountService;

    public ShoppingCartService(ProductRepository productRepository,
                               DiscountService discountService) {
        this.productRepository = productRepository;
        this.discountService = discountService;
    }

    public double calculateTotal(String customerId, List<CartItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Koszyk nie może być pusty");
        }

        double subtotal = 0.0;
        for (CartItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(item.getProductId()));
            subtotal += product.getPrice() * item.getQuantity();
        }

        double discount = discountService.getDiscountForCustomer(customerId);
        return subtotal * (1.0 - discount);
    }
}
```

### Polecenie

Napisz klasę `ShoppingCartServiceTest` z **5 testami** w obrębie `@Nested class CalculateTotalTests`:

1. **`shouldCalculateTotalForSingleItem`** -- jeden produkt w koszyku, brak rabatu (`0.0`). Sprawdź wynik i zweryfikuj, że `findById` oraz `getDiscountForCustomer` zostały wywołane.
2. **`shouldApplyDiscountCorrectly`** -- 2 sztuki produktu o cenie `200.0` (subtotal = 400.0), rabat 10% (`0.1`), oczekiwany wynik `360.0` (użyj `isCloseTo(..., within(0.01))`).
3. **`shouldCalculateTotalForMultipleProducts`** -- 1 sztuka P1 (100 zł) + 3 sztuki P2 (50 zł), suma 250 zł, brak rabatu.
4. **`shouldThrowExceptionForEmptyCart`** -- pusty koszyk rzuca `IllegalArgumentException` z komunikatem zawierającym `"pusty"`. Zweryfikuj `verifyNoInteractions` na obu mockach.
5. **`shouldThrowExceptionWhenProductNotFound`** -- produkt `"P999"` nie istnieje (mock zwraca `Optional.empty()`). Sprawdź, że wyrzucony jest `ProductNotFoundException` z komunikatem zawierającym `"P999"`.

**Wymagania:**
- Adnotuj klasę testową `@ExtendWith(MockitoExtension.class)` i `@DisplayName("ShoppingCartService - testy mockowania")`
- Użyj statycznego importu z AssertJ: `import static org.assertj.core.api.Assertions.*;`
- Każdy test musi mieć `@DisplayName` w języku polskim

### Zadanie rozszerzające

Dodaj do `ShoppingCartService` metodę `getCartSummary(String customerId, List<CartItem> items)` zwracającą obiekt `CartSummary` z polami:
- `subtotal` (cena przed rabatem)
- `discount` (kwota rabatu w złotówkach)
- `total` (cena po rabacie)
- `itemCount` (liczba pozycji w koszyku)

Napisz dla niej **3 testy**.

### Pytania

1. Po co używać `verifyNoInteractions` zamiast po prostu pominąć sprawdzenie?
2. Co robi `@InjectMocks` "pod spodem"?

---

## Zadanie 9: Przelew bankowy -- mockowanie błędów (Intermediate)

**Ćwiczone zagadnienia:** `thenThrow`, `verify(never())`, `verifyNoInteractions`, `verifyNoMoreInteractions`, testowanie wielu ścieżek błędów.

### Struktura plików

```
src/
├── main/java/com/example/bank/
│   ├── Account.java
│   ├── TransferLog.java
│   ├── AccountRepository.java
│   ├── AuditLogger.java
│   ├── NotificationService.java
│   ├── InsufficientFundsException.java
│   ├── AccountNotFoundException.java
│   └── TransferService.java
└── test/java/com/example/bank/
    └── TransferServiceTest.java   ← Twój plik do napisania
```

### Kod produkcyjny

```java
// Account.java
package com.example.bank;

public class Account {

    private final String accountNumber;
    private final String ownerName;
    private double balance;

    public Account(String accountNumber, String ownerName, double balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}
```

```java
// TransferLog.java
package com.example.bank;

import java.time.LocalDateTime;

public class TransferLog {

    private final String fromAccount;
    private final String toAccount;
    private final double amount;
    private final boolean success;
    private final String failureReason;
    private final LocalDateTime timestamp;

    public TransferLog(String fromAccount, String toAccount, double amount,
                       boolean success, String failureReason) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.success = success;
        this.failureReason = failureReason;
        this.timestamp = LocalDateTime.now();
    }

    public String getFromAccount() { return fromAccount; }
    public String getToAccount() { return toAccount; }
    public double getAmount() { return amount; }
    public boolean isSuccess() { return success; }
    public String getFailureReason() { return failureReason; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
```

```java
// AccountRepository.java
package com.example.bank;

import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findByAccountNumber(String accountNumber);
    Account save(Account account);
}
```

```java
// AuditLogger.java
package com.example.bank;

public interface AuditLogger {
    void logTransfer(TransferLog transferLog);
}
```

```java
// NotificationService.java
package com.example.bank;

public interface NotificationService {
    void sendTransferConfirmation(String accountNumber, double amount, String recipientAccount);
    void sendTransferFailure(String accountNumber, double amount, String reason);
}
```

```java
// InsufficientFundsException.java
package com.example.bank;

public class InsufficientFundsException extends RuntimeException {

    private final String accountNumber;
    private final double requested;
    private final double available;

    public InsufficientFundsException(String accountNumber, double requested, double available) {
        super(String.format("Konto %s: żądano %.2f, dostępne %.2f", accountNumber, requested, available));
        this.accountNumber = accountNumber;
        this.requested = requested;
        this.available = available;
    }

    public String getAccountNumber() { return accountNumber; }
    public double getRequested() { return requested; }
    public double getAvailable() { return available; }
}
```

```java
// AccountNotFoundException.java
package com.example.bank;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String accountNumber) {
        super("Konto nie znalezione: " + accountNumber);
    }
}
```

```java
// TransferService.java
package com.example.bank;

public class TransferService {

    private final AccountRepository accountRepository;
    private final AuditLogger auditLogger;
    private final NotificationService notificationService;

    public TransferService(AccountRepository accountRepository,
                           AuditLogger auditLogger,
                           NotificationService notificationService) {
        this.accountRepository = accountRepository;
        this.auditLogger = auditLogger;
        this.notificationService = notificationService;
    }

    public void transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Kwota przelewu musi być większa od zera");
        }

        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
            .orElseThrow(() -> new AccountNotFoundException(fromAccountNumber));

        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
            .orElseThrow(() -> new AccountNotFoundException(toAccountNumber));

        if (fromAccount.getBalance() < amount) {
            auditLogger.logTransfer(new TransferLog(
                fromAccountNumber, toAccountNumber, amount, false, "Brak środków"
            ));
            notificationService.sendTransferFailure(
                fromAccountNumber, amount, "Brak wystarczających środków"
            );
            throw new InsufficientFundsException(
                fromAccountNumber, amount, fromAccount.getBalance()
            );
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        auditLogger.logTransfer(new TransferLog(
            fromAccountNumber, toAccountNumber, amount, true, null
        ));
        notificationService.sendTransferConfirmation(
            fromAccountNumber, amount, toAccountNumber
        );
    }
}
```

### Polecenie

Napisz `TransferServiceTest` z testami pogrupowanymi w **4 klasy `@Nested`**:

**A. `@Nested class SuccessfulTransferTests`**

1. `shouldTransferMoneyBetweenAccounts` -- udany przelew 200 zł z konta z saldem 1000 zł na konto z saldem 500 zł. Zweryfikuj:
    - `save` wywołane dokładnie 2 razy (`verify(..., times(2))`)
    - `sendTransferConfirmation` wywołane z poprawnymi argumentami
    - `sendTransferFailure` NIGDY nie wywołane
    - `logTransfer` wywołane

**B. `@Nested class InsufficientFundsTests`**

2. `shouldThrowExceptionWhenInsufficientFunds` -- konto ma 100 zł, próba przelewu 500 zł:
    - Rzuca `InsufficientFundsException`
    - `save` NIGDY nie wywołane
    - `logTransfer` wywołane (logujemy też nieudane próby)
    - `sendTransferFailure` wywołane z `eq("ACC-1"), eq(500.0), anyString()`

3. `shouldNotSendSuccessNotificationWhenInsufficientFunds` -- sprawdź że `sendTransferConfirmation` NIGDY nie zostało wywołane, używając `verifyNoMoreInteractions(notificationService)` po `verify(...).sendTransferFailure(...)`.

**C. `@Nested class AccountNotFoundTests`**

4. `shouldThrowExceptionWhenSenderAccountNotFound` -- konto `"ACC-FAKE"` nie istnieje (`Optional.empty()`). Rzuca `AccountNotFoundException`. `verifyNoInteractions(auditLogger, notificationService)`.

5. `shouldThrowExceptionWhenReceiverAccountNotFound` -- konto nadawcy istnieje, odbiorcy nie. Rzuca wyjątek. Brak interakcji z loggerem i notyfikacjami.

**D. `@Nested class ValidationTests`**

6. `shouldThrowExceptionForNegativeAmount` -- ujemna kwota, `IllegalArgumentException` z komunikatem zawierającym `"większa od zera"`. `verifyNoInteractions` na WSZYSTKICH mockach.

7. `shouldThrowExceptionForZeroAmount` -- kwota zero, `IllegalArgumentException`. `verifyNoInteractions` na WSZYSTKICH mockach.

**Wymagania:**
- Adnotacje: `@ExtendWith(MockitoExtension.class)`, `@DisplayName`, `@BeforeEach` z inicjalizacją kont
- W `@BeforeEach` utwórz `senderAccount = new Account("ACC-1", "Jan Kowalski", 1000.0)` i `receiverAccount = new Account("ACC-2", "Anna Nowak", 500.0)`
- Użyj AssertJ (`assertThatThrownBy(...).isInstanceOf(...).hasMessageContaining(...)`)
- Do mockowania `save` w teście udanego przelewu użyj `when(...).thenAnswer(inv -> inv.getArgument(0))`

### Zadanie rozszerzające

Dodaj do `TransferService` metodę `transferWithLimit(String from, String to, double amount, double dailyLimit)`, która:
- Sprawdza czy kwota nie przekracza dziennego limitu
- Rzuca `DailyLimitExceededException` gdy limit przekroczony
- Loguje próbę przekroczenia limitu

Napisz **3 testy**: udany przelew w limicie, przekroczenie limitu, przelew na granicy limitu.

### Pytania

1. Czym różni się `verifyNoMoreInteractions` od `verifyNoInteractions`?
2. Dlaczego logujemy też **nieudane** próby przelewu? Co byś zmienił, gdyby logger sam rzucał wyjątek?

---

## Zadanie 10: System rezerwacji -- `ArgumentCaptor`, `InOrder`, `thenAnswer` (Advanced)

**Ćwiczone zagadnienia:** `ArgumentCaptor` do weryfikacji złożonych obiektów, `InOrder` do sprawdzania kolejności wywołań, `thenAnswer` do dynamicznych odpowiedzi.

### Struktura plików

```
src/
├── main/java/com/example/reservation/
│   ├── Room.java
│   ├── TimeSlot.java
│   ├── Reservation.java
│   ├── ReservationStatus.java
│   ├── ConfirmationEmail.java
│   ├── RoomRepository.java
│   ├── ReservationRepository.java
│   ├── ReservationEmailService.java
│   ├── ConfirmationCodeGenerator.java
│   ├── EventPublisher.java
│   └── ReservationService.java
└── test/java/com/example/reservation/
    └── ReservationServiceTest.java   ← Twój plik do napisania
```

### Kod produkcyjny

```java
// Room.java
package com.example.reservation;

public class Room {

    private final String roomId;
    private final String name;
    private final int capacity;
    private final boolean hasProjector;

    public Room(String roomId, String name, int capacity, boolean hasProjector) {
        this.roomId = roomId;
        this.name = name;
        this.capacity = capacity;
        this.hasProjector = hasProjector;
    }

    public String getRoomId() { return roomId; }
    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public boolean hasProjector() { return hasProjector; }
}
```

```java
// TimeSlot.java
package com.example.reservation;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimeSlot {

    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public TimeSlot(LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
}
```

```java
// ReservationStatus.java
package com.example.reservation;

public enum ReservationStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}
```

```java
// Reservation.java
package com.example.reservation;

import java.time.LocalDateTime;

public class Reservation {

    private Long id;
    private String confirmationCode;
    private String roomId;
    private String organizerEmail;
    private TimeSlot timeSlot;
    private int attendees;
    private ReservationStatus status;
    private LocalDateTime createdAt;

    public Reservation() {
        this.status = ReservationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getConfirmationCode() { return confirmationCode; }
    public String getRoomId() { return roomId; }
    public String getOrganizerEmail() { return organizerEmail; }
    public TimeSlot getTimeSlot() { return timeSlot; }
    public int getAttendees() { return attendees; }
    public ReservationStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setConfirmationCode(String code) { this.confirmationCode = code; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public void setOrganizerEmail(String email) { this.organizerEmail = email; }
    public void setTimeSlot(TimeSlot timeSlot) { this.timeSlot = timeSlot; }
    public void setAttendees(int attendees) { this.attendees = attendees; }
    public void setStatus(ReservationStatus status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
```

```java
// ConfirmationEmail.java
package com.example.reservation;

public class ConfirmationEmail {

    private final String recipientEmail;
    private final String subject;
    private final String body;

    public ConfirmationEmail(String recipientEmail, String subject, String body) {
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.body = body;
    }

    public String getRecipientEmail() { return recipientEmail; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
}
```

```java
// RoomRepository.java
package com.example.reservation;

import java.util.Optional;

public interface RoomRepository {
    Optional<Room> findById(String roomId);
}
```

```java
// ReservationRepository.java
package com.example.reservation;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
    boolean existsByRoomIdAndTimeSlot(String roomId, TimeSlot timeSlot);
}
```

```java
// ReservationEmailService.java
package com.example.reservation;

public interface ReservationEmailService {
    void sendConfirmation(ConfirmationEmail email);
}
```

```java
// ConfirmationCodeGenerator.java
package com.example.reservation;

public interface ConfirmationCodeGenerator {
    /**
     * Generuje unikalny kod potwierdzenia.
     * Format: "RES-XXXXXXXX" (np. "RES-A1B2C3D4")
     */
    String generate();
}
```

```java
// EventPublisher.java
package com.example.reservation;

public interface EventPublisher {
    void publish(String eventType, String details);
}
```

```java
// ReservationService.java
package com.example.reservation;

public class ReservationService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationEmailService emailService;
    private final ConfirmationCodeGenerator codeGenerator;
    private final EventPublisher eventPublisher;

    public ReservationService(RoomRepository roomRepository,
                              ReservationRepository reservationRepository,
                              ReservationEmailService emailService,
                              ConfirmationCodeGenerator codeGenerator,
                              EventPublisher eventPublisher) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
        this.emailService = emailService;
        this.codeGenerator = codeGenerator;
        this.eventPublisher = eventPublisher;
    }

    public Reservation createReservation(String roomId, String organizerEmail,
                                          TimeSlot timeSlot, int attendees) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException("Sala nie istnieje: " + roomId));

        if (attendees > room.getCapacity()) {
            throw new IllegalArgumentException(
                String.format("Sala %s ma pojemność %d, żądano %d miejsc",
                    room.getName(), room.getCapacity(), attendees));
        }

        if (reservationRepository.existsByRoomIdAndTimeSlot(roomId, timeSlot)) {
            throw new IllegalStateException(
                "Sala " + room.getName() + " jest już zarezerwowana w tym terminie");
        }

        String confirmationCode = codeGenerator.generate();

        Reservation reservation = new Reservation();
        reservation.setRoomId(roomId);
        reservation.setOrganizerEmail(organizerEmail);
        reservation.setTimeSlot(timeSlot);
        reservation.setAttendees(attendees);
        reservation.setConfirmationCode(confirmationCode);
        reservation.setStatus(ReservationStatus.CONFIRMED);

        Reservation saved = reservationRepository.save(reservation);

        String subject = "Potwierdzenie rezerwacji sali " + room.getName();
        String body = String.format(
            "Rezerwacja potwierdzona!\nSala: %s\nData: %s\nGodziny: %s - %s\nKod: %s",
            room.getName(),
            timeSlot.getDate(),
            timeSlot.getStartTime(),
            timeSlot.getEndTime(),
            confirmationCode
        );
        emailService.sendConfirmation(new ConfirmationEmail(organizerEmail, subject, body));

        eventPublisher.publish("RESERVATION_CREATED",
            "Rezerwacja " + confirmationCode + " dla sali " + room.getName());

        return saved;
    }
}
```

### Polecenie

Napisz `ReservationServiceTest` z testami pogrupowanymi w **4 klasy `@Nested`**:

**Setup (w klasie testowej):**
- `@BeforeEach` tworzy `room = new Room("ROOM-A", "Sala Konferencyjna A", 20, true)` oraz `timeSlot` na `LocalDate.of(2026, 6, 15)` od 10:00 do 12:00
- Pomocnicza metoda prywatna `setupSuccessfulReservation()` ustawia mocki tak, żeby udana rezerwacja "działała" -- generuje kod `"RES-TEST1234"`, nadaje rezerwacji `id=1L` przez `thenAnswer`
- 3 captory: `@Captor ArgumentCaptor<Reservation> reservationCaptor`, `@Captor ArgumentCaptor<ConfirmationEmail> emailCaptor`, `@Captor ArgumentCaptor<String> stringCaptor`

**A. `@Nested class ArgumentCaptorTests`**

1. `shouldSaveReservationWithCorrectData` -- po wywołaniu `createReservation("ROOM-A", "anna@firma.pl", timeSlot, 10)` przechwyć obiekt przekazany do `save()` i sprawdź: `roomId`, `organizerEmail`, `attendees`, `confirmationCode`, `status == CONFIRMED`.

2. `shouldSendEmailWithCorrectContent` -- przechwyć `ConfirmationEmail` i sprawdź:
    - `recipientEmail` to `"anna@firma.pl"`
    - `subject` zawiera `"Sala Konferencyjna A"`
    - `body` zawiera `"RES-TEST1234"` oraz `"2026-06-15"`

3. `shouldPublishEventWithReservationDetails` -- użyj **dwóch** lokalnych captorów (`ArgumentCaptor.forClass(String.class)`) i sprawdź, że `eventType == "RESERVATION_CREATED"` i `details` zawiera kod rezerwacji oraz nazwę sali.

**B. `@Nested class InOrderTests`**

4. `shouldExecuteOperationsInCorrectOrder` -- użyj `InOrder inOrder = inOrder(roomRepository, reservationRepository, codeGenerator, emailService, eventPublisher)` i sprawdź sekwencję:
    1. `roomRepository.findById("ROOM-A")`
    2. `reservationRepository.existsByRoomIdAndTimeSlot(eq("ROOM-A"), any(TimeSlot.class))`
    3. `codeGenerator.generate()`
    4. `reservationRepository.save(any(Reservation.class))`
    5. `emailService.sendConfirmation(any(ConfirmationEmail.class))`
    6. `eventPublisher.publish(anyString(), anyString())`

**C. `@Nested class ThenAnswerTests`**

5. `shouldGenerateUniqueCodesForMultipleReservations` -- skonfiguruj `codeGenerator.generate()` żeby zwracał `"RES-1"`, `"RES-2"`, `"RES-3"`... używając `AtomicInteger` i `thenAnswer`. Analogicznie nadawaj kolejne `id` rezerwacjom (`AtomicLong` w `thenAnswer` w `save`). Utwórz dwie rezerwacje w różnych terminach i przechwyć **obie** przez `reservationCaptor.capture()` z `verify(..., times(2))`, następnie `captor.getAllValues()`. Sprawdź, że pierwsza ma kod `"RES-1"` i `id=1L`, druga `"RES-2"` i `id=2L`.

**D. `@Nested class ValidationTests`**

6. `shouldThrowWhenRoomNotFound` -- sala `"ROOM-X"` zwraca `Optional.empty()`, oczekujemy `IllegalArgumentException` z komunikatem zawierającym `"nie istnieje"`. `verifyNoInteractions(reservationRepository, emailService, codeGenerator, eventPublisher)`.

7. `shouldThrowWhenTooManyAttendees` -- sala ma pojemność 20, żądamy 25. `IllegalArgumentException` z komunikatem zawierającym `"pojemność"`. Brak interakcji z resztą mocków.

8. `shouldThrowWhenTimeSlotAlreadyBooked` -- `existsByRoomIdAndTimeSlot(...)` zwraca `true`. `IllegalStateException` z komunikatem zawierającym `"zarezerwowana"`. Zweryfikuj `codeGenerator, never()` oraz `reservationRepository, never()` na `save(...)`.

**Wymagania:**
- `@ExtendWith(MockitoExtension.class)` + `@DisplayName`
- Statyczne importy: AssertJ, Mockito, ArgumentMatchers
- AssertJ do asercji, np. `assertThatThrownBy(...).isInstanceOf(...).hasMessageContaining(...)`

### Zadanie rozszerzające

1. Dodaj do `ReservationService` metodę `cancelReservation(String confirmationCode)`:
    - Znajduje rezerwację po kodzie (dodaj `findByConfirmationCode` do `ReservationRepository`)
    - Zmienia status na `CANCELLED`
    - Wysyła email o anulowaniu
    - Publikuje zdarzenie `"RESERVATION_CANCELLED"`

2. Napisz testy używając `ArgumentCaptor` (treść emaila) i `InOrder` (kolejność operacji).

### Pytania

1. Kiedy używać `ArgumentCaptor`, a kiedy wystarczy `verify(mock).method("konkretnaWartość")`?
2. Jak `thenAnswer` różni się od `thenReturn`? Podaj przykład, gdy `thenAnswer` jest niezbędne.

---

## Zadanie 11: Przepisz JUnit na AssertJ

**Ćwiczone zagadnienia:** fluent API AssertJ, czytelność asercji.

### Polecenie

Przepisz poniższe trzy fragmenty asercji JUnit na **jeden fluent łańcuch AssertJ** dla każdego:

```java
// Fragment 1
assertEquals(3, list.size());
assertTrue(list.contains("java"));
assertFalse(list.isEmpty());

// Fragment 2
assertNotNull(user);
assertEquals("Jan", user.getName());
assertTrue(user.getAge() > 18);

// Fragment 3
String[] expected = {"a", "b", "c"};
assertArrayEquals(expected, actual);
```

Wpisz odpowiedzi do tabeli:

| # | Wersja AssertJ |
|---|----------------|
| 1 | ? |
| 2 | ? |
| 3 | ? |

### Pytania

1. Wymień 3 zalety AssertJ względem standardowych asercji JUnit.
2. Co robi `extracting(...)` w AssertJ i kiedy jest przydatne?

---

## Zadanie 12: TDD -- Liczby rzymskie

**Ćwiczone zagadnienia:** cykl Red-Green-Refactor, pisanie testu PRZED implementacją.

### Polecenie

Stosując **TDD krok po kroku**, zaimplementuj klasę `RomanNumerals` z metodą:

```java
public static String toRoman(int number)
```

**Reguły konwersji:**

| Arabska | Rzymska |
|---------|---------|
| 1 | I |
| 4 | IV |
| 5 | V |
| 9 | IX |
| 10 | X |
| 40 | XL |
| 50 | L |
| 90 | XC |
| 100 | C |
| 400 | CD |
| 500 | D |
| 900 | CM |
| 1000 | M |

**Procedura TDD (powtarzaj):**

1. **RED** -- napisz **jeden test** dla najprostszego nieobsłużonego przypadku (np. zacznij od `toRoman(1)` → `"I"`). Uruchom -- test musi **nie przejść**.
2. **GREEN** -- dopisz **minimum kodu** w `toRoman`, żeby test przeszedł (na początku może to być wręcz `return "I";`).
3. **REFACTOR** -- jeśli kod jest brzydki, posprzątaj. Testy nadal muszą przechodzić.
4. Wróć do RED z kolejnym testem.

**Sugerowana kolejność testów:**

```
toRoman(1)    → "I"
toRoman(2)    → "II"
toRoman(3)    → "III"
toRoman(4)    → "IV"
toRoman(5)    → "V"
toRoman(9)    → "IX"
toRoman(10)   → "X"
toRoman(40)   → "XL"
toRoman(58)   → "LVIII"
toRoman(1994) → "MCMXCIV"
toRoman(3999) → "MMMCMXCIX"
```

**Wymagania:**
- Po każdym kroku zatwierdź zmianę (np. mentalnie, lub commitem)
- W ostatecznej wersji klasa testowa powinna mieć **co najmniej 11 testów** (po jednym dla każdego wiersza wyżej)
- Spróbuj zastąpić serię osobnych testów **jednym** `@ParameterizedTest` z `@CsvSource`

### Pytania

1. Co dało Ci pisanie testu PRZED implementacją? Wymień 2 obserwacje.
2. Czy w pewnym momencie kusiło Cię, żeby napisać od razu pełną implementację? Dlaczego TDD od tego odradza?
3. Jakie ograniczenia ma walidacja wejścia w metodzie -- co powinno się stać dla `toRoman(0)` i `toRoman(-5)`? Dopisz testy i obsługę.

---

## Zadanie 13 (rozszerzające): Code coverage z JaCoCo

**Ćwiczone zagadnienia:** raporty pokrycia kodu, interpretacja, świadomość pułapek.

### Polecenie

1. Dodaj do `pom.xml` plugin JaCoCo:

   ```xml
   <build>
       <plugins>
           <plugin>
               <groupId>org.jacoco</groupId>
               <artifactId>jacoco-maven-plugin</artifactId>
               <version>0.8.11</version>
               <executions>
                   <execution>
                       <goals><goal>prepare-agent</goal></goals>
                   </execution>
                   <execution>
                       <id>report</id>
                       <phase>test</phase>
                       <goals><goal>report</goal></goals>
                   </execution>
               </executions>
           </plugin>
       </plugins>
   </build>
   ```

2. Uruchom `mvn clean test` i otwórz raport: `target/site/jacoco/index.html`.

3. Wypełnij tabelę dla **co najmniej 3 swoich klas** produkcyjnych z poprzednich zadań:

   | Klasa | Line coverage | Branch coverage | Czy 100%? Co jest niepokryte? |
      |-------|---------------|-----------------|-------------------------------|
   | `ShoppingCartService` | ? | ? | ? |
   | `TransferService` | ? | ? | ? |
   | `RomanNumerals` | ? | ? | ? |

### Pytania

1. Co oznacza **branch coverage** w porównaniu z **line coverage**? Daj przykład linii, która ma 100% line coverage, ale niepełne branch coverage.
2. Dlaczego "test który wywołuje metody bez asercji" daje wysokie line coverage, ale jest **bezużyteczny**?
3. Czy warto wymagać 100% coverage? Uzasadnij.

---

## Lista skrótów i przydatne pakiety

| Adnotacja / klasa | Pakiet |
|-------------------|--------|
| `@Test`, `@BeforeEach`, `@AfterEach`, `@BeforeAll`, `@AfterAll`, `@Nested`, `@DisplayName`, `@Disabled` | `org.junit.jupiter.api.*` |
| `assertEquals`, `assertTrue`, `assertThrows`, `assertDoesNotThrow`, `assertNotNull` | `org.junit.jupiter.api.Assertions.*` |
| `@ParameterizedTest`, `@ValueSource`, `@CsvSource`, `@MethodSource`, `@EnumSource`, `@NullAndEmptySource` | `org.junit.jupiter.params.*` |
| `@Mock`, `@InjectMocks`, `@Spy`, `@Captor` | `org.mockito.*` |
| `@ExtendWith(MockitoExtension.class)` | `org.mockito.junit.jupiter.MockitoExtension` |
| `when`, `verify`, `mock`, `times`, `never`, `verifyNoInteractions`, `verifyNoMoreInteractions`, `inOrder` | `org.mockito.Mockito.*` |
| `any`, `anyString`, `anyLong`, `anyDouble`, `eq` | `org.mockito.ArgumentMatchers.*` |
| `ArgumentCaptor` | `org.mockito.ArgumentCaptor` |
| `assertThat`, `assertThatThrownBy`, `within` | `org.assertj.core.api.Assertions.*` |

---

## Checklist samokontroli

Po wykonaniu wszystkich zadań sprawdź, czy potrafisz:

- [ ] Wyjaśnić różnicę między testem jednostkowym a integracyjnym
- [ ] Napisać test używając wzorca AAA i Given-When-Then
- [ ] Przetestować, że metoda **rzuca konkretny wyjątek z konkretnym komunikatem**
- [ ] Napisać `@ParameterizedTest` z `@CsvSource`
- [ ] Zmockować zależność, użyć `when().thenReturn()` i `verify()`
- [ ] Użyć `ArgumentCaptor` do przechwycenia argumentu wywołania mocka
- [ ] Sprawdzić **kolejność** wywołań przy pomocy `InOrder`
- [ ] Użyć `thenAnswer` z dynamiczną odpowiedzią mocka
- [ ] Przepisać kilka asercji JUnit na łańcuch AssertJ
- [ ] Zastosować TDD (RED → GREEN → REFACTOR)
- [ ] Wygenerować i odczytać raport JaCoCo
