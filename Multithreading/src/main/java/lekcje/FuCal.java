package lekcje;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FuCal {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<Integer> future = executor.submit(() -> {
            Thread.sleep(10000);
            return 42;
        });

        System.out.println("Czy gotowe? " + future.isDone());
        System.out.println("Wynik: " + future.get()); // blokuje
        System.out.println("koniec");

        executor.shutdown();
    }
}
/*
Executors.newSingleThreadExecutor() – tworzy pulę z jednym wątkiem. Zadania wykonują się sekwencyjnie, jedno po drugim.
.submit(Callable<T>) – wysyła zadanie zwracające wynik. Zwraca Future<T>. Różnica od Runnable: Callable może zwracać wartość i rzucać wyjątki.
.isDone() – sprawdza, czy zadanie się zakończyło (nie blokuje). Zwraca true/false.
.get() – blokuje bieżący wątek do momentu uzyskania wyniku. Rzuca ExecutionException jeśli zadanie rzuciło wyjątek, oraz InterruptedException.
.shutdown() – zamyka executor (nie przyjmuje nowych zadań, kończy bieżące).
Ograniczenie: get() blokuje.
 */