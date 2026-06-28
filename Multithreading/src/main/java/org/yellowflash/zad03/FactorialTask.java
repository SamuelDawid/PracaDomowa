package org.yellowflash.zad03;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;

public class FactorialTask  implements Callable<BigInteger> {
    Random rng = new Random();
    int num;

    public FactorialTask(int num) {
        this.num = num;
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("How many numbers in the list?");
        int howmany = sc.nextInt();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        Map<Integer,Future<BigInteger>> futureResult = new TreeMap<>();

        for (int i = 1; i <= howmany; i++) {
            FactorialTask task = new FactorialTask(i);
            futureResult.put(i, executorService.submit(task));

        }
        futureResult.forEach( (k,v) -> {
            try {
                System.out.println("Factorial of " + k + " is : " + v.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });


        executorService.shutdown();
    }
    public static BigInteger factorial(int n) {
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }
    @Override
    public BigInteger call() throws Exception {
        BigInteger result =factorial(num);
        Thread.sleep(rng.nextInt(401) + 100);
        return result;
    }
}
