package terrain;

import enums.Point;
import enums.SnowConsistency;


public class Summit extends POI{
    private int snowHeight;
    private SnowConsistency snowConsistency;

    public Summit(long id, String name, Point location, SkiArea skiArea, int snowHeight, SnowConsistency snowConsistency) {
        super(id, name, location, skiArea, enums.POIStatus.OPEN); // summit always open lol
        this.snowHeight = snowHeight;
        this.snowConsistency = snowConsistency;
    }

    public int getSnowHeight() { return this.snowHeight; }
    public SnowConsistency getSnowConsistency() { return this.snowConsistency; }

    public void setSnowHeight(int snowHeight) { this.snowHeight = snowHeight; }
    public void setSnowConsistency(SnowConsistency snowConsistency) { this.snowConsistency = snowConsistency; }

    @Override
    public String toString() {
        return "Summit: snow height=" + this.snowHeight
                + "snow consistency="+ this.snowConsistency
                + ", " + super.toString();
    }
}
