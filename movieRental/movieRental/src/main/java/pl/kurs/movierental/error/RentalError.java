package pl.kurs.movierental.error;

public sealed interface RentalError permits  MovieNotFound, CustomerNotFound, CustomerBlocked,
        TooYoungForCategory, MovieAlreadyRented, RentalLimitExceeded{
    String message();
}
