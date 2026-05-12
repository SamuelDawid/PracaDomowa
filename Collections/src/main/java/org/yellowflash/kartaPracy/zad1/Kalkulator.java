package org.yellowflash.kartaPracy.zad1;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Kalkulator {
    public static Integer calculate(int a, int b, BiFunction<Integer, Integer, Integer> op) {
        return op.apply(a, b);
    }
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


        

