package com.Funeraria.Funeraria_G3.repository;

import com.Funeraria.Funeraria_G3.domain.Cementerio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CementerioRepository extends JpaRepository<Cementerio, Integer> {

    public List<Cementerio> findByEstadoNombreEstadoOrderByNombreCementerioAsc(String nombreEstado);

}
