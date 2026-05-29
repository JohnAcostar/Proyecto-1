package modelo;

/**
 * Detalle individual de una venta de café.
 * Permite desglosar qué productos del café se vendieron.
 */
public class DetalleVentaCafe {
    private String detalleId;
    private ProductoMenu producto;
    private int cantidad;
    private double precioPorUnidad;
    private double subtotal;

    public DetalleVentaCafe(String detalleId, ProductoMenu producto, int cantidad, double precioPorUnidad) {
        this.detalleId = detalleId;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioPorUnidad = precioPorUnidad;
        calcularSubtotal();
    }

    public void calcularSubtotal() {
        this.subtotal = cantidad * precioPorUnidad;
    }

    // Getters
    public String getDetalleId() {
        return detalleId;
    }

    public ProductoMenu getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioPorUnidad() {
        return precioPorUnidad;
    }

    public double getSubtotal() {
        return subtotal;
    }

    @Override
    public String toString() {
        return String.format("%s x%d @ $%.0f = $%.0f", producto.getNombre(), cantidad, precioPorUnidad,
                subtotal);
    }
}
