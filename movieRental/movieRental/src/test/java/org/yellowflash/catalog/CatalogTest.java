package org.yellowflash.catalog;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yellowflash.domain.Movie;
import org.yellowflash.domain.enums.Category;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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