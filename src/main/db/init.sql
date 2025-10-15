-- ===============================
-- Ski Resort DB - fresh init.sql
-- ===============================
-- Safe to run multiple times (idempotent). Requires PostgreSQL 13+.
-- Models:
--   - Worksite supertype (SkiArea, Lift, Restaurant, RescuePoint)
--   - Slope (independent; NOT a Worksite)
--   - POI base + subtypes (Restaurant/RescuePoint as POI+Worksite; Summit as POI-only)
--   - Persons & Passes kept minimal (user said they'll stay in SQL)
-- Conventions:
-- Conventions:
--   - All "instant" timestamps are TIMESTAMPTZ with auto-updated updated_at
--   - 3D points are (x,y,z_m) with z_m >= 0
--   - public_id defaults to gen_random_uuid()
--   - Composite uniqueness on (worksite_name, worksite_type)
--   - Opening hours kept as JSONB for flexibility (optional)

-- ---------- Extensions ----------
CREATE EXTENSION IF NOT EXISTS pgcrypto;
-- Uncomment if you want trigram search on names:
-- CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- ---------- Utility: updated_at trigger ----------
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS trigger
LANGUAGE plpgsql AS $$
BEGIN
  NEW.updated_at := now();
  RETURN NEW;
END;
$$;

-- ---------- Utility: enforce worksite_type ----------
CREATE OR REPLACE FUNCTION assert_worksite_type(p_worksite_id BIGINT, p_expected TEXT)
RETURNS void
LANGUAGE plpgsql AS $$
DECLARE
  t TEXT;
BEGIN
  SELECT worksite_type INTO t FROM worksite WHERE id = p_worksite_id;
  IF t IS NULL THEN
    RAISE EXCEPTION 'Worksite % not found', p_worksite_id USING ERRCODE='foreign_key_violation';
  END IF;
  IF t <> p_expected THEN
    RAISE EXCEPTION 'Worksite % has type %, expected %', p_worksite_id, t, p_expected USING ERRCODE='check_violation';
  END IF;
END;
$$;

-- ---------- Core supertype: worksite ----------
CREATE TABLE IF NOT EXISTS worksite (
  id               BIGSERIAL PRIMARY KEY,
  worksite_name    VARCHAR(96) NOT NULL,
  worksite_type    VARCHAR(24)  NOT NULL CHECK (worksite_type IN ('SKI_AREA','LIFT','RESTAURANT','RESCUE_POINT')),
  opening_hours    JSONB,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);
-- Composite uniqueness (name within a type)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'uq_worksite_name_type'
  ) THEN
    ALTER TABLE worksite
      ADD CONSTRAINT uq_worksite_name_type UNIQUE (worksite_name, worksite_type);
  END IF;
END$$;

CREATE INDEX IF NOT EXISTS idx_worksite_type ON worksite(worksite_type);
CREATE INDEX IF NOT EXISTS idx_worksite_name_lower ON worksite (LOWER(worksite_name));

