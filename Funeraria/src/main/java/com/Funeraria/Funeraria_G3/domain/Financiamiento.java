package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.*;

// HU-10: contrato del plan de previsión de un cliente (expediente comercial)
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_financiamientos_tb")
public class Financiamiento implements Serializable {

    private static final long serialVersionUID = 1L;

    // Número de contrato
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_financiamiento")
    private Integer idFinanciamiento;

    @ManyToOne
    @JoinColumn(name = "id_plan")
    private PlanPrevision plan;

    @ManyToOne
    @JoinColumn(name = "cedula_cliente")
    private Cliente cliente;

    @Column(precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @NotNull
    @Min(1)
    @Max(60)
    private Integer cantidadCuotas;

    @Column(precision = 10, scale = 2)
    private BigDecimal saldoPendiente;

    private LocalDate fechaInicio;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;

    // Relación de uno a muchos con la clase Cuota (plan de pagos)
    // Sin "cascade" ni "orphanRemoval" para evitar la propagación de operaciones.
    @OneToMany(mappedBy = "financiamiento")
    private List<Cuota> cuotas;
}
