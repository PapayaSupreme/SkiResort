package terrain;

import java.util.Map;

public final class Resort {
    private final Map<Long, SkiArea> skiAreas;
    private final Map<Long, Slope> slopes;
    private final Map<Long, Lift> lifts;
    private final Map<Long, Restaurant> restaurants;
    private final Map<Long, RescuePoint> rescuePoints;
    private final Map<Long, Summit> summits;

    public Resort(Map<Long, ?> sa, Map<Long, ?> sl, Map<Long, ?> lf,
                  Map<Long, ?> re, Map<Long, ?> rp, Map<Long, ?> su) {
        this.skiAreas     = Map.copyOf((Map<Long, SkiArea>) sa);
        this.slopes       = Map.copyOf((Map<Long, Slope>) sl);
        this.lifts        = Map.copyOf((Map<Long, Lift>) lf);
        this.restaurants  = Map.copyOf((Map<Long, Restaurant>) re);
        this.rescuePoints = Map.copyOf((Map<Long, RescuePoint>) rp);
        this.summits      = Map.copyOf((Map<Long, Summit>) su);
    }

    // getters…
    public Map<Long, SkiArea> getSkiAreas()     { return skiAreas; }
    public Map<Long, Slope>   getSlopes()       { return slopes; }
    public Map<Long, Lift>    getLifts()        { return lifts; }
    public Map<Long, Restaurant> getRestaurants(){ return restaurants; }
    public Map<Long, RescuePoint> getRescuePoints(){ return rescuePoints; }
    public Map<Long, Summit>  getSummits()      { return summits; }
}

