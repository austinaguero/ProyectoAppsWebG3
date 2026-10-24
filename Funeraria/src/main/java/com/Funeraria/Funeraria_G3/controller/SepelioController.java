package com.Funeraria.Funeraria_G3.controller;

import com.Funeraria.Funeraria_G3.domain.Sepelio;
import com.Funeraria.Funeraria_G3.service.ConflictoHorarioException;
import com.Funeraria.Funeraria_G3.service.ReglaNegocioException;
import com.Funeraria.Funeraria_G3.service.SepelioService;
import com.Funeraria.Funeraria_G3.service.UnidadSepulturaService;
import com.Funeraria.Funeraria_G3.service.ServicioService;
import com.Funeraria.Funeraria_G3.service.UsuarioService;
import jakarta.validation.Valid;
import java.time.format.DateTimeFormatter;
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
@RequestMapping("/sepelio")
public class SepelioController {

    private final SepelioService sepelioService;
    private final ServicioService servicioService;
    private final UsuarioService usuarioService;
    private final UnidadSepulturaService unidadSepulturaService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(Model model) {
        var sepelios = sepelioService.getSepelios();
        model.addAttribute("sepelios", sepelios);
        model.addAttribute("totalSepelios", sepelios.size());
        model.addAttribute("servicios", servicioService.getServicios(true));
        model.addAttribute("colaboradores", usuarioService.getColaboradores());
        // HU-09: solo se ofrecen las unidades «Disponible»
        model.addAttribute("unidadesDisponibles", unidadSepulturaService.getDisponibles());
        return "sepelio/listado";
    }

    // Programa un sepelio nuevo o modifica uno existente según venga o no el idSepelio
    @PostMapping("/guardar")
    public String guardar(@Valid Sepelio sepelio, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        boolean nuevo = sepelio.getIdSepelio() == null;
        String titulo = "todoOk";
        String mensaje = nuevo ? "sepelio.programado" : "sepelio.actualizado";
        Object[] datos = null;
        if (bindingResult.hasErrors()) {
            titulo = "error";
            if (bindingResult.getErrorCount() == 1 && bindingResult.hasFieldErrors("unidadSeleccionada")) {
                mensaje = "sepelio.error07"; // Solo falta el espacio donde sepultar
            } else {
                mensaje = "sepelio.error01"; // Fecha, hora o responsable vacíos (HU-06 criterio 2)
            }
        } else {
            try {
                if (nuevo) {
                    sepelioService.programar(sepelio);
                } else {
                    sepelioService.actualizar(sepelio);
                }
            } catch (ConflictoHorarioException e) {
                titulo = "error"; // El responsable ya tiene otro sepelio a esa hora (HU-07 criterio 2)
                mensaje = "sepelio.conflicto";
                var existente = e.getSepelioExistente();
                datos = new Object[]{
                    existente.getResponsable().getPersona().getNombreCompleto(),
                    existente.getFallecido().getPersona().getNombreCompleto(),
                    existente.getHoraSepelio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    existente.getHoraSepelio().format(DateTimeFormatter.ofPattern("HH:mm"))};
            } catch (ReglaNegocioException e) {
                titulo = "error"; // La unidad ya no está disponible (HU-09)
                mensaje = e.getClave();
            } catch (IllegalArgumentException e) {
                titulo = "error"; // El sepelio, el servicio o el responsable no existen
                mensaje = "sepelio.error02";
            } catch (IllegalStateException e) {
                titulo = "error"; // El servicio no está activo o no tiene fallecido
                mensaje = "sepelio.error03";
            } catch (DataIntegrityViolationException e) {
                titulo = "error"; // La base rechazó la asignación doble de la unidad (HU-09 criterio 2)
                mensaje = "sepelio.error08";
                log.error("" + e);
            }
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(mensaje, datos, LocaleContextHolder.getLocale()));
        return "redirect:/sepelio/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idSepelio, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "sepelio.eliminado";
        try {
            sepelioService.delete(idSepelio);
        } catch (IllegalArgumentException e) {
            titulo = "error"; // Captura la excepción de argumento inválido para el mensaje de "no existe"
            detalle = "sepelio.error05";
        } catch (Exception e) {
            titulo = "error";  // Captura cualquier otra excepción inesperada
            detalle = "sepelio.error04";
            log.error("" + e);
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(detalle, null, LocaleContextHolder.getLocale()));
        return "redirect:/sepelio/listado";
    }
}
