package org.pliki.zad05;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathDemo {

    public static void main(String[] args) {

        System.out.println(Path.of("dane/witaj.txt").getRoot());          // ?
        System.out.println(Path.of("/Users/anna/witaj.txt").getRoot());   // ?
// na Windowsie:
        System.out.println(Path.of("C:/Users/anna/witaj.txt").getRoot()); // ?
        // Dwa równoważne sposoby tworzenia Path
        Path p1 = Path.of("dane", "raporty", "2024", "styczen.csv");
        Path p2 = Paths.get("dane/raporty/2024/styczen.csv");

        System.out.println("p1 = " + p1);
        System.out.println("p2 = " + p2);
        System.out.println("Czy te same? " + p1.equals(p2));

        System.out.println("\n--- analiza ścieżki p1 ---");
        System.out.println("getFileName():   " + p1.getFileName());     // styczen.csv
        System.out.println("getParent():     " + p1.getParent());       // dane/raporty/2024
        System.out.println("getRoot():       " + p1.getRoot());         // null (relative)
        System.out.println("getNameCount():  " + p1.getNameCount());    // 4
        System.out.println("toAbsolutePath():" + p1.toAbsolutePath());  // /Users/.../dane/raporty/2024/styczen.csv

        // Iteracja po segmentach
        System.out.println("\n--- segmenty ---");
        for (int i = 0; i < p1.getNameCount(); i++) {
            System.out.println("  [" + i + "] " + p1.getName(i));
        }


        // Łączenie i normalizacja
        Path baza = Path.of("dane");
        Path dolaczone = baza.resolve("kopia/notatka.txt"); //
        System.out.println("\nresolve: " + dolaczone);
        Path dolaczoneKolejne = Path.of(baza.toString(),"abc");
        Path brzydka = Path.of("dane/raporty/../raporty/./2024/styczen.csv");
        System.out.println("brzydka:    " + brzydka);
        System.out.println("normalize: " + brzydka.normalize());

        // Konwersja do starego File (jeśli musisz użyć starego API)
        java.io.File staryFile = p1.toFile();
        System.out.println("\nJako File: " + staryFile.getAbsolutePath());
    }
    /*
    Co dokładnie robi Path.of("a", "b") — i czy wynik różni się od Path.of("a/b")?
    nie wynik nie rozni sie, jednak pierwszy przypadek sam nam dodaje znak systemowy czyli w windowsie /, jezli piszemy sami a/b to w linuxie dostaniemy bledy.
    Wiec zeby Pisac Cross-Platformowo powinno sie uzyc "a" i "b".
Czym resolve() różni się od Path.of(...) z dwoma argumentami?
Resolve dolacza prefix czyli to podane w resolve(...) leci na koniec sciezki z obiektu pierwszego i powstaje plik Path.
A Path of. robi podobnie bo tez mozemy zabrac path.toSting i dolaczyc na koniec.
Kiedy getRoot() zwraca null?
Returns the root component of this path as a Path object, or null if this path does not have a root component.
Returns: a path representing the root component of this path, or null.
Czyli jezeli Root zwroci null to znaczy ze sciezka jest wzgledna.
Po co w ogóle wprowadzono Path skoro był już File?
Poniewaz dodano nowe metody do Path ktore umozliwiaja :rozdzielanie segmentów, łączenie, normalizacja, względność, Path sam nic nie czyta,
wiec zrobione klase pomocnicza Path, Path + files daja takze lepsze bledy diagnostyczne.
     */
}
