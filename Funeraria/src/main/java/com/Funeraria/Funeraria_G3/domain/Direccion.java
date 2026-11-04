package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_direcciones_tb")
public class Direccion implements Serializable {

    private static final long serialVersionUID = 1L;

    // En la base la llave es (cedula, provincia, cantón, distrito); el sistema maneja
    // un solo domicilio por persona, por eso basta la cédula como identificador.
    @Id
    @Column(name = "cedula", length = 20)
    private String cedula;

    // Persona dueña del domicilio (misma cédula)
    @ManyToOne
    @JoinColumn(name = "cedula", insertable = false, updatable = false)
    private Persona persona;

    @ManyToOne
    @JoinColumn(name = "id_provincia")
    private Provincia provincia;

    @ManyToOne
    @JoinColumn(name = "id_canton")
    private Canton canton;

    @ManyToOne
    @JoinColumn(name = "id_distrito")
    private Distrito distrito;

    @Column(length = 200)
    private String otrasSennas;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;

    // Texto corto para el listado: distrito, cantón, provincia
    public String getResumen() {
        return distrito.getNombreDistrito() + ", " + canton.getNombreCanton() + ", " + provincia.getNombreProvincia();
    }
}
