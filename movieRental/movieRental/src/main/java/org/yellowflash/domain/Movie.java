package org.yellowflash.domain;


import org.apache.commons.lang3.Validate;
import org.yellowflash.domain.enums.Category;

public record Movie(int id, String title, String director, int year, Category category) {

    public Movie {
        Validate.notBlank(title, "Must provide title");
        Validate.notBlank(director, "Must provide director");
        Validate.notNull(category, "Must provide category");
        Validate.isTrue(year >= 1888, "First movie was made in 1888");
    }
}
