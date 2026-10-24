package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_personas_tb")
public class Persona implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 20)
    private String cedula;

    @Column(length = 100)
    private String nombre;

    @Column(name = "apellido_1", length = 100)
    private String apellido1;

    @Column(name = "apellido_2", length = 100)
    private String apellido2;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;

    // Nombre y apellidos juntos para mostrar en listados y selects
    public String getNombreCompleto() {
        return String.join(" ",
                nombre != null ? nombre : "",
                apellido1 != null ? apellido1 : "",
                apellido2 != null ? apellido2 : "").trim();
    }
}
