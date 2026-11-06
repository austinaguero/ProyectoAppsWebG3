package com.Funeraria.Funeraria_G3.repository;

import com.Funeraria.Funeraria_G3.domain.MetodoPago;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Integer> {

    // Métodos de pago según su estado (select del pago de la HU-12)
    public List<MetodoPago> findByEstadoNombreEstado(String nombreEstado);
}
