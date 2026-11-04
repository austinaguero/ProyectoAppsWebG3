package com.Funeraria.Funeraria_G3.repository;

import com.Funeraria.Funeraria_G3.domain.Sepelio;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SepelioRepository extends JpaRepository<Sepelio, Integer> {

    public List<Sepelio> findAllByOrderByHoraSepelioAsc();

    public boolean existsByServicioIdServicio(Integer idServicio);

    public boolean existsByUnidadSepulturaIdUnidadSepultura(Integer idUnidadSepultura);

    //Consulta SQL (HU-07) que recupera los sepelios del responsable que empiezan dentro del rango
    //(inicio, fin), sin contar el sepelio que se está editando, ordenados por hora
    @Query(nativeQuery = true,
            value = "SELECT s.* FROM fide_sepelios_tb s "
            + "WHERE s.cedula_responsable = :cedulaResponsable "
            + "AND s.hora_sepelio > :inicio AND s.hora_sepelio < :fin "
            + "AND s.id_sepelio <> :idSepelio "
            + "ORDER BY s.hora_sepelio ASC")
    public List<Sepelio> consultaSQL(String cedulaResponsable, LocalDateTime inicio, LocalDateTime fin, Integer idSepelio);

}
