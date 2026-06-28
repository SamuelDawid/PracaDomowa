package lekcje;

import java.util.concurrent.locks.ReentrantLock;

public class TryLockDemo {
    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        Runnable task = () -> {
            if (lock.tryLock()) {          // nie blokuje – zwraca true/false
                try {
                    System.out.println(Thread.currentThread().getName() + " uzyskał lock");
                    Thread.sleep(2000);    // symulacja pracy
                } catch (InterruptedException ignored) {
                } finally {
                    lock.unlock();
                }
            } else {
                System.out.println(Thread.currentThread().getName() + " NIE uzyskał locka – robi coś innego");
            }
        };

        new Thread(task, "Wątek-A").start();
        new Thread(task, "Wątek-B").start();
    }
}