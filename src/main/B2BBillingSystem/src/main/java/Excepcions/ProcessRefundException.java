package Excepcions;

import java.math.BigDecimal;

public class ProcessRefundException extends RuntimeException {
    public ProcessRefundException(BigDecimal amount) {
        System.out.println("correction is bigger than inv amount, please process refund of: " + amount);
    }
}
