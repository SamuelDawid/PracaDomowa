package org.pliki.sam01;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class LicznikSlow {
    public static void main(String[] args) {

        Path path = args.length < 1 ? Paths.get("dane","sam01","tekst.txt") : Path.of(args[0]);
        System.out.println("Szukam pliku: " + path.toAbsolutePath());
        if(!Files.exists(path)){
            System.out.println("file does not exits: " );
        }
        try {
            List<String> odczyt = Files.readAllLines(path);
            System.out.println("Liczba lini: " + odczyt.size());

        } catch (IOException e) {
            System.out.println("Odczyt sie nie udal.");
        }


    }
}
