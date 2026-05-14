# Karta pracy — Pliki w Javie

## Jak korzystać z tych zadań

Każde zadanie zawiera:

* **Cel + teoria w pigułce** — krótkie wyjaśnienie, co i dlaczego.
* **Kod** — gotowy plik Java do skopiowania.
* **Krok po kroku** — co masz zrobić, czego się spodziewać, co zmodyfikować.
* **Pytania kontrolne** — pytania do odpowiedzi w głowie albo w notatniku.

**Przygotowanie projektu:**

1. Utwórz nowy projekt Maven w IntelliJ IDEA (*File → New → Project → Maven*).
2. Skopiuj `pom.xml` podany niżej.
3. Dla każdego zadania utwórz odpowiedni pakiet w `src/main/java/`.
4. Skopiuj kod do pliku w odpowiednim pakiecie.
5. Uruchom klasę (`Shift+F10` w Windows/Linux, `^R` w macOS).

**Wspólny pom.xml:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>pliki-homework</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
</project>
```

**Wspólny katalog na pliki testowe:**

W całej karcie pracy używamy katalogu `dane/` (umieszczonego w głównym katalogu projektu — tam, gdzie jest `pom.xml`). Możesz go utworzyć ręcznie w IntelliJ (*prawy klik na projekcie → New → Directory → `dane`*) lub pozwolić Javie utworzyć go w trakcie zadań.

> **Wskazówka:** Po każdym zadaniu zajrzyj do katalogu `dane/` w eksploratorze plików, żeby zobaczyć **na własne oczy**, co stworzył Twój kod. Programowanie plików robi się dziesięć razy bardziej zrozumiałe, gdy widzisz efekt na dysku.

**Krótka mapa: która klasa do czego?**

| Co chcę zrobić | Klasa / metoda | Od kiedy w Javie |
|---|---|---|
| Sprawdzić, czy plik istnieje (stary sposób) | `java.io.File#exists()` | Java 1.0 |
| Sprawdzić, czy plik istnieje (nowy sposób) | `java.nio.file.Files#exists(Path)` | Java 7 |
| Zapisać krótki tekst do pliku | `Files.writeString(path, text)` | Java 11 |
| Odczytać cały plik jako String | `Files.readString(path)` | Java 11 |
| Odczytać plik linia po linii | `Files.readAllLines(path)` lub `BufferedReader` | Java 7 / 1.1 |
| Czytać duży plik leniwie | `Files.lines(path)` | Java 8 |
| Listować katalog | `Files.list(path)` / `Files.walk(path)` | Java 8 |
| Skopiować plik | `Files.copy(src, dst)` | Java 7 |
| Czytać/pisać bajty (obrazy, PDF) | `InputStream` / `OutputStream` | Java 1.0 |

---

## 1. `java.io.File`: stara, ale ciągle używana klasa

**Cel:** Poznać klasę `File` z Javy 1.0. Sprawdzić, czy plik istnieje, zobaczyć jego atrybuty, utworzyć nowy plik.

**Teoria w pigułce:**
`java.io.File` to **klasa z Javy 1.0** reprezentująca **ścieżkę** do pliku lub katalogu. Sama w sobie nie czyta ani nie pisze zawartości — to tylko „uchwyt" do miejsca w systemie plików. Mimo wieku spotkasz ją w starym kodzie, w wielu bibliotekach (np. JFileChooser, multipart upload w Springu) i czasem nawet w nowych projektach z przyzwyczajenia. Warto ją znać, ale w **nowym kodzie zazwyczaj wybieraj `Path` + `Files`** (zadania 5+).

### Kod

Utwórz plik: `src/main/java/com/example/pliki/zad01/FileBasics.java`

```java
package com.example.pliki.zad01;

import java.io.File;
import java.io.IOException;

public class FileBasics {

    public static void main(String[] args) throws IOException {
        // File NIE tworzy nic na dysku — to tylko obiekt opisujący ścieżkę.
        File plik = new File("dane/witaj.txt");

        System.out.println("Ścieżka:           " + plik.getPath());
        System.out.println("Bezwzględna:       " + plik.getAbsolutePath());
        System.out.println("Nazwa pliku:       " + plik.getName());
        System.out.println("Katalog rodzica:   " + plik.getParent());
        System.out.println("Czy istnieje?      " + plik.exists());

        // Upewnij się, że katalog istnieje (utworzy "dane/", jeśli go nie ma)
        File katalog = new File("dane");
        if (!katalog.exists()) {
            boolean ok = katalog.mkdirs();
            System.out.println("Utworzono katalog dane/: " + ok);
        }

        // Stwórz pusty plik na dysku
        boolean utworzony = plik.createNewFile();
        System.out.println("Utworzono plik:    " + utworzony);

        // Po utworzeniu odpytaj atrybuty raz jeszcze
        System.out.println("Czy istnieje?      " + plik.exists());
        System.out.println("Czy to plik?       " + plik.isFile());
        System.out.println("Czy to katalog?    " + plik.isDirectory());
        System.out.println("Rozmiar (bajty):   " + plik.length());
        System.out.println("Można czytać?      " + plik.canRead());
        System.out.println("Można pisać?       " + plik.canWrite());
    }
}
```

### Krok po kroku

1. Skopiuj kod, uruchom. Pierwszy raz — wszystkie pola powinny pokazać sensowne wartości, `Utworzono plik: true`.
2. Uruchom **drugi raz**. Co się zmieniło? Dlaczego `Utworzono plik: false`?
3. Wejdź w eksplorator plików (Finder / Explorer) i zajrzyj do `dane/`. Plik `witaj.txt` powinien tam być, pusty (0 bajtów).
4. Spróbuj usunąć go z poziomu kodu, dopisując na końcu `main`:
   ```java
   boolean usuniety = plik.delete();
   System.out.println("Usunięto plik: " + usuniety);
   ```
   Uruchom. Teraz program tworzy plik i od razu go kasuje. Sprawdź eksplorator.
5. Zmień ścieżkę z `"dane/witaj.txt"` na `"/etc/witaj.txt"` (na macOS/Linux) lub `"C:/Windows/witaj.txt"` (Windows). Co zwraca `createNewFile()`? Dlaczego?

### Pytania kontrolne

1. Co dokładnie robi `new File("dane/witaj.txt")` — w którym momencie powstaje coś na dysku?
2. Czym różni się `getPath()` od `getAbsolutePath()`?
3. Dlaczego `createNewFile()` zwraca `boolean`, a nie po prostu rzuca wyjątek, gdy plik już istnieje?
4. W którym przypadku użyłbyś dziś `java.io.File`, a w którym `java.nio.file.Path`?

---

## 2. `FileWriter`: zapis tekstu po staremu

**Cel:** Zapisać kilka linii tekstu do pliku, używając klasycznego `FileWriter`. Zobaczyć różnicę między **zapisem** a **dopisaniem** (tryb append).

**Teoria w pigułce:**
`FileWriter` to **strumień znakowy** z Javy 1.1 — pisze znaki (nie bajty) do pliku tekstowego. Najprostszy sposób zapisu „od początku" w starym kodzie. Ma **pułapkę**: jeśli nie wywołasz `close()`, zapis może nigdy nie dotrzeć na dysk (zostaje w buforze systemowym). Dlatego w zadaniu 4 nauczymy się **try-with-resources** — automatycznego zamykania.

### Kod

Utwórz plik: `src/main/java/com/example/pliki/zad02/FileWriterDemo.java`

```java
package com.example.pliki.zad02;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileWriterDemo {

    public static void main(String[] args) throws IOException {
        new File("dane").mkdirs();
        File plik = new File("dane/lista_zakupow.txt");

        // === ZAPIS (nadpisuje całą zawartość) ===
        FileWriter writer = new FileWriter(plik);
        writer.write("1. Chleb\n");
        writer.write("2. Mleko\n");
        writer.write("3. Jajka\n");
        writer.close();   // BARDZO WAŻNE — bez tego nic nie zostanie zapisane!

        System.out.println("Zapisano listę zakupów do " + plik.getAbsolutePath());

        // === DOPISYWANIE (drugi argument true) ===
        FileWriter dopisujacy = new FileWriter(plik, true);
        dopisujacy.write("4. Masło\n");
        dopisujacy.write("5. Ser\n");
        dopisujacy.close();

        System.out.println("Dopisano dwie linie.");
    }
}
```

