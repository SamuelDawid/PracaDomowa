package records;

import Excepcions.NoRateAvailableException;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExchangeRateTableTest {

    @Test
    void rateOf() {
        BigDecimal result = new BigDecimal("4.3450");
        assertEquals(result,ExchangeRateTable.rateOf("EUR", LocalDate.of(2024,10,21)));
    }
    @Test
    void rateOfThrowsNoRateAvailableException(){
        assertThrows(NoRateAvailableException.class,()->
                ExchangeRateTable.rateOf("GBP",LocalDate.of(2024,10,21))
                );
    }
    @Test
    void convertToPLNToPLN() {
        Money expected = new Money(new BigDecimal("100.00"),"PLN");
        Money source = new Money(new BigDecimal("100"),"PLN");
        assertEquals(expected,
                ExchangeRateTable.convertTo(source,"PLN",LocalDate.of(2024,10,21))
                );
    }
    @Test
    void convertToPLNToUSD() {
        Money expected = new Money(new BigDecimal("25"),"USD");
        Money source = new Money(new BigDecimal("100"),"PLN");
        assertEquals(expected,
                ExchangeRateTable.convertTo(source,"USD",LocalDate.of(2024,10,20))
        );
    }
    @Test
    void convertToEURToUSD() {
        Money expected = new Money(new BigDecimal("54.38"),"USD");
        Money source = new Money(new BigDecimal("50"),"EUR");
        assertEquals(expected,
                ExchangeRateTable.convertTo(source,"USD",LocalDate.of(2024,10,20))
        );
    }
    @Test
    void convertToThrowsNoRateAvailableException(){
        Money source = new Money(new BigDecimal("100.00"), "YAP");
        assertThrows(NoRateAvailableException.class,()->
                ExchangeRateTable.convertTo(source,"YAP",LocalDate.of(2024,10,20))
        );
    }
}