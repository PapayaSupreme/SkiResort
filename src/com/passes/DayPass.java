package com.passes;

import com.enums.PassCategory;
import com.enums.PassStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class DayPass extends Pass {
    private final double price;
    private final LocalDate validDate;

    public DayPass(int id,
                   int ownerId,
                   String lastName,
                   String firstName,
                   PassCategory category,
                   double price,
                   LocalDate validDate) {
        super(id, ownerId, lastName, firstName, category);
        this.price = price;
        this.validDate = validDate;
    }

    @Override
    public boolean isValidAt(Instant at) {
        LocalDate date = at.atZone(ZoneId.systemDefault()).toLocalDate();
        return date.isEqual(validDate) && getPassStatus() == PassStatus.ACTIVE;
    }

    @Override
    public double getPrice() {
        return price;
    }

    public LocalDate getValidDate() {
        return validDate;
    }
}
