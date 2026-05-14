package org.pliki.zad06;

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
