package modelo;

import java.util.List;
import java.util.stream.Collectors;

public class Cliente extends Usuario {
    private String codigoDeDescuento;
    private int puntosDeFidelidad;

    public Cliente(String login, String password, String id, String codigoDeDescuento) {
        super(login, password, id);
        this.codigoDeDescuento = codigoDeDescuento;
        this.puntosDeFidelidad = 0;
    }

    public Reserva hacerReserva(int cantidad, boolean hayNinos, boolean hayJovenes) {
        return new Reserva(cantidad, hayNinos, hayJovenes);
    }

    public VentaCafe comprarProductosCafe(String ventaId, double base, double propinaPorcentaje) {
        VentaCafe venta = new VentaCafe(ventaId, base, propinaPorcentaje, this);
        venta.calcularSubtotal();
        venta.calcularTotal();
        this.getHistorialVentas().add(venta);
        return venta;
    }

    public VentaJuegos comprarJuegos(String ventaId, double base) {
        VentaJuegos venta = new VentaJuegos(ventaId, base, this);
        venta.calcularSubtotal();
        venta.calcularTotal();
        this.getHistorialVentas().add(venta);
        puntosDeFidelidad += venta.calcularPuntos();
        return venta;
    }

    public void devolverJuegos(Prestamo prestamo) {
        if (prestamo != null && prestamo.estaActivo()) {
            prestamo.registrarDevolucion();
        }
    }

    public void devolverJuego(Prestamo prestamo) {
        if (prestamo != null && prestamo.estaActivo()) {
            prestamo.registrarDevolucion();
        }
    }

    public void usarPuntos(int valor) {
        if (this.puntosDeFidelidad >= valor) {
            this.puntosDeFidelidad -= valor;
        }
    }

    public void acumularPuntos(int puntos) {
        this.puntosDeFidelidad += puntos;
    }

    public double getPuntosDeFidelidad() {
        return puntosDeFidelidad;
    }

    public void setPuntosDeFidelidad(int puntos) {
        this.puntosDeFidelidad = puntos;
    }

    public String getCodigoDeDescuento() {
        return codigoDeDescuento;
    }

    public List<Integer> getJuegosFavoritos() {
        return getFavoritos().stream()
                .map(JuegoDeMesa::getId)
                .collect(Collectors.toList());
    }

    public void agregarJuegoFavorito(int idJuego) {
        agregarFavorito(new JuegoDeMesa(String.valueOf(idJuego), "", 0, "", 1, 1,
                RestriccionEdad.TODAS_LAS_EDADES, CategoriaJuego.TABLERO, EstadoJuego.NUEVO, false));
    }

    // Nuevos métodos para acceso simplificado
    public int holaPuntosActuales() {
        return this.puntosDeFidelidad;
    }

    public void canjeaPuntos(int cantidad) {
        if (this.puntosDeFidelidad >= cantidad) {
            this.puntosDeFidelidad -= cantidad;
        }
    }

    public void acumulaPuntos(int cantidad) {
        this.puntosDeFidelidad += cantidad;
    }
}
