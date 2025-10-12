package factory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Two-phase loader:
 * Phase 1: bulk fetch rows into lightweight Row records (no cross refs).
 * Phase 2: wire references (IDs -> objects), then build an immutable ResortSnapshot.
 */
public record ResortLoader(DataSource ds) {

    // -------- Public API

    public static final class ResortSnapshot {
        public final Map<Long, Object> skiAreas;     // id -> SkiArea
        public final Map<Long, Object> lifts;        // worksite.id -> Lift
        public final Map<Long, Object> slopes;       // slope.id -> Slope
        public final Map<Long, Object> restaurants;  // worksite.id -> Restaurant
        public final Map<Long, Object> rescuePoints; // worksite.id -> RescuePoint
        public final Map<Long, Object> summits;      // poi.id -> Summit
        public final OffsetDateTime loadedAt;

        private ResortSnapshot(
                Map<Long, Object> skiAreas,
                Map<Long, Object> lifts,
                Map<Long, Object> slopes,
                Map<Long, Object> restaurants,
                Map<Long, Object> rescuePoints,
                Map<Long, Object> summits
        ) {
            this.skiAreas = Collections.unmodifiableMap(new LinkedHashMap<>(skiAreas));
            this.lifts = Collections.unmodifiableMap(new LinkedHashMap<>(lifts));
            this.slopes = Collections.unmodifiableMap(new LinkedHashMap<>(slopes));
            this.restaurants = Collections.unmodifiableMap(new LinkedHashMap<>(restaurants));
            this.rescuePoints = Collections.unmodifiableMap(new LinkedHashMap<>(rescuePoints));
            this.summits = Collections.unmodifiableMap(new LinkedHashMap<>(summits));
            this.loadedAt = OffsetDateTime.now();
        }
    }

    /**
     * Pluggable constructors. Return your real domain objects from these.
     */
    public interface Mappers {
        Object makeSkiArea(SkiAreaRow ws, SkiAreaDetailRow detail);

        Object makeSlope(SlopeRow row, Object skiArea /* SkiArea */);

        Object makeLift(LiftRow row, Object skiArea /* SkiArea */,
                        Object upSlope /* Slope or null */, Object downSlope /* Slope or null */);

        Object makeRestaurant(RestaurantRow row, Object skiArea /* SkiArea */);

        Object makeRescuePoint(RescuePointRow row, Object skiArea /* SkiArea */);

        Object makeSummit(SummitRow row, Object skiArea /* SkiArea */);
    }

    public ResortLoader(DataSource ds) {
        this.ds = Objects.requireNonNull(ds);
    }

