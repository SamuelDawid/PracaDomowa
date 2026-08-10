package org.yellowflash.zadanie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class Zadanie5i6 {
    public static void main(String[] args) {
        //region zadanie 5
        UnaryOperator<List<Integer>> removeNegatives = (list) -> {
            list.removeIf(x -> x < 0);
            return list;
        };
        BinaryOperator<Integer> maxOp = (a, b) -> a > b ? a : b;
        List<Integer> nums = new ArrayList<>(Arrays.asList(3, -1, 7, -5, 10, 0));
        removeNegatives.apply(nums);
        System.out.println(nums);
        int max = nums.get(0);
        for (int i = 1; i < nums.size(); i++) {
            max = maxOp.apply(max, nums.get(i));
        }
        System.out.println("Maksimum (przez maxOp): " + max);
        //endregion

        //region zadanie6
        String[] loginy = {"adam", "Ala123", "x", "User_01", "ADMIN", "gość"};
        Predicate<String> minThreeChar = (s) -> s != null && s.length() >= 3;
        Predicate<String> onlyascii = (s) ->  s.matches("[A-Za-z0-9]+");
        Predicate<String> startsWithLetter = (s) -> !s.isEmpty() && Character.isLetter(s.charAt(0));
        Predicate<String> isValidLogin = minThreeChar.and(onlyascii).and(startsWithLetter);
        int count = 0;
        for (String s : loginy)
            if(isValidLogin.test(s)){
                System.out.println(s);
                count++;
            }


        System.out.println("Niepoprawnych loginow "+(loginy.length - count));
        //endregion
    }
}
