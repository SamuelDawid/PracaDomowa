# PD 04 — Wypożyczalnia filmów na DVD

## 🎯 Co budujesz

Konsolowy program zarządzający wypożyczalnią filmów. Pracujesz na trzech encjach: **Movie**, **Customer**, **Rental**. Główne operacje:

1. **Wypożyczenie filmu** klientowi — sprawdza, czy film istnieje, czy klient istnieje i nie jest zablokowany, czy klient jest dość duży na kategorię filmu, czy nie przekracza limitu wypożyczeń, czy film nie jest już wypożyczony.
2. **Zwrot filmu** — wpisuje datę zwrotu i nalicza karę za zwłokę.
3. **Wyświetlenie błędu** w czytelnej formie.

Po zaimplementowaniu wszystkiego (Część F) uruchomienie `Main` powinno wypisać coś takiego:

```
=== Movie Rental ===
Renting movieId=1 -> customerId=10  OK (return by 2026-05-10)
Renting movieId=2 -> customerId=11  ERROR: Customer is 10 years old, category DRAMA requires 12+
Renting movieId=3 -> customerId=99  ERROR: Customer with id 99 does not exist
Returning rentalId=1 (5 days late) — fine: 5 PLN
=== Session ended ===
Operations: 5
```

---

## 📦 Struktura projektu

Stwórz projekt Maven (lub zwykły katalog z `src/main/java`). `pom.xml` ustaw na Java 17 (`<maven.compiler.release>17</maven.compiler.release>`). Pakiet główny: `pl.kurs.movierental`. Sugerowana struktura plików:

```
src/main/java/pl/kurs/movierental/
├── domain/
│   ├── Category.java               (Część A)
│   ├── CustomerStatus.java         (Część A)
│   ├── Movie.java                  (Część A)
│   ├── Customer.java               (Część A)
│   └── Rental.java                 (Część A)
├── catalog/
│   └── Catalog.java                (Część B)
├── error/
│   ├── RentalError.java            (Część C — sealed)
│   ├── MovieNotFound.java          (Część C)
│   ├── CustomerNotFound.java       (Część C)
│   ├── CustomerBlocked.java        (Część C)
│   ├── TooYoungForCategory.java    (Część C)
│   ├── MovieAlreadyRented.java     (Część C)
│   ├── RentalLimitExceeded.java    (Część C)
│   ├── RentalException.java        (Część C — checked)
│   └── RentalStateException.java   (Część C — unchecked)
├── service/
│   ├── RentalService.java          (Część D)
│   └── ErrorDescriber.java         (Część E — switch)
├── session/
│   └── SessionLog.java             (Część F — AutoCloseable)
└── Main.java                       (Część F)
```

---

## 🗺️ Plan pracy

Rób w tej kolejności — każda część opiera się na poprzedniej:

1. **A.** Modele domenowe: enumy `CustomerStatus` i `Category`, rekordy `Movie`, `Customer`, `Rental`.
2. **B.** Generyczny `Catalog<T>` (kontener filmów i klientów).
3. **C.** Hierarchia błędów: `sealed interface RentalError` + 6 permitów, `RentalException` (checked), `RentalStateException` (unchecked).
4. **D.** `RentalService` — logika biznesowa (`rent`, `returnMovie`, dwie metody pomocnicze).
5. **E.** `ErrorDescriber.describe(...)` — `switch` pattern matching nad sealed interfejsem.
6. **F.** `SessionLog implements AutoCloseable` + `Main` ze scenariuszem demo.

---

## 🧩 Część A — Modele domenowe

### A1. `CustomerStatus`

Plik: `domain/CustomerStatus.java`. Zwykły enum z trzema stałymi (bez metod, bez pól): `ACTIVE`, `SUSPENDED`, `BLOCKED`.

### A2. `Category` — enum z metodą abstrakcyjną per stałą

Plik: `domain/Category.java`. **Każda stała ma własne wartości trzech metod abstrakcyjnych**: `rentalDays()`, `pricePLN()`, `minimumAge()`. To wzorzec strategii wbudowany w enum.

