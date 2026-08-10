\# PD 01 — Fundamenty 1: Dziennik elektroniczny



> Duże zadanie syntetyczne do Lekcji 01. Integruje: tablice 1D/2D, metody (przeciążanie, varargs), pętle, `if`/`switch expression`, rekurencję i ręczne parsowanie stringów. \*\*Świadomie nie używamy\*\* `Stream`, `List`, `Map`, `Arrays.sort` ani `Collections.\*` — to zadanie ma wtłoczyć do palców fundamenty, zanim w Lekcji 05 i 07 otrzymasz narzędzia wyższego poziomu.



\---



\## 🎯 Cel



Po wykonaniu tego zadania potrafisz:



\- swobodnie operować na tablicach 1D i 2D (w tym jagged arrays),

\- \*\*ręcznie implementować algorytmy\*\*, które w produkcji dawno byś wyrzucił na rzecz gotowców (parsowanie, sortowanie, wyrównywanie kolumn),

\- pisać metody z jasnym kontraktem i przeciążeniami,

\- stosować rekurencję i świadomie ocenić, kiedy się opłaca,

\- używać `switch expression` z JDK 14+ do klasyfikacji bez `break`,

\- parsować tekst znak po znaku i rozumieć, co dzieje się "pod spodem" `String.split`.



\---



\## 📦 Kontekst domenowy



Piszesz rdzeń \*\*dziennika elektronicznego\*\* dla szkoły średniej. System trzyma oceny uczniów z wielu przedmiotów i na żądanie wychowawcy generuje raport roczny: statystyki per uczeń i per przedmiot, histogram ocen, ranking, decyzje o promocji.



Na tym etapie projektu nie masz jeszcze bazy danych, kolekcji ani streamów — wszystko musi działać na tablicach. Traktuj to jak pisanie biblioteki niskiego poziomu: oszczędnej, bez zależności, testowalnej.



\*\*Zasady gry:\*\*



\- Dane wejściowe dostajesz jako \*\*text block\*\* CSV (stała w kodzie). Nie czytasz plików — to temat Lekcji 09.

\- Wszystkie metody \*\*statyczne\*\* w klasie `GradeBook`.

\- `main` jest cienki — tylko woła metody i drukuje wyniki.

\- Każda metoda ma \*\*jedną odpowiedzialność\*\* i nazwę mówiącą, co robi.

\- Wynik każdej metody jest deterministyczny (dla tych samych wejść — ten sam wynik).



\---



\## 🧩 Część A — MVP



\### Dane



Hardcoded w kodzie, jako pola statyczne klasy:



```java

static final String\[] STUDENTS = {

&#x20;   "Ala Kowalska", "Bartek Nowak", "Celina Zielińska", "Damian Wójcik"

};

static final String\[] SUBJECTS = {

&#x20;   "Matematyka", "Polski", "Angielski", "Historia", "Fizyka"

};

// wiersz = uczeń, kolumna = przedmiot; 0 = brak oceny, 1-6 skala

static final int\[]\[] GRADES = {

&#x20;   { 5, 4, 6, 3, 5 },

&#x20;   { 3, 3, 4, 2, 0 },

&#x20;   { 6, 5, 5, 6, 6 },

&#x20;   { 2, 1, 3, 2, 3 }

};

```



\### Wymagane metody



1\. `static double average(int\[] grades)` — średnia z tablicy, \*\*pomija `0`\*\* (brak oceny). Dla pustej / samych zer — zwraca `-1.0`.

2\. `static double averageForStudent(int\[]\[] grades, int studentIndex)` — przeciążenie, używa `average(int\[])`.

3\. `static double averageForSubject(int\[]\[] grades, int subjectIndex)` — średnia z kolumny.

4\. `static int maxRecursive(int\[]\[] grades)` — znalezienie maksymalnej oceny w całej macierzy \*\*rekurencyjnie\*\* (zakaz pętli w tej metodzie; pomocnicza metoda prywatna dozwolona).

5\. `static String classify(double average)` — używa `switch expression`:

&#x20;  - `< 0` → "brak ocen",

&#x20;  - `< 2.0` → "zagrożony",

&#x20;  - `< 3.5` → "przeciętny",

&#x20;  - `< 4.5` → "dobry",

&#x20;  - `< 5.5` → "bardzo dobry",

&#x20;  - wpp. → "celujący".

6\. `static void printBasicReport()` — drukuje dla każdego ucznia linię: `imię | średnia | klasyfikacja` oraz na końcu globalną maksymalną ocenę znalezioną przez `maxRecursive`.



\---



\## 🧩 Część B — Rozszerzenie



\### B1. Parsowanie text blocka (bez `String.split`)



Dodaj nowe źródło danych jako \*\*text block\*\*:



```java

static final String CSV\_DATA = """

&#x20;       name,Matematyka,Polski,Angielski,Historia,Fizyka

&#x20;       Ala Kowalska,5,4,6,3,5

&#x20;       Bartek Nowak,3,3,4,2,0

&#x20;       Celina Zielińska,6,5,5,6,6

&#x20;       Damian Wójcik,2,1,3,2,3

&#x20;       Ewelina Lis,4,4,5,4,5

&#x20;       """;

```



Zaimplementuj:



\- `static String\[]\[] parseCsv(String csv)` — parsuje text block do \*\*jagged array\*\* stringów (pierwszy wiersz = nagłówek, reszta = wiersze danych).

