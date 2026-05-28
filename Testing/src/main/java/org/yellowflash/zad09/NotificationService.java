package org.yellowflash.zad09;

public interface NotificationService {
    void sendTransferConfirmation(String accountNumber, double amount, String recipientAccount);
    void sendTransferFailure(String accountNumber, double amount, String reason);
}