Szkielet (uzupełnij pozostałe stałe na podstawie tabeli niżej):

```java
public enum Category {
    KIDS {
        @Override public int rentalDays()  { return 14; }
        @Override public int pricePLN()    { return 3; }
        @Override public int minimumAge()  { return 0; }
    },
    // FAMILY, DRAMA, ACTION, HORROR — fill in analogously
    ;

    public abstract int rentalDays();
    public abstract int pricePLN();
    public abstract int minimumAge();
}
```

Wartości:

| Stała   | rentalDays | pricePLN | minimumAge |
|---------|-----------:|---------:|-----------:|
| KIDS    | 14         | 3        | 0          |
| FAMILY  | 7          | 5        | 0          |
| DRAMA   | 7          | 6        | 12         |
| ACTION  | 5          | 7        | 16         |
| HORROR  | 5          | 8        | 18         |

**Sprawdzenie:** `Category.HORROR.minimumAge()` → `18`; `Category.KIDS.pricePLN()` → `3`.

### A3. `Movie` (record)

Plik: `domain/Movie.java`.

| Pole          | Typ        |
|---------------|------------|
| `id`          | `int`      |
| `title`       | `String`   |
| `director`    | `String`   |
| `year`        | `int`      |
| `category`    | `Category` |

**Walidacja w konstruktorze kompaktowym** (rzucaj `IllegalArgumentException` z czytelnym komunikatem):

- `title` nie może być `null` ani pusty/biały,
- `director` nie może być `null` ani pusty/biały,
- `category` nie może być `null`,
- `year >= 1888` (rok pierwszego filmu).

### A4. `Customer` (record)

Plik: `domain/Customer.java`.

| Pole         | Typ              |
|--------------|------------------|
| `id`         | `int`            |
| `firstName`  | `String`         |
| `lastName`   | `String`         |
| `age`        | `int`            |
| `status`     | `CustomerStatus` |

**Walidacja:**

- `firstName`, `lastName` — niepuste,
- `0 <= age <= 130`,
- `status` nie `null`.

### A5. `Rental` (record)

Plik: `domain/Rental.java`.

| Pole                  | Typ                       |
|-----------------------|---------------------------|
| `id`                  | `int`                     |
| `movieId`             | `int`                     |
| `customerId`          | `int`                     |
| `rentDate`            | `LocalDate`               |
| `plannedReturnDate`   | `LocalDate`               |
| `actualReturnDate`    | `Optional<LocalDate>`     |

**Walidacja:**

- żadna z dat nie może być `null`,
- `actualReturnDate` nie może być `null` — dopóki film nie zwrócony, ma być `Optional.empty()` (nie `null`!),
- `plannedReturnDate` nie może być wcześniej niż `rentDate`.

**Dodatkowa metoda:** `Rental withReturn(LocalDate today)` — zwraca **nowy** `Rental` z `actualReturnDate = Optional.of(today)`, reszta pól skopiowana. (Rekordy są niemutowalne — kopiuj.)

> **Uwaga o `Optional` jako polu rekordu:** zwykle `Optional` służy jako typ **zwracany** z metody i nie wkłada się go do pól. Tutaj robimy wyjątek świadomie: rekord `Rental` opisuje **stan** (zwrócone/niezwrócone), a `Optional` precyzyjnie wyraża "może być, może nie być". To jeden z nielicznych sensownych przypadków.

---

## 🧩 Część B — Generyczny `Catalog<T>`

Plik: `catalog/Catalog.java`. **Jedyna generyczna klasa w zadaniu** — pokazuje, że jeden kod obsługuje katalog filmów i katalog klientów. Wewnątrz trzymaj `LinkedHashMap<Integer, T>` (kolejność wstawiania, łatwa nawigacja).

Publiczne API:

