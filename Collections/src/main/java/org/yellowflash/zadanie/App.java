package org.yellowflash.zadanie;

import java.util.Arrays;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.yellowflash.kartaPracy.zad1.Kalkulator.calculate;

public class App {
    public static void main(String[] args) {
        //region zadanie1
        Runnable runnable1 = () -> System.out.println("Startuje Program...");
        Runnable runnable2 = () -> System.out.println("Kończę Program...");

        runnable1.run();
        runnable2.run();
        new Thread(runnable1).start();
        //endregion
        //region  zadanie2

        //Function<String,String> deleteSpace = String::trim;
        Function<String,String> toLower = String::toLowerCase;
        Function<String,String> replaceSpace = s ->s.replaceAll(" ","");
        String[] loginy = {"  Adam  ", "ANIA K  ", "  k o w a l ","   lol   "};
        String[] noweLoginy = new String[loginy.length];
        for (int i = 0;i < loginy.length;i++)
            noweLoginy[i] =replaceSpace.andThen(toLower).apply(loginy[i]);
        for (String s : noweLoginy)
            System.out.println(s);
        //endregion

        //region zadanie3
        String samogloski ="aeiouyAEIOUY";
        Function<String, Integer> textLength = string -> string.replaceAll(" ","").length();
        Function<String, Integer> countVowels  =  string -> {
          int count = 0;
            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                if(samogloski.indexOf(c) >= 0) count++;
            }
            return count;
        };
        Scanner scanner = new Scanner(System.in);
        String text = scanner.nextLine();
        System.out.println("Liczba znakow bez spacji: " + textLength.apply(text));
        System.out.println("Liczba samoglosek: " + countVowels.apply(text));
        //endregion
        //region zadanie4

        BiFunction<Integer,Integer,Integer> add = Integer::sum;
        BiFunction<Integer,Integer,Integer> sub = (a,b) -> a - b;
        BiFunction<Integer,Integer,Integer> mul = (a,b) -> a * b;
        BiFunction<Integer,Integer,Integer> div = (a,b) -> a / b;
        Scanner sc = new Scanner(System.in);
        System.out.print("Podaj a: ");
        int a = sc.nextInt();
        System.out.print("Podaj b: ");
        int b = sc.nextInt();
        System.out.print("Podaj operator (+ - * /): ");
        String op = sc.next();
        if((op.equals("/") || op.equals("%")) && (b == 0)){
            System.out.println("You can't divide by 0");
        }else {
            BiFunction<Integer, Integer, Integer> chosen = switch (op) {
                case "+" -> add;
                case "-" -> sub;
                case "*" -> mul;
                case "/" -> div;
                default -> throw new IllegalArgumentException("Nieznany operator: " + op);
            };
            System.out.println("Wynik: " + calculate(a ,b ,chosen));
        //endregion
    }
}
}
