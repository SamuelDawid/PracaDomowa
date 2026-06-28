package org.yellowflash.zad02Lombok;

import lombok.Value;

@Value
public class Address {
    String streetName,postCode;
    int doorNumber;
}
