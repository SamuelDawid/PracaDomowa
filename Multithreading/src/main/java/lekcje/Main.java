package lekcje;

public class Main {
    public static void main(String[] args) {
        MyThread mt1 = new MyThread(5);
        mt1.start();

        new MyThread(10).start();

        Runnable task = () -> System.out
                .println("Runnable running: " +
                        Thread.currentThread().getName());
        new Thread(task).start();

    }
}
