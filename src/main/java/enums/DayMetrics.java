package enums;

import java.time.LocalDate;

public record DayMetrics(LocalDate localDate, long distinctPasses, long uses) {

    @Override
    public String toString() {
        return "Day: " + this.localDate.toString() + " (" + this.uses + " usages on " + this.distinctPasses + " passes)";
    }
}
