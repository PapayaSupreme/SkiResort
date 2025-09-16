package com.terrain;

import com.enums.OpeningHours;
import com.enums.Point;
import com.peoples.Worksite;

import java.util.ArrayList;
import java.util.List;

public class SkiArea implements Worksite {
    private String name = "";
    private final int id;
    private boolean functioning = true;
    private Point highest;
    private Point lowest;
    private final String domain;
    private double length;
    private OpeningHours openingHours;

    private final List<Lift> lifts = new ArrayList<>();
    private final List<Slope> slopes = new ArrayList<>();
    private final List<POI> pois = new ArrayList<>();


    public SkiArea(String name, int id, String domain, OpeningHours openingHours) {
        this.name = name;
        this.id = id;
        this.domain = domain;
        this.openingHours = openingHours;
    }

    public SkiArea(String name, int id, Point highest, Point lowest,
                   String domain, double length, OpeningHours openingHours) {
        this.name = name;
        this.id = id;
        this.highest = highest;
        this.lowest = lowest;
        this.domain = domain;
        this.length = length;
        this.openingHours = openingHours;
    }

    @Override
    public String getName() { return this.name; }
    @Override
    public int getId() { return id; }
    public boolean isFunctioning() { return functioning; }
    public Point getHighest() { return highest; }
    public Point getLowest() { return lowest; }
    public String getDomain() { return domain; }
    public double getLength() { return length; }
    public OpeningHours getOpeningHours() { return openingHours; }
    public List<Lift> getLifts() { return lifts; }
    public List<Slope> getSlopes() { return slopes; }
    public List<POI> getPois() { return pois; }

    public void setName(String name) { this.name = name; }
    public void setFunctioning(boolean functioning) { this.functioning = functioning; }
    public void setHighest(Point highest) { this.highest = highest; }
    public void setLowest(Point lowest) { this.lowest = lowest; }
    public void setLength(double length) { this.length = length; }
    public void setOpeningHours(OpeningHours openingHours) { this.openingHours = openingHours; }
    public void addLift(Lift l) { lifts.add(l); }
    public void addSlope(Slope s) { slopes.add(s); }
    public void addPOI(POI p) { pois.add(p); }


    public void updateHeight() {
        //TODO: for highest and lowest, iterate through
        // summits and slopes at their extremities
        // and so length follow too
    }
}
