package service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import modelo.Cafe;
import modelo.CopiaJuego;
import modelo.JuegoDeMesa;
import modelo.Mesa;
import modelo.Prestamo;
import modelo.ResultadoValidacion;
import modelo.Usuario;
import service.validadores.ValidadorPrestamo;

/**
 * Servicio centralizado para gestión de préstamos.
 * Encapsula todas las reglas de negocio relacionadas con préstamos.
 */
public class ServicioPrestamos {
    private ValidadorPrestamo validador;
    private List<Prestamo> prestamosActivos;
    private List<Prestamo> prestamosHistorico;

    public ServicioPrestamos() {
        this.validador = new ValidadorPrestamo();
        this.prestamosActivos = new ArrayList<>();
        this.prestamosHistorico = new ArrayList<>();
    }

    /**
     * Solicita un préstamo después de validar todas las reglas.
     */
    public ResultadoValidacion solicitarPrestamo(JuegoDeMesa juego, Cafe cafe, Mesa mesa, Usuario usuario,
            String prestamoId) {
        // Validar
        ResultadoValidacion resultado = validador.validar(juego, cafe, mesa);
        if (!resultado.esValido()) {
            return resultado;
        }

        // Si todo es válido, crear el préstamo
        CopiaJuego copia = obtenerCopiaPrestable(juego);
        if (copia == null) {
            return ResultadoValidacion.error("No hay copias disponibles", "ERR_COPIAS_NULL");
        }

        boolean advertencia = necesitaAdvertencia(juego, cafe);
        Prestamo prestamo = new Prestamo(prestamoId, copia, advertencia, usuario);
        prestamosActivos.add(prestamo);
        prestamosHistorico.add(prestamo);
        mesa.agregarPrestamoDeJuego(prestamo);

        return ResultadoValidacion.exitoso();
    }

    /**
     * Registra la devolución de un préstamo.
     */
    public ResultadoValidacion devolverPrestamo(String prestamoId) {
        Prestamo prestamo = encontrarPrestamo(prestamoId);
        if (prestamo == null) {
            return ResultadoValidacion.error("Préstamo no encontrado", "ERR_PRESTAMO_NOT_FOUND");
        }

        if (!prestamo.estaActivo()) {
            return ResultadoValidacion.error("Préstamo ya fue devuelto", "ERR_PRESTAMO_NO_ACTIVO");
        }

        prestamo.registrarDevolucion();
        prestamosActivos.remove(prestamo);

        return ResultadoValidacion.exitoso();
    }

    /**
     * Obtiene todos los préstamos activos de un usuario.
     */
    public List<Prestamo> obtenerPrestamosActivos(Usuario usuario) {
        return prestamosActivos.stream()
                .filter(p -> p.getUsuario() != null && p.getUsuario().equals(usuario))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene historial completo de préstamos de un usuario.
     */
    public List<Prestamo> obtenerHistorialPrestamos(Usuario usuario) {
        return prestamosHistorico.stream()
                .filter(p -> p.getUsuario() != null && p.getUsuario().equals(usuario))
                .collect(Collectors.toList());
    }

    // Helper methods
    private CopiaJuego obtenerCopiaPrestable(JuegoDeMesa juego) {
        // TODO: consultar InventarioPrestamos
        return null;
    }

    private boolean necesitaAdvertencia(JuegoDeMesa juego, Cafe cafe) {
        // Si el juego es difícil pero no hay mesero, retorna true (advertencia)
        return false;
    }

    private Prestamo encontrarPrestamo(String prestamoId) {
        for (Prestamo p : prestamosActivos) {
            if (p.getPrestamoId().equals(prestamoId)) {
                return p;
            }
        }
        for (Prestamo p : prestamosHistorico) {
            if (p.getPrestamoId().equals(prestamoId)) {
                return p;
            }
        }
        return null;
    }

    // Getters
    public List<Prestamo> getPrestamosActivos() {
        return new ArrayList<>(prestamosActivos);
    }

    public List<Prestamo> getHistoricoPrestamos() {
        return new ArrayList<>(prestamosHistorico);
    }
}
