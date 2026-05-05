package org.yellowflash.domain;

import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.time.LocalDate;
import java.util.Optional;

public record Rental(int id,
                     int moveId,
                     int customerId,
                     LocalDate rentDate,
                     LocalDate plannedReturnDate,
                     Optional<LocalDate> actualReturnDate) {
    public Rental {
        Validate.notNull(rentDate,"Must choose rentDate");
        Validate.notNull(plannedReturnDate,"Must choose plannedReturnDate");
        Validate.notNull(actualReturnDate,"actualReturnDate cannot be null, use Optional.empty()");
        Validate.isTrue(plannedReturnDate.isAfter(rentDate),"Planned Return Date must be after Rent Day");
    }

    Rental withReturn(LocalDate today){

        return new Rental(this.id,this.moveId,this.customerId,this.rentDate,this.plannedReturnDate,
                Optional.of(today));
    }
}
