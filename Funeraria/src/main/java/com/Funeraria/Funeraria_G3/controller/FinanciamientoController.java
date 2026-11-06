package com.Funeraria.Funeraria_G3.controller;

import com.Funeraria.Funeraria_G3.domain.Financiamiento;
import com.Funeraria.Funeraria_G3.service.ClienteService;
import com.Funeraria.Funeraria_G3.service.FinanciamientoService;
import com.Funeraria.Funeraria_G3.service.PlanPrevisionService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// HU-10: venta de planes de previsión (menú «Ventas»)
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/financiamiento")
public class FinanciamientoController {

    private final FinanciamientoService financiamientoService;
    private final ClienteService clienteService;
    private final PlanPrevisionService planPrevisionService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(Model model) {
        var financiamientos = financiamientoService.getFinanciamientos();
        model.addAttribute("financiamientos", financiamientos);
        model.addAttribute("totalFinanciamientos", financiamientos.size());
        model.addAttribute("clientes", clienteService.getClientes(true));
        model.addAttribute("planes", planPrevisionService.getPlanes(true));
        return "financiamiento/listado";
    }

    // HU-10: consulta SQL de contratos por número, cédula o nombre del cliente
    @PostMapping("/consultaSQL")
    public String consultaSQL(@RequestParam() String buscar, Model model) {
        var financiamientos = financiamientoService.consultaSQL(buscar);
        model.addAttribute("financiamientos", financiamientos);
        model.addAttribute("totalFinanciamientos", financiamientos.size());
        model.addAttribute("clientes", clienteService.getClientes(true));
        model.addAttribute("planes", planPrevisionService.getPlanes(true));
        model.addAttribute("buscar", buscar);
        return "financiamiento/listado";
    }

    // HU-10: registra el contrato y su plan de pagos.
    // El primer vencimiento llega aparte, como en Tienda la imagen (imagenFile).
    @PostMapping("/guardar")
    public String guardar(@Valid Financiamiento financiamiento, BindingResult bindingResult,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate primerVencimiento,
            RedirectAttributes redirectAttributes) {
        // Desde el formulario llegan solo los ids de los selects
        boolean faltaCliente = financiamiento.getCliente() == null || financiamiento.getCliente().getCedulaCliente() == null
                || financiamiento.getCliente().getCedulaCliente().isBlank();
        boolean faltaPlan = financiamiento.getPlan() == null || financiamiento.getPlan().getIdPlan() == null;
        String titulo = "todoOk";
        String mensaje = "financiamiento.agregado";
        Object[] datos = null;
        if (bindingResult.hasErrors() || faltaCliente || faltaPlan || primerVencimiento == null) {
            titulo = "error"; // Falta el cliente, el plan, el primer vencimiento o las cuotas no están entre 1 y 60
            mensaje = "financiamiento.error01";
        } else if (primerVencimiento.isBefore(LocalDate.now())) {
            titulo = "error"; // El primer vencimiento no puede quedar en el pasado
            mensaje = "financiamiento.error05";
        } else {
            try {
                financiamientoService.registrar(financiamiento, primerVencimiento);
                datos = new Object[]{financiamiento.getIdFinanciamiento(), financiamiento.getCantidadCuotas()};
            } catch (IllegalArgumentException e) {
                titulo = "error"; // El cliente no está registrado o el plan no existe (HU-10 precondición)
                mensaje = "financiamiento.error03";
            } catch (IllegalStateException e) {
                titulo = "error"; // El plan no tiene servicios con costo
                mensaje = "financiamiento.error04";
            } catch (DataIntegrityViolationException e) {
                titulo = "error"; // Error de la base de datos
                mensaje = "financiamiento.error02";
                log.error("" + e);
            }
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(mensaje, datos, Locale.getDefault()));
        return "redirect:/financiamiento/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idFinanciamiento, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            financiamientoService.delete(idFinanciamiento);
        } catch (IllegalArgumentException e) {
            titulo = "error"; // Captura la excepción de argumento inválido para el mensaje de "no existe"
            detalle = "financiamiento.error06";
        } catch (IllegalStateException e) {
            titulo = "error"; // Captura la excepción de estado ilegal para el mensaje de "datos asociados"
            detalle = "financiamiento.error07";
        } catch (Exception e) {
            titulo = "error";  // Captura cualquier otra excepción inesperada
            detalle = "financiamiento.error02";
            log.error("" + e);
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/financiamiento/listado";
    }
}
