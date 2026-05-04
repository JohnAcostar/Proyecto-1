package modelo;

import java.time.LocalDateTime;

public abstract class Venta {
    private String ventaId;
    private LocalDateTime fecha;
    protected double subtotal;
    protected double impuesto;
    protected double total;
    private RubroVenta rubro;
    private Usuario usuario;

    protected Venta(String ventaId, RubroVenta rubro) {
        this.ventaId = ventaId;
        this.rubro = rubro;
        this.fecha = LocalDateTime.now();
    }

    protected Venta(String ventaId, RubroVenta rubro, Usuario usuario) {
        this(ventaId, rubro);
        this.usuario = usuario;
    }

    public abstract double calcularSubtotal();

    public abstract double calcularTotal();

    public int calcularPuntos() {
        return (int) Math.floor(total * 0.01);
    }

    public String getVentaId() {
        return ventaId;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public RubroVenta getRubro() {
        return rubro;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getImpuesto() {
        return impuesto;
    }

    public double getTotal() {
        return total;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
