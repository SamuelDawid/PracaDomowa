package org.pliki.zad08;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ReadAllLinesDemo {

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("dane"));
        Path plik = Path.of("dane/zadania.txt");

        // === ZAPIS LISTY DO PLIKU ===
        List<String> zadania = List.of(
                "[ ] Kupić chleb",
                "[ ] Wynieść śmieci",
                "[x] Odpisać na maila",
                "[ ] Zadzwonić do mamy",
                "[x] Zapłacić rachunek za prąd"
        );
        Files.write(plik, zadania);
        System.out.println("Zapisano " + zadania.size() + " zadań do " + plik);

        // === ODCZYT WSZYSTKICH LINII ===
        List<String> wczytane = Files.readAllLines(plik);
        System.out.println("\nWczytane zadania:");
        for (int i = 0; i < wczytane.size(); i++) {
            System.out.println((i + 1) + ". " + wczytane.get(i));
        }
        int count = 0;
        // === FILTROWANIE — tylko nieukończone ===
        System.out.println("\nNieukończone:");
        for (String z : wczytane) {
            if (z.startsWith("[ ]")) {
                count++;
                System.out.println("  -> " + z);
            }
        }

        // === MODYFIKACJA — dodajemy nowe zadanie i zapisujemy ===
        List<String> noweZadania = new ArrayList<>(wczytane);
        noweZadania.add("[ ] Wyrzucić śmieci od sąsiada");
        Files.write(plik, noweZadania);
        System.out.println("\nZapisano nową wersję, teraz " + noweZadania.size() + " zadań."+ "\n" + "Ukonczono: "+ (noweZadania.size()- count)+" z " +noweZadania.size());

    }
    /*
    Czym Files.readAllLines różni się od Files.readString?
    readAllLines zwraca nam Liste Stringow, gdzie readString zwraca jeden string
Co stanie się, jeśli plik ma 5 GB, a Ty wywołasz Files.readAllLines?
Dostanie outofmemory exception bo znowy dodamy 5bg do heapa ktory ma tylko max 512 mb w JVM
Dlaczego trzeba przepisać List.of(...) do new ArrayList<>(...) przed dodaniem elementu?
Bo lista jest niemutowalna.
Czy Files.write(path, list) dodaje znak końca linii po każdym elemencie listy?
tak dodaje System.lineSeperator();
     */
}
