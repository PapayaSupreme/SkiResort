package com.terrain;

import com.enums.LiftStatus;
import com.enums.LiftType;
import com.enums.OpeningHours;
import com.enums.Point;
import com.people.Worksite;

public class Lift implements Worksite {
    private final int id;
    private String name;
    private LiftType type;
    private LiftStatus status = LiftStatus.CLOSED;
    private final SkiArea skiArea;
    private final Point up;
    private final Point down;
    private final double length;
    private OpeningHours openingHours;
    private Slope upSlope;
    private Slope downSlope;

    public Lift(String name, int id, Point up, Point down,
                double length, OpeningHours openingHours,
                LiftType type, Slope upSlope, Slope downSlope,
                SkiArea skiArea) {
        this.name = name;
        this.id = id;
        this.up = up;
        this.down = down;
        this.length = length;
        this.openingHours = openingHours;
        this.type = type;
        this.upSlope = upSlope;
        this.downSlope = downSlope;
        this.skiArea = skiArea;
    }
    @Override public String getName() { return this.name; }
    @Override public int getId() { return this.id; }
    public Point getUp() { return this.up; }
    public Point getDown() { return this.down; }
    public double getLength() { return this.length; }
    public OpeningHours getOpeningHours() { return this.openingHours; }
    public LiftType getType() { return this.type; }
    public LiftStatus getStatus() { return this.status; }
    public Slope getUpSlope() { return this.upSlope; }
    public Slope getDownSlope() { return this.downSlope; }
    public SkiArea getSkiArea() { return skiArea; }

    @Override public void setName(String name) { this.name = name; }
    public void setType(LiftType type) { this.type = type; }
    public void setStatus(LiftStatus status) { this.status = status; }
    public void setUpSlope(Slope upSlope) { this.upSlope = upSlope; }
    public void setDownSlope(Slope downSlope) { this.downSlope = downSlope; }
    public void setOpeningHours(OpeningHours openingHours) { this.openingHours = openingHours; }
}
