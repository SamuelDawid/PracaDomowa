package org.yellowflash.error;

public final class MovieNotFound implements RentalError {
    @Override
    public String message() {
        return "Movie not found";
    }
}
