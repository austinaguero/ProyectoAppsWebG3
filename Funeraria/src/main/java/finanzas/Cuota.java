
package finanzas;

import java.time.LocalDate;

public class Cuota {

    private int numero;
    private double monto;
    private double montoPagado;
    private LocalDate fechaVencimiento;

    public Cuota(int numero, double monto,
            LocalDate fechaVencimiento) {

        this.numero = numero;
        this.monto = monto;
        this.fechaVencimiento = fechaVencimiento;
        this.montoPagado = 0;
    }

    public int getNumero() {
        return numero;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public double getSaldoPendiente() {
        return monto - montoPagado;
    }

    public String getEstado() {

        if (getSaldoPendiente() <= 0.001) {
            return "Pagado";
        }

        if (fechaVencimiento.isBefore(LocalDate.now())) {
            return "En mora";
        }

        return "Al día";
    }

    public void registrarAbono(double cantidad) {
        montoPagado += cantidad;
    }

    public double getMonto() {
        return monto;
    }

    public double getMontoPagado() {
        return montoPagado;
    }
}
