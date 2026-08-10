package org.yellowflash.zadanie;

public class Zadanie11i12 {
    public static void main(String[] args) {
        //region zadanie11
        @FunctionalInterface
        interface StringFormatter {
            String format(String input);
            static String applyFormat(String text, StringFormatter formatter){
             return formatter.format(text);
            };
        }

        StringFormatter toUpper = String::toUpperCase;
        StringFormatter addPrefix = s -> ">>>" + s;
        StringFormatter revers = s -> new StringBuilder(s).reverse().toString();

        String test = "WhyNotTest";

        System.out.println(
        StringFormatter.applyFormat(test,toUpper));
        System.out.println(StringFormatter.applyFormat(test,addPrefix));
        System.out.println(StringFormatter.applyFormat(test,revers));
        //endregion

        //region zadanie12
        @FunctionalInterface
        interface TriFunction<T, U, V, R> {
            R apply(T t, U u, V v);
        }
        TriFunction<Double, Double, Double, Double> weightedAverage = (ocena, waga, maxWaga) -> (ocena * waga ) / maxWaga;
        double[] oceny = {3.0, 4.5, 5.0};
        double[] wagi  = {1.0, 2.0, 3.0};
        double sumaWag = 0;
        for (double w : wagi) sumaWag += w;
        double avg = 0;
        for (int i = 0; i < oceny.length; i++) {
            avg = weightedAverage.apply(oceny[i],wagi[i],sumaWag);
        }
        System.out.println("Średnia ważona: " + avg);
        //endregion
    }

}
