package pl.kurs.movierental.error;

import pl.kurs.movierental.domain.enums.CustomerStatus;

public record CustomerBlocked(int customerId, CustomerStatus status) implements RentalError {
    @Override
    public String message() {
        return "Customer with id %d has status %s and cannot rent".formatted(customerId, status);
    }
}