    /**
     * Loads a consistent snapshot (single txn), builds & returns the wired model.
     */
    public ResortSnapshot load(Mappers mappers) throws SQLException {
        try (Connection c = ds.getConnection()) {
            c.setAutoCommit(false);
            c.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            // ---- Phase 1: bulk fetch rows
            Map<Long, WorksiteRow> worksites = fetchWorksites(c);
            Map<Long, SkiAreaDetailRow> skiAreaDetails = fetchSkiArea(c);
            Map<Long, SlopeRow> slopeRows = fetchSlopes(c);
            Map<Long, LiftRow> liftRows = fetchLifts(c);

            Map<Long, PoiRow> poiRows = fetchPois(c);
            Map<Long, RestaurantLinkRow> restaurantLinks = fetchRestaurants(c);
            Map<Long, RescuePointLinkRow> rescueLinks = fetchRescuePoints(c);
            Map<Long, SummitLinkRow> summitLinks = fetchSummits(c);

            // ---- Phase 2: construct objects & wire references

            // 2.1 SkiAreas (worksite.id is the key)
            Map<Long, Object> skiAreas = new LinkedHashMap<>();
            for (var e : skiAreaDetails.entrySet()) {
                long id = e.getKey();
                var ws = worksites.get(id);
                if (ws == null || !"SKI_AREA".equals(ws.worksiteType)) continue;
                var area = mappers.makeSkiArea(new SkiAreaRow(ws), e.getValue());
                skiAreas.put(id, area);
            }

            // 2.2 Slopes (independent; keyed by slope.id). Need SkiArea by FK.
            Map<Long, Object> slopes = new LinkedHashMap<>();
            for (var e : slopeRows.entrySet()) {
                var row = e.getValue();
                var area = skiAreas.get(row.skiAreaId);
                if (area == null) continue; // skip orphan
                var slope = mappers.makeSlope(row, area);
                slopes.put(row.id, slope);
            }

            // 2.3 Lifts (worksite.id key). Need SkiArea + optional up/down slope refs.
            Map<Long, Object> lifts = new LinkedHashMap<>();
            for (var e : liftRows.entrySet()) {
                var row = e.getValue();
                var ws = worksites.get(row.id);
                if (ws == null || !"LIFT".equals(ws.worksiteType)) continue;
                var area = skiAreas.get(row.skiAreaId);
                var up = row.upSlopeId != null ? slopes.get(row.upSlopeId) : null;
                var down = row.downSlopeId != null ? slopes.get(row.downSlopeId) : null;
                var lift = mappers.makeLift(row, area, up, down);
                lifts.put(row.id, lift);
            }

            // 2.4 Restaurants (POI + Worksite). We expose a simplified RestaurantRow with merged data.
            Map<Long, Object> restaurants = new LinkedHashMap<>();
            for (var e : restaurantLinks.entrySet()) {
                long wsId = e.getKey();                // worksite.id (and restaurant.id)
                var link = e.getValue();              // poi_id + ws id
                var poi = poiRows.get(link.poiId);
                var ws = worksites.get(wsId);
                if (poi == null || ws == null) continue;
                var area = skiAreas.get(poi.skiAreaId);
                var row = RestaurantRow.from(ws, poi);
                var obj = mappers.makeRestaurant(row, area);
                restaurants.put(wsId, obj);
            }

            // 2.5 Rescue Points
            Map<Long, Object> rescuePoints = new LinkedHashMap<>();
            for (var e : rescueLinks.entrySet()) {
                long wsId = e.getKey();
                var link = e.getValue();
                var poi = poiRows.get(link.poiId);
                var ws = worksites.get(wsId);
                if (poi == null || ws == null) continue;
                var area = skiAreas.get(poi.skiAreaId);
                var row = RescuePointRow.from(ws, poi, link.warning);
                var obj = mappers.makeRescuePoint(row, area);
                rescuePoints.put(wsId, obj);
            }

            // 2.6 Summits (POI only)
            Map<Long, Object> summits = new LinkedHashMap<>();
            for (var e : summitLinks.entrySet()) {
                long poiId = e.getKey();
                var link = e.getValue();
                var poi = poiRows.get(poiId);
                if (poi == null) continue;
                var area = skiAreas.get(poi.skiAreaId);
                var row = SummitRow.from(poi, link);
                var obj = mappers.makeSummit(row, area);
                summits.put(poiId, obj);
            }

            c.commit();
            return new ResortSnapshot(skiAreas, lifts, slopes, restaurants, rescuePoints, summits);
        }
    }

    // -------- Phase 1: ROW FETCHERS (SQL strictly matches init.sql NOTHING ELSE NO)

