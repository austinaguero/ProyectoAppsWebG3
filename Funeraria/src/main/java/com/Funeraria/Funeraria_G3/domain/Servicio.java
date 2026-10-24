package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_servicios_tb")
public class Servicio implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private Integer idServicio;

    // Relación con el fallecido asociado al servicio (HU-05)
    @ManyToOne
    @JoinColumn(name = "cedula_fallecido")
    private Fallecido fallecido;

    @ManyToOne
    @JoinColumn(name = "id_tipo_servicio")
    private TipoServicio tipoServicio;

    @Column(length = 200)
    @Size(max = 200)
    private String descripcion;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaSolicitud;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;

    // Desde el formulario llegan solo los ids de los selects, por eso se valida que vengan
    @AssertTrue
    public boolean isFallecidoSeleccionado() {
        return fallecido != null && fallecido.getCedulaFallecido() != null
                && !fallecido.getCedulaFallecido().isBlank();
    }

    @AssertTrue
    public boolean isTipoServicioSeleccionado() {
        return tipoServicio != null && tipoServicio.getIdTipoServicio() != null;
    }
}
