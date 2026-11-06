package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.*;

// HU-11 / HU-12: cuota del plan de pagos de un contrato
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_cuotas_tb")
public class Cuota implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuota")
    private Integer idCuota;

    @ManyToOne
    @JoinColumn(name = "id_financiamiento")
    private Financiamiento financiamiento;

    private Integer numeroCuota;

    @Column(precision = 10, scale = 2)
    private BigDecimal monto;

    private LocalDate fechaVencimiento;

    // Pendiente o Pagada; «Al día» y «En mora» se deducen de la fecha de vencimiento
    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;

    // Relación de uno a muchos con la clase Pago (recibos de la cuota)
    // Sin "cascade" ni "orphanRemoval" para evitar la propagación de operaciones.
    @OneToMany(mappedBy = "cuota")
    private List<Pago> pagos;
}
