package lekcje;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class TryLockTimeoutDemo {
    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        Runnable task = () -> {
            try {
                // czekaj max 1 sekundę na lock
                if (lock.tryLock(1, TimeUnit.SECONDS)) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " pracuje...");
                        Thread.sleep(3000);
                    } finally {
                        lock.unlock();
                    }
                } else {
                    System.out.println(Thread.currentThread().getName() + " timeout – lock niedostępny");
                }
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " przerwany");
            }
        };

        new Thread(task, "Wątek-A").start();
        new Thread(task, "Wątek-B").start();
    }
}