package homework.model;

import java.util.List;

public class Company {
    private final String name;
    private final List<User> users;

    public Company(String name, List<User> users) {
        this.name = name;
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public List<User> getUsers() {
        return users;
    }
}
