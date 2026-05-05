package pl.kurs.movierental.error;

import org.junit.jupiter.api.Test;
import pl.kurs.movierental.domain.enums.Category;
import pl.kurs.movierental.domain.enums.CustomerStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ErrorExceptionsTest {

    @Test
    void movieNotFoundMessage() {
        MovieNotFound error = new MovieNotFound(42);
        assertThat(error.message()).isEqualTo("Movie with id 42 does not exist");
    }
    @Test
    void CustomerBlockedMessage(){
        CustomerBlocked error = new CustomerBlocked(12, CustomerStatus.BLOCKED);
        CustomerBlocked error2 = new CustomerBlocked(12, CustomerStatus.SUSPENDED);
        assertThat(error.message()).isEqualTo("Customer with id %d has status %s and cannot rent".formatted(12, CustomerStatus.BLOCKED));
        assertThat(error2.message()).isEqualTo("Customer with id %d has status %s and cannot rent".formatted(12, CustomerStatus.SUSPENDED));
    }
    @Test
    void CustomerNotFoundMessage(){
        CustomerNotFound error = new CustomerNotFound(22);
        assertThat(error.message()).isEqualTo("Customer with id %d does not exist".formatted(22));
    }
    @Test
    void MovieAlreadyRentedMessage(){
        MovieAlreadyRented error = new MovieAlreadyRented(21,"Batman");
        assertThat(error.message()).isEqualTo("Movie with id: %d '%s'  is already rented".formatted(21,"Batman"));
    }
    @Test
    void TooYoungForCategoryMessage(){
        TooYoungForCategory error = new TooYoungForCategory(10, Category.HORROR,18);
        assertThat(error.message()).isEqualTo("Customer is %d years old, category %s requires %d+".formatted(10, Category.HORROR,18));
    }
    @Test
    void RentalLimitExceededMessage(){
        RentalLimitExceeded error = new RentalLimitExceeded(123,3,3);
        assertThat(error.message()).isEqualTo("Customer with id %d already has %d active rentals (limit %d)".formatted(123,3,3));
    }
}