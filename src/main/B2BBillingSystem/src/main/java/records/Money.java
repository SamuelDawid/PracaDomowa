package records;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount, String currency) {
    public Money {
        Objects.requireNonNull(amount);
        Objects.requireNonNull(currency);
        if (amount.signum() < 0)
            throw new IllegalArgumentException("negative amount");
        if (currency.length() != 3)
            throw new IllegalArgumentException("currency must be 3 letters");
        currency = currency.toUpperCase();
        amount = amount.setScale(2, RoundingMode.HALF_EVEN);
    }
}
