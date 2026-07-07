package org.yellowflash.zad09;

public class TransferService {

    private final AccountRepository accountRepository;
    private final AuditLogger auditLogger;
    private final NotificationService notificationService;

    public TransferService(AccountRepository accountRepository,
                           AuditLogger auditLogger,
                           NotificationService notificationService) {
        this.accountRepository = accountRepository;
        this.auditLogger = auditLogger;
        this.notificationService = notificationService;
    }

    public void transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Kwota przelewu musi być większa od zera");
        }

        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException(fromAccountNumber));

        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException(toAccountNumber));

        if (fromAccount.getBalance() < amount) {
            auditLogger.logTransfer(new TransferLog(
                    fromAccountNumber, toAccountNumber, amount, false, "Brak środków"
            ));
            notificationService.sendTransferFailure(
                    fromAccountNumber, amount, "Brak wystarczających środków"
            );
            throw new InsufficientFundsException(
                    fromAccountNumber, amount, fromAccount.getBalance()
            );
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        auditLogger.logTransfer(new TransferLog(
                fromAccountNumber, toAccountNumber, amount, true, null
        ));
        notificationService.sendTransferConfirmation(
                fromAccountNumber, amount, toAccountNumber
        );
    }
    public void transferWithLimit(String fromAccountNumber, String toAccountNumber, double amount, double dailyLimit){
        if (amount <= 0) {
            throw new IllegalArgumentException("Kwota przelewu musi być większa od zera");
        }
        if(amount > dailyLimit) {
            auditLogger.logTransfer(new TransferLog(
                    fromAccountNumber, toAccountNumber, amount, false, "Dzienny Limit przekroczony"
            ));
            throw new DailyLimitExceededException();
        }

        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException(fromAccountNumber));

        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException(toAccountNumber));

        if (fromAccount.getBalance() < amount) {
            auditLogger.logTransfer(new TransferLog(
                    fromAccountNumber, toAccountNumber, amount, false, "Brak środków"
            ));
            notificationService.sendTransferFailure(
                    fromAccountNumber, amount, "Brak wystarczających środków"
            );
            throw new InsufficientFundsException(
                    fromAccountNumber, amount, fromAccount.getBalance()
            );
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        auditLogger.logTransfer(new TransferLog(
                fromAccountNumber, toAccountNumber, amount, true, null
        ));
        notificationService.sendTransferConfirmation(
                fromAccountNumber, amount, toAccountNumber
        );
    }
}
