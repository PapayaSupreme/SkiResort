package com.terrain;

import com.enums.*;

public class Slope extends SkiArea {
    private SlopeDifficulty slopeDifficulty;
    private SnowConsistency snowConsistency = SnowConsistency.NONE;
    private SlopeType slopeType;
    private SkiArea skiArea;

    public Slope(String name, int id, Point highest, Point lowest,
                 String domain, double length, OpeningHours openingHours,
                 SlopeDifficulty slopeDifficulty, SlopeType slopeType, SkiArea skiArea) {
        super(name, id, highest, lowest, domain, length, openingHours);
        this.slopeDifficulty = slopeDifficulty;
        this.slopeType = slopeType;
        this.skiArea = skiArea;
    }

    public SlopeDifficulty getSlopeDifficulty() { return this.slopeDifficulty; }
    public SnowConsistency getSnowConsistency() { return this.snowConsistency; }
    public SlopeType getSlopeType() { return this.slopeType;}
    public SkiArea getSkiArea() { return skiArea; }

    public void setSlopeDifficulty(SlopeDifficulty slopeDifficulty) { this.slopeDifficulty = slopeDifficulty; }
    public void setSnowConsistency(SnowConsistency snowConsistency) { this.snowConsistency = snowConsistency; }
    public void setSlopeType(SlopeType slopeType) { this.slopeType = slopeType; }
    public void setSkiArea(SkiArea skiArea) { this.skiArea = skiArea; }
}
