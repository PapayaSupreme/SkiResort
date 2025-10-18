create table if not exists worksite
(
    id            bigserial
        primary key,
    worksite_name varchar(96)                            not null,
    worksite_type varchar(24)                            not null
        constraint worksite_worksite_type_check
            check ((worksite_type)::text = ANY
                   ((ARRAY ['SKI_AREA'::character varying, 'LIFT'::character varying, 'RESTAURANT'::character varying, 'RESCUE_POINT'::character varying])::text[])),
    opening_hours jsonb,
    created_at    timestamp with time zone default now() not null,
    updated_at    timestamp with time zone default now() not null,
    constraint uq_worksite_name_type
        unique (worksite_name, worksite_type)
);

alter table worksite
    owner to postgres;

create index if not exists idx_worksite_type
    on worksite (worksite_type);

create index if not exists idx_worksite_name_lower
    on worksite (lower(worksite_name::text));

create table if not exists ski_area
(
    id              bigint                                             not null
        primary key
        references worksite
            on delete cascade,
    public_id       uuid                     default gen_random_uuid() not null
        unique,
    elevation_min_m integer
        constraint ski_area_elevation_min_m_check
            check ((elevation_min_m IS NULL) OR (elevation_min_m >= 0)),
    elevation_max_m integer,
    functioning     boolean                  default true              not null,
    opening_hours   jsonb,
    updated_at      timestamp with time zone default now()             not null,
    constraint ski_area_check
        check ((elevation_max_m IS NULL) OR (elevation_min_m IS NULL) OR (elevation_max_m >= elevation_min_m))
);

alter table ski_area
    owner to postgres;

create index if not exists idx_ski_area_functioning
    on ski_area (functioning);

create table if not exists slope
(
    id            bigserial
        primary key,
    ski_area_id   bigint                                             not null
        references ski_area
            on delete restrict,
    public_id     uuid                     default gen_random_uuid() not null
        unique,
    name          varchar(96)                                        not null,
    difficulty    varchar(8)                                         not null
        constraint slope_difficulty_check
            check ((difficulty)::text = ANY
                   ((ARRAY ['GREEN'::character varying, 'BLUE'::character varying, 'RED'::character varying, 'BLACK'::character varying])::text[])),
    slope_type    varchar(32)                                        not null
        constraint slope_slope_type_check
            check ((slope_type)::text = ANY
                   ((ARRAY ['PISTE'::character varying, 'SLALOM'::character varying, 'SNOWPARK'::character varying, 'UNGROOMED'::character varying, 'OFF_PISTE'::character varying])::text[])),
    length_m      integer
        constraint slope_length_m_check
            check ((length_m IS NULL) OR (length_m > 0)),
    avg_width_m   integer
        constraint slope_avg_width_m_check
            check ((avg_width_m IS NULL) OR (avg_width_m > 0)),
    groomed       boolean                  default true              not null,
    snowmaking    boolean                  default false             not null,
    up_x          double precision,
    up_y          double precision,
    up_z_m        double precision
        constraint slope_up_z_m_check
            check ((up_z_m IS NULL) OR (up_z_m >= (0)::double precision)),
    down_x        double precision,
    down_y        double precision,
    down_z_m      double precision
        constraint slope_down_z_m_check
            check ((down_z_m IS NULL) OR (down_z_m >= (0)::double precision)),
    opening_hours jsonb,
    updated_at    timestamp with time zone default now()             not null,
    constraint uq_slope_area_name
        unique (ski_area_id, name)
);

alter table slope
    owner to postgres;

create index if not exists idx_slope_area
    on slope (ski_area_id);

create index if not exists idx_slope_difficulty
    on slope (difficulty);

