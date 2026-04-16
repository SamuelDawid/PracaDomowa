package Excepcions;

import java.time.LocalDate;

public class NoRateAvailableException extends IllegalStateException {
    public NoRateAvailableException(String currency, LocalDate onDate) {

        System.out.println("no rate for " + currency + " on or before " + onDate);
    }
}
