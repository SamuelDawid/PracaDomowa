package org.pliki.zad11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class CopyMoveDemo {

    public static void main(String[] args) throws IOException {
        Path dane = Path.of("dane");
        Files.createDirectories(dane);

        Path zrodlo = dane.resolve("oryginal.txt");
        Files.writeString(zrodlo, "To jest oryginalna treść.\nLinia druga.");
        System.out.println("Stworzono: " + zrodlo);

        // === KOPIOWANIE ===
        Path kopia = dane.resolve("kopia.txt");
        Path podkat = Path.of("dane/podkatalog");
        Path src = Path.of("src/resources");
        Files.deleteIfExists(kopia);  // posprzątaj na wszelki wypadek
        Files.copy(zrodlo, kopia);
        System.out.println("Skopiowano do: " + kopia);
        System.out.println("Treść kopii:\n" + Files.readString(kopia));
        try{
            Files.copy(podkat,src);
        }catch (IOException e) {
            System.out.println("\nWyjątek przy ponownym kopiowaniu: " + e.getClass().getSimpleName());
        }
        // === Próba kopii, gdy cel istnieje — wybuchnie FileAlreadyExistsException ===
        try {
            Files.copy(zrodlo, kopia);   // już istnieje!
        } catch (IOException e) {
            System.out.println("\nWyjątek przy ponownym kopiowaniu: " + e.getClass().getSimpleName());
        }

        // === Kopia z nadpisywaniem ===
        //Files.copy(zrodlo, kopia, StandardCopyOption.COPY_ATTRIBUTES);
        System.out.println("Nadpisano kopię.");

        // === PRZENIESIENIE / RENAME ===
        Path nowaNazwa = dane.resolve("kopia_ze_zmienioną_nazwą.txt");
        Files.deleteIfExists(nowaNazwa);
        System.out.println("\nPrzeniesiono kopia.txt → " + nowaNazwa.getFileName());
        System.out.println("Czy stara kopia istnieje? " + Files.exists(kopia));
        System.out.println("Czy nowa nazwa istnieje? " + Files.exists(nowaNazwa));
    }
    /*
    Co domyślnie robi Files.copy, jeśli cel istnieje?
    FileAlreadyExistsException
Czemu Files.move w obrębie jednego dysku jest niemal natychmiastowe, a kopiowanie wolne?
Poniewaz kopionwanie to zapisywanie danych, zapisywanie danych na dysku trwa a move to tylko przesuniecie czegos.
Czym różni się StandardCopyOption.REPLACE_EXISTING od StandardCopyOption.COPY_ATTRIBUTES?
REPLACE_EXISTING Pozwala nadpisać istniejący plik docelowy (bez tego → FileAlreadyExistsException)
COPY_ATTRIBUTES Kopiuje też metadane pliku — datę modyfikacji, utworzenia, uprawnienia POSIX. Normalnie kopia ma świeżą datę.
Czy Files.copy na katalogu kopiuje też jego zawartość?
Nie
     */
}
