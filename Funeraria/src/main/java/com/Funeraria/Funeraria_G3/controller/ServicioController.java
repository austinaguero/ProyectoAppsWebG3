package com.Funeraria.Funeraria_G3.controller;

import com.Funeraria.Funeraria_G3.domain.Servicio;
import com.Funeraria.Funeraria_G3.service.FallecidoService;
import com.Funeraria.Funeraria_G3.service.ServicioService;
import com.Funeraria.Funeraria_G3.service.TipoServicioService;
import jakarta.validation.Valid;
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
@RequestMapping("/servicio")
public class ServicioController {

    private final ServicioService servicioService;
    private final FallecidoService fallecidoService;
    private final TipoServicioService tipoServicioService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(Model model) {
        var servicios = servicioService.getServicios(false);
        model.addAttribute("servicios", servicios);
        model.addAttribute("totalServicios", servicios.size());
        model.addAttribute("fallecidos", fallecidoService.getFallecidos(true));
        model.addAttribute("tiposServicio", tipoServicioService.getTiposServicio(true));
        return "servicio/listado";
    }

    // Agrega o modifica según venga o no el idServicio
    @PostMapping("/guardar")
    public String guardar(@Valid Servicio servicio, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String mensaje = servicio.getIdServicio() == null ? "servicio.agregado" : "mensaje.actualizado";
        if (bindingResult.hasErrors()) {
            titulo = "error"; // Falta el fallecido o el tipo de servicio
            mensaje = "servicio.error01";
        } else {
            try {
                servicioService.save(servicio);
            } catch (IllegalArgumentException e) {
                titulo = "error"; // El servicio a modificar no existe
                mensaje = "servicio.error03";
            } catch (IllegalStateException e) {
                titulo = "error"; // Tiene sepelio y se intentó cambiar el fallecido
                mensaje = "servicio.error05";
            } catch (DataIntegrityViolationException e) {
                titulo = "error"; // El fallecido o el tipo de servicio no existen
                mensaje = "servicio.error02";
                log.error("" + e);
            }
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(mensaje, null, LocaleContextHolder.getLocale()));
        return "redirect:/servicio/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idServicio, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            servicioService.delete(idServicio);
        } catch (IllegalArgumentException e) {
            titulo = "error"; // Captura la excepción de argumento inválido para el mensaje de "no existe"
            detalle = "servicio.error03";
        } catch (IllegalStateException e) {
            titulo = "error"; // Captura la excepción de estado ilegal para el mensaje de "datos asociados"
            detalle = "servicio.error04";
        } catch (Exception e) {
            titulo = "error";  // Captura cualquier otra excepción inesperada
            detalle = "servicio.error02";
            log.error("" + e);
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(detalle, null, LocaleContextHolder.getLocale()));
        return "redirect:/servicio/listado";
    }
}
