package com.Funeraria.Funeraria_G3.service;

import com.Funeraria.Funeraria_G3.domain.Fallecido;
import com.Funeraria.Funeraria_G3.repository.FallecidoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FallecidoService {

    // El repositorio es final para asegurar la inmutabilidad
    private final FallecidoRepository fallecidoRepository;

    @Transactional(readOnly = true)
    public List<Fallecido> getFallecidos(boolean activo) {
        if (activo) { //Sólo activos...
            return fallecidoRepository.findByEstadoNombreEstado("Activo");
        }
        return fallecidoRepository.findAll();
    }
}
