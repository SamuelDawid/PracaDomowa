package org.pliki.zad03;

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
        // === ZAPIS przez BufferedWriter nowy uczen===
        BufferedWriter writer1 = new BufferedWriter(new FileWriter(plik, true));
        writer1.write("Kowalska Anna,5");
        writer1.close();
        // === ODCZYT przez BufferedReader linia po linii ===
        BufferedReader reader = new BufferedReader(new FileReader(plik));
        String linia;
        int numer = 1;
        while ((linia = reader.readLine()) != null) {
            String[] parts = linia.split(",");// null = koniec pliku
            System.out.println(numer + ") "+ parts[0]+ parts[1]);
            numer++;
        }
        reader.close();
    }
    /*
Co dokładnie zwraca readLine() po przeczytaniu wszystkich linii?
zwraca null dlatego mamy wrorzec :linia = reader.readLine()) != null
Dlaczego newLine() jest lepsze od dosłownego "\n"?
poniewaz newLine() dodaje znak zgodny z systemem a znak koncowy moze byc inny na roznych systemach
Czemu opakowujemy FileWriter w BufferedWriter (a nie używamy FileWriter bezpośrednio)?
Poniewaz write robu syscall za kazdym znakiem, a BuffedWriter dorzuca do bufora w ram a ten wypycha na dysk kiedy sie zapelni.
Co stanie się z plikiem 1 GB, jeśli zamiast readLine() w pętli wczytasz wszystko jednym read()?
Poniewaz readLine pracuje Strumieniowo, trzyma w pamieci tylko jedna linie naraz. Wiec uzycie tego w jednym read gdzie heap JVM to okolo 512 MB daje nam OutOfMemoryError.
     */
}
