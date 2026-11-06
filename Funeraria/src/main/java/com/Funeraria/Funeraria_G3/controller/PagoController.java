package com.Funeraria.Funeraria_G3.controller;

import com.Funeraria.Funeraria_G3.domain.Pago;
import com.Funeraria.Funeraria_G3.service.MetodoPagoService;
import com.Funeraria.Funeraria_G3.service.PagoService;
import jakarta.validation.Valid;
import java.time.LocalDate;
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

// HU-11 y HU-12: estado de cuenta por contrato y registro de pagos de sus cuotas (menú «Pagos»)
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/pago")
public class PagoController {

    private final PagoService pagoService;
    private final MetodoPagoService metodoPagoService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(Model model) {
        var financiamientos = pagoService.getFinanciamientos();
        model.addAttribute("financiamientos", financiamientos);
        model.addAttribute("totalFinanciamientos", financiamientos.size());
        model.addAttribute("metodosPago", metodoPagoService.getMetodosPago(true));
        // Con «hoy» la vista distingue «Al día» de «En mora» y con la tasa muestra el recargo
        model.addAttribute("hoy", LocalDate.now());
        model.addAttribute("tasaRecargo", pagoService.getTasaRecargo());
        return "pago/listado";
    }

    // HU-11: consulta SQL de contratos por número, cédula o nombre del cliente y por situación
    @PostMapping("/consultaSQL")
    public String consultaSQL(@RequestParam(defaultValue = "") String buscar,
            @RequestParam(defaultValue = "") String situacion, Model model) {
        var financiamientos = pagoService.consultaSQL(buscar, situacion);
        model.addAttribute("financiamientos", financiamientos);
        model.addAttribute("totalFinanciamientos", financiamientos.size());
        model.addAttribute("metodosPago", metodoPagoService.getMetodosPago(true));
        model.addAttribute("hoy", LocalDate.now());
        model.addAttribute("tasaRecargo", pagoService.getTasaRecargo());
        model.addAttribute("buscar", buscar);
        model.addAttribute("situacion", situacion);
        return "pago/listado";
    }

    // HU-12: registra el pago de una o varias cuotas seguidas y entrega el número de comprobante.
    // La cantidad de cuotas llega aparte, como en Tienda la imagen (imagenFile).
    @PostMapping("/guardar")
    public String guardar(@Valid Pago pago, BindingResult bindingResult,
            @RequestParam(defaultValue = "1") int cantidad,
            RedirectAttributes redirectAttributes) {
        // Desde el formulario llegan solo los ids de la cuota y del método de pago
        boolean faltaCuota = pago.getCuota() == null || pago.getCuota().getIdCuota() == null;
        boolean faltaMetodo = pago.getMetodoPago() == null || pago.getMetodoPago().getIdMetodoPago() == null;
        var cuota = faltaCuota ? null : pagoService.getCuota(pago.getCuota().getIdCuota()).orElse(null);
        String titulo = "todoOk";
        String mensaje = "pago.agregado";
        Object[] datos = null;
        if (bindingResult.hasErrors() || faltaCuota || faltaMetodo) {
            titulo = "error"; // Monto vacío o en cero, o no se escogió el método de pago (HU-12 criterio 2)
            mensaje = "pago.error01";
        } else if (cuota == null) {
            titulo = "error"; // La cuota no existe
            mensaje = "pago.error03";
        } else if ("Pagada".equals(cuota.getEstado().getNombreEstado())) {
            titulo = "error"; // La cuota ya se pagó
            mensaje = "pago.error04";
        } else if (cantidad < 1 || cantidad > pagoService.getCuotasPendientes(cuota).size()) {
            titulo = "error"; // Se pidió pagar más cuotas de las que quedan pendientes
            mensaje = "pago.error07";
            datos = new Object[]{pagoService.getCuotasPendientes(cuota).size()};
        } else if (pago.getMontoPagado().compareTo(pagoService.getTotalAPagar(cuota, cantidad)) != 0) {
            titulo = "error"; // El pago debe ser por el monto exacto de las cuotas más el recargo de las vencidas
            mensaje = "pago.error05";
            // Mismo formato de montos que el listado (1,234.56)
            datos = new Object[]{String.format(Locale.US, "%,.2f", pagoService.getTotalAPagar(cuota, cantidad))};
        } else if (!pagoService.getCuotasEnMora(cuota).isEmpty()) {
            titulo = "error"; // Regla de prelación: hay una cuota anterior vencida sin pagar (HU-12)
            mensaje = "pago.error06";
            datos = new Object[]{pagoService.getCuotasEnMora(cuota).get(0).getNumeroCuota()};
        } else {
            try {
                pagoService.pagar(pago, cantidad);
                if (cantidad > 1) {
                    mensaje = "pago.agregadoVarias";
                    datos = new Object[]{pago.getNumComprobante(), cantidad, cuota.getNumeroCuota(),
                        cuota.getNumeroCuota() + cantidad - 1, cuota.getFinanciamiento().getIdFinanciamiento()};
                } else {
                    datos = new Object[]{pago.getNumComprobante(), cuota.getNumeroCuota(),
                        cuota.getFinanciamiento().getIdFinanciamiento()};
                }
            } catch (IllegalArgumentException e) {
                titulo = "error"; // La cuota o el método de pago no existen
                mensaje = "pago.error03";
            } catch (IllegalStateException e) {
                titulo = "error"; // La cuota ya se pagó o la cantidad de cuotas no es válida
                mensaje = "pago.error04";
            } catch (DataIntegrityViolationException e) {
                titulo = "error"; // Error de la base de datos
                mensaje = "pago.error02";
                log.error("" + e);
            }
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(mensaje, datos, Locale.getDefault()));
        return "redirect:/pago/listado";
    }
}