| Metoda                              | Zachowanie |
|-------------------------------------|------------|
| `void add(int id, T element)`       | Dodaje element. Rzuca `IllegalArgumentException` jeśli `element == null` lub jeśli `id` już istnieje. |
| `Optional<T> find(int id)`          | Zwraca element zawinięty w `Optional`, lub `Optional.empty()` gdy brak. |
| `boolean contains(int id)`          | Czy `id` jest w katalogu. |
| `int size()`                        | Liczba elementów. |

**Sprawdzenie:**

```java
Catalog<Movie> movies = new Catalog<>();
movies.add(1, new Movie(1, "Shrek", "Adamson", 2001, Category.FAMILY));
Optional<Movie> hit  = movies.find(1);    // present
Optional<Movie> miss = movies.find(999);  // empty
movies.size();                            // 1
```

---

## 🧩 Część C — Hierarchia błędów

### C1. `sealed interface RentalError`

Plik: `error/RentalError.java`. **Sealed** — wymienia dokładnie sześć dozwolonych implementacji. Każda implementacja musi zwracać krótki, techniczny komunikat błędu przez metodę `message()`.

```java
public sealed interface RentalError
        permits MovieNotFound, CustomerNotFound, CustomerBlocked,
                TooYoungForCategory, MovieAlreadyRented, RentalLimitExceeded {
    String message();
}
```

### C2. Sześć rekordów-permitów

Każdy w **osobnym pliku** w pakiecie `error`. Każdy implementuje `RentalError` i zwraca `message()` zgodnie z formatem z tabeli.

**Wzór** (jeden rekord pokazany w pełni — pozostałe pisz analogicznie):

```java
public record MovieNotFound(int movieId) implements RentalError {
    @Override
    public String message() {
        return "Movie with id " + movieId + " does not exist";
    }
}
```

| Rekord                  | Pola                                                     | Format `message()`                                                                |
|-------------------------|----------------------------------------------------------|-----------------------------------------------------------------------------------|
| `MovieNotFound`         | `int movieId`                                            | `Movie with id {movieId} does not exist`                                          |
| `CustomerNotFound`      | `int customerId`                                         | `Customer with id {customerId} does not exist`                                    |
| `CustomerBlocked`       | `int customerId, CustomerStatus status`                  | `Customer with id {customerId} has status {status} and cannot rent`               |
| `TooYoungForCategory`   | `int customerAge, Category category, int minimumAge`     | `Customer is {customerAge} years old, category {category} requires {minimumAge}+` |
| `MovieAlreadyRented`    | `int movieId, String title`                              | `Movie '{title}' is already rented`                                               |
| `RentalLimitExceeded`   | `int customerId, int active, int limit`                  | `c`                                                                               |

### C3. `RentalException` (checked)

Plik: `error/RentalException.java`. Dziedziczy po `Exception` (checked — błąd domenowy, normalna ścieżka; **kod wywołujący musi zdecydować co zrobić**). Niesie strukturalny `RentalError`.

Wymagania:

- konstruktor przyjmuje `RentalError error`,
- wewnątrz: `super(error.message())` + zapamiętaj `error` w polu prywatnym `final`,
- gettera nazwij **`error()`** (zwraca `RentalError`).

### C4. `RentalStateException` (unchecked)

Plik: `error/RentalStateException.java`. Dziedziczy po `RuntimeException` (unchecked — **bug programisty**, np. próba użycia zamkniętej sesji albo zwrotu nieistniejącego wypożyczenia). Konstruktor przyjmuje `String message`.

> **Co tu się dzieje (główne pytanie z lekcji 4):** musisz świadomie wybrać, który wyjątek to checked, a który unchecked.
> - **Checked** (`RentalException`) → klient/film nie istnieje, klient zablokowany, za młody, limit, film wypożyczony — to **przewidywane** sytuacje, użytkownik dostał złe dane.
> - **Unchecked** (`RentalStateException`) → ktoś zwraca `rentalId` którego nie ma, albo używa zamkniętej `SessionLog` — to **bug**, nie powinno się zdarzyć.

---

## 🧩 Część D — `RentalService`

Plik: `service/RentalService.java`. Główna klasa logiki biznesowej.

**Stałe:**

