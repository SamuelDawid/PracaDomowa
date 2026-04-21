# Zadania domowe -- Debugowanie w IntelliJ IDEA

---

## Jak korzystać z tych zadań

Każde zadanie zawiera **gotowy kod Java** do skopiowania oraz **szczegółowy scenariusz debugowania** do wykonania krok po kroku w IntelliJ IDEA.

**Przygotowanie projektu:**

1. Utwórz nowy projekt Maven w IntelliJ IDEA (File → New → Project → Maven)
2. Skopiuj `pom.xml` podany niżej
3. Dla każdego zadania utwórz odpowiedni pakiet w `src/main/java/`
4. Skopiuj kod Java do pliku w odpowiednim pakiecie
5. Uruchom klasę w trybie **Debug** (`Shift+F9`)

**Wspólny pom.xml:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>debugger-homework</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
</project>
```

> Debugger jest wbudowany w IntelliJ IDEA -- nie potrzebujesz żadnych dodatkowych zależności!

**Kluczowe skróty klawiszowe (ściąga):**

| Akcja | Skrót |
|-------|-------|
| Uruchom Debug | `Shift+F9` |
| Ustaw/usuń breakpoint | `Ctrl+F8` |
| Dialog breakpointów | `Ctrl+Shift+F8` |
| Step Over | `F8` |
| Step Into | `F7` |
| Step Out | `Shift+F8` |
| Run to Cursor | `Alt+F9` |
| Resume (kontynuuj) | `F9` |
| Evaluate Expression | `Alt+F8` |

---

## Zadanie 1: Pierwsze kroki z debuggerem -- breakpointy i krokowanie

**Ćwiczone narzędzia:** ustawianie breakpointów, Step Over (`F8`), Step Into (`F7`), Step Out (`Shift+F8`), Run to Cursor (`Alt+F9`), Resume (`F9`), panel Variables.

**Opis:** Program oblicza wskaźnik BMI i przypisuje kategorię wagową. Dla osoby o wadze 70 kg i wzroście 1.75 m oczekujemy BMI ≈ 22.86 (norma), ale program zwraca błędną wartość. Znajdź buga za pomocą debuggera.

### Kod

Utwórz plik: `src/main/java/com/example/debug/zad1/BmiCalculator.java`

```java
package com.example.debug.zad1;

public class BmiCalculator {

    public double calculateBmi(double weight, double height) {
        // BUG: brakuje potęgowania wzrostu!
        double bmi = weight / height;
        return Math.round(bmi * 100.0) / 100.0;
    }

    public String getCategory(double bmi) {
        if (bmi < 18.5) {
            return "Niedowaga";
        } else if (bmi < 25.0) {
            return "Norma";
        } else if (bmi < 30.0) {
            return "Nadwaga";
        } else {
            return "Otyłość";
        }
    }

    public String diagnose(String name, double weight, double height) {
        double bmi = calculateBmi(weight, height);
        String category = getCategory(bmi);
        return name + ": waga=" + weight + " kg, wzrost=" + height
            + " m, BMI=" + bmi + " → " + category;
    }

    public static void main(String[] args) {
        BmiCalculator calc = new BmiCalculator();

        // Osoba 1: oczekiwane BMI = 70 / (1.75 * 1.75) = 22.86 → Norma
        System.out.println(calc.diagnose("Anna", 70.0, 1.75));

        // Osoba 2: oczekiwane BMI = 90 / (1.80 * 1.80) = 27.78 → Nadwaga
        System.out.println(calc.diagnose("Bartek", 90.0, 1.80));

        // Osoba 3: oczekiwane BMI = 55 / (1.70 * 1.70) = 19.03 → Norma
        System.out.println(calc.diagnose("Celina", 55.0, 1.70));
    }
}
```

### Scenariusz debugowania krok po kroku

1. **Ustaw breakpoint** na linii `double bmi = weight / height;` w metodzie `calculateBmi()` -- kliknij na szary margines obok numeru tej linii (pojawi się czerwona kropka)
2. **Uruchom Debug** (`Shift+F9`)
3. Program zatrzymuje się na breakpoincie. W panelu **Variables** (prawy dolny róg) sprawdź:
    - Jaka jest wartość `weight`? (powinna być `70.0`)
    - Jaka jest wartość `height`? (powinna być `1.75`)
4. Naciśnij **F8** (Step Over) -- wykonaj linię z obliczeniem BMI
5. Sprawdź w Variables wartość `bmi`. Czy to jest poprawne BMI? (Poprawne BMI dla 70 kg i 1.75 m to 22.86)
6. Naciśnij **F8** jeszcze raz -- `return` zwraca zaokrąglony wynik. Sprawdź co zwraca.
7. Naciśnij **F9** (Resume) -- program przechodzi do następnego wywołania `calculateBmi` (dla Bartka)
8. Tym razem użyj **F7** (Step Into) na linii `String category = getCategory(bmi)` w metodzie `diagnose()`:
    - Wchodzisz do wnętrza `getCategory()`
    - Obserwuj kolejne warunki `if` -- który się spełnia? Czy to poprawna kategoria?
9. Użyj **Shift+F8** (Step Out) -- wychodzisz z `getCategory()` z powrotem do `diagnose()`
10. Dla trzeciej osoby (Celina) użyj **Alt+F9** (Run to Cursor) -- ustaw kursor na linii `return name + ...` i naciśnij `Alt+F9`, żeby przeskoczyć resztę obliczeń
11. Sprawdź końcowy wynik w Variables -- jaki `category` dostała Celina?

### Pytania

1. Jaką wartość BMI obliczył program dla Anny? Jaka powinna być prawidłowa wartość?
2. Dlaczego wzór `weight / height` daje błędny wynik? Jaki powinien być poprawny wzór?
3. Dla Bartka -- jaka kategoria została przypisana? Jaka powinna być prawidłowa?
4. Wypełnij tabelę:

| Osoba | Otrzymane BMI | Oczekiwane BMI | Otrzymana kategoria | Oczekiwana kategoria |
|-------|--------------|----------------|--------------------|--------------------|
| Anna | ? | 22.86 | ? | Norma |
| Bartek | ? | 27.78 | ? | Nadwaga |
| Celina | ? | 19.03 | ? | Norma |

5. Jak naprawić bug? Napisz poprawną linię kodu.

---

## Zadanie 2: Inspekcja zmiennych, Watches i Evaluate Expression

**Ćwiczone narzędzia:** panel Variables (rozwijanie obiektów), Watches, Evaluate Expression (`Alt+F8`), Set Value (`F2`).

**Opis:** Program zarządza dziennikiem ocen studentów. Metoda `getBestStudent()` powinna zwracać studenta z najwyższą średnią, ale zwraca najgorszego. Użyj narzędzi inspekcji, żeby zrozumieć dlaczego.

### Kod

Utwórz plik: `src/main/java/com/example/debug/zad2/GradeBook.java`

```java
package com.example.debug.zad2;

import java.util.ArrayList;
import java.util.List;

public class GradeBook {

    private List<StudentRecord> students = new ArrayList<>();

    public void addStudent(String name) {
        students.add(new StudentRecord(name));
    }

