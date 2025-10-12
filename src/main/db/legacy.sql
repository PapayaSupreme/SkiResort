CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE person (
                        id              BIGSERIAL PRIMARY KEY,
                        public_id       UUID UNIQUE NOT NULL,
                        first_name      VARCHAR(50) NOT NULL,
                        last_name       VARCHAR(50) NOT NULL,
                        dob             DATE NOT NULL,

    -- employee-specific fields
                        employee_type   VARCHAR(20) CHECK (
                            (person_kind <> 'EMPLOYEE' AND employee_type IS NULL)
                                OR (person_kind = 'EMPLOYEE' AND employee_type IN ('PISTER', 'LIFT_OP', 'RESTAURATION', 'MAINTENANCE'))
                            ),
    -- employee + instructors specific field
                        worksite_id    BIGINT CHECK (
                            (person_kind = 'GUEST' AND worksite_id IS NULL)
                                OR (person_kind <> 'GUEST' AND worksite_id IS NOT NULL)),

    -- instructor-specific field
                        ski_school  VARCHAR(50) CHECK (
                            (person_kind <> 'INSTRUCTOR' AND ski_school IS NULL)
                                OR (person_kind = 'INSTRUCTOR' AND ski_school IS NOT NULL)),

    -- replace java extends for kinds of subclass
                        person_kind     VARCHAR(20) NOT NULL CHECK (person_kind IN ('EMPLOYEE', 'GUEST', 'INSTRUCTOR')),

                        created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
                        updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_person_kind ON person (person_kind);
CREATE INDEX IF NOT EXISTS idx_employee_type ON person (employee_type) WHERE person_kind = 'EMPLOYEE';
CREATE INDEX IF NOT EXISTS idx_person_worksite ON person(worksite_id);



CREATE TABLE pass (
                      id              BIGSERIAL PRIMARY KEY,
                      public_id       UUID UNIQUE NOT NULL,
                      owner_id        BIGINT NOT NULL REFERENCES person(id) ON DELETE CASCADE,

                      valid_from      DATE NOT NULL,
                      valid_until     DATE NOT NULL,
                      price           NUMERIC(8,2) NOT NULL CHECK (price >= 0),

                      pass_type       VARCHAR(20) NOT NULL CHECK (pass_type IN ('DAY', 'MULTIDAY', 'ALACARTE', 'SEASON')),
                      pass_status     VARCHAR(20) NOT NULL CHECK (pass_status IN ('ACTIVE', 'SUSPENDED', 'EXPIRED')),

                      created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
                      updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
CREATE INDEX IF NOT EXISTS idx_pass_owner ON pass (owner_id);
CREATE INDEX IF NOT EXISTS idx_pass_type ON pass (pass_type);
CREATE INDEX IF NOT EXISTS idx_pass_validity ON pass (valid_from, valid_until);

ALTER TABLE pass
    ADD CONSTRAINT chk_pass_dates CHECK (valid_until >= valid_from);

CREATE TABLE pass_day_used (
                        pass_id    BIGINT NOT NULL REFERENCES pass(id) ON DELETE CASCADE,
                        used_date  DATE NOT NULL,
                        PRIMARY KEY (pass_id, used_date)
);

CREATE INDEX idx_pass_day_used_date ON pass_day_used (used_date);

CREATE TABLE worksite (
                    id BIGSERIAL PRIMARY KEY,
                    worksite_name VARCHAR(50) NOT NULL UNIQUE,
                    worksite_type VARCHAR (20) NOT NULL CHECK (worksite_type IN ('LIFT', 'RESTAURANT', 'RESCUE_POINT', 'SKI_AREA')),
                    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_worksite_type ON worksite(worksite_type);
CREATE INDEX IF NOT EXISTS idx_worksite_name_lower
    ON worksite(LOWER(worksite_name));

ALTER TABLE person
    ADD CONSTRAINT fk_person_worksite
        FOREIGN KEY (worksite_id)
            REFERENCES worksite(id)
            ON UPDATE CASCADE
            ON DELETE SET NULL;

CREATE TABLE IF NOT EXISTS ski_area (
                                        id               BIGINT PRIMARY KEY
                                            REFERENCES worksite(id) ON DELETE CASCADE,
                                        public_id        UUID UNIQUE NOT NULL,
                                        name            VARCHAR(64) NOT NULL,
                                        up  INTEGER,
                                        down  INTEGER,
                                        functioning      BOOLEAN NOT NULL DEFAULT TRUE,
                                        updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_ski_area_id ON ski_area(id);
CREATE INDEX IF NOT EXISTS idx_ski_area_name ON ski_area(name);

-- ---------- SLOPE (Independent entity; NOT a Worksite) ----------
CREATE TABLE IF NOT EXISTS slope (
                                     id                 BIGSERIAL PRIMARY KEY,   -- independent identity (not worksite.id)
                                     ski_area_id        BIGINT NOT NULL
                                         REFERENCES ski_area(id) ON DELETE RESTRICT,
                                     public_id          UUID UNIQUE NOT NULL,

                                     name               VARCHAR(96) NOT NULL,    -- keep display name here since not a worksite
                                     difficulty         VARCHAR(8)  NOT NULL CHECK (difficulty IN ('GREEN','BLUE','RED','BLACK')),
                                     snow_consistency   VARCHAR(32) NOT NULL DEFAULT 'NONE'
                                         CHECK (snow_consistency IN ('POWDER', 'GROOMED', 'PACKED', 'ICY', 'SLUSH', 'CRUST', 'ARTIFICIAL', 'NONE')),
                                     slope_type         VARCHAR (64) NOT NULL
                                         CHECK (slope_type IN ('PISTE', 'SLALOM', 'SNOWPARK', 'UNGROOMED', 'OFF_PISTE')),

    -- Optional 3D endpoints (start/finish)
                                     up_x            DOUBLE PRECISION,
                                     up_y            DOUBLE PRECISION,
                                     up_z            DOUBLE PRECISION,
                                     down_x          DOUBLE PRECISION,
                                     down_y          DOUBLE PRECISION,
                                     down_z          DOUBLE PRECISION,
                                     updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_slope_area        ON slope(ski_area_id);
CREATE INDEX IF NOT EXISTS idx_slope_difficulty  ON slope(difficulty);
CREATE INDEX IF NOT EXISTS idx_slope_id      ON slope(id);



-- ---------- LIFT (Worksite implement) ----------
CREATE TABLE IF NOT EXISTS lift (
                                    id                 BIGINT PRIMARY KEY
                                        REFERENCES worksite(id) ON DELETE CASCADE,
                                    name                VARCHAR (50) NOT NULL,
                                    ski_area_id        BIGINT NOT NULL
                                        REFERENCES ski_area(id) ON DELETE RESTRICT,
                                    public_id          UUID UNIQUE NOT NULL,

                                    lift_type          VARCHAR(32) NOT NULL
                                        CHECK (lift_type IN ('TREADMILL', 'TELESKI', 'CHAIRLIFT', 'GONDOLA', 'CABLECAR', 'FUNICULAR')),
                                    lift_status        VARCHAR(32) NOT NULL DEFAULT 'CLOSED'
                                        CHECK (lift_status IN ('OPEN', 'CLOSED', 'MAINTENANCE', 'WIND_HOLD')),
    -- 3D endpoints match your Point(x,y,z)
                                    up_x            DOUBLE PRECISION,
                                    up_y            DOUBLE PRECISION,
                                    up_z            DOUBLE PRECISION,
                                    down_x          DOUBLE PRECISION,
                                    down_y          DOUBLE PRECISION,
                                    down_z          DOUBLE PRECISION,

                                    up_slope        BIGINT
                                        REFERENCES slope(id) ON DELETE RESTRICT,
                                    down_slope        BIGINT
                                    REFERENCES slope(id) ON DELETE RESTRICT,

                                    length          DOUBLE PRECISION NOT NULL CHECK (length > 0),
                                    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_lift_area     ON lift(ski_area_id);
CREATE INDEX IF NOT EXISTS idx_lift_type     ON lift(lift_type);
CREATE INDEX IF NOT EXISTS idx_lift_id   ON lift(id);

CREATE TABLE IF NOT EXISTS poi (
                                   id              BIGSERIAL PRIMARY KEY,
                                   ski_area_id     BIGINT NOT NULL
                                       REFERENCES ski_area(id) ON DELETE RESTRICT,


                                   worksite_id     BIGINT UNIQUE
                                       REFERENCES worksite(id) ON DELETE CASCADE,


                                   name            VARCHAR(96),

    -- 3D point (matches your Point(x,y,z))
                                   x               DOUBLE PRECISION,
                                   y               DOUBLE PRECISION,
                                   z_m             DOUBLE PRECISION CHECK (z_m IS NULL OR z_m >= 0),

                                   status          VARCHAR(24) NOT NULL DEFAULT 'CLOSED'
                                       CHECK (status IN ('OPEN','CLOSED','WARNING','MAINTENANCE')),  -- match your POIStatus
                                   public_id       UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),

                                   updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
                                   is_deleted      BOOLEAN NOT NULL DEFAULT FALSE,

    -- If it’s not a worksite, require a name; if it is, name may be NULL (use worksite name).
                                   CONSTRAINT chk_poi_name_presence
                                       CHECK ( (worksite_id IS NOT NULL) OR (name IS NOT NULL) )
);

CREATE INDEX IF NOT EXISTS idx_poi_area     ON poi(ski_area_id);
CREATE INDEX IF NOT EXISTS idx_poi_status   ON poi(status);
CREATE INDEX IF NOT EXISTS idx_poi_name_lw  ON poi(LOWER(name));

-- =========================
-- Restaurant (POI + Worksite)
-- =========================
-- Identity comes from WORKSITE (id), but we also bind to the POI row.
-- Enforce 1–1: each restaurant worksite must map to exactly one poi, and vice-versa.
CREATE TABLE IF NOT EXISTS restaurant (
                                          id          BIGINT PRIMARY KEY
                                              REFERENCES worksite(id) ON DELETE CASCADE,
                                          poi_id      BIGINT UNIQUE NOT NULL
                                              REFERENCES poi(id) ON DELETE CASCADE,

                                          updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- (Optional, but recommended) A trigger can enforce that the linked worksite row
-- has worksite_type = 'RESTAURANT'. If you want, I can add the plpgsql trigger.

-- =========================
-- Rescue Point (POI + Worksite)
-- =========================
CREATE TABLE IF NOT EXISTS rescue_point (
                                            id          BIGINT PRIMARY KEY
                                                REFERENCES worksite(id) ON DELETE CASCADE,
                                            poi_id      BIGINT UNIQUE NOT NULL
                                                REFERENCES poi(id) ON DELETE CASCADE,

                                            warning     BOOLEAN NOT NULL DEFAULT FALSE,
                                            updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- (Optional) Trigger to enforce worksite_type = 'RESCUE_POINT' for 'id'.

-- =========================
-- Summit (POI only; NOT a Worksite)
-- =========================
CREATE TABLE IF NOT EXISTS summit (
                                      poi_id          BIGINT PRIMARY KEY
                                          REFERENCES poi(id) ON DELETE CASCADE,

                                      snow_height_cm  INTEGER NOT NULL DEFAULT 0 CHECK (snow_height_cm >= 0),
                                      snow_consistency VARCHAR(24) NOT NULL DEFAULT 'NONE'
                                          CHECK (snow_consistency IN ('POWDER','GROOMED','PACKED','ICY','SLUSH','CRUST','ARTIFICIAL','NONE')),

                                      updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);



-- Add missing columns in ski_area table
ALTER TABLE ski_area ADD COLUMN opening_hours VARCHAR(100);

-- Add missing columns in slope table
ALTER TABLE slope ADD COLUMN opening_hours VARCHAR(100);
ALTER TABLE slope ADD COLUMN length DOUBLE PRECISION;

-- Add missing columns in lift table
ALTER TABLE lift ADD COLUMN opening_hours VARCHAR(100);

-- Add missing opening_hours to relevant tables
ALTER TABLE worksite ADD COLUMN opening_hours VARCHAR(100);
-- run after creating worksite but BEFORE loading data
ALTER TABLE worksite DROP CONSTRAINT IF EXISTS worksite_worksite_name_key;
ALTER TABLE worksite ADD CONSTRAINT uq_worksite_name_type
    UNIQUE (worksite_name, worksite_type);


ALTER TABLE person   ALTER COLUMN public_id SET DEFAULT gen_random_uuid();
ALTER TABLE pass     ALTER COLUMN public_id SET DEFAULT gen_random_uuid();
ALTER TABLE ski_area ALTER COLUMN public_id SET DEFAULT gen_random_uuid();
ALTER TABLE slope    ALTER COLUMN public_id SET DEFAULT gen_random_uuid();
ALTER TABLE lift     ALTER COLUMN public_id SET DEFAULT gen_random_uuid();
-- poi already has it

ALTER TABLE lift DROP COLUMN IF EXISTS name;



