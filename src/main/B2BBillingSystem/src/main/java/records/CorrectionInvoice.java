package records;

import GlobalValues.CorrectionInvoiceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public record CorrectionInvoice(String correctionNumber,
                                String originalNumber,
                                Money amountAdjustment,      // negative = decrease, positive = increase
                                LocalDate issuedOn,
                                String reason,
                                Enum<CorrectionInvoiceType> type) {

    public CorrectionInvoice{
        Objects.requireNonNull(correctionNumber);
        Objects.requireNonNull(originalNumber);
        Objects.requireNonNull(amountAdjustment);
        Objects.requireNonNull(issuedOn);
        Objects.requireNonNull(reason);
    }
}
