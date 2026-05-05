package org.yellowflash.domain;

import org.apache.commons.lang3.Validate;
import org.yellowflash.domain.enums.CustomerStatus;

public record Customer(int id,
                       String firstName,
                       String lastName,
                       int age,
                       CustomerStatus status) {
    public Customer {
        Validate.notBlank(firstName,"Must provide first name");
        Validate.notBlank(lastName,"Must provide last name");
        Validate.notNull(status,"Must choose status");
    }
}
