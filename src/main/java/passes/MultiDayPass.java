package passes;

import enums.PassCategory;
import enums.PassStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class MultiDayPass extends Pass {
    private final double price;
    private final LocalDate startDate;
    private final int numberOfDays;

    public MultiDayPass(int ownerId, PassCategory passCategory, double price, LocalDate startDate, int numberOfDays) {
        super(ownerId, passCategory);
        this.price = price;
        this.startDate = startDate;
        this.numberOfDays = numberOfDays;
    }

    @Override public double getPrice() { return this.price; }
    public LocalDate getStartDate() { return this.startDate; }
    public int getNumberOfDays() { return this.numberOfDays; }
    public LocalDate getEndDate() {
        return this.startDate.plusDays(this.numberOfDays - 1);
    }

    @Override
    public boolean isValidAt(Instant at) {
        LocalDate date = at.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = this.startDate.plusDays(this.numberOfDays - 1);
        return (date.isEqual(this.startDate) || date.isAfter(this.startDate))
                && (date.isEqual(endDate)   || date.isBefore(endDate))
                && getPassStatus() == PassStatus.ACTIVE;
    }
}
