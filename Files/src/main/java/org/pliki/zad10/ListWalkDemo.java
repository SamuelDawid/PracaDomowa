package org.pliki.zad10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class ListWalkDemo {

    public static void main(String[] args) throws IOException {
        Path dane = Path.of("dane");
        Files.createDirectories(dane);
        Path podkatagol = Path.of("dane/podkatalog");
        // Wygenerujmy parę plików, żeby było co listować
        Files.writeString(dane.resolve("plik1.txt"), "treść 1");
        Files.writeString(dane.resolve("plik2.txt"), "treść 2");
        Files.writeString(dane.resolve("notatka.md"), "# Notatka");
        Files.createDirectories(dane.resolve("podkatalog"));
        Files.writeString(dane.resolve("podkatalog/glebsze.txt"), "ukryte głębiej");

        // === Files.list — TYLKO bezpośrednia zawartość ===
        System.out.println("=== Files.list (płasko) ===");
        try (Stream<Path> sciezki = Files.list(podkatagol)) {
            sciezki.forEach(p -> System.out.println("  " + p));
        }

        // === Files.walk — REKURENCYJNIE ===
        System.out.println("\n=== Files.walk (rekurencyjnie) ===");
        try (Stream<Path> sciezki = Files.walk(podkatagol)) {
            sciezki.forEach(p -> System.out.println("  " + p));
        }

        // === Filtrowanie: tylko pliki .txt, w całym drzewie ===
        System.out.println("\n=== Tylko pliki .txt (w całym drzewie) ===");
        long ileTxt;
        try (Stream<Path> sciezki = Files.walk(dane)) {
            ileTxt = sciezki
                    .filter(Files::isRegularFile)
                    .sorted()
                    .filter(p -> p.toString().endsWith(".txt"))
                    //.peek(p -> System.out.println("  " + p))
                    .peek(p -> {
                        try {
                            System.out.println(" " + p + " (" + Files.size(p) + " B)");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .count();
        }
        System.out.println("Razem plików .txt: " + ileTxt);

        // === Maksymalna głębokość przy walku ===
        System.out.println("\n=== Files.walk z maxDepth=1 (jak Files.list) ===");
        try (Stream<Path> sciezki = Files.walk(dane, 1)) {
            sciezki.forEach(p -> System.out.println("  " + p));
        }
    }
    /*
    Czym Files.list różni się od Files.walk przy katalogu z 1 plikiem i 0 podkatalogami
list nam da tylko 1 linie dane/plik a walk wejdzie 2 razy rekurencyjnie pierwsze dane potem dane/plik
Czemu Files.walk wlicza sam katalog startowy w wynikach?
poniewaz to jest jego punkt startowy w drzewie.
Jaki typ ma element strumienia z Files.list / Files.walk?
Stream path
Jak ograniczyć głębokość rekurencji w Files.walk?
uzyc depth Files.walk(start, maxDepth).
     */
}