    public void addGrade(String name, int grade) {
        for (StudentRecord s : students) {
            if (s.getName().equals(name)) {
                s.addGrade(grade);
                return;
            }
        }
    }

    public StudentRecord getBestStudent() {
        if (students.isEmpty()) return null;

        StudentRecord best = students.get(0);
        for (int i = 1; i < students.size(); i++) {
            StudentRecord current = students.get(i);
            // BUG: < zamiast > (znajduje najgorszego zamiast najlepszego!)
            if (current.getAverage() < best.getAverage()) {
                best = current;
            }
        }
        return best;
    }

    public void printReport() {
        System.out.println("=== Dziennik ocen ===");
        for (StudentRecord s : students) {
            System.out.println(s.getName() + ": oceny=" + s.getGrades()
                + ", średnia=" + String.format("%.2f", s.getAverage()));
        }
        StudentRecord best = getBestStudent();
        System.out.println("Najlepszy student: " + (best != null ? best.getName() : "brak"));
    }

    public static void main(String[] args) {
        GradeBook book = new GradeBook();

        book.addStudent("Anna");
        book.addStudent("Bartek");
        book.addStudent("Celina");
        book.addStudent("Dawid");

        book.addGrade("Anna", 5);
        book.addGrade("Anna", 4);
        book.addGrade("Anna", 5);

        book.addGrade("Bartek", 3);
        book.addGrade("Bartek", 2);
        book.addGrade("Bartek", 3);

        book.addGrade("Celina", 4);
        book.addGrade("Celina", 5);
        book.addGrade("Celina", 4);

        book.addGrade("Dawid", 2);
        book.addGrade("Dawid", 3);
        book.addGrade("Dawid", 2);

        book.printReport();
        // Oczekujemy: "Najlepszy student: Anna" (średnia 4.67)
        // Dostajemy kogoś innego...
    }

    static class StudentRecord {
        private String name;
        private List<Integer> grades;

        StudentRecord(String name) {
            this.name = name;
            this.grades = new ArrayList<>();
        }

        void addGrade(int grade) {
            grades.add(grade);
        }

        String getName() { return name; }
        List<Integer> getGrades() { return grades; }

        double getAverage() {
            if (grades.isEmpty()) return 0;
            int sum = 0;
            for (int g : grades) {
                sum += g;
            }
            return (double) sum / grades.size();
        }
    }
}
```

### Scenariusz debugowania krok po kroku

1. **Ustaw breakpoint** na linii `StudentRecord best = students.get(0);` w metodzie `getBestStudent()`
2. **Uruchom Debug** (`Shift+F9`)
3. Gdy program się zatrzyma, w panelu **Variables** rozwiń obiekt `this` → `students`:
    - Rozwiń `[0]` → sprawdź `name` i `grades`
    - Rozwiń `[1]` → sprawdź `name` i `grades`
    - Powtórz dla `[2]` i `[3]`
4. **Dodaj Watch** (kliknij **+** w panelu Watches):
    - Wpisz: `students.get(0).getAverage()` → jaka średnia Anny? 4.66
    - Wpisz: `students.get(1).getAverage()` → jaka średnia Bartka? 2.33
5. Otwórz **Evaluate Expression** (`Alt+F8`) i wpisz kolejno:
    - `students.stream().mapToDouble(s -> s.getAverage()).max().orElse(0)` → jaka najwyższa średnia? 4.66
    - `students.stream().mapToDouble(s -> s.getAverage()).min().orElse(0)` → jaka najniższa średnia? 2.33
6. Naciśnij **F8** (Step Over) -- `best` wskazuje na `students.get(0)` (Anna) 
7. Teraz krokuj przez pętlę `for` naciskając **F8** wielokrotnie:
    - Iteracja 1 (`i=1`, Bartek): sprawdź warunek `current.getAverage() < best.getAverage()`. Czy jest `true`? Czy `best` się zmienia?
    - Iteracja 2 (`i=2`, Celina): co się dzieje?
    - Iteracja 3 (`i=3`, Dawid): co się dzieje?
8. Po pętli sprawdź kto jest w zmiennej `best` -- czy to najlepszy czy najgorszy student?
9. **Set Value:** Kliknij prawym na zmiennej `best` w Variables → Set Value. Nie musisz nic zmieniać -- to ćwiczenie na przyszłość. Widzisz, że możesz modyfikować stan programu w locie.

### Pytania

1. Jakie średnie mają poszczególni studenci? (Wypełnij z Evaluate Expression)

| Student | Średnia |
|---------|---------|
| Anna | ? |
| Bartek | ? |
| Celina | ? |
| Dawid | ? |

2. Kogo zwróciła metoda `getBestStudent()`? Kogo powinna zwrócić?
3. Jaki jest bug w warunku porównania? Jak go naprawić?
4. Jakie wyrażenie wpisałeś w Evaluate Expression, żeby obliczyć najwyższą średnią?

---

## Zadanie 3: Breakpointy warunkowe i Log Breakpoints

**Ćwiczone narzędzia:** breakpointy warunkowe (Condition), Hit Count (`i % 50 == 0`), Log Breakpoints / Tracepoints (logowanie bez zatrzymywania).

**Opis:** Program analizuje 200 odczytów z czujników temperatury i klasyfikuje je jako NORMAL, WARNING lub CRITICAL. Żaden odczyt nie jest klasyfikowany jako CRITICAL, mimo że wiele z nich ma temperaturę powyżej 40°C. Znajdź dlaczego, używając breakpointów warunkowych i log breakpointów.

### Kod

Utwórz plik: `src/main/java/com/example/debug/zad3/TemperatureSensor.java`

```java
package com.example.debug.zad3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TemperatureSensor {

    public String classify(double temperature) {
        if (temperature > 100) {    // BUG: powinno być > 40 (czujnik mierzy °C, nie wrzący wrzatek!)
            return "CRITICAL";
        } else if (temperature > 30) {
            return "WARNING";
        } else {
            return "NORMAL";
        }
    }

    public void analyzeReadings(List<Reading> readings) {
        int normal = 0, warning = 0, critical = 0;

        for (int i = 0; i < readings.size(); i++) {
            Reading reading = readings.get(i);
            String status = classify(reading.getTemperature());
            reading.setStatus(status);

            switch (status) {
                case "NORMAL" -> normal++;
                case "WARNING" -> warning++;
                case "CRITICAL" -> critical++;
            }
        }

        System.out.println("=== Raport czujników ===");
        System.out.println("NORMAL:   " + normal);
        System.out.println("WARNING:  " + warning);
        System.out.println("CRITICAL: " + critical);
        System.out.println("Razem:    " + readings.size());
    }

    public static void main(String[] args) {
        TemperatureSensor sensor = new TemperatureSensor();
        List<Reading> readings = new ArrayList<>();
        Random rng = new Random(42);

        for (int i = 0; i < 200; i++) {
            String sensorId = "SENSOR-" + (i % 5 + 1);
            // Temperatury od 15°C do 55°C -- wiele powyżej 40°C powinno być CRITICAL!
            double temp = 15 + rng.nextDouble() * 40;
            readings.add(new Reading(sensorId, Math.round(temp * 10.0) / 10.0));
        }

        sensor.analyzeReadings(readings);
        // Oczekujemy: kilkadziesiąt odczytów CRITICAL (temp > 40°C)
        // Dostajemy: 0 odczytów CRITICAL!
    }

    static class Reading {
        private String sensorId;
        private double temperature;
        private String status;

        Reading(String sensorId, double temperature) {
            this.sensorId = sensorId;
            this.temperature = temperature;
        }

        String getSensorId() { return sensorId; }
        double getTemperature() { return temperature; }
        String getStatus() { return status; }
        void setStatus(String status) { this.status = status; }
    }
}
```

### Scenariusz debugowania krok po kroku

**Część A -- Problem ze zwykłym breakpointem:**

1. **Ustaw breakpoint** na linii `String status = classify(reading.getTemperature());` w pętli `for`
2. **Uruchom Debug** (`Shift+F9`)
3. Program zatrzymuje się. Naciśnij **F9** (Resume) kilka razy -- za każdym razem zatrzymuje się ponownie. Przy 200 odczytach to koszmar! **Zatrzymaj program** (`Ctrl+F2`)

**Część B -- Breakpoint warunkowy:**

4. **Prawy klik** na czerwonej kropce breakpointu → w pole **Condition** wpisz: `reading.getTemperature() > 38`
5. **Uruchom Debug** -- teraz program zatrzymuje się tylko dla gorących odczytów
6. Sprawdź w Variables: `reading.getTemperature()` -- jaka wartość? `status` -- jaki?
7. Czy jakikolwiek odczyt > 38°C dostał status "CRITICAL"? Użyj **Evaluate Expression** (`Alt+F8`): wpisz `reading.getTemperature() > 100` → jaki wynik?
8. Zmień warunek breakpointu na: `i % 50 == 0` (zatrzymuje się co 50 iteracji) -- obserwuj postęp przetwarzania

**Część C -- Log Breakpoint (Tracepoint):**

9. **Prawy klik** na breakpoincie → **odznacz** "Suspend" (program NIE będzie się zatrzymywał)
10. Zaznacz **"Evaluate and log"** i wpisz wyrażenie:
    ```
    "i=" + i + " sensor=" + reading.getSensorId() + " temp=" + reading.getTemperature() + " → " + status
    ```
11. **Uruchom Debug** -- program nie zatrzymuje się, ale w zakładce **Console** panelu Debug pojawiają się logi
12. Przejrzyj logi w konsoli -- czy widzisz jakikolwiek wpis z "CRITICAL"?
13. Znajdź w logach odczyty z temperaturą powyżej 40°C -- jaki mają status?

### Pytania

1. Ile razy program się zatrzymywał z warunkiem `reading.getTemperature() > 38`? Czy to lepsze niż 200 zatrzymań?
2. Dlaczego żaden odczyt nie dostaje statusu "CRITICAL"? Jaki jest próg w kodzie?
3. Na co trzeba zmienić próg, żeby czujnik klasyfikował temperatury > 40°C jako CRITICAL?
4. Wymień 3 zalety Log Breakpoints nad `System.out.println`.

---

## Zadanie 4: Exception Breakpoints -- polowanie na NullPointerException

**Ćwiczone narzędzia:** Exception Breakpoints, Caught vs Uncaught, nawigacja po Call Stack (panel Frames).

**Opis:** Program przetwarza listę faktur i generuje raport. Przy uruchomieniu crashuje z `NullPointerException`. Użyj Exception Breakpoint, żeby znaleźć dokładnie który obiekt jest `null` i skąd ta wartość przyszła.

### Kod

Utwórz plik: `src/main/java/com/example/debug/zad4/InvoiceProcessor.java`

```java
package com.example.debug.zad4;

