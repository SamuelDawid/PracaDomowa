package org.yellowflash.zad09;

public class InsufficientFundsException extends RuntimeException {

    private final String accountNumber;
    private final double requested;
    private final double available;

    public InsufficientFundsException(String accountNumber, double requested, double available) {
        super(String.format("Konto %s: żądano %.2f, dostępne %.2f", accountNumber, requested, available));
        this.accountNumber = accountNumber;
        this.requested = requested;
        this.available = available;
    }

    public String getAccountNumber() { return accountNumber; }
    public double getRequested() { return requested; }
    public double getAvailable() { return available; }
}
