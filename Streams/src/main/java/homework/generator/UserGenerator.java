package homework.generator;

import homework.model.Account;
import homework.model.Permit;
import homework.model.Sex;
import homework.model.User;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class UserGenerator {
    public List<User> generate() {
        List<Account> accounts = new AccountGenerator().generate();
        List<User> result = new LinkedList<>();

        result.add(new User("Adam", "Wojcik", Sex.MAN, 17,
                Arrays.asList(accounts.get(0), accounts.get(1)),
                Arrays.asList(Permit.DEPOSIT, Permit.TRANSFER))
        );

        result.add(new User("Mateusz", "Kowalski", Sex.MAN, 33,
                List.of(accounts.get(2)),
                Arrays.asList(Permit.DEPOSIT, Permit.TRANSFER, Permit.LOAN)
        ));

        result.add(new User("Bartek", "Pasibrzuch", Sex.OTHER, 18,
                List.of(accounts.get(3)),
                Arrays.asList(Permit.DEPOSIT, Permit.TRANSFER, Permit.LOAN, Permit.ORDER_HISTORY)
        ));

        result.add(new User("Jan", "Bazuka", Sex.MAN, 46,
                Arrays.asList(accounts.get(4), accounts.get(5), accounts.get(6)),
                Arrays.asList(Permit.DEPOSIT, Permit.TRANSFER, Permit.LOAN)
        ));

        result.add(new User("Zosia", "Psikuta", Sex.WOMAN, 67,
                Arrays.asList(accounts.get(7), accounts.get(8), accounts.get(9)),
                Arrays.asList(Permit.DEPOSIT, Permit.TRANSFER, Permit.LOAN, Permit.ORDER_HISTORY)
        ));

        result.add(new User("Magdalena", "Warszawska", Sex.WOMAN, 33,
                Arrays.asList(accounts.get(10), accounts.get(11)),
                Arrays.asList(Permit.DEPOSIT, Permit.LOAN, Permit.ORDER_HISTORY)
        ));

        result.add(new User("Amadeusz", "Mocarz", Sex.MAN, 29,
                Arrays.asList(accounts.get(12)),
                Arrays.asList(Permit.ORDER_HISTORY)
        ));

        result.add(new User("Filip", "Flirciczart", Sex.MAN, 33,
                Arrays.asList(accounts.get(13)),
                Arrays.asList(Permit.DEPOSIT, Permit.TRANSFER, Permit.LOAN, Permit.ORDER_HISTORY)
        ));

        result.add(new User("Zenek", "Biednapalka", Sex.MAN, 18,
                Arrays.asList(),
                Arrays.asList()
        ));

        result.add(new User("Zenon", "Kucowski", Sex.OTHER, 21,
                Arrays.asList(),
                Arrays.asList(Permit.DEPOSIT, Permit.TRANSFER, Permit.LOAN, Permit.ORDER_HISTORY)
        ));

        result.add(new User("Mariusz", "Dreh", Sex.OTHER, 50,
                Arrays.asList(accounts.get(14)),
                Arrays.asList(Permit.DEPOSIT, Permit.TRANSFER, Permit.LOAN, Permit.ORDER_HISTORY)
        ));

        result.add(new User("Marcin", "Marcinowicz", Sex.MAN, 37,
                Arrays.asList(
                        accounts.get(15),
                        accounts.get(16),
                        accounts.get(17),
                        accounts.get(18),
                        accounts.get(19),
                        accounts.get(20)
                ),
                Arrays.asList(Permit.DEPOSIT, Permit.TRANSFER, Permit.LOAN, Permit.ORDER_HISTORY)
        ));

        result.add(new User("Jan", "Nowicki", Sex.MAN, 45,
                Arrays.asList(accounts.get(22)),
                Arrays.asList(Permit.TRANSFER, Permit.LOAN, Permit.ORDER_HISTORY)
        ));

        result.add(new User("Kasia", "Nawalka", Sex.WOMAN, 29,
                Arrays.asList(accounts.get(23), accounts.get(24)),
                Arrays.asList(Permit.DEPOSIT, Permit.TRANSFER, Permit.LOAN)
        ));

        result.add(new User("Martin", "Prawicowy", Sex.MAN, 29,
                Arrays.asList(accounts.get(25), accounts.get(26)),
                Arrays.asList(Permit.DEPOSIT, Permit.TRANSFER, Permit.LOAN, Permit.ORDER_HISTORY)
        ));

        result.add(new User("Karol", "Romanowicz", Sex.MAN, 64,
                Arrays.asList(accounts.get(27), accounts.get(28)),
                Arrays.asList(Permit.LOAN)
        ));

        result.add(new User("Marta", "Wialkibuz", Sex.WOMAN, 33,
                Arrays.asList(accounts.get(29)),
                Arrays.asList(Permit.DEPOSIT, Permit.TRANSFER, Permit.LOAN, Permit.ORDER_HISTORY)
        ));

        result.add(new User("Patryk", "Piwny", Sex.MAN, 28,
                Arrays.asList(accounts.get(30)),
                Arrays.asList(Permit.DEPOSIT, Permit.TRANSFER, Permit.LOAN, Permit.ORDER_HISTORY)
        ));

        result.add(new User("Zenek", "Jawowy", Sex.MAN, 22,
                Arrays.asList(accounts.get(31)),
                Arrays.asList()
        ));

        result.add(new User("Alfred", "Pasibrzuch", Sex.MAN, 40,
                Arrays.asList(accounts.get(32), accounts.get(33), accounts.get(34)),
                Arrays.asList(Permit.DEPOSIT, Permit.TRANSFER, Permit.LOAN, Permit.ORDER_HISTORY)
        ));

        return result;
    }
}