\- `static int\[]\[] extractGrades(String\[]\[] parsed)` — z surowej macierzy stringów wyciąga tylko kolumny ocen (pomija nagłówek i kolumnę z nazwiskiem), konwertuje na `int`.

\- `static String\[] extractNames(String\[]\[] parsed)` — wyciąga imiona.



\*\*Ograniczenie\*\*: w funkcji `parseCsv` \*\*nie używasz\*\* `String.split`. Implementujesz parser znak-po-znaku używając `charAt`, `length`, `substring` i ręcznego trackowania granic pól. Cel: zrozumieć, że `split` to skrót mentalny, pod spodem jest pętla.



\### B2. Rekurencyjny quicksort indeksów



Napisz `static int\[] rankStudentsByAverage(String\[] names, int\[]\[] grades)` — zwraca \*\*tablicę indeksów\*\* posortowaną malejąco wg średniej ucznia. Oryginalna macierz i tablica nazwisk \*\*nie są modyfikowane\*\*.



\*\*Wymagania:\*\*



\- Algorytm: \*\*rekurencyjny quicksort\*\* (lub merge sort) na tablicy indeksów z comparatorem "average desc".

\- Zakaz `Arrays.sort` / `Collections.sort`.

\- Osobna metoda prywatna `quicksortIndices(int\[] idx, double\[] keys, int lo, int hi)` i jej pomocnicze.

\- Dla równych średnich — kolejność oryginalna (stable nie jest wymagana, ale nie gwiazdkuj losowo).



\### B3. Histogram ASCII rozkładu ocen



Metoda `static void printGradeHistogram(int\[]\[] grades)` drukuje histogram ocen 1–6 w formacie:



```

Histogram ocen (łącznie 23):

&#x20; 1: \*\* (2)

&#x20; 2: \*\*\*\* (4)

&#x20; 3: \*\*\*\*\* (5)

&#x20; 4: \*\*\*\*\*\* (6)

&#x20; 5: \*\*\* (3)

&#x20; 6: \*\*\* (3)

```



\- `0` nie liczymy.

\- Skala słupków proporcjonalna do największego; jeśli największa ilość > 40, skaluj: `slupkow = count \* 40 / max`.



\### B4. Mediana i odchylenie standardowe



\- `static double median(int\[] grades)` — mediana, pomija `0`, dla pustej zwraca `-1.0`. Wymaga posortowania (użyj quicksortu z B2 w wersji dla `int\[]` — rekurencyjnie, bez `Arrays.sort`).

\- `static double stddev(int\[] grades)` — odchylenie standardowe (próbkowe, dzielone przez `n-1`), pomija `0`. `Math.sqrt` dozwolone.



\---



\## 🧩 Część C — Rozszerzenie zaawansowane



\### C1. Średnia krocząca (moving average)



`static double\[] movingAverage(int\[] grades, int window)` — średnia krocząca z okna `window` ostatnich niezerowych ocen. Dla pierwszych `window-1` pozycji zwracaj `-1.0`. Przydatne, gdy chcemy pokazać uczniowi, że jego forma ostatnio rośnie / spada.



\### C2. Decyzja o promocji



`static boolean canBePromoted(int\[] grades)` — uczeń może być promowany gdy:



\- nie ma ani jednej jedynki,

\- liczba ocen `< 3` jest ≤ 2,

\- średnia (pomijając 0) jest ≥ 2.5.



Implementuj \*\*jedną pętlą\*\* po ocenach (nie trzema przebiegami).



\### C3. Pretty-print tabeli ranking



Dodaj `static void printRankingTable(String\[] names, int\[]\[] grades)`:



```

╔════╦═══════════════════╦═════════╦══════════════╗

║ Lp ║ Imię              ║ Średnia ║ Klasyfikacja ║

╠════╬═══════════════════╬═════════╬══════════════╣

║  1 ║ Celina Zielińska  ║    5.60 ║ celujący     ║

║  2 ║ Ala Kowalska      ║    4.60 ║ bardzo dobry ║

║  3 ║ Bartek Nowak      ║    3.00 ║ przeciętny   ║

║  4 ║ Damian Wójcik     ║    2.20 ║ zagrożony    ║

╚════╩═══════════════════╩═════════╩══════════════╝

```



\- Szerokość kolumny "Imię" wyliczana dynamicznie z najdłuższego nazwiska.

\- Szerokości kolumn liczbowych stałe, ale \*\*wyrównane do prawej\*\*.

\- Nie używasz `String.format("%-20s")` dla dynamicznych szerokości — policz ręcznie dopełnienie spacjami. (Użycie `String.format` z wyliczoną szerokością jako parametrem `"%-" + width + "s"` jest OK jako kompromis).



\---



\## ⚙️ Wymagania techniczne



\- \*\*Java 17+\*\*.

\- Klasa `GradeBook` z `public static void main(String\[] args)`.

\- \*\*Musi wystąpić\*\*: `switch expression`, rekurencja, przeciążanie metod, text block, tablice 2D jagged.

\- \*\*Zakazy\*\*: `Stream`, `Collectors`, `Arrays.stream`, `Arrays.sort`, `Collections.\*`, `List`, `Map`, `Set`.

\- \*\*Dozwolone\*\*: `Arrays.copyOf`, `Arrays.fill`, `Arrays.toString`, `Arrays.deepToString`, `Math.\*`, `String.format`, `Integer.parseInt`.



