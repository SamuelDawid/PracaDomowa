package homework.generator;

import homework.model.Account;
import homework.model.AccountType;
import homework.model.Currency;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class AccountGenerator {
    public List<Account> generate() {
        return Arrays.asList(
                new Account(AccountType.LO1, "6754", new BigDecimal("10.50"), Currency.PLN),
                new Account(AccountType.ROR1, "1178", new BigDecimal("999.50"), Currency.USD),
                new Account(AccountType.ROR1, "8967", new BigDecimal("1000"), Currency.EUR),
                new Account(AccountType.ROR1, "112221", new BigDecimal("1667.22"), Currency.PLN),
                new Account(AccountType.ROR1, "1234", new BigDecimal("1888822.1"), Currency.EUR),
                new Account(AccountType.ROR1, "2346", new BigDecimal("8236626.12"), Currency.PLN),
                new Account(AccountType.ROR1, "7676", new BigDecimal("1230.00"), Currency.EUR),
                new Account(AccountType.ROR1, "0192", new BigDecimal("88890.00"), Currency.PLN),
                new Account(AccountType.ROR1, "8474", new BigDecimal("10000.60"), Currency.CHF),
                new Account(AccountType.RO1, "3892", new BigDecimal("70998.8"), Currency.EUR),
                new Account(AccountType.ROR1, "65423", new BigDecimal("800.99"), Currency.PLN),
                new Account(AccountType.RO2, "87631", new BigDecimal("100"), Currency.CHF),
                new Account(AccountType.LO1, "1235478", new BigDecimal("1"), Currency.PLN),
                new Account(AccountType.RO1, "72446", new BigDecimal("0.01"), Currency.CHF),
                new Account(AccountType.RO1, "90753", new BigDecimal("0"), Currency.CHF),
                new Account(AccountType.RO2, "865423", new BigDecimal("0"), Currency.CHF),
                new Account(AccountType.LO2, "9612541", new BigDecimal("23500.86"), Currency.USD),
                new Account(AccountType.RO1, "971561", new BigDecimal("9999"), Currency.USD),
                new Account(AccountType.LO2, "97156221", new BigDecimal("10.00"), Currency.CHF),
                new Account(AccountType.RO1, "867151", new BigDecimal("109823.00"), Currency.PLN),
                new Account(AccountType.RO1, "862252", new BigDecimal("123771"), Currency.CHF),
                new Account(AccountType.LO1, "872562", new BigDecimal("7332"), Currency.PLN),
                new Account(AccountType.RO2, "34563", new BigDecimal("2346"), Currency.PLN),
                new Account(AccountType.RO2, "1122", new BigDecimal("100"), Currency.USD),
                new Account(AccountType.RO1, "62222", new BigDecimal("1009"), Currency.PLN),
                new Account(AccountType.RO2, "73344", new BigDecimal("0"), Currency.PLN),
                new Account(AccountType.LO1, "8723212", new BigDecimal("2435"), Currency.USD),
                new Account(AccountType.RO2, "3457117", new BigDecimal("10000984"), Currency.PLN),
                new Account(AccountType.ROR2, "45218", new BigDecimal("108987.0"), Currency.CHF),
                new Account(AccountType.LO1, "24578", new BigDecimal("13873"), Currency.CHF),
                new Account(AccountType.LO1, "0000064", new BigDecimal("9766"), Currency.USD),
                new Account(AccountType.ROR2, "2322255", new BigDecimal("1000"), Currency.CHF),
                new Account(AccountType.ROR2, "666622", new BigDecimal("287"), Currency.CHF),
                new Account(AccountType.ROR2, "998292", new BigDecimal("1467"), Currency.CHF),
                new Account(AccountType.LO1, "938383", new BigDecimal("1600"), Currency.EUR),
                new Account(AccountType.LO1, "2018543", new BigDecimal("23000.86"), Currency.PLN)
        );
    }
}
