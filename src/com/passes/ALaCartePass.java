package com.passes;

import com.enums.PassCategory;
import com.enums.PassStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A-la-carte (pay-later) pass:
 * - Valid for the full season window while ACTIVE.
 * - Logs specific calendar days actually used.
 * - Billing is pricePerDay * uniqueUsageDays.
 */
public class ALaCartePass extends Pass {
    private final LocalDate seasonStart;
    private final LocalDate seasonEnd;
    private int counter;

    // Unique day uses, in insertion order
    private final Set<LocalDate> usageDays = new LinkedHashSet<>();

    public ALaCartePass(int id, int ownerId, PassCategory category, String firstName,
                        String lastName, LocalDate seasonStart, LocalDate seasonEnd) {
        super(id, ownerId, lastName, firstName, category);

        if (seasonStart == null || seasonEnd == null) {
            throw new IllegalArgumentException("Season start/end must be provided");
        }
        if (seasonEnd.isBefore(seasonStart)) {
            throw new IllegalArgumentException("seasonEnd must be on or after seasonStart");
        }

        this.seasonStart = seasonStart;
        this.seasonEnd = seasonEnd;
        this.counter = 0;
    }

    @Override
    public boolean isValidAt(Instant at) {
        if (getPassStatus() != PassStatus.ACTIVE) return false;
        LocalDate d = at.atZone(ZoneId.systemDefault()).toLocalDate();
        return !d.isBefore(seasonStart) && !d.isAfter(seasonEnd);
    }

    @Override
    public double getPrice() {
        //TODO: implement this
        return 0;
    }

    public boolean logUse(LocalDate date) {
        Objects.requireNonNull(date, "date");
        if (getPassStatus() != PassStatus.ACTIVE) return false;
        if (date.isBefore(seasonStart) || date.isAfter(seasonEnd)) return false;
        return usageDays.add(date);
    }

    public boolean logUse(Instant when) {
        Objects.requireNonNull(when, "when");
        LocalDate date = when.atZone(ZoneId.systemDefault()).toLocalDate();
        return logUse(date);
    }

    public boolean removeUse(LocalDate date) {
        return usageDays.remove(date);
    }

    public Set<LocalDate> getUsageDays() {
        return Collections.unmodifiableSet(usageDays);
    }

    public int getUsageCount() {
        return usageDays.size();
    }

    public double getAmountDue() {
        return 0;
        //TODO: implement that too
    }

    public LocalDate getSeasonStart() { return this.seasonStart; }
    public LocalDate getSeasonEnd()   { return this.seasonEnd; }

    public int getCounter() { return this.counter; }
}
