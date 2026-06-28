package lekcje;

import java.util.concurrent.locks.ReentrantLock;

public class InterruptibleLockDemo {
    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        // Wątek A trzyma lock przez 5 sekund
        Thread tA = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("A: trzymam lock");
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            } finally {
                lock.unlock();
            }
        });

        // Wątek B próbuje uzyskać lock, ale można go przerwać
        Thread tB = new Thread(() -> {
            try {
                System.out.println("B: czekam na lock...");
                lock.lockInterruptibly();   // można przerwać!
                try {
                    System.out.println("B: uzyskałem lock");
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                System.out.println("B: przerwano czekanie na lock!");
            }
        });

        tA.start();
        Thread.sleep(100); // daj A czas na zajęcie locka
        tB.start();
        Thread.sleep(1000);
        tB.interrupt();    // przerywamy B po 1 sekundzie
        tA.join();
        tB.join();
    }
}