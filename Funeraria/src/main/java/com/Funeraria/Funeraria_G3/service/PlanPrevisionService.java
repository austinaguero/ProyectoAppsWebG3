package com.Funeraria.Funeraria_G3.service;

import com.Funeraria.Funeraria_G3.domain.PlanPrevision;
import com.Funeraria.Funeraria_G3.repository.PlanPrevisionRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlanPrevisionService {

    // El repositorio es final para asegurar la inmutabilidad
    private final PlanPrevisionRepository planPrevisionRepository;

    @Transactional(readOnly = true)
    public List<PlanPrevision> getPlanes(boolean activo) {
        if (activo) { //Sólo activos y vigentes hoy...
            var hoy = LocalDate.now();
            return planPrevisionRepository
                    .findByEstadoNombreEstadoAndVigenciaInicioLessThanEqualAndVigenciaFinGreaterThanEqualOrderByIdPlanAsc(
                            "Activo", hoy, hoy);
        }
        return planPrevisionRepository.findAll();
    }
}
