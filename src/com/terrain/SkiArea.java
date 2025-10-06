package com.terrain;

import com.enums.OpeningHours;
import com.enums.Point;
import com.people.Worksite;

import java.util.ArrayList;
import java.util.List;

public class SkiArea implements Worksite {
    private String name;
    private final int id;
    private boolean functioning = true;
    private Point highest;
    private Point lowest;
    private double perimeter;
    private OpeningHours openingHours;

    private final List<Lift> lifts = new ArrayList<>();
    private final List<Slope> slopes = new ArrayList<>();
    private final List<POI> pois = new ArrayList<>();


    public SkiArea(String name, int id, OpeningHours openingHours) {
        this.name = name;
        this.id = id;
        this.openingHours = openingHours;
    }

    public SkiArea(String name, int id, Point highest, Point lowest,
                   double perimeter, OpeningHours openingHours) {
        this.name = name;
        this.id = id;
        this.highest = highest;
        this.lowest = lowest;
        this.perimeter = perimeter;
        this.openingHours = openingHours;
    }

    @Override public String getName() { return this.name; }
    @Override public int getId() { return this.id; }
    public boolean isFunctioning() { return this.functioning; }
    public Point getHighest() { return this.highest; }
    public Point getLowest() { return this.lowest; }
    public double getPerimeter() { return this.perimeter; }
    public OpeningHours getOpeningHours() { return this.openingHours; }
    public List<Lift> getLifts() { return this.lifts; }
    public List<Slope> getSlopes() { return this.slopes; }
    public List<POI> getPois() { return this.pois; }

    @Override public void setName(String name) { this.name = name; }
    public void setFunctioning(boolean functioning) { this.functioning = functioning; }
    public void setHighest(Point highest) { this.highest = highest; }
    public void setLowest(Point lowest) { this.lowest = lowest; }
    public void setPerimeter(double perimeter) { this.perimeter = perimeter; }
    public void setOpeningHours(OpeningHours openingHours) { this.openingHours = openingHours; }
    public void addLift(Lift lift) { this.lifts.add(lift); }
    public void addSlope(Slope slope) { this.slopes.add(slope); }
    public void addPoi(POI poi) { this.pois.add(poi); }

    public boolean removeLift(Lift lift) { return this.lifts.remove(lift); }
    public boolean removeSlope(Slope slope) { return this.slopes.remove(slope); }
    public boolean removePOI(POI poi) { return this.pois.remove(poi); }

    public void updateHeight() {
        //TODO: for highest and lowest, iterate through
        // summits and slopes at their extremities
        // and so length follow too
    }
}