### Krok po kroku

1. Skopiuj kod, uruchom. Otwórz `dane/lista_zakupow.txt` w IntelliJ albo w notatniku — powinno być 5 pozycji.
2. Uruchom **drugi raz**. Co się dzieje? Zauważ: pierwszy `FileWriter` (bez `true`) **nadpisuje plik od zera**, drugi (z `true`) dopisuje. Po drugim uruchomieniu znowu jest 5 linii.
3. Zakomentuj `writer.close()` (pierwszy). Uruchom. Otwórz plik. Czy widzisz pierwsze 3 linie? Najczęściej **nie** — bufor nie został wypchnięty (tzw. `flush`).
4. Odkomentuj. Spróbuj zamiast `close()` dać `writer.flush()`. Plik się zapisze, ale strumień zostanie otwarty — sprawdź dokumentację, dlaczego to **nie** wystarcza długoterminowo (pozostaje otwarty deskryptor systemowy).
5. Zamiast pojedynczych `write(...)` użyj jednego: `writer.write(String.join("\n", "1. Chleb", "2. Mleko", "3. Jajka") + "\n");`. Działa tak samo?

### Pytania kontrolne

1. Co stanie się z plikiem `lista_zakupow.txt`, jeśli istniał, a Ty stworzysz `new FileWriter(plik)` (bez `true`)?
2. Po co istnieje `flush()` skoro jest `close()`? Czym się różnią?
3. Dlaczego `FileWriter` rzuca `IOException`? Czego ta klasa **nie wie z góry**?
4. Co stanie się, jeśli wywołasz `writer.write(...)` po `writer.close()`?

---

## 3. `BufferedReader` i `BufferedWriter`: szybciej, linia po linii

**Cel:** Czytać plik **linia po linii** za pomocą `BufferedReader`. Pisać szybciej i wygodniej z `BufferedWriter` (`newLine()`).

**Teoria w pigułce:**
Bezpośredni `FileReader` / `FileWriter` chodzi do dysku za każdym znakiem — wolno. **Bufor** to kawałek pamięci RAM, do którego Java kopiuje wiele znaków naraz, a fizyczne odczyty/zapisy robi rzadziej. `BufferedReader` daje też metodę `readLine()` — czyta cały wiersz aż do `\n`. To **klasyczny patent z Javy 1.1**, ciągle używany.

### Kod

Utwórz plik: `src/main/java/com/example/pliki/zad03/BufferedDemo.java`

```java
package com.example.pliki.zad03;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BufferedDemo {

    public static void main(String[] args) throws IOException {
        new File("dane").mkdirs();
        File plik = new File("dane/uczniowie.txt");

        // === ZAPIS przez BufferedWriter ===
        BufferedWriter writer = new BufferedWriter(new FileWriter(plik));
        writer.write("Anna Kowalska,5");
        writer.newLine();                  // dodaje znak końca linii zgodny z systemem
        writer.write("Bartek Nowak,3");
        writer.newLine();
        writer.write("Celina Wiśniewska,4");
        writer.newLine();
        writer.close();

        // === ODCZYT przez BufferedReader linia po linii ===
        BufferedReader reader = new BufferedReader(new FileReader(plik));
        String linia;
        int numer = 1;
        while ((linia = reader.readLine()) != null) {   // null = koniec pliku
            System.out.println(numer + ") " + linia);
            numer++;
        }
        reader.close();
    }
}
```

### Krok po kroku

1. Skopiuj, uruchom. Powinieneś zobaczyć trzy linie wypisane z numerkami.
2. Zwróć uwagę na konstrukcję `(linia = reader.readLine()) != null` — w jednej linii **przypisujemy** wynik `readLine()` do `linia` i **porównujemy** z `null`. To bardzo częsty wzorzec.
3. Otwórz `dane/uczniowie.txt` w IntelliJ. Zobacz, że jest format CSV (przecinek między imieniem a oceną).
4. Zmodyfikuj odczyt: po `readLine()` wykonaj `String[] parts = linia.split(",");` i wypisuj `parts[0]` (imię) oraz `parts[1]` (ocena) osobno.
5. Dopisz do tego samego pliku jeszcze jednego ucznia używając `BufferedWriter` z trybem append: `new BufferedWriter(new FileWriter(plik, true))`.

### Pytania kontrolne

1. Co dokładnie zwraca `readLine()` po przeczytaniu wszystkich linii?
2. Dlaczego `newLine()` jest lepsze od dosłownego `"\n"`?
3. Czemu opakowujemy `FileWriter` w `BufferedWriter` (a nie używamy `FileWriter` bezpośrednio)?
4. Co stanie się z plikiem 1 GB, jeśli zamiast `readLine()` w pętli wczytasz wszystko jednym `read()`?

---

## 4. `try-with-resources`: zamykanie zasobów bez bólu

**Cel:** Pozbyć się ręcznego `close()`. Zobaczyć, jak `try-with-resources` automatycznie zamyka pliki **nawet wtedy, gdy poleci wyjątek**.

**Teoria w pigułce:**
W zadaniach 2–3 łatwo zapomnieć o `close()`. Jeszcze gorzej — jeśli między `new FileWriter(...)` a `close()` poleci wyjątek, `close()` **nigdy się nie wykona** i plik zostanie otwarty (a w niektórych systemach wręcz zablokowany). Java 7 wprowadziła konstrukcję `try (...) { ... }` — wszystko, co zadeklarujesz w nawiasie, **zostanie automatycznie zamknięte** na końcu bloku, bez względu na to, czy poleciał wyjątek, czy nie. To **nowoczesny standard** dla każdej pracy z plikami.

### Kod

Utwórz plik: `src/main/java/com/example/pliki/zad04/TryWithResourcesDemo.java`

```java
package com.example.pliki.zad04;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TryWithResourcesDemo {

    public static void main(String[] args) {
        new File("dane").mkdirs();
        File plik = new File("dane/notatka.txt");

        // === ZAPIS — try-with-resources zamyka writer automatycznie ===
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(plik))) {
            writer.write("Pierwsza linia notatki.");
            writer.newLine();
            writer.write("Druga linia notatki.");
            writer.newLine();
            // Brak writer.close() — Java zrobi to za nas po wyjściu z try.
        } catch (IOException e) {
            System.err.println("Błąd zapisu: " + e.getMessage());
        }

        // === ODCZYT — try-with-resources również ===
        try (BufferedReader reader = new BufferedReader(new FileReader(plik))) {
            String linia;
            while ((linia = reader.readLine()) != null) {
                System.out.println(linia);
            }
        } catch (IOException e) {
            System.err.println("Błąd odczytu: " + e.getMessage());
        }
    }
}
```

### Krok po kroku

1. Skopiuj, uruchom. Powinieneś zobaczyć dwie linie z notatki.
2. Zauważ: nie ma `throws IOException` w sygnaturze `main`. Wszystko obsługuje `catch`.
3. **Eksperyment z wyjątkiem:** w środku bloku zapisu, między `writer.newLine();` a `writer.write("Druga...");`, dopisz `if (true) throw new RuntimeException("nagła awaria");`. Uruchom. Co się stanie? Sprawdź plik — pierwsza linia powinna tam być (writer został domknięty mimo wyjątku).
4. Spróbuj otworzyć **dwa zasoby naraz** — w jednym `try` zadeklaruj reader **i** writer:
   ```java
   try (BufferedReader r = new BufferedReader(new FileReader("dane/notatka.txt"));
        BufferedWriter w = new BufferedWriter(new FileWriter("dane/kopia.txt"))) {
       String linia;
       while ((linia = r.readLine()) != null) {
           w.write(linia);
           w.newLine();
       }
   } catch (IOException e) {
       System.err.println("Błąd kopiowania: " + e.getMessage());
   }
   ```
   Uruchom. Powinieneś dostać `dane/kopia.txt` identyczną z `dane/notatka.txt`.
5. Wytrzyj `dane/notatka.txt` (usuń ręcznie). Uruchom oryginalny program. Co wypisze `catch`?

### Pytania kontrolne

1. Co dokładnie dzieje się z `writer`, gdy program wychodzi z bloku `try (...) { ... }` przez wyjątek?
2. Jaki interfejs musi implementować klasa, żeby działała w `try-with-resources`?
3. Czy w jednym `try (...)` można otworzyć kilka zasobów? Jeśli tak — w jakiej kolejności są zamykane?
4. Po co osobne `catch` na `IOException` skoro `try-with-resources` „samo zamyka"?

