package org.yellowflash.kartaPracy;

import java.util.Scanner;
import java.util.function.BiFunction;

public class Kalkulator {
    static Double calculate(double a, double b, BiFunction<Double, Double, Double> op) {
        return op.apply(a, b);
    }

    public static void main(String[] args) {
        BiFunction<Double, Double, Double> add = (a, b) -> a + b;
        BiFunction<Double, Double, Double> sub = (a, b) -> a - b;
        BiFunction<Double, Double, Double> mul = (a, b) -> a * b;
        BiFunction<Double, Double, Double> div = (a, b) -> a / b;
        BiFunction<Double, Double, Double> mod = (a, b) -> a % b;
        Scanner sc = new Scanner(System.in);
        System.out.print("Podaj a: ");
        double a = sc.nextDouble();
        System.out.print("Podaj b: ");
        double b = sc.nextDouble();
        System.out.print("Podaj operator (+ - * /): ");
        String op = sc.next();
        if((op.equals("/") || op.equals("%")) && (b == 0)){
            System.out.println("You can't divide by 0");
        }else {
            BiFunction<Double, Double, Double> chosen = switch (op) {
                case "+" -> add;
                case "-" -> sub;
                case "*" -> mul;
                case "/" -> div;
                case "%" -> mod;
                default -> throw new IllegalArgumentException("Nieznany operator: " + op);
            };
            System.out.println("Wynik: " + calculate(a, b, chosen));
        }
        /*
        Pytania kontrolne
        Jakie typy są w BiFunction<Integer, Integer, Integer> (kolejność: argument 1, argument 2, wynik)?
        Params:
t – the first function argument
u – the second function argument
Returns:
the function result
        Dlaczego w sygnaturze BiFunction<Integer, Integer, Integer>, a nie BiFunction<int, int, int>?
        Bo Interface BiFunction obsluguje wartosci Obiektowe a nie prymitywne.
        Co metoda calculate zyskuje na tym, że bierze BiFunction zamiast hardcodować +?
        jest otwara na rozszerzanie i zamknieta na modyfikacje.
         */


        
    }
}
