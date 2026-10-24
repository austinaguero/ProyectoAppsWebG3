package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_tipos_unidad_tb")
public class TipoUnidad implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_tipo_unidad")
    private Integer idTipoUnidad;

    @Column(nullable = false, length = 100)
    private String nombreTipoUnidad;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;
}
