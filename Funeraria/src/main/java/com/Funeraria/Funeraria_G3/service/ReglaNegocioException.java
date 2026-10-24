package com.Funeraria.Funeraria_G3.service;

import lombok.Getter;

// Se rompe una regla del negocio; lleva la clave del mensaje (messages.properties) que se muestra al usuario
@Getter
public class ReglaNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String clave;

    public ReglaNegocioException(String clave) {
        super(clave);
        this.clave = clave;
    }
}
