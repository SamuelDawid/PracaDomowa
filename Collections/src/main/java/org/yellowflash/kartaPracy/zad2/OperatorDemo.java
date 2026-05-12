package org.yellowflash.kartaPracy.zad2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class OperatorDemo {
    public static void main(String[] args) {

        // UnaryOperator - usuwa liczby ujemne z listy (mutuje listę i zwraca tę samą referencję)
        UnaryOperator<List<Integer>> removeNegatives = list -> {
            list.removeIf(x -> x < 0);
            return list;
        };

        // BinaryOperator - wybiera większą z dwóch liczb
        BinaryOperator<Integer> maxOp = Integer::max;
        BinaryOperator<String> concat = (a,b) -> a+", "+b;
        // Test
        List<Integer> nums = new ArrayList<>(Arrays.asList(3, -1, 7, -5, 10, 0));
        List<Integer> isItStillNums = removeNegatives.apply(nums);
        List<String> names = Arrays.asList("Jan","Bartek","Mateusz","Robert");
        System.out.println("Przed: " + nums);
        removeNegatives.apply(nums);
        System.out.println("Po removeNegatives: " + nums);
        String result = names.getFirst();
        for (int i = 1; i < names.size(); i++) {
            result = concat.apply(result,names.get(i));
        }

        // Maximum w pętli for, BEZ stream()
        int max = nums.get(0);
        for (int i = 1; i < nums.size(); i++) {
            max = maxOp.apply(max, nums.get(i));
        }
        System.out.println("Czy to ten sam obiekt? " + (nums == isItStillNums));
        System.out.println("Maksimum (przez maxOp): " + max);
        /*
        Dlaczego UnaryOperator<List<Integer>> jest lepsze niż Function<List<Integer>, List<Integer>> w tym przypadku?
        Poniewaz UnaryOperator to bardziej specificzny typ Function, zapis jest krotwszy i czytelniejszny czyli wiemy ze zwracamy List<Integer>, czyli kompilator pilnuje typu wejscia i wyjscia.
        Czy BinaryOperator<Integer> zadziała tam, gdzie kod wymaga BiFunction<Integer, Integer, Integer>? (Wskazówka: dziedziczenie.)
        Tak dziala poniewaz  Binary Operator jest bardziej specyficznym typem BiFunction rozszerza interface.
        Co zwróci removeNegatives.apply(nums) — nową listę czy tę samą? Sprawdź referencje.
        Te same reference, lambda tutaj nam przekszalca nasza liste.
         */
    }
}
