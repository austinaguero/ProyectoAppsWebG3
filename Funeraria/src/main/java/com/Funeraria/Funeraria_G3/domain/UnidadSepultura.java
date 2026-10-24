package com.Funeraria.Funeraria_G3.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fide_unidades_sepultura_tb")
public class UnidadSepultura implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidad_sepultura")
    private Integer idUnidadSepultura;

    @ManyToOne
    @JoinColumn(name = "id_cementerio")
    private Cementerio cementerio;

    @ManyToOne
    @JoinColumn(name = "id_tipo_unidad")
    private TipoUnidad tipoUnidad;

    @Column(nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String sector;

    @Column(nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String bloque;

    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Integer numero;

    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Integer capacidad;

    // Disponible, Reservada u Ocupada
    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;

    // Texto corto para mostrar la ubicación en selects y listados
    public String getUbicacion() {
        return (cementerio != null ? cementerio.getNombreCementerio() : "")
                + " · " + sector + " · " + bloque + " · #" + numero;
    }

    // Desde el formulario llegan solo los ids de los selects, por eso se valida que vengan
    @AssertTrue
    public boolean isCementerioSeleccionado() {
        return cementerio != null && cementerio.getIdCementerio() != null;
    }

    @AssertTrue
    public boolean isTipoUnidadSeleccionado() {
        return tipoUnidad != null && tipoUnidad.getIdTipoUnidad() != null;
    }

    @AssertTrue
    public boolean isEstadoSeleccionado() {
        return estado != null && estado.getIdEstado() != null;
    }
}
