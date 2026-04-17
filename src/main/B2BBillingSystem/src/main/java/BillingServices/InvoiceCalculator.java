package BillingServices;

import Excepcions.NoInvoicesFoundException;
import Excepcions.NotAllInvoicesShareSameCurrency;
import records.Invoice;
import records.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

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
}
