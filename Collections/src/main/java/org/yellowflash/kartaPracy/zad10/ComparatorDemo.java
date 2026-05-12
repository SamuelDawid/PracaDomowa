package org.yellowflash.kartaPracy.zad10;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ComparatorDemo {

    record Product(String name, double price, int rating) {}

    public static void main(String[] args) {
        List<Product> products = new ArrayList<>(List.of(
                new Product("Mysz",        89.99, 4),
                new Product("Klawiatura", 199.00, 5),
                new Product("Mysz pro",    89.99, 5),
                new Product("Słuchawki",  349.00, 3),
                new Product("Mata",        89.99, 3)
        ));

        // Cena rosnąco, w razie remisu rating MALEJĄCO (najwyższy rating na górze)
        Comparator<Product> byPriceThenRatingDesc =
                Comparator.comparingDouble(Product::price)
                        .thenComparing(Comparator.comparingInt(Product::rating).reversed()).thenComparing(Comparator.comparing(Product::name));


        products.sort(byPriceThenRatingDesc);

        products.sort((a,b) -> {
        int byPrice = Double.compare(a.price(),b.price());
        if(byPrice != 0) return byPrice;
        int rating = Integer.compare(b.rating(),a.rating());
        if(rating != 0) return rating;
            return a.name().compareTo(b.name());
        });
        System.out.println("Posortowane (cena rosnąco, rating malejąco):");
        for (Product p : products) {
            System.out.println("  " + p);
        }

    }
}
