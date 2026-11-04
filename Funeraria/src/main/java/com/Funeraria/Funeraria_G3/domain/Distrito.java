package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_distrito_tb")
public class Distrito implements Serializable {

    private static final long serialVersionUID = 1L;

    // Código oficial del distrito (cantón + 2 dígitos, ej. 10101)
    @Id
    @Column(name = "id_distrito")
    private Integer idDistrito;

    @Column(length = 100)
    private String nombreDistrito;

    @ManyToOne
    @JoinColumn(name = "id_canton")
    private Canton canton;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;
}
