package lekcje;

import java.util.concurrent.CompletableFuture;

public class CallFu {
    public static void main(String[] args) {
        CompletableFuture.supplyAsync(() -> "Hello")
                .thenApply(s -> s + " World")
                .thenAccept(System.out::println);

        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> 10);
        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> 20);
        CompletableFuture<Integer> sum = f1
                .thenCombine(f2,
                        Integer::sum);



        System.out.println("Wynik: " + sum.join());
    }
}

/*
CompletableFuture.supplyAsync(() -> ...) – uruchamia zadanie asynchronicznie (domyślnie w ForkJoinPool.commonPool()) i zwraca CompletableFuture<T> z wynikiem.
.thenApply(Function) – po zakończeniu poprzedniego etapu, transformuje wynik (jak map w streamach). Zwraca nowy CompletableFuture.
.thenAccept(Consumer) – konsumuje wynik końcowy bez zwracania nowej wartości. Używamy do efektów ubocznych (np. wypisanie na ekran).
 */

/*
.thenCombine(other, BiFunction) – czeka na zakończenie obu CompletableFuture i łączy wyniki za pomocą podanej funkcji (tu Integer::sum – dodaje dwie liczby).
.join() (na CompletableFuture) – blokuje do uzyskania wyniku, podobnie jak Future.get(), ale rzuca unchecked CompletionException zamiast checked wyjątków. Wygodniejszy w lambdach.
 */