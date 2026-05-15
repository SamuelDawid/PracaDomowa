package org.pliki.zad01;
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
        boolean usuniety = plik.delete();
        System.out.println("Usunięto plik: " + usuniety);
    }
    /*
Co dokładnie robi new File("dane/witaj.txt") — w którym momencie powstaje coś na dysku?
Po plik.creteNewFile() - wtedy tworzony jest nowy plik, File plik = new File to tylko nadanie adresu.
Czym różni się getPath() od getAbsolutePath()?
getPath daje sciezke z katalogu  a drugi daje absolutna z dysku,
Dlaczego createNewFile() zwraca boolean, a nie po prostu rzuca wyjątek, gdy plik już istnieje?
Poniewaz na poczatku gdy Java zostala stworzone to stworzenie/ usuniecie pliku to tylko informacji tak albo nie, a w dzisiejszych czasach rzuca sie konkretny wyjatek z informacja co sie stalo i dlaczego zeby nakierowac programiste.
W którym przypadku użyłbyś dziś java.io.File, a w którym java.nio.file.Path?
W dzisiejszych czasach uzycie File bylo by nieodpowiednie poniewaz sa nowe biblioteki ktore sa duzo lepsze pod katem funcjonalnosci. Jednak moze w jakies starej bibiotece by mozna bylo uzyc ktora by wymagala Typu File.
     */
}
