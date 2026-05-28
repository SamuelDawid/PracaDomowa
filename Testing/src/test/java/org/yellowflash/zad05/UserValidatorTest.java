package org.yellowflash.zad05;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.yellowflash.zad03.StringUtils;

import static org.junit.jupiter.api.Assertions.*;


class UserValidatorTest {
    @Nested
    @DisplayName("Walidacja email")
    class EmailValidationTest{
        @Test
        void validEmailShouldNotThrow() {
            assertDoesNotThrow(() ->UserValidator.validateEmail("jan@example.com"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"janexample.com",""})
        void shouldThrowIllegalArgumentException(String text) {
            assertThrows(IllegalArgumentException.class,() -> UserValidator.validateEmail(text));
        }
        @Test
        void shouldThrowWithCorrectMessage_whenEmailHasNoAtSign(){
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> UserValidator.validateEmail("janexample.com"));
            assertEquals("@",ex.getMessage());
        }
        @Test
        void shouldThrowNullPointerException(){
            assertThrows(NullPointerException.class, () -> UserValidator.validateEmail(null));
        }
    }

}