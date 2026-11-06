package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_planes_servicios_tb")
public class PlanServicio implements Serializable {

    private static final long serialVersionUID = 1L;

    // En la base la llave es (id_plan_servicio, id_tipo_servicio), pero id_plan_servicio es único
    @Id
    @Column(name = "id_plan_servicio")
    private Integer idPlanServicio;

    @ManyToOne
    @JoinColumn(name = "id_plan")
    private PlanPrevision plan;

    // Servicio funerario que cubre el plan (Velación, Sepelio, Cremación)
    @ManyToOne
    @JoinColumn(name = "id_tipo_servicio")
    private TipoServicio tipoServicio;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;
}
