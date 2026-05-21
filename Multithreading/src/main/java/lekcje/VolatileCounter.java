package lekcje;

class VolatileCounter {
    static volatile int counter = 0; // volatile NIE czyni ++ atomowym!

    static void incMany() {
        for (int i = 0; i < 1_000_000; i++) {
            counter++; // wciąż gubi inkrementacje
        }
    }
}