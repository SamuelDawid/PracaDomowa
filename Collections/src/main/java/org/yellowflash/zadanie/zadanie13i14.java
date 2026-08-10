package org.yellowflash.zadanie;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class zadanie13i14 {
    public static void main(String[] args) {
        //region zadanie13
        List<Product> products = new ArrayList<>(List.of(
                new Product("Mysz", 89.99, 4),
                new Product("Klawiatura", 199.00, 5),
                new Product("Mysz pro", 89.99, 5),
                new Product("Słuchawki", 349.00, 3),
                new Product("Mata", 89.99, 3)
        ));
        Comparator<Product> priceDescending = Comparator.comparingDouble(Product::price);
        Comparator<Product> ratingAscending = Comparator.comparingInt(Product::rating).reversed();
        products.sort(priceDescending.thenComparing(ratingAscending));
        System.out.println("Posortowane (cena rosnąco, rating malejąco):");
        for (Product p : products) {
            System.out.println("  " + p);
        }
        //endregion

        //region zadanie14
        @FunctionalInterface
        interface PriceStrategy {
            double apply(double basePrice);
            static double calculatePrice(double basePrice, PriceStrategy strategy){
             return strategy.apply(basePrice);
            };
        }

        PriceStrategy normal = p -> p;
        PriceStrategy student = p -> p * 0.9;
        PriceStrategy vip = p -> p * 0.8;
        PriceStrategy blackFriday = p -> p * 0.7;
        PriceStrategy[] array = {normal,student,vip,blackFriday};
        int[] prices = {200,100,150,20};
        for (int i = 0; i < prices.length; i++) {
            System.out.println(
            PriceStrategy.calculatePrice(prices[i],array[i]));
        }
        //endregion
    }
}
