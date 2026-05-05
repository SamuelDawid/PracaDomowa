package org.yellowflash.error;

import org.yellowflash.domain.enums.Category;

public record TooYoungForCategory(int customerAge, Category category, int minimumAge) implements RentalError {
    @Override
    public String message() {
        return "Customer is %d years old, category %s requires %d+".formatted(customerAge, category, minimumAge);
    }
}
