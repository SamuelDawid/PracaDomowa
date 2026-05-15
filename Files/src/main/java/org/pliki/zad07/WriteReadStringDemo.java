package org.pliki.zad07;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class WriteReadStringDemo {

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("dane"));
        Files.writeString(Path.of("dane/config.json"), "{\"port\": 8080, \"debug\": true}");
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
    /*
    Po co istnieje Files.writeString, skoro Java 1.1 miała już FileWriter?
   Kod jest duzo krutszy, i Files nam daje domyslnie UFT-8.
Co domyślnie robi Files.writeString z istniejącym plikiem — nadpisuje czy dopisuje?
nadpisuje jezeli nei dodamy opcji append.
Jaki konkretny wyjątek dostajesz przy readString na nieistniejącym pliku?
NoSuchFileException
W jakim kodowaniu Java zapisuje tekst, jeśli nie wskażesz inaczej?
UTF-8
     */
}
