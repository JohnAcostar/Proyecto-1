package service.validadores;

import modelo.Cocinero;
import modelo.Empleado;
import modelo.Horario;
import modelo.Mesero;
import modelo.ResultadoValidacion;
import modelo.SolicitudCambioTurno;

/**
 * Valida cambios de turno según cobertura mínima requerida.
 */
public class ValidadorTurnos {
    private static final int MIN_COCINEROS = 1;
    private static final int MIN_MESEROS = 2;

    public ResultadoValidacion validarCambioTurno(SolicitudCambioTurno solicitud, Horario horario) {
        if (solicitud == null) {
            return ResultadoValidacion.error("Solicitud nula", "ERR_SOLICITUD_NULL");
        }

        Empleado empleadoQueSeVa = solicitud.getEmpleadoOrigen();
        Empleado empleadoQueViene = solicitud.getEmpleadoDestino();

        if (empleadoQueSeVa == null) {
            return ResultadoValidacion.error("Empleado origen nulo", "ERR_EMPLEADO_ORIGEN_NULL");
        }

        if (empleadoQueViene == null) {
            return ResultadoValidacion.error("Empleado destino nulo", "ERR_EMPLEADO_DESTINO_NULL");
        }

        // Validar que los roles sean compatibles
        if (!sonRolesCompatibles(empleadoQueSeVa, empleadoQueViene)) {
            return ResultadoValidacion.error("Los roles no son compatibles para intercambio de turno",
                    "ERR_ROLES_INCOMPATIBLES");
        }

        return ResultadoValidacion.exitoso();
    }

    private boolean sonRolesCompatibles(Empleado a, Empleado b) {
        // Cocinero no reemplaza Mesero y viceversa
        boolean aEsCocinero = a instanceof Cocinero;
        boolean bEsCocinero = b instanceof Cocinero;
        boolean aEsMesero = a instanceof Mesero;
        boolean bEsMesero = b instanceof Mesero;

        if ((aEsCocinero && bEsMesero) || (aEsMesero && bEsCocinero)) {
            return false;
        }

        return true;
    }
}
