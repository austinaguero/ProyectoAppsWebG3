package com.Funeraria.Funeraria_G3.repository;

import com.Funeraria.Funeraria_G3.domain.TipoUnidad;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoUnidadRepository extends JpaRepository<TipoUnidad, Integer> {

    public List<TipoUnidad> findByEstadoNombreEstado(String nombreEstado);

}
