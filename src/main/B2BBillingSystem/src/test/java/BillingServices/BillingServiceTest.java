package BillingServices;

import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.*;

class BillingServiceTest {
    Invoice invA = new Invoice("INV/2024/10/001", "Jan Kowalski",
            new Money(new BigDecimal("100.00"), "PLN"),
            new BigDecimal("0.23"),
            ZonedDateTime.of(2024, 10, 15, 10, 0, 0, 0, ZoneId.of("Europe/Warsaw")),
            LocalDate.of(2024, 10, 15));

    Invoice invB = new Invoice("INV/2024/10/002", "Anna Nowak",
            new Money(new BigDecimal("200.00"), "PLN"),
            new BigDecimal("0.08"),
            ZonedDateTime.of(2024, 10, 20, 12, 0, 0, 0, ZoneId.of("Europe/Warsaw")),
            LocalDate.of(2024, 10, 20));

    Invoice invC = new Invoice("INV/2024/11/001", "Firma XYZ",
            new Money(new BigDecimal("50.00"), "PLN"),
            new BigDecimal("0.23"),
            ZonedDateTime.of(2024, 11, 5, 9, 0, 0, 0, ZoneId.of("Europe/Warsaw")),
            LocalDate.of(2024, 11, 5));
    List<Invoice> testInv = new ArrayList<>(List.of(invA,invB,invC));
    ExchangeRateTable rateTable = new ExchangeRateTable(ExchangeRateTable.sampleRates().ratesByDate());
    @Test
    void monthlyGrossEmptyList() {
        Map<YearMonth, Money> result = BillingService.monthlyGrossInPln(
                List.of(), rateTable);
        assertEquals(0,result.size());
    }
    @Test
    void monthlyGrossOneValueList() {
        Map<YearMonth, Money> result = BillingService.monthlyGrossInPln(
                List.of(invA), rateTable);
        assertEquals(1,result.size());
        assertEquals(new BigDecimal("123.00"),result.get(YearMonth.of(2024, 10)).amount());
    }
    @Test
    void monthlyGrossShouldGroupAndSumByMonth() {
        Map<YearMonth, Money> result = BillingService.monthlyGrossInPln(
                List.of(invA, invB, invC), rateTable);
        assertEquals(2, result.size());
        assertEquals(0, new BigDecimal("339.00").compareTo(
                result.get(YearMonth.of(2024, 10)).amount()));
        assertEquals(0, new BigDecimal("61.50").compareTo(
                result.get(YearMonth.of(2024, 11)).amount()));
    }

    @Test
    void lateInterestTenDays() {
        LocalDate paidOn = LocalDate.of(2024, 10, 25);
        BigDecimal annualRate = new BigDecimal("0.115");
        // expected: -0.39 PLN
        Money result = new Money(new BigDecimal("0.39"),"PLN");
        assertEquals(result,BillingService.lateInterest(invA,paidOn,annualRate));
    }
}