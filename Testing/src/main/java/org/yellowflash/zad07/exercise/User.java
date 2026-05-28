package org.yellowflash.zad07.exercise;

// User.java
public class User {
    private final Long id;
    private final String email;
    private final String phoneNumber;

    public User(Long id, String email, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
}
