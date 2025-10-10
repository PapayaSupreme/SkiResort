package terrain;

import enums.Point;
import enums.SnowConsistency;

public class Summit extends POI{
    private int snowHeight = 0;
    private SnowConsistency snowConsistency = SnowConsistency.NONE;

    public Summit(String name, Point location, SkiArea skiArea) {
        super(name, location, skiArea);
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