CREATE TRIGGER trg_worksite_updated
BEFORE UPDATE ON worksite
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ---------- SkiArea (Worksite subtype) ----------
CREATE TABLE IF NOT EXISTS ski_area (
  id               BIGINT PRIMARY KEY REFERENCES worksite(id) ON DELETE CASCADE,
  public_id        UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
  elevation_min_m  INTEGER CHECK (elevation_min_m IS NULL OR elevation_min_m >= 0),
  elevation_max_m  INTEGER CHECK (elevation_max_m IS NULL OR elevation_min_m IS NULL OR elevation_max_m >= elevation_min_m),
  functioning      BOOLEAN NOT NULL DEFAULT TRUE,
  opening_hours    JSONB,
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_ski_area_functioning ON ski_area(functioning);

CREATE TRIGGER trg_ski_area_updated
BEFORE UPDATE ON ski_area
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ---------- Slope (NOT a Worksite) ----------
CREATE TABLE IF NOT EXISTS slope (
  id               BIGSERIAL PRIMARY KEY,
  ski_area_id      BIGINT NOT NULL REFERENCES ski_area(id) ON DELETE RESTRICT,
  public_id        UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),

  name             VARCHAR(96) NOT NULL,
  difficulty       VARCHAR(8)  NOT NULL CHECK (difficulty IN ('GREEN','BLUE','RED','BLACK')),
  slope_type       VARCHAR(32) NOT NULL CHECK (slope_type IN ('PISTE','SLALOM','SNOWPARK','UNGROOMED','OFF_PISTE')),
  length_m         INTEGER CHECK (length_m IS NULL OR length_m > 0),
  avg_width_m      INTEGER CHECK (avg_width_m IS NULL OR avg_width_m > 0),
  groomed          BOOLEAN NOT NULL DEFAULT TRUE,
  snowmaking       BOOLEAN NOT NULL DEFAULT FALSE,

  -- 3D endpoints
  up_x             DOUBLE PRECISION,
  up_y             DOUBLE PRECISION,
  up_z_m           DOUBLE PRECISION CHECK (up_z_m IS NULL OR up_z_m >= 0),
  down_x           DOUBLE PRECISION,
  down_y           DOUBLE PRECISION,
  down_z_m         DOUBLE PRECISION CHECK (down_z_m IS NULL OR down_z_m >= 0),

  opening_hours    JSONB,
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);
ALTER TABLE slope
    ADD CONSTRAINT uq_slope_area_name UNIQUE (ski_area_id, name);

CREATE INDEX IF NOT EXISTS idx_slope_area       ON slope(ski_area_id);
CREATE INDEX IF NOT EXISTS idx_slope_difficulty ON slope(difficulty);
-- CREATE INDEX IF NOT EXISTS idx_slope_name_trgm ON slope USING gin (name gin_trgm_ops);

CREATE TRIGGER trg_slope_updated
BEFORE UPDATE ON slope
FOR EACH ROW EXECUTE FUNCTION set_updated_at();



-- ---------- Lift (Worksite subtype) ----------
CREATE TABLE IF NOT EXISTS lift (
  id               BIGINT PRIMARY KEY REFERENCES worksite(id) ON DELETE CASCADE,
  ski_area_id      BIGINT NOT NULL REFERENCES ski_area(id) ON DELETE RESTRICT,
  public_id        UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),

  lift_type        VARCHAR(32) NOT NULL
    CHECK (lift_type IN ('TREADMILL','TELESKI','CHAIRLIFT','CHAIR_FIXED','CHAIR_DETACHABLE','GONDOLA','TELECABIN','CABLECAR','FUNICULAR')),
  lift_status      VARCHAR(32) NOT NULL DEFAULT 'CLOSED'
    CHECK (lift_status IN ('OPEN','CLOSED','MAINTENANCE','WIND_HOLD')),
  length_m         INTEGER CHECK (length_m IS NULL OR length_m > 0),
  vertical_rise_m  INTEGER CHECK (vertical_rise_m IS NULL OR vertical_rise_m >= 0),
  speed_mps        NUMERIC(5,2) CHECK (speed_mps IS NULL OR speed_mps > 0),

  -- 3D endpoints (top/bottom)
  up_x             DOUBLE PRECISION,
  up_y             DOUBLE PRECISION,
  up_z_m           DOUBLE PRECISION CHECK (up_z_m IS NULL OR up_z_m >= 0),
  down_x           DOUBLE PRECISION,
  down_y           DOUBLE PRECISION,
  down_z_m         DOUBLE PRECISION CHECK (down_z_m IS NULL OR down_z_m >= 0),

  -- Optional links to nearest slopes
  up_slope         BIGINT REFERENCES slope(id)   ON DELETE RESTRICT,
  down_slope       BIGINT REFERENCES slope(id)   ON DELETE RESTRICT,

  opening_hours    JSONB,
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_lift_area  ON lift(ski_area_id);
CREATE INDEX IF NOT EXISTS idx_lift_type  ON lift(lift_type);
CREATE INDEX IF NOT EXISTS idx_lift_stat  ON lift(lift_status);

