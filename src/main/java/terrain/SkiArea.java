package terrain;

import enums.OpeningHours;
import enums.TerrainType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SkiArea implements Terrain, Worksite {
    private final String name;
    private final UUID publicId; //TODO: make it so that nothing is editable except in admit mode, and it shows changes on db reload
    private final long id;
    private final int up;
    private final int down;
    private boolean functioning;
    private final OpeningHours openingHours;

    private final List<Lift> lifts = new ArrayList<>();
    private final List<Slope> slopes = new ArrayList<>();
    private final List<RescuePoint> rescuePoints = new ArrayList<>();
    private final List<Restaurant> restaurants = new ArrayList<>();
    private final List<Summit> summits = new ArrayList<>();

    public SkiArea(long id, UUID publicId, String name, int up, int down,
                   OpeningHours openingHours, boolean functioning) {
        this.id = id;
        this.name = name;
        this.up = up;
        this.down = down;
        this.openingHours = openingHours;
        this.publicId = publicId;
        this.functioning = functioning;
    }

    @Override public String getName() { return this.name; }
    @Override public long getId() { return this.id; }
    public boolean isFunctioning() { return this.functioning; }
    public int getUp() { return this.up; }
    public int getDown() { return this.down; }
    public OpeningHours getOpeningHours() { return this.openingHours; }
    public List<Lift> getLifts() { return List.copyOf(this.lifts); }
    public List<Slope> getSlopes() { return List.copyOf(this.slopes); }
    public List<RescuePoint> getRescuePoints() { return List.copyOf(this.rescuePoints); }
    public List<Restaurant> getRestaurants() { return List.copyOf(this.restaurants); }
    public List<Summit> getSummits() { return List.copyOf(this.summits); }

    public void setFunctioning(boolean functioning) { this.functioning = functioning; }
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
    public TerrainType getTerrainType(){
        return TerrainType.SkiArea;
    }

    @Override
    public TerrainType getWorksiteType() {
        return TerrainType.SkiArea;
    }

    @Override
    public String toString() {
        return "Ski area: name=" + this.name + ", id=" + this.id+ ", up="
                + this.up + ", down=" + this.down
                + ", opening hours=" + this.openingHours;
    }
}
