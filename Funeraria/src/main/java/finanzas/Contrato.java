
package finanzas;

import java.util.ArrayList;

public class Contrato {

    private int numeroContrato;
    private String cedulaCliente;
    private PlanPrevision plan;
    private ArrayList<Cuota> cuotas;
    private String estado;

    public Contrato(int numeroContrato,
            String cedulaCliente, PlanPrevision plan) {

        this.numeroContrato = numeroContrato;
        this.cedulaCliente = cedulaCliente;
        this.plan = plan;
        this.estado = "Activo";
        this.cuotas = new ArrayList<>();
    }

    public void agregarCuota(Cuota cuota) {
        cuotas.add(cuota);
    }

    public double calcularSaldoPendiente() {

        double total = 0;

        for (Cuota cuota : cuotas) {
            total += cuota.getSaldoPendiente();
        }

        return total;
    }

    public int getNumeroContrato() {
        return numeroContrato;
    }

    public String getCedulaCliente() {
        return cedulaCliente;
    }

    public PlanPrevision getPlan() {
        return plan;
    }

    public ArrayList<Cuota> getCuotas() {
        return cuotas;
    }

    public String getEstado() {
        return estado;
    }
}
