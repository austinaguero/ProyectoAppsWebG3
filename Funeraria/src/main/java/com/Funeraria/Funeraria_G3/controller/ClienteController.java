package com.Funeraria.Funeraria_G3.controller;

import com.Funeraria.Funeraria_G3.domain.Cliente;
import com.Funeraria.Funeraria_G3.service.ClienteService;
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
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteService clienteService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(Model model) {
        var clientes = clienteService.getClientes(false);
        model.addAttribute("clientes", clientes);
        model.addAttribute("totalClientes", clientes.size());
        return "cliente/listado";
    }

    // HU-02: consulta SQL de clientes por cédula o nombre completo
    @PostMapping("/consultaSQL")
    public String consultaSQL(@RequestParam() String buscar, Model model) {
        var clientes = clienteService.consultaSQL(buscar);
        model.addAttribute("clientes", clientes);
        model.addAttribute("totalClientes", clientes.size());
        model.addAttribute("buscar", buscar);
        return "cliente/listado";
    }

    // Registra (HU-01) o actualiza (HU-03) según el valor de "editar"
    @PostMapping("/guardar")
    public String guardar(@Valid Cliente cliente, BindingResult bindingResult,
            @RequestParam(defaultValue = "false") boolean editar,
            RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String mensaje = editar ? "mensaje.actualizado" : "cliente.agregado";
        if (bindingResult.hasErrors()) {
            titulo = "error"; // Faltan la cédula, el nombre o el primer apellido
            mensaje = "cliente.error02";
        } else if (!editar && clienteService.getCliente(cliente.getCedulaCliente()).isPresent()) {
            titulo = "error"; // Ya existe un cliente con esa cédula (HU-01)
            mensaje = "cliente.error01";
        } else {
            try {
                if (editar) {
                    clienteService.actualizar(cliente);
                } else {
                    clienteService.registrar(cliente);
                }
            } catch (IllegalArgumentException e) {
                titulo = "error"; // El cliente a modificar no existe
                mensaje = "cliente.error03";
            } catch (DataIntegrityViolationException e) {
                titulo = "error"; // Error de la base de datos
                mensaje = "cliente.error04";
                log.error("" + e);
            }
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(mensaje, null, Locale.getDefault()));
        return "redirect:/cliente/listado";
    }
}