    private Map<Long, WorksiteRow> fetchWorksites(Connection c) throws SQLException {
        String sql = """
          SELECT id, worksite_name, worksite_type, opening_hours
          FROM worksite
        """;
        Map<Long, WorksiteRow> out = new LinkedHashMap<>();
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                long id = rs.getLong("id");
                out.put(id, new WorksiteRow(
                        id,
                        rs.getString("worksite_name"),
                        rs.getString("worksite_type"),
                        rs.getString("opening_hours") // JSONB -> String; you can parse later if needed
                ));
            }
        }
        return out;
    }

    private Map<Long, SkiAreaDetailRow> fetchSkiArea(Connection c) throws SQLException {
        String sql = """
          SELECT id, public_id, elevation_min_m, elevation_max_m, functioning, opening_hours
          FROM ski_area
        """;
        Map<Long, SkiAreaDetailRow> out = new LinkedHashMap<>();
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                long id = rs.getLong("id");
                out.put(id, new SkiAreaDetailRow(
                        id,
                        rs.getString("public_id"),
                        getNullableInt(rs, "elevation_min_m"),
                        getNullableInt(rs, "elevation_max_m"),
                        rs.getBoolean("functioning"),
                        rs.getString("opening_hours")
                ));
            }
        }
        return out;
    }

    private Map<Long, SlopeRow> fetchSlopes(Connection c) throws SQLException {
        String sql = """
          SELECT id, ski_area_id, public_id, name, difficulty, slope_type,
                 length_m, avg_width_m, groomed, snowmaking,
                 up_x, up_y, up_z_m, down_x, down_y, down_z_m,
                 opening_hours
          FROM slope
        """;
        Map<Long, SlopeRow> out = new LinkedHashMap<>();
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                long id = rs.getLong("id");
                out.put(id, new SlopeRow(
                        id,
                        rs.getLong("ski_area_id"),
                        rs.getString("public_id"),
                        rs.getString("name"),
                        rs.getString("difficulty"),
                        rs.getString("slope_type"),
                        getNullableInt(rs, "length_m"),
                        getNullableInt(rs, "avg_width_m"),
                        rs.getBoolean("groomed"),
                        rs.getBoolean("snowmaking"),
                        getNullableDouble(rs, "up_x"),
                        getNullableDouble(rs, "up_y"),
                        getNullableDouble(rs, "up_z_m"),
                        getNullableDouble(rs, "down_x"),
                        getNullableDouble(rs, "down_y"),
                        getNullableDouble(rs, "down_z_m"),
                        rs.getString("opening_hours")
                ));
            }
        }
        return out;
    }

    // 2) Query: join worksite to get the name
    private Map<Long, LiftRow> fetchLifts(Connection c) throws SQLException {
        String sql = """
      SELECT l.id, w.worksite_name AS name, l.ski_area_id, l.public_id, l.lift_type, l.lift_status,
             l.length_m, l.vertical_rise_m, l.speed_mps,
             l.up_x, l.up_y, l.up_z_m, l.down_x, l.down_y, l.down_z_m,
             l.up_slope, l.down_slope, l.opening_hours
      FROM lift l
      JOIN worksite w ON w.id = l.id
    """;
        Map<Long, LiftRow> out = new LinkedHashMap<>();
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                long id = rs.getLong("id");
                out.put(id, new LiftRow(
                        id,
                        rs.getString("name"),
                        rs.getLong("ski_area_id"),
                        rs.getString("public_id"),
                        rs.getString("lift_type"),
                        rs.getString("lift_status"),
                        getNullableInt(rs, "length_m"),
                        getNullableInt(rs, "vertical_rise_m"),
                        getNullableDouble(rs, "speed_mps"),
                        getNullableDouble(rs, "up_x"),
                        getNullableDouble(rs, "up_y"),
                        getNullableDouble(rs, "up_z_m"),
                        getNullableDouble(rs, "down_x"),
                        getNullableDouble(rs, "down_y"),
                        getNullableDouble(rs, "down_z_m"),
                        getNullableLong(rs, "up_slope"),
                        getNullableLong(rs, "down_slope"),
                        rs.getString("opening_hours")
                ));
            }
        }
        return out;
    }


    private Map<Long, PoiRow> fetchPois(Connection c) throws SQLException {
        String sql = """
          SELECT id, ski_area_id, worksite_id, name, x, y, z_m, status, public_id
          FROM poi
          WHERE is_deleted = FALSE
        """;
        Map<Long, PoiRow> out = new LinkedHashMap<>();
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                long id = rs.getLong("id");
                out.put(id, new PoiRow(
                        id,
                        rs.getLong("ski_area_id"),
                        getNullableLong(rs, "worksite_id"),
                        rs.getString("name"),
                        getNullableDouble(rs, "x"),
                        getNullableDouble(rs, "y"),
                        getNullableDouble(rs, "z_m"),
                        rs.getString("status"),
                        rs.getString("public_id")
                ));
            }
        }
        return out;
    }

    private Map<Long, RestaurantLinkRow> fetchRestaurants(Connection c) throws SQLException {
        String sql = "SELECT id, poi_id FROM restaurant";
        Map<Long, RestaurantLinkRow> out = new LinkedHashMap<>();
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                long id = rs.getLong("id"); // worksite.id
                out.put(id, new RestaurantLinkRow(id, rs.getLong("poi_id")));
            }
        }
        return out;
    }

    private Map<Long, RescuePointLinkRow> fetchRescuePoints(Connection c) throws SQLException {
        String sql = "SELECT id, poi_id, warning FROM rescue_point";
        Map<Long, RescuePointLinkRow> out = new LinkedHashMap<>();
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                long id = rs.getLong("id"); // worksite.id
                out.put(id, new RescuePointLinkRow(id, rs.getLong("poi_id"), rs.getBoolean("warning")));
            }
        }
        return out;
    }

    private Map<Long, SummitLinkRow> fetchSummits(Connection c) throws SQLException {
        String sql = "SELECT poi_id, snow_height_cm, snow_consistency FROM summit";
        Map<Long, SummitLinkRow> out = new LinkedHashMap<>();
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                long poiId = rs.getLong("poi_id");
                out.put(poiId, new SummitLinkRow(poiId, rs.getInt("snow_height_cm"), rs.getString("snow_consistency")));
            }
        }
        return out;
    }

    // -------- Row records (Phase 1 containers)

    public record WorksiteRow(long id, String name, String worksiteType, String openingHoursJson) {
    }

    public static final class SkiAreaRow {
        public final long id;
        public final String name;
        public final String openingHoursJson;

        public SkiAreaRow(WorksiteRow ws) {
            this.id = ws.id;
            this.name = ws.name;
            this.openingHoursJson = ws.openingHoursJson;
        }
    }

    public record SkiAreaDetailRow(long id, String publicId, Integer elevationMin, Integer elevationMax,
                                   boolean functioning, String openingHoursJson) {
    }

    public record SlopeRow(long id, long skiAreaId, String publicId, String name, String difficulty, String slopeType,
                           Integer lengthM, Integer avgWidthM, boolean groomed, boolean snowmaking, Double upX,
                           Double upY, Double upZ, Double downX, Double downY, Double downZ, String openingHoursJson) {
    }

    public record LiftRow(long id, String name, long skiAreaId, String publicId, String liftType, String liftStatus,
                          Integer lengthM, Integer verticalRiseM, Double speedMps, Double upX, Double upY, Double upZ,
                          Double downX, Double downY, Double downZ, Long upSlopeId, Long downSlopeId,
                          String openingHoursJson) {
    }

    public record PoiRow(long id, long skiAreaId, Long worksiteId, String name, Double x, Double y, Double z,
                         String status, String publicId) {
    }

    public record RestaurantLinkRow(long id, long poiId) {
    }

    public record RescuePointLinkRow(long id, long poiId, boolean warning) {
    }

    public record SummitLinkRow(long poiId, int snowHeightCm, String snowConsistency) {
    }

        public record RestaurantRow(long worksiteId, String name, long poiId, long skiAreaId, Double x, Double y, Double z,
                                    String status) {

        static RestaurantRow from(WorksiteRow ws, PoiRow poi) {
                return new RestaurantRow(ws.id, ws.name, poi.id, poi.skiAreaId, poi.x, poi.y, poi.z, poi.status);
            }
        }


    public record RescuePointRow(long worksiteId, String name, long poiId, long skiAreaId, Double x, Double y, Double z,
                                 String status, boolean warning) {

        static RescuePointRow from(WorksiteRow ws, PoiRow poi, boolean warning) {
                return new RescuePointRow(ws.id, ws.name, poi.id, poi.skiAreaId, poi.x, poi.y, poi.z, poi.status, warning);
            }
        }


    public record SummitRow(long poiId, String name, long skiAreaId, Double x, Double y, Double z, int snowHeightCm,
                            String snowConsistency) {

        static SummitRow from(PoiRow poi, SummitLinkRow link) {
                return new SummitRow(poi.id, poi.name, poi.skiAreaId, poi.x, poi.y, poi.z, link.snowHeightCm, link.snowConsistency);
            }
        }

    // -------- utils

    private static Integer getNullableInt(ResultSet rs, String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? null : v;
    }

    private static Long getNullableLong(ResultSet rs, String col) throws SQLException {
        long v = rs.getLong(col);
        return rs.wasNull() ? null : v;
    }

    private static Double getNullableDouble(ResultSet rs, String col) throws SQLException {
        double v = rs.getDouble(col);
        return rs.wasNull() ? null : v;
    }
}

