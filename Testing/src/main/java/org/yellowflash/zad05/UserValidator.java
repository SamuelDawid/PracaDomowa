package org.yellowflash.zad05;


public class UserValidator {

    public static void validateEmail(String email){
        if(email == null) throw new NullPointerException("Email nie może być null");
        if(email.isBlank()) throw new IllegalArgumentException("pusty");
        if(!email.contains("@")) throw new IllegalArgumentException("@");
    }
}