---

## 5. `java.nio.file.Path` i `Paths`: nowoczesny opis ścieżki

**Cel:** Poznać `Path` — następcę `File`. Zobaczyć, jak wygodnie składać i analizować ścieżki bez manualnego konkatenowania `"/"`.

**Teoria w pigułce:**
Java 7 dodała pakiet `java.nio.file` (NIO.2 — *New I/O 2*). Klasa `Path` to **nowoczesny zamiennik `File`**: nadal opisuje ścieżkę, ale ma znacznie więcej metod (rozdzielanie segmentów, łączenie, normalizacja, względność). `Path` sam nic nie czyta — robi to klasa `Files` (zadania 6+). Razem to **standard nowego kodu**.

### Kod

Utwórz plik: `src/main/java/com/example/pliki/zad05/PathDemo.java`

```java
package com.example.pliki.zad05;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathDemo {

    public static void main(String[] args) {
        // Dwa równoważne sposoby tworzenia Path
        Path p1 = Path.of("dane", "raporty", "2024", "styczen.csv");
        Path p2 = Paths.get("dane/raporty/2024/styczen.csv");

        System.out.println("p1 = " + p1);
        System.out.println("p2 = " + p2);
        System.out.println("Czy te same? " + p1.equals(p2));

        System.out.println("\n--- analiza ścieżki p1 ---");
        System.out.println("getFileName():   " + p1.getFileName());     // styczen.csv
        System.out.println("getParent():     " + p1.getParent());       // dane/raporty/2024
        System.out.println("getRoot():       " + p1.getRoot());         // null (relative)
        System.out.println("getNameCount():  " + p1.getNameCount());    // 4
        System.out.println("toAbsolutePath():" + p1.toAbsolutePath());  // /Users/.../dane/raporty/2024/styczen.csv

        // Iteracja po segmentach
        System.out.println("\n--- segmenty ---");
        for (int i = 0; i < p1.getNameCount(); i++) {
            System.out.println("  [" + i + "] " + p1.getName(i));
        }

        // Łączenie i normalizacja
        Path baza = Path.of("dane");
        Path dolaczone = baza.resolve("kopia/notatka.txt");
        System.out.println("\nresolve: " + dolaczone);

        Path brzydka = Path.of("dane/raporty/../raporty/./2024/styczen.csv");
        System.out.println("brzydka:    " + brzydka);
        System.out.println("normalize: " + brzydka.normalize());

        // Konwersja do starego File (jeśli musisz użyć starego API)
        java.io.File staryFile = p1.toFile();
        System.out.println("\nJako File: " + staryFile.getAbsolutePath());
    }
}
```

### Krok po kroku

1. Skopiuj, uruchom. Przejrzyj wynik — `p1` i `p2` to dokładnie ten sam `Path`, choć stworzone na dwa sposoby.
2. **Zwróć uwagę na `Path.of(...)`** — przyjmuje wiele argumentów i sam dokleja separator. Bez konkatenowania `"/"` ręcznie!
3. Zmień separatory w `p2` na `\\` (`"dane\\raporty\\2024\\styczen.csv"`). Sprawdź, czy nadal `p1.equals(p2)` na Twoim systemie. (Na Linuxie/macOS — nie. Na Windowsie — tak. Dlatego nie powinieneś na sztywno zapisywać separatora.)
4. Wymyśl ścieżkę `Path scieżka = Path.of("a", "b", "c.txt")`. Wywołaj `subpath(0, 2)` — co zwraca?
5. Stwórz dwa Path-y: `Path baza = Path.of("/Users/anna/projekt")` i `Path plik = Path.of("/Users/anna/projekt/dane/raport.txt")`. Wywołaj `baza.relativize(plik)` — co dostajesz? Po co to się przydaje?

### Pytania kontrolne

1. Co dokładnie robi `Path.of("a", "b")` — i czy wynik różni się od `Path.of("a/b")`?
2. Czym `resolve()` różni się od `Path.of(...)` z dwoma argumentami?
3. Kiedy `getRoot()` zwraca `null`?
4. Po co w ogóle wprowadzono `Path` skoro był już `File`?

---

## 6. `Files`: tworzenie, sprawdzanie, usuwanie

**Cel:** Zastąpić `File#createNewFile()`, `File#mkdirs()` i `File#delete()` przez nowsze, czytelniejsze metody z klasy `Files`.

**Teoria w pigułce:**
`java.nio.file.Files` to **klasa narzędziowa** z samymi metodami statycznymi. Robi wszystko, co umiał `File`, ale czytelniej i z lepszymi wyjątkami (zamiast `boolean false` zwykle dostajesz konkretny `IOException`, np. `NoSuchFileException`, `AccessDeniedException`).

### Kod

Utwórz plik: `src/main/java/com/example/pliki/zad06/FilesBasics.java`

```java
package com.example.pliki.zad06;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilesBasics {

    public static void main(String[] args) throws IOException {
        Path katalog = Path.of("dane/operacje");
        Path plik = katalog.resolve("test.txt");

        // === KATALOGI ===
        if (!Files.exists(katalog)) {
            Files.createDirectories(katalog);   // tworzy też katalogi pośrednie
            System.out.println("Utworzono katalog: " + katalog);
        }

        // === PLIK ===
        if (Files.exists(plik)) {
            Files.delete(plik);
            System.out.println("Usunięto stary plik.");
        }
        Files.createFile(plik);
        System.out.println("Utworzono plik: " + plik);

        // === ATRYBUTY ===
        System.out.println("Czy istnieje?     " + Files.exists(plik));
        System.out.println("Czy plik?         " + Files.isRegularFile(plik));
        System.out.println("Czy katalog?      " + Files.isDirectory(plik));
        System.out.println("Można czytać?     " + Files.isReadable(plik));
        System.out.println("Rozmiar (bajty):  " + Files.size(plik));

        // === USUWANIE BEZPIECZNE ===
        boolean usuniety = Files.deleteIfExists(plik);
        System.out.println("Usunięto plik:    " + usuniety);

        // Próba usunięcia po raz drugi — nie rzuca wyjątku (deleteIfExists)
        boolean drugaProba = Files.deleteIfExists(plik);
        System.out.println("Drugie usunięcie: " + drugaProba);
    }
}
```

### Krok po kroku

1. Skopiuj, uruchom kilka razy z rzędu. Za każdym razem program robi to samo — usuwa stary plik i tworzy świeży.
2. Zamień `Files.deleteIfExists(plik)` na zwykłe `Files.delete(plik)`. Uruchom dwa razy. Drugi raz wybuchnie `NoSuchFileException`. Zauważ różnicę: `deleteIfExists` jest „idempotentne".
3. Spróbuj utworzyć plik w katalogu, który nie istnieje — `Files.createFile(Path.of("dane/inny/nowy/plik.txt"))`. Co się stanie?
4. Stwórz plik o nazwie `dane/operacje/podkatalog`. Spróbuj `Files.createDirectories(...)` na tej samej ścieżce. Co dostaniesz?
5. Zmień ostatnie sprawdzenie na `Files.notExists(plik)` — uważaj, to **nie to samo** co `!Files.exists(plik)`. Sprawdź dokumentację, dlaczego (są ścieżki, dla których oba zwrócą `false`).

### Pytania kontrolne

1. Czym `Files.createFile` różni się od `File#createNewFile`?
2. Dlaczego `Files.createDirectories` (liczba mnoga) jest bezpieczniejsze niż `Files.createDirectory`?
3. Czemu lepiej `Files.deleteIfExists` niż sprawdzenie `Files.exists` + `Files.delete` osobno (z punktu widzenia wielu wątków)?
4. Czym `Files.exists` różni się od `Files.notExists`?

---

## 7. `Files.writeString` i `Files.readString`: jednolinijkowy zapis i odczyt (Java 11)

**Cel:** Zapisać i odczytać krótki tekst do/z pliku **w jednej linii kodu**, używając najnowszej (Java 11) metody.

