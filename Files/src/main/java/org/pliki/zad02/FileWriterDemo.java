package org.pliki.zad02;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileWriterDemo {

    public static void main(String[] args) throws IOException {
        new File("dane").mkdirs();
        File plik = new File("dane/lista_zakupow.txt");

        // === ZAPIS (nadpisuje całą zawartość) ===
        FileWriter writer = new FileWriter(plik);
        writer.write(String.join("\n", "1. Chleb", "2. Mleko", "3. Jajka") + "\n");
        writer.write("1. Chleb\n");
        writer.write("2. Mleko\n");
        writer.write("3. Jajka\n");
        writer.close();  // BARDZO WAŻNE — bez tego nic nie zostanie zapisane!
        writer.write('c');
        System.out.println("Zapisano listę zakupów do " + plik.getAbsolutePath());

        // === DOPISYWANIE (drugi argument true) ===
        FileWriter dopisujacy = new FileWriter(plik, true);
        dopisujacy.write("4. Masło\n");
        dopisujacy.write("5. Ser\n");
        dopisujacy.close();

        System.out.println("Dopisano dwie linie.");
    }
    /*
    Co stanie się z plikiem lista_zakupow.txt, jeśli istniał, a Ty stworzysz new FileWriter(plik) (bez true)?
    Zostanie on nadpisany, wszystko w nim jest nadpisane.
Po co istnieje flush() skoro jest close()? Czym się różnią?
Flusz wypycha dane na dysk ale zostawia otwarty strumien, gdzie close wypycha dane i zamyka strumine.
Dlaczego FileWriter rzuca IOException? Czego ta klasa nie wie z góry?
if the named file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any other reason
Co stanie się, jeśli wywołasz writer.write(...) po writer.close()?
IO Exception : Stream is closed
     */
}
