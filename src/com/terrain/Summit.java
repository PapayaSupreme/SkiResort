package com.terrain;

import com.enums.POIStatus;
import com.enums.Point;
import com.enums.SnowConsistency;

public class Summit extends POI{
    private int snow_height = 0;
    private SnowConsistency snow_consistency = SnowConsistency.NONE;

    public Summit(String name, int id, Point location, POIStatus status) {
        super(name, id, location, status);
    }
}
