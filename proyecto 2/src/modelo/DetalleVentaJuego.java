package modelo;

/**
 * Detalle individual de una venta de juegos.
 * Permite desglosar qué juegos se vendieron y a qué precio.
 */
public class DetalleVentaJuego {
    private String detalleId;
    private JuegoDeMesa juego;
    private int cantidad;
    private double precioPorUnidad;
    private double subtotal;
    private double descuentoAplicado;

    public DetalleVentaJuego(String detalleId, JuegoDeMesa juego, int cantidad, double precioPorUnidad) {
        this.detalleId = detalleId;
        this.juego = juego;
        this.cantidad = cantidad;
        this.precioPorUnidad = precioPorUnidad;
        this.descuentoAplicado = 0.0;
        calcularSubtotal();
    }

    public void calcularSubtotal() {
        this.subtotal = (cantidad * precioPorUnidad) - descuentoAplicado;
    }

    public void aplicarDescuento(double descuento) {
        if (descuento >= 0 && descuento <= subtotal) {
            this.descuentoAplicado = descuento;
            calcularSubtotal();
        }
    }

    public double getSubtotalOriginal() {
        return cantidad * precioPorUnidad;
    }

    // Getters
    public String getDetalleId() {
        return detalleId;
    }

    public JuegoDeMesa getJuego() {
        return juego;
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

    public double getDescuentoAplicado() {
        return descuentoAplicado;
    }

    @Override
    public String toString() {
        return String.format("%s x%d @ $%.0f = $%.0f", juego.getNombre(), cantidad, precioPorUnidad,
                subtotal);
    }
}
