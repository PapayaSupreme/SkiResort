package com.terrain;

import com.enums.POIStatus;
import com.enums.Point;
import com.peoples.Worksite;

public class POI implements Worksite {
    private String name = "";
    private final int id;
    private Point location;
    private POIStatus status = POIStatus.CLOSED;

    public POI(String name, int id, Point location) {
        this.name = name;
        this.id = id;
        this.location = location;
    }

    @Override
    public String getName() { return this.name; }
    @Override
    public int getId() { return this.id; }
    public Point getLocation() { return this.location; }
    public POIStatus getStatus() { return this.status; }

    @Override
    public void setName(String name) { this.name = name; }
    public void setLocation(Point location) { this.location = location; }
    public void setStatus(POIStatus status) { this.status = status; }
}
