package org.yellowflash.error;

public final class CustomerBlocked implements RentalError {
    @Override
    public String message() {
        return "User account block, please contact custer support";
    }
}
