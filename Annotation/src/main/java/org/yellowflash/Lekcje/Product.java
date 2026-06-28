package org.yellowflash.Lekcje;
import lombok.*;

@Data
@Builder
public class Product {
    private Long id;
    private String name;
    private double price;

    private void m1() {
    }
}
