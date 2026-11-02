/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.Funeraria.Funeraria_G3.repository;

import com.Funeraria.Funeraria_G3.domain.Cliente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {

    // Clientes según su estado (Activo / Inactivo)
    public List<Cliente> findByEstadoNombreEstado(String nombreEstado);

    // HU-02: búsqueda por cédula, nombre o primer apellido
    public List<Cliente> findByCedulaClienteContainingOrPersonaNombreContainingIgnoreCaseOrPersonaApellido1ContainingIgnoreCase(
            String cedula, String nombre, String apellido);
}
