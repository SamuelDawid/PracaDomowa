package org.yellowflash.zad02;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class zad02 extends Thread{

    public static void main(String[] args) throws InterruptedException {
        int[] numbers = new int[1_000_000];

        long singleThreadSum = 0;
        Random rng = new Random(55);
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = rng.nextInt(10000);
        }
        long startSingle = System.nanoTime();
        for (int i = 0; i < numbers.length; i++) {
            singleThreadSum += numbers[i];
        }
        long endSingle = System.nanoTime();

        Scanner sc = new Scanner(System.in);
        System.out.println("how many parts: ");
        int k = sc.nextInt();
        long[] partialSums = new long[k];
        Thread[] threads = new Thread[k];
        int fragmentSize = numbers.length / k;
        long startParallel = System.nanoTime();
        for (int i = 0; i < k; i++) {
            int end;
            int threadIndex = i;
            int start = i * fragmentSize;
            if(i == k -1){
                end = numbers.length;
            }else
                end = start + fragmentSize;

            threads[i] = new Thread(() ->{
                long localsum = 0;
                for (int j = start; j < end; j++) {
                    localsum += numbers[j];
                }
                partialSums[threadIndex] =localsum;
            });

            threads[i].start();
        }

        for(Thread t : threads) t.join();
        long endParallel = System.nanoTime();
        long parallelSum = 0;

        for (long partial : partialSums) parallelSum += partial;

        System.out.println("Single thread sum: " + singleThreadSum);
        System.out.println("Parallel sum:      " + parallelSum);
        System.out.println("Single thread time: "
                + (endSingle - startSingle) / 1_000_000.0 + " ms");

        System.out.println("Parallel time: "
                + (endParallel - startParallel) / 1_000_000.0 + " ms");
        System.out.println(singleThreadSum == parallelSum);
    }

}
