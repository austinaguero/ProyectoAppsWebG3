package com.Funeraria.Funeraria_G3.service;

import com.Funeraria.Funeraria_G3.domain.Servicio;
import com.Funeraria.Funeraria_G3.repository.EstadoRepository;
import com.Funeraria.Funeraria_G3.repository.SepelioRepository;
import com.Funeraria.Funeraria_G3.repository.ServicioRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServicioService {

    private static final String ACTIVO = "Activo";

    // Los repositorios son final para asegurar la inmutabilidad
    private final ServicioRepository servicioRepository;
    private final SepelioRepository sepelioRepository;
    private final EstadoRepository estadoRepository;

    @Transactional(readOnly = true)
    public List<Servicio> getServicios(boolean activo) {
        if (activo) { //Sólo activos...
            return servicioRepository.findByEstadoNombreEstado(ACTIVO);
        }
        return servicioRepository.findAll();
    }

    @Transactional
    public void save(Servicio servicio) {
        if (servicio.getFechaSolicitud() == null) {
            servicio.setFechaSolicitud(LocalDate.now());
        }
        if (servicio.getIdServicio() == null) {
            // Un servicio nuevo inicia activo
            servicio.setEstado(estadoRepository.findByNombreEstado(ACTIVO)
                    .orElseThrow(() -> new IllegalStateException("No existe el estado " + ACTIVO + ".")));
            servicioRepository.save(servicio);
            return;
        }
        // Al editar se conserva el estado actual del servicio
        var actual = servicioRepository.findById(servicio.getIdServicio())
                .orElseThrow(() -> new IllegalArgumentException("El servicio con ID " + servicio.getIdServicio() + " no existe."));
        boolean cambiaFallecido = !actual.getFallecido().getCedulaFallecido()
                .equals(servicio.getFallecido().getCedulaFallecido());
        if (cambiaFallecido && sepelioRepository.existsByServicioIdServicio(actual.getIdServicio())) {
            // El sepelio guarda la cédula del fallecido, no se puede cambiar por debajo
            throw new IllegalStateException("El servicio tiene un sepelio programado, no se puede cambiar el fallecido.");
        }
        actual.setFallecido(servicio.getFallecido());
        actual.setTipoServicio(servicio.getTipoServicio());
        actual.setDescripcion(servicio.getDescripcion());
        actual.setFechaSolicitud(servicio.getFechaSolicitud());
        servicioRepository.save(actual);
    }

    @Transactional
    public void delete(Integer idServicio) {
        // Verifica si el servicio existe antes de intentar eliminarlo
        if (!servicioRepository.existsById(idServicio)) {
            throw new IllegalArgumentException("El servicio con ID " + idServicio + " no existe.");
        }
        try {
            servicioRepository.deleteById(idServicio);
            servicioRepository.flush();
        } catch (DataIntegrityViolationException e) {
            // Tiene sepelios u otros registros asociados
            throw new IllegalStateException("No se puede eliminar el servicio. Tiene datos asociados.", e);
        }
    }
}
