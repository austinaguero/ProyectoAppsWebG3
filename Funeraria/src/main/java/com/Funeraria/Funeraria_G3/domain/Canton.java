package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_canton_tb")
public class Canton implements Serializable {

    private static final long serialVersionUID = 1L;

    // Código oficial del cantón (provincia + 2 dígitos, ej. 101)
    @Id
    @Column(name = "id_canton")
    private Integer idCanton;

    @Column(length = 100)
    private String nombreCanton;

    @ManyToOne
    @JoinColumn(name = "id_provincia")
    private Provincia provincia;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;
}
