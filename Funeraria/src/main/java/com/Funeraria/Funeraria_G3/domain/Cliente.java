package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_clientes_tb")
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    // La cédula del cliente es su llave primaria y también la de la persona
    @Id
    @Column(name = "cedula_cliente", length = 20)
    private String cedulaCliente;

    @ManyToOne
    @JoinColumn(name = "cedula_cliente", insertable = false, updatable = false)
    private Persona persona;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaRegistro;

    @Column(length = 100)
    private String referenciaFamiliar;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;
}
