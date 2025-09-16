package com.terrain;

import com.enums.POIStatus;
import com.enums.Point;
import com.peoples.Worksite;

public class POI implements Worksite {
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

    @Override
    public String getName() { return this.name; }
    @Override
    public int getId() { return this.id; }
}
