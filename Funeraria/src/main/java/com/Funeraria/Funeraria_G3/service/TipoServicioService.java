package com.Funeraria.Funeraria_G3.service;

import com.Funeraria.Funeraria_G3.domain.TipoServicio;
import com.Funeraria.Funeraria_G3.repository.TipoServicioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TipoServicioService {

    // El repositorio es final para asegurar la inmutabilidad
    private final TipoServicioRepository tipoServicioRepository;

    @Transactional(readOnly = true)
    public List<TipoServicio> getTiposServicio(boolean activo) {
        if (activo) { //Sólo activos...
            return tipoServicioRepository.findByEstadoNombreEstado("Activo");
        }
        return tipoServicioRepository.findAll();
    }
}
