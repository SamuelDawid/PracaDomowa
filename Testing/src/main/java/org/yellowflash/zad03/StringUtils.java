package org.yellowflash.zad03;

public class StringUtils {

    public static boolean isPalindrome(String text){
        StringBuilder sb = new StringBuilder(text);

        if(!text.isBlank()) return  text.toLowerCase().trim().endsWith(sb.reverse().toString().trim().toLowerCase());

        return true;
    }
}
