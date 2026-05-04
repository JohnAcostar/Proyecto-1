package service;

import java.util.ArrayList;
import java.util.List;

import modelo.Horario;
import modelo.ResultadoValidacion;
import modelo.SolicitudCambioTurno;
import service.validadores.ValidadorTurnos;

/**
 * Servicio centralizado para gestión de turnos y cambios.
 */
public class ServicioTurnos {
    private ValidadorTurnos validador;
    private List<SolicitudCambioTurno> solicitudes;
    private Horario horario;

    public ServicioTurnos(Horario horario) {
        this.validador = new ValidadorTurnos();
        this.solicitudes = new ArrayList<>();
        this.horario = horario;
    }

    /**
     * Solicita un cambio de turno.
     */
    public ResultadoValidacion solicitarCambioTurno(SolicitudCambioTurno solicitud) {
        if (solicitud == null) {
            return ResultadoValidacion.error("Solicitud nula", "ERR_SOLICITUD_NULL");
        }

        ResultadoValidacion valida = validador.validarCambioTurno(solicitud, horario);
        if (!valida.esValido()) {
            return valida;
        }

        solicitudes.add(solicitud);
        return ResultadoValidacion.exitoso();
    }

    /**
     * Aprueba una solicitud de cambio de turno.
     */
    public ResultadoValidacion aprobarCambio(SolicitudCambioTurno solicitud) {
        if (!solicitudes.contains(solicitud)) {
            return ResultadoValidacion.error("Solicitud no encontrada", "ERR_SOLICITUD_NOT_FOUND");
        }

        // TODO: ejecutar cambio en horario
        solicitudes.remove(solicitud);

        return ResultadoValidacion.exitoso();
    }

    /**
     * Rechaza una solicitud de cambio de turno.
     */
    public ResultadoValidacion rechazarCambio(SolicitudCambioTurno solicitud, String motivo) {
        if (!solicitudes.contains(solicitud)) {
            return ResultadoValidacion.error("Solicitud no encontrada", "ERR_SOLICITUD_NOT_FOUND");
        }

        solicitudes.remove(solicitud);

        return ResultadoValidacion.exitoso();
    }

    // Getters
    public List<SolicitudCambioTurno> getSolicitudesPendientes() {
        return new ArrayList<>(solicitudes);
    }
}
