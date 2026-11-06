package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_planes_prevision_tb")
public class PlanPrevision implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_plan")
    private Integer idPlan;

    @Column(length = 100)
    private String nombrePlan;

    private LocalDate vigenciaInicio;

    private LocalDate vigenciaFin;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;

    // Relación de uno a muchos con la clase PlanServicio (servicios cubiertos por el plan)
    // Sin "cascade" ni "orphanRemoval" para evitar la propagación de operaciones.
    @OneToMany(mappedBy = "plan")
    private List<PlanServicio> servicios;
}
