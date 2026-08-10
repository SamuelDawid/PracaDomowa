package org.yellowflash.kartaPracy.zad4;

import java.util.Scanner;
import java.util.function.BiPredicate;

public class BiPredicateDemo {
    public static void main(String[] args) {
        BiPredicate<String, String> sameIgnoreCase =
                (a, b) -> a != null && a.equalsIgnoreCase(b);

        BiPredicate<String, String> isSuffix =
                (a, b) -> a != null && b != null && a.endsWith(b);

        Scanner sc = new Scanner(System.in);
        System.out.print("Podaj string A: ");
        String a = sc.nextLine();
        System.out.print("Podaj string B: ");
        String b = sc.nextLine();

        System.out.println("sameIgnoreCase(A, B) = " + sameIgnoreCase.test(a, b));
        System.out.println("isSuffix(A, B)       = " + isSuffix.test(a, b));
    }
    /*
Czym BiPredicate<T, U> różni się od Predicate<T>?
BiPredicate porownuje do sibie 2 argumenty zamiast sprwdzania jednego warunku.
Czy BiPredicate ma metody and, or, negate?
Tak poniewaz dziedziczy je po Predicate since Bi to extension of Predicate
Dlaczego ważne jest sprawdzenie a != null zanim wywołasz a.endsWith(b)?
Zeby nie dostac npe
     */
}
