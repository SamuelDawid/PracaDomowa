package pl.kurs.movierental.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.kurs.movierental.catalog.Catalog;
import pl.kurs.movierental.domain.Customer;
import pl.kurs.movierental.domain.Movie;
import pl.kurs.movierental.domain.enums.Category;
import pl.kurs.movierental.domain.enums.CustomerStatus;
import pl.kurs.movierental.error.*;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RentalServiceTest {
    RentalService service;
    LocalDate today = LocalDate.of(2026, 1, 1);
    @BeforeEach
    void setUp() {
        Catalog<Movie> movies = new Catalog<>();
        Catalog<Customer> customers = new Catalog<>();

        movies.add(1, new Movie(1, "Shrek", "Adamson", 2001, Category.FAMILY));
        movies.add(2, new Movie(2, "Saw", "Wan", 2004, Category.HORROR));
        movies.add(3, new Movie(3, "Matrix", "Wachowski", 1999, Category.ACTION));
        movies.add(4, new Movie(4, "Titanic", "Cameron", 1997, Category.DRAMA));
        movies.add(5, new Movie(5, "Toy Story", "Lasseter", 1995, Category.KIDS));
        movies.add(6, new Movie(6, "It", "Muschietti", 2017, Category.HORROR));


        customers.add(10, new Customer(10, "Anna", "Nowak", 30, CustomerStatus.ACTIVE));
        customers.add(11, new Customer(11, "Tomek", "Maly", 10, CustomerStatus.ACTIVE));
        customers.add(12, new Customer(12, "Jan", "Blok", 30, CustomerStatus.BLOCKED));
        service = new RentalService(movies, customers);


    }
    @Test
    void serviceThrRentalLimitExceededException() throws RentalException{
            service.rent(10,1,today);
            service.rent(10,2,today);
            service.rent(10,3,today);
        RentalException ex = assertThrows(RentalException.class, () -> service.rent(10,4,today));
        assertThat(ex.error()).isInstanceOf(RentalLimitExceeded.class);
        RentalLimitExceeded r = (RentalLimitExceeded) ex.error();
        assertThat(r.customerId()).isEqualTo(10);
        assertThat(r.active()).isEqualTo(3);
        assertThat(r.limit()).isEqualTo(3);
    }
    @Test
    void serviceThrMovieAlreadyRentedException() throws RentalException {
        service.rent(10,1,today);
        RentalException ex = assertThrows(RentalException.class, () -> service.rent(11,1,today));
        assertThat(ex.error()).isInstanceOf(MovieAlreadyRented.class);
        MovieAlreadyRented m = (MovieAlreadyRented) ex.error();
        assertThat(m.movieId()).isEqualTo(1);
        assertThat(m.title()).isEqualTo("Shrek");
    }
    @Test
    void serviceThrTooYoungForCategoryException(){
        RentalException ex = assertThrows(RentalException.class, () -> service.rent(11,2,today));
        assertThat(ex.error()).isInstanceOf(TooYoungForCategory.class);

        TooYoungForCategory t = (TooYoungForCategory) ex.error();
        assertThat(t.customerAge()).isEqualTo(10);
        assertThat(t.category()).isEqualTo(Category.HORROR);
    }
    @Test
    void serviceThrCustomerBlockedException(){
        RentalException ex = assertThrows(RentalException.class, () -> service.rent(12,1,today));

        assertThat(ex.error()).isInstanceOf(CustomerBlocked.class);

        CustomerBlocked c = (CustomerBlocked) ex.error();
        assertThat(c.customerId()).isEqualTo(12);
        assertThat(c.status()).isEqualTo(CustomerStatus.BLOCKED);
    }
    @Test
    void serviceThrCustomerNotFoundException(){
        RentalException ex = assertThrows(RentalException.class, () -> service.rent(999,1,today));

        assertThat(ex.error()).isInstanceOf(CustomerNotFound.class);

        CustomerNotFound c = (CustomerNotFound) ex.error();
        assertThat(c.customerId()).isEqualTo(999);
//        try{
//           service.rent(999,1,today);
//        }catch (RentalException ex){
//            if (ex.error() instanceof CustomerNotFound(int customerId)){
//                assertThat(customerId).isEqualTo(999);
//            }
//        }
    }
    @Test
    void serviceThrMovieNotFoundException(){
        RentalException ex = assertThrows(RentalException.class, () ->service.rent(10,999,today));

        assertThat(ex.error()).isInstanceOf(MovieNotFound.class);

        MovieNotFound m = (MovieNotFound) ex.error();
        assertThat(m.movieId()).isEqualTo(999);
//        try{
//            service.rent(10,999,today);
//        }catch (RentalException ex){
//            if(ex.error() instanceof MovieNotFound(int movieId)){
//                assertThat(movieId).isEqualTo(999);
//            }
//        }
    }

}