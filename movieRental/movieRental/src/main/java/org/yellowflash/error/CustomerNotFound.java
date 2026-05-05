package org.yellowflash.error;

public final class CustomerNotFound implements RentalError {
    @Override
    public String message() {
        return "Custer not found";
    }
}
