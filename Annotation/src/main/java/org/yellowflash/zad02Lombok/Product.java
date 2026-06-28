package org.yellowflash.zad02Lombok;

import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Product {
    @EqualsAndHashCode.Exclude
    final String name;
    @EqualsAndHashCode.Exclude
    Double price;
    @ToString.Exclude
    final String id;
    @Getter
    final String category;
}
