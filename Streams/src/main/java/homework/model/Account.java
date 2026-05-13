package homework.model;

import java.math.BigDecimal;

public class Account {
    private final AccountType type;
    private final String number;
    private final BigDecimal amount;
    private final Currency currency;

    public Account(AccountType type, String number, BigDecimal amount, Currency currency) {
        this.type = type;
        this.number = number;
        this.amount = amount;
        this.currency = currency;
    }

    public AccountType getType() {
        return type;
    }

    public String getNumber() {
        return number;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

}
