package org.pliki.zad13;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        System.out.println("=== Notatnik (komendy: add, list, del N, find X, clear, exit) ===");

        while (true) {
            System.out.print("> ");
            String linia = sc.nextLine().trim();
            if (linia.isEmpty()) continue;

            String komenda = linia.split(" ", 2)[0];
            String reszta = linia.length() > komenda.length()
                    ? linia.substring(komenda.length() + 1)
                    : "";

            switch (komenda) {
                case "clear" -> {
                    System.out.println("Czy aby napewno chcesz usuac wsystkie notaki ?");
                    String input = sc.nextLine();
                    if(input.equalsIgnoreCase("tak")) clear();
                }
                case "add"  -> dodaj(reszta);
                case "list" -> wypisz();
                case "del"  -> {
                    try {
                        usun(Integer.parseInt(reszta));
                    }catch (NumberFormatException e){
                        System.out.println("Please give us number");
                    }

                }
                case "find" -> szukaj(reszta);
                case "exit" -> { System.out.println("Do widzenia!"); return; }
                default     -> System.out.println("Nieznana komenda. Spróbuj: add, list, del N, find X, exit");
            }
        }
    }
    private static void clear() throws IOException {
        Files.write(PLIK,new  ArrayList<>());
    }
    private static void dodaj(String tresc) throws IOException {
        if (tresc.isBlank()) {
            System.out.println("Pusta notatka — pominięto.");
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String result ="["+ LocalDateTime.now().format(formatter)+"]" +" "+ tresc;
        List<String> notatki = new ArrayList<>(Files.readAllLines(PLIK));
        Files.write(PLIK,List.of(result),StandardOpenOption.APPEND);
        System.out.println("Dodano (#" + (notatki.size()+1) + ").");
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
    /*
    Czemu na początku main jest Files.createDirectories(PLIK.getParent())?
   Upewniamy sie czy katalog dane instnieje, nie -> stworz nowy.
Co się stanie, jeśli plik dane/notatki.txt jest otwarty w innym programie (np. w Wordzie) i spróbujesz Files.write?
Dostaniemy AccessDeniedException , jezeli w notatniku to go nadpisze a w linuxie moze nadpisze ale zwrocic stara wiadomsoc
Wskaż dokładnie te miejsca w kodzie, gdzie używasz API z Javy 7 (NIO.2) i gdzie używasz API z Javy 11 (writeString/readString).
linia 18-21 Java 7 ,Files.readAllLines(PLIK) - java 7 . Nie znalazlem zadnych API z Java 11
 Czy program działa też na Javie 8?
 tak powniewa uzywamy API z Javy 7
Co się stanie, jeśli wpiszesz del abc (zamiast liczby)? Jak by to ładnie obsłużyć?
 try {
                        usun(Integer.parseInt(reszta));
                    }catch (NumberFormatException e){
                        System.out.println("Please give us number");
                    }
Jak zmieniłbyś program, żeby zapisywał notatki w formacie JSON zamiast „linia = jedna notatka"?
Dodac dependency do pom najelpiej Jackson, stworzyc klase/record typu Note z polami i Uzyc Mapper z Dependency.
     */
}
