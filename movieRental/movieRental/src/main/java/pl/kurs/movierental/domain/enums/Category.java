package pl.kurs.movierental.domain.enums;

public enum Category {
    KIDS{
        @Override public int rentalDays()  { return 14; }
        @Override public int pricePLN()    { return 3; }
        @Override public int minimumAge()  { return 0; }
    },
    FAMILY{
        @Override public int rentalDays()  { return 7; }
        @Override public int pricePLN()    { return 6; }
        @Override public int minimumAge()  { return 0; }
    },
    DRAMA{
        @Override public int rentalDays()  { return 7; }
        @Override public int pricePLN()    { return 6; }
        @Override public int minimumAge()  { return 12; }
    },
    ACTION{
        @Override public int rentalDays()  { return 5; }
        @Override public int pricePLN()    { return 7; }
        @Override public int minimumAge()  { return 16; }
    },
    HORROR{
        @Override public int rentalDays()  { return 5; }
        @Override public int pricePLN()    { return 8; }
        @Override public int minimumAge()  { return 18; }
    };

    public abstract int rentalDays();
    public abstract int pricePLN();
    public abstract int minimumAge();

}
