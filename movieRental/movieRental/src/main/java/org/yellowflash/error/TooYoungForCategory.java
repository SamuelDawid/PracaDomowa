package org.yellowflash.error;

public final class TooYoungForCategory implements RentalError {
    @Override
    public String message() {
        return "User is too young to rent movies from this category";
    }
}
