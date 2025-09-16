package com.terrain;

import com.enums.LiftStatus;
import com.enums.LiftType;
import com.enums.OpeningHours;
import com.enums.Point;

public class Lift extends SkiArea{
    private LiftType type;
    private LiftStatus status = LiftStatus.CLOSED;
    private Slope up_slope;
    private Slope down_slope;

    public Lift(String name, int id, Point highest, Point lowest,
                String domain, double length, OpeningHours opening_hours,
                LiftType type, Slope up_slope, Slope down_slope) {
        super(name, id, highest, lowest, domain, length, opening_hours);
        this.type = type;
        this.up_slope = up_slope;
        this.down_slope = down_slope;
    }

    public LiftType getType() { return type; }
    public LiftStatus getStatus() { return status; }
}
