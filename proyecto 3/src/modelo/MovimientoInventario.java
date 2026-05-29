package modelo;

import java.time.LocalDateTime;

/**
 * Registra cada movimiento de inventario.
 * Auditoría completa de qué pasó con cada juego.
 */
public class MovimientoInventario {
    public enum TipoMovimiento {
        ENTRADA, SALIDA_VENTA, SALIDA_PRESTAMO, REPARACION, DESAPARICION, TRANSFERENCIA
    }

    private String movimientoId;
    private TipoMovimiento tipo;
    private JuegoDeMesa juego;
    private LocalDateTime fecha;
    private int cantidad;
    private Usuario usuario;
    private String motivo;

    public MovimientoInventario(String movimientoId, TipoMovimiento tipo, JuegoDeMesa juego, int cantidad,
            Usuario usuario, String motivo) {
        this.movimientoId = movimientoId;
        this.tipo = tipo;
        this.juego = juego;
        this.fecha = LocalDateTime.now();
        this.cantidad = cantidad;
        this.usuario = usuario;
        this.motivo = motivo;
    }

    // Getters
    public String getMovimientoId() {
        return movimientoId;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public JuegoDeMesa getJuego() {
        return juego;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public int getCantidad() {
        return cantidad;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getMotivo() {
        return motivo;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s x%d por %s (%s)", fecha.toLocalDate(), tipo, juego.getNombre(),
                cantidad, usuario.getLogin(), motivo);
    }
}
