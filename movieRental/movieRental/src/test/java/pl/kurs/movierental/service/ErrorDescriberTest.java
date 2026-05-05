package pl.kurs.movierental.service;

import org.junit.jupiter.api.Test;
import pl.kurs.movierental.error.MovieNotFound;
import pl.kurs.movierental.error.RentalError;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ErrorDescriberTest {
    @Test
    void describerMovieNotFound() {
        RentalError error = new MovieNotFound(42);
        assertThat(ErrorDescriber.describe(error))
                .isEqualTo("Movie with id 42 does not exist");
    }
}