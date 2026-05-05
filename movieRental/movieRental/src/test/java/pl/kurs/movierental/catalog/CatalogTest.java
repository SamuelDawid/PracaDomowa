package pl.kurs.movierental.catalog;

import org.junit.jupiter.api.Test;
import pl.kurs.movierental.domain.Movie;
import pl.kurs.movierental.domain.enums.Category;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogTest {

    @Test
    void createCatalog() {
        Catalog<Movie> movies = new Catalog<>();
        movies.add(1, new Movie(1, "Shrek", "Adamson", 2001, Category.FAMILY));

        assertThat(movies.find(1)).isPresent();
        assertThat(movies.find(99)).isEmpty();
        assertThat(movies.size()).isEqualTo(1);
    }


}