package modelo;

public class ProductoCafe {
    private String id;
    private String nombre;
    private double precioBase;

    public ProductoCafe(String id, String nombre, double precioBase) {
        this.id = id;
        this.nombre = nombre;
        this.precioBase = precioBase;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    @Override
    public String toString() {
        return String.format("%-3s | %-20s | $%.0f", id, nombre, precioBase);
    }
}
