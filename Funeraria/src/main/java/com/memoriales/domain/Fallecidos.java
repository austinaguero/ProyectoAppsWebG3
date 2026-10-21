package com.memoriales.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;

@Data
@Entity
@Table(name = "fide_fallecidos_tb")
public class Fallecidos implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "cedula_fallecido")
    private String cedulaFallecido;

    @Column(name = "cedula_cliente", nullable = false)
    private String cedulaCliente;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "fecha_fallecimiento")
    private LocalDate fechaFallecimiento;

    @Column(name = "lugar_fallecimiento")
    private String lugarFallecimiento;

    @Column(name = "causa_fallecimiento")
    private String causaFallecimiento;

    @Column(name = "id_estado", nullable = false)
    private Integer idEstado = 1;
}