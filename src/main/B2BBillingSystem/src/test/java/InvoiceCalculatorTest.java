import Excepcions.NotAllInvoicesShareSameCurrency;
import records.Invoice;
import records.Money;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceCalculatorTest {
    // TEST INVOICES //
    Invoice inv1 = new Invoice("INV/2024/01/001", "Jan Kowalski",
            new Money(new BigDecimal("150.00"), "PLN"),
            new BigDecimal("0.23"),
            ZonedDateTime.now(), LocalDate.now());

    Invoice inv2 = new Invoice("INV/2024/01/002", "Anna Nowak",
            new Money(new BigDecimal("33.33"), "PLN"),
            new BigDecimal("0.23"),
            ZonedDateTime.now(), LocalDate.now());

    Invoice inv3 = new Invoice("INV/2024/01/003", "Firma XYZ",
            new Money(new BigDecimal("0.00"), "PLN"),
            new BigDecimal("0.08"),
            ZonedDateTime.now(), LocalDate.now());

    Invoice inv4 = new Invoice("INV/2024/01/004", "Egzotyczny Klient",
            new Money(new BigDecimal("200.50"), "YAP"),
            new BigDecimal("0.05"),
            ZonedDateTime.now(), LocalDate.now());

    @org.junit.jupiter.api.Test
    void grossAmount() {
        BigDecimal net = new BigDecimal("100.00");
        BigDecimal vat = new BigDecimal("0.23");
        Invoice test =new Invoice("INV/001", "Test Client", new Money(net,"PLN"), vat, ZonedDateTime.now(), LocalDate.now());
        Money result = new Money(new BigDecimal("123.00"),"PLN");
        assertEquals(result,InvoiceCalculator.grossAmount(test));
    }
    @org.junit.jupiter.api.Test
    void grossAmountZero() {
        BigDecimal net = new BigDecimal("0.00");
        BigDecimal vat = new BigDecimal("0.23");
        Invoice test =new Invoice("INV/001", "Test Client", new Money(net,"PLN"), vat, ZonedDateTime.now(), LocalDate.now());
        Money result = new Money(new BigDecimal("0.00"),"PLN");
        assertEquals(result,InvoiceCalculator.grossAmount(test));
    }
    @org.junit.jupiter.api.Test
    void grossAmountHalfEven() {

//        BigDecimal net = new BigDecimal("33.33");
//        BigDecimal vat = new BigDecimal("0.23");
        BigDecimal net = new BigDecimal("10");
        BigDecimal vat = new BigDecimal("0.115");
        Invoice test =new Invoice("INV/001", "Test Client", new Money(net,"PLN"), vat, ZonedDateTime.now(), LocalDate.now());
        //Money result = new Money(new BigDecimal("41.00"),"PLN"); // returns 41.
        Money result = new Money(new BigDecimal("11.15"),"PLN");
        assertEquals(result,InvoiceCalculator.grossAmount(test));
    }
    @org.junit.jupiter.api.Test
    void vatAmount() {
        BigDecimal net = new BigDecimal("10");
        BigDecimal vat = new BigDecimal("0.23");
        Invoice test =new Invoice("INV/001", "Test Client", new Money(net,"PLN"), vat, ZonedDateTime.now(), LocalDate.now());
        Money result = new Money(new BigDecimal("0.23"),"PLN");

        assertEquals(result,InvoiceCalculator.vatAmount(test));
    }
    @org.junit.jupiter.api.Test
    void totalNet(){
        Money result = new Money(new BigDecimal("183.33"),"PLN");
        assertEquals(result,InvoiceCalculator.totalNet(inv1,inv2,inv3));
    }
    @org.junit.jupiter.api.Test
    void shouldThrowNotAllInvoicesShareSameCurrency() {
        assertThrows(NotAllInvoicesShareSameCurrency.class,() ->{
            InvoiceCalculator.totalNet(inv1,inv2,inv3,inv4);
        });

    }
}