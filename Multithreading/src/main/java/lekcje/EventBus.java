package lekcje;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {
    private final List<String> listeners = new CopyOnWriteArrayList<>();

    public void register(String listener) {
        listeners.add(listener);    // tworzy kopię tablicy
    }

    public void notifyAll(String event) {
        // bezpieczna iteracja – nawet jeśli ktoś doda listenera w trakcie
        for (String listener : listeners) {
            System.out.println("Powiadomienie " + listener + ": " + event);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        EventBus bus = new EventBus();

        // Wątek dodający listenerów
        Thread adder = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                bus.register("Listener-" + i);
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }
        });

        // Wątek wysyłający eventy (iteruje po liście)
        Thread notifier = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                bus.notifyAll("Event-" + i);
                try { Thread.sleep(50); } catch (InterruptedException ignored) {}
            }
        });

        adder.start();
        notifier.start();
        adder.join();
        notifier.join();
    }
}