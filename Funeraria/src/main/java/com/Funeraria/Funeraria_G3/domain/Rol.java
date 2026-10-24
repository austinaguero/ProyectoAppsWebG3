package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_roles_tb")
public class Rol implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(nullable = false, length = 100)
    private String nombreRol;

    @Column(length = 200)
    private String descripcion;
}
