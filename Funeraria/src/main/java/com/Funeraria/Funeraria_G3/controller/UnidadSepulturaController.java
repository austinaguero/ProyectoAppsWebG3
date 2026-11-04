package com.Funeraria.Funeraria_G3.controller;

import com.Funeraria.Funeraria_G3.domain.UnidadSepultura;
import com.Funeraria.Funeraria_G3.service.UnidadSepulturaService;
import jakarta.validation.Valid;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/unidad")
public class UnidadSepulturaController {

    private final UnidadSepulturaService unidadSepulturaService;
    private final MessageSource messageSource;

    // HU-08: consulta de unidades disponibles filtrando por cementerio y sector
    @GetMapping("/consulta")
    public String consulta(@RequestParam(required = false) Integer idCementerio,
            @RequestParam(required = false) String sector,
            Model model) {
        model.addAttribute("cementerios", unidadSepulturaService.getCementerios());
        model.addAttribute("idCementerioActual", idCementerio);
        model.addAttribute("sectorActual", sector);

        if (idCementerio != null) {
            model.addAttribute("cementerioActual", unidadSepulturaService.getCementerio(idCementerio).orElse(null));
            model.addAttribute("sectores", unidadSepulturaService.consultaSQLSectores(idCementerio));
            // Sectores del cementerio con unidades disponibles, para marcarlos en las pestañas
            model.addAttribute("sectoresDisponibles", unidadSepulturaService.consultaSQLSectoresDisponibles(idCementerio));
        }

        if (idCementerio != null && sector != null && !sector.isBlank()) {
            var unidades = unidadSepulturaService.consultaSQL(idCementerio, sector);
            model.addAttribute("unidades", unidades);
            model.addAttribute("totalUnidades", unidades.size());
            if (unidades.isEmpty()) {
                // HU-08 criterio 2: se sugieren otros sectores o cementerios con espacio libre
                model.addAttribute("otrasOpciones", unidadSepulturaService.consultaSQLOtrasOpciones());
            }
        }
        return "unidad/consulta";
    }

    // Mantenimiento de unidades de sepultura
    @GetMapping("/listado")
    public String listado(Model model) {
        var unidades = unidadSepulturaService.getUnidades();
        model.addAttribute("unidades", unidades);
        model.addAttribute("totalUnidades", unidades.size());
        model.addAttribute("cementerios", unidadSepulturaService.getCementerios());
        model.addAttribute("tiposUnidad", unidadSepulturaService.getTiposUnidad());
        model.addAttribute("estadosUnidad", unidadSepulturaService.getEstadosUnidad());
        return "unidad/listado";
    }

    // Agrega o modifica según venga o no el idUnidadSepultura
    @PostMapping("/guardar")
    public String guardar(@Valid UnidadSepultura unidadSepultura, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String mensaje = unidadSepultura.getIdUnidadSepultura() == null ? "unidad.agregada" : "mensaje.actualizado";
        // Desde el formulario llegan solo los ids de los selects
        boolean faltaSelect = unidadSepultura.getCementerio() == null || unidadSepultura.getCementerio().getIdCementerio() == null
                || unidadSepultura.getTipoUnidad() == null || unidadSepultura.getTipoUnidad().getIdTipoUnidad() == null
                || unidadSepultura.getEstado() == null || unidadSepultura.getEstado().getIdEstado() == null;
        if (bindingResult.hasErrors() || faltaSelect) {
            titulo = "error"; // Falta algún dato o el número/capacidad no es válido
            mensaje = "unidad.error01";
        } else {
            try {
                unidadSepulturaService.save(unidadSepultura);
            } catch (IllegalArgumentException e) {
                titulo = "error"; // El estado no es válido o la unidad a modificar no existe
                mensaje = "unidad.error01";
            } catch (IllegalStateException e) {
                titulo = "error"; // Tiene un sepelio asignado y se intentó dejar «Disponible»
                mensaje = "unidad.error04";
            } catch (DataIntegrityViolationException e) {
                titulo = "error"; // Ya existe una unidad en esa ubicación (UNIQUE de la base)
                mensaje = "unidad.error02";
            }
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(mensaje, null, Locale.getDefault()));
        return "redirect:/unidad/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idUnidadSepultura, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            unidadSepulturaService.delete(idUnidadSepultura);
        } catch (IllegalArgumentException e) {
            titulo = "error"; // Captura la excepción de argumento inválido para el mensaje de "no existe"
            detalle = "unidad.error03";
        } catch (IllegalStateException e) {
            titulo = "error"; // Captura la excepción de estado ilegal para el mensaje de "datos asociados"
            detalle = "unidad.error05";
        } catch (Exception e) {
            titulo = "error";  // Captura cualquier otra excepción inesperada
            detalle = "unidad.error05";
            log.error("" + e);
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/unidad/listado";
    }
}
