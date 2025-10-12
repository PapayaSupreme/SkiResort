package terrain;

import java.util.Map;

public final class Resort {
    private final String resortName;
    private final Map<Long, SkiArea> skiAreas;
    private final Map<Long, Slope> slopes;
    private final Map<Long, Lift> lifts;
    private final Map<Long, Restaurant> restaurants;
    private final Map<Long, RescuePoint> rescuePoints;
    private final Map<Long, Summit> summits;

    public Resort(String resortName, Map<Long, ?> sa, Map<Long, ?> sl, Map<Long, ?> lf,
                  Map<Long, ?> re, Map<Long, ?> rp, Map<Long, ?> su) {
        this.resortName = resortName;
        this.skiAreas     = Map.copyOf((Map<Long, SkiArea>) sa);
        this.slopes       = Map.copyOf((Map<Long, Slope>) sl);
        this.lifts        = Map.copyOf((Map<Long, Lift>) lf);
        this.restaurants  = Map.copyOf((Map<Long, Restaurant>) re);
        this.rescuePoints = Map.copyOf((Map<Long, RescuePoint>) rp);
        this.summits      = Map.copyOf((Map<Long, Summit>) su);
    }

    public String getResortName(){ return this.resortName; }
    public Map<Long, SkiArea> getSkiAreas(){ return this.skiAreas; }
    public Map<Long, Slope>   getSlopes(){ return this.slopes; }
    public Map<Long, Lift>    getLifts(){ return this.lifts; }
    public Map<Long, Restaurant> getRestaurants(){ return this.restaurants; }
    public Map<Long, RescuePoint> getRescuePoints(){ return this.rescuePoints; }
    public Map<Long, Summit>  getSummits(){ return this.summits; }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder("===== " + this.resortName + " =====\n\n === Ski Areas ===\n");
        for (SkiArea skiArea: this.skiAreas.values()){
            s.append(skiArea.toString()).append("\n");
        }
        s.append("\n=== Slopes ===\n");
        for (Slope slope: this.slopes.values()){
            s.append(slope.toString()).append("\n");
        }
        s.append("\n=== Lifts ===\n");
        for (Lift lift: this.lifts.values()){
            s.append(lift.toString()).append("\n");
        }
        s.append("\n=== Restaurants ===\n");
        for (Restaurant restaurant: this.restaurants.values()){
            s.append(restaurant.toString()).append("\n");
        }
        s.append("\n=== Rescue Points ===\n");
        for (RescuePoint rescuePoint: this.rescuePoints.values()){
            s.append(rescuePoint.toString()).append("\n");
        }
        s.append("\n=== Summits ===\n");
        for (Summit summit: this.summits.values()){
            s.append(summit.toString()).append("\n");
        }
        return s.toString();
    }
}

