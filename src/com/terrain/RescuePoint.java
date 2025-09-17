package com.terrain;

import com.enums.Point;

public class RescuePoint extends POI{
    private boolean warning = false;

    public RescuePoint(String name, int id, Point location) {
        super(name, id, location);
    }

    public boolean isWarning() { return this.warning; }

    public void setWarning(boolean warning) { this.warning = warning; }
}
