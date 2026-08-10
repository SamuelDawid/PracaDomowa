package records;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;

public record Invoice(String number,                // e.g. "INV/2024/10/001"
                      String clientName,
                      Money netAmount,
                      BigDecimal vatRate,           // 0.23 for 23%
                      ZonedDateTime issuedAt,
                      LocalDate dueDate,
                      boolean splitPayment) {
    public Invoice {
        Objects.requireNonNull(number);
        Objects.requireNonNull(clientName);
        Objects.requireNonNull(netAmount);
        Objects.requireNonNull(issuedAt);
        Objects.requireNonNull(dueDate);
        if (vatRate.signum() < 0 || vatRate.compareTo(BigDecimal.ONE) > 0)
            throw new IllegalArgumentException("vatRate out of [0, 1]");
    }
}
