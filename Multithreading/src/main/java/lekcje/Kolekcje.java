package lekcje;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Kolekcje {
//    public static void main(String[] args) {
//        Map<String, Integer> map = new HashMap<>();
//        List<String> list = new ArrayList<>();

    /// /        Collections.synchronizedCollection()
//    }
    public static void main(String[] args) throws InterruptedException {
        ConcurrentHashMap<String, Integer> wordCount = new ConcurrentHashMap<>();
        // |___|___| CAS -> compareAndSweep

        String[] texts = {
                "Java jest super Java jest szybka",
                "Java ma wątki wątki są fajne",
                "Kolekcje w Java są thread-safe"
        };

        Thread[] threads = new Thread[texts.length];
        for (int i = 0; i < texts.length; i++) {
            final String text = texts[i];
            threads[i] = new Thread(() -> {
                for (String word : text.split(" ")) {
                    // atomowe "pobierz i zaktualizuj"
                    wordCount.merge(word, 1, Integer::sum);
                }
            });
            threads[i].start();
        }

        for (Thread t : threads) t.join();

        wordCount.forEach((word, count) ->
                System.out.println(word + " -> " + count)
        );
    }
}

/*
new ConcurrentHashMap<>() – tworzy thread-safe mapę. Operacje get/put/remove są atomowe.
.merge(key, value, remappingFunction) – jeśli klucz nie istnieje, wstawia wartość. Jeśli istnieje, łączy starą wartość z nową za pomocą podanej funkcji. Atomowa operacja – nie trzeba synchronizować!
.forEach(BiConsumer) – iteruje po wszystkich parach klucz-wartość. Thread-safe, ale widzi „migawkę" danych.
 */

