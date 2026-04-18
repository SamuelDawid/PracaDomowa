package Excepcions;

public class SplitNotAvailableException extends RuntimeException {
    public SplitNotAvailableException()
    {
        System.out.println("Split is not available for this invoice");;
    }
}
