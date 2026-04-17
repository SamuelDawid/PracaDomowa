import BillingServices.BillingService;
import BillingServices.ExchangeRateTable;
import BillingServices.InvoiceCalculator;
import records.Invoice;
import records.Money;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Main {


    public static void main(String[] args) {
        Invoice inv1 = new Invoice("INV/2024/09/001", "Jan Kowalski",
                new Money(new BigDecimal("150.00"), "PLN"),
                new BigDecimal("0.23"),
                ZonedDateTime.of(2024, 10, 21, 14, 30, 0, 0, ZoneId.of("Europe/Warsaw")), LocalDate.now());

        Invoice inv2 = new Invoice("INV/2024/12/002", "Anna Nowak",
                new Money(new BigDecimal("33.33"), "PLN"),
                new BigDecimal("0.23"),
                ZonedDateTime.of(2024, 10, 21, 14, 30, 0, 0, ZoneId.of("Europe/Warsaw")), LocalDate.now());

        Invoice inv3 = new Invoice("INV/2024/11/003", "Firma XYZ",
                new Money(new BigDecimal("0.00"), "PLN"),
                new BigDecimal("0.08"),
                ZonedDateTime.of(2024, 10, 21, 14, 30, 0, 0, ZoneId.of("Europe/Warsaw")), LocalDate.now());

        Invoice inv4 = new Invoice("INV/2024/10/004", "Egzotyczny Klient",
                new Money(new BigDecimal("200.50"), "YAP"),
                new BigDecimal("0.05"),
                ZonedDateTime.of(2024, 10, 21, 14, 30, 0, 0, ZoneId.of("Europe/Warsaw")), LocalDate.now());

        List<Invoice> tempList = new ArrayList<>(List.of(inv1,inv2,inv3));
        ExchangeRateTable exchangeRateTable = new ExchangeRateTable(ExchangeRateTable.sampleRates().ratesByDate());
        Map<YearMonth, Money> test = BillingService.monthlyGrossInPln(tempList, exchangeRateTable);
        System.out.println(test);

    }
}
