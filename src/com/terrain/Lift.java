package com.terrain;

import com.enums.LiftStatus;
import com.enums.LiftType;
import com.enums.OpeningHours;
import com.enums.Point;

public class Lift extends SkiArea{
    private LiftType type;
    private LiftStatus status = LiftStatus.CLOSED;
    private Slope upSlope;
    private Slope downSlope;

    public Lift(String name, int id, Point highest, Point lowest,
                String domain, double length, OpeningHours openingHours,
                LiftType type, Slope upSlope, Slope downSlope) {
        super(name, id, highest, lowest, domain, length, openingHours);
        this.type = type;
        this.upSlope = upSlope;
        this.downSlope = downSlope;
    }

    public LiftType getType() { return this.type; }
    public LiftStatus getStatus() { return this.status; }
    public Slope getUpSlope() { return this.upSlope; }
    public Slope getDownSlope() { return this.downSlope; }

    public void setType(LiftType type) { this.type = type; }
    public void setStatus(LiftStatus status) { this.status = status; }
    public void setUpSlope(Slope upSlope) { this.upSlope = upSlope; }
    public void setDownSlope(Slope downSlope) { this.downSlope = downSlope; }
}
