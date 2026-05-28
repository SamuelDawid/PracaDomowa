package org.yellowflash.zad06;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    @ParameterizedTest
    @DisplayName("shouldValidatePasswordStrength")
    @CsvSource({
            "Password1, true",
            "pass,      false",
            "password,  false",
            "password1, false",
            "PASSWORD1, true",
            "Pass1234,  true",
            "Aa1,       false"
    })
    void isValid(String password,boolean expected) {
        assertEquals(expected,PasswordValidator.isValid(password));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should Validate Empty or Null password")
    @ValueSource(strings = {" ","\t","  "})
    void shouldRejectnullOrEmptyPassword(String password){
        assertFalse(PasswordValidator.isValid(password));
    }

    @ParameterizedTest
    @DisplayName("Should Validate Three passwords")
    @ValueSource(strings = {"Password1","NewPassword123","ThisIsMyNewPass3321!"})
    void shouldAcceptThreeCorrectPasswords(String password){
        assertTrue(PasswordValidator.isValid(password));
    }
}