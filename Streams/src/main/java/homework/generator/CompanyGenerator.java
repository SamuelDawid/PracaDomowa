package homework.generator;

import homework.model.Company;
import homework.model.User;

import java.util.Arrays;
import java.util.List;

public class CompanyGenerator {
    public List<Company> generate() {
        UserGenerator userGenerator = new UserGenerator();
        List<User> users = userGenerator.generate();

        return Arrays.asList(
                new Company("Fanta",
                        Arrays.asList(
                                users.get(0),
                                users.get(1),
                                users.get(2)
                        )),
                new Company("Sprite",
                        Arrays.asList(
                                users.get(3),
                                users.get(4)
                        )),
                new Company("Nescafe",
                        Arrays.asList(
                                users.get(5),
                                users.get(6),
                                users.get(7),
                                users.get(8)
                        )),
                new Company("Gerber",
                        Arrays.asList(
                                users.get(9),
                                users.get(10),
                                users.get(11)
                        )),
                new Company("Nestea",
                        Arrays.asList(
                                users.get(12)
                        )),
                new Company("Lays",
                        Arrays.asList(
                                users.get(13),
                                users.get(14)
                        )),
                new Company("Pepsi",
                        Arrays.asList(
                                users.get(15),
                                users.get(16),
                                users.get(17)
                        )),
                new Company("Mirinda",
                        Arrays.asList(
                                users.get(18),
                                users.get(19)
                        )
                ));
    }
}
