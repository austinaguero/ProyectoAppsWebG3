package com.Funeraria.Funeraria_G3.service;

import com.Funeraria.Funeraria_G3.domain.MetodoPago;
import com.Funeraria.Funeraria_G3.repository.MetodoPagoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MetodoPagoService {

    // El repositorio es final para asegurar la inmutabilidad
    private final MetodoPagoRepository metodoPagoRepository;

    @Transactional(readOnly = true)
    public List<MetodoPago> getMetodosPago(boolean activo) {
        if (activo) { //Sólo activos...
            return metodoPagoRepository.findByEstadoNombreEstado("Activo");
        }
        return metodoPagoRepository.findAll();
    }
}
