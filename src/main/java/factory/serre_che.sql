-- ===============================
-- Serre Chevalier seed (compatible with fresh init.sql)
-- ===============================

-- Opening-hours helper JSON
WITH oh AS (
  SELECT '{"open":"09:00","close":"16:30"}'::jsonb AS j
)
-- 1) Insert Ski Areas as Worksites
INSERT INTO worksite (worksite_name, worksite_type, opening_hours)
SELECT * FROM (
  SELECT 'Briançon'::varchar,     'SKI_AREA'::varchar, (SELECT j FROM oh)
  UNION ALL SELECT 'Chantemerle', 'SKI_AREA', (SELECT j FROM oh)
  UNION ALL SELECT 'Villeneuve',  'SKI_AREA', (SELECT j FROM oh)
  UNION ALL SELECT 'Le Monêtier', 'SKI_AREA', (SELECT j FROM oh)
) AS t(name, type, oh)
ON CONFLICT (worksite_name, worksite_type) DO NOTHING;

-- 2) Materialize Ski Areas (link to Worksite id)
-- Elevations from your notes (min/max). Adjust if needed.
INSERT INTO ski_area (id, public_id, elevation_min_m, elevation_max_m, opening_hours)
SELECT w.id, gen_random_uuid(),
       CASE w.worksite_name
         WHEN 'Briançon'     THEN 1200
         WHEN 'Chantemerle'  THEN 1350
         WHEN 'Villeneuve'   THEN 1400
         WHEN 'Le Monêtier'  THEN 1500
       END,
       CASE w.worksite_name
         WHEN 'Briançon'     THEN 2360
         WHEN 'Chantemerle'  THEN 2491
         WHEN 'Villeneuve'   THEN 2659
         WHEN 'Le Monêtier'  THEN 2830
       END,
       w.opening_hours
FROM worksite w
WHERE w.worksite_type = 'SKI_AREA'
ON CONFLICT (id) DO NOTHING;

-- Helper: resolve ski_area id by name
WITH sa AS (
  SELECT sa.id, w.worksite_name AS name
  FROM ski_area sa JOIN worksite w ON w.id = sa.id
), oh AS (
  SELECT '{"open":"09:00","close":"16:30"}'::jsonb AS j
)
-- 3) Insert Slopes (NOT worksites)
INSERT INTO slope (ski_area_id, public_id, name, difficulty, slope_type,
                   up_x, up_y, up_z_m, down_x, down_y, down_z_m, length_m, opening_hours)
SELECT sa.id,
       gen_random_uuid(),
       s.name, s.difficulty, 'PISTE',
       0, 0, s.up_z, 0, 0, s.down_z, s.length_m,
       (SELECT '{"open":"09:00","close":"16:30"}'::jsonb)
FROM (
         SELECT sa.id, w.worksite_name AS name
         FROM ski_area sa JOIN worksite w ON w.id = sa.id
     ) sa
         JOIN (
    VALUES
        ('Briançon',    'Grande Gargouille', 'RED',   2360, 1606, 2614),
        ('Briançon',    'Vauban',            'RED',   1625, 1215, 1920),
        ('Chantemerle', 'Luc Alphand',       'BLACK', 1892, 1350, 1978),
        ('Villeneuve',  'Cucumelle',         'RED',   2510, 1775, 4036),
        ('Villeneuve',  'Route Fréjus',      'GREEN', 1940, 1490, 4312),
        ('Le Monêtier', 'Rochamout',         'BLUE',  2175, 1500, 4361)
) AS s(area_name, name, difficulty, up_z, down_z, length_m)
              ON sa.name = s.area_name
ON CONFLICT ON CONSTRAINT uq_slope_area_name DO NOTHING;


-- 4) Insert Lifts as Worksites
WITH oh AS (SELECT '{"open":"09:00","close":"16:30"}'::jsonb AS j)
INSERT INTO worksite (worksite_name, worksite_type, opening_hours)
SELECT * FROM (
  SELECT 'Prorel 2'::varchar, 'LIFT'::varchar, (SELECT j FROM oh)
  UNION ALL SELECT 'Ratier',  'LIFT', (SELECT j FROM oh)
  UNION ALL SELECT 'Vallons', 'LIFT', (SELECT j FROM oh)
  UNION ALL SELECT 'Bachas',  'LIFT', (SELECT j FROM oh)
) AS t(n, t, oh)
ON CONFLICT (worksite_name, worksite_type) DO NOTHING;

-- 5) Materialize Lifts (link to Ski Areas + geometry; no name column here)
WITH sa AS (
    SELECT sa.id, w.worksite_name AS name
    FROM ski_area sa
             JOIN worksite w ON w.id = sa.id
)
INSERT INTO lift (
    id, ski_area_id, public_id, lift_type,
    up_x, up_y, up_z_m, down_x, down_y, down_z_m,
    length_m, opening_hours, lift_status
)
SELECT
    w.id,
    sa.id,
    gen_random_uuid(),
    l.lift_type,
    0, 0, l.up_z,
    0, 0, l.down_z,
    l.length_m,
    '{"open":"09:00","close":"16:30"}'::jsonb,
    'OPEN'
