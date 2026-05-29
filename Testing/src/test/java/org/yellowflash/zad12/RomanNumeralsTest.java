package org.yellowflash.zad12;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class RomanNumeralsTest {
    // red -> green
    @Test
    void shouldReturnI_whenInputIsOne() {
        assertEquals("I", RomanNumerals.toRoman(1));
    }
    @ParameterizedTest
    @CsvSource({
            "1,    I",
            "2,    II",
            "3,    III",
            "4,    IV",
            "5,    V",
            "9,    IX",
            "10,   X",
            "40,   XL",
            "58,   LVIII",
            "1994, MCMXCIV",
            "3999, MMMCMXCIX"
    })
    void shouldConvertToRoman(int input, String expected) {
        assertEquals(expected, RomanNumerals.toRoman(input));
    }
    @Test
    void shouldThrowForZero() {
        assertThrows(IllegalArgumentException.class, () -> RomanNumerals.toRoman(0));
    }

    @Test
    void shouldThrowForNegative() {
        assertThrows(IllegalArgumentException.class, () -> RomanNumerals.toRoman(-5));
    }
}