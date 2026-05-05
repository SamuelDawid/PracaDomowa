package pl.kurs.movierental.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.kurs.movierental.domain.Movie;
import pl.kurs.movierental.domain.enums.Category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CatalogTest {
    Catalog<Movie> catalog;

    @BeforeEach
    void setUp() {
        catalog = new Catalog<>();
        catalog.add(1, new Movie(1, "Shrek", "Adamson", 2001, Category.FAMILY));
    }

    @Test
    void createCatalogCheckFindAndSize() {
        assertThat(catalog.find(1)).isPresent();
        assertThat(catalog.find(99)).isEmpty();
        assertThat(catalog.size()).isEqualTo(1);
    }
    @Test
    void CatalogContains(){
        assertThat(catalog.contains(1)).isTrue();
        assertThat(catalog.contains(99)).isFalse();
    }
    // Validate
    @Test
    void addThrIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class,
                () -> catalog.add(1, new Movie(1, "Matrix", "Wachowski", 1999, Category.ACTION)));
    }

    @Test
    void addNull(){
        assertThrows(NullPointerException.class,
                () -> catalog.add(2, null));
    }

}