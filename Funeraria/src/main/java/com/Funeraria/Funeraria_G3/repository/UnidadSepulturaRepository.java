package com.Funeraria.Funeraria_G3.repository;

import com.Funeraria.Funeraria_G3.domain.SectorDisponible;
import com.Funeraria.Funeraria_G3.domain.UnidadSepultura;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UnidadSepulturaRepository extends JpaRepository<UnidadSepultura, Integer> {

    // Listado del mantenimiento, ordenado por ubicación
    public List<UnidadSepultura> findAllByOrderByCementerioNombreCementerioAscSectorAscBloqueAscNumeroAsc();

    // Unidades en un estado, ordenadas por ubicación (select del sepelio)
    public List<UnidadSepultura> findByEstadoNombreEstadoOrderByCementerioNombreCementerioAscSectorAscBloqueAscNumeroAsc(
            String nombreEstado);

    // HU-08 criterio 1: unidades de un cementerio y sector en un estado dado
    public List<UnidadSepultura> findByCementerioIdCementerioAndSectorAndEstadoNombreEstadoOrderByBloqueAscNumeroAsc(
            Integer idCementerio, String sector, String nombreEstado);

    // Evita registrar dos veces la misma ubicación (sin contar la unidad que se edita)
    public boolean existsByCementerioIdCementerioAndSectorAndBloqueAndNumeroAndIdUnidadSepulturaNot(
            Integer idCementerio, String sector, String bloque, Integer numero, Integer idUnidadSepultura);

    // Sectores registrados en un cementerio, para los filtros
    @Query("select distinct u.sector from UnidadSepultura u where u.cementerio.idCementerio = :idCementerio order by u.sector")
    public List<String> findSectores(@Param("idCementerio") Integer idCementerio);

    // HU-08 criterio 2: sectores que sí tienen unidades en el estado indicado, para sugerir otras opciones
    @Query("""
           select new com.Funeraria.Funeraria_G3.domain.SectorDisponible(
                  u.cementerio.idCementerio, u.cementerio.nombreCementerio, u.sector, count(u))
           from UnidadSepultura u
           where u.estado.nombreEstado = :nombreEstado
           group by u.cementerio.idCementerio, u.cementerio.nombreCementerio, u.sector
           order by u.cementerio.nombreCementerio, u.sector
           """)
    public List<SectorDisponible> contarPorSector(@Param("nombreEstado") String nombreEstado);

}
