/* Servicios funerarios
   Llave autoincremental para usar @GeneratedValue(IDENTITY) igual que en Tienda.
   fide_servicios_tb es referenciada por fide_sepelios_tb y fide_servicio_producto_tb,
   por eso se desactivan las llaves foráneas solo mientras se modifica la columna
   (el tipo de dato no cambia, solo se agrega AUTO_INCREMENT). */

USE Proyecto;

SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE fide_servicios_tb
    MODIFY id_servicio INT NOT NULL AUTO_INCREMENT;

SET FOREIGN_KEY_CHECKS = 1;
