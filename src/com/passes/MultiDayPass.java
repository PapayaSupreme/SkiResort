package com.passes;

import com.enums.PassCategory;
import com.enums.PassStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * A pass valid for a fixed number of consecutive days starting from a given date.
 */
public class MultiDayPass extends Pass {
    private final double price;
    private final LocalDate startDate;
    private final int numberOfDays;

    public MultiDayPass(int id,
                        int ownerId,
                        String lastName,
                        String firstName,
                        PassCategory category,
                        double price,
                        LocalDate startDate,
                        int numberOfDays) {
        super(id, ownerId, lastName, firstName, category);
        this.price = price;
        this.startDate = startDate;
        this.numberOfDays = numberOfDays;
    }

    @Override
    public boolean isValidAt(Instant at) {
        LocalDate date = at.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = startDate.plusDays(numberOfDays - 1);
        return (date.isEqual(startDate) || date.isAfter(startDate))
                && (date.isEqual(endDate)   || date.isBefore(endDate))
                && getPassStatus() == PassStatus.ACTIVE;
    }

    @Override
    public double getPrice() {
        return price;
    }

    public LocalDate getStartDate() { return startDate; }
    public int getNumberOfDays() { return numberOfDays; }

    public LocalDate getEndDate() {
        return startDate.plusDays(numberOfDays - 1);
    }
}
