package com.Funeraria.Funeraria_G3.repository;

import com.Funeraria.Funeraria_G3.domain.Financiamiento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FinanciamientoRepository extends JpaRepository<Financiamiento, Integer> {

    // Listado de contratos, del más reciente al más antiguo
    public List<Financiamiento> findAllByOrderByIdFinanciamientoDesc();

    //Consulta SQL (HU-10) que recupera los contratos cuyo número es el texto buscado o cuya cédula
    //o nombre completo del cliente contiene el texto, y los ordena del más reciente al más antiguo
    @Query(nativeQuery = true,
            value = "SELECT f.* FROM fide_financiamientos_tb f JOIN fide_personas_tb p ON p.cedula = f.cedula_cliente "
            + "WHERE CAST(f.id_financiamiento AS CHAR) = :texto "
            + "OR f.cedula_cliente LIKE CONCAT('%', :texto, '%') "
            + "OR CONCAT_WS(' ', p.nombre, p.apellido_1, p.apellido_2) LIKE CONCAT('%', :texto, '%') "
            + "ORDER BY f.id_financiamiento DESC")
    public List<Financiamiento> consultaSQL(String texto);

    //Consulta SQL (HU-11) del estado de cuenta: recupera los contratos cuyo número es el texto buscado o cuya
    //cédula o nombre del cliente contiene el texto (vacío trae todos), filtrados por situación:
    //«pagado» (todas sus cuotas pagadas), «mora» (alguna cuota vencida sin pagar) o «alDia»; vacío trae todos
    @Query(nativeQuery = true,
            value = "SELECT f.* FROM fide_financiamientos_tb f JOIN fide_personas_tb p ON p.cedula = f.cedula_cliente "
            + "WHERE (:texto = '' OR CAST(f.id_financiamiento AS CHAR) = :texto "
            + "OR f.cedula_cliente LIKE CONCAT('%', :texto, '%') "
            + "OR CONCAT_WS(' ', p.nombre, p.apellido_1, p.apellido_2) LIKE CONCAT('%', :texto, '%')) "
            + "AND (:situacion = '' OR :situacion = CASE "
            + "WHEN NOT EXISTS (SELECT 1 FROM fide_cuotas_tb q JOIN fide_estado_tb e ON e.id_estado = q.id_estado "
            + "WHERE q.id_financiamiento = f.id_financiamiento AND e.nombre_estado <> 'Pagada') THEN 'pagado' "
            + "WHEN EXISTS (SELECT 1 FROM fide_cuotas_tb q JOIN fide_estado_tb e ON e.id_estado = q.id_estado "
            + "WHERE q.id_financiamiento = f.id_financiamiento AND e.nombre_estado <> 'Pagada' "
            + "AND q.fecha_vencimiento < CURDATE()) THEN 'mora' "
            + "ELSE 'alDia' END) "
            + "ORDER BY f.id_financiamiento DESC")
    public List<Financiamiento> consultaSQLEstadoCuenta(String texto, String situacion);
}
