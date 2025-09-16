package com.terrain;

import com.enums.POIStatus;
import com.enums.Point;

public class RescuePoint extends POI{
    private boolean warning = false;

    public RescuePoint(String name, int id, Point location, POIStatus status) {
        super(name, id, location, status);
    }
}
