package org.pliki.sam02;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Grep {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        Path path ;
        String phrase;
        if(args.length < 2) {
            System.out.println("Make sure you add both arguments");
            System.out.println("Path: ");
            String pathFromUser = scanner.nextLine();
            System.out.println("phrase: ");
            String phraseFromUser = scanner.nextLine();
            path = Path.of(pathFromUser);
            phrase = phraseFromUser;
        }else{
            path = Path.of(args[0]);
            phrase = args[1];
        }
        AtomicInteger dopasowan = new AtomicInteger();
        AtomicInteger plikiZDopasowaniem = new AtomicInteger();

        try (Stream<Path> sciezki = Files.walk(path)) {

            sciezki
                    .filter(Files::isRegularFile)
                    .filter(path1 -> path1.toString().endsWith(".txt"))
                    .forEach(path1 -> {
                        try {
                            List<String> odczyt = Files.readAllLines(path1);
                            int machesInThisFile = 0;
                            for (int i = 0; i < odczyt.size(); i++) {
                                if(odczyt.get(i).toLowerCase().contains(phrase.toLowerCase())) {
                                    dopasowan.getAndIncrement();
                                    machesInThisFile++;
                                    System.out.println( path1+":"+(i+1)+": " + odczyt.get(i));
                                }
                            }
                            if(machesInThisFile > 0) plikiZDopasowaniem.getAndIncrement();
                        } catch (IOException e) {
                            System.out.println("Something went wrong");
                        }
                    });
            System.out.println("Znaleziono "+ dopasowan+" dopasowań w "+plikiZDopasowaniem+" plikach");
        }
    }
}
