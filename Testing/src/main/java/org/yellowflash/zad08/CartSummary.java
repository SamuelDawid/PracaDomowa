package org.yellowflash.zad08;

import java.util.Objects;

public class CartSummary {
    double subtotal,discountAmount,total;
    int itemCount;

    public CartSummary(double subtotal, double discountAmount, double total, int itemCount) {
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.total = total;
        this.itemCount = itemCount;
    }
    public CartSummary(){

    }
    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        CartSummary that = (CartSummary) object;
        return Double.compare(subtotal, that.subtotal) == 0 && Double.compare(discountAmount, that.discountAmount) == 0 && Double.compare(total, that.total) == 0 && itemCount == that.itemCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(subtotal, discountAmount, total, itemCount);
    }

    @Override
    public String toString() {
        return "CartSummary{" +
                "SubTotal=" + subtotal +
                ", discountAmount=" + discountAmount +
                ", total=" + total +
                ", itemCount=" + itemCount +
                '}';
    }
}