- `public static final int ACTIVE_RENTALS_LIMIT = 3;`
- `public static final int OVERDUE_FINE_PER_DAY_PLN = 1;`

**Pola:**

- `final Catalog<Movie> movies`
- `final Catalog<Customer> customers`
- `final List<Rental> history` (zacznij od pustej `ArrayList<>()`)
- `int nextRentalId = 1;` — sekwencja id wypożyczeń

**Konstruktor** przyjmuje oba katalogi.

### `Rental rent(int customerId, int movieId, LocalDate today) throws RentalException`

**Sprawdzaj reguły w tej kolejności**, przy pierwszym napotkanym problemie rzuć `new RentalException(...)` z odpowiednim permitem `RentalError`:

1. Klient o `customerId` istnieje → inaczej `CustomerNotFound(customerId)`.
2. Film o `movieId` istnieje → inaczej `MovieNotFound(movieId)`.
3. Klient ma status `ACTIVE` → inaczej `CustomerBlocked(customerId, status)`.
4. `customer.age() >= movie.category().minimumAge()` → inaczej `TooYoungForCategory(age, category, minimumAge)`.
5. `activeRentalsForCustomer(customerId) < ACTIVE_RENTALS_LIMIT` → inaczej `RentalLimitExceeded(customerId, active, limit)`.
6. `!isMovieRented(movieId)` → inaczej `MovieAlreadyRented(movieId, title)`.

Jeśli wszystkie reguły OK:

- utwórz `Rental` z `id = nextRentalId++`, `rentDate = today`, `plannedReturnDate = today.plusDays(movie.category().rentalDays())`, `actualReturnDate = Optional.empty()`,
- dodaj do `history`,
- zwróć utworzony `Rental`.

### `int returnMovie(int rentalId, LocalDate today)`

Zwraca **karę w PLN** (0 jeśli na czas / przed czasem).

Reguły:

- znajdź `Rental` o `id == rentalId` w `history` (pętla `for-each`); brak → `throw new RentalStateException("Rental " + rentalId + " not found")`,
- jeśli `actualReturnDate().isPresent()` → `throw new RentalStateException("Rental " + rentalId + " already returned")`,
- **podmień** wpis w `history` (pamiętaj że rekordy są niemutowalne — użyj `withReturn(today)` z A5; do podmiany w liście użyj `history.set(index, ...)`),
- policz `daysLate = ChronoUnit.DAYS.between(plannedReturnDate, today)` (uwaga: zwraca `long`, rzutuj na `int`),
- zwróć `Math.max(0, daysLate) * OVERDUE_FINE_PER_DAY_PLN`.

### `int activeRentalsForCustomer(int customerId)`

Pętla `for-each` po `history`. Zlicz wpisy, gdzie `rental.customerId() == customerId` **oraz** `rental.actualReturnDate().isEmpty()`.

### `boolean isMovieRented(int movieId)`

Pętla `for-each` po `history`. Zwróć `true` przy pierwszym aktywnym wypożyczeniu danego filmu (`movieId` zgadza się i `actualReturnDate().isEmpty()`).

**Wskazówki implementacyjne:**

- Do `Catalog.find(...)` używaj `Optional.isPresent()` / `Optional.get()` — **nie** `ifPresent`/`map`/`orElseGet` (te przyjmują `Consumer`/`Function`/`Supplier` — zakaz w tym zadaniu).
- Sprawdzaj reguły dokładnie w podanej kolejności — testy z tabelki w sekcji "Dodatkowe testy" tego oczekują.
- **Zakaz** `.stream().filter().count()` — pętla `for` + licznik.
- `ChronoUnit.DAYS.between(a, b)` zwraca `long`. Jak `b > a` to dodatnie. Rzutuj na `int`.

---

## 🧩 Część E — `ErrorDescriber.describe`

Plik: `service/ErrorDescriber.java`. Statyczna metoda zamieniająca `RentalError` na czytelny tekst dla użytkownika końcowego — można pozwolić sobie na bogatsze formułowanie niż w `message()` (to drugie miejsce na komunikat).

