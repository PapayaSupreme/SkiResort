package terrain;

import enums.POIStatus;
import enums.Point;
import enums.TerrainType;

public class Restaurant extends POI implements Terrain, Worksite {

    public Restaurant(long id, String name, Point location, SkiArea skiArea, POIStatus status) {
        super(id, name, location, skiArea, status);
    }

    @Override
    public TerrainType getTerrainType(){
        return TerrainType.Restaurant;
    }

    @Override
    public TerrainType getWorksiteType() {
        return TerrainType.Restaurant;
    }

    @Override
    public String toString() {
        return "Restaurant - " + super.toString();
    }
}
