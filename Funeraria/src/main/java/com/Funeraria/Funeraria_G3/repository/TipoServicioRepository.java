package com.Funeraria.Funeraria_G3.repository;

import com.Funeraria.Funeraria_G3.domain.TipoServicio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoServicioRepository extends JpaRepository<TipoServicio, Integer> {

    public List<TipoServicio> findByEstadoNombreEstado(String nombreEstado);

}
