package homework.model;

public enum AccountType {
    ROR1("Rachunek bieżący"),
    ROR2("Internetowy hit"),
    RO1("Super zysk"),
    RO2("Szybki zysk"),
    LO1("Stabilna lokata"),
    LO2("Mocny procent");

    private final String name;

    AccountType(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
