public class TicketBookingSystem {

    private int availableTickets = 10;

    public void bookTicket(String customerName) {
        int current = availableTickets;    // BREAKPOINT (Suspend: Thread)

        if (current > 0) {
            // Symulacja opóźnienia przetwarzania
            try { Thread.sleep(1); } catch (InterruptedException e) { }
            availableTickets = current - 1;
            System.out.println(customerName + " zarezerwował bilet. Pozostało: " + availableTickets);
        } else {
            System.out.println(customerName + " -- BRAK biletów!");
        }
    }

    public int getAvailableTickets() {
        return availableTickets;
    }

    public static void main(String[] args) throws InterruptedException {
        TicketBookingSystem system = new TicketBookingSystem();

        System.out.println("Start: " + system.getAvailableTickets() + " biletów");

        Thread[] threads = new Thread[5];
        for (int t = 0; t < 5; t++) {
            final String name = "Klient-" + (t + 1);
            threads[t] = new Thread(() -> {
                for (int i = 0; i < 3; i++) {
                    system.bookTicket(name);
                }
            }, "Watek-" + (t + 1));
        }

        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("\nKoniec. Bilety pozostałe: " + system.getAvailableTickets());
        System.out.println("Oczekiwano: >= 0 biletów (nie ujemna liczba!)");
        if (system.getAvailableTickets() < 0) {
            System.out.println("BŁĄD: Zarezerwowano więcej biletów niż było dostępnych!");
        }
    }
    /*
1. Czy wynik programu był identyczny przy każdym uruchomieniu (bez debuggera)? Dlaczego?
2. Co to jest race condition? Opisz własnymi słowami na podstawie tego co zaobserwowałeś.
3. Na czym polega problem: dwa wątki odczytują `availableTickets` -- co się dzieje gdy oba odczytają tę samą wartość?
4. Jaka jest różnica między Suspend Policy "All" i "Thread"?
5. Jak można naprawić ten problem? (Podpowiedź: `synchronized`)
     */
}
