package com.Funeraria.Funeraria_G3.service;

import com.Funeraria.Funeraria_G3.domain.Cliente;
import com.Funeraria.Funeraria_G3.domain.Estado;
import com.Funeraria.Funeraria_G3.domain.Persona;
import com.Funeraria.Funeraria_G3.repository.ClienteRepository;
import com.Funeraria.Funeraria_G3.repository.EstadoRepository;
import com.Funeraria.Funeraria_G3.repository.PersonaRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private static final String ACTIVO = "Activo";

    private final ClienteRepository clienteRepository;
    private final PersonaRepository personaRepository;
    private final EstadoRepository estadoRepository;

    @Transactional(readOnly = true)
    public List<Cliente> getClientes(boolean activo) {
        if (activo) { //Sólo activos
            return clienteRepository.findByEstadoNombreEstado(ACTIVO);
        }
        return clienteRepository.findAll();
    }

    // HU-02: consulta por cédula, nombre o primer apellido
    @Transactional(readOnly = true)
    public List<Cliente> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return clienteRepository.findAll();
        }
        String t = texto.trim();
        return clienteRepository
                .findByCedulaClienteContainingOrPersonaNombreContainingIgnoreCaseOrPersonaApellido1ContainingIgnoreCase(t, t, t);
    }

    // HU-01: registrar un cliente nuevo
    @Transactional
    public void registrar(Cliente cliente) {
        validarDatos(cliente);
        String cedula = cliente.getCedulaCliente().trim();

        // Regla de negocio: la cédula no puede estar registrada como cliente
        if (clienteRepository.existsById(cedula)) {
            throw new ReglaNegocioException("cliente.error01");
        }
        Estado activo = getEstadoActivo();

        // 1. Se guardan (o actualizan) los datos personales en la DB
        Persona persona = personaRepository.findById(cedula).orElseGet(Persona::new);
        persona.setCedula(cedula);
        persona.setNombre(cliente.getPersona().getNombre().trim());
        persona.setApellido1(cliente.getPersona().getApellido1().trim());
        persona.setApellido2(cliente.getPersona().getApellido2());
        persona.setEstado(activo);
        persona = personaRepository.save(persona);

        // 2. Se guarda el cliente en fide_clientes_tb, nace en estado Activo
        cliente.setCedulaCliente(cedula);
        cliente.setPersona(persona);
        cliente.setFechaRegistro(LocalDate.now());
        cliente.setEstado(activo);
        clienteRepository.save(cliente);
    }

    // HU-03: actualizar la información de un cliente existente
    @Transactional
    public void actualizar(Cliente cliente) {
        validarDatos(cliente);
        var actual = clienteRepository.findById(cliente.getCedulaCliente())
                .orElseThrow(() -> new IllegalArgumentException(
                        "El cliente con cédula " + cliente.getCedulaCliente() + " no existe."));

        // La cédula no se modifica: sí el nombre, apellidos y la referencia familiar
        Persona persona = actual.getPersona();
        persona.setNombre(cliente.getPersona().getNombre().trim());
        persona.setApellido1(cliente.getPersona().getApellido1().trim());
        persona.setApellido2(cliente.getPersona().getApellido2());
        personaRepository.save(persona);

        actual.setReferenciaFamiliar(cliente.getReferenciaFamiliar());
        clienteRepository.save(actual);
    }

    // Cédula, nombre y primer apellido son obligatorios
    private void validarDatos(Cliente cliente) {
        if (cliente.getCedulaCliente() == null || cliente.getCedulaCliente().isBlank()
                || cliente.getPersona() == null
                || cliente.getPersona().getNombre() == null || cliente.getPersona().getNombre().isBlank()
                || cliente.getPersona().getApellido1() == null || cliente.getPersona().getApellido1().isBlank()) {
            throw new ReglaNegocioException("cliente.error02");
        }
    }

    private Estado getEstadoActivo() {
        return estadoRepository.findByNombreEstado(ACTIVO)
                .orElseThrow(() -> new IllegalStateException("No existe el estado " + ACTIVO + "."));
    }
}