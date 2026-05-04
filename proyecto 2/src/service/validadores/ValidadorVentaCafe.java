package service.validadores;

import modelo.Mesa;
import modelo.ResultadoValidacion;

/**
 * Valida si una venta de café puede realizarse según reglas del negocio.
 */
public class ValidadorVentaCafe {

    public ResultadoValidacion validar(Mesa mesa) {
        if (mesa == null) {
            return ResultadoValidacion.error("Mesa nula", "ERR_MESA_NULL");
        }

        return ResultadoValidacion.exitoso();
    }

    public ResultadoValidacion validarBebidasCalientesConJuegoAccion(Mesa mesa) {
        if (mesa == null) {
            return ResultadoValidacion.error("Mesa nula", "ERR_MESA_NULL");
        }

        // Placeholder: rastrear bebidas calientes servidas
        boolean tieneJuegoAccion = mesa.getJuegosPrestados().stream()
                .anyMatch(p -> p.getCopia().getJuego().getCategoria().toString().equals("ACCION"));

        if (tieneJuegoAccion) {
            return ResultadoValidacion.warning("Nota: Hay juegos ACCION en la mesa", "WARN_JUEGO_ACCION");
        }

        return ResultadoValidacion.exitoso();
    }
}
