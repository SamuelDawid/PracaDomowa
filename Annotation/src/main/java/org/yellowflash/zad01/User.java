package org.yellowflash.zad01;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class User {
    @PasswordValidation(minLength = 11,requireSpecialChar = true)
    String password;


}
