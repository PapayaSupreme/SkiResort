package com.terrain;


import com.enums.SlopeDifficulty;
import com.enums.SnowConsistency;
import com.enums.SlopeType;
import com.enums.Point;
import com.enums.OpeningHours;
import com.peoples.Worksite;

public class Slope implements Worksite {
    private final int id;
    private String name;
    private SlopeDifficulty slopeDifficulty;
    private SnowConsistency snowConsistency = SnowConsistency.NONE;
    private final SlopeType slopeType;
    private final SkiArea skiArea;
    private final Point start;
    private final Point end;
    private final double length;
    private OpeningHours openingHours;

    public Slope(String name, int id, Point start, Point end,
                 double length, OpeningHours openingHours,
                 SlopeDifficulty slopeDifficulty, SlopeType slopeType, SkiArea skiArea) {
        this.name = name;
        this.id = id;
        this.start = start;
        this.end = end;
        this.length = length;
        this.openingHours = openingHours;
        this.slopeDifficulty = slopeDifficulty;
        this.slopeType = slopeType;
        this.skiArea = skiArea;
    }

    @Override public String getName() { return name; }
    @Override public int getId() { return id; }
    public Point getStart() { return start; }
    public Point getEnd() { return end; }
    public double getLength() { return length; }
    public OpeningHours getOpeningHours() { return openingHours; }
    public SlopeDifficulty getSlopeDifficulty() { return this.slopeDifficulty; }
    public SnowConsistency getSnowConsistency() { return this.snowConsistency; }
    public SlopeType getSlopeType() { return this.slopeType;}
    public SkiArea getSkiArea() { return skiArea; }


    @Override public void setName(String name) { this.name = name; }
    public void setOpeningHours(OpeningHours openingHours) { this.openingHours = openingHours; }
    public void setSlopeDifficulty(SlopeDifficulty slopeDifficulty) { this.slopeDifficulty = slopeDifficulty; }
    public void setSnowConsistency(SnowConsistency snowConsistency) { this.snowConsistency = snowConsistency; }
}
