package lekcje;

public class RaceDemo {
    static int counter = 0;

    private static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(RaceDemo::incrementMany);
        Thread t2 = new Thread(RaceDemo::incrementMany);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("Counter = " + counter); // często < 2_000_000
    }

    static void incrementMany() {
        for (int i = 0; i < 1_000_000; i++) {
//            counter++; // nieatomowe!
            safeIncrement();
        }
    }

    static synchronized void safeIncrement() {
//        synchronized (lock) {
            counter++;
//        }
    }


}
