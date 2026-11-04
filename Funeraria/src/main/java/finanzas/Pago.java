
package finanzas;

import java.time.LocalDate;

public class Pago {

    private int numeroRecibo;
    private int numeroContrato;
    private int numeroCuota;
    private double monto;
    private String metodoPago;
    private LocalDate fecha;

    public Pago(int numeroRecibo, int numeroContrato,
            int numeroCuota, double monto,
            String metodoPago) {

        this.numeroRecibo = numeroRecibo;
        this.numeroContrato = numeroContrato;
        this.numeroCuota = numeroCuota;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.fecha = LocalDate.now();
    }

    public String generarRecibo() {

        return "RECIBO #" + numeroRecibo
                + "\nContrato: " + numeroContrato
                + "\nCuota: " + numeroCuota
                + "\nMonto pagado: " + monto
                + "\nMétodo: " + metodoPago
                + "\nFecha: " + fecha;
    }
}
