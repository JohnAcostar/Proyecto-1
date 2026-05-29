package modelo;

public class VentaCafe extends Venta {
    private static final double IMPUESTO_CONSUMO = 0.08;
    private double base;
    private double porcentajePropina;

    public VentaCafe(String ventaId, double base, double porcentajePropina) {
        super(ventaId, RubroVenta.CAFE);
        this.base = base;
        this.porcentajePropina = porcentajePropina;
    }

    public VentaCafe(String ventaId, double base, double porcentajePropina, Usuario usuario) {
        super(ventaId, RubroVenta.CAFE, usuario);
        this.base = base;
        this.porcentajePropina = porcentajePropina;
    }

    @Override
    public double calcularSubtotal() {
        subtotal = base;
        return subtotal;
    }

    @Override
    public double calcularTotal() {
        calcularSubtotal();
        impuesto = subtotal * IMPUESTO_CONSUMO;
        double propina = subtotal * porcentajePropina;
        total = subtotal + impuesto + propina;
        return total;
    }

    public double getBase() {
        return base;
    }

    public double getPorcentajePropina() {
        return porcentajePropina;
    }

    @Override
    public int calcularPuntos() {
        return 100;
    }
}
