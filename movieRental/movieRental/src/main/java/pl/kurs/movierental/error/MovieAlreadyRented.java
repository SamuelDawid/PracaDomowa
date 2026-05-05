package pl.kurs.movierental.error;

public record MovieAlreadyRented(int movieId, String title) implements RentalError {
    @Override
    public String message() {
        return "Movie with id: %d '%s'  is already rented".formatted(movieId,title);
    }
}
