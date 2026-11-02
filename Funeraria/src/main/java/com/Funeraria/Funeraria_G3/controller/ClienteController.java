package com.Funeraria.Funeraria_G3.controller;

import com.Funeraria.Funeraria_G3.domain.Cliente;
import com.Funeraria.Funeraria_G3.service.ClienteService;
import com.Funeraria.Funeraria_G3.service.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteService clienteService;
    private final MessageSource messageSource;

    // Muestra el listado; si viene texto en "buscar" se filtra (HU-02)
    @GetMapping("/listado")
    public String listado(@RequestParam(required = false) String buscar, Model model) {
        var clientes = clienteService.buscar(buscar);
        model.addAttribute("clientes", clientes);
        model.addAttribute("totalClientes", clientes.size());
        model.addAttribute("buscar", buscar);
        return "cliente/listado";
    }

    // Registra (HU-01) o actualiza (HU-03) según el valor de "editar"
    @PostMapping("/guardar")
    public String guardar(Cliente cliente,
            @RequestParam(defaultValue = "false") boolean editar,
            RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String mensaje = editar ? "mensaje.actualizado" : "cliente.agregado";
        try {
            if (editar) {
                clienteService.actualizar(cliente);
            } else {
                clienteService.registrar(cliente);
            }
        } catch (ReglaNegocioException e) {
            titulo = "error"; // Cédula repetida o faltan datos obligatorios
            mensaje = e.getClave();
        } catch (IllegalArgumentException e) {
            titulo = "error"; // El cliente a modificar no existe
            mensaje = "cliente.error03";
        } catch (DataIntegrityViolationException e) {
            titulo = "error"; // Error de la base de datos
            mensaje = "cliente.error04";
            log.error("" + e);
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(mensaje, null, LocaleContextHolder.getLocale()));
        return "redirect:/cliente/listado";
    }
}