import java.util.List;

public class InvoiceProcessor {

    public void generateReport(List<Invoice> invoices) {
        System.out.println("=== Raport faktur ===");
        for (Invoice invoice : invoices) {
            String line = formatInvoice(invoice);
            System.out.println(line);
        }
    }

    private String formatInvoice(Invoice invoice) {
        String customerInfo = getCustomerInfo(invoice.getCustomer());
        double total = calculateTotal(invoice.getItems());
        return invoice.getId() + " | " + customerInfo + " | Razem: " + total + " zł";
    }

    private String getCustomerInfo(Customer customer) {
        String address = customer.getAddress();
        // NPE gdy address jest null!
        return customer.getName() + ", " + address.toUpperCase();
    }

    private double calculateTotal(List<InvoiceItem> items) {
        double total = 0;
        for (InvoiceItem item : items) {
            total += item.getQuantity() * item.getUnitPrice();
        }
        return total;
    }

    public static void main(String[] args) {
        InvoiceProcessor processor = new InvoiceProcessor();

        List<Invoice> invoices = List.of(
            new Invoice("FV-001",
                new Customer("Jan Kowalski", "ul. Kwiatowa 5, Warszawa"),
                List.of(new InvoiceItem("Laptop", 1, 3500.0),
                        new InvoiceItem("Myszka", 2, 89.99))),
            new Invoice("FV-002",
                new Customer("Anna Nowak", null),  // Anna nie ma adresu!
                List.of(new InvoiceItem("Monitor", 1, 1200.0))),
            new Invoice("FV-003",
                new Customer("Piotr Wiśniewski", "ul. Leśna 12, Kraków"),
                List.of(new InvoiceItem("Klawiatura", 1, 250.0),
                        new InvoiceItem("Słuchawki", 1, 350.0)))
        );

        processor.generateReport(invoices);
    }

    static class Invoice {
        private String id;
        private Customer customer;
        private List<InvoiceItem> items;

        Invoice(String id, Customer customer, List<InvoiceItem> items) {
            this.id = id;
            this.customer = customer;
            this.items = items;
        }

        String getId() { return id; }
        Customer getCustomer() { return customer; }
        List<InvoiceItem> getItems() { return items; }
    }

    static class Customer {
        private String name;
        private String address;

        Customer(String name, String address) {
            this.name = name;
            this.address = address;
        }

        String getName() { return name; }
        String getAddress() { return address; }
    }

    static class InvoiceItem {
        private String productName;
        private int quantity;
        private double unitPrice;

