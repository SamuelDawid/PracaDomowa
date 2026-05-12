package org.yellowflash.kartaPracy.zad12;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MethodReferenceDemo {

    public static void main(String[] args) {
        // 1) Statyczna metoda
        Function<String, Integer> parse1 = s -> Integer.parseInt(s);   // lambda
        Function<String, Integer> parse2 = Integer::parseInt;          // referencja
        System.out.println(parse2.apply("42"));

        // 2) Metoda instancyjna konkretnego obiektu
        Consumer<String> print1 = System.out::println;          // lambda
        Consumer<String> print2 = System.out::println;                 // referencja
        print2.accept("Hello");

        // 3) Metoda instancyjna typu T (bierze T jako parametr "this")
        Function<String, String> upper1 = s -> s.toUpperCase();        // lambda
        Function<String, String> upper2 = String::toUpperCase;         // referencja
        System.out.println(upper2.apply("java"));

        // 4) Konstruktor
        Supplier<List<String>> factory1 = () -> new ArrayList<>();     // lambda
        Supplier<List<String>> factory2 = ArrayList::new;              // referencja
        List<String> list = factory2.get();
        list.add("element");
        System.out.println(list);
        Function<String, String> prefix = s -> "PREFIX " + s;
    }
}
