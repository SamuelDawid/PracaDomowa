package lekcje;

public class MyThread extends Thread {

    int age;

    MyThread(int age) {
        this.age = age;
    }

    @Override
    public void run() {
        System.out.println(
                "Hello"
                 + Thread.currentThread().getName()
        );
    }
}
