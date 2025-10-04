package com.terrain;

import com.enums.Point;
import com.enums.SnowConsistency;

public class Summit extends POI{
    private int snowHeight = 0;
    private SnowConsistency snowConsistency = SnowConsistency.NONE;

    public Summit(String name, int id, Point location, SkiArea skiArea) {
        super(name, id, location, skiArea);
    }

    public int getSnowHeight() { return snowHeight; }
    public SnowConsistency getSnowConsistency() { return snowConsistency; }

    public void setSnowHeight(int snowHeight) { this.snowHeight = snowHeight; }
    public void setSnowConsistency(SnowConsistency snowConsistency) { this.snowConsistency = snowConsistency; }
}
