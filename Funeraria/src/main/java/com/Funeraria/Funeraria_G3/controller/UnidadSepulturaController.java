package com.Funeraria.Funeraria_G3.controller;

import com.Funeraria.Funeraria_G3.domain.SectorDisponible;
import com.Funeraria.Funeraria_G3.domain.UnidadSepultura;
import com.Funeraria.Funeraria_G3.service.ReglaNegocioException;
import com.Funeraria.Funeraria_G3.service.UnidadSepulturaService;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
        var cementerios = unidadSepulturaService.getCementerios();
        var conDisponibles = unidadSepulturaService.getSectoresConDisponibles();
        model.addAttribute("cementerios", cementerios);
        model.addAttribute("idCementerioActual", idCementerio);
        model.addAttribute("sectorActual", sector);

        if (idCementerio != null) {
            var cementerio = cementerios.stream()
                    .filter(c -> c.getIdCementerio().equals(idCementerio))
                    .findFirst().orElse(null);
            model.addAttribute("cementerioActual", cementerio);
            model.addAttribute("sectores", unidadSepulturaService.getSectores(idCementerio));
            // Cantidad de disponibles por sector del cementerio, para mostrarla en cada pestaña
            Map<String, Long> disponiblesPorSector = conDisponibles.stream()
                    .filter(s -> s.idCementerio().equals(idCementerio))
                    .collect(Collectors.toMap(SectorDisponible::sector, SectorDisponible::cantidad));
            model.addAttribute("disponiblesPorSector", disponiblesPorSector);
        }

        if (idCementerio != null && sector != null && !sector.isBlank()) {
            var unidades = unidadSepulturaService.getDisponibles(idCementerio, sector);
            model.addAttribute("unidades", unidades);
            model.addAttribute("totalUnidades", unidades.size());
            if (unidades.isEmpty()) {
                // HU-08 criterio 2: se sugieren otros sectores o cementerios con espacio libre
                model.addAttribute("otrasOpciones", conDisponibles);
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
        if (bindingResult.hasErrors()) {
            titulo = "error"; // Falta algún dato o el número/capacidad no es válido
            mensaje = "unidad.error01";
        } else {
            try {
                unidadSepulturaService.save(unidadSepultura);
            } catch (ReglaNegocioException e) {
                titulo = "error";
                mensaje = e.getClave();
            } catch (DataIntegrityViolationException e) {
                titulo = "error";
                mensaje = "unidad.error01";
                log.error("" + e);
            }
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(mensaje, null, LocaleContextHolder.getLocale()));
        return "redirect:/unidad/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idUnidadSepultura, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            unidadSepulturaService.delete(idUnidadSepultura);
        } catch (ReglaNegocioException e) {
            titulo = "error";
            detalle = e.getClave();
        } catch (Exception e) {
            titulo = "error";  // Captura cualquier otra excepción inesperada
            detalle = "unidad.error05";
            log.error("" + e);
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(detalle, null, LocaleContextHolder.getLocale()));
        return "redirect:/unidad/listado";
    }
}
