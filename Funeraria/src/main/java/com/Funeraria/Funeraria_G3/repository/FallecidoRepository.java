package com.Funeraria.Funeraria_G3.repository;

import com.Funeraria.Funeraria_G3.domain.Fallecido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FallecidoRepository extends JpaRepository<Fallecido, String> {

    public List<Fallecido> findByEstadoNombreEstado(String nombreEstado);

}
