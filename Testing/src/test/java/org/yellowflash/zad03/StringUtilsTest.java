package org.yellowflash.zad03;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringUtilsTest {
    @ParameterizedTest
    @ValueSource(strings = {"kajak","Kajak"," ","","k a j a k",})
    void shouldDetectPalindrome(String text) {
        assertTrue(StringUtils.isPalindrome(text));
    }
        /*

         */
}