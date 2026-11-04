package com.Funeraria.Funeraria_G3.service;

import com.Funeraria.Funeraria_G3.domain.Cementerio;
import com.Funeraria.Funeraria_G3.domain.Estado;
import com.Funeraria.Funeraria_G3.domain.TipoUnidad;
import com.Funeraria.Funeraria_G3.domain.UnidadSepultura;
import com.Funeraria.Funeraria_G3.repository.CementerioRepository;
import com.Funeraria.Funeraria_G3.repository.EstadoRepository;
import com.Funeraria.Funeraria_G3.repository.SepelioRepository;
import com.Funeraria.Funeraria_G3.repository.TipoUnidadRepository;
import com.Funeraria.Funeraria_G3.repository.UnidadSepulturaRepository;
import java.util.List;
import java.util.Optional;
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

    // Consulta SQL de los sectores de un cementerio (pestañas de la HU-08)
    @Transactional(readOnly = true)
    public List<String> consultaSQLSectores(Integer idCementerio) {
        return unidadSepulturaRepository.consultaSQLSectores(idCementerio);
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

    // HU-08 criterio 1: consulta SQL de las unidades «Disponible» de un cementerio y sector
    @Transactional(readOnly = true)
    public List<UnidadSepultura> consultaSQL(Integer idCementerio, String sector) {
        return unidadSepulturaRepository.consultaSQL(idCementerio, sector, DISPONIBLE);
    }

    // Consulta SQL de los sectores de un cementerio que tienen unidades «Disponible» (marca de las pestañas)
    @Transactional(readOnly = true)
    public List<String> consultaSQLSectoresDisponibles(Integer idCementerio) {
        return unidadSepulturaRepository.consultaSQLSectoresDisponibles(idCementerio, DISPONIBLE);
    }

    // HU-08 criterio 2: consulta SQL de una unidad «Disponible» por cada cementerio y sector con espacio
    @Transactional(readOnly = true)
    public List<UnidadSepultura> consultaSQLOtrasOpciones() {
        return unidadSepulturaRepository.consultaSQLOtrasOpciones(DISPONIBLE);
    }

    @Transactional(readOnly = true)
    public Optional<Cementerio> getCementerio(Integer idCementerio) {
        return cementerioRepository.findById(idCementerio);
    }

    // Una ubicación repetida la rechaza la base (UNIQUE) y llega como DataIntegrityViolationException
    @Transactional
    public void save(UnidadSepultura unidad) {
        var estado = estadoRepository.findById(unidad.getEstado().getIdEstado()).orElse(null);
        if (estado == null || !ESTADOS_UNIDAD.contains(estado.getNombreEstado())) {
            throw new IllegalArgumentException("El estado de la unidad no es válido.");
        }
        unidad.setSector(unidad.getSector().trim());
        unidad.setBloque(unidad.getBloque().trim());

        if (unidad.getIdUnidadSepultura() != null) {
            if (!unidadSepulturaRepository.existsById(unidad.getIdUnidadSepultura())) {
                throw new IllegalArgumentException("La unidad con ID " + unidad.getIdUnidadSepultura() + " no existe.");
            }
            // Si tiene un sepelio asignado no puede quedar libre para otro (HU-09)
            if (DISPONIBLE.equals(estado.getNombreEstado())
                    && sepelioRepository.existsByUnidadSepulturaIdUnidadSepultura(unidad.getIdUnidadSepultura())) {
                throw new IllegalStateException("La unidad tiene un sepelio asignado, no puede quedar disponible.");
            }
        }
        unidad.setEstado(estado);
        unidadSepulturaRepository.saveAndFlush(unidad);
    }

    @Transactional
    public void delete(Integer idUnidadSepultura) {
        // Verifica si la unidad existe antes de intentar eliminarla
        if (!unidadSepulturaRepository.existsById(idUnidadSepultura)) {
            throw new IllegalArgumentException("La unidad con ID " + idUnidadSepultura + " no existe.");
        }
        try {
            unidadSepulturaRepository.deleteById(idUnidadSepultura);
            unidadSepulturaRepository.flush();
        } catch (DataIntegrityViolationException e) {
            // Tiene un sepelio asignado
            throw new IllegalStateException("No se puede eliminar la unidad. Tiene un sepelio asignado.", e);
        }
    }
}
