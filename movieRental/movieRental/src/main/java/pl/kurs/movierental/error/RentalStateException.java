package pl.kurs.movierental.error;

public class RentalStateException extends RuntimeException {
    public RentalStateException(String message) {
        super(message);
    }
}
