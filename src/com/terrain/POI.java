package com.terrain;

import com.enums.POIStatus;
import com.enums.Point;
import com.people.Worksite;

public class POI {
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

    public String getName() { return this.name; }
    public int getId() { return this.id; }
    public Point getLocation() { return this.location; }
    public POIStatus getStatus() { return this.status; }
    public SkiArea getSkiArea() { return skiArea; }

    public void setName(String name) { this.name = name; }
    public void setStatus(POIStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "name=" + this.name + ", id=" + this.id+ ", location="
                + this.location.toString() + ", status=" + this.status
                + ", ski area=" + this.skiArea.getName();
    }
}
