package org.yellowflash.error;

public final class MovieAlreadyRented implements RentalError {
    @Override
    public String message() {
        return "Movie rented already";
    }
}
