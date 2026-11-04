
package finanzas;

import java.time.LocalDate;
import java.util.ArrayList;

public class GestionFinanciera {

    private ArrayList<Contrato> contratos;
    private ArrayList<Pago> pagos;
    private int siguienteContrato = 1;
    private int siguienteRecibo = 1;

    public GestionFinanciera() {
        contratos = new ArrayList<>();
        pagos = new ArrayList<>();
    }

    // HU-10: Registrar un plan de previsión funeraria

    public String registrarContrato(String cedula,
            PlanPrevision plan, int cantidadCuotas,
            LocalDate primerVencimiento) {

        if (cedula == null || cedula.trim().isEmpty()
                || plan == null || plan.getPrecio() <= 0
                || cantidadCuotas <= 0
                || primerVencimiento == null) {

            return "Error: datos del contrato inválidos";
        }

        Contrato contrato = new Contrato(
                siguienteContrato, cedula, plan);

        
        double precioCentimos =
                Math.round(plan.getPrecio() * 100);

        long totalCentimos = (long) precioCentimos;
        long cuotaBase = totalCentimos / cantidadCuotas;
        long resto = totalCentimos % cantidadCuotas;

        for (int i = 1; i <= cantidadCuotas; i++) {

            long centimos = cuotaBase;

           
            if (i <= resto) {
                centimos++;
            }

            double montoCuota = centimos / 100.0;

            LocalDate fecha = primerVencimiento
                    .plusMonths(i - 1);

            Cuota cuota = new Cuota(
                    i, montoCuota, fecha);

            contrato.agregarCuota(cuota);
        }

        contratos.add(contrato);
        siguienteContrato++;

        return "Contrato registrado correctamente. Número: "
                + contrato.getNumeroContrato();
    }

   

    public Contrato buscarContrato(int numero) {

        for (Contrato contrato : contratos) {

            if (contrato.getNumeroContrato() == numero) {
                return contrato;
            }
        }

        return null;
    }

    

    public ArrayList<Contrato> buscarPorCedula(
            String cedula) {

        ArrayList<Contrato> resultado = new ArrayList<>();

        for (Contrato contrato : contratos) {

            if (contrato.getCedulaCliente().equals(cedula)) {
                resultado.add(contrato);
            }
        }

        return resultado;
    }

    

    public void consultarCuotas(int numeroContrato) {

        Contrato contrato = buscarContrato(numeroContrato);

        if (contrato == null) {
            System.out.println("Contrato no encontrado");
            return;
        }

        System.out.println("Contrato: " + numeroContrato);
        System.out.println("Plan: "
                + contrato.getPlan().getNombre());

        for (Cuota cuota : contrato.getCuotas()) {

            System.out.println(
                    "Cuota: " + cuota.getNumero()
                    + " | Vence: " + cuota.getFechaVencimiento()
                    + " | Estado: " + cuota.getEstado()
                    + " | Pendiente: "
                    + cuota.getSaldoPendiente());
        }

        System.out.println("Saldo total: "
                + contrato.calcularSaldoPendiente());
    }

    

    public ArrayList<Cuota> filtrarCuotas(
            int numeroContrato, String estado) {

        ArrayList<Cuota> resultado = new ArrayList<>();

        Contrato contrato = buscarContrato(numeroContrato);

        if (contrato == null || estado == null) {
            return resultado;
        }

        for (Cuota cuota : contrato.getCuotas()) {

            if (cuota.getEstado().equalsIgnoreCase(estado)) {
                resultado.add(cuota);
            }
        }

        return resultado;
    }

    

    public String registrarPago(int numeroContrato,
            int numeroCuota, double monto,
            String metodoPago) {

        Contrato contrato = buscarContrato(numeroContrato);

        if (contrato == null) {
            return "Error: contrato no encontrado";
        }

        if (!contrato.getEstado().equals("Activo")) {
            return "Error: el contrato no está activo";
        }

        if (monto <= 0 || Double.isNaN(monto)
                || Double.isInfinite(monto)) {
            return "Error: el monto debe ser mayor a cero";
        }

        if (metodoPago == null
                || metodoPago.trim().isEmpty()) {
            return "Error: indique un método de pago";
        }

        Cuota seleccionada = null;

        for (Cuota cuota : contrato.getCuotas()) {

            if (cuota.getNumero() == numeroCuota) {
                seleccionada = cuota;
                break;
            }
        }

        if (seleccionada == null) {
            return "Error: cuota no encontrada";
        }

        if (seleccionada.getSaldoPendiente() <= 0.001) {
            return "Error: esta cuota ya está pagada";
        }

        if (monto > seleccionada.getSaldoPendiente()
                + 0.001) {
            return "Error: el pago supera el saldo pendiente";
        }

        
        for (Cuota cuota : contrato.getCuotas()) {

            if (cuota.getNumero() < numeroCuota
                    && cuota.getFechaVencimiento()
                            .isBefore(LocalDate.now())
                    && cuota.getSaldoPendiente() > 0.001) {

                return "Error: debe pagar primero "
                        + "la cuota vencida #"
                        + cuota.getNumero();
            }
        }

        
        seleccionada.registrarAbono(monto);

        Pago pago = new Pago(
                siguienteRecibo,
                numeroContrato,
                numeroCuota,
                monto,
                metodoPago);

        pagos.add(pago);
        siguienteRecibo++;

        return "Pago registrado correctamente\n"
                + pago.generarRecibo()
                + "\nNuevo saldo del contrato: "
                + contrato.calcularSaldoPendiente();
    }
}
