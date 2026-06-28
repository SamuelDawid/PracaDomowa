# Praca domowa: Adnotacje w Javie

## Zadanie 1: Walidator hasła z adnotacjami

Stwórz system walidacji hasła oparty na własnej adnotacji.

### Wymagania:

1. **Utwórz adnotację `@PasswordValidation`**:
    - Może być używana na polach (`ElementType.FIELD`)
    - Jest dostępna w runtime (`RetentionPolicy.RUNTIME`)
    - Parametry:
        - `minLength` (int) - minimalna długość hasła, domyślnie 8
        - `requireDigit` (boolean) - czy wymagana jest cyfra, domyślnie true
        - `requireSpecialChar` (boolean) - czy wymagany jest znak specjalny, domyślnie false

2. **Utwórz klasę `User`**:
    - Pole `String password` oznaczone adnotacją `@PasswordValidation`
    - Skonfiguruj adnotację: minLength=10, requireDigit=true, requireSpecialChar=true
    - Konstruktor przyjmujący hasło i getter

3. **Utwórz klasę `PasswordValidator`**:
    - Metoda statyczna `boolean validate(Object obj)` która:
        - Przez refleksję znajduje pola z adnotacją `@PasswordValidation` (użyj `getDeclaredFields()`, `isAnnotationPresent()`, `getAnnotation()`)
        - Odczytuje wartość pola - hasło (użyj `setAccessible(true)` i `field.get(obj)`)
        - Sprawdza reguły z adnotacji (długość, obecność cyfry, obecność znaku specjalnego)
        - Zwraca `true` jeśli hasło spełnia wszystkie reguły, `false` w przeciwnym razie

4. **Utwórz klasę `Main`** z metodą `main` która przetestuje walidator na kilku obiektach User z różnymi hasłami.

---

## Zadanie 2: Adnotacje wbudowane JDK

Przećwicz użycie wbudowanych adnotacji z JDK.

### Wymagania:

1. **Utwórz klasę `Pojazd`**:
    - Metoda `String opis()` zwracająca "To jest pojazd"
    - Metoda `void staraMetoda()` wypisująca cokolwiek na konsolę

2. **Utwórz klasę `Samochod` dziedziczącą po `Pojazd`**:
    - Nadpisz metodę `opis()` używając adnotacji `@Override`, zwróć "To jest samochód"
    - Nadpisz metodę `staraMetoda()` i oznacz ją jako `@Deprecated` z parametrami `since="1.0"` i `forRemoval=true`
    - Dodaj nową metodę `void nowaMetoda()`

3. **Utwórz interfejs funkcyjny `Obliczenie`**:
    - Oznacz adnotacją `@FunctionalInterface`
    - Jedna metoda abstrakcyjna: `int wykonaj(int a, int b)`

4. **Utwórz klasę `Main`** z metodą `main` która:
    - Tworzy obiekty Pojazd i Samochod, wywołuje ich metody
    - Używa interfejsu Obliczenie z wyrażeniami lambda (np. dodawanie, mnożenie)

5. **Przetestuj wszystkie adnotacje z biblioteki Lombok**:

   Dodaj do projektu zależność Lombok (Maven/Gradle) oraz wtyczkę Lombok w IDE. Utwórz osobne klasy demonstrujące działanie każdej z poniższych adnotacji i sprawdź w klasie `Main`, jak wpływają one na wygenerowany kod (np. przez wywołanie metod lub `javap`).

   **Adnotacje generujące metody:**
    - `@Getter` - generuje gettery dla pól (można użyć na polu lub klasie)
    - `@Setter` - generuje settery dla pól (można użyć na polu lub klasie)
    - `@ToString` - generuje metodę `toString()`; parametry: `exclude`, `of`, `callSuper`, `onlyExplicitlyIncluded`
    - `@EqualsAndHashCode` - generuje `equals()` i `hashCode()`; parametry: `exclude`, `of`, `callSuper`
    - `@ToString.Include` / `@ToString.Exclude` - kontrola które pola są w `toString()`
    - `@EqualsAndHashCode.Include` / `@EqualsAndHashCode.Exclude` - kontrola pól w `equals/hashCode`

   **Adnotacje konstruktorów:**
    - `@NoArgsConstructor` - konstruktor bezargumentowy
    - `@AllArgsConstructor` - konstruktor ze wszystkimi polami
    - `@RequiredArgsConstructor` - konstruktor z polami `final` i `@NonNull`

   **Adnotacje agregujące (kombinacje):**
    - `@Data` - `@Getter` + `@Setter` + `@ToString` + `@EqualsAndHashCode` + `@RequiredArgsConstructor`
    - `@Value` - niemutowalna wersja `@Data` (wszystkie pola `final`, klasa `final`)
    - `@Builder` - wzorzec Builder; przetestuj parametry `toBuilder`, `builderMethodName`, `buildMethodName`
    - `@SuperBuilder` - Builder działający z dziedziczeniem
    - `@Singular` - dla kolekcji w Builderze (dodawanie pojedynczych elementów)

   W klasie `Main` utwórz instancje przygotowanych klas i pokaż w konsoli efekty działania każdej z adnotacji (wywołaj getter/setter, `toString()`, builder, logger itd.).
