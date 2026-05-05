package pl.kurs.movierental.error;

public record RentalLimitExceeded(int customerId, int active, int limit) implements RentalError {
    @Override
    public String message() {
        return "Customer with id %d already has %d active rentals (limit %d)".formatted(customerId, active, limit);
    }
}
