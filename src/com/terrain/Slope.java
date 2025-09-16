package com.terrain;

import com.enums.*;

public class Slope extends SkiArea {
    private SlopeDifficulty slopeDifficulty;
    private SnowConsistency snowConsistency = SnowConsistency.NONE;
    private SlopeType type;

    public Slope(String name, int id, Point highest, Point lowest,
                 String domain, double length, OpeningHours openingHours,
                 SlopeDifficulty slopeDifficulty, SlopeType slopeType) {
        super(name, id, highest, lowest, domain, length, openingHours);
        this.slopeDifficulty = slopeDifficulty;
        this.type = slopeType;
    }
}