**Teoria w pigułce:**
Java 11 dodała `Files.writeString(path, text)` i `Files.readString(path)`. To **najprostszy możliwy** sposób na pracę z plikami tekstowymi w nowoczesnej Javie. Pod spodem klasa sama otwiera, zapisuje/czyta, **i zamyka** plik. Idealne do plików konfiguracyjnych, krótkich notatek, JSON-ów, raportów. Dla plików rzędu setek MB lepiej użyć strumieniowego API (zadanie 9).

### Kod

Utwórz plik: `src/main/java/com/example/pliki/zad07/WriteReadStringDemo.java`

```java
package com.example.pliki.zad07;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class WriteReadStringDemo {

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("dane"));
        Path plik = Path.of("dane/wiersz.txt");

        // === ZAPIS — jedna linia kodu ===
        String tresc = """
                Litwo, Ojczyzno moja! ty jesteś jak zdrowie;
                Ile cię trzeba cenić, ten tylko się dowie,
                Kto cię stracił.
                """;

        Files.writeString(plik, tresc);
        System.out.println("Zapisano " + tresc.length() + " znaków.");

        // === ODCZYT — też jedna linia ===
        String wczytane = Files.readString(plik);
        System.out.println("\n--- Wczytane z pliku ---");
        System.out.println(wczytane);

        // === DOPISYWANIE (z opcją APPEND) ===
        Files.writeString(plik,
                "\n— Adam Mickiewicz, Pan Tadeusz\n",
                StandardOpenOption.APPEND);

        System.out.println("Po dopisaniu autora:\n");
        System.out.println(Files.readString(plik));
    }
}
```

### Krok po kroku

1. Skopiuj, uruchom. Wynik — plik z 4 liniami wiersza + linią z autorem.
2. Spróbuj **wczytać plik, który nie istnieje** — zmień ścieżkę na `Path.of("dane/nieistnieje.txt")` w `readString`. Sprawdź, jaki **konkretny** wyjątek wybucha. (Wskazówka: `NoSuchFileException` — podtyp `IOException`.)
3. Zwróć uwagę na **tekst blokowy** `""" ... """` — to składnia z Javy 15+. Świetna do długich tekstów wieloliniowych.
4. Zmień ostatni `writeString` tak, by **nie miał** `StandardOpenOption.APPEND`. Uruchom — co się stanie z wierszem? (Domyślnie `writeString` **nadpisuje** plik!)
5. Zapisz krótki JSON do pliku: `Files.writeString(Path.of("dane/config.json"), "{\"port\": 8080, \"debug\": true}");`. Otwórz, sprawdź. Wystarczy do prostych plików konfiguracyjnych.

### Pytania kontrolne

1. Po co istnieje `Files.writeString`, skoro Java 1.1 miała już `FileWriter`?
2. Co domyślnie robi `Files.writeString` z istniejącym plikiem — nadpisuje czy dopisuje?
3. Jaki konkretny wyjątek dostajesz przy `readString` na nieistniejącym pliku?
4. W jakim **kodowaniu** Java zapisuje tekst, jeśli nie wskażesz inaczej?

---

## 8. `Files.readAllLines` i `Files.write`: cały plik jako lista linii

**Cel:** Wczytać plik tekstowy do `List<String>`, gdzie każda linia to osobny element. Zapisać `List<String>` do pliku, każdy element w osobnej linii.

**Teoria w pigułce:**
`Files.readAllLines(path)` zwraca `List<String>` — wygodne, gdy plik jest mały (do kilku MB) i chcesz mieć szybki dostęp do dowolnej linii. `Files.write(path, listaLinii)` robi rzecz odwrotną. Obie metody **wczytują/zapisują wszystko naraz** — dla bardzo dużych plików wybierz `Files.lines()` (zadanie 9).

### Kod

Utwórz plik: `src/main/java/com/example/pliki/zad08/ReadAllLinesDemo.java`

```java
package com.example.pliki.zad08;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ReadAllLinesDemo {

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("dane"));
        Path plik = Path.of("dane/zadania.txt");

        // === ZAPIS LISTY DO PLIKU ===
        List<String> zadania = List.of(
                "[ ] Kupić chleb",
                "[ ] Wynieść śmieci",
                "[x] Odpisać na maila",
                "[ ] Zadzwonić do mamy",
                "[x] Zapłacić rachunek za prąd"
        );
        Files.write(plik, zadania);
        System.out.println("Zapisano " + zadania.size() + " zadań do " + plik);

        // === ODCZYT WSZYSTKICH LINII ===
        List<String> wczytane = Files.readAllLines(plik);
        System.out.println("\nWczytane zadania:");
        for (int i = 0; i < wczytane.size(); i++) {
            System.out.println((i + 1) + ". " + wczytane.get(i));
        }

        // === FILTROWANIE — tylko nieukończone ===
        System.out.println("\nNieukończone:");
        for (String z : wczytane) {
            if (z.startsWith("[ ]")) {
                System.out.println("  -> " + z);
            }
        }

        // === MODYFIKACJA — dodajemy nowe zadanie i zapisujemy ===
        List<String> noweZadania = new ArrayList<>(wczytane);
        noweZadania.add("[ ] Wyrzucić śmieci od sąsiada");
        Files.write(plik, noweZadania);
        System.out.println("\nZapisano nową wersję, teraz " + noweZadania.size() + " zadań.");
    }
}
```

### Krok po kroku

1. Skopiuj, uruchom. Powinieneś zobaczyć listę zadań, wyfiltrowaną listę nieukończonych i komunikat o zapisie.
2. Otwórz `dane/zadania.txt` w eksploratorze — sprawdź, czy każde zadanie jest w osobnej linii.
3. **Zauważ pułapkę z `List.of(...)`** — ta lista jest niemodyfikowalna. Dlatego do dodania nowego elementu przepisujemy do `new ArrayList<>(wczytane)`. Spróbuj dodać element bezpośrednio do `wczytane.add(...)` — co dostajesz?
4. Policz zadania ukończone (`startsWith("[x]")`) i wypisz na końcu `Ukończono X z Y zadań`.
5. Spróbuj `Files.write(plik, noweZadania, StandardOpenOption.APPEND)` zamiast nadpisywania. Uruchom dwa razy. Co dzieje się z plikiem?

### Pytania kontrolne

1. Czym `Files.readAllLines` różni się od `Files.readString`?
2. Co stanie się, jeśli plik ma 5 GB, a Ty wywołasz `Files.readAllLines`?
3. Dlaczego trzeba przepisać `List.of(...)` do `new ArrayList<>(...)` przed dodaniem elementu?
4. Czy `Files.write(path, list)` dodaje znak końca linii po każdym elemencie listy?

---

## 9. `Files.lines()`: leniwe czytanie dużych plików

**Cel:** Wczytać duży plik tekstowy „leniwie" — linia po linii, bez ładowania całości do pamięci. Użyć Stream API do filtrowania i zliczania.

**Teoria w pigułce:**
`Files.lines(path)` zwraca `Stream<String>` — strumień linii. **Strumień nie czyta z góry — czyta dopiero, gdy go pytasz.** Dzięki temu nawet plik 10 GB można przejrzeć w stałej, małej ilości pamięci. **Wymaga** `try-with-resources` — strumień trzyma otwarty deskryptor pliku.

### Kod

Utwórz plik: `src/main/java/com/example/pliki/zad09/FilesLinesDemo.java`

```java
package com.example.pliki.zad09;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FilesLinesDemo {

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("dane"));
        Path plik = Path.of("dane/log.txt");

        // === Najpierw wygenerujmy spory plik logów ===
        List<String> logi = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            String poziom = (i % 7 == 0) ? "ERROR"
                          : (i % 3 == 0) ? "WARN"
                          : "INFO";
            logi.add("2024-01-01 10:" + String.format("%02d", i % 60)
                     + ":00 " + poziom + " Zdarzenie #" + i);
        }
        Files.write(plik, logi);
        System.out.println("Wygenerowano " + logi.size() + " linii logów.\n");

        // === ZLICZANIE bez wczytywania całego pliku do pamięci ===
        long liczbaErrorow;
        try (Stream<String> linie = Files.lines(plik)) {
            liczbaErrorow = linie.filter(l -> l.contains("ERROR")).count();
        }
        System.out.println("Liczba ERROR-ów: " + liczbaErrorow);

        // === Pobranie pierwszych 5 ostrzeżeń ===
        System.out.println("\nPierwsze 5 ostrzeżeń (WARN):");
        try (Stream<String> linie = Files.lines(plik)) {
            linie.filter(l -> l.contains("WARN"))
                 .limit(5)
                 .forEach(l -> System.out.println("  " + l));
        }

        // === Wypisanie tylko numerów linii zawierających ERROR ===
        System.out.println("\nLinie z ERROR-em (pierwsze 3 numery):");
        int[] numer = {0};   // sprytny trick na licznik w lambdzie
        try (Stream<String> linie = Files.lines(plik)) {
            linie.peek(l -> numer[0]++)
                 .filter(l -> l.contains("ERROR"))
                 .limit(3)
                 .forEach(l -> System.out.println("  linia " + numer[0] + ": " + l));
        }
    }
}
```

