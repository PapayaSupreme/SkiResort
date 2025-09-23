package com.passes;

import com.enums.PassCategory;
import com.enums.PassStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class YearPass extends Pass {
    private final double price;
    private final LocalDate seasonStart;
    private final LocalDate seasonEnd;

    public YearPass(int id,
                    int ownerId,
                    String lastName,
                    String firstName,
                    PassCategory category,
                    double price,
                    LocalDate seasonStart,
                    LocalDate seasonEnd) {
        super(id, ownerId, lastName, firstName, category);
        this.price = price;
        this.seasonStart = seasonStart;
        this.seasonEnd = seasonEnd;
    }

    @Override
    public boolean isValidAt(Instant at) {
        LocalDate date = at.atZone(ZoneId.systemDefault()).toLocalDate();
        return (date.isEqual(seasonStart) || date.isAfter(seasonStart))
                && (date.isEqual(seasonEnd)   || date.isBefore(seasonEnd))
                && getPassStatus() == PassStatus.ACTIVE;
    }

    @Override
    public double getPrice() {
        return price;
    }

    public LocalDate getSeasonStart() { return seasonStart; }
    public LocalDate getSeasonEnd()   { return seasonEnd; }
}
