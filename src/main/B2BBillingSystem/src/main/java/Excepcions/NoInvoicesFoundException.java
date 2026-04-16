package Excepcions;

public class NoInvoicesFoundException extends RuntimeException {
    public NoInvoicesFoundException() {
        System.out.println("No invoices found");
    }
}
