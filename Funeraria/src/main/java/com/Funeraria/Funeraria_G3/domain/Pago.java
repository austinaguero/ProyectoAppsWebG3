package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

// HU-12: pago de una cuota; el número de comprobante es el recibo
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_pagos_tb")
public class Pago implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer idPago;

    @ManyToOne
    @JoinColumn(name = "id_cuota")
    private Cuota cuota;

    @ManyToOne
    @JoinColumn(name = "id_metodo_pago")
    private MetodoPago metodoPago;

    private LocalDate fechaPago;

    @Column(precision = 10, scale = 2)
    @NotNull(message = "El monto no puede estar vacío.")
    @DecimalMin(value = "0.01", inclusive = true, message = "El monto debe ser mayor a 0.")
    private BigDecimal montoPagado;

    // Recargo por mora incluido en montoPagado (0 si la cuota se pagó a tiempo)
    @Column(precision = 10, scale = 2)
    private BigDecimal recargo;

    @Column(length = 50)
    private String numComprobante;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;
}
