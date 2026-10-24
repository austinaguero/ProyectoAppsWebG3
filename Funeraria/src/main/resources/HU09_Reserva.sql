/* Mantenimiento de unidades de sepultura y HU-09 Reserva de Unidad de Sepultura

   1. id_unidad_sepultura autoincremental para crear unidades desde el sistema
      (@GeneratedValue IDENTITY, igual que en Tienda). fide_sepelios_tb la referencia,
      por eso se desactivan las llaves foráneas solo mientras se modifica la columna;
      el tipo de dato no cambia.

   2. HU-09 criterio 2: una unidad no puede quedar asignada a dos sepelios.
      La restricción UNIQUE hace que la base de datos rechace la asignación doble
      aunque dos usuarios guarden al mismo tiempo (los NULL no cuentan). */

USE Proyecto;

SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE fide_unidades_sepultura_tb
    MODIFY id_unidad_sepultura INT NOT NULL AUTO_INCREMENT;

SET FOREIGN_KEY_CHECKS = 1;

ALTER TABLE fide_sepelios_tb
    ADD CONSTRAINT fide_sepelios_id_unidad_sepultura_uq UNIQUE (id_unidad_sepultura);
