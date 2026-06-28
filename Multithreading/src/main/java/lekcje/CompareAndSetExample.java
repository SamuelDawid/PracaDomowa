package lekcje;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CompareAndSetExample {

    public static void main(String[] args) {
        AtomicInteger number = new AtomicInteger(10);

        boolean result = number.compareAndSet(10, 20);

        System.out.println("Czy zmieniono? " + result);
        System.out.println("Aktualna wartość: " + number.get());
//        AtomicReference
    }
    /*
    AtomicInteger
AtomicLong
AtomicBoolean
AtomicReference
     */
}