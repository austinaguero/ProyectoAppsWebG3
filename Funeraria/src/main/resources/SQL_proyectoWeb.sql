/* =====================================================================
   Proyecto Funeraria G3 - Base de datos "Proyecto" (MySQL)
   Script único: estructura + catálogos + datos de prueba.

   REGLA: todo cambio a la base de datos se agrega en este archivo.

   Historial de cambios sobre el script inicial:
   - 2026-10-23 HU-06 Programación de sepelios:
       * fide_sepelios_tb.id_sepelio AUTO_INCREMENT.
       * fide_sepelios_tb.cedula_responsable (colaborador responsable, FK a fide_usuarios_tb).
       * fide_sepelios_tb.id_unidad_sepultura acepta NULL (la unidad se asigna en la HU-09).
   - 2026-10-23 Mantenimiento de servicios funerarios:
       * fide_servicios_tb.id_servicio AUTO_INCREMENT.
   - 2026-10-23 HU-09 Reserva de unidad / mantenimiento de unidades:
       * fide_unidades_sepultura_tb.id_unidad_sepultura AUTO_INCREMENT.
       * UNIQUE fide_sepelios_id_unidad_sepultura_uq: una unidad no puede quedar
         asignada a dos sepelios (la base rechaza la asignación doble).
   - 2026-10-29 Mantenimiento de unidades:
       * UNIQUE fide_unidades_sepultura_ubicacion_uq (id_cementerio, sector, bloque, numero):
         la base rechaza una unidad repetida en la misma ubicación.
   - 2026-10-29 HU-04 Registrar fallecido: segundo cliente de prueba (Ana Rojas Mora).
   - 2026-10-29 Dirección de domicilio del fallecido:
       * fide_canton_tb.id_provincia y fide_distrito_tb.id_canton (con su FK) para que
         el cantón dependa de la provincia y el distrito del cantón.
       * Catálogo completo de provincias (7), cantones (84) y distritos (492) de Costa Rica,
         con los códigos oficiales como llave.
   - 2026-11-03 Se aplicó en Aiven la FK fide_clientes_cedula_cliente_fk (clientes -> personas),
     que ya estaba en este script pero faltaba en la base creada en Aiven.
   - 2026-10-23 Catálogos y datos de prueba (estados, roles, tipos, personas,
     usuarios, cliente, fallecidos, servicios, cementerios y unidades).
   ===================================================================== */

SET FOREIGN_KEY_CHECKS = 0;

USE Proyecto;

