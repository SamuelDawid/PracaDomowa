package org.yellowflash.error;

public record MovieAlreadyRented(int movieId, String title) implements RentalError {
    @Override
    public String message() {
        return "Movie '%s' is already rented".formatted(title);
    }
}
