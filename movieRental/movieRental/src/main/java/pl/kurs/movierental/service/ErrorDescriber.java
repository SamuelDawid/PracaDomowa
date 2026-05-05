package pl.kurs.movierental.service;

import pl.kurs.movierental.error.*;

public final class ErrorDescriber {

    public static String describe(RentalError error){
        return switch(error){
            case MovieNotFound m -> "Movie with id " + m.movieId() + " does not exist";
            case TooYoungForCategory t ->"Customer is " + t.customerAge() + " years old, category " + t.category() + " requires " + t.minimumAge() + "+";
            case CustomerNotFound c -> "Customer with id "+ c.customerId()+" does not exist";
            case CustomerBlocked c -> "Customer with id "+ c.customerId()+" has status" + c.status()+" and cannot rent";
            case MovieAlreadyRented m -> "`Movie "+m.title()+" is already rented";
            case RentalLimitExceeded r ->"Customer with id "+r.customerId()+" already has "+r.active()+" active rentals (limit "+r.limit()+")";
        };
    }
}
