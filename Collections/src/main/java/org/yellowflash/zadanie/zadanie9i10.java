package org.yellowflash.zadanie;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class zadanie9i10 {
    public static void main(String[] args) {


        //region zadanie9
        Map<String, Integer> productToQty = new HashMap<>();
        productToQty.put("Laptop", 5);
        productToQty.put("Mysz", 12);
        productToQty.put("Klawiatura", 7);
        BiConsumer<String, Integer> reporter = (s,i) -> System.out.println("Product: "+s+", sztuk " + i);
        printReport(productToQty,reporter);
        //endregion
        //region zadanie10
        final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        final Random rng = new Random(41);
        Supplier<String> codeSupplier = () -> {
           StringBuilder sb = new StringBuilder(6);
            for (int i = 0; i < 6; i++) {
                sb.append(letters.charAt(rng.nextInt(letters.length())));
            }
            return sb.toString();
        };
        String[] codes = new String[10];
        for (int i = 0; i < codes.length; i++) {
            codes[i] = codeSupplier.get();
        }
        System.out.println("Wygenerowane kody:");
        for (String c : codes) {
            System.out.println("  " + c);
        }
        //endregion
    }

    static void printReport(Map<String, Integer> map, BiConsumer<String, Integer> consumer){
        for (String s : map.keySet())
            consumer.accept(s, map.get(s));
    }
}
