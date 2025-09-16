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
                String domain, double length, OpeningHours opening_hours,
                LiftType type, Slope upSlope, Slope downSlope) {
        super(name, id, highest, lowest, domain, length, opening_hours);
        this.type = type;
        this.upSlope = upSlope;
        this.downSlope = downSlope;
    }

    public LiftType getType() { return type; }
    public LiftStatus getStatus() { return status; }
}
