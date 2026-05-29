package service.validadores;

import modelo.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * Validador especializado en lógica de negocio para torneos.
 * Valida todas las operaciones relacionadas con creación, inscripción y retiro de torneos.
 */
public class ValidadorTorneo {

    /**
     * Valida la creación de un nuevo torneo.
     */
    public ResultadoValidacion validarCreacionTorneo(String nombre, JuegoDeMesa juego,
                                                      TipoTorneo tipo, int cantidadParticipantes,
                                                      int diaSemana, double montoEntrada) {
        // Validar nombre
        if (nombre == null || nombre.trim().isEmpty()) {
            return new ResultadoValidacion(false, "El nombre del torneo no puede estar vacío");
        }

        // Validar juego
        if (juego == null) {
            return new ResultadoValidacion(false, "Debe seleccionar un juego válido");
        }

        // Validar cantidad de participantes
        if (cantidadParticipantes < 2) {
            return new ResultadoValidacion(false, "El torneo debe tener mínimo 2 participantes");
        }

        // Validar día de semana
        if (diaSemana < 1 || diaSemana > 7) {
            return new ResultadoValidacion(false, "El día de la semana debe estar entre 1 (lunes) y 7 (domingo)");
        }

        // Para torneos competitivos, validar monto de entrada
        if (tipo == TipoTorneo.COMPETITIVO && montoEntrada <= 0) {
            return new ResultadoValidacion(false, "Los torneos competitivos requieren una cuota de inscripción mayor a 0");
        }

        if (tipo == TipoTorneo.AMISTOSO && montoEntrada > 0) {
            return new ResultadoValidacion(false, "Los torneos amistosos no deben tener cuota de inscripción");
        }

        return new ResultadoValidacion(true, "Torneo válido para crear");
    }

    /**
     * Valida que un usuario pueda registrarse a un torneo.
     */
    public ResultadoValidacion validarRegistroTorneo(Torneo torneo, Usuario usuario,
                                                      List<Torneo> allTournaments) {
        // Validar estado del torneo
        if (torneo.getEstado() != EstadoTorneo.ABIERTO) {
            return new ResultadoValidacion(false,
                    "El torneo " + torneo.getNombre() + " no está abierto para inscripciones");
        }

        // Validar que haya spots disponibles
        if (!torneo.haySpotDisponible()) {
            return new ResultadoValidacion(false, "No hay spots disponibles en este torneo");
        }

        // Validar límite de 3 participantes por usuario
        if (torneo.contarParticipantesDelUsuario(usuario.getId()) >= 3) {
            return new ResultadoValidacion(false,
                    "Máximo 3 participantes por usuario por torneo");
        }

        // Si el usuario es empleado, validar que no esté trabajando ese día
        if (usuario instanceof Empleado) {
            Empleado empleado = (Empleado) usuario;
            boolean estaTrabajando = false;
            
            for (Turno turno : empleado.getTurnos()) {
                if (turno.getDiaSemana() == torneo.getDiaSemana() &&
                    turno.getEstado() != EstadoReserva.CANCELADO) {
                    estaTrabajando = true;
                    break;
                }
            }
            
            if (estaTrabajando) {
                return new ResultadoValidacion(false,
                        "No puedes inscribirte a un torneo el día que trabajas");
            }
        }

        return new ResultadoValidacion(true, "Usuario puede registrarse al torneo");
    }

    /**
     * Valida que un usuario pueda retirarse de un torneo.
     */
    public ResultadoValidacion validarRetiroTorneo(Torneo torneo, Usuario usuario) {
        // Validar que el usuario esté inscrito
        ParticipanteTorneo participante = torneo.obtenerParticipante(usuario.getId());
        if (participante == null) {
            return new ResultadoValidacion(false,
                    "No estás inscrito en este torneo");
        }

        // Validar que el torneo aún esté abierto (no en progreso ni finalizado)
        if (torneo.getEstado() == EstadoTorneo.EN_PROGRESO ||
            torneo.getEstado() == EstadoTorneo.FINALIZADO) {
            return new ResultadoValidacion(false,
                    "No puedes retirarte de un torneo que ya comenzó o finalizó");
        }

        return new ResultadoValidacion(true, "Usuario puede retirarse del torneo");
    }

    /**
     * Valida que un torneo pueda finalizarse.
     */
    public ResultadoValidacion validarFinalizacionTorneo(Torneo torneo) {
        if (torneo.getEstado() == EstadoTorneo.FINALIZADO) {
            return new ResultadoValidacion(false, "El torneo ya está finalizado");
        }

        if (torneo.getEstado() == EstadoTorneo.CANCELADO) {
            return new ResultadoValidacion(false, "No se puede finalizar un torneo cancelado");
        }

        if (torneo.getCantidadParticipantes() < 2) {
            return new ResultadoValidacion(false,
                    "No se puede finalizar un torneo con menos de 2 participantes");
        }

        return new ResultadoValidacion(true, "Torneo puede finalizarse");
    }

    /**
     * Valida el cálculo del descuento o premio para un ganador.
     */
    public ResultadoValidacion validarCalculoPremio(Torneo torneo, double montoRecaudado) {
        if (torneo.getTipo() == TipoTorneo.COMPETITIVO) {
            if (montoRecaudado <= 0) {
                return new ResultadoValidacion(false,
                        "No se puede calcular premio con monto recaudado <= 0");
            }
        }

        return new ResultadoValidacion(true, "Cálculo de premio válido");
    }

    /**
     * Obtiene el día de semana en formato legible.
     */
    public static String obtenerNombreDiaSemana(int dia) {
        String[] dias = {"", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        return dia >= 1 && dia <= 7 ? dias[dia] : "Día inválido";
    }
}