### Krok po kroku

1. Skopiuj, uruchom. Powinieneś dostać liczbę ERROR-ów (≈143), 5 pierwszych ostrzeżeń i 3 pierwsze linie z ERROR-em z numerami.
2. **Zauważ trzy osobne `try-with-resources`** — każdy strumień jest **jednorazowy**. Po użyciu trzeba otworzyć nowy.
3. Usuń `try-with-resources` (zostaw samo `Files.lines(plik).filter(...)...`). Skompiluje się, zadziała, ale **plik pozostanie otwarty** — w długo działającym programie skończą Ci się deskryptory systemowe.
4. Wygeneruj plik z 10 milionami linii (zmień `1000` na `10_000_000`). Uruchom. Pamiętaj: leniwe czytanie powinno być szybkie i zużywać mało pamięci. Jeśli zamiast `Files.lines` użyjesz `Files.readAllLines` — Java zacznie się dusić.
5. Policz, **ile w sumie znaków** mają wszystkie linie z poziomem `INFO`:
   ```java
   long znaki;
   try (Stream<String> linie = Files.lines(plik)) {
       znaki = linie.filter(l -> l.contains("INFO")).mapToInt(String::length).sum();
   }
   ```

### Pytania kontrolne

1. Dlaczego `Files.lines(...)` **musi** być w `try-with-resources`, a `Files.readAllLines(...)` nie?
2. Co znaczy „leniwe" czytanie?
3. Czemu strumień można przejść tylko **raz**?
4. Czy `Files.lines(plik).filter(...).count()` wczytuje plik raz, czy dwa razy?

---

## 10. Listowanie zawartości katalogu: `Files.list` i `Files.walk`

**Cel:** Wypisać pliki w katalogu (płasko) oraz przejść całe drzewo katalogów rekurencyjnie. Policzyć ile plików `.txt` jest w `dane/`.

**Teoria w pigułce:**
`Files.list(katalog)` daje `Stream<Path>` z **bezpośrednią zawartością** katalogu (bez wchodzenia głębiej). `Files.walk(katalog)` chodzi **rekurencyjnie** po wszystkich podkatalogach. Oba zwracają `Stream` — więc pamiętaj o `try-with-resources` jak w zadaniu 9.

### Kod

Utwórz plik: `src/main/java/com/example/pliki/zad10/ListWalkDemo.java`

```java
package com.example.pliki.zad10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class ListWalkDemo {

    public static void main(String[] args) throws IOException {
        Path dane = Path.of("dane");
        Files.createDirectories(dane);

        // Wygenerujmy parę plików, żeby było co listować
        Files.writeString(dane.resolve("plik1.txt"), "treść 1");
        Files.writeString(dane.resolve("plik2.txt"), "treść 2");
        Files.writeString(dane.resolve("notatka.md"), "# Notatka");
        Files.createDirectories(dane.resolve("podkatalog"));
        Files.writeString(dane.resolve("podkatalog/glebsze.txt"), "ukryte głębiej");

        // === Files.list — TYLKO bezpośrednia zawartość ===
        System.out.println("=== Files.list (płasko) ===");
        try (Stream<Path> sciezki = Files.list(dane)) {
            sciezki.forEach(p -> System.out.println("  " + p));
        }

        // === Files.walk — REKURENCYJNIE ===
        System.out.println("\n=== Files.walk (rekurencyjnie) ===");
        try (Stream<Path> sciezki = Files.walk(dane)) {
            sciezki.forEach(p -> System.out.println("  " + p));
        }

        // === Filtrowanie: tylko pliki .txt, w całym drzewie ===
        System.out.println("\n=== Tylko pliki .txt (w całym drzewie) ===");
        long ileTxt;
        try (Stream<Path> sciezki = Files.walk(dane)) {
            ileTxt = sciezki
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt"))
                    .peek(p -> System.out.println("  " + p))
                    .count();
        }
        System.out.println("Razem plików .txt: " + ileTxt);

        // === Maksymalna głębokość przy walku ===
        System.out.println("\n=== Files.walk z maxDepth=1 (jak Files.list) ===");
        try (Stream<Path> sciezki = Files.walk(dane, 1)) {
            sciezki.forEach(p -> System.out.println("  " + p));
        }
    }
}
```

### Krok po kroku

1. Skopiuj, uruchom. Najpierw zobaczysz płaską listę `dane/`, potem rekurencyjną (z `glebsze.txt`), potem same `.txt`.
2. **Zauważ:** `Files.walk` **wlicza sam katalog startowy** — pierwsza pozycja to `dane`. Dlatego filtrujemy `Files::isRegularFile`, żeby pominąć katalogi.
3. Posortuj wynik alfabetycznie: dodaj `.sorted()` po `.filter(Files::isRegularFile)`.
4. Wypisz dla każdego pliku jego **rozmiar**: `.peek(p -> System.out.println("  " + p + " (" + Files.size(p) + " B)"))`. Uwaga — `Files.size` rzuca `IOException`, więc lambda się nie skompiluje. Owiń wywołanie w `try/catch` w lambdzie albo wydziel pomocniczą metodę.
5. Spróbuj `Files.walk(Path.of("/"), 2)` (na macOS/Linux). Wypisze całą zawartość systemu plików do 2 poziomów. **Tylko nie rób tego z `forEach(System.out::println)` na produkcji** — drukować można, ale chwilę zajmie.

### Pytania kontrolne

1. Czym `Files.list` różni się od `Files.walk` przy katalogu z 1 plikiem i 0 podkatalogami?
2. Czemu `Files.walk` **wlicza sam katalog startowy** w wynikach?
3. Jaki typ ma element strumienia z `Files.list` / `Files.walk`?
4. Jak ograniczyć głębokość rekurencji w `Files.walk`?

---

## 11. Kopiowanie i przenoszenie: `Files.copy` i `Files.move`

**Cel:** Skopiować plik, przenieść (zmienić nazwę), zastąpić istniejący docelowy plik. Zobaczyć, co się dzieje, gdy cel już istnieje.

**Teoria w pigułce:**
`Files.copy(src, dst)` kopiuje plik. `Files.move(src, dst)` przenosi (zwykle to **rename** w obrębie tego samego dysku — bardzo szybko, bez fizycznego kopiowania bajtów). Domyślnie obie rzucają wyjątek, jeśli `dst` już istnieje. Można im powiedzieć „nadpisz" przez `StandardCopyOption.REPLACE_EXISTING`.

### Kod

Utwórz plik: `src/main/java/com/example/pliki/zad11/CopyMoveDemo.java`

```java
package com.example.pliki.zad11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class CopyMoveDemo {

    public static void main(String[] args) throws IOException {
        Path dane = Path.of("dane");
        Files.createDirectories(dane);

        Path zrodlo = dane.resolve("oryginal.txt");
        Files.writeString(zrodlo, "To jest oryginalna treść.\nLinia druga.");
        System.out.println("Stworzono: " + zrodlo);

        // === KOPIOWANIE ===
        Path kopia = dane.resolve("kopia.txt");
        Files.deleteIfExists(kopia);  // posprzątaj na wszelki wypadek
        Files.copy(zrodlo, kopia);
        System.out.println("Skopiowano do: " + kopia);
        System.out.println("Treść kopii:\n" + Files.readString(kopia));

        // === Próba kopii, gdy cel istnieje — wybuchnie FileAlreadyExistsException ===
        try {
            Files.copy(zrodlo, kopia);   // już istnieje!
        } catch (IOException e) {
            System.out.println("\nWyjątek przy ponownym kopiowaniu: " + e.getClass().getSimpleName());
        }

        // === Kopia z nadpisywaniem ===
        Files.copy(zrodlo, kopia, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Nadpisano kopię.");

        // === PRZENIESIENIE / RENAME ===
        Path nowaNazwa = dane.resolve("kopia_ze_zmienioną_nazwą.txt");
        Files.deleteIfExists(nowaNazwa);
        Files.move(kopia, nowaNazwa);
        System.out.println("\nPrzeniesiono kopia.txt → " + nowaNazwa.getFileName());
        System.out.println("Czy stara kopia istnieje? " + Files.exists(kopia));
        System.out.println("Czy nowa nazwa istnieje? " + Files.exists(nowaNazwa));
    }
}
```

