/* HU-08 Consulta de Unidades de Sepultura Disponibles
   Datos de prueba: tipos de unidad, cementerios y unidades por sector.
   Estados usados (fide_estado_tb): 1 Activo, 4 Disponible, 5 Reservada, 6 Ocupada.
   El "Sector B" de Jardín del Recuerdo no tiene unidades disponibles (criterio 2). */

USE Proyecto;

INSERT INTO fide_tipos_unidad_tb (id_tipo_unidad, nombre_tipo_unidad, id_estado) VALUES
    (1, 'Nicho', 1),
    (2, 'Bóveda', 1),
    (3, 'Mausoleo', 1);

INSERT INTO fide_cementerios_tb (id_cementerio, nombre_cementerio, id_direccion_cementerio, capacidad_total, id_estado) VALUES
    (1, 'Jardín del Recuerdo', NULL, 300, 1),
    (2, 'Campo Santo La Piedad', NULL, 200, 1);

INSERT INTO fide_unidades_sepultura_tb (id_unidad_sepultura, id_cementerio, id_tipo_unidad, sector, bloque, numero, capacidad, id_estado) VALUES
    -- Jardín del Recuerdo, Sector A: dos disponibles
    (1,  1, 1, 'Sector A', 'Bloque 1', 1, 1, 4),
    (2,  1, 1, 'Sector A', 'Bloque 1', 2, 1, 4),
    (3,  1, 1, 'Sector A', 'Bloque 1', 3, 1, 6),
    (4,  1, 1, 'Sector A', 'Bloque 2', 1, 1, 5),
    -- Jardín del Recuerdo, Sector B: ninguna disponible
    (5,  1, 2, 'Sector B', 'Bloque 1', 1, 4, 6),
    (6,  1, 2, 'Sector B', 'Bloque 1', 2, 4, 6),
    (7,  1, 2, 'Sector B', 'Bloque 1', 3, 4, 5),
    -- Jardín del Recuerdo, Sector C: dos disponibles
    (8,  1, 2, 'Sector C', 'Bloque 3', 1, 4, 4),
    (9,  1, 3, 'Sector C', 'Bloque 3', 2, 8, 4),
    (10, 1, 2, 'Sector C', 'Bloque 3', 3, 4, 6),
    -- Campo Santo La Piedad, Norte: una disponible
    (11, 2, 1, 'Norte', 'Pabellón 1', 10, 1, 4),
    (12, 2, 1, 'Norte', 'Pabellón 1', 11, 1, 6),
    (13, 2, 1, 'Norte', 'Pabellón 1', 12, 1, 6),
    -- Campo Santo La Piedad, Sur: dos disponibles
    (14, 2, 2, 'Sur', 'Pabellón 2', 1, 4, 4),
    (15, 2, 2, 'Sur', 'Pabellón 2', 2, 4, 4);
