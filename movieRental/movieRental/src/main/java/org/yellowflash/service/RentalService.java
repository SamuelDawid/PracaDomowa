package org.yellowflash.service;

import org.apache.commons.lang3.Validate;
import org.yellowflash.catalog.Catalog;
import org.yellowflash.domain.Customer;
import org.yellowflash.domain.Movie;
import org.yellowflash.domain.Rental;
import org.yellowflash.domain.enums.CustomerStatus;
import org.yellowflash.error.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RentalService {
    public static final int ACTIVE_RENTALS_LIMIT = 3;
    public static final int OVERDUE_FINE_PER_DAY_PLN = 1;

    final Catalog<Movie> movies;
    final Catalog<Customer> customers;
    final List<Rental> history;
    int nextRentalId = 1;
    public RentalService(Catalog<Movie> movies, Catalog<Customer> customers) {
        this.movies = movies;
        this.customers = customers;
        this.history = new ArrayList<>();
    }
    Rental rent(int customerId, int movieId, LocalDate today) throws RentalException {
        int active = activeRentalsForCustomer(customerId);
        Customer customer = customers.find(customerId).orElseThrow( () -> new RentalException(new CustomerNotFound(customerId)));
        Movie movie = movies.find(movieId).orElseThrow( () -> new RentalException(new MovieNotFound(movieId)));
        if(customer.status() != CustomerStatus.ACTIVE) throw new RentalException(new CustomerBlocked(customerId,customer.status()));
        if((customer.age() < movie.category().minimumAge())) throw new RentalException(new TooYoungForCategory(customer.age(),movie.category(),movie.category().minimumAge()));
        if(active >= ACTIVE_RENTALS_LIMIT) throw new RentalException(new RentalLimitExceeded(customerId,active,ACTIVE_RENTALS_LIMIT));
        if(isMovieRented(movieId)) throw new RentalException(new MovieAlreadyRented(movieId,movie.title()));
        Rental newRental =new Rental(nextRentalId++,movieId,customerId,today,
                today.plusDays(movie.category().rentalDays()),
                Optional.empty());
        history.add(newRental);
        return newRental;
    }

    private int activeRentalsForCustomer(int customerId){
        int  activeRentals = 0;
        for(Rental rental : history)
            if (rental.customerId() == customerId && rental.actualReturnDate().isEmpty()) {
                activeRentals++;
            }

        return activeRentals;
    }
    private boolean isMovieRented(int movieId) {
        for (Rental rental : history) {
            if (rental.movieId() == movieId && rental.actualReturnDate().isEmpty()) {
                return true;
            }
        }
        return false;
    }
    public int returnMovie(int rentalId,LocalDate today){
        int index = -1;
        Rental rentalToReturn = null;
        for (int i = 0; i < history.size(); i++) {
            if(history.get(i).id() == rentalId){
                rentalToReturn = history.get(i);
                index = i;
            }
        }
        if(rentalToReturn == null)throw new RentalStateException("Rental " +rentalId+"not found");
        if(rentalToReturn.actualReturnDate().isPresent()) throw new RentalStateException("Rental "+rentalId+" already returned");
        history.set(index,rentalToReturn.withReturn(today));
        int lateDays = Math.toIntExact(ChronoUnit.DAYS.between(rentalToReturn.plannedReturnDate(), today));
        return Math.max(0,lateDays) * OVERDUE_FINE_PER_DAY_PLN;

    }
    private Optional<Rental> findRental(int rentalId){
        return history.stream().filter(rental -> rental.id() == rentalId).findAny();
    }
}
