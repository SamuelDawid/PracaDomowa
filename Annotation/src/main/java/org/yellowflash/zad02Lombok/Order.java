package org.yellowflash.zad02Lombok;

import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
public class Order {
    @Singular
    List<Product> products;
    @Getter
    int id;
    @Getter
    String customerName;
    @EqualsAndHashCode.Exclude
    Product product;
    @EqualsAndHashCode.Exclude
    Address address;
}
