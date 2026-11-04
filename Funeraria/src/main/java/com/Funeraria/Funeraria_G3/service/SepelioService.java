package com.Funeraria.Funeraria_G3.service;

import com.Funeraria.Funeraria_G3.domain.Estado;
import com.Funeraria.Funeraria_G3.domain.Sepelio;
import com.Funeraria.Funeraria_G3.domain.UnidadSepultura;
import com.Funeraria.Funeraria_G3.repository.EstadoRepository;
import com.Funeraria.Funeraria_G3.repository.SepelioRepository;
import com.Funeraria.Funeraria_G3.repository.ServicioRepository;
import com.Funeraria.Funeraria_G3.repository.UnidadSepulturaRepository;
import com.Funeraria.Funeraria_G3.repository.UsuarioRepository;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SepelioService {

    private static final String ACTIVO = "Activo";
    private static final String PROGRAMADO = "Programado";
    private static final String DISPONIBLE = "Disponible";
    private static final String RESERVADA = "Reservada";

    // HU-07: horas que se considera ocupado el responsable desde el inicio de un sepelio
    @Value("${sepelio.duracion.horas:2}")
    private long duracionHoras;

    // Los repositorios son final para asegurar la inmutabilidad
    private final SepelioRepository sepelioRepository;
    private final ServicioRepository servicioRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstadoRepository estadoRepository;
    private final UnidadSepulturaRepository unidadSepulturaRepository;

    @Transactional(readOnly = true)
    public List<Sepelio> getSepelios() {
        return sepelioRepository.findAllByOrderByHoraSepelioAsc();
    }

    // HU-06: guarda el sepelio y deja el servicio en estado «Programado»
    // HU-09: reserva la unidad de sepultura escogida
    @Transactional
    public void programar(Sepelio sepelio, LocalTime hora) {
        var servicio = servicioRepository.findById(sepelio.getServicio().getIdServicio())
                .orElseThrow(() -> new IllegalArgumentException("El servicio funerario no existe."));
        var responsable = usuarioRepository.findById(sepelio.getResponsable().getCedulaUsuario())
                .orElseThrow(() -> new IllegalArgumentException("El colaborador responsable no existe."));

        // Precondiciones: servicio activo y con un fallecido asociado
        if (servicio.getEstado() == null || !ACTIVO.equals(servicio.getEstado().getNombreEstado())) {
            throw new IllegalStateException("El servicio funerario no está activo.");
        }
        if (servicio.getFallecido() == null) {
            throw new IllegalStateException("El servicio funerario no tiene un fallecido asociado.");
        }

        var unidad = getUnidad(sepelio.getUnidadSepultura().getIdUnidadSepultura());
        Estado programado = getEstado(PROGRAMADO);

        sepelio.setServicio(servicio);
        sepelio.setResponsable(responsable);
        sepelio.setFallecido(servicio.getFallecido());
        sepelio.setHoraSepelio(sepelio.getFechaSepelio().atTime(hora));
        sepelio.setEstado(programado);
        sepelio.setUnidadSepultura(unidad);
        // Se guarda de inmediato para que la base rechace una unidad ya asignada (HU-09 criterio 2)
        sepelioRepository.saveAndFlush(sepelio);

        unidad.setEstado(getEstado(RESERVADA));
        unidadSepulturaRepository.save(unidad);

        servicio.setEstado(programado);
        servicioRepository.save(servicio);
    }

    // Edición: cambian la fecha, la hora, el responsable y la unidad; el servicio se mantiene
    @Transactional
    public void actualizar(Sepelio sepelio, LocalTime hora) {
        var actual = sepelioRepository.findById(sepelio.getIdSepelio())
                .orElseThrow(() -> new IllegalArgumentException("El sepelio con ID " + sepelio.getIdSepelio() + " no existe."));
        var responsable = usuarioRepository.findById(sepelio.getResponsable().getCedulaUsuario())
                .orElseThrow(() -> new IllegalArgumentException("El colaborador responsable no existe."));

        // Si cambia la unidad: se reserva la nueva y la anterior se libera
        var anterior = actual.getUnidadSepultura();
        var idNueva = sepelio.getUnidadSepultura().getIdUnidadSepultura();
        if (anterior == null || !anterior.getIdUnidadSepultura().equals(idNueva)) {
            var nueva = getUnidad(idNueva);
            liberar(anterior);
            actual.setUnidadSepultura(nueva);
            nueva.setEstado(getEstado(RESERVADA));
            unidadSepulturaRepository.save(nueva);
        }

        actual.setFechaSepelio(sepelio.getFechaSepelio());
        actual.setHoraSepelio(sepelio.getFechaSepelio().atTime(hora));
        actual.setResponsable(responsable);
        sepelioRepository.saveAndFlush(actual);
    }

    // Al eliminar el sepelio el servicio vuelve a «Activo» y la unidad reservada queda libre
    @Transactional
    public void delete(Integer idSepelio) {
        var sepelio = sepelioRepository.findById(idSepelio)
                .orElseThrow(() -> new IllegalArgumentException("El sepelio con ID " + idSepelio + " no existe."));
        var servicio = sepelio.getServicio();
        var unidad = sepelio.getUnidadSepultura();
        sepelioRepository.delete(sepelio);
        sepelioRepository.flush();
        liberar(unidad);
        if (!sepelioRepository.existsByServicioIdServicio(servicio.getIdServicio())
                && PROGRAMADO.equals(servicio.getEstado().getNombreEstado())) {
            servicio.setEstado(getEstado(ACTIVO));
            servicioRepository.save(servicio);
        }
    }

    // HU-07: sepelios del mismo responsable que empiezan a menos de "duracionHoras"
    // antes o después de este. Al editar se excluye el mismo sepelio.
    @Transactional(readOnly = true)
    public List<Sepelio> getConflictoHorario(Sepelio sepelio, LocalTime hora) {
        var horaSepelio = sepelio.getFechaSepelio().atTime(hora);
        // Un sepelio nuevo no tiene id: se usa 0 para que no excluya ninguno
        var excluir = sepelio.getIdSepelio() != null ? sepelio.getIdSepelio() : 0;
        return sepelioRepository.consultaSQL(sepelio.getResponsable().getCedulaUsuario(),
                horaSepelio.minusHours(duracionHoras), horaSepelio.plusHours(duracionHoras), excluir);
    }

    // HU-09 precondición: la unidad escogida debe estar «Disponible»
    // (al editar también sirve la unidad que el sepelio ya tiene reservada)
    @Transactional(readOnly = true)
    public boolean isUnidadDisponible(Sepelio sepelio) {
        var idUnidad = sepelio.getUnidadSepultura().getIdUnidadSepultura();
        if (sepelio.getIdSepelio() != null) {
            var actual = sepelioRepository.findById(sepelio.getIdSepelio()).orElse(null);
            if (actual != null && actual.getUnidadSepultura() != null
                    && actual.getUnidadSepultura().getIdUnidadSepultura().equals(idUnidad)) {
                return true;
            }
        }
        var unidad = unidadSepulturaRepository.findById(idUnidad).orElse(null);
        return unidad != null && DISPONIBLE.equals(unidad.getEstado().getNombreEstado());
    }

    private UnidadSepultura getUnidad(Integer idUnidadSepultura) {
        return unidadSepulturaRepository.findById(idUnidadSepultura)
                .orElseThrow(() -> new IllegalArgumentException("La unidad de sepultura no existe."));
    }

    // Una unidad reservada vuelve a «Disponible»; si ya está ocupada se deja como está
    private void liberar(UnidadSepultura unidad) {
        if (unidad != null && RESERVADA.equals(unidad.getEstado().getNombreEstado())) {
            unidad.setEstado(getEstado(DISPONIBLE));
            unidadSepulturaRepository.save(unidad);
        }
    }

    private Estado getEstado(String nombre) {
        return estadoRepository.findByNombreEstado(nombre)
                .orElseThrow(() -> new IllegalStateException("No existe el estado " + nombre + "."));
    }
}
