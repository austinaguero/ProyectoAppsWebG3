package com.Funeraria.Funeraria_G3.repository;

import com.Funeraria.Funeraria_G3.domain.Estado;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Integer> {

    public Optional<Estado> findByNombreEstado(String nombreEstado);

    public List<Estado> findByNombreEstadoInOrderByIdEstado(List<String> nombres);

}
