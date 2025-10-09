package com.terrain;

import com.enums.OpeningHours;
import com.enums.Point;
import com.utils.Worksite;
import com.utils.IDGenerator;

import java.util.ArrayList;
import java.util.List;

public class SkiArea implements Worksite {
    private String name;
    private final long id;
    private boolean functioning = true;
    private Point up;
    private Point down;
    private double perimeter;
    private OpeningHours openingHours;

    private final List<Lift> lifts = new ArrayList<>();
    private final List<Slope> slopes = new ArrayList<>();
    private final List<RescuePoint> rescuePoints = new ArrayList<>();
    private final List<Restaurant> restaurants = new ArrayList<>();
    private final List<Summit> summits = new ArrayList<>();

    public SkiArea(String name, Point highest, Point down,
                   double perimeter, OpeningHours openingHours) {
        this.name = name;
        this.id = IDGenerator.generateID();
        this.up = highest;
        this.down = down;
        this.perimeter = perimeter;
        this.openingHours = openingHours;
    }

    @Override public String getName() { return this.name; }
    @Override public long getId() { return this.id; }
    public boolean isFunctioning() { return this.functioning; }
    public Point getUp() { return this.up; }
    public Point getDown() { return this.down; }
    public double getPerimeter() { return this.perimeter; }
    public OpeningHours getOpeningHours() { return this.openingHours; }
    public List<Lift> getLifts() { return List.copyOf(this.lifts); }
    public List<Slope> getSlopes() { return List.copyOf(this.slopes); }
    public List<RescuePoint> getRescuePoints() { return List.copyOf(this.rescuePoints); }
    public List<Restaurant> getRestaurants() { return List.copyOf(this.restaurants); }
    public List<Summit> getSummits() { return List.copyOf(this.summits); }

    @Override public void setName(String name) { this.name = name; }
    public void setFunctioning(boolean functioning) { this.functioning = functioning; }
    public void setUp(Point highest) { this.up = highest; }
    public void setDown(Point down) { this.down = down; }
    public void setPerimeter(double perimeter) { this.perimeter = perimeter; }
    public void setOpeningHours(OpeningHours openingHours) { this.openingHours = openingHours; }
    public void addLift(Lift lift) { this.lifts.add(lift); }
    public void addSlope(Slope slope) { this.slopes.add(slope); }
    public void addRescuePoint(RescuePoint rescuePoint) { this.rescuePoints.add(rescuePoint); }
    public void addRestaurant(Restaurant restaurant) { this.restaurants.add(restaurant); }
    public void addSummit(Summit summit) { this.summits.add(summit); }

    public boolean removeLift(Lift lift) { return this.lifts.remove(lift); }
    public boolean removeSlope(Slope slope) { return this.slopes.remove(slope); }
    public boolean removeRescuePoint(RescuePoint rescuePoint) { return this.rescuePoints.remove(rescuePoint); }
    public boolean removeRestaurant(Restaurant restaurant) { return this.restaurants.remove(restaurant); }
    public boolean removeSummit(Summit summit) { return this.summits.remove(summit); }

    public List<POI> getAllPOIs() {
        List<POI> all = new ArrayList<>();
        all.addAll(this.restaurants);
        all.addAll(this.rescuePoints);
        all.addAll(this.summits);
        return all;
    }

    @Override
    public String getWorksiteType(){
        return "SkiArea";
    }

    @Override
    public String toString() {
        return "Ski area: name=" + this.name + ", id=" + this.id+ ", up="
                + this.up.toString() + ", down=" + this.down
                + ", perimeter=" + this.perimeter + ", opening hours=" + this.openingHours;
    }
}
