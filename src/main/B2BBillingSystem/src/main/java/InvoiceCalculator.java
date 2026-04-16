import Excepcions.NoInvoicesFoundException;
import Excepcions.NotAllInvoicesShareSameCurrency;
import records.Invoice;
import records.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
}
