package homework.model;

import java.util.List;

public class Holding {
    private final String name;
    private final List<Company> companies;

    public Holding(String name, List<Company> companies) {
        this.name = name;
        this.companies = companies;
    }

    public String getName() {
        return name;
    }

    public List<Company> getCompanies() {
        return companies;
    }
}
