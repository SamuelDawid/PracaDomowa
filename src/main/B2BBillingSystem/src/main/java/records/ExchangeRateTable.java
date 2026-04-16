package records;

import Excepcions.NoRateAvailableException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public record ExchangeRateTable(Map<LocalDate, Map<String, BigDecimal>> ratesByDate) {
    static ExchangeRateTable sampleRates() {
        Map<LocalDate, Map<String, BigDecimal>> m = new HashMap<>();
        m.put(LocalDate.of(2024, 10, 15), Map.of(
                "PLN", new BigDecimal("1.0000"),
                "EUR", new BigDecimal("4.3250"),
                "USD", new BigDecimal("3.9800"),
                "JPY", new BigDecimal("0.0265")
        ));
        m.put(LocalDate.of(2024, 10, 16), Map.of(
                "PLN", new BigDecimal("1.0000"),
                "EUR", new BigDecimal("4.3310"),
                "USD", new BigDecimal("3.9875"),
                "JPY", new BigDecimal("0.0264")
        ));
        m.put(LocalDate.of(2024, 10, 17), Map.of(
                "PLN", new BigDecimal("1.0000"),
                "EUR", new BigDecimal("4.3290"),
                "USD", new BigDecimal("3.9820"),
                "JPY", new BigDecimal("0.0263")
        ));

        m.put(LocalDate.of(2024, 10, 18), Map.of(
                "PLN", new BigDecimal("1.0000"),
                "EUR", new BigDecimal("4.3400"),
                "USD", new BigDecimal("3.9900"),
                "JPY", new BigDecimal("0.0262")
        ));

        m.put(LocalDate.of(2024, 10, 19), Map.of(
                "PLN", new BigDecimal("1.0000"),
                "EUR", new BigDecimal("4.3350"),
                "USD", new BigDecimal("3.9850"),
                "JPY", new BigDecimal("0.0261")
        ));

        m.put(LocalDate.of(2024, 10, 20), Map.of(
                "PLN", new BigDecimal("1.0000"),
                "EUR", new BigDecimal("4.3500"),
                "USD", new BigDecimal("4.0000"),
                "JPY", new BigDecimal("0.0260")
        ));

        m.put(LocalDate.of(2024, 10, 21), Map.of(
                "PLN", new BigDecimal("1.0000"),
                "EUR", new BigDecimal("4.3450"),
                "USD", new BigDecimal("3.9950"),
                "JPY", new BigDecimal("0.0259")
        ));
        // ... add ~5 dates
        return new ExchangeRateTable(m);
    }

     static BigDecimal rateOf(String currency, LocalDate onDate){
        TreeMap<LocalDate, Map<String, BigDecimal>> sortedMap = new TreeMap<>(ExchangeRateTable.sampleRates().ratesByDate);
        LocalDate closestDate = Optional.ofNullable(sortedMap.floorKey(onDate)).orElseThrow(() ->new NoRateAvailableException(currency,onDate));
        return Optional.ofNullable(sortedMap.get(closestDate).get(currency)).orElseThrow(() -> new NoRateAvailableException(currency,onDate)) ;
    }

    static Money convertTo(Money source, String targetCurrency, LocalDate onDate){
        BigDecimal targetRate = rateOf(targetCurrency,onDate);
        BigDecimal amount = source.amount();
        BigDecimal currentCurrency = rateOf(source.currency(),onDate);
        if(targetCurrency.equals("PLN"))
            return new Money(amount.multiply(targetRate),targetCurrency);
        else
            return new Money((amount.multiply(currentCurrency.setScale(2, RoundingMode.HALF_EVEN)).divide(targetRate,2,RoundingMode.HALF_EVEN)),targetCurrency);
    }
}
