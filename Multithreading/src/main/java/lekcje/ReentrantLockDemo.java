package lekcje;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo {
    private static int counter = 0;
    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(ReentrantLockDemo::incrementMany);
        Thread t2 = new Thread(ReentrantLockDemo::incrementMany);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("Counter = " + counter); // 2_000_000
    }

    static void incrementMany() {
        for (int i = 0; i < 1_000_000; i++) {
            lock.lock();          // wejście do sekcji krytycznej
            try {
                counter++;
            } finally {
                lock.unlock();    // ZAWSZE w finally!
            }
        }
    }
}