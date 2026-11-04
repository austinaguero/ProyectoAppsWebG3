package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_provincia_tb")
public class Provincia implements Serializable {

    private static final long serialVersionUID = 1L;

    // Código oficial de la provincia (1 a 7)
    @Id
    @Column(name = "id_provincia")
    private Integer idProvincia;

    @Column(length = 100)
    private String nombreProvincia;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;
}