        InvoiceItem(String productName, int quantity, double unitPrice) {
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        String getProductName() { return productName; }
        int getQuantity() { return quantity; }
        double getUnitPrice() { return unitPrice; }
    }
}
```

### Scenariusz debugowania krok po kroku

**Część A -- Uruchom bez debuggera:**

1. **Uruchom program normalnie** (Run, `Shift+F10`). Przeczytaj stack trace w konsoli:
    - Na jakiej linii jest `NullPointerException`?
    - Która metoda rzuciła wyjątek?
    - Czy z samego stack trace potrafisz powiedzieć **co dokładnie** jest `null`?

**Część B -- Exception Breakpoint:**

2. Otwórz dialog breakpointów: `Ctrl+Shift+F8`
3. Kliknij **+** (Add) → wybierz **Java Exception Breakpoints**
4. Wpisz: `NullPointerException` i zatwierdź
5. Zaznacz **Uncaught** (nieprzechwycone), odznacz **Caught** (przechwycone)
6. **Uruchom Debug** (`Shift+F9`)
7. Debugger zatrzymuje się **w momencie** rzucenia NPE. Sprawdź:
    - Na jakiej **dokładnie** linii się zatrzymał?
    - W panelu **Variables** -- jaka wartość ma zmienna `address`?
    - Jaka wartość ma `customer.getName()`?

**Część C -- Nawigacja po Call Stack:**

8. Spójrz na panel **Frames** (po lewej stronie panelu Debug) -- to stos wywołań metod
9. Kliknij na ramkę **`formatInvoice`** (wyżej w stosie) -- jakie wartości widzisz? Jaki jest `invoice.getId()`?
10. Kliknij na ramkę **`generateReport`** (jeszcze wyżej) -- która iteracja pętli? Która faktura spowodowała problem?
11. Kliknij na ramkę **`getCustomerInfo`** (z powrotem) -- teraz wiesz: klient "Anna Nowak" nie ma adresu (`address = null`)

### Pytania

1. Na jakiej linii debugger się zatrzymał? Która zmienna jest `null`?
2. Dla której faktury (jaki `id`) wystąpił problem?
3. Jak naprawić bug? (Podpowiedź: sprawdź `null` przed wywołaniem `toUpperCase()`)
4. Jaka jest różnica między Exception Breakpoint z opcją "Caught" a "Uncaught"? Kiedy który wybrać?
5. Dlaczego nawigacja po Call Stack (Frames) pomogła w zrozumieniu problemu?

---

## Zadanie 5: Method Breakpoints i Field Watchpoints

**Ćwiczone narzędzia:** Method Breakpoints (na interfejsie -- łapie wszystkie implementacje), Field Watchpoints (śledzenie zmian pola).

**Opis:** System powiadomień wysyła wiadomości przez e-mail, SMS i push. Raport powinien pokazywać łączną liczbę wysłanych powiadomień, ale licznik `sentCount` nie zgadza się z rzeczywistą liczbą wywołań. Użyj Method Breakpoints i Field Watchpoints, żeby znaleźć kto nie inkrementuje licznika.

### Kod

Utwórz plik: `src/main/java/com/example/debug/zad5/NotificationSystem.java`

```java
package com.example.debug.zad5;

public class NotificationSystem {

    interface NotificationSender {
        void send(String recipient, String message);  // METHOD BREAKPOINT na tej linii
    }

    static class EmailSender implements NotificationSender {
        private final NotificationManager manager;

        EmailSender(NotificationManager manager) {
            this.manager = manager;
        }

        @Override
        public void send(String recipient, String message) {
            System.out.println("[EMAIL] Do: " + recipient + " → " + message);
            manager.incrementSentCount();
        }
    }

    static class SmsSender implements NotificationSender {
        private final NotificationManager manager;

        SmsSender(NotificationManager manager) {
            this.manager = manager;
        }

        @Override
        public void send(String recipient, String message) {
            System.out.println("[SMS] Do: " + recipient + " → " + message);
            // BUG: brakuje manager.incrementSentCount()!
        }
    }

    static class PushSender implements NotificationSender {
        private final NotificationManager manager;

        PushSender(NotificationManager manager) {
            this.manager = manager;
        }

        @Override
        public void send(String recipient, String message) {
            System.out.println("[PUSH] Do: " + recipient + " → " + message);
            manager.incrementSentCount();
        }
    }

    static class NotificationManager {
        private int sentCount = 0;   // FIELD WATCHPOINT na to pole

        void incrementSentCount() {
            sentCount++;
        }

        int getSentCount() {
            return sentCount;
        }
    }

    public static void main(String[] args) {
        NotificationManager manager = new NotificationManager();

        NotificationSender email = new EmailSender(manager);
        NotificationSender sms = new SmsSender(manager);
        NotificationSender push = new PushSender(manager);

        // Wysyłamy 7 powiadomień
        email.send("jan@example.com", "Twoje zamówienie zostało wysłane");
        sms.send("+48123456789", "Kod weryfikacyjny: 4821");
        push.send("user_anna", "Nowa wiadomość od Bartka");
        email.send("anna@example.com", "Faktura FV-001 do pobrania");
        sms.send("+48987654321", "Twoja paczka czeka w paczkomacie");
        push.send("user_piotr", "Przypomnienie: spotkanie o 15:00");
        email.send("piotr@example.com", "Potwierdzenie rezerwacji");

        System.out.println("\n=== Raport ===");
        System.out.println("Wysłano powiadomień: " + manager.getSentCount());
        System.out.println("Oczekiwano: 7");
        System.out.println(manager.getSentCount() == 7 ? "OK" : "BŁĄD - licznik się nie zgadza!");
    }
}
```

### Scenariusz debugowania krok po kroku

**Część A -- Method Breakpoints:**

1. **Ustaw Method Breakpoint** na linii `void send(String recipient, String message);` w interfejsie `NotificationSender` -- kliknij na margines obok tej deklaracji (pojawi się czerwony romb zamiast kropki)
2. **Uruchom Debug** (`Shift+F9`)
3. Przy **każdym** zatrzymaniu sprawdź w panelu **Frames** (stos wywołań):
    - Która **implementacja** jest wywoływana? (`EmailSender`, `SmsSender` czy `PushSender`?)
    - Jaki jest `recipient`?
    - Jaki jest `message`?
4. Naciśnij **F9** (Resume) po każdym zatrzymaniu. Wypełnij tabelę:

| Lp. | Implementacja | Recipient | Wiadomość (początek) |
|-----|--------------|-----------|---------------------|
| 1 | ? | ? | ? |
| 2 | ? | ? | ? |
| 3 | ? | ? | ? |
| ... | ... | ... | ... |

5. Ile razy w sumie zatrzymał się debugger? Czy to zgadza się z liczbą wywołań `send()`?

**Część B -- Field Watchpoints:**

6. **Usuń** Method Breakpoint (kliknij na romb żeby go usunąć)
7. **Ustaw Field Watchpoint** na polu `private int sentCount = 0;` w klasie `NotificationManager` -- kliknij na margines obok tej deklaracji
8. **Prawy klik** na watchpoincie → zaznacz **Field modification** (interesuje nas kiedy pole się zmienia)
9. **Uruchom Debug**
10. Przy **każdym** zatrzymaniu sprawdź:
    - W jakiej metodzie się zatrzymałeś? (Sprawdź Frames)
    - Jaka jest **nowa** wartość `sentCount`?
    - Z jakiego sendera przyszła zmiana? (Kliknij wyżej w Frames)
11. Po każdym zatrzymaniu naciśnij **F9**. Ile razy Field Watchpoint się aktywował?
12. Porównaj z liczbą wywołań `send()` -- czy któryś sender **nie** powoduje aktywacji watchpointu?

### Pytania

1. Ile razy aktywował się Method Breakpoint? A ile razy Field Watchpoint na `sentCount`?
2. Który sender nie inkrementuje `sentCount`? Jak to ustaliłeś?
3. Czym różni się ikona Method Breakpoint (romb) od zwykłego Line Breakpoint (kropka)?
4. Kiedy warto użyć Field Watchpoint zamiast zwykłego breakpointu?

---

## Zadanie 6: Debugowanie kolekcji i Stream Debugger

**Ćwiczone narzędzia:** inspekcja kolekcji w Variables, Stream Debugger ("Trace Current Stream Chain"), Lambda Breakpoints.

**Opis:** Program analizuje listę pracowników -- filtruje doświadczonych (≥ 3 lata) i oblicza średnią pensję w każdym departamencie. Wyniki są niepoprawne -- raport dotyczy juniorów zamiast seniorów. Użyj Stream Debugger, żeby zwizualizować przepływ danych.

### Kod

Utwórz plik: `src/main/java/com/example/debug/zad6/EmployeeAnalytics.java`

```java
package com.example.debug.zad6;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmployeeAnalytics {

    public static void main(String[] args) {
        List<Employee> employees = List.of(
            new Employee("Anna", "IT", 8500, 5),
            new Employee("Bartek", "IT", 5000, 1),
            new Employee("Celina", "HR", 7200, 4),
            new Employee("Dawid", "HR", 4800, 2),
            new Employee("Ewa", "IT", 9200, 7),
            new Employee("Filip", "Marketing", 6500, 3),
            new Employee("Grażyna", "Marketing", 4200, 1),
            new Employee("Henryk", "HR", 8000, 6),
            new Employee("Iga", "IT", 4500, 0),
            new Employee("Jan", "Marketing", 7800, 5)
        );

        // Cel: średnia pensja DOŚWIADCZONYCH pracowników (>= 3 lata) w każdym departamencie
        // BUG: filtr < 3 zamiast >= 3 (bierze juniorów zamiast seniorów!)
        Map<String, Double> avgSalaryByDept = employees.stream()
            .filter(e -> e.getYearsOfExperience() < 3)
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.averagingDouble(Employee::getSalary)
            ));

