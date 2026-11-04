package com.Funeraria.Funeraria_G3.service;

import com.Funeraria.Funeraria_G3.domain.Cliente;
import com.Funeraria.Funeraria_G3.domain.Estado;
import com.Funeraria.Funeraria_G3.domain.Persona;
import com.Funeraria.Funeraria_G3.repository.ClienteRepository;
import com.Funeraria.Funeraria_G3.repository.EstadoRepository;
import com.Funeraria.Funeraria_G3.repository.PersonaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    // HU-02: consulta SQL por cédula o nombre completo
    @Transactional(readOnly = true)
    public List<Cliente> consultaSQL(String texto) {
        return clienteRepository.consultaSQL(texto.trim());
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> getCliente(String cedulaCliente) {
        return clienteRepository.findById(cedulaCliente.trim());
    }

    // HU-01: registrar un cliente nuevo (los datos obligatorios ya vienen validados con @Valid
    // y el controlador ya validó que la cédula no esté registrada como cliente)
    @Transactional
    public void registrar(Cliente cliente) {
        String cedula = cliente.getCedulaCliente().trim();
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

    private Estado getEstadoActivo() {
        return estadoRepository.findByNombreEstado(ACTIVO)
                .orElseThrow(() -> new IllegalStateException("No existe el estado " + ACTIVO + "."));
    }
}