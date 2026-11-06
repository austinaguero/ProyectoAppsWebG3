package com.Funeraria.Funeraria_G3.repository;

import com.Funeraria.Funeraria_G3.domain.PlanPrevision;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanPrevisionRepository extends JpaRepository<PlanPrevision, Integer> {

    // Planes activos y vigentes en una fecha (select del contrato de la HU-10)
    public List<PlanPrevision> findByEstadoNombreEstadoAndVigenciaInicioLessThanEqualAndVigenciaFinGreaterThanEqualOrderByIdPlanAsc(
            String nombreEstado, LocalDate fechaInicio, LocalDate fechaFin);

    //Consulta SQL (HU-10) que recupera el precio de un plan: la suma de los costos de los servicios que cubre
    @Query(nativeQuery = true,
            value = "SELECT COALESCE(SUM(c.costo), 0) FROM fide_planes_servicios_tb s "
            + "JOIN fide_planes_costos_tb c ON c.id_plan_servicio = s.id_plan_servicio "
            + "WHERE s.id_plan = :idPlan")
    public BigDecimal consultaSQLPrecio(Integer idPlan);
}
