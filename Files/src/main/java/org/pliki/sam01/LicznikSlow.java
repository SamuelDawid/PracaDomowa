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
        Path path1 = args.length < 1 ? Paths.get("dane","sam01","pusty.txt") : Path.of(args[0]);
        Path path2 = args.length < 1 ? Paths.get("dane","sam01","remis.txt") : Path.of(args[0]);

        printPartOne(path);
        printPartOne(path1);
        printPartOne(path2);


    }

    static void printPartOne(Path path){
        if(!Files.exists(path)){
            System.out.println("file does not exits: " +path.toAbsolutePath() );
        }
        try {
            List<String> odczyt = Files.readAllLines(path);
            System.out.println("Liczba lini: " + odczyt.size());
            int words =0;
            long charactersCount = 0;
            String longestWord = "";
            for (String line : odczyt) {
                charactersCount+= line.length() - line.replace(" ", "").length();
                String[] parts = line.split(" ");
                words += parts.length;
                for (String s : parts){
                    longestWord = longestWord.length() < s.length() ? s : longestWord;
                    charactersCount += s.length();
                }

            }
            System.out.println("liczbę słów: " +words);
            System.out.println("liczbę znaków: " +charactersCount);
            if (longestWord.isEmpty()) {
                System.out.println("Najdluzsze slowo: brak");
            } else {
                System.out.println("Najdluzsze slowo: " + longestWord);
            }
        } catch (IOException e) {
            System.out.println("Odczyt sie nie udal.");
        }
    }
}
