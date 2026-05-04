package service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import modelo.Administrador;
import modelo.CopiaJuego;
import modelo.JuegoDeMesa;
import modelo.MovimientoInventario;
import modelo.Usuario;

/**
 * Servicio centralizado para auditoría y movimientos de inventario.
 */
public class ServicioInventario {
    private List<MovimientoInventario> movimientos;

    public ServicioInventario() {
        this.movimientos = new ArrayList<>();
    }

    /**
     * Registra un movimiento de inventario.
     */
    public void registrarMovimiento(MovimientoInventario movimiento) {
        if (movimiento != null) {
            movimientos.add(movimiento);
        }
    }

    /**
     * Obtiene movimientos de un juego específico.
     */
    public List<MovimientoInventario> obtenerMovimientos(JuegoDeMesa juego) {
        return movimientos.stream()
                .filter(m -> m.getJuego().equals(juego))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene movimientos en un rango de fechas.
     */
    public List<MovimientoInventario> obtenerMovimientosEnRango(LocalDate desde, LocalDate hasta) {
        return movimientos.stream()
                .filter(m -> !m.getFecha().toLocalDate().isBefore(desde)
                        && !m.getFecha().toLocalDate().isAfter(hasta))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene movimientos por tipo.
     */
    public List<MovimientoInventario> obtenerMovimientosPorTipo(
            MovimientoInventario.TipoMovimiento tipo) {
        return movimientos.stream()
                .filter(m -> m.getTipo() == tipo)
                .collect(Collectors.toList());
    }

    // Getters
    public List<MovimientoInventario> getHistorialCompleto() {
        return new ArrayList<>(movimientos);
    }
}
