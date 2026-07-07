package org.yellowflash.zad06;

import java.util.function.Predicate;

public class PasswordValidator {
    public static final int minLength = 8;
    public static boolean isValid(String password){
        Predicate<String> minLeng = string -> string != null && string.length() >= minLength;
        Predicate<String> containsNumber = string -> string.chars().anyMatch(Character::isDigit);
        Predicate<String> containsCapitalLetter = string -> string.chars().anyMatch(Character::isUpperCase);
        Predicate<String> isValidPassword = minLeng.and(containsNumber).and(containsCapitalLetter);

        return isValidPassword.test(password);
    }
}
