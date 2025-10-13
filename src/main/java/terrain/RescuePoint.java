package terrain;

import enums.POIStatus;
import enums.Point;
import enums.TerrainType;
import enums.WorksiteType;

public class RescuePoint extends POI implements Terrain, Worksite {
    private boolean warning ;

    public RescuePoint(long id, String name, Point location, SkiArea skiArea, POIStatus status, boolean warning) {
        super(id, name, location, skiArea, status);
        this.warning = warning;
    }

    public boolean isWarning() { return this.warning; }

    public void setWarning(boolean warning) { this.warning = warning; }

    @Override
    public TerrainType getTerrainType(){
        return TerrainType.RescuePoint;
    }

    @Override
    public TerrainType getWorksiteType() {
        return TerrainType.RescuePoint;
    }

    @Override
    public String toString() {
        return "Rescue Point: warning=" + this.warning + ", " + super.toString();
    }
}