Klasa: `public final class ErrorDescriber` z prywatnym konstruktorem (utility class — nie tworzymy instancji).

Metoda: `public static String describe(RentalError error)`.

Wewnątrz — **`switch` expression z pattern matching, bez `default`**. Kompilator wymusi exhaustive check dzięki temu, że `RentalError` jest sealed. Jeśli kiedyś dojdzie siódmy permit, kompilator pokaże ten plik na czerwono.

Szkielet (uzupełnij pozostałe cztery case'y):

```java
public static String describe(RentalError error) {
    return switch (error) {
        case MovieNotFound m ->
            "Movie with id " + m.movieId() + " does not exist";
        case TooYoungForCategory t ->
            "Customer is " + t.customerAge() + " years old, category "
                + t.category() + " requires " + t.minimumAge() + "+";
        // CustomerNotFound, CustomerBlocked, MovieAlreadyRented, RentalLimitExceeded — fill in
    };
}
```

> **Uwaga:** `->` w `switch expression` to **składnia switcha**, a nie lambda. To jedyne miejsce w zadaniu, gdzie pojawia się strzałka — i jest dozwolone.

---

## 🧩 Część F — `SessionLog` + `Main`

### F1. `SessionLog implements AutoCloseable`

Plik: `session/SessionLog.java`. Loger sesji używany w `try-with-resources`.

**Stan:**

- `final List<String> entries = new ArrayList<>();`
- `boolean closed = false;`

**Metody:**

| Sygnatura                      | Zachowanie |
|--------------------------------|------------|
| `void log(String line)`        | Jeśli `closed` → rzuć `RentalStateException("Session is already closed")`. Inaczej dodaj linię do `entries` i wypisz na `System.out`. |
| `int entryCount()`             | Liczba zapisanych linii. |
| `@Override void close()`       | **Idempotentne**: jeśli już zamknięte, wracaj. Inaczej ustaw `closed = true` i wypisz `=== Session ended ===` oraz `Operations: {entries.size()}`. |

**Deklaracja:**

```java
public class SessionLog implements AutoCloseable {
    // fields, methods — fill in
}
```

> Dlaczego `RentalStateException` (unchecked)? Bo "logowanie po zamknięciu" to bug programisty, nie sytuacja domenowa. Lekcja 4 to dokładnie ten przypadek.

### F2. `Main`

Plik: `Main.java` (pakiet `pl.kurs.movierental`). Pełen scenariusz pokazujący wszystkie elementy. **Tu dostajesz pełen kod jako referencję integracyjną** — przeanalizuj jak wszystko się składa:

```java
package pl.kurs.movierental;

import pl.kurs.movierental.catalog.Catalog;
import pl.kurs.movierental.domain.*;
import pl.kurs.movierental.error.RentalException;
import pl.kurs.movierental.service.ErrorDescriber;
import pl.kurs.movierental.service.RentalService;
import pl.kurs.movierental.session.SessionLog;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Catalog<Movie> movies = new Catalog<>();
        movies.add(1, new Movie(1, "Shrek",        "Adamson",   2001, Category.FAMILY));
        movies.add(2, new Movie(2, "Pulp Fiction", "Tarantino", 1994, Category.DRAMA));
        movies.add(3, new Movie(3, "Matrix",       "Wachowski", 1999, Category.ACTION));

        Catalog<Customer> customers = new Catalog<>();
        customers.add(10, new Customer(10, "Anna",  "Nowak", 30, CustomerStatus.ACTIVE));
        customers.add(11, new Customer(11, "Tomek", "Maly",  10, CustomerStatus.ACTIVE));

        RentalService service = new RentalService(movies, customers);
        LocalDate today = LocalDate.of(2026, 5, 3);

        try (SessionLog log = new SessionLog()) {
            log.log("=== Movie Rental ===");

            tryRent(service, log, 10, 1, today); // Anna  -> Shrek            — OK
            tryRent(service, log, 11, 2, today); // Tomek -> Pulp Fiction     — TooYoungForCategory
            tryRent(service, log, 99, 3, today); // unknown customer          — CustomerNotFound

            int fine = service.returnMovie(1, today.plusDays(12)); // FAMILY: 7 dni → 5 dni zwloki
            log.log("Returning rentalId=1 (5 days late) — fine: " + fine + " PLN");
        }
    }

    private static void tryRent(RentalService service, SessionLog log,
                                int customerId, int movieId, LocalDate today) {
        try {
            Rental r = service.rent(customerId, movieId, today);
            log.log("Renting movieId=" + movieId + " -> customerId=" + customerId
                  + "  OK (return by " + r.plannedReturnDate() + ")");
        } catch (RentalException ex) {
            log.log("Renting movieId=" + movieId + " -> customerId=" + customerId
                  + "  ERROR: " + ErrorDescriber.describe(ex.error()));
        }
    }
}
```

---

## ✅ Kryteria odbioru

Po zaimplementowaniu wszystkiego uruchom `Main`. Output **musi** odpowiadać sample output ze sekcji "🎯 Co budujesz". Sprawdź ręcznie:

1. **OK** dla pary `(Anna id=10, Shrek id=1)` — wyświetla `return by 2026-05-10` (`today + 7 dni` dla `FAMILY`).
2. **ERROR `TooYoungForCategory`** dla `(Tomek 10 lat, Pulp Fiction DRAMA 12+)`.
3. **ERROR `CustomerNotFound`** dla klienta o id 99.
4. **Zwrot po terminie** zwraca karę = 5 PLN (5 dni × 1 PLN).
5. **Pasek końcowy** od `SessionLog.close()`: `=== Session ended ===` + `Operations: 5`.

---

## 🧪 Dodatkowe testy do napisania samodzielnie

Sprawdź **każdy z sześciu permitów `RentalError`** co najmniej raz, klasycznym `try`/`catch`:

| Test | Setup                                                    | Oczekiwany permit          |
|------|----------------------------------------------------------|----------------------------|
| 1    | klient nie istnieje (id 999)                             | `CustomerNotFound`         |
| 2    | film nie istnieje (id 999)                               | `MovieNotFound`            |
| 3    | klient ma `CustomerStatus.BLOCKED`                       | `CustomerBlocked`          |
| 4    | klient 10 lat + film `HORROR` (18+)                      | `TooYoungForCategory`      |
| 5    | film aktualnie wypożyczony przez kogoś innego            | `MovieAlreadyRented`       |
| 6    | klient ma 3 aktywne wypożyczenia, próbuje 4.             | `RentalLimitExceeded`      |

Do rozróżniania użyj **`instanceof` z pattern**:

```java
try {
    service.rent(customerId, movieId, today);
} catch (RentalException ex) {
    if (ex.error() instanceof TooYoungForCategory t) {
        System.out.println("Customer too young: " + t.customerAge() + " < " + t.minimumAge());
    }
}
```

---

## ⚙️ Wymagania techniczne (musi się pojawić)

- **Java 17+** (`<maven.compiler.release>17</maven.compiler.release>`).
- **Generyki**: klasa `Catalog<T>` (Część B).
- **Enum z metodą abstrakcyjną per stałą**: `Category` (Część A2).
- **`Optional`** jako typ zwracany: `Catalog.find(...)`. **`Optional` jako pole rekordu** (świadomie, opisane w A5): `Rental.actualReturnDate`.
- **Rekordy**: wszystkie modele domenowe (3) + wszystkie permity sealed (6) → ≥ 9 rekordów.
- **`sealed interface` + `permits`**: `RentalError` (Część C1).
- **`switch` pattern matching bez `default`**: `ErrorDescriber.describe` (Część E).
- **Checked wyjątek z własnej hierarchii**: `RentalException` (Część C3).
- **Unchecked wyjątek z własnej hierarchii**: `RentalStateException` (Część C4).
- **`try-with-resources` z własnym `AutoCloseable`**: `SessionLog` użyty w `Main` (Część F).
- **`instanceof` z pattern**: w testach z tabelki wyżej.

---