package com.Funeraria.Funeraria_G3.repository;

import com.Funeraria.Funeraria_G3.domain.Cuota;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CuotaRepository extends JpaRepository<Cuota, Integer> {

    //Consulta SQL (HU-12 regla de prelación) que recupera las cuotas anteriores del mismo contrato
    //que están vencidas y sin pagar
    @Query(nativeQuery = true,
            value = "SELECT q.* FROM fide_cuotas_tb q JOIN fide_estado_tb e ON e.id_estado = q.id_estado "
            + "WHERE q.id_financiamiento = :idFinanciamiento AND q.numero_cuota < :numeroCuota "
            + "AND e.nombre_estado <> 'Pagada' AND q.fecha_vencimiento < CURDATE() "
            + "ORDER BY q.numero_cuota ASC")
    public List<Cuota> consultaSQLMora(Integer idFinanciamiento, Integer numeroCuota);

    //Consulta SQL (HU-12 pago de varias cuotas) que recupera las cuotas sin pagar de un contrato a partir
    //de un número de cuota, ordenadas por número (las que se pagan juntas, en orden)
    @Query(nativeQuery = true,
            value = "SELECT q.* FROM fide_cuotas_tb q JOIN fide_estado_tb e ON e.id_estado = q.id_estado "
            + "WHERE q.id_financiamiento = :idFinanciamiento AND q.numero_cuota >= :numeroCuota "
            + "AND e.nombre_estado <> 'Pagada' "
            + "ORDER BY q.numero_cuota ASC")
    public List<Cuota> consultaSQLPendientes(Integer idFinanciamiento, Integer numeroCuota);
}