create table if not exists lift
(
    id              bigint                                                       not null
        primary key
        references worksite
            on delete cascade,
    ski_area_id     bigint                                                       not null
        references ski_area
            on delete restrict,
    public_id       uuid                     default gen_random_uuid()           not null
        unique,
    lift_type       varchar(32)                                                  not null
        constraint lift_lift_type_check
            check ((lift_type)::text = ANY
                   ((ARRAY ['TREADMILL'::character varying, 'TELESKI'::character varying, 'CHAIRLIFT'::character varying, 'CHAIR_FIXED'::character varying, 'CHAIR_DETACHABLE'::character varying, 'GONDOLA'::character varying, 'TELECABIN'::character varying, 'CABLECAR'::character varying, 'FUNICULAR'::character varying])::text[])),
    lift_status     varchar(32)              default 'CLOSED'::character varying not null
        constraint lift_lift_status_check
            check ((lift_status)::text = ANY
                   ((ARRAY ['OPEN'::character varying, 'CLOSED'::character varying, 'MAINTENANCE'::character varying, 'WIND_HOLD'::character varying])::text[])),
    length_m        integer
        constraint lift_length_m_check
            check ((length_m IS NULL) OR (length_m > 0)),
    vertical_rise_m integer
        constraint lift_vertical_rise_m_check
            check ((vertical_rise_m IS NULL) OR (vertical_rise_m >= 0)),
    speed_mps       numeric(5, 2)
        constraint lift_speed_mps_check
            check ((speed_mps IS NULL) OR (speed_mps > (0)::numeric)),
    up_x            double precision,
    up_y            double precision,
    up_z_m          double precision
        constraint lift_up_z_m_check
            check ((up_z_m IS NULL) OR (up_z_m >= (0)::double precision)),
    down_x          double precision,
    down_y          double precision,
    down_z_m        double precision
        constraint lift_down_z_m_check
            check ((down_z_m IS NULL) OR (down_z_m >= (0)::double precision)),
    up_slope        bigint
        references slope
            on delete restrict,
    down_slope      bigint
        references slope
            on delete restrict,
    opening_hours   jsonb,
    updated_at      timestamp with time zone default now()                       not null
);

alter table lift
    owner to postgres;

create index if not exists idx_lift_area
    on lift (ski_area_id);

create index if not exists idx_lift_type
    on lift (lift_type);

create index if not exists idx_lift_stat
    on lift (lift_status);

create table if not exists poi
(
    id          bigserial
        primary key,
    ski_area_id bigint                                                       not null
        references ski_area
            on delete restrict,
    worksite_id bigint
        unique
        references worksite
            on delete cascade,
    name        varchar(96),
    x           double precision,
    y           double precision,
    z_m         double precision
        constraint poi_z_m_check
            check ((z_m IS NULL) OR (z_m >= (0)::double precision)),
    status      varchar(24)              default 'CLOSED'::character varying not null
        constraint poi_status_check
            check ((status)::text = ANY
                   ((ARRAY ['OPEN'::character varying, 'CLOSED'::character varying, 'WARNING'::character varying, 'MAINTENANCE'::character varying])::text[])),
    public_id   uuid                     default gen_random_uuid()           not null
        unique,
    updated_at  timestamp with time zone default now()                       not null,
    is_deleted  boolean                  default false                       not null,
    constraint chk_poi_name_presence
        check ((worksite_id IS NOT NULL) OR (name IS NOT NULL))
);

alter table poi
    owner to postgres;

create index if not exists idx_poi_area
    on poi (ski_area_id);

create index if not exists idx_poi_status
    on poi (status);

create unique index if not exists uq_poi_area_name
    on poi (ski_area_id, lower(name::text))
    where (worksite_id IS NULL);

create table if not exists restaurant
(
    id         bigint                                 not null
        primary key
        references worksite
            on delete cascade,
    poi_id     bigint                                 not null
        unique
        references poi
            on delete cascade,
    updated_at timestamp with time zone default now() not null
);

alter table restaurant
    owner to postgres;

create table if not exists rescue_point
(
    id         bigint                                 not null
        primary key
        references worksite
            on delete cascade,
    poi_id     bigint                                 not null
        unique
        references poi
            on delete cascade,
    warning    boolean                  default false not null,
    updated_at timestamp with time zone default now() not null
);

alter table rescue_point
    owner to postgres;

create table if not exists summit
(
    poi_id           bigint                                                     not null
        primary key
        references poi
            on delete cascade,
    snow_height_cm   integer                  default 0                         not null
        constraint summit_snow_height_cm_check
            check (snow_height_cm >= 0),
    snow_consistency varchar(24)              default 'NONE'::character varying not null
        constraint summit_snow_consistency_check
            check ((snow_consistency)::text = ANY
                   ((ARRAY ['POWDER'::character varying, 'GROOMED'::character varying, 'PACKED'::character varying, 'ICY'::character varying, 'SLUSH'::character varying, 'CRUST'::character varying, 'ARTIFICIAL'::character varying, 'NONE'::character varying])::text[])),
    updated_at       timestamp with time zone default now()                     not null
);

alter table summit
    owner to postgres;