FROM worksite w
         JOIN (
    VALUES
        ('Prorel 2', 'Briançon',    'GONDOLA',   2355, 1627, 2336),
        ('Ratier',   'Chantemerle', 'GONDOLA',   1888, 1350, 1610),
        ('Vallons',  'Chantemerle', 'CHAIRLIFT', 2505, 1915, 2207),
        ('Bachas',   'Le Monêtier', 'CHAIRLIFT', 2176, 1465, 2492)
) AS l(worksite_name, area_name, lift_type, up_z, down_z, length_m)
              ON w.worksite_name = l.worksite_name
                  AND w.worksite_type = 'LIFT'
         JOIN sa
              ON sa.name = l.area_name
ON CONFLICT (id) DO NOTHING;


-- 6) Restaurants (POI + Worksite)
INSERT INTO worksite (worksite_name, worksite_type)
VALUES ('Chalet de Serre Blanc','RESTAURANT'),
       ('Cabane à Sucre','RESTAURANT'),
       ('Bivouac 3200','RESTAURANT'),
       ('Flocon','RESTAURANT')
ON CONFLICT (worksite_name, worksite_type) DO NOTHING;

WITH sa AS (
  SELECT sa.id, w.worksite_name AS name FROM ski_area sa JOIN worksite w ON w.id = sa.id
), wsr AS (
  SELECT id, worksite_name FROM worksite WHERE worksite_type='RESTAURANT'
), pois AS (
  INSERT INTO poi (ski_area_id, worksite_id, name, x, y, z_m)
  SELECT sa.id, w.id, w.worksite_name, 0,0,
         CASE w.worksite_name
           WHEN 'Chalet de Serre Blanc' THEN 2200
           WHEN 'Cabane à Sucre'        THEN 2172
           WHEN 'Bivouac 3200'          THEN 2300
           WHEN 'Flocon'                THEN 2176
         END
  FROM wsr w
  JOIN sa ON sa.name = CASE w.worksite_name
                          WHEN 'Chalet de Serre Blanc' THEN 'Briançon'
                          WHEN 'Cabane à Sucre'        THEN 'Chantemerle'
                          WHEN 'Bivouac 3200'          THEN 'Villeneuve'
                          WHEN 'Flocon'                THEN 'Le Monêtier'
                        END
  ON CONFLICT DO NOTHING
  RETURNING id, worksite_id
)
INSERT INTO restaurant (id, poi_id)
SELECT worksite_id, id FROM pois
ON CONFLICT (id) DO NOTHING;

-- 7) Rescue Points (POI + Worksite)
INSERT INTO worksite (worksite_name, worksite_type)
VALUES ('Prorel','RESCUE_POINT'),
       ('Serre Ratier','RESCUE_POINT'),
       ('Méa','RESCUE_POINT'),
       ('Bachas','RESCUE_POINT')
ON CONFLICT (worksite_name, worksite_type) DO NOTHING;

WITH sa AS (
  SELECT sa.id, w.worksite_name AS name FROM ski_area sa JOIN worksite w ON w.id = sa.id
), wsr AS (
  SELECT id, worksite_name FROM worksite WHERE worksite_type='RESCUE_POINT'
), pois AS (
  INSERT INTO poi (ski_area_id, worksite_id, name, x, y, z_m)
  SELECT sa.id, w.id, w.worksite_name, 0,0,
         CASE w.worksite_name
           WHEN 'Prorel'       THEN 2360
           WHEN 'Serre Ratier' THEN 1905
           WHEN 'Méa'          THEN 2251
           WHEN 'Bachas'       THEN 2176
         END
  FROM wsr w
  JOIN sa ON sa.name = CASE w.worksite_name
                          WHEN 'Prorel'       THEN 'Briançon'
                          WHEN 'Serre Ratier' THEN 'Chantemerle'
                          WHEN 'Méa'          THEN 'Villeneuve'
                          WHEN 'Bachas'       THEN 'Le Monêtier'
                        END
  ON CONFLICT DO NOTHING
  RETURNING id, worksite_id
)
INSERT INTO rescue_point (id, poi_id)
SELECT worksite_id, id FROM pois
ON CONFLICT (id) DO NOTHING;

-- 8) Summits (POI only)
WITH sa AS (
  SELECT sa.id, w.worksite_name AS name FROM ski_area sa JOIN worksite w ON w.id = sa.id
), ins AS (
  INSERT INTO poi (ski_area_id, name, x, y, z_m)
  SELECT sa.id, p.name, 0,0, p.elevation
  FROM (VALUES
          ('Col du Prorel',           'Briançon',    2404),
          ('Serre Chevalier',         'Chantemerle', 2491),
          ('L''Eychauda',             'Villeneuve',  2659),
          ('Col de la Cucumelle',     'Le Monêtier', 2505)
       ) AS p(name, area_name, elevation)
  JOIN sa ON sa.name = p.area_name
  ON CONFLICT DO NOTHING
  RETURNING id
)
INSERT INTO summit (poi_id)
SELECT id FROM ins
ON CONFLICT (poi_id) DO NOTHING;
