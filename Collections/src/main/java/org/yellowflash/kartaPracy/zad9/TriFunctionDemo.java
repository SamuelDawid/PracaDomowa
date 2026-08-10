package org.yellowflash.kartaPracy.zad9;

public class TriFunctionDemo {
    @FunctionalInterface
    interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }
    @FunctionalInterface
    interface QuadFunction<A,B,C,D,R>{
        R apply(A a,B b,C c,D d);
    }
    public static void main(String[] args) {
        // ocena * waga / sumaWag (czyli udział tej oceny w finałowej średniej ważonej)
        TriFunction<Double, Double, Double, Double> weightedShare =
                (ocena, waga, sumaWag) -> ocena * waga / sumaWag;

        double[] oceny = {3.0, 4.5, 5.0};
        double[] wagi  = {2.0, 2.0, 2.0};
        if(oceny.length != wagi.length) throw new IllegalArgumentException();
        // 1) sumujemy wagi
        double sumaWag = 0;
        for (double w : wagi) sumaWag += w;

        // 2) sumujemy udziały - każdy udział to weightedShare.apply(ocena, waga, sumaWag)
        double srednia = 0;
        for (int i = 0; i < oceny.length; i++) {
            srednia += weightedShare.apply(oceny[i], wagi[i], sumaWag);
        }

        System.out.println("Średnia ważona: " + srednia);
        // Spodziewany wynik: (3*1 + 4.5*2 + 5*3) / (1 + 2 + 3) = (3 + 9 + 15) / 6 = 27/6 = 4.5
    }
}
