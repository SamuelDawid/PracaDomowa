# Przed rozpoczęciem

Zanim zaczniesz, przeczytaj arykuly!!!!!!

https://www.geeksforgeeks.org/java/java-multithreading-tutorial/
https://www.digitalocean.com/community/tutorials/multithreading-in-java
https://medium.com/@rohitpatil3898/concurrent-collection-in-java-1c97ad28fed2
https://www.baeldung.com/java-synchronized-collections
https://www.baeldung.com/java-executor-service-tutorial
https://www.baeldung.com/java-completablefuture
---

# Zadanie 1 – Prosty licznik wątków (podstawy `Thread` / `Runnable`)

## Opis

Napisz program, który:

* Tworzy **N** wątków (np. **5**).
* Każdy wątek:

    * wypisuje swój numer (np. „Wątek 1”, „Wątek 2”, …),
    * wykonuje pętlę od **1** do **10** i wypisuje kolejne liczby z krótkim opóźnieniem (np. `Thread.sleep(100)`).
* Po uruchomieniu wszystkich wątków, **główny wątek** czeka, aż wszystkie się zakończą, a następnie wypisuje: **„Wszystkie wątki zakończyły działanie.”**

## Wymagania techniczne

* Utwórz własną klasę implementującą `Runnable` lub dziedziczącą po `Thread`.
* Użyj `join()`, żeby poczekać na zakończenie wątków.
* Zadbaj, aby numery wątków się nie myliły (np. przekaż numer w konstruktorze).

### Dodatkowe wyzwanie (opcjonalnie)

* Dodaj wspólną zmienną statyczną (np. licznik uruchomionych wątków) i bez synchronizacji zobacz, jak potrafi się „rozjechać”.
* Potem popraw to przy użyciu `synchronized` lub `AtomicInteger`.

---

# Zadanie 2 – Liczenie sumy tablicy w wielu wątkach (synchronizacja / współdzielone dane)

## Opis

Masz dużą tablicę `int[]` o długości np. **1_000_000**, wypełnioną losowymi wartościami. Napisz program, który:

* Dzieli tablicę na **K** fragmentów (np. **4** lub **8**).
* Każdy fragment jest przetwarzany przez osobny wątek, który:

    * oblicza częściową sumę swojego fragmentu,
    * zapisuje wynik w bezpieczny sposób (np. do wspólnej zmiennej lub osobnej struktury).
* Po zakończeniu wszystkich wątków program:

    * oblicza **całkowitą sumę** na podstawie wyników częściowych,
    * wypisuje sumę i **porównuje** ją z sumą policzoną **jednowątkowo** (w zwykłej pętli) – żeby sprawdzić poprawność.

## Wymagania techniczne

* Wątki mogą zapisywać:

    * do tablicy wyników `long[] partialSums`, gdzie każdy wątek ma swój indeks (wtedy **nie musisz** synchronizować zapisu), **lub**
    * do jednego współdzielonego licznika z użyciem `synchronized` lub `AtomicLong`.
* Główny wątek czeka na wszystkie wątki (`join()`).
* Na końcu porównaj wynik równoległy z wynikiem sekwencyjnym.

### Dodatkowe wyzwania (opcjonalnie)

* Zmierz czas liczenia w wersji: **jednowątkowej**, **wielowątkowej**, i porównaj (dla małych tablic wielowątkowo może być wolniej – ciekawa obserwacja).

---

# Zadanie 3 – `ExecutorService` + silnia albo `2^n` z użyciem `long` / `BigInteger`

## Cel

Poznanie `ExecutorService`, puli wątków, `Callable`, `Future` + pracy na dużych liczbach (`long` / `BigInteger`).

## Opis ogólny

Symulujemy system, który przetwarza zestaw zadań obliczeniowych w tle z użyciem puli wątków.

## Szczegółowy opis

Masz listę liczb całkowitych, np. `List<Integer>` z wartościami od **1** do **20** (lub więcej). Dla każdej liczby **n** tworzysz zadanie (`Callable`), które:

* albo oblicza **silnię** `n!`,
* albo oblicza wartość **`2^n`**,
* śpi losową ilość czasu (np. **100–500 ms**), aby zasymulować różny czas przetwarzania,
* zwraca wynik jako: `long`, jeśli zakres jest mały (np. **max n = 20** dla silni), lub jako `BigInteger`, jeśli chcesz obsłużyć większe **n** bez przepełnienia.

Użyj `ExecutorService` z `Executors.newFixedThreadPool(K)` (np. **4 wątki**), aby:

* dla każdej liczby utworzyć zadanie `Callable<BigInteger>` (albo `Callable<Long>`),
* przekazać je do executora (np. `submit()` lub `invokeAll()`),
* otrzymać listę `Future<BigInteger>` / `Future<Long>`,
* odczytać wyniki metodą `get()`.

## Po zebraniu wszystkich wyników

* wypisz w konsoli pary w stylu: `n -> wynik` (np. `5 -> 120` albo `10 -> 1024`),
* zamknij executor (`shutdown()`).

## Sugestia co do typu

* Jeśli chcesz mieć prościej, ale z ograniczeniami zakresu → użyj `long`.
* Jeśli chcesz mieć poprawnie dla dużych **n** → użyj `BigInteger` (polecane przy silni).
