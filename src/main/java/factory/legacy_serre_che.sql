-- First insert the ski areas as worksites
INSERT INTO worksite (worksite_name, worksite_type, opening_hours) VALUES
                                                                       ('Briançon', 'SKI_AREA', '09:00-16:30'),
                                                                       ('Chantemerle', 'SKI_AREA', '09:00-16:30'),
                                                                       ('Villeneuve', 'SKI_AREA', '09:00-16:30'),
                                                                       ('Le Monêtier', 'SKI_AREA', '09:00-16:30');

-- Then create the ski areas
INSERT INTO ski_area (id, public_id, name, up, down, opening_hours)
SELECT id, gen_random_uuid(), worksite_name, 1000, 500, opening_hours
FROM worksite
WHERE worksite_type = 'SKI_AREA';

-- Insert slopes
INSERT INTO slope (ski_area_id, public_id, name, difficulty, slope_type, up_x, up_y, up_z, down_x, down_y, down_z, length, opening_hours) VALUES
                                                                                                                                              ((SELECT id FROM ski_area WHERE name = 'Briançon'), gen_random_uuid(), 'Grande Gargouille', 'RED', 'PISTE', 0, 0, 2360, 0, 0, 1606, 2614.0, '09:00-16:30'),
                                                                                                                                              ((SELECT id FROM ski_area WHERE name = 'Briançon'), gen_random_uuid(), 'Vauban', 'RED', 'PISTE', 0, 0, 1625, 0, 0, 1215, 1920.0, '09:00-16:30'),
                                                                                                                                              ((SELECT id FROM ski_area WHERE name = 'Chantemerle'), gen_random_uuid(), 'Luc Alphand', 'BLACK', 'PISTE', 0, 0, 1892, 0, 0, 1350, 1978.0, '09:00-16:30'),
                                                                                                                                              ((SELECT id FROM ski_area WHERE name = 'Villeneuve'), gen_random_uuid(), 'Cucumelle', 'RED', 'PISTE', 0, 0, 2510, 0, 0, 1775, 4036.0, '09:00-16:30'),
                                                                                                                                              ((SELECT id FROM ski_area WHERE name = 'Villeneuve'), gen_random_uuid(), 'Route Fréjus', 'GREEN', 'PISTE', 0, 0, 1940, 0, 0, 1490, 4312.0, '09:00-16:30'),
                                                                                                                                              ((SELECT id FROM ski_area WHERE name = 'Le Monêtier'), gen_random_uuid(), 'Rochamout', 'BLUE', 'PISTE', 0, 0, 2175, 0, 0, 1500, 4361.0, '09:00-16:30');

-- Insert lifts (first as worksites)
INSERT INTO worksite (worksite_name, worksite_type, opening_hours) VALUES
                                                                       ('Prorel 2', 'LIFT', '09:00-16:30'),
                                                                       ('Ratier', 'LIFT', '09:00-16:30'),
                                                                       ('Vallons', 'LIFT', '09:00-16:30'),
                                                                       ('Bachas', 'LIFT', '09:00-16:30');

-- Then create the lifts
INSERT INTO lift (id, name, ski_area_id, public_id, lift_type, up_x, up_y, up_z, down_x, down_y, down_z, length, opening_hours) VALUES
                                                                                                                                    ((SELECT id FROM worksite WHERE worksite_name = 'Prorel 2'), 'Prorel 2',
                                                                                                                                     (SELECT id FROM ski_area WHERE name = 'Briançon'), gen_random_uuid(), 'GONDOLA',
                                                                                                                                     0, 0, 2355, 0, 0, 1627, 2336.0, '09:00-16:30'),

                                                                                                                                    ((SELECT id FROM worksite WHERE worksite_name = 'Ratier'), 'Ratier',
                                                                                                                                     (SELECT id FROM ski_area WHERE name = 'Chantemerle'), gen_random_uuid(), 'GONDOLA',
                                                                                                                                     0, 0, 1888, 0, 0, 1350, 1610.0, '09:00-16:30'),

                                                                                                                                    ((SELECT id FROM worksite WHERE worksite_name = 'Vallons'), 'Vallons',
                                                                                                                                     (SELECT id FROM ski_area WHERE name = 'Chantemerle'), gen_random_uuid(), 'CHAIRLIFT',
                                                                                                                                     0, 0, 2505, 0, 0, 1915, 2207.0, '09:00-16:30'),

                                                                                                                                    ((SELECT id FROM worksite WHERE worksite_name = 'Bachas'), 'Bachas',
                                                                                                                                     (SELECT id FROM ski_area WHERE name = 'Le Monêtier'), gen_random_uuid(), 'CHAIRLIFT',
                                                                                                                                     0, 0, 2176, 0, 0, 1465, 2492.0, '09:00-16:30');


