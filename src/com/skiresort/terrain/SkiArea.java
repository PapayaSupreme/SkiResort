package com.skiresort.terrain;

import com.skiresort.enums.*;

import java.util.ArrayList;
import java.util.List;

public class SkiArea {
    private String name = "";
    private final int id;
    private boolean functioning = true;
    private Point highest;
    private Point lowest;
    private final String domain;
    private double length;
    private OpeningHours opening_hours;

    private final List<Lift> lifts = new ArrayList<>();
    private final List<Slope> slopes = new ArrayList<>();
    private final List<POI> pois = new ArrayList<>();


    public SkiArea(String name, int id, String domain, OpeningHours opening_hours) {
        this.name = name;
        this.id = id;
        this.domain = domain;
        this.opening_hours = opening_hours;
    }

    public SkiArea(String name, int id, Point highest, Point lowest,
                   String domain, double length, OpeningHours opening_hours) {
        this.name = name;
        this.id = id;
        this.highest = highest;
        this.lowest = lowest;
        this.domain = domain;
        this.length = length;
        this.opening_hours = opening_hours;
    }


    public String getName() { return this.name; }
    public int getId() { return id; }
    public boolean isFunctioning() { return functioning; }
    public Point getHighest() { return highest; }
    public Point getLowest() { return lowest; }
    public String getDomain() { return domain; }
    public double getLength() { return length; }
    public OpeningHours getOpening_hours() { return opening_hours; }
    public List<Lift> getLifts() { return lifts; }
    public List<Slope> getSlopes() { return slopes; }
    public List<POI> getPois() { return pois; }

    public void setName(String name) { this.name = name; }
    public void setFunctioning(boolean functioning) { this.functioning = functioning; }
    public void setHighest(Point highest) { this.highest = highest; }
    public void setLowest(Point lowest) { this.lowest = lowest; }
    public void setLength(double length) { this.length = length; }
    public void setOpening_hours(OpeningHours opening_hours) { this.opening_hours = opening_hours; }
    public void addLift(Lift l) { lifts.add(l); }
    public void addSlope(Slope s) { slopes.add(s); }
    public void addPOI(POI p) { pois.add(p); }


    public void updateHeight() {
        //TODO: for highest and lowest, iterate through
        // summits and slopes at their extremities
        // and so length follow too
    }
}
