package com.Funeraria.Funeraria_G3.service;

import com.Funeraria.Funeraria_G3.domain.Cuota;
import com.Funeraria.Funeraria_G3.domain.Estado;
import com.Funeraria.Funeraria_G3.domain.Financiamiento;
import com.Funeraria.Funeraria_G3.repository.ClienteRepository;
import com.Funeraria.Funeraria_G3.repository.CuotaRepository;
import com.Funeraria.Funeraria_G3.repository.EstadoRepository;
import com.Funeraria.Funeraria_G3.repository.FinanciamientoRepository;
import com.Funeraria.Funeraria_G3.repository.PlanPrevisionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FinanciamientoService {

    private static final String ACTIVO = "Activo";
    private static final String PENDIENTE = "Pendiente";

    // Los repositorios son final para asegurar la inmutabilidad
    private final FinanciamientoRepository financiamientoRepository;
    private final CuotaRepository cuotaRepository;
    private final ClienteRepository clienteRepository;
    private final PlanPrevisionRepository planPrevisionRepository;
    private final EstadoRepository estadoRepository;

    @Transactional(readOnly = true)
    public List<Financiamiento> getFinanciamientos() {
        return financiamientoRepository.findAllByOrderByIdFinanciamientoDesc();
    }

    // HU-10: consulta SQL por número de contrato, cédula o nombre del cliente
    @Transactional(readOnly = true)
    public List<Financiamiento> consultaSQL(String texto) {
        return financiamientoRepository.consultaSQL(texto.trim());
    }

    // HU-10: registra el contrato del plan y genera su plan de pagos. El precio es la suma de los
    // costos de los servicios del plan y se reparte en cuotas mensuales iguales (en céntimos; los
    // céntimos que sobran se suman a las primeras cuotas para que el total cuadre exacto)
    @Transactional
    public void registrar(Financiamiento financiamiento, LocalDate primerVencimiento) {
        var cliente = clienteRepository.findById(financiamiento.getCliente().getCedulaCliente().trim())
                .orElseThrow(() -> new IllegalArgumentException("El cliente no está registrado."));
        var plan = planPrevisionRepository.findById(financiamiento.getPlan().getIdPlan())
                .orElseThrow(() -> new IllegalArgumentException("El plan de previsión no existe."));
        BigDecimal precio = planPrevisionRepository.consultaSQLPrecio(plan.getIdPlan());
        if (precio == null || precio.signum() <= 0) {
            throw new IllegalStateException("El plan de previsión no tiene servicios con costo.");
        }

        financiamiento.setCliente(cliente);
        financiamiento.setPlan(plan);
        financiamiento.setMontoTotal(precio);
        financiamiento.setSaldoPendiente(precio);
        financiamiento.setFechaInicio(LocalDate.now());
        financiamiento.setEstado(getEstado(ACTIVO));
        financiamientoRepository.saveAndFlush(financiamiento);

        // Plan de pagos: cuotas mensuales a partir del primer vencimiento, en estado «Pendiente»
        Estado pendiente = getEstado(PENDIENTE);
        int cantidad = financiamiento.getCantidadCuotas();
        long centimos = precio.movePointRight(2).longValueExact();
        long base = centimos / cantidad;
        long sobrante = centimos % cantidad;
        for (int i = 0; i < cantidad; i++) {
            long monto = i < sobrante ? base + 1 : base;
            Cuota cuota = new Cuota();
            cuota.setFinanciamiento(financiamiento);
            cuota.setNumeroCuota(i + 1);
            cuota.setMonto(BigDecimal.valueOf(monto, 2));
            cuota.setFechaVencimiento(primerVencimiento.plusMonths(i));
            cuota.setEstado(pendiente);
            cuotaRepository.save(cuota);
        }
    }

    // Un contrato solo se elimina si ninguna de sus cuotas tiene pagos
    @Transactional
    public void delete(Integer idFinanciamiento) {
        var financiamiento = financiamientoRepository.findById(idFinanciamiento)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El contrato N.º " + idFinanciamiento + " no existe."));
        for (Cuota cuota : financiamiento.getCuotas()) {
            if (cuota.getPagos() != null && !cuota.getPagos().isEmpty()) {
                throw new IllegalStateException("El contrato tiene pagos registrados.");
            }
        }
        cuotaRepository.deleteAll(financiamiento.getCuotas());
        financiamientoRepository.delete(financiamiento);
    }

    private Estado getEstado(String nombre) {
        return estadoRepository.findByNombreEstado(nombre)
                .orElseThrow(() -> new IllegalStateException("No existe el estado " + nombre + "."));
    }
}
