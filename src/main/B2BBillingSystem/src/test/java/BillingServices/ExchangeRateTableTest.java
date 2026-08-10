package BillingServices;

import Excepcions.NoRateAvailableException;
import org.junit.jupiter.api.Test;
import records.Money;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRateTableTest {
    ExchangeRateTable rateTable = new ExchangeRateTable(ExchangeRateTable.sampleRates().ratesByDate());
    void rateOf() {
        BigDecimal result = new BigDecimal("4.3450");
        assertEquals(result,rateTable.rateOf("EUR", LocalDate.of(2024,10,21)));
    }
    @Test
    void rateOfThrowsNoRateAvailableException(){
        assertThrows(NoRateAvailableException.class,()->
                rateTable.rateOf("GBP",LocalDate.of(2024,10,21))
        );
    }
    @Test
    void convertToPLNToPLN() {
        Money expected = new Money(new BigDecimal("100.00"),"PLN");
        Money source = new Money(new BigDecimal("100"),"PLN");
        assertEquals(expected,
                rateTable.convertTo(source,"PLN",LocalDate.of(2024,10,21))
        );
    }
    @Test
    void convertToPLNToUSD() {
        Money expected = new Money(new BigDecimal("25"),"USD");
        Money source = new Money(new BigDecimal("100"),"PLN");
        assertEquals(expected,
                rateTable.convertTo(source,"USD",LocalDate.of(2024,10,20))
        );
    }
    @Test
    void convertToEURToUSD() {
        Money expected = new Money(new BigDecimal("54.38"),"USD");
        Money source = new Money(new BigDecimal("50"),"EUR");
        assertEquals(expected,
                rateTable.convertTo(source,"USD",LocalDate.of(2024,10,20))
        );
    }
    @Test
    void convertToThrowsNoRateAvailableException(){
        Money source = new Money(new BigDecimal("100.00"), "YAP");
        assertThrows(NoRateAvailableException.class,()->
                rateTable.convertTo(source,"YAP",LocalDate.of(2024,10,20))
        );
    }

}