import records.Invoice;
import records.Money;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Main {


    public static void main(String[] args) {
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
        System.out.println(InvoiceCalculator.renderSimpleReport(ZoneId.of("Europe/Warsaw"),inv1,inv2,inv3));

    }
}
