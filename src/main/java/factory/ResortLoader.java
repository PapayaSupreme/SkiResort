package factory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Two-phase loader:
 *   Phase 1: bulk fetch rows into lightweight Row records (no cross refs).
 *   Phase 2: wire references (IDs -> objects), then build an immutable ResortSnapshot.
 */
public final class ResortLoader {

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
            this.skiAreas     = Collections.unmodifiableMap(new LinkedHashMap<>(skiAreas));
            this.lifts        = Collections.unmodifiableMap(new LinkedHashMap<>(lifts));
            this.slopes       = Collections.unmodifiableMap(new LinkedHashMap<>(slopes));
            this.restaurants  = Collections.unmodifiableMap(new LinkedHashMap<>(restaurants));
            this.rescuePoints = Collections.unmodifiableMap(new LinkedHashMap<>(rescuePoints));
            this.summits      = Collections.unmodifiableMap(new LinkedHashMap<>(summits));
            this.loadedAt     = OffsetDateTime.now();
        }
    }

    /** Pluggable constructors. Return your real domain objects from these. */
    public interface Mappers {
        Object makeSkiArea(SkiAreaRow ws, SkiAreaDetailRow detail);
        Object makeSlope(SlopeRow row, Object skiArea /* SkiArea */);
        Object makeLift(LiftRow row, Object skiArea /* SkiArea */,
                        Object upSlope /* Slope or null */, Object downSlope /* Slope or null */);
        Object makeRestaurant(RestaurantRow row, Object skiArea /* SkiArea */);
        Object makeRescuePoint(RescuePointRow row, Object skiArea /* SkiArea */);
        Object makeSummit(SummitRow row, Object skiArea /* SkiArea */);
    }

    private final DataSource ds;

    public ResortLoader(DataSource ds) {
        this.ds = Objects.requireNonNull(ds);
    }

    /** Loads a consistent snapshot (single txn), builds & returns the wired model. */
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
                var up   = row.upSlopeId   != null ? slopes.get(row.upSlopeId)   : null;
                var down = row.downSlopeId != null ? slopes.get(row.downSlopeId) : null;
                var lift = mappers.makeLift(row, area, up, down);
                lifts.put(row.id, lift);
            }

            // 2.4 Restaurants (POI + Worksite). We expose a simplified RestaurantRow with merged data.
            Map<Long, Object> restaurants = new LinkedHashMap<>();
            for (var e : restaurantLinks.entrySet()) {
                long wsId = e.getKey();                // worksite.id (and restaurant.id)
                var link  = e.getValue();              // poi_id + ws id
                var poi   = poiRows.get(link.poiId);
                var ws    = worksites.get(wsId);
                if (poi == null || ws == null) continue;
                var area  = skiAreas.get(poi.skiAreaId);
                var row   = RestaurantRow.from(ws, poi);
                var obj   = mappers.makeRestaurant(row, area);
                restaurants.put(wsId, obj);
            }

            // 2.5 Rescue Points
            Map<Long, Object> rescuePoints = new LinkedHashMap<>();
            for (var e : rescueLinks.entrySet()) {
                long wsId = e.getKey();
                var link  = e.getValue();
                var poi   = poiRows.get(link.poiId);
                var ws    = worksites.get(wsId);
                if (poi == null || ws == null) continue;
                var area  = skiAreas.get(poi.skiAreaId);
                var row   = RescuePointRow.from(ws, poi, link.warning);
                var obj   = mappers.makeRescuePoint(row, area);
                rescuePoints.put(wsId, obj);
            }

            // 2.6 Summits (POI only)
            Map<Long, Object> summits = new LinkedHashMap<>();
            for (var e : summitLinks.entrySet()) {
                long poiId = e.getKey();
                var link   = e.getValue();
                var poi    = poiRows.get(poiId);
                if (poi == null) continue;
                var area   = skiAreas.get(poi.skiAreaId);
                var row    = SummitRow.from(poi, link);
                var obj    = mappers.makeSummit(row, area);
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

    public static final class WorksiteRow {
        public final long id;
        public final String name;
        public final String worksiteType; // SKI_AREA, LIFT, RESTAURANT, RESCUE_POINT
        public final String openingHoursJson;
        public WorksiteRow(long id, String name, String worksiteType, String openingHoursJson) {
            this.id = id; this.name = name; this.worksiteType = worksiteType; this.openingHoursJson = openingHoursJson;
        }
    }
    /** Exposed view for SkiArea mapper: includes Worksite name/type + detail row. */
    public static final class SkiAreaRow {
        public final long id;
        public final String name;
        public final String openingHoursJson;
        public SkiAreaRow(WorksiteRow ws) {
            this.id = ws.id; this.name = ws.name; this.openingHoursJson = ws.openingHoursJson;
        }
    }
    public static final class SkiAreaDetailRow {
        public final long id;
        public final String publicId;
        public final Integer elevationMin;
        public final Integer elevationMax;
        public final boolean functioning;
        public final String openingHoursJson;
        public SkiAreaDetailRow(long id, String publicId, Integer elevationMin, Integer elevationMax, boolean functioning, String openingHoursJson) {
            this.id = id; this.publicId = publicId; this.elevationMin = elevationMin; this.elevationMax = elevationMax; this.functioning = functioning; this.openingHoursJson = openingHoursJson;
        }
    }
    public static final class SlopeRow {
        public final long id;
        public final long skiAreaId;
        public final String publicId;
        public final String name;
        public final String difficulty; // GREEN/BLUE/RED/BLACK
        public final String slopeType;  // PISTE/...
        public final Integer lengthM;
        public final Integer avgWidthM;
        public final boolean groomed;
        public final boolean snowmaking;
        public final Double upX, upY, upZ;
        public final Double downX, downY, downZ;
        public final String openingHoursJson;
        public SlopeRow(long id, long skiAreaId, String publicId, String name, String difficulty, String slopeType,
                        Integer lengthM, Integer avgWidthM, boolean groomed, boolean snowmaking,
                        Double upX, Double upY, Double upZ, Double downX, Double downY, Double downZ,
                        String openingHoursJson) {
            this.id = id; this.skiAreaId = skiAreaId; this.publicId = publicId; this.name = name; this.difficulty = difficulty; this.slopeType = slopeType;
            this.lengthM = lengthM; this.avgWidthM = avgWidthM; this.groomed = groomed; this.snowmaking = snowmaking;
            this.upX = upX; this.upY = upY; this.upZ = upZ; this.downX = downX; this.downY = downY; this.downZ = downZ;
            this.openingHoursJson = openingHoursJson;
        }
    }
    public static final class LiftRow {
        public final long id;              // worksite.id
        public final String name;
        public final long skiAreaId;
        public final String publicId;
        public final String liftType;
        public final String liftStatus;
        public final Integer lengthM;
        public final Integer verticalRiseM;
        public final Double speedMps;
        public final Double upX, upY, upZ;
        public final Double downX, downY, downZ;
        public final Long upSlopeId, downSlopeId;
        public final String openingHoursJson;
        public LiftRow(long id, String name, long skiAreaId, String publicId, String liftType, String liftStatus,
                       Integer lengthM, Integer verticalRiseM, Double speedMps,
                       Double upX, Double upY, Double upZ, Double downX, Double downY, Double downZ,
                       Long upSlopeId, Long downSlopeId, String openingHoursJson) {
            this.id = id; this.name = name; this.skiAreaId = skiAreaId; this.publicId = publicId; this.liftType = liftType; this.liftStatus = liftStatus;
            this.lengthM = lengthM; this.verticalRiseM = verticalRiseM; this.speedMps = speedMps;
            this.upX = upX; this.upY = upY; this.upZ = upZ; this.downX = downX; this.downY = downY; this.downZ = downZ;
            this.upSlopeId = upSlopeId; this.downSlopeId = downSlopeId; this.openingHoursJson = openingHoursJson;
        }
    }
    public static final class PoiRow {
        public final long id;
        public final long skiAreaId;
        public final Long worksiteId; // null for non-worksite POIs (e.g., Summit)
        public final String name;      // may be null if worksite-backed; else required
        public final Double x, y, z;
        public final String status;    // OPEN/CLOSED/...
        public final String publicId;
        public PoiRow(long id, long skiAreaId, Long worksiteId, String name, Double x, Double y, Double z, String status, String publicId) {
            this.id = id; this.skiAreaId = skiAreaId; this.worksiteId = worksiteId; this.name = name; this.x = x; this.y = y; this.z = z; this.status = status; this.publicId = publicId;
        }
    }
    public static final class RestaurantLinkRow {
        public final long id;    // worksite.id
        public final long poiId; // poi.id
        public RestaurantLinkRow(long id, long poiId) { this.id = id; this.poiId = poiId; }
    }
    public static final class RescuePointLinkRow {
        public final long id;    // worksite.id
        public final long poiId; // poi.id
        public final boolean warning;
        public RescuePointLinkRow(long id, long poiId, boolean warning) { this.id = id; this.poiId = poiId; this.warning = warning; }
    }
    public static final class SummitLinkRow {
        public final long poiId;
        public final int snowHeightCm;
        public final String snowConsistency;
        public SummitLinkRow(long poiId, int snowHeightCm, String snowConsistency) { this.poiId = poiId; this.snowHeightCm = snowHeightCm; this.snowConsistency = snowConsistency; }
    }

    // Simplified input DTOs for POI-backed subtypes you’ll construct
    public static final class RestaurantRow {
        public final long worksiteId;
        public final String name; // from worksite
        public final long poiId;
        public final long skiAreaId;
        public final Double x, y, z;
        public final String status;
        private RestaurantRow(long worksiteId, String name, long poiId, long skiAreaId, Double x, Double y, Double z, String status) {
            this.worksiteId = worksiteId; this.name = name; this.poiId = poiId; this.skiAreaId = skiAreaId;
            this.x = x; this.y = y; this.z = z; this.status = status;
        }
        static RestaurantRow from(WorksiteRow ws, PoiRow poi) {
            return new RestaurantRow(ws.id, ws.name, poi.id, poi.skiAreaId, poi.x, poi.y, poi.z, poi.status);
        }
    }
    public static final class RescuePointRow {
        public final long worksiteId;
        public final String name; // from worksite
        public final long poiId;
        public final long skiAreaId;
        public final Double x, y, z;
        public final String status;
        public final boolean warning;
        private RescuePointRow(long worksiteId, String name, long poiId, long skiAreaId, Double x, Double y, Double z, String status, boolean warning) {
            this.worksiteId = worksiteId; this.name = name; this.poiId = poiId; this.skiAreaId = skiAreaId;
            this.x = x; this.y = y; this.z = z; this.status = status; this.warning = warning;
        }
        static RescuePointRow from(WorksiteRow ws, PoiRow poi, boolean warning) {
            return new RescuePointRow(ws.id, ws.name, poi.id, poi.skiAreaId, poi.x, poi.y, poi.z, poi.status, warning);
        }
    }
    public static final class SummitRow {
        public final long poiId;
        public final String name; // from poi (non-worksite POI)
        public final long skiAreaId;
        public final Double x, y, z;
        public final int snowHeightCm;
        public final String snowConsistency;
        private SummitRow(long poiId, String name, long skiAreaId, Double x, Double y, Double z, int snowHeightCm, String snowConsistency) {
            this.poiId = poiId; this.name = name; this.skiAreaId = skiAreaId; this.x = x; this.y = y; this.z = z; this.snowHeightCm = snowHeightCm; this.snowConsistency = snowConsistency;
        }
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

