package lekcje;

public class MailboxExample {

    static class Mailbox {

//        Object lock;
        private String msg;

        public synchronized void put(String m) {
            while (msg != null) { // skrzynka pełna — czekaj
                try {
                    wait();
                } catch (InterruptedException ignored) {}
            }
            msg = m;
            System.out.println("Wysłano wiadomość: " + msg);
            notifyAll(); // powiadom czekające wątki
        }

        public synchronized String take() {
            while (msg == null) { // skrzynka pusta — czekaj
                try {
                    wait();
                } catch (InterruptedException ignored) {}
            }
            String out = msg;
            msg = null;

            System.out.println("Odebrano wiadomość: " + out);
            notifyAll(); // powiadom producenta
            return out;
        }
    }

    public static void main(String[] args) {
        Mailbox mailbox = new Mailbox();

        Thread producer = new Thread(() -> {
            String[] messages = {"Cześć", "Jak się masz?", "To działa!", "Koniec"};
            for (String m : messages) {
                mailbox.put(m);
                try {
                    Thread.sleep(1000); // symulacja czasu produkcji
                } catch (InterruptedException ignored) {}
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 4; i++) {
                mailbox.take();
                try {
                    Thread.sleep(1500); // symulacja przetwarzania
                } catch (InterruptedException ignored) {}
            }
        });

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException ignored) {}
    }
}