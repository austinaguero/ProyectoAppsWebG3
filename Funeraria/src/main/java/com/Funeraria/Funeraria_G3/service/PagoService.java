package com.Funeraria.Funeraria_G3.service;

import com.Funeraria.Funeraria_G3.domain.Cuota;
import com.Funeraria.Funeraria_G3.domain.Estado;
import com.Funeraria.Funeraria_G3.domain.Financiamiento;
import com.Funeraria.Funeraria_G3.domain.Pago;
import com.Funeraria.Funeraria_G3.repository.CuotaRepository;
import com.Funeraria.Funeraria_G3.repository.EstadoRepository;
import com.Funeraria.Funeraria_G3.repository.FinanciamientoRepository;
import com.Funeraria.Funeraria_G3.repository.MetodoPagoRepository;
import com.Funeraria.Funeraria_G3.repository.PagoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PagoService {

    private static final String ACTIVO = "Activo";
    private static final String PAGADA = "Pagada";

    // HU-12: porcentaje de recargo sobre el monto de una cuota que se paga vencida
    @Value("${pago.recargo.porcentaje:5}")
    private BigDecimal porcentajeRecargo;

    // Los repositorios son final para asegurar la inmutabilidad
    private final PagoRepository pagoRepository;
    private final CuotaRepository cuotaRepository;
    private final FinanciamientoRepository financiamientoRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final EstadoRepository estadoRepository;

    // HU-11: estado de cuenta por contrato (cada contrato trae sus cuotas)
    @Transactional(readOnly = true)
    public List<Financiamiento> getFinanciamientos() {
        return financiamientoRepository.findAllByOrderByIdFinanciamientoDesc();
    }

    // HU-11: consulta SQL de contratos por número o cliente y por situación (alDia, mora, pagado)
    @Transactional(readOnly = true)
    public List<Financiamiento> consultaSQL(String texto, String situacion) {
        return financiamientoRepository.consultaSQLEstadoCuenta(texto.trim(), situacion);
    }

    @Transactional(readOnly = true)
    public Optional<Cuota> getCuota(Integer idCuota) {
        return cuotaRepository.findById(idCuota);
    }

    // HU-12 regla de prelación: cuotas anteriores del mismo contrato que están vencidas sin pagar
    @Transactional(readOnly = true)
    public List<Cuota> getCuotasEnMora(Cuota cuota) {
        return cuotaRepository.consultaSQLMora(cuota.getFinanciamiento().getIdFinanciamiento(),
                cuota.getNumeroCuota());
    }

    // HU-12: tasa del recargo (porcentaje / 100); la vista la usa para mostrar el recargo de las cuotas en mora
    public BigDecimal getTasaRecargo() {
        return porcentajeRecargo.movePointLeft(2);
    }

    // HU-12: recargo de una cuota sin pagar que ya venció; 0 si todavía no vence
    public BigDecimal getRecargo(Cuota cuota) {
        if (!cuota.getFechaVencimiento().isBefore(LocalDate.now())) {
            return BigDecimal.ZERO.setScale(2);
        }
        // Mismo redondeo que el formato de números de la vista
        return cuota.getMonto().multiply(getTasaRecargo()).setScale(2, RoundingMode.HALF_EVEN);
    }

    // HU-12: cuotas sin pagar del contrato desde la cuota escogida, en orden; son las que se pueden pagar juntas
    @Transactional(readOnly = true)
    public List<Cuota> getCuotasPendientes(Cuota cuota) {
        return cuotaRepository.consultaSQLPendientes(cuota.getFinanciamiento().getIdFinanciamiento(),
                cuota.getNumeroCuota());
    }

    // HU-12: total a pagar por las primeras «cantidad» cuotas pendientes desde la cuota escogida
    // (monto de cada cuota más su recargo si está vencida)
    @Transactional(readOnly = true)
    public BigDecimal getTotalAPagar(Cuota cuota, int cantidad) {
        var pendientes = getCuotasPendientes(cuota);
        BigDecimal total = BigDecimal.ZERO.setScale(2);
        for (int i = 0; i < cantidad && i < pendientes.size(); i++) {
            total = total.add(pendientes.get(i).getMonto()).add(getRecargo(pendientes.get(i)));
        }
        return total;
    }

    // HU-12: registra el pago de «cantidad» cuotas seguidas desde la cuota escogida (el controlador ya validó
    // la cantidad, el monto y la prelación). Cada cuota queda con su propio registro de pago (monto + recargo),
    // todas con el mismo número de comprobante, y pasa a «Pagada». Al saldo solo se le rebaja el monto de las cuotas.
    @Transactional
    public void pagar(Pago pago, int cantidad) {
        var cuota = cuotaRepository.findById(pago.getCuota().getIdCuota())
                .orElseThrow(() -> new IllegalArgumentException("La cuota no existe."));
        if (PAGADA.equals(cuota.getEstado().getNombreEstado())) {
            throw new IllegalStateException("La cuota ya está pagada.");
        }
        var metodoPago = metodoPagoRepository.findById(pago.getMetodoPago().getIdMetodoPago())
                .orElseThrow(() -> new IllegalArgumentException("El método de pago no existe."));
        var pendientes = getCuotasPendientes(cuota);
        if (cantidad < 1 || cantidad > pendientes.size()) {
            throw new IllegalStateException("La cantidad de cuotas no es válida.");
        }

        Estado activo = getEstado(ACTIVO);
        Estado pagada = getEstado(PAGADA);
        var financiamiento = cuota.getFinanciamiento();
        String comprobante = null;
        for (int i = 0; i < cantidad; i++) {
            Cuota actual = pendientes.get(i);
            // El primer registro es el que llegó del formulario; los demás son nuevos
            Pago registro = i == 0 ? pago : new Pago();
            BigDecimal recargo = getRecargo(actual);
            registro.setCuota(actual);
            registro.setMetodoPago(metodoPago);
            registro.setFechaPago(LocalDate.now());
            registro.setMontoPagado(actual.getMonto().add(recargo));
            registro.setRecargo(recargo);
            registro.setEstado(activo);
            pagoRepository.saveAndFlush(registro);
            if (comprobante == null) {
                // El comprobante se arma con el id que generó la base para el primer registro
                comprobante = String.format("REC-%06d", registro.getIdPago());
            }
            registro.setNumComprobante(comprobante);
            pagoRepository.save(registro);

            actual.setEstado(pagada);
            cuotaRepository.save(actual);
            financiamiento.setSaldoPendiente(financiamiento.getSaldoPendiente().subtract(actual.getMonto()));
        }
        if (financiamiento.getSaldoPendiente().signum() <= 0) {
            financiamiento.setEstado(pagada); // Contrato cancelado por completo
        }
        financiamientoRepository.save(financiamiento);
    }

    private Estado getEstado(String nombre) {
        return estadoRepository.findByNombreEstado(nombre)
                .orElseThrow(() -> new IllegalStateException("No existe el estado " + nombre + "."));
    }
}
