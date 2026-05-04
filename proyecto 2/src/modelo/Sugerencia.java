package modelo;

public class Sugerencia {
    private String nombrePropuesto;
    private String descripcion;
    private boolean aprobada;

    public Sugerencia(String nombrePropuesto, String descripcion) {
        this.nombrePropuesto = nombrePropuesto;
        this.descripcion = descripcion;
    }

    public Sugerencia(String nombrePropuesto, String descripcion, boolean aprobada) {
        this.nombrePropuesto = nombrePropuesto;
        this.descripcion = descripcion;
        this.aprobada = aprobada;
    }

    public void aprobar() {
        this.aprobada = true;
    }

    public void rechazar() {
        this.aprobada = false;
    }

    public String getNombrePropuesto() {
        return nombrePropuesto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isAprobada() {
        return aprobada;
    }
}