        System.out.println("=== Średnia pensja doświadczonych pracowników (>= 3 lata) ===");
        avgSalaryByDept.forEach((dept, avg) ->
            System.out.printf("  %s: %.2f zł%n", dept, avg));

        // Dodatkowa analiza: najlepiej zarabiający w każdym departamencie
        Map<String, Employee> topEarners = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.collectingAndThen(
                    Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary)),
                    opt -> opt.orElse(null)
                )
            ));

        System.out.println("\n=== Najlepiej zarabiający w departamencie ===");
        topEarners.forEach((dept, emp) ->
            System.out.printf("  %s: %s (%.2f zł)%n", dept, emp.getName(), emp.getSalary()));
    }

    static class Employee {
        private String name;
        private String department;
        private double salary;
        private int yearsOfExperience;

        Employee(String name, String department, double salary, int yearsOfExperience) {
            this.name = name;
            this.department = department;
            this.salary = salary;
            this.yearsOfExperience = yearsOfExperience;
        }

        String getName() { return name; }
        String getDepartment() { return department; }
        double getSalary() { return salary; }
        int getYearsOfExperience() { return yearsOfExperience; }
    }
}
```

### Scenariusz debugowania krok po kroku

**Część A -- Inspekcja kolekcji:**

1. **Ustaw breakpoint** na linii `Map<String, Double> avgSalaryByDept = employees.stream()` (na początku streama)
2. **Uruchom Debug** (`Shift+F9`)
3. Gdy program się zatrzyma, w panelu **Variables** rozwiń kolekcję `employees`:
    - Ile elementów ma lista?
    - Rozwiń `[0]` -- kto to? Ile lat doświadczenia?
    - Rozwiń `[4]` -- kto to? Ile lat doświadczenia?

**Część B -- Stream Debugger:**

4. Zostaw breakpoint z kroku 1 na linii `Map<String, Double> avgSalaryByDept = employees.stream()` (uwaga: breakpoint na samym `));` nie zadziała -- to nie jest wykonywalna linia kodu!)
5. **Restart Debug** (`Ctrl+Shift+F9` lub ikonka rerun)
6. Gdy debugger się zatrzyma na linii ze streamem, znajdź w panelu Debug przycisk **"Trace Current Stream Chain"** (ikona streama z lupą) i kliknij
7. Otworzy się **wizualna mapa** przepływu danych. Sprawdź:
    - **filter:** którzy pracownicy **przeszli** przez filtr? Którzy zostali **odrzuceni**?
    - Czy pracownicy z >= 3 lata doświadczenia przeszli, czy zostali odrzuceni?
    - **groupingBy:** jak zostali zgrupowani po departamencie?
8. Porównaj wynik z oczekiwanym -- czy filtr działa prawidłowo?

**Część C -- Lambda Breakpoint:**

9. Ustaw **breakpoint na linii z lambdą** `.filter(e -> e.getYearsOfExperience() < 3)` -- gdy IntelliJ zapyta, wybierz breakpoint **wewnątrz lambdy** (nie na całej linii)
10. **Restart Debug** -- breakpoint aktywuje się **dla każdego elementu** przechodzącego przez `filter`
11. Przy każdym zatrzymaniu sprawdź:
    - `e.getName()` -- kto to?
    - `e.getYearsOfExperience()` -- ile lat?
    - Czy warunek `< 3` jest `true` czy `false` dla tego pracownika?

### Pytania

1. Którzy pracownicy przeszli przez filtr `< 3`? Wymień ich imiona.
2. Czy to są doświadczeni pracownicy (≥ 3 lata)? Co jest nie tak z filtrem?
3. Jak naprawić filtr? Napisz poprawny warunek.
4. Co dokładnie pokazuje Stream Debugger (Trace Current Stream Chain)? Dlaczego jest przydatny?

---

## Zadanie 7: Debugowanie rekurencji i analiza Call Stack

**Ćwiczone narzędzia:** panel Frames / Call Stack, klikanie między ramkami, breakpoint warunkowy na rekurencji, Exception Breakpoint na `StackOverflowError`.

**Opis:** Program rekurencyjnie oblicza łączny rozmiar plików w drzewie katalogów. Przy uruchomieniu wpada w nieskończoną rekurencję i rzuca `StackOverflowError`. Znajdź dlaczego, analizując stos wywołań.

### Kod

Utwórz plik: `src/main/java/com/example/debug/zad7/DirectorySize.java`

```java
package com.example.debug.zad7;

import java.util.Arrays;
import java.util.List;

public class DirectorySize {

    public long calculateSize(FileNode node) {
        if (node.isFile()) {
            return node.getSize();
        }

        long totalSize = 0;
        for (FileNode child : node.getChildren()) {
            // BUG: calculateSize(node) zamiast calculateSize(child)!
            totalSize += calculateSize(node);
        }
        return totalSize;
    }

    public static void main(String[] args) {
        // Drzewo katalogów:
        // home/
        //   documents/
        //     raport.pdf (1500)
        //     notatki.txt (200)
        //   photos/
        //     wakacje.jpg (3500)
        //     rodzina.jpg (2800)
        //   readme.txt (100)

        FileNode home = new FileNode("home", 0, Arrays.asList(
            new FileNode("documents", 0, Arrays.asList(
                new FileNode("raport.pdf", 1500, null),
                new FileNode("notatki.txt", 200, null)
            )),
            new FileNode("photos", 0, Arrays.asList(
                new FileNode("wakacje.jpg", 3500, null),
                new FileNode("rodzina.jpg", 2800, null)
            )),
            new FileNode("readme.txt", 100, null)
        ));

        DirectorySize calculator = new DirectorySize();
        // Oczekiwany rozmiar: 1500 + 200 + 3500 + 2800 + 100 = 8100
        long size = calculator.calculateSize(home);
        System.out.println("Rozmiar katalogu home: " + size + " bajtów");
        System.out.println("Oczekiwano: 8100 bajtów");
    }

