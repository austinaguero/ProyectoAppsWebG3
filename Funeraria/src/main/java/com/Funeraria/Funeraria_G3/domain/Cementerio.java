package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_cementerios_tb")
public class Cementerio implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_cementerio")
    private Integer idCementerio;

    @Column(nullable = false, length = 100)
    private String nombreCementerio;

    // La dirección del cementerio todavía no se usa en el sistema
    @Column(name = "id_direccion_cementerio")
    private Integer idDireccionCementerio;

    @Column(nullable = false)
    private Integer capacidadTotal;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;
}
