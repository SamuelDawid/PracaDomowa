package org.yellowflash.zad09;

import java.time.LocalDateTime;

public class TransferLog {

    private final String fromAccount;
    private final String toAccount;
    private final double amount;
    private final boolean success;
    private final String failureReason;
    private final LocalDateTime timestamp;

    public TransferLog(String fromAccount, String toAccount, double amount,
                       boolean success, String failureReason) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.success = success;
        this.failureReason = failureReason;
        this.timestamp = LocalDateTime.now();
    }

    public String getFromAccount() { return fromAccount; }
    public String getToAccount() { return toAccount; }
    public double getAmount() { return amount; }
    public boolean isSuccess() { return success; }
    public String getFailureReason() { return failureReason; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
