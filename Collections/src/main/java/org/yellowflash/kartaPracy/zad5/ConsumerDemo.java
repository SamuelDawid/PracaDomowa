package org.yellowflash.kartaPracy.zad5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConsumerDemo {
    static void printWithNumbers(List<String> lines, Consumer<String> consumer) {
        for (int i = 0; i < lines.size(); i++) {
            String numbered = (i + 1) + ") " + lines.get(i);
            consumer.accept(numbered);
        }
    }

    public static void main(String[] args) {
        List<String> lines = Arrays.asList("alpha", "beta", "gamma", "delta");
        List<String> log = new ArrayList<>();
        Consumer<String> logger = System.out::println;
        Consumer<String> shouter = s -> System.out.println(">>> " + s.toUpperCase());
        Consumer<String> auditor = log::add;
        System.out.println("=== logger ===");
        printWithNumbers(lines, logger);

        System.out.println("\n=== logger.andThen(shouter) ===");
        printWithNumbers(lines, logger.andThen(shouter));
        printWithNumbers(lines, auditor);
        log.forEach(System.out::println);
        Function<String, Void> printer = s -> {
            System.out.println(s);
            return null;          // <-- co tu wstawisz?
        };
    }
    /*
    Jaki typ zwraca Consumer#accept?
    void
Czym Consumer<String> różni się od Function<String, Void>?
Function will still return the result of the apply which returns R result so we must return null.
When Cosumer only perform the action and doesnt return anything.
Jak działa c1.andThen(c2) na pojedynczym elemencie?
Wykonuje pierwsze consumer a potem drugi.
     */
}