### Krok po kroku

1. Skopiuj, uruchom. Sprawdź `dane/` w eksploratorze: powinieneś mieć `oryginal.txt` i `kopia_ze_zmienioną_nazwą.txt` (`kopia.txt` zostało przemianowane).
2. Uruchom drugi raz. Część `try/catch` ciągle wybucha (kopia istnieje przed `REPLACE_EXISTING`), bo pierwszy `Files.copy` sprawdza istnienie. To **oczekiwane zachowanie**.
3. Spróbuj zrobić **kopię z atrybutami** (czas modyfikacji oryginału): dodaj `StandardCopyOption.COPY_ATTRIBUTES`. Sprawdź różnicę w eksploratorze (data modyfikacji).
4. Spróbuj `Files.move` na pliku, który **nie istnieje** — co dostajesz?
5. Skopiuj cały **katalog**: `Files.copy(Path.of("dane"), Path.of("dane_kopia"))`. Co dostajesz? (Wskazówka: kopia katalogu kopiuje **sam katalog**, a nie jego zawartość. Do rekurencyjnego kopiowania trzeba `Files.walk` + ręcznie kopiować każdy plik — to ćwiczenie zaawansowane.)

### Pytania kontrolne

1. Co domyślnie robi `Files.copy`, jeśli cel istnieje?
2. Czemu `Files.move` w obrębie jednego dysku jest niemal natychmiastowe, a kopiowanie wolne?
3. Czym różni się `StandardCopyOption.REPLACE_EXISTING` od `StandardCopyOption.COPY_ATTRIBUTES`?
4. Czy `Files.copy` na katalogu kopiuje też jego zawartość?

---

## 12. Pliki binarne: `InputStream` i `OutputStream`

**Cel:** Skopiować dowolny plik **bajt po bajcie**, używając strumieni binarnych. Działa tak samo dla obrazów, PDF-ów, plików ZIP — czegokolwiek, co nie jest tekstem.

**Teoria w pigułce:**
Tekstowe API (`Reader`/`Writer`, `readString`, `readAllLines`) **interpretuje bajty jako znaki** w jakimś kodowaniu (UTF-8). Dla **plików binarnych** (obrazy, PDF, ZIP, MP3) trzeba pracować na **surowych bajtach** — `InputStream` (czyta) i `OutputStream` (pisze). Współczesna Java ma też skróty: `Files.readAllBytes(path)` i `Files.write(path, bytes)`.

### Kod

Utwórz plik: `src/main/java/com/example/pliki/zad12/BinaryStreamsDemo.java`

```java
package com.example.pliki.zad12;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class BinaryStreamsDemo {

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("dane"));

        // === 1. Stwórzmy "sztuczny" plik binarny — sekwencję 256 bajtów ===
        Path zrodlo = Path.of("dane/binarny.dat");
        byte[] zawartosc = new byte[256];
        for (int i = 0; i < 256; i++) {
            zawartosc[i] = (byte) i;
        }
        Files.write(zrodlo, zawartosc);
        System.out.println("Stworzono " + zrodlo + " (" + Files.size(zrodlo) + " B)");

        // === 2. Skopiowanie strumieniowo (klasyczny patent) ===
        Path cel = Path.of("dane/binarny_kopia.dat");
        try (InputStream in = new BufferedInputStream(Files.newInputStream(zrodlo));
             OutputStream out = new BufferedOutputStream(Files.newOutputStream(cel))) {

            byte[] bufor = new byte[64];   // czytamy po 64 bajty
            int przeczytanych;
            while ((przeczytanych = in.read(bufor)) != -1) {   // -1 = koniec pliku
                out.write(bufor, 0, przeczytanych);
            }
        }
        System.out.println("Skopiowano strumieniowo do " + cel
                           + " (" + Files.size(cel) + " B)");

        // === 3. Skrót dla małych plików: readAllBytes / write ===
        Path celSkrot = Path.of("dane/binarny_skrot.dat");
        byte[] wszystkie = Files.readAllBytes(zrodlo);
        Files.write(celSkrot, wszystkie);
        System.out.println("Skopiowano przez readAllBytes/write: "
                           + celSkrot + " (" + Files.size(celSkrot) + " B)");

        // === 4. Skrót II: Files.copy(InputStream, Path) ===
        Path celCopy = Path.of("dane/binarny_copy.dat");
        try (InputStream in = Files.newInputStream(zrodlo)) {
            Files.copy(in, celCopy, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        System.out.println("Skopiowano przez Files.copy: "
                           + celCopy + " (" + Files.size(celCopy) + " B)");
    }
}
```

### Krok po kroku

1. Skopiuj, uruchom. Powinny powstać 4 pliki w `dane/`, każdy po 256 bajtów.
2. Skopiuj jakikolwiek **prawdziwy** obraz (np. `~/Desktop/zdjecie.png`) do `dane/foto.png`. Zmień `zrodlo` na ten plik. Uruchom. Otwórz `dane/binarny_kopia.dat` w przeglądarce obrazów (zmień rozszerzenie na `.png`) — powinien wyświetlić się oryginał.
3. **Eksperyment:** spróbuj otworzyć `dane/binarny.dat` przez `Files.readString(...)` (z zadania 7). Co się stanie? (Najczęściej wybucha `MalformedInputException` — bajty nie są poprawnym UTF-8.)
4. **Bufor**: zmień `byte[] bufor = new byte[64]` na `byte[] bufor = new byte[1]`. Działa, ale **wolno** — kopiujesz po jednym bajcie. Zmień na `8192` (typowy rozmiar bufora) — szybciej, gdy plik jest większy.
5. Zamiast pętli `while`, użyj jednolinijkowca: `in.transferTo(out)` (Java 9+). To **standardowy** sposób od Javy 9 — krócej, szybciej.

### Pytania kontrolne

1. Czemu do obrazów / PDF-ów używamy `InputStream`/`OutputStream`, a nie `Reader`/`Writer`?
2. Co oznacza `-1` zwrócone przez `read(bufor)`?
3. Po co opakowywać `Files.newInputStream(...)` w `BufferedInputStream`?
4. Dla bardzo dużego pliku (10 GB) — `Files.readAllBytes` czy strumieniowe kopiowanie? Dlaczego?

---

## 13. Mini-projekt: Konsolowy notatnik

**Cel:** Połączyć poznane narzędzia w jedną aplikację — prosty notatnik dostępny z konsoli. **Add**, **list**, **delete**, **search** — wszystko zapisywane do pliku, czyli **trwałe** między uruchomieniami.

**Teoria w pigułce:**
Tu nie ma nowej teorii — łączysz to, co już znasz: `Scanner` do wczytywania komend, `Files.readAllLines` / `Files.write` do trwałości, `try-with-resources` dla bezpieczeństwa, `Path.of` do ścieżek. To zadanie ma pokazać, że wszystkie te małe klocki **składają się w realną aplikację** w 60 linijkach.

### Kod

Utwórz plik: `src/main/java/com/example/pliki/zad13/Notatnik.java`

