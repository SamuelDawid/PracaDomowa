package org.yellowflash.zad09;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String accountNumber) {
        super("Konto nie znalezione: " + accountNumber);
    }
}
