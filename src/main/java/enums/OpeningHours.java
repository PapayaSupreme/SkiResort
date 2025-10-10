package enums;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

public record OpeningHours(
        LocalTime opening,
        LocalTime closing,
        Set<DayOfWeek> daysClosed
) {}
