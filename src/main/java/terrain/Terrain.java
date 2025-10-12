package terrain;

import enums.TerrainType;

public interface Terrain {
    public long getId();
    public String getName();
    public TerrainType getTerrainType();
}
