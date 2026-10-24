package com.Funeraria.Funeraria_G3.service;

import com.Funeraria.Funeraria_G3.domain.Cementerio;
import com.Funeraria.Funeraria_G3.domain.Estado;
import com.Funeraria.Funeraria_G3.domain.SectorDisponible;
import com.Funeraria.Funeraria_G3.domain.TipoUnidad;
import com.Funeraria.Funeraria_G3.domain.UnidadSepultura;
import com.Funeraria.Funeraria_G3.repository.CementerioRepository;
import com.Funeraria.Funeraria_G3.repository.EstadoRepository;
import com.Funeraria.Funeraria_G3.repository.SepelioRepository;
import com.Funeraria.Funeraria_G3.repository.TipoUnidadRepository;
import com.Funeraria.Funeraria_G3.repository.UnidadSepulturaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnidadSepulturaService {

    private static final String ACTIVO = "Activo";
    private static final String DISPONIBLE = "Disponible";
    // Estados que puede tener una unidad de sepultura
    private static final List<String> ESTADOS_UNIDAD = List.of(DISPONIBLE, "Reservada", "Ocupada");

    // Los repositorios son final para asegurar la inmutabilidad
    private final UnidadSepulturaRepository unidadSepulturaRepository;
    private final CementerioRepository cementerioRepository;
    private final TipoUnidadRepository tipoUnidadRepository;
    private final EstadoRepository estadoRepository;
    private final SepelioRepository sepelioRepository;

    @Transactional(readOnly = true)
    public List<Cementerio> getCementerios() {
        return cementerioRepository.findByEstadoNombreEstadoOrderByNombreCementerioAsc(ACTIVO);
    }

    @Transactional(readOnly = true)
    public List<TipoUnidad> getTiposUnidad() {
        return tipoUnidadRepository.findByEstadoNombreEstado(ACTIVO);
    }

    @Transactional(readOnly = true)
    public List<Estado> getEstadosUnidad() {
        return estadoRepository.findByNombreEstadoInOrderByIdEstado(ESTADOS_UNIDAD);
    }

    @Transactional(readOnly = true)
    public List<String> getSectores(Integer idCementerio) {
        return unidadSepulturaRepository.findSectores(idCementerio);
    }

    // Todas las unidades, para el mantenimiento
    @Transactional(readOnly = true)
    public List<UnidadSepultura> getUnidades() {
        return unidadSepulturaRepository.findAllByOrderByCementerioNombreCementerioAscSectorAscBloqueAscNumeroAsc();
    }

    // Unidades libres de todos los cementerios, para escoger al programar un sepelio
    @Transactional(readOnly = true)
    public List<UnidadSepultura> getDisponibles() {
        return unidadSepulturaRepository
                .findByEstadoNombreEstadoOrderByCementerioNombreCementerioAscSectorAscBloqueAscNumeroAsc(DISPONIBLE);
    }

    // HU-08 criterio 1: solo las unidades en estado «Disponible»
    @Transactional(readOnly = true)
    public List<UnidadSepultura> getDisponibles(Integer idCementerio, String sector) {
        return unidadSepulturaRepository
                .findByCementerioIdCementerioAndSectorAndEstadoNombreEstadoOrderByBloqueAscNumeroAsc(
                        idCementerio, sector, DISPONIBLE);
    }

    // Cantidad de unidades disponibles en cada cementerio y sector
    @Transactional(readOnly = true)
    public List<SectorDisponible> getSectoresConDisponibles() {
        return unidadSepulturaRepository.contarPorSector(DISPONIBLE);
    }

    @Transactional
    public void save(UnidadSepultura unidad) {
        var estado = estadoRepository.findById(unidad.getEstado().getIdEstado())
                .filter(e -> ESTADOS_UNIDAD.contains(e.getNombreEstado()))
                .orElseThrow(() -> new ReglaNegocioException("unidad.error01"));
        unidad.setSector(unidad.getSector().trim());
        unidad.setBloque(unidad.getBloque().trim());

        // No se puede registrar dos veces la misma ubicación
        var idActual = unidad.getIdUnidadSepultura() != null ? unidad.getIdUnidadSepultura() : 0;
        if (unidadSepulturaRepository.existsByCementerioIdCementerioAndSectorAndBloqueAndNumeroAndIdUnidadSepulturaNot(
                unidad.getCementerio().getIdCementerio(), unidad.getSector(), unidad.getBloque(),
                unidad.getNumero(), idActual)) {
            throw new ReglaNegocioException("unidad.error02");
        }

        if (unidad.getIdUnidadSepultura() != null) {
            if (!unidadSepulturaRepository.existsById(unidad.getIdUnidadSepultura())) {
                throw new ReglaNegocioException("unidad.error03");
            }
            // Si tiene un sepelio asignado no puede quedar libre para otro (HU-09)
            if (DISPONIBLE.equals(estado.getNombreEstado())
                    && sepelioRepository.existsByUnidadSepulturaIdUnidadSepultura(unidad.getIdUnidadSepultura())) {
                throw new ReglaNegocioException("unidad.error04");
            }
        }
        unidad.setEstado(estado);
        unidadSepulturaRepository.save(unidad);
    }

    @Transactional
    public void delete(Integer idUnidadSepultura) {
        if (!unidadSepulturaRepository.existsById(idUnidadSepultura)) {
            throw new ReglaNegocioException("unidad.error03");
        }
        if (sepelioRepository.existsByUnidadSepulturaIdUnidadSepultura(idUnidadSepultura)) {
            throw new ReglaNegocioException("unidad.error05");
        }
        try {
            unidadSepulturaRepository.deleteById(idUnidadSepultura);
            unidadSepulturaRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ReglaNegocioException("unidad.error05");
        }
    }
}
