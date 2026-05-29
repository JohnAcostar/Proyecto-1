package test;

import modelo.Cocinero;
import modelo.Horario;
import modelo.Mesero;
import modelo.ResultadoValidacion;
import modelo.SolicitudCambioTurno;
import modelo.TipoSolicitudTurno;
import service.ServicioTurnos;
import service.validadores.ValidadorTurnos;

/**
 * Demo 6: Gestión de Turnos
 * Valida solicitudes de cambio de turno y cobertura mínima.
 */
public class DemoPrueba6GestionTurnos {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║ Demo 6: Gestión de Turnos            ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        // Crear empleados
        Mesero mesero1 = new Mesero("carlos", "pass123", "EMP001", "Carlos García", "DESC001");
        Mesero mesero2 = new Mesero("laura", "pass123", "EMP002", "Laura Pérez", "DESC002");
        Cocinero cocinero1 = new Cocinero("pedro", "pass123", "EMP003", "Pedro López", "DESC003");

        System.out.println("✓ Empleados creados:");
        System.out.println("  • " + mesero1.getLogin() + " (Mesero)");
        System.out.println("  • " + mesero2.getLogin() + " (Mesero)");
        System.out.println("  • " + cocinero1.getLogin() + " (Cocinero)\n");

        // Crear horario
        Horario horario = new Horario();
        System.out.println("✓ Horario creado\n");

        // Servicio de turnos
        ServicioTurnos servicioTurnos = new ServicioTurnos(horario);

        // Test 1: Crear solicitud de cambio entre meseros
        System.out.println("─── TEST 1: Solicitud de cambio (Mesero A ↔ Mesero B) ───");
        SolicitudCambioTurno solicitud1 = new SolicitudCambioTurno(TipoSolicitudTurno.INTERCAMBIO);
        solicitud1.setEmpleadoOrigen(mesero1);
        solicitud1.setEmpleadoDestino(mesero2);

        ResultadoValidacion res1 = servicioTurnos.solicitarCambioTurno(solicitud1);
        System.out.println(res1.toString());
        System.out.println("  Origen: " + solicitud1.getEmpleadoOrigen().getLogin());
        System.out.println("  Destino: " + solicitud1.getEmpleadoDestino().getLogin() + "\n");

        // Test 2: Intentar cambio entre roles incompatibles
        System.out.println("─── TEST 2: Solicitud incompatible (Mesero ↔ Cocinero) ───");
        SolicitudCambioTurno solicitud2 = new SolicitudCambioTurno(TipoSolicitudTurno.INTERCAMBIO);
        solicitud2.setEmpleadoOrigen(mesero1);
        solicitud2.setEmpleadoDestino(cocinero1);

        ValidadorTurnos validador = new ValidadorTurnos();
        ResultadoValidacion res2 = validador.validarCambioTurno(solicitud2, horario);
        System.out.println(res2.toString() + "\n");

        // Test 3: Listar solicitudes pendientes
        System.out.println("─── TEST 3: Solicitudes pendientes ───");
        System.out.println("Total: " + servicioTurnos.getSolicitudesPendientes().size());
        for (SolicitudCambioTurno s : servicioTurnos.getSolicitudesPendientes()) {
            System.out.println("  • Tipo: " + s.getTipo() + ", Estado: " + s.getEstado());
        }
        System.out.println();

        // Test 4: Aprobar solicitud
        System.out.println("─── TEST 4: Aprobar solicitud ───");
        ResultadoValidacion res4 = servicioTurnos.aprobarCambio(solicitud1);
        System.out.println(res4.toString());
        System.out.println("  Solicitudes pendientes después: " + servicioTurnos.getSolicitudesPendientes().size()
                + "\n");

        System.out.println("✓ Demo 6 completada\n");
    }
}
