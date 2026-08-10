package BillingServices;

import Excepcions.NoInvoicesFoundException;
import Excepcions.NotAllInvoicesShareSameCurrency;
import Excepcions.ProcessRefundException;
import Excepcions.SplitNotAvailableException;
import GlobalValues.CorrectionInvoiceType;
import records.CorrectionInvoice;
import records.Invoice;
import records.Money;
import records.SplitPaymentBreakdown;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class InvoiceCalculator {
    static Money grossAmount(Invoice i){
        BigDecimal net = i.netAmount().amount();
        BigDecimal vat = i.vatRate();
        BigDecimal gross = new BigDecimal(
                String.valueOf(net.add(net.multiply(vat))));

        return new Money(gross.setScale(2, RoundingMode.HALF_EVEN),i.netAmount().currency());
    }

    static Money vatAmount(Invoice i){
        return new Money(i.vatRate().setScale(2, RoundingMode.HALF_EVEN),i.netAmount().currency());
    }
    static Money totalNet(Invoice... invoices){
        if(invoices.length < 1) throw new NoInvoicesFoundException();
        String currency = invoices[0].netAmount().currency();
        boolean allCurrencySame = Arrays.stream(invoices).allMatch(invoice -> invoice.netAmount().currency().equals(currency));
        if(!allCurrencySame)  throw new NotAllInvoicesShareSameCurrency();

        BigDecimal netTotal = Arrays.stream(invoices)
                .map(inv -> inv.netAmount().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Money(netTotal.setScale(2, RoundingMode.HALF_EVEN),currency);
    }
    static String renderSimpleReport(ZoneId displayZone, Invoice... invoices){

        StringBuilder header = new StringBuilder("Invoice Report (display zone: "+displayZone+")"+ "\n");
        StringBuilder Invoices = new StringBuilder();
        DateTimeFormatter PolandWar = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
       for (Invoice inv : invoices){
           Invoices.append(inv.number()).append(" | ").append(inv.clientName()).append(" | ").append(inv.issuedAt().withZoneSameInstant(displayZone).format(PolandWar)).append(" | ").append("net ").append(inv.netAmount().toString()).append(" | ").append("gross ").append(grossAmount(inv)).append("\n");
       }
       String[] lines = Invoices.toString().split("\n");
        int n = 0;
        for (String s : lines) {
            n = Math.max(n, s.length());
        }
        StringBuilder line = new StringBuilder("-".repeat(n)+ "\n");
        return header.append(line).append(Invoices).append(line).toString();
    }

    static SplitPaymentBreakdown split(Invoice i){
        if(!i.splitPayment()) throw new SplitNotAvailableException();

            return new SplitPaymentBreakdown(i.netAmount(),vatAmount(i));
    }
    static Money effectiveGross(Invoice original, List<CorrectionInvoice> corrections){
        BigDecimal grossAmount = grossAmount(original).amount();
        BigDecimal correction = new BigDecimal("0.0");
        for (CorrectionInvoice inv : corrections){
            if(!inv.amountAdjustment().currency().equals(original.netAmount().currency())) throw new IllegalArgumentException("Not all invoice have the same currency.");
            if(inv.type().equals(CorrectionInvoiceType.REFUND)) correction = correction.subtract(inv.amountAdjustment().amount());
            else correction = correction.add(inv.amountAdjustment().amount());
        }
        if(grossAmount.add(correction).compareTo(BigDecimal.ZERO) < 0){
         throw new ProcessRefundException(grossAmount.add(correction));
        }
        return new Money(grossAmount.add(correction),original.netAmount().currency());
    }
}
