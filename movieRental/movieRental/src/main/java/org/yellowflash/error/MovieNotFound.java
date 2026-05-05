package org.yellowflash.error;

public record MovieNotFound(int movieId) implements RentalError {
    @Override
    public String message() {
        return "Movie with id %d does not exist".formatted(movieId);
    }
}
