package com.terrain;

import com.enums.Point;

public class RescuePoint extends POI{
    private boolean warning = false;

    public RescuePoint(String name, int id, Point location, SkiArea skiArea) {
        super(name, id, location, skiArea);
    }

    public boolean isWarning() { return this.warning; }

    public void setWarning(boolean warning) { this.warning = warning; }

    @Override
    public String toString() {
        return "Rescue Point: warning=" + this.warning + " - " + super.toString();
    }
}
