package terrain;

import enums.TerrainType;

public interface Worksite {
    TerrainType getWorksiteType();
    long getId();
    String getName();
}