create table if not exists person
(
    id            bigserial
        primary key,
    public_id     uuid                     default gen_random_uuid() not null
        unique,
    first_name    varchar(50)                                        not null,
    last_name     varchar(50)                                        not null,
    email         varchar(128)
        constraint person_pk
            unique,
    worksite_id   bigint
                                                                     references worksite
                                                                         on delete set null,
    created_at    timestamp with time zone default now()             not null,
    updated_at    timestamp with time zone default now()             not null,
    person_kind   varchar(31)                                        not null
        constraint person_person_kind_check
            check ((person_kind)::text = ANY
                   ((ARRAY ['GUEST'::character varying, 'EMPLOYEE'::character varying, 'INSTRUCTOR'::character varying])::text[])),
    dob           date                                               not null,
    employee_type varchar(255)
        constraint person_employee_type_check
            check ((employee_type)::text = ANY
                   ((ARRAY ['PISTER'::character varying, 'LIFT_OP'::character varying, 'RESTAURATION'::character varying, 'MAINTENANCE'::character varying])::text[])),
    ski_school    varchar(255)
        constraint person_ski_school_check
            check ((ski_school)::text = ANY ((ARRAY ['ESF'::character varying, 'ISF'::character varying])::text[]))
);

alter table person
    owner to postgres;

create index if not exists idx_person_email
    on person (lower(email::text));

create table if not exists pass
(
    id          bigserial
        primary key,
    public_id   uuid                     default gen_random_uuid() not null
        unique,
    owner_id    bigint                                             not null
        references person
            on delete cascade,
    pass_kind   varchar(31)                                        not null
        constraint pass_pass_category_check
            check ((pass_kind)::text = ANY
                   (ARRAY [('ALACARTE'::character varying)::text, ('DAY'::character varying)::text, ('MULTIDAY'::character varying)::text, ('SEASON'::character varying)::text])),
    valid_day   date,
    valid_from  date,
    valid_to    date,
    created_at  timestamp with time zone default now()             not null,
    updated_at  timestamp with time zone default now()             not null,
    pass_status varchar(32),
    constraint chk_pass_dates_range_ok
        check ((valid_from IS NULL) OR (valid_to IS NULL) OR (valid_to >= valid_from))
);

alter table pass
    owner to postgres;

create index if not exists idx_pass_owner
    on pass (owner_id);

create table if not exists pass_usage
(
    pass_id    bigint                                 not null
        references pass
            on delete cascade,
    used_on    date                                   not null,
    created_at timestamp with time zone default now() not null,
    primary key (pass_id, used_on)
);

alter table pass_usage
    owner to postgres;

create index if not exists idx_pass_usage_used_on
    on pass_usage (used_on);

create view v_lifts_with_areas
            (worksite_id, lift_name, lift_public_id, lift_type, lift_status, ski_area_id, ski_area_name) as
SELECT w.id             AS worksite_id,
       w.worksite_name  AS lift_name,
       l.public_id      AS lift_public_id,
       l.lift_type,
       l.lift_status,
       sa.id            AS ski_area_id,
       wa.worksite_name AS ski_area_name
FROM skiresort.lift l
         JOIN skiresort.worksite w ON w.id = l.id
         JOIN skiresort.ski_area sa ON sa.id = l.ski_area_id
         JOIN skiresort.worksite wa ON wa.id = sa.id;

alter table v_lifts_with_areas
    owner to postgres;

create view v_slopes_by_area(id, public_id, name, difficulty, length_m, ski_area_id, ski_area_name) as
SELECT s.id,
       s.public_id,
       s.name,
       s.difficulty,
       s.length_m,
       s.ski_area_id,
       wa.worksite_name AS ski_area_name
FROM skiresort.slope s
         JOIN skiresort.ski_area sa ON sa.id = s.ski_area_id
         JOIN skiresort.worksite wa ON wa.id = sa.id;

alter table v_slopes_by_area
    owner to postgres;

create function digest(text, text) returns bytea
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function digest(text, text) owner to postgres;

create function digest(bytea, text) returns bytea
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function digest(bytea, text) owner to postgres;

create function hmac(text, text, text) returns bytea
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function hmac(text, text, text) owner to postgres;

create function hmac(bytea, bytea, text) returns bytea
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function hmac(bytea, bytea, text) owner to postgres;

create function crypt(text, text) returns text
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function crypt(text, text) owner to postgres;

create function gen_salt(text) returns text
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function gen_salt(text) owner to postgres;

create function gen_salt(text, integer) returns text
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function gen_salt(text, integer) owner to postgres;

create function encrypt(bytea, bytea, text) returns bytea
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function encrypt(bytea, bytea, text) owner to postgres;

create function decrypt(bytea, bytea, text) returns bytea
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function decrypt(bytea, bytea, text) owner to postgres;

create function encrypt_iv(bytea, bytea, bytea, text) returns bytea
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function encrypt_iv(bytea, bytea, bytea, text) owner to postgres;

