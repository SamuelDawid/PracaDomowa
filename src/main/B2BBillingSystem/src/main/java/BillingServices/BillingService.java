package BillingServices;

import records.Invoice;
import records.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static StaticFinal.StaticFinalValues.*;

public class BillingService {
    public static Map<YearMonth, Money> monthlyGrossInPln(List<Invoice> invoices, ExchangeRateTable rates){

        Map<YearMonth,Money> result = new TreeMap<>();
        for (Invoice inv : invoices){
            result.merge(YearMonth.from(inv.issuedAt()),
                    rates.convertTo(InvoiceCalculator.grossAmount(inv),BStargetCurrency,inv.issuedAt().toLocalDate()),
                    (valOne,valTwo) -> new Money(valOne.amount().add(valTwo.amount()),valOne.currency())
                    );
        }

        return result;
    }
    static Money lateInterest(Invoice invoice, LocalDate paidOn, BigDecimal annualRate){
        if(ChronoUnit.DAYS.between(invoice.dueDate(),paidOn) <= 0) return new Money(BigDecimal.ZERO,invoice.netAmount().currency());
        long daysOverdue = ChronoUnit.DAYS.between(invoice.dueDate(),paidOn);
        BigDecimal interestAmount = (InvoiceCalculator.grossAmount(invoice).amount().multiply(annualRate)
                .multiply(new BigDecimal(daysOverdue)))
                .divide(days,2,RoundingMode.HALF_EVEN);

        return new Money(interestAmount,invoice.netAmount().currency());
    }
    static String renderMonthlyReport(YearMonth month, List<Invoice> invoices, ExchangeRateTable rates, ZoneId displayZone){

    }
}
