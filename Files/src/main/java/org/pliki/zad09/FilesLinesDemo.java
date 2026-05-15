package org.pliki.zad09;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.Stream;

public class FilesLinesDemo {

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("dane"));
        Path plik = Path.of("dane/log.txt");

        // === Najpierw wygenerujmy spory plik logów ===
        List<String> logi = new ArrayList<>();
        for (int i = 0; i < 10_000; i++) {
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
        //ile w sumie znaków mają wszystkie linie z poziomem INFO ==
        System.out.println("ile w sumie znaków mają wszystkie linie z poziomem INFO: ");
        long znaki;
        try (Stream<String> linie = Files.lines(plik)) {
            znaki = linie.filter(l -> l.contains("INFO")).mapToInt(String::length).sum();
            System.out.print(znaki);
        }

    }
    /*
    Dlaczego Files.lines(...) musi być w try-with-resources, a Files.readAllLines(...) nie?
    Poniewaz Files.lines zwraca stream ktorego jezeli nei zamkniemy to moze sie skonczyc deskryptor systemowy.
Co znaczy „leniwe" czytanie?
Leniwe czytanie znaczy to samo kiedy mowimy ze streamy sa leniwe, to znaczy ze dopuki nie dodamy metody terminalniej to i tak sie ta metoda nie wytworzy,
tak samo leniew czytanie jest duzo szybsze i zuzywa malo pamieci.
Czemu strumień można przejść tylko raz?
Poniewaz strumien dziala jak taki rurociag, czyli wykonuje operacje, filer - > count. Po count czyli terminalnej operacji rurociag sie zamyka.
Czy Files.lines(plik).filter(...).count() wczytuje plik raz, czy dwa razy?
Wczytuje tylko raz, ale linia po lini (leniwie )
     */
}
