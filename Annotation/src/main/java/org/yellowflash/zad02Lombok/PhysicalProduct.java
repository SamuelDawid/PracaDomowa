package org.yellowflash.zad02Lombok;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class PhysicalProduct extends Product{
    int stock;


}
