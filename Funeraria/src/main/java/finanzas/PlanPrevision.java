
package finanzas;

public class PlanPrevision {

    private int idPlan;
    private String nombre;
    private String servicios;
    private double precio;

    public PlanPrevision(int idPlan, String nombre,
            String servicios, double precio) {

        this.idPlan = idPlan;
        this.nombre = nombre;
        this.servicios = servicios;
        this.precio = precio;
    }

    public int getIdPlan() {
        return idPlan;
    }

    public String getNombre() {
        return nombre;
    }

    public String getServicios() {
        return servicios;
    }

    public double getPrecio() {
        return precio;
    }
}
