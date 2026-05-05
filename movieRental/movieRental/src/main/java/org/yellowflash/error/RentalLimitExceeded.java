package org.yellowflash.error;

public final class RentalLimitExceeded implements RentalError {
    @Override
    public String message() {
        return "Rental limit has been exceeded";
    }
}
