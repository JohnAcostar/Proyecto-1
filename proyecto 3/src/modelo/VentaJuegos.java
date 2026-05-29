package modelo;

public class VentaJuegos extends Venta {
    private static final double IVA = 0.19;
    private double descuentoAplicado;
    private double base;

    public VentaJuegos(String ventaId, double base) {
        super(ventaId, RubroVenta.JUEGO);
        this.base = base;
    }

    public VentaJuegos(String ventaId, double base, Usuario usuario) {
        super(ventaId, RubroVenta.JUEGO, usuario);
        this.base = base;
    }

    public void aplicarDescuento(double porcentaje) {
        this.descuentoAplicado = porcentaje;
    }

    @Override
    public double calcularSubtotal() {
        subtotal = base * (1 - descuentoAplicado);
        return subtotal;
    }

    @Override
    public double calcularTotal() {
        calcularSubtotal();
        impuesto = subtotal * IVA;
        total = subtotal + impuesto;
        return total;
    }

    public double getDescuentoAplicado() {
        return descuentoAplicado;
    }

    public double getBase() {
        return base;
    }

    @Override
    public int calcularPuntos() {
        return 300;
    }
}
