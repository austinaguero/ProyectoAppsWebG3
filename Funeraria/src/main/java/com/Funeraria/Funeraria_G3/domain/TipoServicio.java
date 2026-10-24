package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_tipos_servicio_tb")
public class TipoServicio implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_tipo_servicio")
    private Integer idTipoServicio;

    @Column(length = 100)
    private String nombreTipo;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;
}
