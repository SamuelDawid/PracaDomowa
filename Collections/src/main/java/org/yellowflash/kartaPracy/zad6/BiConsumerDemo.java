package org.yellowflash.kartaPracy.zad6;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class BiConsumerDemo {
    static void printReport(Map<String, Integer> map, BiConsumer<String, Integer> consumer) {
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            consumer.accept(e.getKey(), e.getValue());
        }
    }

    public static void main(String[] args) {
        Map<String, Integer> productToQty = new LinkedHashMap<>();
        productToQty.put("Laptop", 5);
        productToQty.put("Mysz", 12);
        productToQty.put("Klawiatura", 7);

        BiConsumer<String, Integer> reporter =
                (name, qty) -> System.out.println("Produkt: " + name + ", sztuk: " + qty);
        BiConsumer<String,Integer> warehouse = (k,v) -> {
            if(v < 10) System.out.println("Low Stock for "+k);
        };
        System.out.println("=== Raport (przez własną printReport) ===");
        printReport(productToQty, reporter.andThen(warehouse));

        System.out.println("\n=== To samo przez Map.forEach ===");
        productToQty.forEach(reporter.andThen(warehouse));

    }
    /*
    Jaka jest sygnatura BiConsumer#accept?
    void (T t, R r)
Po co istnieje Map.forEach(BiConsumer) skoro można entrySet().forEach(...)?
bo entry set przechodzi tylko po kluczach a for each idzie po obu klucz i wartosc
Czy w BiConsumer<String, Integer> r = (k, v) -> ... typy są w kolejności klucz, wartość?
tak
     */
}
