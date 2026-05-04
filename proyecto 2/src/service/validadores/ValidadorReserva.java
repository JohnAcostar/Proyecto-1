package service.validadores;

import modelo.Cafe;
import modelo.Reserva;
import modelo.ResultadoValidacion;

/**
 * Valida si una reserva es viable según las reglas del negocio.
 */
public class ValidadorReserva {

    public ResultadoValidacion validar(Reserva reserva, Cafe cafe) {
        if (reserva == null) {
            return ResultadoValidacion.error("Reserva nula", "ERR_RESERVA_NULL");
        }

        if (cafe == null) {
            return ResultadoValidacion.error("Café no existe", "ERR_CAFE_NULL");
        }

        // Validar capacidad
        if (!cafe.puedeAlbergarReserva(reserva)) {
            return ResultadoValidacion.error(
                    String.format("Capacidad insuficiente. Disponible: %d, Solicitado: %d",
                            cafe.getCapacidadDisponible(), reserva.getCantidadPersonas()),
                    "ERR_CAPACIDAD_INSUFICIENTE");
        }

        // Validar cantidad mínima de personas
        if (reserva.getCantidadPersonas() <= 0) {
            return ResultadoValidacion.error("La cantidad de personas debe ser mayor a 0", "ERR_PERSONAS_INVALIDA");
        }

        // Validar cantidad máxima razonable
        if (reserva.getCantidadPersonas() > 20) {
            return ResultadoValidacion.error(
                    "La cantidad de personas no puede superar 20 (requiere múltiples mesas)",
                    "ERR_PERSONAS_EXCESO");
        }

        return ResultadoValidacion.exitoso();
    }
}
