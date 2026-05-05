package org.yellowflash.error;

public class RentalException extends Exception{

    private final RentalError error;
    public RentalException(RentalError rentalError){
        super(rentalError.message());
        this.error = rentalError;
    }
    public RentalError error() {
        return error;
    }
}
