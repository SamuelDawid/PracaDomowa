package org.yellowflash.zad07.exercise;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("Użytkownik nie istnieje: " + userId);
    }
}