    static class FileNode {
        private String name;
        private long size;
        private List<FileNode> children;

        FileNode(String name, long size, List<FileNode> children) {
            this.name = name;
            this.size = size;
            this.children = children;
        }

        String getName() { return name; }
        long getSize() { return size; }
        List<FileNode> getChildren() { return children; }

        boolean isFile() {
            return children == null;
        }
    }
}
```

### Scenariusz debugowania krok po kroku

**Część A -- Obserwacja crashu:**

1. **Uruchom program normalnie** (`Shift+F10`). Co się dzieje? (Program rzuca `StackOverflowError`)

**Część B -- Exception Breakpoint na StackOverflowError:**

2. `Ctrl+Shift+F8` → **+** → Java Exception Breakpoints → wpisz `StackOverflowError`
3. **Uruchom Debug** (`Shift+F9`)
4. Debugger zatrzymuje się. Spójrz na panel **Frames** -- ile ramek `calculateSize` widzisz? (Bardzo dużo!)
5. Kliknij na **pierwszą ramkę** (najwyższą) -- jaka wartość ma `node.getName()`?
6. Kliknij na ramkę **5 pozycji niżej** -- jaka wartość ma `node.getName()`?
7. Kliknij na ramkę **10 pozycji niżej** -- jaka wartość ma `node.getName()`?
8. **Czy `node.getName()` zmienia się między ramkami?** (Nie -- to cały czas ten sam węzeł!)

**Część C -- Breakpoint warunkowy:**

9. Usuń Exception Breakpoint. Ustaw **zwykły breakpoint** na linii `totalSize += calculateSize(node);` z **warunkiem**: `node.getName().equals("home")`
10. **Restart Debug**
11. Debugger zatrzymuje się. Sprawdź w Variables:
    - `node.getName()` → jaka wartość?
    - `child.getName()` → jaka wartość?
    - **Czy `node` i `child` to ten sam obiekt?** Porównaj nazwy.
12. Naciśnij **F7** (Step Into) na linii `calculateSize(node)` -- do jakiego węzła wchodzisz?
13. Sprawdź `node.getName()` wewnątrz nowego wywołania -- czy to `child` czy znowu `node`?

### Pytania

1. Co zobaczysz w panelu Frames po `StackOverflowError`? Ile ramek `calculateSize`?
2. Czy parametr `node` zmienia się między kolejnymi ramkami wywołań? (Kliknij na różne ramki i porównaj)
3. Na czym polega bug? Dlaczego rekurencja nigdy się nie kończy? poniewaz petla sie nigdy nie konczy gdzyz sprawdzamy pierwszy node ktory zawsze ma home subchild i zwracamy number child dla tego samego pliku w kolko.
4. Jak naprawić bug? Napisz poprawną linię kodu. 
5. Jaki jest oczekiwany wynik po naprawie? (Policz ręcznie)
6. 

---

## Zadanie 8: Race condition w wątkach

**Ćwiczone narzędzia:** panel Threads, Suspend Policy (`Thread` zamiast `All`), przełączanie między wątkami, wizualizacja race condition.

**Opis:** System rezerwacji biletów ma pulę 10 biletów. Pięć wątków próbuje zarezerwować po 3 bilety (łącznie 15 prób, ale jest tylko 10 biletów). Program powinien odmówić gdy bilety się skończą, ale z powodu race condition pozwala zarezerwować więcej niż 10 biletów. Użyj debuggera do wizualizacji problemu.

### Kod

Utwórz plik: `src/main/java/com/example/debug/zad8/TicketBookingSystem.java`

```java
package com.example.debug.zad8;

public class TicketBookingSystem {

    private int availableTickets = 10;

    public void bookTicket(String customerName) {
        int current = availableTickets;    // BREAKPOINT (Suspend: Thread)

        if (current > 0) {
            // Symulacja opóźnienia przetwarzania
            try { Thread.sleep(1); } catch (InterruptedException e) { }
            availableTickets = current - 1;
            System.out.println(customerName + " zarezerwował bilet. Pozostało: " + availableTickets);
        } else {
            System.out.println(customerName + " -- BRAK biletów!");
        }
    }

    public int getAvailableTickets() {
        return availableTickets;
    }

    public static void main(String[] args) throws InterruptedException {
        TicketBookingSystem system = new TicketBookingSystem();

        System.out.println("Start: " + system.getAvailableTickets() + " biletów");

        Thread[] threads = new Thread[5];
        for (int t = 0; t < 5; t++) {
            final String name = "Klient-" + (t + 1);
            threads[t] = new Thread(() -> {
                for (int i = 0; i < 3; i++) {
                    system.bookTicket(name);
                }
            }, "Watek-" + (t + 1));
        }

        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("\nKoniec. Bilety pozostałe: " + system.getAvailableTickets());
        System.out.println("Oczekiwano: >= 0 biletów (nie ujemna liczba!)");
        if (system.getAvailableTickets() < 0) {
            System.out.println("BŁĄD: Zarezerwowano więcej biletów niż było dostępnych!");
        }
    }
}
```

### Scenariusz debugowania krok po kroku

**Część A -- Uruchom bez debuggera:**

1. **Uruchom program normalnie** (`Shift+F10`) **5 razy**
2. Czy za każdym razem wynik jest taki sam? Czy `availableTickets` jest kiedyś ujemne?
3. Zapisz wyniki kilku uruchomień

**Część B -- Wizualizacja race condition:**

4. **Ustaw breakpoint** na linii `int current = availableTickets;` w metodzie `bookTicket`
5. **Prawy klik** na breakpoincie → zmień **Suspend** na **Thread** (domyślnie jest "All" -- to zatrzymuje wszystkie wątki; "Thread" zatrzymuje tylko bieżący wątek)
6. **Uruchom Debug** (`Shift+F9`)
7. Jeden z wątków zatrzymuje się. Sprawdź w Variables:
    - `availableTickets` = ? (np. 10)
    - `customerName` = ? (np. "Klient-1")
8. Naciśnij **F8** (Step Over) -- `current` = wartość `availableTickets`
9. **Przełącz się na inny wątek** w panelu **Threads** (po lewej stronie panelu Debug):
    - Kliknij na inny wątek (np. "Watek-2")
    - Jeśli ten wątek też doszedł do breakpointu, sprawdź jego `current`
    - **Czy oba wątki odczytały TĘ SAMĄ wartość `availableTickets`?**
10. Naciśnij **F8** na obu wątkach -- oba robią `availableTickets = current - 1`:
    - Wątek-1: `availableTickets = 10 - 1 = 9`
    - Wątek-2: `availableTickets = 10 - 1 = 9` (nadpisuje na 9 zamiast 8!)
    - **Jeden bilet zniknął!** To jest race condition.

**Część C -- Porównaj Suspend Policy:**

11. Zmień Suspend Policy na **All** (prawy klik na breakpoincie → Suspend: All)
12. **Restart Debug** -- teraz **wszystkie wątki** zatrzymują się gdy breakpoint jest trafiony
13. Zauważ różnicę: z "All" łatwiej kontrolujesz sytuację, ale nie widzisz naturalnego zachowania wątków

### Pytania

1. Czy wynik programu był identyczny przy każdym uruchomieniu (bez debuggera)? Dlaczego?
2. Co to jest race condition? Opisz własnymi słowami na podstawie tego co zaobserwowałeś.
3. Na czym polega problem: dwa wątki odczytują `availableTickets` -- co się dzieje gdy oba odczytają tę samą wartość?
4. Jaka jest różnica między Suspend Policy "All" i "Thread"?
5. Jak można naprawić ten problem? (Podpowiedź: `synchronized`)

---

## Zadanie 9: Deadlock -- diagnoza zawieszenia programu

**Ćwiczone narzędzia:** Pause, Deadlock detection w IntelliJ, stany wątków (BLOCKED), identyfikacja locków.

**Opis:** Program symuluje dwa wątki, które próbują uzyskać dostęp do dwóch współdzielonych zasobów. Program zawiesza się i nigdy się nie kończy. Użyj debuggera, żeby zdiagnozować deadlock.

### Kod

Utwórz plik: `src/main/java/com/example/debug/zad9/ResourceManager.java`

```java
package com.example.debug.zad9;

