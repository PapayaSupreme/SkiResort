package com.terrain;

import com.enums.POIStatus;
import com.enums.Point;
import com.enums.SnowConsistency;

public class Summit extends POI{
    private int snowHeight = 0;
    private SnowConsistency snowConsistency = SnowConsistency.NONE;

    public Summit(String name, int id, Point location, POIStatus status) {
        super(name, id, location, status);
    }
}
