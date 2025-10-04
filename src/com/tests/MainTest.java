package com.tests;

import com.enums.*;
import com.passes.*;
import com.peoples.*;

import java.time.*;
import java.util.Set;

public class MainTest {
    public static void main(String[] args) {
        """final LocalDate OPENING_DATE = LocalDate.of(2025,12,1);
        final LocalDate CLOSING_DATE = LocalDate.of(2026,4,30);
        // Opening hours example
        OpeningHours oh = new OpeningHours(
                LocalTime.of(9, 0), LocalTime.of(16, 30),
                Set.of(DayOfWeek.MONDAY)
        );

        // Year pass
        YearPass yp = new YearPass(
                1, 101, "Doe", "Jane",
                PassCategory.ADULT,
                799.0,
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2026, 4, 30)
        );
        System.out.println("YearPass valid on 2025-12-15: " +
                yp.isValidAt(LocalDate.of(2025,12,15).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        // Day pass
        DayPass dp = new DayPass(
                2, 101, "Doe", "Jane",
                PassCategory.ADULT,
                55.0,
                LocalDate.of(2025, 12, 27)
        );
        System.out.println("DayPass valid on 2025-12-27: " +
                dp.isValidAt(LocalDate.of(2025,12,27).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        // Multi-day pass (3 days from Dec 26)
        MultiDayPass mdp = new MultiDayPass(
                3, 101, "Doe", "Jane",
                PassCategory.ADULT,
                150.0,
                LocalDate.of(2025, 12, 26),
                3
        );
        System.out.println("MultiDayPass valid on 2025-12-28: " +
                mdp.isValidAt(LocalDate.of(2025,12,28).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        // A la carte: log usage
        ALaCartePass alc = new ALaCartePass(
                4, 101, PassCategory.ADULT, "Jane", "Doe",
                LocalDate.of(2025,12,1), LocalDate.of(2026,4,30)
        );
        alc.logUse(LocalDate.of(2025,12,26));
        alc.logUse(LocalDate.of(2025,12,27));
        System.out.println("ALaCarte uses: " + alc.getUsageCount() + ", due: " + alc.getAmountDue());"""

    }
}
