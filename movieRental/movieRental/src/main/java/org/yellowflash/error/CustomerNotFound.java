package org.yellowflash.error;

public record CustomerNotFound(int customerId) implements RentalError {
    @Override
    public String message() {
        return "Customer with id %d does not exist".formatted(customerId);
    }
}
