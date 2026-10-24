package com.Funeraria.Funeraria_G3.repository;

import com.Funeraria.Funeraria_G3.domain.Sepelio;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SepelioRepository extends JpaRepository<Sepelio, Integer> {

    public List<Sepelio> findAllByOrderByHoraSepelioAsc();

    public boolean existsByServicioIdServicio(Integer idServicio);

    public boolean existsByUnidadSepulturaIdUnidadSepultura(Integer idUnidadSepultura);

    // HU-07: primer sepelio del responsable dentro del rango (inicio, fin), sin contar el que se edita
    public Optional<Sepelio> findFirstByResponsableCedulaUsuarioAndHoraSepelioGreaterThanAndHoraSepelioLessThanAndIdSepelioNot(
            String cedulaUsuario, LocalDateTime inicio, LocalDateTime fin, Integer idSepelio);

}
