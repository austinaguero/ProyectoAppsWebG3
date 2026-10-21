package com.memoriales.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;

@Data
@Entity
@Table(name = "fide_servicios_tb")
public class Servicio implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private Long idServicio;

    @Column(name = "cedula_fallecido", nullable = false)
    private String cedulaFallecido; // HU-05: Fallecido asociado

    @Column(name = "id_tipo_servicio", nullable = false)
    private Integer idTipoServicio;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_solicitud")
    private LocalDate fechaSolicitud = LocalDate.now();

    @Column(name = "id_estado", nullable = false)
    private Integer idEstado = 1; // 1 = Activo
}