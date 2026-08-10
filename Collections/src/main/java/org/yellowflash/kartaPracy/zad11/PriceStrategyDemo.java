package org.yellowflash.kartaPracy.zad11;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

public class PriceStrategyDemo {
    @FunctionalInterface
    interface PriceStrategy {
        double apply(double basePrice);

        default PriceStrategy andThan(PriceStrategy next){
            return p-> next.apply(this.apply(p));
        }
    }

    static double calculatePrice(double basePrice, PriceStrategy strategy) {
        return strategy.apply(basePrice);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PriceStrategy normal      = p -> p;
        PriceStrategy student     = p -> p * 0.90;
        PriceStrategy vip         = p -> p * 0.80;
        PriceStrategy blackFriday = p -> p * 0.70;
        PriceStrategy loyalty = p -> Math.max(0,p *0.85 -5);
        PriceStrategy mega = student.andThan(blackFriday);
        System.out.println(mega.apply(100));
        Map<String,PriceStrategy> map = new HashMap<>(Map.of("normal",normal,"student",student,"vip",vip,"blackFriday",blackFriday,"loyalty",loyalty));

        double[] ceny = {100.0, 250.0, 399.0,4.99};

        System.out.printf("%-12s %-8s %-8s %-8s %-8s %-12s%n",
                "Cena bazowa", "normal", "student", "vip", "blackFriday","loyalty");
        for (double cena : ceny) {
            System.out.printf("%-12.2f %-8.2f %-8.2f %-8.2f %-8.2f %-12.2f%n",
                    cena,
                    calculatePrice(cena, normal),
                    calculatePrice(cena, student),
                    calculatePrice(cena, vip),
                    calculatePrice(cena, blackFriday),
                    calculatePrice(cena,loyalty)
            );
        }
        String type = scanner.nextLine();

        for (double cena : ceny)
            System.out.println(calculatePrice(cena,map.get(type)));

    }
}