CREATE TRIGGER trg_lift_updated
BEFORE UPDATE ON lift
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ---------- POI base ----------
CREATE TABLE IF NOT EXISTS poi (
  id               BIGSERIAL PRIMARY KEY,
  ski_area_id      BIGINT NOT NULL REFERENCES ski_area(id) ON DELETE RESTRICT,
  worksite_id      BIGINT UNIQUE REFERENCES worksite(id) ON DELETE CASCADE, -- only for worksite-backed POIs
  name             VARCHAR(96), -- required when not backed by a worksite
  x                DOUBLE PRECISION,
  y                DOUBLE PRECISION,
  z_m              DOUBLE PRECISION CHECK (z_m IS NULL OR z_m >= 0),
  status           VARCHAR(24) NOT NULL DEFAULT 'CLOSED'
                     CHECK (status IN ('OPEN','CLOSED','WARNING','MAINTENANCE')),
  public_id        UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  is_deleted       BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT chk_poi_name_presence CHECK ( (worksite_id IS NOT NULL) OR (name IS NOT NULL) )
);

CREATE INDEX IF NOT EXISTS idx_poi_area    ON poi(ski_area_id);
CREATE INDEX IF NOT EXISTS idx_poi_status  ON poi(status);
-- CREATE INDEX IF NOT EXISTS idx_poi_name_lw ON poi (LOWER(name));