```java
package com.example.pliki.zad13;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Notatnik {

    private static final Path PLIK = Path.of("dane/notatki.txt");

    public static void main(String[] args) throws IOException {
        Files.createDirectories(PLIK.getParent());
        if (!Files.exists(PLIK)) {
            Files.createFile(PLIK);
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("=== Notatnik (komendy: add, list, del N, find X, exit) ===");

        while (true) {
            System.out.print("> ");
            String linia = sc.nextLine().trim();
            if (linia.isEmpty()) continue;

            String komenda = linia.split(" ", 2)[0];
            String reszta = linia.length() > komenda.length()
                    ? linia.substring(komenda.length() + 1)
                    : "";

            switch (komenda) {
                case "add"  -> dodaj(reszta);
                case "list" -> wypisz();
                case "del"  -> usun(Integer.parseInt(reszta));
                case "find" -> szukaj(reszta);
                case "exit" -> { System.out.println("Do widzenia!"); return; }
                default     -> System.out.println("Nieznana komenda. Spróbuj: add, list, del N, find X, exit");
            }
        }
    }

    private static void dodaj(String tresc) throws IOException {
        if (tresc.isBlank()) {
            System.out.println("Pusta notatka — pominięto.");
            return;
        }
        List<String> notatki = new ArrayList<>(Files.readAllLines(PLIK));
        notatki.add(tresc);
        Files.write(PLIK, notatki);
        System.out.println("Dodano (#" + notatki.size() + ").");
    }

    private static void wypisz() throws IOException {
        List<String> notatki = Files.readAllLines(PLIK);
        if (notatki.isEmpty()) {
            System.out.println("(brak notatek)");
            return;
        }
        for (int i = 0; i < notatki.size(); i++) {
            System.out.println((i + 1) + ". " + notatki.get(i));
        }
    }

    private static void usun(int numer) throws IOException {
        List<String> notatki = new ArrayList<>(Files.readAllLines(PLIK));
        if (numer < 1 || numer > notatki.size()) {
            System.out.println("Brak notatki #" + numer + ".");
            return;
        }
        String usunieta = notatki.remove(numer - 1);
        Files.write(PLIK, notatki);
        System.out.println("Usunięto: " + usunieta);
    }

    private static void szukaj(String fraza) throws IOException {
        List<String> notatki = Files.readAllLines(PLIK);
        boolean cosZnaleziono = false;
        for (int i = 0; i < notatki.size(); i++) {
            if (notatki.get(i).toLowerCase().contains(fraza.toLowerCase())) {
                System.out.println((i + 1) + ". " + notatki.get(i));
                cosZnaleziono = true;
            }
        }
        if (!cosZnaleziono) {
            System.out.println("Nic nie pasuje do: " + fraza);
        }
    }
}
```

### Krok po kroku

1. Skopiuj, uruchom. Powinieneś zobaczyć prompt `>`. Wpisz kolejno:
   ```
   add Kupić chleb
   add Wynieść śmieci
   add Zapłacić rachunek
   list
   find chleb
   del 2
   list
   exit
   ```
2. **Zamknij program** (`exit`), uruchom **drugi raz**. Wpisz `list`. Notatki **dalej tam są** — bo siedzą w `dane/notatki.txt`. To jest właśnie **trwałość**.
3. Otwórz `dane/notatki.txt` w edytorze. Edytuj ręcznie (dodaj linię). Wróć do programu, wpisz `list`. Twoja ręczna zmiana powinna być widoczna.
4. **Mała wada:** każda komenda `add` / `del` wczytuje cały plik i nadpisuje. Dla 10 notatek — bez znaczenia. Dla 10 milionów — katastrofa. Pomyśl, jak by to zoptymalizować (wskazówka: `StandardOpenOption.APPEND` przy dodawaniu).
5. **Zadanie samodzielne:** dodaj komendę `clear` — usuwa wszystkie notatki (po potwierdzeniu `tak`).
6. **Zadanie samodzielne:** dodaj timestamp do każdej notatki w formacie `[2024-01-01 14:30] treść`. Skorzystaj z `java.time.LocalDateTime.now()`.

### Pytania kontrolne

1. Czemu na początku `main` jest `Files.createDirectories(PLIK.getParent())`?
2. Co się stanie, jeśli plik `dane/notatki.txt` jest otwarty w innym programie (np. w Wordzie) i spróbujesz `Files.write`?
3. Wskaż dokładnie te miejsca w kodzie, gdzie używasz API z **Javy 7** (NIO.2) i gdzie używasz API z **Javy 11** (`writeString`/`readString`). Czy program działa też na Javie 8?
4. Co się stanie, jeśli wpiszesz `del abc` (zamiast liczby)? Jak by to ładnie obsłużyć?
5. Jak zmieniłbyś program, żeby zapisywał notatki w formacie JSON zamiast „linia = jedna notatka"?

---

## Zadania do samodzielnego rozwiązania
---

### Zadanie samodzielne 1: Licznik słów

**Klasa:** `LicznikSlow` w pakiecie `com.example.pliki.sam01`

Program wczytuje plik tekstowy podany jako pierwszy argument `main` (jeśli nie ma argumentu — używa `dane/sam01/tekst.txt`) i wypisuje:

* liczbę linii,
* liczbę słów (oddzielonych spacjami / białymi znakami),
* liczbę znaków (bez znaków końca linii),
* najdłuższe słowo w pliku.

**Wymagania:**

* Najdłuższe słowo: jeśli jest remis, wypisz **pierwsze** napotkane.
* Pusty plik nie powinien wybuchnąć — wypisz wszędzie `0` i `"(brak)"` dla słowa.

**Wskazówki:** `Files.readAllLines`, `String.split("\\s+")`, prosta pętla. Nie używaj Stream API, jeśli jeszcze go nie znasz.

#### Pliki testowe do przygotowania ręcznie

Przed uruchomieniem `LicznikSlow` utwórz w IntelliJ katalog `dane/sam01/` i trzy pliki testowe o podanej zawartości (kliknij prawym na `dane/sam01/` → *New → File*, wpisz nazwę, wklej treść, zapisz).

**Plik 1 — `dane/sam01/tekst.txt`:**

```
Ala ma kota
a kot ma Alę
ten kot bardzo lubi mleko
i nadzwyczajnie smakowite ryby
```

**Plik 2 — `dane/sam01/pusty.txt`:** plik istnieje, ale jest **całkowicie pusty** (0 bajtów). W IntelliJ — utwórz plik i nic nie wpisuj.

**Plik 3 — `dane/sam01/remis.txt`:**

```
abc def ghi
abcdefghij klmnopqrst
x y z
```

> W trzeciej linii pliku 3 są dwa słowa po **10 znaków** (`abcdefghij` i `klmnopqrst`) — to świadomy remis na sprawdzenie reguły „pierwszy wygrywa".

#### Oczekiwane wyjście

**Dla `tekst.txt`:**

```
Liczba linii: 4
Liczba słów: 16
Liczba znaków: 78
Najdłuższe słowo: nadzwyczajnie
```

> **Uwaga do liczenia znaków:** zliczamy wszystkie znaki w liniach (włącznie ze spacjami), **bez** znaków końca linii. Suma: `11 + 12 + 25 + 30 = 78`. Jeśli wychodzi Ci dużo więcej, prawdopodobnie liczysz bajty (`getBytes().length`) i polskie litery liczą się podwójnie. Użyj `String#length()` — w Javie to liczba znaków UTF-16, dla zwykłego polskiego tekstu po prostu „liczba liter ze spacjami".

**Dla `pusty.txt`:**

```
Liczba linii: 0
Liczba słów: 0
Liczba znaków: 0
Najdłuższe słowo: (brak)
```

**Dla `remis.txt`:**

```
Liczba linii: 3
Liczba słów: 8
Liczba znaków: 37
Najdłuższe słowo: abcdefghij
```

> Suma znaków: `11 + 21 + 5 = 37`. Najdłuższe — `abcdefghij`, bo pojawia się jako **pierwsze** z dwóch 10-znakowych słów.

#### Jak sprawdzić swoje rozwiązanie

Uruchom `LicznikSlow` z argumentem `dane/sam01/tekst.txt`, potem `dane/sam01/pusty.txt`, potem `dane/sam01/remis.txt`. Porównaj swoje wyjście z tabelkami powyżej **dokładnie linia po linii**.

---

### Zadanie samodzielne 2: Wyszukiwarka frazy w drzewie katalogów

**Klasa:** `Grep` w pakiecie `com.example.pliki.sam02`

Program przyjmuje dwa argumenty: katalog startowy i frazę. Przechodzi rekurencyjnie po katalogu i wypisuje **każdą linię każdego pliku `.txt`**, w której znajdzie frazę, w formacie:

```
ścieżka/do/pliku.txt:42: tu jest linia z frazą
```

(czyli: ścieżka, dwukropek, numer linii (od 1), dwukropek, treść).

**Wymagania:**

