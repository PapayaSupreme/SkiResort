package terrain;

import java.time.LocalDate;
import java.util.*;

import static utils.ResortUtils.norm;
import static utils.ResortUtils.runTimer;

public final class Resort {
    private final String resortName;
    private final Map<Long, SkiArea> skiAreas;
    private final Map<Long, Slope> slopes;
    private final Map<Long, Lift> lifts;
    private final Map<Long, Restaurant> restaurants;
    private final Map<Long, RescuePoint> rescuePoints;
    private final Map<Long, Summit> summits;
    private final Map<String, List<Long>> idFromName = new HashMap<>();
    private static final Map<Long, Terrain> terrainIndex = new HashMap<>();

    private static final LocalDate SEASON_START = LocalDate.of(2025, 1, 1); //TODO; obv temporary, make it not hardcoded
    private static final LocalDate SEASON_END = LocalDate.of(2026, 12, 31);

    public Resort(String resortName, Map<Long, ?> sa, Map<Long, ?> sl, Map<Long, ?> lf,
                  Map<Long, ?> re, Map<Long, ?> rp, Map<Long, ?> su) {
        this.resortName = resortName;
        this.skiAreas     = Map.copyOf((Map<Long, SkiArea>) sa);
        this.slopes       = Map.copyOf((Map<Long, Slope>) sl);
        this.lifts        = Map.copyOf((Map<Long, Lift>) lf);
        this.restaurants  = Map.copyOf((Map<Long, Restaurant>) re);
        this.rescuePoints = Map.copyOf((Map<Long, RescuePoint>) rp);
        this.summits      = Map.copyOf((Map<Long, Summit>) su);
        buildTerrainIndex();
        buildIdFromNameIndex();
    }

    public String getResortName(){ return this.resortName; }
    public Map<Long, SkiArea> getSkiAreas(){ return Map.copyOf(this.skiAreas); }
    public Map<Long, Slope>   getSlopes(){ return Map.copyOf(this.slopes); }
    public Map<Long, Lift>    getLifts(){ return Map.copyOf(this.lifts); }
    public Map<Long, Restaurant> getRestaurants(){ return Map.copyOf(this.restaurants); }
    public Map<Long, RescuePoint> getRescuePoints(){ return Map.copyOf(this.rescuePoints); }
    public Map<Long, Summit>  getSummits(){ return Map.copyOf(this.summits); }
    public static Map<Long, Terrain> getTerrainIndex() { return Map.copyOf(terrainIndex); }

    public static LocalDate getSeasonStart() { return SEASON_START; }
    public static LocalDate getSeasonEnd() { return SEASON_END; }

    public HashSet<Long> getIdsFromName(String name){
        HashSet<Long> out = new HashSet<>();
        if (name == null) return out;
        String contain = norm(name);
        long t0 = System.nanoTime();
        for (var idFromNameEntry: this.idFromName.entrySet()) {
            if (idFromNameEntry.getKey().contains(contain)){
                out.addAll(idFromNameEntry.getValue());
            }
        }
        long t1 = System.nanoTime();
        runTimer("Terrain name search query", t0, t1);
        return out;
    }



    //once-at-runtime helper, DO NOT USE AFTER OR MAYBE FOR HOT REBUILD
    private void buildIdFromNameIndex() {
        this.idFromName.clear();
        for (var terrainIndexEntry: this.terrainIndex.entrySet()){
            String k = norm(terrainIndexEntry.getValue().getName());
            this.idFromName.computeIfAbsent(k, unused -> new ArrayList<>())
            .add(terrainIndexEntry.getKey());
        }
    }

    private void buildTerrainIndex() {
        this.terrainIndex.clear();
        this.terrainIndex.putAll(this.skiAreas);
        this.terrainIndex.putAll(this.slopes);
        this.terrainIndex.putAll(this.lifts);
        this.terrainIndex.putAll(this.restaurants);
        this.terrainIndex.putAll(this.rescuePoints);
        this.terrainIndex.putAll(this.summits);
    }

    public boolean isInSeasonRange(LocalDate day){
        return SEASON_START.isBefore(day) &&
                SEASON_END.isAfter(day); //TODO; obv temporary, make it not hardcoded
    }

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

