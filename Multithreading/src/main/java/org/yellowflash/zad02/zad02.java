package org.yellowflash.zad02;

import java.util.Random;

public class zad02 {
    public static void main(String[] args) {
        int[] numbers = new int[1_000_000];
        Random rng = new Random(55);
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = rng.nextInt(0,10000);
        }

    }
}
