package org.yellowflash.zad02;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {
    Calculator calculator = new Calculator();

    @Nested
    public class SubtractionTest{

        @Test
        void shouldReturnPositive_whenSubtractingSmallerFromLarger(){
            assertEquals(2,calculator.subtract(5,3));
        }
        @Test
        void shouldReturnNegative_whenSubtractingLargerFromSmaller(){
            assertEquals(-2, calculator.subtract(3, 5));
        }
        @Test
        void shouldReturnSameNumber_whenSubtractingZero(){
            assertEquals(5, calculator.subtract(5, 0));
        }
        @Test
        void shouldReturnNegative_whenSubtractingFromZero(){
            assertEquals(-5, calculator.subtract(0, 5));
        }
        @Test
        void shouldReturnNegative_whenSubtractingTwoNegativeNumbers(){
            assertEquals(-1, calculator.subtract(-3, -2));
        }
    }

    @Test
    void add() {
    }

    @Test
    void subtract() {
    }

    @Test
    void multiply() {
    }

    @Test
    void divide() {
    }
}