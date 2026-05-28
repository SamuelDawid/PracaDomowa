package org.yellowflash.zad09;

import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findByAccountNumber(String accountNumber);
    Account save(Account account);
}
