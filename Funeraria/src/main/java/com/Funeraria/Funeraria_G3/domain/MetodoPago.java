package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_tipo_metodo_pago_tb")
public class MetodoPago implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_metodo_pago")
    private Integer idMetodoPago;

    @Column(length = 100)
    private String nombreMetodoPago;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;
}
