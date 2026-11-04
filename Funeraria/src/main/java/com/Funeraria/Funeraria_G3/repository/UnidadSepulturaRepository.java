package com.Funeraria.Funeraria_G3.repository;

import com.Funeraria.Funeraria_G3.domain.UnidadSepultura;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UnidadSepulturaRepository extends JpaRepository<UnidadSepultura, Integer> {

    // Listado del mantenimiento, ordenado por ubicación
    public List<UnidadSepultura> findAllByOrderByCementerioNombreCementerioAscSectorAscBloqueAscNumeroAsc();

    // Unidades en un estado, ordenadas por ubicación (select del sepelio)
    public List<UnidadSepultura> findByEstadoNombreEstadoOrderByCementerioNombreCementerioAscSectorAscBloqueAscNumeroAsc(
            String nombreEstado);

    //Consulta SQL (HU-08 criterio 1) que recupera las unidades de un cementerio y sector en un estado dado
    //y las ordena por bloque y número
    @Query(nativeQuery = true,
            value = "SELECT u.* FROM fide_unidades_sepultura_tb u JOIN fide_estado_tb e ON e.id_estado = u.id_estado "
            + "WHERE u.id_cementerio = :idCementerio AND u.sector = :sector AND e.nombre_estado = :nombreEstado "
            + "ORDER BY u.bloque ASC, u.numero ASC")
    public List<UnidadSepultura> consultaSQL(Integer idCementerio, String sector, String nombreEstado);

    //Consulta SQL que recupera los sectores registrados en un cementerio (pestañas del filtro de la HU-08)
    @Query(nativeQuery = true,
            value = "SELECT DISTINCT u.sector FROM fide_unidades_sepultura_tb u WHERE u.id_cementerio = :idCementerio "
            + "ORDER BY u.sector ASC")
    public List<String> consultaSQLSectores(Integer idCementerio);

    //Consulta SQL que recupera los sectores de un cementerio que tienen unidades en el estado indicado
    @Query(nativeQuery = true,
            value = "SELECT DISTINCT u.sector FROM fide_unidades_sepultura_tb u JOIN fide_estado_tb e ON e.id_estado = u.id_estado "
            + "WHERE u.id_cementerio = :idCementerio AND e.nombre_estado = :nombreEstado "
            + "ORDER BY u.sector ASC")
    public List<String> consultaSQLSectoresDisponibles(Integer idCementerio, String nombreEstado);

    //Consulta SQL (HU-08 criterio 2) que recupera una unidad por cada cementerio y sector que tiene
    //unidades en el estado indicado, para sugerir otros sectores o cementerios con espacio
    @Query(nativeQuery = true,
            value = "SELECT u.* FROM fide_unidades_sepultura_tb u "
            + "JOIN fide_cementerios_tb c ON c.id_cementerio = u.id_cementerio "
            + "WHERE u.id_unidad_sepultura IN ("
            + "SELECT MIN(u2.id_unidad_sepultura) FROM fide_unidades_sepultura_tb u2 "
            + "JOIN fide_estado_tb e ON e.id_estado = u2.id_estado "
            + "WHERE e.nombre_estado = :nombreEstado GROUP BY u2.id_cementerio, u2.sector) "
            + "ORDER BY c.nombre_cementerio ASC, u.sector ASC")
    public List<UnidadSepultura> consultaSQLOtrasOpciones(String nombreEstado);

}
