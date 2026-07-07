package org.yellowflash.zad09;

public class DailyLimitExceededException extends RuntimeException {
    public DailyLimitExceededException(){
        super( "Kwota ktora chesz wyslac przekracza Twoj dzienny limit");
    }
}