public class ResourceManager {

    private static final Object printerLock = new Object();
    private static final Object scannerLock = new Object();

    public static void main(String[] args) {
        Thread worker1 = new Thread(() -> {
            System.out.println("Pracownik-1: próbuję zablokować drukarkę...");
            synchronized (printerLock) {
                System.out.println("Pracownik-1: mam drukarkę! Próbuję zablokować skaner...");
                sleep(100); // Daje czas drugiemu wątkowi
                synchronized (scannerLock) {
                    System.out.println("Pracownik-1: mam oba urządzenia! Drukuję i skanuję.");
                }
            }
        }, "Pracownik-1");

        Thread worker2 = new Thread(() -> {
            System.out.println("Pracownik-2: próbuję zablokować skaner...");
            synchronized (scannerLock) {
                System.out.println("Pracownik-2: mam skaner! Próbuję zablokować drukarkę...");
                sleep(100); // Daje czas pierwszemu wątkowi
                synchronized (printerLock) {
                    System.out.println("Pracownik-2: mam oba urządzenia! Skanuję i drukuję.");
                }
            }
        }, "Pracownik-2");

        worker1.start();
        worker2.start();

        System.out.println("[Main] Wątki uruchomione. Czekam na zakończenie...");

        // Program nigdy nie dojdzie tutaj -- deadlock!
        try {
            worker1.join(5000); // Czekaj max 5 sekund
            worker2.join(5000);
        } catch (InterruptedException e) { }

        if (worker1.isAlive() || worker2.isAlive()) {
            System.out.println("\n[Main] TIMEOUT! Wątki nadal działają -- prawdopodobnie deadlock!");
        }
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { }
    }
}
```

### Scenariusz debugowania krok po kroku

> To zadanie jest **obserwacyjne** -- nie ma buga do naprawy, ale musisz **zdiagnozować** deadlock.

1. **Uruchom program w Debug** (`Shift+F9`) -- **BEZ breakpointów!**
2. W konsoli zobaczysz:
   ```
   Pracownik-1: próbuję zablokować drukarkę...
   Pracownik-1: mam drukarkę! Próbuję zablokować skaner...
   Pracownik-2: próbuję zablokować skaner...
   Pracownik-2: mam skaner! Próbuję zablokować drukarkę...
   ```
3. **Zaczekaj 2-3 sekundy** -- program się zawiesi (żadne nowe komunikaty nie pojawiają się). Po 5 sekundach zobaczysz komunikat `TIMEOUT!` -- to potwierdza deadlock.
4. Kliknij przycisk **Pause** (⏸) w panelu Debug, a następnie kliknij ikonę **"Get Thread Dump"** (ikona aparatu 📷) w pasku narzędzi panelu Debug. Otworzy się okno z tekstowym zrzutem wszystkich wątków.
5. **Jak czytać Thread Dump** -- szukaj wątków ze stanem `BLOCKED`. Powinieneś zobaczyć coś podobnego do:
   ```
   "Pracownik-1" java.lang.Thread.State: BLOCKED
      waiting for Pracownik-2 to release lock on <0x...> (a java.lang.Object)
      - locked <0x...> (a java.lang.Object)

   "Pracownik-2" java.lang.Thread.State: BLOCKED
      waiting for Pracownik-1 to release lock on <0x...> (a java.lang.Object)
      - locked <0x...> (a java.lang.Object)
   ```
   Kluczowe informacje w dump to:
    - **`Thread.State: BLOCKED`** -- wątek jest zablokowany (czeka na lock)
    - **`waiting for ... to release lock on <0x34d>`** -- na jaki lock czeka (numer `<0x34d>` to identyfikator obiektu-locka)
    - **`locked <0x34e>`** -- jaki lock trzyma
    - Deadlock rozpoznajesz po tym, że **każdy wątek trzyma lock, na który czeka drugi wątek** (cykliczne oczekiwanie)
6. Przeanalizuj dump obu wątków i wypełnij:
    - **Pracownik-1:** stan = `BLOCKED`, trzyma lock `<...>` (printerLock), czeka na lock `<...>` (scannerLock)
    - **Pracownik-2:** stan = `BLOCKED`, trzyma lock `<...>` (scannerLock), czeka na lock `<...>` (printerLock)
7. Kliknij na **Pracownik-1** w panelu **Threads**:
    - Spójrz na Frames -- na którym `synchronized` jest zablokowany?
    - Który lock trzyma, a na który czeka?
8. Kliknij na **Pracownik-2** i powtórz analizę

> **Uwaga:** Deadlock w tym kodzie powinien wystąpić za każdym razem dzięki `sleep(100)`. Jeśli z jakiegoś powodu się nie pojawia, zwiększ wartość `sleep` do `500` w obu wątkach.
>
> **Tip:** Niektóre wersje IntelliJ mają przycisk **"Detect Deadlock"** (ikona zamka) w oknie Thread Dump, który automatycznie podświetla deadlockowane wątki. Jeśli go widzisz -- kliknij, ale nawet bez niego deadlock widać ręcznie w tekście dump (szukaj `BLOCKED` + `waiting for` + `locked`).

### Pytania

1. Jakie komunikaty pojawiły się w konsoli zanim program się zawiesił?
2. Jakie stany mają oba wątki? (`RUNNING`, `WAITING`, `BLOCKED`, `TIMED_WAITING`?)
3. Narysuj diagram deadlocka:
   ```
   Pracownik-1: trzyma _______, czeka na _______
   Pracownik-2: trzyma _______, czeka na _______
   ```
4. Dlaczego żaden z wątków nie może kontynuować? (Opisz cykliczne oczekiwanie)
5. Jak można zapobiec deadlockowi? (Podpowiedź: oba wątki powinny blokować zasoby w **tej samej kolejności**)

---

## Zadanie 10: Kompleksowe polowanie na bugi -- samodzielne debugowanie

**Ćwiczone narzędzia:** wszystkie poznane wcześniej -- sam zdecyduj, które narzędzie jest najlepsze dla danego buga.

**Opis:** System rejestracji na kursy ma **3 ukryte bugi**. Twoje zadanie: uruchom program, zaobserwuj nieprawidłowe zachowanie i znajdź wszystkie bugi, samodzielnie dobierając narzędzia debugera. Dla każdego buga opisz: (a) jakie narzędzie użyłeś, (b) co zaobserwowałeś, (c) jak naprawić.

### Kod

Utwórz plik: `src/main/java/com/example/debug/zad10/RegistrationSystem.java`

```java
package com.example.debug.zad10;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RegistrationSystem {

    private List<Course> courses = new ArrayList<>();
    private List<Student> students = new ArrayList<>();

    public void addCourse(String name, int maxCapacity) {
        courses.add(new Course(name, maxCapacity));
    }

    public void addStudent(String name, String email) {
        students.add(new Student(name, email));
    }

    public boolean register(String studentName, String courseName) {
        Student student = findStudent(studentName);
        Course course = findCourse(courseName);

        if (student == null || course == null) {
            System.out.println("Nie znaleziono studenta lub kursu!");
            return false;
        }

        // BUG #2: > zamiast >= (pozwala zapisać 1 studenta za dużo!)
        if (course.getEnrolledStudents().size() > course.getMaxCapacity()) {
            System.out.println("Kurs " + courseName + " jest pełny!");
            return false;
        }

        course.addStudent(student);
        student.addCourse(courseName);
        System.out.println(studentName + " zapisany na kurs: " + courseName);
        return true;
    }

    public List<String> getStudentsByCourse(String courseName) {
        Course course = findCourse(courseName);
        // BUG #1: brak sprawdzenia null! NPE gdy kurs nie istnieje
        return course.getEnrolledStudents().stream()
            .map(Student::getName)
            .collect(Collectors.toList());
    }

    public List<String> getMostPopularCourses() {
        // BUG #3: sortowanie rosnące zamiast malejącego!
        return courses.stream()
            .sorted(Comparator.comparingInt(c -> c.getEnrolledStudents().size()))
            .map(c -> c.getName() + " (" + c.getEnrolledStudents().size() + " studentów)")
            .collect(Collectors.toList());
    }

    private Student findStudent(String name) {
        return students.stream()
            .filter(s -> s.getName().equals(name))
            .findFirst().orElse(null);
    }

    private Course findCourse(String name) {
        return courses.stream()
            .filter(c -> c.getName().equals(name))
            .findFirst().orElse(null);
    }

    public static void main(String[] args) {
        RegistrationSystem system = new RegistrationSystem();

        // Kursy
        system.addCourse("Java Podstawy", 2);     // max 2 osoby
        system.addCourse("Bazy Danych", 3);        // max 3 osoby
        system.addCourse("Algorytmy", 2);          // max 2 osoby

        // Studenci
        system.addStudent("Anna", "anna@edu.pl");
        system.addStudent("Bartek", "bartek@edu.pl");
        system.addStudent("Celina", "celina@edu.pl");
        system.addStudent("Dawid", "dawid@edu.pl");

        // Rejestracje
        System.out.println("=== Rejestracja ===");
        system.register("Anna", "Java Podstawy");
        system.register("Bartek", "Java Podstawy");
        system.register("Celina", "Java Podstawy");  // Powinno się nie udać (max 2)!
        system.register("Dawid", "Bazy Danych");
        system.register("Anna", "Bazy Danych");
        system.register("Bartek", "Algorytmy");

        // Sprawdź Bug #2: czy Celina została zapisana mimo limitu?
        System.out.println("\n=== Studenci na Java Podstawy ===");
        List<String> javaStudents = system.getStudentsByCourse("Java Podstawy");
        System.out.println(javaStudents);
        System.out.println("Oczekiwano max 2 studentów, jest: " + javaStudents.size());

        // Sprawdź Bug #3: ranking popularności
        System.out.println("\n=== Najpopularniejsze kursy (od najpopularniejszego) ===");
        system.getMostPopularCourses().forEach(System.out::println);

        // Sprawdź Bug #1: kurs który nie istnieje
        System.out.println("\n=== Studenci na Python (nie istnieje) ===");
        List<String> pythonStudents = system.getStudentsByCourse("Python");
        System.out.println(pythonStudents);
    }

    static class Course {
        private String name;
        private int maxCapacity;
        private List<Student> enrolledStudents = new ArrayList<>();

        Course(String name, int maxCapacity) {
            this.name = name;
            this.maxCapacity = maxCapacity;
        }

        void addStudent(Student student) { enrolledStudents.add(student); }
        String getName() { return name; }
        int getMaxCapacity() { return maxCapacity; }
        List<Student> getEnrolledStudents() { return enrolledStudents; }
    }

    static class Student {
        private String name;
        private String email;
        private List<String> enrolledCourses = new ArrayList<>();

        Student(String name, String email) {
            this.name = name;
            this.email = email;
        }

        void addCourse(String course) { enrolledCourses.add(course); }
        String getName() { return name; }
        String getEmail() { return email; }
        List<String> getEnrolledCourses() { return enrolledCourses; }
    }
}
```

### Wskazówki (bez szczegółowego scenariusza -- sam dobierz narzędzia!)

**Bug #1 -- NullPointerException:**
- Uruchom program normalnie. Na jakiej linii dostaniesz wyjątek?
- Użyj odpowiedniego **Exception Breakpoint** i nawigacji po **Call Stack**, żeby ustalić co jest `null` i dlaczego

**Bug #2 -- Za dużo studentów na kursie:**
- Kurs "Java Podstawy" ma `maxCapacity = 2`, ale Celina też została zapisana (3 osoby!)
- Ustaw **breakpoint warunkowy** w metodzie `register` i użyj **Evaluate Expression**, żeby sprawdzić warunek `course.getEnrolledStudents().size() > course.getMaxCapacity()`
- Czy warunek jest poprawny? Kiedy powinien blokować rejestrację?

**Bug #3 -- Odwrotna kolejność rankingu:**
- Lista najpopularniejszych kursów powinna być od najpopularniejszego do najmniej popularnego
- Użyj **Stream Debugger** (Trace Current Stream Chain) na linii z `.collect(...)` w metodzie `getMostPopularCourses()`
- Sprawdź jak `sorted()` układa elementy -- rosnąco czy malejąco?

### Pytania

Dla **każdego** z 3 bugów opisz:

1. **Jakie narzędzie** debugera użyłeś? (np. Exception Breakpoint, breakpoint warunkowy, Stream Debugger, Evaluate Expression, itp.)
2. **Co zaobserwowałeś** w debuggerze? (np. jaka wartość zmiennej, jaki warunek, jaki przepływ danych)
3. **Na czym polega bug** i **jak go naprawić?** (Napisz poprawną linię kodu)
