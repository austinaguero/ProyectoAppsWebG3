package com.Funeraria.Funeraria_G3.service;

import com.Funeraria.Funeraria_G3.domain.Direccion;
import com.Funeraria.Funeraria_G3.domain.Fallecido;
import com.Funeraria.Funeraria_G3.domain.Persona;
import com.Funeraria.Funeraria_G3.repository.ClienteRepository;
import com.Funeraria.Funeraria_G3.repository.DireccionRepository;
import com.Funeraria.Funeraria_G3.repository.DistritoRepository;
import com.Funeraria.Funeraria_G3.repository.EstadoRepository;
import com.Funeraria.Funeraria_G3.repository.FallecidoRepository;
import com.Funeraria.Funeraria_G3.repository.PersonaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FallecidoService {

    private static final String ACTIVO = "Activo";

    // Los repositorios son final para asegurar la inmutabilidad
    private final FallecidoRepository fallecidoRepository;
    private final PersonaRepository personaRepository;
    private final ClienteRepository clienteRepository;
    private final EstadoRepository estadoRepository;
    private final DistritoRepository distritoRepository;
    private final DireccionRepository direccionRepository;

    @Transactional(readOnly = true)
    public List<Fallecido> getFallecidos(boolean activo) {
        if (activo) { //Sólo activos...
            return fallecidoRepository.findByEstadoNombreEstado(ACTIVO);
        }
        return fallecidoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Fallecido> getFallecido(String cedulaFallecido) {
        return fallecidoRepository.findById(cedulaFallecido.trim());
    }

    // HU-04: registra (o modifica) al fallecido y lo asocia con el cliente responsable.
    // La cédula es la llave, por eso se indica si es una modificación.
    // También guarda su dirección de domicilio (provincia, cantón, distrito y otras señas).
    @Transactional
    public void save(Fallecido fallecido, boolean edicion,
            Integer idProvincia, Integer idCanton, Integer idDistrito, String otrasSennas) {
        var cedula = fallecido.getCedulaFallecido().trim();
        var cliente = clienteRepository.findById(fallecido.getCliente().getCedulaCliente())
                .orElseThrow(() -> new IllegalArgumentException("El cliente responsable no existe."));
        // El controlador ya validó que el distrito sea del cantón y el cantón de la provincia
        var distrito = distritoRepository.findById(idDistrito)
                .orElseThrow(() -> new IllegalArgumentException("La dirección no es válida."));
        var activo = estadoRepository.findByNombreEstado(ACTIVO)
                .orElseThrow(() -> new IllegalStateException("No existe el estado " + ACTIVO + "."));

        Fallecido registro;
        if (edicion) {
            registro = fallecidoRepository.findById(cedula)
                    .orElseThrow(() -> new IllegalArgumentException("El fallecido con cédula " + cedula + " no existe."));
        } else {
            // El controlador ya validó que la cédula no esté registrada como fallecido
            registro = new Fallecido();
            registro.setCedulaFallecido(cedula);
            registro.setEstado(activo);
        }

        // Datos de la persona: si la cédula ya existe (por ejemplo era cliente) se actualiza su nombre
        var persona = personaRepository.findById(cedula).orElseGet(() -> {
            var nueva = new Persona();
            nueva.setCedula(cedula);
            nueva.setEstado(activo);
            return nueva;
        });
        persona.setNombre(fallecido.getPersona().getNombre().trim());
        persona.setApellido1(fallecido.getPersona().getApellido1().trim());
        persona.setApellido2(fallecido.getPersona().getApellido2() != null
                ? fallecido.getPersona().getApellido2().trim() : null);
        personaRepository.save(persona);

        // Domicilio: un solo registro por persona, se crea o se actualiza
        var direccion = direccionRepository.findById(cedula).orElseGet(() -> {
            var nueva = new Direccion();
            nueva.setCedula(cedula);
            nueva.setEstado(activo);
            return nueva;
        });
        direccion.setDistrito(distrito);
        direccion.setCanton(distrito.getCanton());
        direccion.setProvincia(distrito.getCanton().getProvincia());
        direccion.setOtrasSennas(otrasSennas != null && !otrasSennas.isBlank() ? otrasSennas.trim() : null);
        direccionRepository.save(direccion);

        registro.setPersona(persona);
        registro.setCliente(cliente);
        registro.setFechaNacimiento(fallecido.getFechaNacimiento());
        registro.setFechaFallecimiento(fallecido.getFechaFallecimiento());
        registro.setLugarFallecimiento(fallecido.getLugarFallecimiento());
        registro.setCausaFallecimiento(fallecido.getCausaFallecimiento());
        fallecidoRepository.saveAndFlush(registro);
    }

    // Se elimina el registro del fallecido; la persona se conserva
    @Transactional
    public void delete(String cedulaFallecido) {
        // Verifica si el fallecido existe antes de intentar eliminarlo
        if (!fallecidoRepository.existsById(cedulaFallecido)) {
            throw new IllegalArgumentException("El fallecido con cédula " + cedulaFallecido + " no existe.");
        }
        try {
            fallecidoRepository.deleteById(cedulaFallecido);
            fallecidoRepository.flush();
        } catch (DataIntegrityViolationException e) {
            // Tiene servicios funerarios o sepelios asociados
            throw new IllegalStateException("No se puede eliminar el fallecido. Tiene datos asociados.", e);
        }
    }
}