create function decrypt_iv(bytea, bytea, bytea, text) returns bytea
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function decrypt_iv(bytea, bytea, bytea, text) owner to postgres;

create function gen_random_bytes(integer) returns bytea
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function gen_random_bytes(integer) owner to postgres;

create function gen_random_uuid() returns uuid
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function gen_random_uuid() owner to postgres;

create function pgp_sym_encrypt(text, text) returns bytea
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function pgp_sym_encrypt(text, text) owner to postgres;

create function pgp_sym_encrypt_bytea(bytea, text) returns bytea
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function pgp_sym_encrypt_bytea(bytea, text) owner to postgres;

create function pgp_sym_encrypt(text, text, text) returns bytea
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function pgp_sym_encrypt(text, text, text) owner to postgres;

create function pgp_sym_encrypt_bytea(bytea, text, text) returns bytea
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function pgp_sym_encrypt_bytea(bytea, text, text) owner to postgres;

create function pgp_sym_decrypt(bytea, text) returns text
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function pgp_sym_decrypt(bytea, text) owner to postgres;

create function pgp_sym_decrypt_bytea(bytea, text) returns bytea
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function pgp_sym_decrypt_bytea(bytea, text) owner to postgres;

create function pgp_sym_decrypt(bytea, text, text) returns text
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function pgp_sym_decrypt(bytea, text, text) owner to postgres;

create function pgp_sym_decrypt_bytea(bytea, text, text) returns bytea
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function pgp_sym_decrypt_bytea(bytea, text, text) owner to postgres;

create function pgp_pub_encrypt(text, bytea) returns bytea
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function pgp_pub_encrypt(text, bytea) owner to postgres;

create function pgp_pub_encrypt_bytea(bytea, bytea) returns bytea
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function pgp_pub_encrypt_bytea(bytea, bytea) owner to postgres;

create function pgp_pub_encrypt(text, bytea, text) returns bytea
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function pgp_pub_encrypt(text, bytea, text) owner to postgres;

create function pgp_pub_encrypt_bytea(bytea, bytea, text) returns bytea
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function pgp_pub_encrypt_bytea(bytea, bytea, text) owner to postgres;

create function pgp_pub_decrypt(bytea, bytea) returns text
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function pgp_pub_decrypt(bytea, bytea) owner to postgres;

create function pgp_pub_decrypt_bytea(bytea, bytea) returns bytea
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function pgp_pub_decrypt_bytea(bytea, bytea) owner to postgres;

create function pgp_pub_decrypt(bytea, bytea, text) returns text
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function pgp_pub_decrypt(bytea, bytea, text) owner to postgres;

create function pgp_pub_decrypt_bytea(bytea, bytea, text) returns bytea
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function pgp_pub_decrypt_bytea(bytea, bytea, text) owner to postgres;

create function pgp_pub_decrypt(bytea, bytea, text, text) returns text
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function pgp_pub_decrypt(bytea, bytea, text, text) owner to postgres;

create function pgp_pub_decrypt_bytea(bytea, bytea, text, text) returns bytea
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function pgp_pub_decrypt_bytea(bytea, bytea, text, text) owner to postgres;

create function pgp_key_id(bytea) returns text
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function pgp_key_id(bytea) owner to postgres;

create function armor(bytea) returns text
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function armor(bytea) owner to postgres;

create function armor(bytea, text[], text[]) returns text
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function armor(bytea, text[], text[]) owner to postgres;

create function dearmor(text) returns bytea
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function dearmor(text) owner to postgres;

create function pgp_armor_headers(text, out key text, out value text) returns setof record
    immutable
    strict
    parallel safe
    language c
as
$$
begin
-- missing source code
end;
$$;

alter function pgp_armor_headers(text, out text, out text) owner to postgres;

create function set_updated_at() returns trigger
    language plpgsql
as
$$
BEGIN
  NEW.updated_at := now();
  RETURN NEW;
END;
$$;

alter function set_updated_at() owner to postgres;

create function assert_worksite_type(p_worksite_id bigint, p_expected text) returns void
    language plpgsql
as
$$
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

alter function assert_worksite_type(bigint, text) owner to postgres;

create function restaurant_type_guard() returns trigger
    language plpgsql
as
$$
BEGIN
  PERFORM assert_worksite_type(NEW.id, 'RESTAURANT');
  RETURN NEW;
END;
$$;

alter function restaurant_type_guard() owner to postgres;

create function rescue_type_guard() returns trigger
    language plpgsql
as
$$
BEGIN
  PERFORM assert_worksite_type(NEW.id, 'RESCUE_POINT');
  RETURN NEW;
END;
$$;

alter function rescue_type_guard() owner to postgres;