CREATE TABLE IF NOT EXISTS fide_estado_tb (
    id_estado INT NOT NULL,
    nombre_estado VARCHAR(100),
    CONSTRAINT fide_estado_id_estado_pk PRIMARY KEY (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_personas_tb (
    cedula VARCHAR(20) NOT NULL,
    nombre VARCHAR(100),
    apellido_1 VARCHAR(100),
    apellido_2 VARCHAR(100),
    id_estado INT NOT NULL,
    CONSTRAINT fide_personas_cedula_pk PRIMARY KEY (cedula),
    CONSTRAINT fide_personas_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_correo_tb (
    cedula VARCHAR(20) NOT NULL,
    nombre_correo VARCHAR(100) NOT NULL,
    id_estado INT NOT NULL,
    CONSTRAINT fide_correo_id_pk PRIMARY KEY (cedula, nombre_correo),
    CONSTRAINT fide_correo_cedula_fk FOREIGN KEY (cedula) REFERENCES fide_personas_tb (cedula),
    CONSTRAINT fide_correo_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_telefono_tb (
    cedula VARCHAR(20) NOT NULL,
    num_telefono VARCHAR(20) NOT NULL,
    id_estado INT NOT NULL,
    CONSTRAINT fide_telefono_id_pk PRIMARY KEY (cedula, num_telefono),
    CONSTRAINT fide_telefono_cedula_fk FOREIGN KEY (cedula) REFERENCES fide_personas_tb (cedula),
    CONSTRAINT fide_telefono_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_provincia_tb (
    id_provincia INT NOT NULL,
    nombre_provincia VARCHAR(100),
    id_estado INT NOT NULL,
    CONSTRAINT fide_provincia_id_provincia_pk PRIMARY KEY (id_provincia),
    CONSTRAINT fide_provincia_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_canton_tb (
    id_canton INT NOT NULL,
    nombre_canton VARCHAR(100),
    -- Cambio: provincia a la que pertenece el cantón
    id_provincia INT NOT NULL,
    id_estado INT NOT NULL,
    CONSTRAINT fide_canton_id_canton_pk PRIMARY KEY (id_canton),
    CONSTRAINT fide_canton_id_provincia_fk FOREIGN KEY (id_provincia) REFERENCES fide_provincia_tb (id_provincia),
    CONSTRAINT fide_canton_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_distrito_tb (
    id_distrito INT NOT NULL,
    nombre_distrito VARCHAR(100),
    -- Cambio: cantón al que pertenece el distrito
    id_canton INT NOT NULL,
    id_estado INT NOT NULL,
    CONSTRAINT fide_distrito_id_distrito_pk PRIMARY KEY (id_distrito),
    CONSTRAINT fide_distrito_id_canton_fk FOREIGN KEY (id_canton) REFERENCES fide_canton_tb (id_canton),
    CONSTRAINT fide_distrito_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_direcciones_tb (
    cedula VARCHAR(20) NOT NULL,
    id_provincia INT NOT NULL,
    id_canton INT NOT NULL,
    id_distrito INT NOT NULL,
    otras_sennas VARCHAR(200),
    id_estado INT NOT NULL,
    CONSTRAINT fide_direcciones_id_pk PRIMARY KEY (cedula, id_provincia, id_canton, id_distrito),
    CONSTRAINT fide_direcciones_cedula_fk FOREIGN KEY (cedula) REFERENCES fide_personas_tb (cedula),
    CONSTRAINT fide_direcciones_id_provincia_fk FOREIGN KEY (id_provincia) REFERENCES fide_provincia_tb (id_provincia),
    CONSTRAINT fide_direcciones_id_canton_fk FOREIGN KEY (id_canton) REFERENCES fide_canton_tb (id_canton),
    CONSTRAINT fide_direcciones_id_distrito_fk FOREIGN KEY (id_distrito) REFERENCES fide_distrito_tb (id_distrito),
    CONSTRAINT fide_direcciones_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_clientes_tb (
    cedula_cliente VARCHAR(20) NOT NULL,
    fecha_registro DATE NOT NULL DEFAULT (CURRENT_DATE),
    referencia_familiar VARCHAR(100),
    id_estado INT NOT NULL,
    CONSTRAINT fide_clientes_cedula_cliente_pk PRIMARY KEY (cedula_cliente),
    CONSTRAINT fide_clientes_cedula_cliente_fk FOREIGN KEY (cedula_cliente) REFERENCES fide_personas_tb (cedula),
    CONSTRAINT fide_clientes_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_fallecidos_tb (
    cedula_fallecido VARCHAR(20) NOT NULL,
    cedula_cliente VARCHAR(20) NOT NULL,
    fecha_nacimiento DATE NOT NULL DEFAULT (CURRENT_DATE),
    fecha_fallecimiento DATE NOT NULL DEFAULT (CURRENT_DATE),
    lugar_fallecimiento VARCHAR(150),
    causa_fallecimiento VARCHAR(150),
    id_estado INT NOT NULL,
    CONSTRAINT fide_fallecidos_cedula_fallecido_pk PRIMARY KEY (cedula_fallecido),
    CONSTRAINT fide_fallecidos_cedula_fallecido_fk FOREIGN KEY (cedula_fallecido) REFERENCES fide_personas_tb (cedula),
    CONSTRAINT fide_fallecidos_cedula_cliente_fk FOREIGN KEY (cedula_cliente) REFERENCES fide_clientes_tb (cedula_cliente),
    CONSTRAINT fide_fallecidos_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_tipos_servicio_tb (
    id_tipo_servicio INT NOT NULL,
    nombre_tipo VARCHAR(100),
    id_estado INT NOT NULL,
    CONSTRAINT fide_tipos_servicio_id_tipo_servicio_pk PRIMARY KEY (id_tipo_servicio),
    CONSTRAINT fide_tipos_servicio_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_tipo_metodo_pago_tb (
    id_metodo_pago INT NOT NULL,
    nombre_metodo_pago VARCHAR(100),
    id_estado INT NOT NULL,
    CONSTRAINT fide_tipo_metodo_pago_id_metodo_pago_pk PRIMARY KEY (id_metodo_pago),
    CONSTRAINT fide_tipo_metodo_pago_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_servicios_costos_tb (
    id_servicio_costo INT NOT NULL,
    id_tipo_servicio INT NOT NULL,
    costo DECIMAL(10, 2),
    id_estado INT NOT NULL,
    CONSTRAINT fide_servicios_costos_id_servicio_costo_pk PRIMARY KEY (id_servicio_costo),
    CONSTRAINT fide_servicios_costos_id_tipo_servicio_fk FOREIGN KEY (id_tipo_servicio) REFERENCES fide_tipos_servicio_tb (id_tipo_servicio),
    CONSTRAINT fide_servicios_costos_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_planes_prevision_tb (
    id_plan INT NOT NULL,
    nombre_plan VARCHAR(100),
    id_plan_servicio INT NULL,
    vigencia_inicio DATE NOT NULL DEFAULT (CURRENT_DATE),
    vigencia_fin DATE NOT NULL DEFAULT (CURRENT_DATE),
    id_estado INT NOT NULL,
    CONSTRAINT fide_planes_prevision_id_plan_pk PRIMARY KEY (id_plan),
    CONSTRAINT fide_planes_prevision_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_planes_servicios_tb (
    id_plan_servicio INT NOT NULL,
    id_plan INT NOT NULL,
    id_tipo_servicio INT NOT NULL,
    id_estado INT NOT NULL,
    CONSTRAINT fide_planes_servicios_id_pk PRIMARY KEY (id_plan_servicio, id_tipo_servicio),
    CONSTRAINT fide_planes_servicios_id_plan_servicio_uq UNIQUE (id_plan_servicio),
    CONSTRAINT fide_planes_servicios_id_plan_fk FOREIGN KEY (id_plan) REFERENCES fide_planes_prevision_tb (id_plan),
    CONSTRAINT fide_planes_servicios_id_tipo_servicio_fk FOREIGN KEY (id_tipo_servicio) REFERENCES fide_tipos_servicio_tb (id_tipo_servicio),
    CONSTRAINT fide_planes_servicios_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

ALTER TABLE fide_planes_prevision_tb
    ADD CONSTRAINT fide_planes_prevision_id_plan_servicio_fk
    FOREIGN KEY (id_plan_servicio) REFERENCES fide_planes_servicios_tb (id_plan_servicio);

CREATE TABLE IF NOT EXISTS fide_planes_costos_tb (
    id_plan_costo INT NOT NULL,
    id_plan_servicio INT NOT NULL,
    costo DECIMAL(10, 2),
    id_estado INT NOT NULL,
    CONSTRAINT fide_planes_costos_id_plan_costo_pk PRIMARY KEY (id_plan_costo),
    CONSTRAINT fide_planes_costos_id_plan_servicio_fk FOREIGN KEY (id_plan_servicio) REFERENCES fide_planes_servicios_tb (id_plan_servicio),
    CONSTRAINT fide_planes_costos_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_servicios_tb (
    -- Cambio: AUTO_INCREMENT para crear servicios desde el sistema
    id_servicio INT NOT NULL AUTO_INCREMENT,
    cedula_fallecido VARCHAR(20) NOT NULL,
    id_tipo_servicio INT NOT NULL,
    descripcion VARCHAR(200),
    fecha_solicitud DATE NOT NULL DEFAULT (CURRENT_DATE),
    id_estado INT NOT NULL,
    CONSTRAINT fide_servicios_id_servicio_pk PRIMARY KEY (id_servicio),
    CONSTRAINT fide_servicios_cedula_fallecido_fk FOREIGN KEY (cedula_fallecido) REFERENCES fide_fallecidos_tb (cedula_fallecido),
    CONSTRAINT fide_servicios_id_tipo_servicio_fk FOREIGN KEY (id_tipo_servicio) REFERENCES fide_tipos_servicio_tb (id_tipo_servicio),
    CONSTRAINT fide_servicios_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_financiamientos_tb (
    id_financiamiento INT NOT NULL,
    id_plan INT NOT NULL,
    cedula_cliente VARCHAR(20) NOT NULL,
    monto_total DECIMAL(10, 2),
    cantidad_cuotas INT,
    saldo_pendiente DECIMAL(10, 2),
    fecha_inicio DATE NOT NULL DEFAULT (CURRENT_DATE),
    id_estado INT NOT NULL,
    CONSTRAINT fide_financiamientos_id_financiamiento_pk PRIMARY KEY (id_financiamiento),
    CONSTRAINT fide_financiamientos_id_plan_fk FOREIGN KEY (id_plan) REFERENCES fide_planes_prevision_tb (id_plan),
    CONSTRAINT fide_financiamientos_cedula_cliente_fk FOREIGN KEY (cedula_cliente) REFERENCES fide_clientes_tb (cedula_cliente),
    CONSTRAINT fide_financiamientos_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_cuotas_tb (
    id_cuota INT NOT NULL,
    id_financiamiento INT NOT NULL,
    numero_cuota INT,
    monto DECIMAL(10, 2),
    fecha_vencimiento DATE NOT NULL DEFAULT (CURRENT_DATE),
    id_estado INT NOT NULL,
    CONSTRAINT fide_cuotas_id_cuota_pk PRIMARY KEY (id_cuota),
    CONSTRAINT fide_cuotas_id_financiamiento_fk FOREIGN KEY (id_financiamiento) REFERENCES fide_financiamientos_tb (id_financiamiento),
    CONSTRAINT fide_cuotas_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_pagos_tb (
    id_pago INT NOT NULL,
    id_cuota INT NOT NULL,
    id_metodo_pago INT NOT NULL,
    fecha_pago DATE NOT NULL DEFAULT (CURRENT_DATE),
    monto_pagado DECIMAL(10, 2),
    num_comprobante VARCHAR(50),
    id_estado INT NOT NULL,
    CONSTRAINT fide_pagos_id_pago_pk PRIMARY KEY (id_pago),
    CONSTRAINT fide_pagos_id_cuota_fk FOREIGN KEY (id_cuota) REFERENCES fide_cuotas_tb (id_cuota),
    CONSTRAINT fide_pagos_id_metodo_pago_fk FOREIGN KEY (id_metodo_pago) REFERENCES fide_tipo_metodo_pago_tb (id_metodo_pago),
    CONSTRAINT fide_pagos_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_roles_tb (
    id_rol INT NOT NULL,
    nombre_rol VARCHAR(100) NOT NULL,
    descripcion VARCHAR(200),
    id_estado INT NOT NULL,
    CONSTRAINT fide_roles_id_rol_pk PRIMARY KEY (id_rol),
    CONSTRAINT fide_roles_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_usuarios_tb (
    cedula_usuario VARCHAR(20) NOT NULL,
    usuario VARCHAR(50) NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    fecha_creacion DATE NOT NULL DEFAULT (CURRENT_DATE),
    id_rol INT NOT NULL,
    id_estado INT NOT NULL,
    CONSTRAINT fide_usuarios_cedula_usuario_pk PRIMARY KEY (cedula_usuario),
    CONSTRAINT fide_usuarios_usuario_uq UNIQUE (usuario),
    CONSTRAINT fide_usuarios_cedula_fk FOREIGN KEY (cedula_usuario) REFERENCES fide_personas_tb (cedula),
    CONSTRAINT fide_usuarios_id_rol_fk FOREIGN KEY (id_rol) REFERENCES fide_roles_tb (id_rol),
    CONSTRAINT fide_usuarios_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_direcciones_cementerios_tb (
    id_direccion_cementerio INT NOT NULL,
    id_provincia INT NOT NULL,
    id_canton INT NOT NULL,
    id_distrito INT NOT NULL,
    otras_sennas VARCHAR(200),
    id_estado INT NOT NULL,
    CONSTRAINT fide_direcciones_cementerios_id_pk PRIMARY KEY (id_direccion_cementerio, id_provincia, id_canton, id_distrito),
    CONSTRAINT fide_direcciones_cementerios_id_direccion_cementerio_uq UNIQUE (id_direccion_cementerio),
    CONSTRAINT fide_direcciones_cementerios_id_provincia_fk FOREIGN KEY (id_provincia) REFERENCES fide_provincia_tb (id_provincia),
    CONSTRAINT fide_direcciones_cementerios_id_canton_fk FOREIGN KEY (id_canton) REFERENCES fide_canton_tb (id_canton),
    CONSTRAINT fide_direcciones_cementerios_id_distrito_fk FOREIGN KEY (id_distrito) REFERENCES fide_distrito_tb (id_distrito),
    CONSTRAINT fide_direcciones_cementerios_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_cementerios_tb (
    id_cementerio INT NOT NULL,
    nombre_cementerio VARCHAR(100) NOT NULL,
    id_direccion_cementerio INT NULL,
    capacidad_total INT NOT NULL,
    id_estado INT NOT NULL,
    CONSTRAINT fide_cementerios_id_cementerio_pk PRIMARY KEY (id_cementerio),
    CONSTRAINT fide_cementerios_id_direccion_cementerio_fk FOREIGN KEY (id_direccion_cementerio) REFERENCES fide_direcciones_cementerios_tb (id_direccion_cementerio),
    CONSTRAINT fide_cementerios_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_tipos_unidad_tb (
    id_tipo_unidad INT NOT NULL,
    nombre_tipo_unidad VARCHAR(100) NOT NULL,
    id_estado INT NOT NULL,
    CONSTRAINT fide_tipos_unidad_id_id_tipo_unidad_pk PRIMARY KEY (id_tipo_unidad),
    CONSTRAINT fide_tipos_unidad_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_unidades_sepultura_tb (
    -- Cambio: AUTO_INCREMENT para registrar unidades desde el sistema
    id_unidad_sepultura INT NOT NULL AUTO_INCREMENT,
    id_cementerio INT NOT NULL,
    id_tipo_unidad INT NOT NULL,
    sector VARCHAR(100) NOT NULL,
    bloque VARCHAR(100) NOT NULL,
    numero INT NOT NULL,
    capacidad INT NOT NULL,
    id_estado INT NOT NULL,
    CONSTRAINT fide_unidades_sepultura_id_unidad_sepultura_pk PRIMARY KEY (id_unidad_sepultura),
    -- Cambio: no se puede registrar dos veces la misma ubicación
    CONSTRAINT fide_unidades_sepultura_ubicacion_uq UNIQUE (id_cementerio, sector, bloque, numero),
    CONSTRAINT fide_unidades_sepultura_id_cementerio_fk FOREIGN KEY (id_cementerio) REFERENCES fide_cementerios_tb (id_cementerio),
    CONSTRAINT fide_unidades_sepultura_id_tipo_unidad_fk FOREIGN KEY (id_tipo_unidad) REFERENCES fide_tipos_unidad_tb (id_tipo_unidad),
    CONSTRAINT fide_unidades_sepultura_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_sepelios_tb (
    -- Cambio HU-06: AUTO_INCREMENT para programar sepelios desde el sistema
    id_sepelio INT NOT NULL AUTO_INCREMENT,
    id_servicio INT NOT NULL,
    -- Cambio HU-06: acepta NULL, la unidad se asigna en la HU-09
    id_unidad_sepultura INT NULL,
    fecha_sepelio DATE NOT NULL DEFAULT (CURRENT_DATE),
    hora_sepelio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- Cambio HU-06: colaborador responsable del sepelio (también se usa en la HU-07)
    cedula_responsable VARCHAR(20) NOT NULL,
    cedula_fallecido VARCHAR(20) NOT NULL,
    id_estado INT NOT NULL,
    CONSTRAINT fide_sepelios_id_sepelio_pk PRIMARY KEY (id_sepelio),
    -- Cambio HU-09: una unidad no puede quedar asignada a dos sepelios (los NULL no cuentan)
    CONSTRAINT fide_sepelios_id_unidad_sepultura_uq UNIQUE (id_unidad_sepultura),
    CONSTRAINT fide_sepelios_id_servicio_fk FOREIGN KEY (id_servicio) REFERENCES fide_servicios_tb (id_servicio),
    CONSTRAINT fide_sepelios_id_unidad_sepultura_fk FOREIGN KEY (id_unidad_sepultura) REFERENCES fide_unidades_sepultura_tb (id_unidad_sepultura),
    CONSTRAINT fide_sepelios_cedula_responsable_fk FOREIGN KEY (cedula_responsable) REFERENCES fide_usuarios_tb (cedula_usuario),
    CONSTRAINT fide_sepelios_cedula_fallecido_fk FOREIGN KEY (cedula_fallecido) REFERENCES fide_fallecidos_tb (cedula_fallecido),
    CONSTRAINT fide_sepelios_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_productos_tb (
    id_producto INT NOT NULL,
    nombre_producto VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    precio_unitario DECIMAL(10, 2),
    stock_actual INT NOT NULL,
    stock_minimo INT NOT NULL,
    id_estado INT NOT NULL,
    CONSTRAINT fide_productos_id_producto_pk PRIMARY KEY (id_producto),
    CONSTRAINT fide_productos_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_ventas_tb (
    id_venta INT NOT NULL,
    cedula_cliente VARCHAR(20) NOT NULL,
    fecha_venta DATE NOT NULL DEFAULT (CURRENT_DATE),
    total_venta DECIMAL(10, 2) NOT NULL,
    id_estado INT NOT NULL,
    CONSTRAINT fide_ventas_id_venta_pk PRIMARY KEY (id_venta),
    CONSTRAINT fide_ventas_cedula_cliente_fk FOREIGN KEY (cedula_cliente) REFERENCES fide_clientes_tb (cedula_cliente),
    CONSTRAINT fide_ventas_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_facturacion_tb (
    id_factura INT NOT NULL,
    id_venta INT NOT NULL,
    cedula_cliente VARCHAR(20) NOT NULL,
    fecha_emision DATE NOT NULL DEFAULT (CURRENT_DATE),
    monto_total DECIMAL(10, 2) NOT NULL,
    id_estado INT NOT NULL,
    CONSTRAINT fide_facturacion_id_factura_pk PRIMARY KEY (id_factura),
    CONSTRAINT fide_facturacion_id_venta_fk FOREIGN KEY (id_venta) REFERENCES fide_ventas_tb (id_venta),
    CONSTRAINT fide_facturacion_cedula_cliente_fk FOREIGN KEY (cedula_cliente) REFERENCES fide_clientes_tb (cedula_cliente),
    CONSTRAINT fide_facturacion_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_servicio_producto_tb (
    id_servicio INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT,
    subtotal DECIMAL(10, 2),
    id_estado INT NOT NULL,
    CONSTRAINT fide_servicio_producto_id_pk PRIMARY KEY (id_servicio, id_producto),
    CONSTRAINT fide_servicio_producto_id_servicio_fk FOREIGN KEY (id_servicio) REFERENCES fide_servicios_tb (id_servicio),
    CONSTRAINT fide_servicio_producto_id_producto_fk FOREIGN KEY (id_producto) REFERENCES fide_productos_tb (id_producto),
    CONSTRAINT fide_servicio_producto_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fide_detalle_venta_tb (
    id_venta INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT,
    precio_unitario DECIMAL(10, 2),
    id_estado INT NOT NULL,
    CONSTRAINT fide_detalle_venta_id_pk PRIMARY KEY (id_venta, id_producto),
    CONSTRAINT fide_detalle_venta_id_venta_fk FOREIGN KEY (id_venta) REFERENCES fide_ventas_tb (id_venta),
    CONSTRAINT fide_detalle_venta_id_producto_fk FOREIGN KEY (id_producto) REFERENCES fide_productos_tb (id_producto),
    CONSTRAINT fide_detalle_venta_id_estado_fk FOREIGN KEY (id_estado) REFERENCES fide_estado_tb (id_estado)
) ENGINE = InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

/* =====================================================================
   CATÁLOGOS
   INSERT IGNORE: si el registro ya existe no se duplica ni da error,
   así el script se puede volver a correr.
   ===================================================================== */

INSERT IGNORE INTO fide_estado_tb (id_estado, nombre_estado) VALUES
    (1, 'Activo'),
    (2, 'Inactivo'),
    (3, 'Programado'),
    (4, 'Disponible'),
    (5, 'Reservada'),
    (6, 'Ocupada');

INSERT IGNORE INTO fide_roles_tb (id_rol, nombre_rol, descripcion, id_estado) VALUES
    (1, 'Administrador', 'Acceso total al sistema', 1),
    (2, 'Personal operativo', 'Gestiona fallecidos, servicios y sepelios', 1),
    (3, 'Encargado de ventas', 'Gestiona clientes y contratos', 1),
    (4, 'Contador', 'Gestiona cobros y financiamientos', 1);

INSERT IGNORE INTO fide_tipos_servicio_tb (id_tipo_servicio, nombre_tipo, id_estado) VALUES
    (1, 'Velación', 1),
    (2, 'Sepelio', 1),
    (3, 'Cremación', 1);

INSERT IGNORE INTO fide_tipos_unidad_tb (id_tipo_unidad, nombre_tipo_unidad, id_estado) VALUES
    (1, 'Nicho', 1),
    (2, 'Bóveda', 1),
    (3, 'Mausoleo', 1);

-- División territorial de Costa Rica (códigos oficiales: provincia N, cantón NCC, distrito NCCDD).
-- Revisar contra la lista oficial del INEC si se detecta algún nombre distinto.
INSERT IGNORE INTO fide_provincia_tb (id_provincia, nombre_provincia, id_estado) VALUES
    (1, 'San José', 1),
    (2, 'Alajuela', 1),
    (3, 'Cartago', 1),
    (4, 'Heredia', 1),
    (5, 'Guanacaste', 1),
    (6, 'Puntarenas', 1),
    (7, 'Limón', 1);

-- 84 cantones
INSERT IGNORE INTO fide_canton_tb (id_canton, nombre_canton, id_provincia, id_estado) VALUES
    (101, 'San José', 1, 1),
    (102, 'Escazú', 1, 1),
    (103, 'Desamparados', 1, 1),
    (104, 'Puriscal', 1, 1),
    (105, 'Tarrazú', 1, 1),
    (106, 'Aserrí', 1, 1),
    (107, 'Mora', 1, 1),
    (108, 'Goicoechea', 1, 1),
    (109, 'Santa Ana', 1, 1),
    (110, 'Alajuelita', 1, 1),
    (111, 'Vázquez de Coronado', 1, 1),
    (112, 'Acosta', 1, 1),
    (113, 'Tibás', 1, 1),
    (114, 'Moravia', 1, 1),
    (115, 'Montes de Oca', 1, 1),
    (116, 'Turrubares', 1, 1),
    (117, 'Dota', 1, 1),
    (118, 'Curridabat', 1, 1),
    (119, 'Pérez Zeledón', 1, 1),
    (120, 'León Cortés Castro', 1, 1),
    (201, 'Alajuela', 2, 1),
    (202, 'San Ramón', 2, 1),
    (203, 'Grecia', 2, 1),
    (204, 'San Mateo', 2, 1),
    (205, 'Atenas', 2, 1),
    (206, 'Naranjo', 2, 1),
    (207, 'Palmares', 2, 1),
    (208, 'Poás', 2, 1),
    (209, 'Orotina', 2, 1),
    (210, 'San Carlos', 2, 1),
    (211, 'Zarcero', 2, 1),
    (212, 'Sarchí', 2, 1),
    (213, 'Upala', 2, 1),
    (214, 'Los Chiles', 2, 1),
    (215, 'Guatuso', 2, 1),
    (216, 'Río Cuarto', 2, 1),
    (301, 'Cartago', 3, 1),
    (302, 'Paraíso', 3, 1),
    (303, 'La Unión', 3, 1),
    (304, 'Jiménez', 3, 1),
    (305, 'Turrialba', 3, 1),
    (306, 'Alvarado', 3, 1),
    (307, 'Oreamuno', 3, 1),
    (308, 'El Guarco', 3, 1),
    (401, 'Heredia', 4, 1),
    (402, 'Barva', 4, 1),
    (403, 'Santo Domingo', 4, 1),
    (404, 'Santa Bárbara', 4, 1),
    (405, 'San Rafael', 4, 1),
    (406, 'San Isidro', 4, 1),
    (407, 'Belén', 4, 1),
    (408, 'Flores', 4, 1),
    (409, 'San Pablo', 4, 1),
    (410, 'Sarapiquí', 4, 1),
    (501, 'Liberia', 5, 1),
    (502, 'Nicoya', 5, 1),
    (503, 'Santa Cruz', 5, 1),
    (504, 'Bagaces', 5, 1),
    (505, 'Carrillo', 5, 1),
    (506, 'Cañas', 5, 1),
    (507, 'Abangares', 5, 1),
    (508, 'Tilarán', 5, 1),
    (509, 'Nandayure', 5, 1),
    (510, 'La Cruz', 5, 1),
    (511, 'Hojancha', 5, 1),
    (601, 'Puntarenas', 6, 1),
    (602, 'Esparza', 6, 1),
    (603, 'Buenos Aires', 6, 1),
    (604, 'Montes de Oro', 6, 1),
    (605, 'Osa', 6, 1),
    (606, 'Quepos', 6, 1),
    (607, 'Golfito', 6, 1),
    (608, 'Coto Brus', 6, 1),
    (609, 'Parrita', 6, 1),
    (610, 'Corredores', 6, 1),
    (611, 'Garabito', 6, 1),
    (612, 'Monteverde', 6, 1),
    (613, 'Puerto Jiménez', 6, 1),
    (701, 'Limón', 7, 1),
    (702, 'Pococí', 7, 1),
    (703, 'Siquirres', 7, 1),
    (704, 'Talamanca', 7, 1),
    (705, 'Matina', 7, 1),
    (706, 'Guácimo', 7, 1);

-- 492 distritos
INSERT IGNORE INTO fide_distrito_tb (id_distrito, nombre_distrito, id_canton, id_estado) VALUES
    (10101, 'Carmen', 101, 1),
    (10102, 'Merced', 101, 1),
    (10103, 'Hospital', 101, 1),
    (10104, 'Catedral', 101, 1),
    (10105, 'Zapote', 101, 1),
    (10106, 'San Francisco de Dos Ríos', 101, 1),
    (10107, 'Uruca', 101, 1),
    (10108, 'Mata Redonda', 101, 1),
    (10109, 'Pavas', 101, 1),
    (10110, 'Hatillo', 101, 1),
    (10111, 'San Sebastián', 101, 1),
    (10201, 'Escazú', 102, 1),
    (10202, 'San Antonio', 102, 1),
    (10203, 'San Rafael', 102, 1),
    (10301, 'Desamparados', 103, 1),
    (10302, 'San Miguel', 103, 1),
    (10303, 'San Juan de Dios', 103, 1),
    (10304, 'San Rafael Arriba', 103, 1),
    (10305, 'San Antonio', 103, 1),
    (10306, 'Frailes', 103, 1),
    (10307, 'Patarrá', 103, 1),
    (10308, 'San Cristóbal', 103, 1),
    (10309, 'Rosario', 103, 1),
    (10310, 'Damas', 103, 1),
    (10311, 'San Rafael Abajo', 103, 1),
    (10312, 'Gravilias', 103, 1),
    (10313, 'Los Guido', 103, 1),
    (10401, 'Santiago', 104, 1),
    (10402, 'Mercedes Sur', 104, 1),
    (10403, 'Barbacoas', 104, 1),
    (10404, 'Grifo Alto', 104, 1),
    (10405, 'San Rafael', 104, 1),
    (10406, 'Candelarita', 104, 1),
    (10407, 'Desamparaditos', 104, 1),
    (10408, 'San Antonio', 104, 1),
    (10409, 'Chires', 104, 1),
    (10501, 'San Marcos', 105, 1),
    (10502, 'San Lorenzo', 105, 1),
    (10503, 'San Carlos', 105, 1),
    (10601, 'Aserrí', 106, 1),
    (10602, 'Tarbaca', 106, 1),
    (10603, 'Vuelta de Jorco', 106, 1),
    (10604, 'San Gabriel', 106, 1),
    (10605, 'Legua', 106, 1),
    (10606, 'Monterrey', 106, 1),
    (10607, 'Salitrillos', 106, 1),
    (10701, 'Colón', 107, 1),
    (10702, 'Guayabo', 107, 1),
    (10703, 'Tabarcia', 107, 1),
    (10704, 'Piedras Negras', 107, 1),
    (10705, 'Picagres', 107, 1),
    (10706, 'Jaris', 107, 1),
    (10707, 'Quitirrisí', 107, 1),
    (10801, 'Guadalupe', 108, 1),
    (10802, 'San Francisco', 108, 1),
    (10803, 'Calle Blancos', 108, 1),
    (10804, 'Mata de Plátano', 108, 1),
    (10805, 'Ipís', 108, 1),
    (10806, 'Rancho Redondo', 108, 1),
    (10807, 'Purral', 108, 1),
    (10901, 'Santa Ana', 109, 1),
    (10902, 'Salitral', 109, 1),
    (10903, 'Pozos', 109, 1),
    (10904, 'Uruca', 109, 1),
    (10905, 'Piedades', 109, 1),
    (10906, 'Brasil', 109, 1),
    (11001, 'Alajuelita', 110, 1),
    (11002, 'San Josecito', 110, 1),
    (11003, 'San Antonio', 110, 1),
    (11004, 'Concepción', 110, 1),
    (11005, 'San Felipe', 110, 1),
    (11101, 'San Isidro', 111, 1),
    (11102, 'San Rafael', 111, 1),
    (11103, 'Dulce Nombre de Jesús', 111, 1),
    (11104, 'Patalillo', 111, 1),
    (11105, 'Cascajal', 111, 1),
    (11201, 'San Ignacio', 112, 1),
    (11202, 'Guaitil', 112, 1),
    (11203, 'Palmichal', 112, 1),
    (11204, 'Cangrejal', 112, 1),
    (11205, 'Sabanillas', 112, 1),
    (11301, 'San Juan', 113, 1),
    (11302, 'Cinco Esquinas', 113, 1),
    (11303, 'Anselmo Llorente', 113, 1),
    (11304, 'León XIII', 113, 1),
    (11305, 'Colima', 113, 1),
    (11401, 'San Vicente', 114, 1),
    (11402, 'San Jerónimo', 114, 1),
    (11403, 'La Trinidad', 114, 1),
    (11501, 'San Pedro', 115, 1),
    (11502, 'Sabanilla', 115, 1),
    (11503, 'Mercedes', 115, 1),
    (11504, 'San Rafael', 115, 1),
    (11601, 'San Pablo', 116, 1),
    (11602, 'San Pedro', 116, 1),
    (11603, 'San Juan de Mata', 116, 1),
    (11604, 'San Luis', 116, 1),
    (11605, 'Carara', 116, 1),
    (11701, 'Santa María', 117, 1),
    (11702, 'Jardín', 117, 1),
    (11703, 'Copey', 117, 1),
    (11801, 'Curridabat', 118, 1),
    (11802, 'Granadilla', 118, 1),
    (11803, 'Sánchez', 118, 1),
    (11804, 'Tirrases', 118, 1),
    (11901, 'San Isidro de El General', 119, 1),
    (11902, 'El General', 119, 1),
    (11903, 'Daniel Flores', 119, 1),
    (11904, 'Rivas', 119, 1),
    (11905, 'San Pedro', 119, 1),
    (11906, 'Platanares', 119, 1),
    (11907, 'Pejibaye', 119, 1),
    (11908, 'Cajón', 119, 1),
    (11909, 'Barú', 119, 1),
    (11910, 'Río Nuevo', 119, 1),
    (11911, 'Páramo', 119, 1),
    (11912, 'La Amistad', 119, 1),
    (12001, 'San Pablo', 120, 1),
    (12002, 'San Andrés', 120, 1),
    (12003, 'Llano Bonito', 120, 1),
    (12004, 'San Isidro', 120, 1),
    (12005, 'Santa Cruz', 120, 1),
    (12006, 'San Antonio', 120, 1),
    (20101, 'Alajuela', 201, 1),
    (20102, 'San José', 201, 1),
    (20103, 'Carrizal', 201, 1),
    (20104, 'San Antonio', 201, 1),
    (20105, 'Guácima', 201, 1),
    (20106, 'San Isidro', 201, 1),
    (20107, 'Sabanilla', 201, 1),
    (20108, 'San Rafael', 201, 1),
    (20109, 'Río Segundo', 201, 1),
    (20110, 'Desamparados', 201, 1),
    (20111, 'Turrúcares', 201, 1),
    (20112, 'Tambor', 201, 1),
    (20113, 'Garita', 201, 1),
    (20114, 'Sarapiquí', 201, 1),
    (20201, 'San Ramón', 202, 1),
    (20202, 'Santiago', 202, 1),
    (20203, 'San Juan', 202, 1),
    (20204, 'Piedades Norte', 202, 1),
    (20205, 'Piedades Sur', 202, 1),
    (20206, 'San Rafael', 202, 1),
    (20207, 'San Isidro', 202, 1),
    (20208, 'Ángeles', 202, 1),
    (20209, 'Alfaro', 202, 1),
    (20210, 'Volio', 202, 1),
    (20211, 'Concepción', 202, 1),
    (20212, 'Zapotal', 202, 1),
    (20213, 'Peñas Blancas', 202, 1),
    (20214, 'San Lorenzo', 202, 1),
    (20301, 'Grecia', 203, 1),
    (20302, 'San Isidro', 203, 1),
    (20303, 'San José', 203, 1),
    (20304, 'San Roque', 203, 1),
    (20305, 'Tacares', 203, 1),
    (20307, 'Puente de Piedra', 203, 1),
    (20308, 'Bolívar', 203, 1),
    (20401, 'San Mateo', 204, 1),
    (20402, 'Desmonte', 204, 1),
    (20403, 'Jesús María', 204, 1),
    (20404, 'Labrador', 204, 1),
    (20501, 'Atenas', 205, 1),
    (20502, 'Jesús', 205, 1),
    (20503, 'Mercedes', 205, 1),
    (20504, 'San Isidro', 205, 1),
    (20505, 'Concepción', 205, 1),
    (20506, 'San José', 205, 1),
    (20507, 'Santa Eulalia', 205, 1),
    (20508, 'Escobal', 205, 1),
    (20601, 'Naranjo', 206, 1),
    (20602, 'San Miguel', 206, 1),
    (20603, 'San José', 206, 1),
    (20604, 'Cirrí Sur', 206, 1),
    (20605, 'San Jerónimo', 206, 1),
    (20606, 'San Juan', 206, 1),
    (20607, 'El Rosario', 206, 1),
    (20608, 'Palmitos', 206, 1),
    (20701, 'Palmares', 207, 1),
    (20702, 'Zaragoza', 207, 1),
    (20703, 'Buenos Aires', 207, 1),
    (20704, 'Santiago', 207, 1),
    (20705, 'Candelaria', 207, 1),
    (20706, 'Esquipulas', 207, 1),
    (20707, 'La Granja', 207, 1),
    (20801, 'San Pedro', 208, 1),
    (20802, 'San Juan', 208, 1),
    (20803, 'San Rafael', 208, 1),
    (20804, 'Carrillos', 208, 1),
    (20805, 'Sabana Redonda', 208, 1),
    (20901, 'Orotina', 209, 1),
    (20902, 'El Mastate', 209, 1),
    (20903, 'Hacienda Vieja', 209, 1),
    (20904, 'Coyolar', 209, 1),
    (20905, 'La Ceiba', 209, 1),
    (21001, 'Quesada', 210, 1),
    (21002, 'Florencia', 210, 1),
    (21003, 'Buenavista', 210, 1),
    (21004, 'Aguas Zarcas', 210, 1),
    (21005, 'Venecia', 210, 1),
    (21006, 'Pital', 210, 1),
    (21007, 'La Fortuna', 210, 1),
    (21008, 'La Tigra', 210, 1),
    (21009, 'La Palmera', 210, 1),
    (21010, 'Venado', 210, 1),
    (21011, 'Cutris', 210, 1),
    (21012, 'Monterrey', 210, 1),
    (21013, 'Pocosol', 210, 1),
    (21101, 'Zarcero', 211, 1),
    (21102, 'Laguna', 211, 1),
    (21103, 'Tapesco', 211, 1),
    (21104, 'Guadalupe', 211, 1),
    (21105, 'Palmira', 211, 1),
    (21106, 'Zapote', 211, 1),
    (21107, 'Brisas', 211, 1),
    (21201, 'Sarchí Norte', 212, 1),
    (21202, 'Sarchí Sur', 212, 1),
    (21203, 'Toro Amarillo', 212, 1),
    (21204, 'San Pedro', 212, 1),
    (21205, 'Rodríguez', 212, 1),
    (21301, 'Upala', 213, 1),
    (21302, 'Aguas Claras', 213, 1),
    (21303, 'San José', 213, 1),
    (21304, 'Bijagua', 213, 1),
    (21305, 'Delicias', 213, 1),
    (21306, 'Dos Ríos', 213, 1),
    (21307, 'Yolillal', 213, 1),
    (21308, 'Canalete', 213, 1),
    (21401, 'Los Chiles', 214, 1),
    (21402, 'Caño Negro', 214, 1),
    (21403, 'El Amparo', 214, 1),
    (21404, 'San Jorge', 214, 1),
    (21501, 'San Rafael', 215, 1),
    (21502, 'Buenavista', 215, 1),
    (21503, 'Cote', 215, 1),
    (21504, 'Katira', 215, 1),
    (21601, 'Río Cuarto', 216, 1),
    (21602, 'Santa Rita', 216, 1),
    (21603, 'Santa Isabel', 216, 1),
    (30101, 'Oriental', 301, 1),
    (30102, 'Occidental', 301, 1),
    (30103, 'Carmen', 301, 1),
    (30104, 'San Nicolás', 301, 1),
    (30105, 'Aguacaliente o San Francisco', 301, 1),
    (30106, 'Guadalupe o Arenilla', 301, 1),
    (30107, 'Corralillo', 301, 1),
    (30108, 'Tierra Blanca', 301, 1),
    (30109, 'Dulce Nombre', 301, 1),
    (30110, 'Llano Grande', 301, 1),
    (30111, 'Quebradilla', 301, 1),
    (30201, 'Paraíso', 302, 1),
    (30202, 'Santiago', 302, 1),
    (30203, 'Orosi', 302, 1),
    (30204, 'Cachí', 302, 1),
    (30205, 'Llanos de Santa Lucía', 302, 1),
    (30206, 'Birrisito', 302, 1),
    (30301, 'Tres Ríos', 303, 1),
    (30302, 'San Diego', 303, 1),
    (30303, 'San Juan', 303, 1),
    (30304, 'San Rafael', 303, 1),
    (30305, 'Concepción', 303, 1),
    (30306, 'Dulce Nombre', 303, 1),
    (30307, 'San Ramón', 303, 1),
    (30308, 'Río Azul', 303, 1),
    (30401, 'Juan Viñas', 304, 1),
    (30402, 'Tucurrique', 304, 1),
    (30403, 'Pejibaye', 304, 1),
    (30404, 'La Victoria', 304, 1),
    (30501, 'Turrialba', 305, 1),
    (30502, 'La Suiza', 305, 1),
    (30503, 'Peralta', 305, 1),
    (30504, 'Santa Cruz', 305, 1),
    (30505, 'Santa Teresita', 305, 1),
    (30506, 'Pavones', 305, 1),
    (30507, 'Tuis', 305, 1),
    (30508, 'Tayutic', 305, 1),
    (30509, 'Santa Rosa', 305, 1),
    (30510, 'Tres Equis', 305, 1),
    (30511, 'La Isabel', 305, 1),
    (30512, 'Chirripó', 305, 1),
    (30601, 'Pacayas', 306, 1),
    (30602, 'Cervantes', 306, 1),
    (30603, 'Capellades', 306, 1),
    (30701, 'San Rafael', 307, 1),
    (30702, 'Cot', 307, 1),
    (30703, 'Potrero Cerrado', 307, 1),
    (30704, 'Cipreses', 307, 1),
    (30705, 'Santa Rosa', 307, 1),
    (30801, 'El Tejar', 308, 1),
    (30802, 'San Isidro', 308, 1),
    (30803, 'Tobosi', 308, 1),
    (30804, 'Patio de Agua', 308, 1),
    (40101, 'Heredia', 401, 1),
    (40102, 'Mercedes', 401, 1),
    (40103, 'San Francisco', 401, 1),
    (40104, 'Ulloa', 401, 1),
    (40105, 'Varablanca', 401, 1),
    (40201, 'Barva', 402, 1),
    (40202, 'San Pedro', 402, 1),
    (40203, 'San Pablo', 402, 1),
    (40204, 'San Roque', 402, 1),
    (40205, 'Santa Lucía', 402, 1),
    (40206, 'San José de la Montaña', 402, 1),
    (40207, 'Puente Salas', 402, 1),
    (40301, 'Santo Domingo', 403, 1),
    (40302, 'San Vicente', 403, 1),
    (40303, 'San Miguel', 403, 1),
    (40304, 'Paracito', 403, 1),
    (40305, 'Santo Tomás', 403, 1),
    (40306, 'Santa Rosa', 403, 1),
    (40307, 'Tures', 403, 1),
    (40308, 'Pará', 403, 1),
    (40401, 'Santa Bárbara', 404, 1),
    (40402, 'San Pedro', 404, 1),
    (40403, 'San Juan', 404, 1),
    (40404, 'Jesús', 404, 1),
    (40405, 'Santo Domingo', 404, 1),
    (40406, 'Purabá', 404, 1),
    (40501, 'San Rafael', 405, 1),
    (40502, 'San Josecito', 405, 1),
    (40503, 'Santiago', 405, 1),
    (40504, 'Ángeles', 405, 1),
    (40505, 'Concepción', 405, 1),
    (40601, 'San Isidro', 406, 1),
    (40602, 'San José', 406, 1),
    (40603, 'Concepción', 406, 1),
    (40604, 'San Francisco', 406, 1),
    (40701, 'San Antonio', 407, 1),
    (40702, 'La Ribera', 407, 1),
    (40703, 'La Asunción', 407, 1),
    (40801, 'San Joaquín', 408, 1),
    (40802, 'Barrantes', 408, 1),
    (40803, 'Llorente', 408, 1),
    (40901, 'San Pablo', 409, 1),
    (40902, 'Rincón de Sabanilla', 409, 1),
    (41001, 'Puerto Viejo', 410, 1),
    (41002, 'La Virgen', 410, 1),
    (41003, 'Las Horquetas', 410, 1),
    (41004, 'Llanuras del Gaspar', 410, 1),
    (41005, 'Cureña', 410, 1),
    (50101, 'Liberia', 501, 1),
    (50102, 'Cañas Dulces', 501, 1),
    (50103, 'Mayorga', 501, 1),
    (50104, 'Nacascolo', 501, 1),
    (50105, 'Curubandé', 501, 1),
    (50201, 'Nicoya', 502, 1),
    (50202, 'Mansión', 502, 1),
    (50203, 'San Antonio', 502, 1),
    (50204, 'Quebrada Honda', 502, 1),
    (50205, 'Sámara', 502, 1),
    (50206, 'Nosara', 502, 1),
    (50207, 'Belén de Nosarita', 502, 1),
    (50301, 'Santa Cruz', 503, 1),
    (50302, 'Bolsón', 503, 1),
    (50303, 'Veintisiete de Abril', 503, 1),
    (50304, 'Tempate', 503, 1),
    (50305, 'Cartagena', 503, 1),
    (50306, 'Cuajiniquil', 503, 1),
    (50307, 'Diriá', 503, 1),
    (50308, 'Cabo Velas', 503, 1),
    (50309, 'Tamarindo', 503, 1),
    (50401, 'Bagaces', 504, 1),
    (50402, 'La Fortuna', 504, 1),
    (50403, 'Mogote', 504, 1),
    (50404, 'Río Naranjo', 504, 1),
    (50501, 'Filadelfia', 505, 1),
    (50502, 'Palmira', 505, 1),
    (50503, 'Sardinal', 505, 1),
    (50504, 'Belén', 505, 1),
    (50601, 'Cañas', 506, 1),
    (50602, 'Palmira', 506, 1),
    (50603, 'San Miguel', 506, 1),
    (50604, 'Bebedero', 506, 1),
    (50605, 'Porozal', 506, 1),
    (50701, 'Las Juntas', 507, 1),
    (50702, 'Sierra', 507, 1),
    (50703, 'San Juan', 507, 1),
    (50704, 'Colorado', 507, 1),
    (50801, 'Tilarán', 508, 1),
    (50802, 'Quebrada Grande', 508, 1),
    (50803, 'Tronadora', 508, 1),
    (50804, 'Santa Rosa', 508, 1),
    (50805, 'Líbano', 508, 1),
    (50806, 'Tierras Morenas', 508, 1),
    (50807, 'Arenal', 508, 1),
    (50808, 'Cabeceras', 508, 1),
    (50901, 'Carmona', 509, 1),
    (50902, 'Santa Rita', 509, 1),
    (50903, 'Zapotal', 509, 1),
    (50904, 'San Pablo', 509, 1),
    (50905, 'Porvenir', 509, 1),
    (50906, 'Bejuco', 509, 1),
    (51001, 'La Cruz', 510, 1),
    (51002, 'Santa Cecilia', 510, 1),
    (51003, 'La Garita', 510, 1),
    (51004, 'Santa Elena', 510, 1),
    (51101, 'Hojancha', 511, 1),
    (51102, 'Monte Romo', 511, 1),
    (51103, 'Puerto Carrillo', 511, 1),
    (51104, 'Huacas', 511, 1),
    (51105, 'Matambú', 511, 1),
    (60101, 'Puntarenas', 601, 1),
    (60102, 'Pitahaya', 601, 1),
    (60103, 'Chomes', 601, 1),
    (60104, 'Lepanto', 601, 1),
    (60105, 'Paquera', 601, 1),
    (60106, 'Manzanillo', 601, 1),
    (60107, 'Guacimal', 601, 1),
    (60108, 'Barranca', 601, 1),
    (60110, 'Isla del Coco', 601, 1),
    (60111, 'Cóbano', 601, 1),
    (60112, 'Chacarita', 601, 1),
    (60113, 'Chira', 601, 1),
    (60114, 'Acapulco', 601, 1),
    (60115, 'El Roble', 601, 1),
    (60116, 'Arancibia', 601, 1),
    (60201, 'Espíritu Santo', 602, 1),
    (60202, 'San Juan Grande', 602, 1),
    (60203, 'Macacona', 602, 1),
    (60204, 'San Rafael', 602, 1),
    (60205, 'San Jerónimo', 602, 1),
    (60206, 'Caldera', 602, 1),
    (60301, 'Buenos Aires', 603, 1),
    (60302, 'Volcán', 603, 1),
    (60303, 'Potrero Grande', 603, 1),
    (60304, 'Boruca', 603, 1),
    (60305, 'Pilas', 603, 1),
    (60306, 'Colinas', 603, 1),
    (60307, 'Chánguena', 603, 1),
    (60308, 'Biolley', 603, 1),
    (60309, 'Brunka', 603, 1),
    (60401, 'Miramar', 604, 1),
    (60402, 'La Unión', 604, 1),
    (60403, 'San Isidro', 604, 1),
    (60501, 'Puerto Cortés', 605, 1),
    (60502, 'Palmar', 605, 1),
    (60503, 'Sierpe', 605, 1),
    (60504, 'Bahía Ballena', 605, 1),
    (60505, 'Piedras Blancas', 605, 1),
    (60506, 'Bahía Drake', 605, 1),
    (60601, 'Quepos', 606, 1),
    (60602, 'Savegre', 606, 1),
    (60603, 'Naranjito', 606, 1),
    (60701, 'Golfito', 607, 1),
    (60703, 'Guaycará', 607, 1),
    (60704, 'Pavón', 607, 1),
    (60801, 'San Vito', 608, 1),
    (60802, 'Sabalito', 608, 1),
    (60803, 'Aguabuena', 608, 1),
    (60804, 'Limoncito', 608, 1),
    (60805, 'Pittier', 608, 1),
    (60806, 'Gutiérrez Braun', 608, 1),
    (60901, 'Parrita', 609, 1),
    (61001, 'Corredor', 610, 1),
    (61002, 'La Cuesta', 610, 1),
    (61003, 'Canoas', 610, 1),
    (61004, 'Laurel', 610, 1),
    (61101, 'Jacó', 611, 1),
    (61102, 'Tárcoles', 611, 1),
    (61103, 'Lagunillas', 611, 1),
    (61201, 'Monteverde', 612, 1),
    (61301, 'Puerto Jiménez', 613, 1),
    (70101, 'Limón', 701, 1),
    (70102, 'Valle La Estrella', 701, 1),
    (70103, 'Río Blanco', 701, 1),
    (70104, 'Matama', 701, 1),
    (70201, 'Guápiles', 702, 1),
    (70202, 'Jiménez', 702, 1),
    (70203, 'Rita', 702, 1),
    (70204, 'Roxana', 702, 1),
    (70205, 'Cariari', 702, 1),
    (70206, 'Colorado', 702, 1),
    (70207, 'La Colonia', 702, 1),
    (70301, 'Siquirres', 703, 1),
    (70302, 'Pacuarito', 703, 1),
    (70303, 'Florida', 703, 1),
    (70304, 'Germania', 703, 1),
    (70305, 'El Cairo', 703, 1),
    (70306, 'Alegría', 703, 1),
    (70307, 'Reventazón', 703, 1),
    (70401, 'Bratsi', 704, 1),
    (70402, 'Sixaola', 704, 1),
    (70403, 'Cahuita', 704, 1),
    (70404, 'Telire', 704, 1),
    (70501, 'Matina', 705, 1),
    (70502, 'Batán', 705, 1),
    (70503, 'Carrandi', 705, 1),
    (70601, 'Guácimo', 706, 1),
    (70602, 'Mercedes', 706, 1),
    (70603, 'Pocora', 706, 1),
    (70604, 'Río Jiménez', 706, 1),
    (70605, 'Duacarí', 706, 1);

/* =====================================================================
   DATOS DE PRUEBA
   ===================================================================== */

INSERT IGNORE INTO fide_personas_tb (cedula, nombre, apellido_1, apellido_2, id_estado) VALUES
    ('101110111', 'Lucía', 'Durán', 'Mora', 1),         -- colaboradora
    ('202220222', 'Carlos', 'Rojas', 'Solís', 1),       -- colaborador
    ('303330333', 'Jorge', 'Araya', 'Vargas', 1),       -- cliente
    ('404440444', 'Rafael', 'Solís', 'Araya', 1),       -- fallecido
    ('505550555', 'Rosa', 'Montero', 'Araya', 1),       -- fallecida
    ('606660666', 'Ana', 'Rojas', 'Mora', 1);           -- cliente (HU-04)

-- Colaboradores operativos (responsables de sepelios, HU-06 y HU-07)
INSERT IGNORE INTO fide_usuarios_tb (cedula_usuario, usuario, contrasena, id_rol, id_estado) VALUES
    ('101110111', 'lduran', '{noop}cambiar', 2, 1),
    ('202220222', 'crojas', '{noop}cambiar', 2, 1);

INSERT IGNORE INTO fide_clientes_tb (cedula_cliente, referencia_familiar, id_estado) VALUES
    ('303330333', 'Familia Araya', 1),
    ('606660666', 'Familia Rojas', 1);

INSERT IGNORE INTO fide_fallecidos_tb (cedula_fallecido, cedula_cliente, fecha_nacimiento, fecha_fallecimiento, lugar_fallecimiento, causa_fallecimiento, id_estado) VALUES
    ('404440444', '303330333', '1950-04-12', '2026-10-20', 'Hospital México', 'Causas naturales', 1),
    ('505550555', '303330333', '1948-09-03', '2026-10-21', 'Domicilio', 'Causas naturales', 1);

-- Servicios activos con su fallecido asociado (precondiciones de la HU-06)
INSERT IGNORE INTO fide_servicios_tb (id_servicio, cedula_fallecido, id_tipo_servicio, descripcion, fecha_solicitud, id_estado) VALUES
    (1, '404440444', 2, 'Sepelio Rafael Solís', '2026-10-21', 1),
    (2, '505550555', 2, 'Sepelio Rosa Montero', '2026-10-22', 1);

-- HU-08: cementerios y unidades por sector.
-- Estados: 4 Disponible, 5 Reservada, 6 Ocupada.
-- El "Sector B" de Jardín del Recuerdo no tiene unidades disponibles (criterio 2).
INSERT IGNORE INTO fide_cementerios_tb (id_cementerio, nombre_cementerio, id_direccion_cementerio, capacidad_total, id_estado) VALUES
    (1, 'Jardín del Recuerdo', NULL, 300, 1),
    (2, 'Campo Santo La Piedad', NULL, 200, 1);

INSERT IGNORE INTO fide_unidades_sepultura_tb (id_unidad_sepultura, id_cementerio, id_tipo_unidad, sector, bloque, numero, capacidad, id_estado) VALUES
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
