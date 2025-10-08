package com.passes;

import com.enums.PassCategory;
import com.enums.PassStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class ALaCartePass extends Pass {
    private final LocalDate seasonStart;
    private final LocalDate seasonEnd;
    private final Set<LocalDate> daysUsed = new LinkedHashSet<>();

    public ALaCartePass(int ownerId, PassCategory passCategory, LocalDate seasonStart, LocalDate seasonEnd) {
        super(ownerId, passCategory);
        this.seasonStart = seasonStart;
        this.seasonEnd = seasonEnd;
    }

    public LocalDate getSeasonStart() { return this.seasonStart; }
    public LocalDate getSeasonEnd()   { return this.seasonEnd; }
    public Set<LocalDate> getDaysUsed() {return Set.copyOf(this.daysUsed);}

    @Override
    public boolean isValidAt(Instant at) {
        if (getPassStatus() != PassStatus.ACTIVE) return false;
        LocalDate d = at.atZone(ZoneId.systemDefault()).toLocalDate();
        return !d.isBefore(this.seasonStart) && !d.isAfter(this.seasonEnd);
    }

    @Override
    public double getPrice() {
        //TODO: implement this
        return 0;
    }

    public void logUse(LocalDate date) {
        this.daysUsed.add(date);
    }

    public int getUsageCount() {
        return daysUsed.size();
    }

    public double getAmountDue() {
        return 0;
        //TODO: implement that too
    }


}
