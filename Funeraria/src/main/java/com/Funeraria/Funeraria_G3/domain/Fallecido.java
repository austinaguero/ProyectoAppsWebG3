package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_fallecidos_tb")
public class Fallecido implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @NotBlank
    @Size(max = 20)
    @Column(name = "cedula_fallecido", length = 20)
    private String cedulaFallecido;

    // La cédula del fallecido es la misma de la persona (nombre y apellidos).
    // Se mapea como ManyToOne de solo lectura: con OneToOne Hibernate no puede insertar
    // un fallecido nuevo cuando la llave la comparte con la persona.
    @Valid
    @ManyToOne
    @JoinColumn(name = "cedula_fallecido", insertable = false, updatable = false)
    private Persona persona;

    // HU-04: cliente responsable del fallecido
    @ManyToOne
    @JoinColumn(name = "cedula_cliente")
    private Cliente cliente;

    @NotNull
    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;

    @NotNull
    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFallecimiento;

    @Column(length = 150)
    @Size(max = 150)
    private String lugarFallecimiento;

    @Column(length = 150)
    @Size(max = 150)
    private String causaFallecimiento;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;

    // Relación de uno a muchos con la clase Sepelio (cementerio y ubicación en el listado)
    // Sin "cascade" ni "orphanRemoval" para evitar la propagación de operaciones.
    @OneToMany(mappedBy = "fallecido")
    private List<Sepelio> sepelios;
}
