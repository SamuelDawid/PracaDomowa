package org.yellowflash.zadanie;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class zadanie7i8 {
    public static void main(String[] args) {
        //region zadanie7
        BiPredicate<String,String> sameIgnoreCase = (s1,s2) -> s1 != null && s1.equalsIgnoreCase(s2);
        BiPredicate<String,String> isSuffix = (s1,s2) -> s1 != null && s2 != null && s1.endsWith(s2);
        Scanner sc = new Scanner(System.in);
        System.out.print("Podaj string A: ");
        String a = sc.nextLine();
        System.out.print("Podaj string B: ");
        String b = sc.nextLine();

        System.out.println("sameIgnoreCase(A, B) = " + sameIgnoreCase.test(a, b));
        System.out.println("isSuffix(A, B)       = " + isSuffix.test(a, b));
        //endregion
        //region zadanie8
        List<String> lines = Arrays.asList("alpha", "beta", "gamma", "delta");
        Consumer<String> logger = System.out::println;
        System.out.println("=== logger ===");
        printWithNumbers(lines, logger);
        //endregion
    }
    static void printWithNumbers(List<String> lines, Consumer<String> consumer){
        for (int i = 0; i < lines.size(); i++) {
            String prefix = (i +1) +")" + lines.get(i);
            consumer.accept(prefix);
        }
    }
}
