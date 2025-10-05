package com.terrain;


import com.enums.SlopeDifficulty;
import com.enums.SnowConsistency;
import com.enums.SlopeType;
import com.enums.Point;
import com.enums.OpeningHours;
import com.people.Worksite;

public class Slope implements Worksite {
    private final int id;
    private String name;
    private SlopeDifficulty slopeDifficulty;
    private SnowConsistency snowConsistency = SnowConsistency.NONE;
    private final SlopeType slopeType;
    private final SkiArea skiArea;
    private final Point up;
    private final Point down;
    private final double length;
    private OpeningHours openingHours;

    public Slope(String name, int id, Point up, Point down,
                 double length, OpeningHours openingHours,
                 SlopeDifficulty slopeDifficulty, SlopeType slopeType, SkiArea skiArea) {
        this.name = name;
        this.id = id;
        this.up = up;
        this.down = down;
        this.length = length;
        this.openingHours = openingHours;
        this.slopeDifficulty = slopeDifficulty;
        this.slopeType = slopeType;
        this.skiArea = skiArea;
    }

    @Override public String getName() { return this.name; }
    @Override public int getId() { return this.id; }
    public Point getUp() { return this.up; }
    public Point getDown() { return this.down; }
    public double getLength() { return this.length; }
    public OpeningHours getOpeningHours() { return this.openingHours; }
    public SlopeDifficulty getSlopeDifficulty() { return this.slopeDifficulty; }
    public SnowConsistency getSnowConsistency() { return this.snowConsistency; }
    public SlopeType getSlopeType() { return this.slopeType;}
    public SkiArea getSkiArea() { return this.skiArea; }


    @Override public void setName(String name) { this.name = name; }
    public void setOpeningHours(OpeningHours openingHours) { this.openingHours = openingHours; }
    public void setSlopeDifficulty(SlopeDifficulty slopeDifficulty) { this.slopeDifficulty = slopeDifficulty; }
    public void setSnowConsistency(SnowConsistency snowConsistency) { this.snowConsistency = snowConsistency; }
}
