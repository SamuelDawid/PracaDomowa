package org.yellowflash.kartaPracy.zad8;

public class StringFormatterDemo {
    @FunctionalInterface
    interface StringFormatter {
        String format(String input);

        default void defvoid() {

        }
        default void defvosid(){}
    }

    static String applyFormat(String text, StringFormatter formatter) {
        return formatter.format(text);
    }
    static void formatagain(String text, StringFormatter formatter){ formatter.format(text);}


    public static void main(String[] args) {
        StringFormatter upper   = String::toUpperCase;
        StringFormatter prefix  = s -> ">>> " + s;
        StringFormatter reverse = s -> new StringBuilder(s).reverse().toString();

        String text = "Java";

        System.out.println("upper:   " + applyFormat(text, upper));
        System.out.println("prefix:  " + applyFormat(text, prefix));
        System.out.println("reverse: " + applyFormat(text, reverse));
    }
    /*
    Co dokładnie sprawdza @FunctionalInterface?
    Sprawdza że interfejs ma dokładnie jedną metodę abstrakcyjną, co ubiespieca nas przed dodaniem kolejnej metody w przyszlosci i popsucie lambd
Czy interfejs funkcyjny może mieć metody default i static?
tak mzoe
Dlaczego applyFormat bierze StringFormatter, a nie konkretną klasę?
Poniewaz lambda jest implemntacja interfejsu i nie musimy pisac klasy
     */
}
