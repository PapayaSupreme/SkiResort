package com.terrain;

import com.enums.*;
import com.skiresort.enums.*;

public class Slope extends SkiArea {
    private SlopeDifficulty difficulty;
    private SnowConsistency snow_consistency = SnowConsistency.NONE;
    private SlopeType type;

    public Slope(String name, int id, Point highest, Point lowest,
                 String domain, double length, OpeningHours opening_hours,
                 SlopeDifficulty difficulty, SlopeType type) {
        super(name, id, highest, lowest, domain, length, opening_hours);
        this.difficulty = difficulty;
        this.type = type;
    }
}
