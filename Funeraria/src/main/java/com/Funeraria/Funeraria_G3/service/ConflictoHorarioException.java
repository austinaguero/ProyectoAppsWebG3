package com.Funeraria.Funeraria_G3.service;

import com.Funeraria.Funeraria_G3.domain.Sepelio;
import lombok.Getter;

// HU-07: el responsable ya tiene un sepelio en ese rango de tiempo
@Getter
public class ConflictoHorarioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    // Sepelio que ya ocupa la agenda del responsable
    private final Sepelio sepelioExistente;

    public ConflictoHorarioException(Sepelio sepelioExistente) {
        super("El responsable ya tiene un sepelio programado en ese horario.");
        this.sepelioExistente = sepelioExistente;
    }
}
