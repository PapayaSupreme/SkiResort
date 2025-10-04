package com.terrain;

import com.enums.POIStatus;
import com.enums.Point;
import com.people.Worksite;

public class POI implements Worksite {
    private String name = "";
    private final int id;
    private final Point location;
    private POIStatus status = POIStatus.CLOSED;
    private final SkiArea skiArea;

    public POI(String name, int id, Point location, SkiArea skiArea) {
        this.name = name;
        this.id = id;
        this.location = location;
        this.skiArea = skiArea;
    }

    @Override  public String getName() { return this.name; }
    @Override  public int getId() { return this.id; }
    public Point getLocation() { return this.location; }
    public POIStatus getStatus() { return this.status; }
    public SkiArea getSkiArea() { return skiArea; }

    @Override  public void setName(String name) { this.name = name; }
    public void setStatus(POIStatus status) { this.status = status; }
}