* Tylko pliki `.txt` (pomijaj inne rozszerzenia i katalogi).
* Wyszukiwanie **bez** rozróżniania wielkości liter.
* Na końcu wypisz: `Znaleziono X dopasowań w Y plikach`.

**Wskazówki:** `Files.walk` + `try-with-resources` + filtr po rozszerzeniu + dla każdego pliku `Files.readAllLines` i pętla z numerowaniem.

#### Pliki testowe do przygotowania ręcznie

Utwórz w IntelliJ następujące drzewo katalogów wewnątrz `dane/sam02/`:

```
dane/sam02/projekt/
├── README.txt
├── src/
│   ├── Main.txt
│   └── Utils.txt
└── docs/
    ├── intro.txt
    ├── notatka.md        ← ma być POMINIĘTY (nie .txt)
    └── archiwum/
        └── stare.txt
```

Każdy z plików wypełnij dokładnie taką zawartością:

**`dane/sam02/projekt/README.txt`:**

```
Projekt o kotach
Autor: Anna
Wersja: 1.0
```

**`dane/sam02/projekt/src/Main.txt`:**

```
public class Main {
    // KOT to nasz bohater
    String kot = "Mruczek";
}
```

**`dane/sam02/projekt/src/Utils.txt`:**

```
Plik z metodami pomocniczymi.
Nie ma tu nic o naszym ulubieńcu.
```

**`dane/sam02/projekt/docs/intro.txt`:**

```
Wprowadzenie
============
Kot domowy (Felis catus) to ssak.
kot bywa kapryśny.
```

**`dane/sam02/projekt/docs/archiwum/stare.txt`:**

```
Stare notatki o psach.
Brak wzmianki o kotach tutaj.
Pies to przyjaciel człowieka.
```

**`dane/sam02/projekt/docs/notatka.md`** (uwaga, rozszerzenie `.md` — `Grep` ma to ignorować):

```
Tu jest dużo o kotach, ale to plik .md — pomiń mnie.
```

#### Oczekiwane wyjście

**Test 1:** uruchom `Grep` z argumentami: `dane/sam02/projekt` i `kot`.

Oczekiwane wyjście (kolejność plików może się różnić w zależności od systemu plików, ale **liczby na końcu muszą się zgadzać**):

```
dane/sam02/projekt/README.txt:1: Projekt o kotach
dane/sam02/projekt/src/Main.txt:2:     // KOT to nasz bohater
dane/sam02/projekt/src/Main.txt:3:     String kot = "Mruczek";
dane/sam02/projekt/docs/intro.txt:3: Kot domowy (Felis catus) to ssak.
dane/sam02/projekt/docs/intro.txt:4: kot bywa kapryśny.
dane/sam02/projekt/docs/archiwum/stare.txt:2: Brak wzmianki o kotach tutaj.
Znaleziono 6 dopasowań w 4 plikach
```

**Test 2:** uruchom `Grep` z argumentami: `dane/sam02/projekt` i `pies`.

Oczekiwane wyjście:

```
dane/sam02/projekt/docs/archiwum/stare.txt:3: Pies to przyjaciel człowieka.
Znaleziono 1 dopasowań w 1 plikach
```

> `psach` **nie** zawiera podciągu `pies`, więc nie pasuje. Pasuje tylko `Pies` (z dużej, ale wyszukiwanie ignoruje wielkość liter).

**Test 3:** fraza, której nie ma — `Grep dane/sam02/projekt xylofon`.

Oczekiwane wyjście:

```
Znaleziono 0 dopasowań w 0 plikach
```

**Test 4 — kontrola pomijania innych rozszerzeń:** uruchom z frazą `.md`. Plik `notatka.md` zawiera tę frazę, ale **nie ma być przeszukiwany**, więc liczba dopasowań może być niezerowa tylko jeśli inne `.txt` zawierają napis `.md` (w tym przypadku — nie zawierają).

```
Znaleziono 0 dopasowań w 0 plikach
```

#### Jak sprawdzić swoje rozwiązanie

1. Upewnij się, że masz utworzone drzewo plików testowych z sekcji powyżej.
2. Uruchom `Grep` z każdą z 4 par argumentów powyżej.
3. **Krytyczne sprawdzenia:**
   * liczba dopasowań i plików na końcu zgadza się z oczekiwaną,
   * plik `notatka.md` **nigdy** nie pojawia się w wyjściu,
   * w teście 1 dostajesz **3 osobne pliki** z dopasowaniami (README, Main, intro) + 1 (stare.txt) = 4, oraz **6 osobnych linii**.

---

### Zadanie samodzielne 3: Backup z timestampem

**Klasa:** `Backup` w pakiecie `com.example.pliki.sam03`

Program kopiuje plik `dane/sam03/wazne.txt` do podkatalogu `dane/sam03/backupy/` z nazwą zawierającą datę i godzinę, np. `wazne_2024-01-15_14-30-22.txt`.

**Wymagania:**

* Format daty: `yyyy-MM-dd_HH-mm-ss` (myślniki zamiast dwukropków — dwukropki nie działają w nazwach plików na Windowsie).
* Jeśli `dane/sam03/wazne.txt` nie istnieje — wypisz czytelny komunikat i zakończ (nie wywal stack trace).
* Jeśli `dane/sam03/backupy/` nie istnieje — utwórz go.
* Po skopiowaniu wypisz pełną ścieżkę bezwzględną pliku backupu.

**Wskazówki:** `java.time.LocalDateTime.now()` + `DateTimeFormatter.ofPattern(...)` + `Files.copy` + `Files.createDirectories`.

#### Plik testowy do przygotowania ręcznie

Utwórz katalog `dane/sam03/` i plik `dane/sam03/wazne.txt` o takiej zawartości:

```
Bardzo ważny dokument.
Numer faktury: FV/2024/001
Kwota: 12 345,67 zł
Data wystawienia: 2024-01-15
```

Katalogu `dane/sam03/backupy/` **nie twórz** — to zadanie Twojego programu.

#### Scenariusze testowe i oczekiwane wyjście

**Test 1 — szczęśliwa ścieżka:** uruchom `Backup`.

Oczekiwane wyjście (data oczywiście inna — bieżąca):

```
Utworzono backup: /Users/anna/projekt/dane/sam03/backupy/wazne_2026-05-14_10-23-45.txt
```

**Sprawdzenie ręczne (otwórz w eksploratorze):**

* w `dane/sam03/backupy/` istnieje **dokładnie jeden** plik o nazwie pasującej do wzorca `wazne_RRRR-MM-DD_HH-MM-SS.txt`,
* rozmiar pliku w `backupy/` jest **identyczny** z rozmiarem `wazne.txt` (kliknij prawym → *Properties* / *Get Info*),
* otwórz oba pliki w edytorze — treść musi być identyczna.

**Test 2 — dwa backupy z rzędu:** uruchom `Backup`, odczekaj 2–3 sekundy, uruchom `Backup` ponownie.

W `backupy/` masz **dwa różne pliki**, oba o poprawnej zawartości — różnią się tylko sekundami w nazwie. To dowodzi, że timestamp działa.

**Test 3 — brak pliku źródłowego:** ręcznie usuń `dane/sam03/wazne.txt` z eksploratora, potem uruchom `Backup`.

Oczekiwane wyjście (przykładowo):

```
Plik źródłowy nie istnieje: dane/sam03/wazne.txt
```

> **Krytyczne:** żaden stack trace, żaden `NullPointerException`, żaden `NoSuchFileException` na ekranie. Czysty, czytelny komunikat.

**Test 4 — pierwszy backup od zera:** odtwórz `wazne.txt` (jak w sekcji powyżej), ręcznie usuń **cały** katalog `dane/sam03/backupy/`, uruchom `Backup`.

Oczekiwane zachowanie: `Backup` sam utworzy katalog `backupy/` i wstawi do niego plik. Nie ma żadnego wyjątku.

#### Jak sprawdzić swoje rozwiązanie

1. Po Teście 1 — sprawdź ręcznie 3 niezmienniki wymienione wyżej (istnieje, rozmiar, treść).
2. Po Teście 2 — w `backupy/` ma być **2** pliki, oba poprawne.
3. Test 3 musi dać czytelny komunikat **bez stack trace**.
4. Test 4 — sprawdź, że nawet bez katalogu `backupy/` program nie wybucha.

---