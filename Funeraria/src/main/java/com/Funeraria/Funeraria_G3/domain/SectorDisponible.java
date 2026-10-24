package com.Funeraria.Funeraria_G3.domain;

// HU-08: cantidad de unidades disponibles por cementerio y sector (resultado de consulta, no es una tabla)
public record SectorDisponible(Integer idCementerio, String nombreCementerio, String sector, Long cantidad) {

}
