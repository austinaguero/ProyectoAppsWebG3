package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_sepelios_tb")
public class Sepelio implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sepelio")
    private Integer idSepelio;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_servicio")
    private Servicio servicio;

    // HU-09: unidad reservada para el sepelio (la base no permite repetirla en otro sepelio)
    @ManyToOne
    @JoinColumn(name = "id_unidad_sepultura")
    private UnidadSepultura unidadSepultura;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(nullable = false)
    private LocalDate fechaSepelio;

    // En la base de datos es un datetime: se arma con la fecha y la hora que llegan del formulario
    @Column(nullable = false)
    private LocalDateTime horaSepelio;

    @NotNull
    @Valid
    @ManyToOne
    @JoinColumn(name = "cedula_responsable")
    private Usuario responsable;

    @ManyToOne
    @JoinColumn(name = "cedula_fallecido")
    private Fallecido fallecido;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;
}
