package org.yellowflash.error;

public sealed interface RentalError permits  MovieNotFound, CustomerNotFound, CustomerBlocked,
        TooYoungForCategory, MovieAlreadyRented, RentalLimitExceeded{
    String message();
}
