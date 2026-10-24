package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_usuarios_tb")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    // El id llega desde el formulario (select del responsable), por eso se valida
    @Id
    @NotBlank
    @Column(name = "cedula_usuario", length = 20)
    private String cedulaUsuario;

    @Column(nullable = false, length = 50)
    private String usuario;

    // La cédula del usuario es la misma de la persona
    @OneToOne
    @JoinColumn(name = "cedula_usuario", insertable = false, updatable = false)
    private Persona persona;

    @ManyToOne
    @JoinColumn(name = "id_rol")
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;
}
