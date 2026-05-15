package org.pliki.zad04;

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
          //  if (true) throw new RuntimeException("nagła awaria");
            writer.write("Druga linia notatki.");
            writer.newLine();
            // Brak writer.close() — Java zrobi to za nas po wyjściu z try.
        } catch (IOException e) {
            System.err.println("Błąd zapisu: " + e.getMessage());
        }

        // === ODCZYT — try-with-resources również ===
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
    }
    /*
    Co dokładnie dzieje się z writer, gdy program wychodzi z bloku try (...) { ... } przez wyjątek?
    writer zostaje zamkniety tuz przed wyjatkiem czyli cala reszta jest wrzucona do pliku przez flush a potem zostaje wyjatek wrzucony do catch.
Jaki interfejs musi implementować klasa, żeby działała w try-with-resources?
AutoCloseable interface ktory mozna znalesc w hierarchi super clasy Closable (CTRl +H )
Czy w jednym try (...) można otworzyć kilka zasobów? Jeśli tak — w jakiej kolejności są zamykane?
tak mozna jednak dziala tutaj kolejnosc LIFO.
Po co osobne catch na IOException skoro try-with-resources „samo zamyka"?
Poniewaz moge byc innego rodzaju IOException rzucone w tym kodznie nie tylko na zamkniecie.
     */
}
