package com.Funeraria.Funeraria_G3.repository;

import com.Funeraria.Funeraria_G3.domain.Cliente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {

    // Clientes según su estado (Activo / Inactivo)
    public List<Cliente> findByEstadoNombreEstado(String nombreEstado);

    //Consulta SQL (HU-02) que recupera los clientes cuya cédula o nombre completo (nombre y apellidos)
    //contiene el texto buscado y los ordena por apellido y nombre
    @Query(nativeQuery = true,
            value = "SELECT c.* FROM fide_clientes_tb c JOIN fide_personas_tb p ON p.cedula = c.cedula_cliente "
            + "WHERE c.cedula_cliente LIKE CONCAT('%', :texto, '%') "
            + "OR CONCAT_WS(' ', p.nombre, p.apellido_1, p.apellido_2) LIKE CONCAT('%', :texto, '%') "
            + "ORDER BY p.apellido_1, p.nombre")
    public List<Cliente> consultaSQL(String texto);
}
