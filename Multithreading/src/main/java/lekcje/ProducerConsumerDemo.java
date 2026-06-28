package lekcje;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ProducerConsumerDemo {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(5); // max 5 elementów

        // Producent
        Thread producer = new Thread(() -> {
            String[] items = {"Jabłko", "Banan", "Cytryna", "Daktyl", "Elderberry",
                    "Figa", "Gruszka", "KONIEC"};
            for (String item : items) {
                try {
                    queue.put(item);  // blokuje jeśli kolejka pełna
                    System.out.println("Wyprodukowano: " + item
                            + " (w kolejce: " + queue.size() + ")");
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // Konsument
        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    String item = queue.take();  // blokuje jeśli kolejka pusta
                    if ("KONIEC".equals(item)) break;
                    System.out.println("  Skonsumowano: " + item);
                    Thread.sleep(500); // konsument wolniejszy od producenta
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        System.out.println("Koniec programu");
    }
}

/*
new ArrayBlockingQueue<>(5) – tworzy blokującą kolejkę o stałej pojemności 5. Gdy kolejka jest pełna, put() blokuje.
.put(element) – dodaje element do kolejki. Blokuje wątek producenta, jeśli kolejka jest pełna. Odpowiednik wait/notify z sekcji 3.5, ale bez ręcznej synchronizacji.
.take() – pobiera i usuwa element z kolejki. Blokuje wątek konsumenta, jeśli kolejka jest pusta.
Thread.currentThread().interrupt() – przywraca flagę przerwania po złapaniu InterruptedException (dobra praktyka).
 */