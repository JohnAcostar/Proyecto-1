package modelo;

/**
 * Interfaz que define los productos servibles en el café.
 * Abstracción para Bebida, Pastelería y ProductoCafe legacy.
 */
public abstract class ProductoMenu {
    protected String productoId;
    protected String nombre;
    protected double precioBase;
    protected boolean disponible;

    public ProductoMenu(String productoId, String nombre, double precioBase) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.precioBase = precioBase;
        this.disponible = true;
    }

    public abstract double calcularPrecioFinal();

    public abstract String getTipo();

    // Getters
    public String getProductoId() {
        return productoId;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public boolean isDisponible() {
        return disponible;
    }

    // Setters
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }
}