-- Insert restaurants (first as worksites)
INSERT INTO worksite (worksite_name, worksite_type) VALUES
                                                        ('Chalet de Serre Blanc', 'RESTAURANT'),
                                                        ('Cabane à Sucre', 'RESTAURANT'),
                                                        ('Bivouac 3200', 'RESTAURANT'),
                                                        ('Flocon', 'RESTAURANT');

-- Then create POIs and restaurants
WITH restaurant_pois AS (
    INSERT INTO poi (ski_area_id, worksite_id, name, x, y, z_m)
        SELECT s.id, w.id, w.worksite_name, 0, 0,
               CASE w.worksite_name
                   WHEN 'Chalet de Serre Blanc' THEN 2200
                   WHEN 'Cabane à Sucre' THEN 2172
                   WHEN 'Bivouac 3200' THEN 2300
                   WHEN 'Flocon' THEN 2176
                   END
        FROM worksite w
                 JOIN ski_area s ON (
            CASE w.worksite_name
                WHEN 'Chalet de Serre Blanc' THEN 'Briançon'
                WHEN 'Cabane à Sucre' THEN 'Chantemerle'
                WHEN 'Bivouac 3200' THEN 'Villeneuve'
                WHEN 'Flocon' THEN 'Le Monêtier'
                END = s.name
            )
        WHERE w.worksite_type = 'RESTAURANT'
        RETURNING id, worksite_id
)
INSERT INTO restaurant (id, poi_id)
SELECT worksite_id, id FROM restaurant_pois;

-- Insert rescue points similarly
INSERT INTO worksite (worksite_name, worksite_type) VALUES
                                                        ('Prorel', 'RESCUE_POINT'),
                                                        ('Serre Ratier', 'RESCUE_POINT'),
                                                        ('Méa', 'RESCUE_POINT'),
                                                        ('Bachas', 'RESCUE_POINT');

WITH rescue_pois AS (
    INSERT INTO poi (ski_area_id, worksite_id, name, x, y, z_m)
        SELECT s.id, w.id, w.worksite_name, 0, 0,
               CASE w.worksite_name
                   WHEN 'Prorel' THEN 2360
                   WHEN 'Serre Ratier' THEN 1905
                   WHEN 'Méa' THEN 2251
                   WHEN 'Bachas' THEN 2176
                   END
        FROM worksite w
                 JOIN ski_area s ON (
            CASE w.worksite_name
                WHEN 'Prorel' THEN 'Briançon'
                WHEN 'Serre Ratier' THEN 'Chantemerle'
                WHEN 'Méa' THEN 'Villeneuve'
                WHEN 'Bachas' THEN 'Le Monêtier'
                END = s.name
            )
        WHERE w.worksite_type = 'RESCUE_POINT'
        RETURNING id, worksite_id
)
INSERT INTO rescue_point (id, poi_id)
SELECT worksite_id, id FROM rescue_pois;

-- Insert summits (POIs only, not worksites)
INSERT INTO poi (ski_area_id, name, x, y, z_m)
SELECT s.id, p.name, 0, 0, p.elevation
FROM (VALUES
          ('Col du Prorel', 'Briançon', 2404),
          ('Serre Chevalier', 'Chantemerle', 2491),
          ('L''Eychauda', 'Villeneuve', 2659),
          ('Col de la Cucumelle', 'Le Monêtier', 2505)
     ) AS p(name, area_name, elevation)
         JOIN ski_area s ON s.name = p.area_name;

INSERT INTO summit (poi_id)
SELECT id FROM poi
WHERE name IN ('Col du Prorel', 'Serre Chevalier', 'L''Eychauda', 'Col de la Cucumelle');
