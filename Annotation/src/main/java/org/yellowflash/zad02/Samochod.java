package org.yellowflash.zad02;

public class Samochod extends Pojazd{
    @Override
    public String opis(){
        return "To jest samochód";
    }

    @Deprecated(since = "1.0", forRemoval = true)
    public void staraMetoda() {
    }
    public void nowaMetoda(){
        System.out.println("Ta nowsza");
    }
}
