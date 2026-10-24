package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_fallecidos_tb")
public class Fallecido implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "cedula_fallecido", length = 20)
    private String cedulaFallecido;

    // La cédula del fallecido es la misma de la persona
    @OneToOne
    @JoinColumn(name = "cedula_fallecido", insertable = false, updatable = false)
    private Persona persona;

    @Column(nullable = false, length = 20)
    private String cedulaCliente;

    private LocalDate fechaNacimiento;

    private LocalDate fechaFallecimiento;

    @Column(length = 150)
    private String lugarFallecimiento;

    @Column(length = 150)
    private String causaFallecimiento;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;
}
