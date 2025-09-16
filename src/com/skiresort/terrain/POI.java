package com.skiresort.terrain;

import com.skiresort.enums.POIStatus;
import com.skiresort.enums.Point;

public class POI {
    private String name = "";
    private final int id;
    private Point location;
    private POIStatus status = POIStatus.CLOSED;

    public POI(String name, int id, Point location, POIStatus status) {
        this.name = name;
        this.id = id;
        this.location = location;
        this.status = status;
    }
}
