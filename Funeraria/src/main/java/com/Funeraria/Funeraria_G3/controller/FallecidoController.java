package com.Funeraria.Funeraria_G3.controller;

import com.Funeraria.Funeraria_G3.domain.Fallecido;
import com.Funeraria.Funeraria_G3.service.ClienteService;
import com.Funeraria.Funeraria_G3.service.DireccionService;
import com.Funeraria.Funeraria_G3.service.FallecidoService;
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
@RequestMapping("/fallecido")
public class FallecidoController {

    private final FallecidoService fallecidoService;
    private final ClienteService clienteService;
    private final DireccionService direccionService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(Model model) {
        var fallecidos = fallecidoService.getFallecidos(false);
        model.addAttribute("fallecidos", fallecidos);
        model.addAttribute("totalFallecidos", fallecidos.size());
        model.addAttribute("clientes", clienteService.getClientes(true));
        // Dirección de domicilio: catálogos para los selects
        // (el sepelio y el domicilio de cada fallecido se leen de sus relaciones en la vista)
        model.addAttribute("provincias", direccionService.getProvincias());
        model.addAttribute("cantones", direccionService.getCantones());
        model.addAttribute("distritos", direccionService.getDistritos());
        return "fallecido/listado";
    }

    // HU-04: registra o modifica según el campo oculto "edicion" (la cédula es la llave).
    // La dirección de domicilio llega aparte, como en Tienda la imagen (imagenFile).
    @PostMapping("/guardar")
    public String guardar(@Valid Fallecido fallecido, BindingResult bindingResult,
            @RequestParam(defaultValue = "false") boolean edicion,
            @RequestParam(required = false) Integer idProvincia,
            @RequestParam(required = false) Integer idCanton,
            @RequestParam(required = false) Integer idDistrito,
            @RequestParam(required = false) String otrasSennas,
            RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String mensaje = edicion ? "mensaje.actualizado" : "fallecido.registrado";
        // Desde el formulario llega solo la cédula del cliente responsable
        boolean faltaCliente = fallecido.getCliente() == null || fallecido.getCliente().getCedulaCliente() == null
                || fallecido.getCliente().getCedulaCliente().isBlank();
        if (bindingResult.hasErrors() || faltaCliente) {
            titulo = "error"; // Faltan datos obligatorios o no son válidos
            mensaje = "fallecido.error01";
        } else if (fallecido.getFechaFallecimiento().isBefore(fallecido.getFechaNacimiento())) {
            titulo = "error"; // Falleció antes de nacer
            mensaje = "fallecido.error02";
        } else if (fallecido.getCedulaFallecido().trim().equals(fallecido.getCliente().getCedulaCliente().trim())) {
            titulo = "error"; // El cliente es la misma persona fallecida
            mensaje = "fallecido.error03";
        } else if (!edicion && fallecidoService.getFallecido(fallecido.getCedulaFallecido()).isPresent()) {
            titulo = "error"; // Ya existe un fallecido con esa cédula
            mensaje = "fallecido.error05";
        } else if (!direccionService.isDireccionValida(idProvincia, idCanton, idDistrito)) {
            titulo = "error"; // Falta la dirección o el distrito no es del cantón/provincia escogidos
            mensaje = "fallecido.error07";
        } else {
            try {
                fallecidoService.save(fallecido, edicion, idProvincia, idCanton, idDistrito, otrasSennas);
            } catch (IllegalArgumentException e) {
                titulo = "error"; // El cliente o el fallecido a modificar no existen
                mensaje = "fallecido.error04";
            } catch (DataIntegrityViolationException e) {
                titulo = "error"; // La base rechazó los datos
                mensaje = "fallecido.error04";
                log.error("" + e);
            }
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(mensaje, null, Locale.getDefault()));
        return "redirect:/fallecido/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam String cedulaFallecido, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            fallecidoService.delete(cedulaFallecido);
        } catch (IllegalArgumentException e) {
            titulo = "error"; // Captura la excepción de argumento inválido para el mensaje de "no existe"
            detalle = "fallecido.error04";
        } catch (IllegalStateException e) {
            titulo = "error"; // Captura la excepción de estado ilegal para el mensaje de "datos asociados"
            detalle = "fallecido.error06";
        } catch (Exception e) {
            titulo = "error";  // Captura cualquier otra excepción inesperada
            detalle = "fallecido.error06";
            log.error("" + e);
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/fallecido/listado";
    }
}
