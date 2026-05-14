package org.pliki.zad12;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class BinaryStreamsDemo {

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("dane"));

        // === 1. Stwórzmy "sztuczny" plik binarny — sekwencję 256 bajtów ===
        Path zrodlo = Path.of("dane/binarny.dat");
        byte[] zawartosc = new byte[256];
        for (int i = 0; i < 256; i++) {
            zawartosc[i] = (byte) i;
        }
        Files.write(zrodlo, zawartosc);
        System.out.println("Stworzono " + zrodlo + " (" + Files.size(zrodlo) + " B)");

        // === 2. Skopiowanie strumieniowo (klasyczny patent) ===
        Path cel = Path.of("dane/binarny_kopia.dat");
        try (InputStream in = new BufferedInputStream(Files.newInputStream(zrodlo));
             OutputStream out = new BufferedOutputStream(Files.newOutputStream(cel))) {

            byte[] bufor = new byte[64];   // czytamy po 64 bajty
            int przeczytanych;
            while ((przeczytanych = in.read(bufor)) != -1) {   // -1 = koniec pliku
                out.write(bufor, 0, przeczytanych);
            }
        }
        System.out.println("Skopiowano strumieniowo do " + cel
                + " (" + Files.size(cel) + " B)");

        // === 3. Skrót dla małych plików: readAllBytes / write ===
        Path celSkrot = Path.of("dane/binarny_skrot.dat");
        byte[] wszystkie = Files.readAllBytes(zrodlo);
        Files.write(celSkrot, wszystkie);
        System.out.println("Skopiowano przez readAllBytes/write: "
                + celSkrot + " (" + Files.size(celSkrot) + " B)");

        // === 4. Skrót II: Files.copy(InputStream, Path) ===
        Path celCopy = Path.of("dane/binarny_copy.dat");
        try (InputStream in = Files.newInputStream(zrodlo)) {
            Files.copy(in, celCopy, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        System.out.println("Skopiowano przez Files.copy: "
                + celCopy + " (" + Files.size(celCopy) + " B)");
    }
    /*
    Czemu do obrazów / PDF-ów używamy InputStream/OutputStream, a nie Reader/Writer?
   reader i writer pracuje na znakach,  a stream pracuje na bajtach bez interpetacji znakow tylko na wartosciach 0-255,
   jeśli otworzysz obraz JPG jako Reader (znaki), Java spróbuje zinterpretować każdy bajt jako znak Unicode.
   Niektóre kombinacje bajtów nie są poprawnym znakiem → Java podstawia ? → plik się uszkodzi.
   Nawet jeśli go potem zapiszesz Writerem, dane są stracone.
Co oznacza -1 zwrócone przez read(bufor)?
oznacza to koniec pliku
Po co opakowywać Files.newInputStream(...) w BufferedInputStream?
Creates a BufferedInputStream with the specified buffer size, and saves its argument, the input stream in, for later use.
An internal buffer array of length size is created and stored in buf.
Bo Files . new InputStream otwiera ten plik a BufferedInput stwarza buffer z odpowienim sizem jako stream przez co mozemy czytac duze pliki.
Plus bez bufora kazdy read to syscall do systemu operacyjnego.
Dla bardzo dużego pliku (10 GB) — Files.readAllBytes czy strumieniowe kopiowanie? Dlaczego?
Strumieniowe kopionowanie poniewaz jest leniwe i mozemu isc buffer po bufferze zamiast czytac all bytem, wydaje mi sie ze bedzie outofmemory excepiton bo heap jest maly

     */
}
