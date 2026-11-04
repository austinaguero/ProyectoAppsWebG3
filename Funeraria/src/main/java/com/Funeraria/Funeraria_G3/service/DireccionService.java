package com.Funeraria.Funeraria_G3.service;

import com.Funeraria.Funeraria_G3.domain.Canton;
import com.Funeraria.Funeraria_G3.domain.Distrito;
import com.Funeraria.Funeraria_G3.domain.Provincia;
import com.Funeraria.Funeraria_G3.repository.CantonRepository;
import com.Funeraria.Funeraria_G3.repository.DistritoRepository;
import com.Funeraria.Funeraria_G3.repository.ProvinciaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DireccionService {

    // Los repositorios son final para asegurar la inmutabilidad
    private final ProvinciaRepository provinciaRepository;
    private final CantonRepository cantonRepository;
    private final DistritoRepository distritoRepository;

    @Transactional(readOnly = true)
    public List<Provincia> getProvincias() {
        return provinciaRepository.findAllByOrderByIdProvinciaAsc();
    }

    @Transactional(readOnly = true)
    public List<Canton> getCantones() {
        return cantonRepository.findAllByOrderByIdCantonAsc();
    }

    @Transactional(readOnly = true)
    public List<Distrito> getDistritos() {
        return distritoRepository.findAllByOrderByIdDistritoAsc();
    }

    // Los tres vienen y el distrito pertenece al cantón y el cantón a la provincia
    @Transactional(readOnly = true)
    public boolean isDireccionValida(Integer idProvincia, Integer idCanton, Integer idDistrito) {
        if (idProvincia == null || idCanton == null || idDistrito == null) {
            return false;
        }
        var distrito = distritoRepository.findById(idDistrito).orElse(null);
        return distrito != null
                && distrito.getCanton().getIdCanton().equals(idCanton)
                && distrito.getCanton().getProvincia().getIdProvincia().equals(idProvincia);
    }
}