CREATE TRIGGER trg_poi_updated
BEFORE UPDATE ON poi
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ---------- Restaurant (POI + Worksite) ----------
CREATE TABLE IF NOT EXISTS restaurant (
  id          BIGINT PRIMARY KEY REFERENCES worksite(id) ON DELETE CASCADE,
  poi_id      BIGINT UNIQUE NOT NULL REFERENCES poi(id) ON DELETE CASCADE,
  updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TRIGGER trg_restaurant_updated
BEFORE UPDATE ON restaurant
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- Type enforcement trigger
CREATE OR REPLACE FUNCTION restaurant_type_guard()
RETURNS trigger
LANGUAGE plpgsql AS $$
BEGIN
  PERFORM assert_worksite_type(NEW.id, 'RESTAURANT');
  RETURN NEW;
END;
$$;
DROP TRIGGER IF EXISTS trg_restaurant_type ON restaurant;
CREATE TRIGGER trg_restaurant_type
BEFORE INSERT OR UPDATE ON restaurant
FOR EACH ROW EXECUTE FUNCTION restaurant_type_guard();

-- ---------- Rescue Point (POI + Worksite) ----------
CREATE TABLE IF NOT EXISTS rescue_point (
  id          BIGINT PRIMARY KEY REFERENCES worksite(id) ON DELETE CASCADE,
  poi_id      BIGINT UNIQUE NOT NULL REFERENCES poi(id) ON DELETE CASCADE,
  warning     BOOLEAN NOT NULL DEFAULT FALSE,
  updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TRIGGER trg_rescue_point_updated
BEFORE UPDATE ON rescue_point
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE OR REPLACE FUNCTION rescue_type_guard()
RETURNS trigger
LANGUAGE plpgsql AS $$
BEGIN
  PERFORM assert_worksite_type(NEW.id, 'RESCUE_POINT');
  RETURN NEW;
END;
$$;
DROP TRIGGER IF EXISTS trg_rescue_type ON rescue_point;
CREATE TRIGGER trg_rescue_type
BEFORE INSERT OR UPDATE ON rescue_point
FOR EACH ROW EXECUTE FUNCTION rescue_type_guard();

-- ---------- Summit (POI only) ----------
CREATE TABLE IF NOT EXISTS summit (
  poi_id            BIGINT PRIMARY KEY REFERENCES poi(id) ON DELETE CASCADE,
  snow_height_cm    INTEGER NOT NULL DEFAULT 0 CHECK (snow_height_cm >= 0),
  snow_consistency  VARCHAR(24) NOT NULL DEFAULT 'NONE'
                      CHECK (snow_consistency IN ('POWDER','GROOMED','PACKED','ICY','SLUSH','CRUST','ARTIFICIAL','NONE')),
  updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TRIGGER trg_summit_updated
BEFORE UPDATE ON summit
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ---------- People & Passes  ---------- //TODO: add back skischools, add a set of passes id to each person
CREATE TABLE IF NOT EXISTS person (
  id           BIGSERIAL PRIMARY KEY,
  public_id    UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
  first_name   VARCHAR(64) NOT NULL,
  last_name    VARCHAR(64) NOT NULL,
  email        VARCHAR(256),

  -- optional affinity to a worksite (e.g., assigned workplace)
  worksite_id  BIGINT REFERENCES worksite(id) ON DELETE SET NULL,
  created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_person_email ON person(LOWER(email));

CREATE TRIGGER trg_person_updated
BEFORE UPDATE ON person
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- PASS
CREATE TABLE IF NOT EXISTS pass (
                                    id           BIGSERIAL PRIMARY KEY,
                                    public_id    UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
                                    owner_id     BIGINT NOT NULL REFERENCES person(id) ON DELETE CASCADE,

                                    pass_category VARCHAR(32) NOT NULL
                                        CHECK (pass_category IN ('ALACARTE', 'DAY', 'MULTIDAY', 'SEASON')),

                                    valid_day     DATE,   -- for DAY / ALACARTE single-day passes
                                    valid_from    DATE,   -- for MULTIDAY / SEASON range start
                                    valid_to      DATE,   -- for MULTIDAY / SEASON range end

                                    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
                                    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),

                                    CONSTRAINT chk_pass_dates_range_ok
                                        CHECK (valid_from IS NULL OR valid_to IS NULL OR valid_to >= valid_from),

                                    CONSTRAINT chk_pass_category_fields
                                        CHECK (
                                            (pass_category IN ('DAY','ALACARTE') AND valid_day IS NOT NULL AND valid_from IS NULL AND valid_to IS NULL)
                                                OR
                                            (pass_category IN ('MULTIDAY','SEASON') AND valid_day IS NULL AND valid_from IS NOT NULL AND valid_to IS NOT NULL)
                                            )
);

CREATE INDEX IF NOT EXISTS idx_pass_owner ON pass(owner_id);

ALTER TABLE pass
    add column if not exists pass_status VARCHAR(32),
    add column if not exists year INT;

ALTER TABLE pass
    drop column if exists year;

ALTER TABLE pass
    DROP constraint chk_pass_category_fields;

CREATE INDEX IF NOT EXISTS idx_pass_year ON pass(year);

DROP TRIGGER IF EXISTS trg_pass_updated ON pass;
CREATE TRIGGER trg_pass_updated
    BEFORE UPDATE ON pass
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- PASS USAGE (one row per day used per pass)
CREATE TABLE IF NOT EXISTS pass_usage (
                                          pass_id     BIGINT NOT NULL REFERENCES pass(id) ON DELETE CASCADE,
                                          used_on     DATE   NOT NULL,
                                          created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
                                          PRIMARY KEY (pass_id, used_on)
);

CREATE INDEX IF NOT EXISTS idx_pass_usage_used_on ON pass_usage(used_on);


CREATE TRIGGER trg_pass_updated
BEFORE UPDATE ON pass
FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TABLE IF NOT EXISTS pass_usage (
                                          pass_id   BIGINT NOT NULL REFERENCES pass(id) ON DELETE CASCADE,
                                          used_on   DATE   NOT NULL,
                                          created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                          PRIMARY KEY (pass_id, used_on) -- no dupes that way hehe
);

------------ Views helpers ----------
CREATE OR REPLACE VIEW v_lifts_with_areas AS
SELECT w.id            AS worksite_id,
       w.worksite_name AS lift_name,
       l.public_id     AS lift_public_id,
       l.lift_type,
       l.lift_status,
       sa.id           AS ski_area_id,
       wa.worksite_name AS ski_area_name
FROM lift l
JOIN worksite w  ON w.id = l.id
JOIN ski_area sa ON sa.id = l.ski_area_id
JOIN worksite wa ON wa.id = sa.id;

CREATE OR REPLACE VIEW v_slopes_by_area AS
SELECT s.id, s.public_id, s.name, s.difficulty, s.length_m, s.ski_area_id, wa.worksite_name AS ski_area_name
FROM slope s
JOIN ski_area sa ON sa.id = s.ski_area_id
JOIN worksite wa ON wa.id = sa.id;
