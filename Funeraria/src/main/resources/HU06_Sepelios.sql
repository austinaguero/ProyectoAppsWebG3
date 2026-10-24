/* HU-06 Programación de Sepelios
   1. Ajustes de esquema en fide_sepelios_tb
   2. Datos de catálogo y de prueba para poder programar un sepelio */

USE Proyecto;

/* ===== 1. Ajustes de esquema ===== */

-- Llave autoincremental para usar @GeneratedValue(IDENTITY) igual que en Tienda
ALTER TABLE fide_sepelios_tb
    MODIFY id_sepelio INT NOT NULL AUTO_INCREMENT;

-- La unidad de sepultura se asigna después (HU-08 / HU-09)
ALTER TABLE fide_sepelios_tb
    MODIFY id_unidad_sepultura INT NULL;

-- Colaborador responsable del sepelio (HU-06 y HU-07)
ALTER TABLE fide_sepelios_tb
    ADD COLUMN cedula_responsable VARCHAR(20) NOT NULL AFTER hora_sepelio,
    ADD CONSTRAINT fide_sepelios_cedula_responsable_fk
        FOREIGN KEY (cedula_responsable) REFERENCES fide_usuarios_tb (cedula_usuario);

/* ===== 2. Catálogos ===== */

INSERT INTO fide_estado_tb (id_estado, nombre_estado) VALUES
    (1, 'Activo'),
    (2, 'Inactivo'),
    (3, 'Programado'),
    (4, 'Disponible'),
    (5, 'Reservada'),
    (6, 'Ocupada');

INSERT INTO fide_roles_tb (id_rol, nombre_rol, descripcion, id_estado) VALUES
    (1, 'Administrador', 'Acceso total al sistema', 1),
    (2, 'Personal operativo', 'Gestiona fallecidos, servicios y sepelios', 1),
    (3, 'Encargado de ventas', 'Gestiona clientes y contratos', 1),
    (4, 'Contador', 'Gestiona cobros y financiamientos', 1);

INSERT INTO fide_tipos_servicio_tb (id_tipo_servicio, nombre_tipo, id_estado) VALUES
    (1, 'Velación', 1),
    (2, 'Sepelio', 1),
    (3, 'Cremación', 1);

/* ===== 3. Datos de prueba ===== */

INSERT INTO fide_personas_tb (cedula, nombre, apellido_1, apellido_2, id_estado) VALUES
    ('101110111', 'Lucía', 'Durán', 'Mora', 1),         -- colaboradora
    ('202220222', 'Carlos', 'Rojas', 'Solís', 1),       -- colaborador
    ('303330333', 'Jorge', 'Araya', 'Vargas', 1),       -- cliente
    ('404440444', 'Rafael', 'Solís', 'Araya', 1),       -- fallecido
    ('505550555', 'Rosa', 'Montero', 'Araya', 1);       -- fallecida

INSERT INTO fide_usuarios_tb (cedula_usuario, usuario, contrasena, id_rol, id_estado) VALUES
    ('101110111', 'lduran', '{noop}cambiar', 2, 1),
    ('202220222', 'crojas', '{noop}cambiar', 2, 1);

INSERT INTO fide_clientes_tb (cedula_cliente, referencia_familiar, id_estado) VALUES
    ('303330333', 'Familia Araya', 1);

INSERT INTO fide_fallecidos_tb (cedula_fallecido, cedula_cliente, fecha_nacimiento, fecha_fallecimiento, lugar_fallecimiento, causa_fallecimiento, id_estado) VALUES
    ('404440444', '303330333', '1950-04-12', '2026-10-20', 'Hospital México', 'Causas naturales', 1),
    ('505550555', '303330333', '1948-09-03', '2026-10-21', 'Domicilio', 'Causas naturales', 1);

-- Servicios activos con su fallecido asociado (precondiciones de la HU-06)
INSERT INTO fide_servicios_tb (id_servicio, cedula_fallecido, id_tipo_servicio, descripcion, fecha_solicitud, id_estado) VALUES
    (1, '404440444', 2, 'Sepelio Rafael Solís', '2026-10-21', 1),
    (2, '505550555', 2, 'Sepelio Rosa Montero', '2026-10-22', 1);
