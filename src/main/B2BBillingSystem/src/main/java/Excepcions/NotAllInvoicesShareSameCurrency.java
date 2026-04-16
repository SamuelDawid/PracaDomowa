package Excepcions;

public class NotAllInvoicesShareSameCurrency extends RuntimeException {
    public NotAllInvoicesShareSameCurrency() {
        System.out.println("Not All Invoices Share the Same Currency");
    }
}
