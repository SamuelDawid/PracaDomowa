package lekcje;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WatkiNowoczsnie {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(3);

        for (int i = 1; i <= 5; i++) {
            int taskId = i;
            pool.submit(() -> {
                System.out.println("Task " + taskId + " run by " + Thread.currentThread().getName());
            });
        }

        pool.shutdown();
    }
}
/*
Executors.newFixedThreadPool(3) – tworzy pulę z dokładnie 3 wątkami. Zadania czekają w kolejce, jeśli wszystkie wątki są zajęte.
.submit(Runnable) – wysyła zadanie do puli do wykonania. Zwraca Future<?> (tu ignorowany).
.shutdown() – sygnalizuje puli, że nie przyjmuje nowych zadań. Czeka na dokończenie bieżących, potem kończy wątki. Bez tego programu się nie zakończy!
Thread.currentThread().getName() – zwraca nazwę wątku z puli (np. "pool-1-thread-1").
 */