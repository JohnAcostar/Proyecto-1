package service.validadores;

import modelo.Cafe;
import modelo.CategoriaJuego;
import modelo.JuegoDeMesa;
import modelo.Mesa;
import modelo.ResultadoValidacion;

/**
 * Valida si un préstamo de juego es permitido según las reglas del negocio.
 */
public class ValidadorPrestamo {

    public ResultadoValidacion validar(JuegoDeMesa juego, Cafe cafe, Mesa mesa) {
        if (juego == null) {
            return ResultadoValidacion.error("Juego nulo", "ERR_JUEGO_NULL");
        }

        if (cafe == null) {
            return ResultadoValidacion.error("Café nulo", "ERR_CAFE_NULL");
        }

        if (mesa == null) {
            return ResultadoValidacion.error("Mesa nula", "ERR_MESA_NULL");
        }

        // Validar cantidad de jugadores
        if (!juego.esAptoParaCantidadJugadores(mesa.getCapacidad())) {
            return ResultadoValidacion
                    .error(String.format("El juego requiere %d-%d jugadores, la mesa tiene %d", juego.getMinJugadores(),
                            juego.getMaxJugadores(), mesa.getCapacidad()), "ERR_JUGADORES");
        }

        // Validar restricciones de edad
        if (!juego.esAptoParaEdad(mesa.hayNinos(), mesa.hayJovenes())) {
            return ResultadoValidacion.error("El juego no es apto para la edad de los ocupantes de la mesa",
                    "ERR_EDAD");
        }

        // Validar máximo 2 juegos por mesa
        if (mesa.cantidadJuegosPrestados() >= 2) {
            return ResultadoValidacion.error("La mesa ya tiene 2 juegos prestados (máximo permitido)",
                    "ERR_MAX_JUEGOS");
        }

        return ResultadoValidacion.exitoso();
    }
}
