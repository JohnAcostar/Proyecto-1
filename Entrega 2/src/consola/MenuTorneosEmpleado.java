package consola;

import modelo.*;
import service.ServicioTorneos;
import java.util.List;
import java.util.Scanner;
import service.validadores.ValidadorTorneo;

/**
 * Interfaz de consola para que empleados gestionen su participación en torneos.
 */
public class MenuTorneosEmpleado {
    private Scanner scanner;
    private Empleado empleado;
    private ServicioTorneos servicioTorneos;
    private List<Torneo> todosTorneos;

    public MenuTorneosEmpleado(Scanner scanner, Empleado empleado, 
                               ServicioTorneos servicioTorneos, 
                               List<Torneo> todosTorneos) {
        this.scanner = scanner;
        this.empleado = empleado;
        this.servicioTorneos = servicioTorneos;
        this.todosTorneos = todosTorneos;
    }

    /**
     * Muestra el menú principal de torneos para empleados.
     */
    public void mostrarMenuTorneos() {
        boolean seguir = true;
        
        while (seguir) {
            System.out.println("\n========== MENÚ DE TORNEOS (EMPLEADO) ==========");
            System.out.println("1. Ver torneos disponibles");
            System.out.println("2. Ver mis inscripciones");
            System.out.println("3. Registrarme a un torneo");
            System.out.println("4. Retirarme de un torneo");
            System.out.println("5. Mi horario de trabajo");
            System.out.println("6. Volver al menú anterior");
            System.out.println("===============================================");
            System.out.print("Selecciona una opción: ");
            
            String opcion = scanner.nextLine().trim();
            
            switch (opcion) {
                case "1":
                    mostrarTorneosDisponibles();
                    break;
                case "2":
                    mostrarMisTorneos();
                    break;
                case "3":
                    registrarseATorneo();
                    break;
                case "4":
                    retirarseDelTorneo();
                    break;
                case "5":
                    verHorarioTrabajo();
                    break;
                case "6":
                    seguir = false;
                    break;
                default:
                    System.out.println("❌ Opción inválida. Intenta de nuevo.");
            }
        }
    }

    /**
     * Muestra todos los torneos abiertos disponibles.
     */
    private void mostrarTorneosDisponibles() {
        List<Torneo> abiertos = servicioTorneos.obtenerTorneosAbiertos();
        
        if (abiertos.isEmpty()) {
            System.out.println("\n❌ No hay torneos disponibles en este momento.");
            return;
        }
        
        System.out.println("\n========== TORNEOS DISPONIBLES ==========");
        for (Torneo torneo : abiertos) {
            mostrarDetallesTorneo(torneo);
            verificarDisponibilidadParaEmpleado(torneo);
            System.out.println();
        }
    }

    /**
     * Muestra los detalles de un torneo específico.
     */
    private void mostrarDetallesTorneo(Torneo torneo) {
        System.out.println("🎮 ID: " + torneo.getId());
        System.out.println("   Nombre: " + torneo.getNombre());
        System.out.println("   Juego: " + torneo.getNombreJuego());
        System.out.println("   Tipo: " + torneo.getTipo().getNombre());
        System.out.println("   Día: " + ValidadorTorneo.obtenerNombreDiaSemana(torneo.getDiaSemana()));
        System.out.println("   Estado: " + torneo.getEstado().getDescripcion());
        System.out.println("   Participantes: " + torneo.getCantidadParticipantes() + "/" + torneo.getCantidadMaximaParticipantes());
        System.out.println("   Spots disponibles: " + torneo.getSpotsDisponibles());
        
        if (torneo.getTipo() == TipoTorneo.COMPETITIVO) {
            System.out.println("   Cuota: $" + torneo.getMontoEntrada() + " (GRATIS PARA EMPLEADOS)");
        }
    }

    /**
     * Verifica si el empleado puede registrarse a un torneo (respetando su horario).
     */
    private void verificarDisponibilidadParaEmpleado(Torneo torneo) {
        boolean puedeRegistrarse = true;
        
        // Verificar si trabaja ese día
        for (Turno turno : empleado.getTurnos()) {
            if (turno.getDiaSemana() == torneo.getDiaSemana() &&
                turno.getEstado() != EstadoReserva.CANCELADO) {
                puedeRegistrarse = false;
                System.out.println("   ⚠️  Trabajas este día (" + turno.getHoraInicio() + " - " + turno.getHoraFin() + ")");
                break;
            }
        }
        
        if (puedeRegistrarse) {
            System.out.println("   ✅ Puedes registrarte");
        }
    }

    /**
     * Muestra los torneos en los que el empleado está inscrito.
     */
    private void mostrarMisTorneos() {
        List<Torneo> misTorneos = servicioTorneos.obtenerTorneosDelUsuario(empleado.getId());
        
        if (misTorneos.isEmpty()) {
            System.out.println("\n❌ No estás inscrito en ningún torneo.");
            return;
        }
        
        System.out.println("\n========== MIS INSCRIPCIONES ==========");
        for (Torneo torneo : misTorneos) {
            ParticipanteTorneo participante = torneo.obtenerParticipante(empleado.getId());
            System.out.println("🎮 " + torneo.getNombre() + " (" + torneo.getTipo().getNombre() + ")");
            System.out.println("   Juego: " + torneo.getNombreJuego());
            System.out.println("   Día: " + ValidadorTorneo.obtenerNombreDiaSemana(torneo.getDiaSemana()));
            System.out.println("   Estado: " + torneo.getEstado().getDescripcion());
            
            if (participante.gano()) {
                System.out.println("   ✅ GANASTE - Premio: $" + participante.getPremioODescuento());
            } else {
                System.out.println("   (Nota: Los empleados no reciben premio en torneos competitivos)");
            }
            System.out.println();
        }
    }

    /**
     * Permite que el empleado se registre a un torneo.
     */
    private void registrarseATorneo() {
        List<Torneo> abiertos = servicioTorneos.obtenerTorneosAbiertos();
        
        if (abiertos.isEmpty()) {
            System.out.println("\n❌ No hay torneos disponibles para inscribirse.");
            return;
        }
        
        System.out.println("\n========== TORNEOS DISPONIBLES PARA INSCRIPCIÓN ==========");
        for (int i = 0; i < abiertos.size(); i++) {
            Torneo t = abiertos.get(i);
            System.out.println((i + 1) + ". " + t.getNombre() + " - " + t.getNombreJuego());
        }
        
        System.out.print("Selecciona el número del torneo (0 para cancelar): ");
        String entrada = scanner.nextLine().trim();
        
        if (entrada.equals("0")) {
            return;
        }
        
        try {
            int indice = Integer.parseInt(entrada) - 1;
            if (indice < 0 || indice >= abiertos.size()) {
                System.out.println("❌ Selección inválida.");
                return;
            }
            
            Torneo torneo = abiertos.get(indice);
            
            // Verificar si trabaja ese día
            boolean trabaja = empleado.getTurnos().stream()
                    .anyMatch(t -> t.getDiaSemana() == torneo.getDiaSemana() &&
                                  t.getEstado() != EstadoReserva.CANCELADO);
            
            if (trabaja) {
                System.out.println("❌ No puedes registrarte a un torneo el día que trabajas.");
                return;
            }
            
            ResultadoValidacion resultado = servicioTorneos.registrarUsuarioATorneo(
                    torneo.getId(), empleado, todosTorneos
            );
            
            if (resultado.esValido()) {
                System.out.println("✅ ¡Te has inscrito al torneo exitosamente!");
                if (torneo.getTipo() == TipoTorneo.COMPETITIVO) {
                    System.out.println("   (La cuota de entrada es gratis para empleados)");
                }
            } else {
                System.out.println("❌ " + resultado.getMensaje());
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Entrada inválida. Debes ingresar un número.");
        }
    }

    /**
     * Permite que el empleado se retire de un torneo.
     */
    private void retirarseDelTorneo() {
        List<Torneo> misTorneos = servicioTorneos.obtenerTorneosDelUsuario(empleado.getId());
        
        if (misTorneos.isEmpty()) {
            System.out.println("\n❌ No estás inscrito en ningún torneo.");
            return;
        }
        
        System.out.println("\n========== MIS TORNEOS ==========");
        for (int i = 0; i < misTorneos.size(); i++) {
            System.out.println((i + 1) + ". " + misTorneos.get(i).getNombre());
        }
        
        System.out.print("Selecciona el número del torneo del cual retirarse (0 para cancelar): ");
        String entrada = scanner.nextLine().trim();
        
        if (entrada.equals("0")) {
            return;
        }
        
        try {
            int indice = Integer.parseInt(entrada) - 1;
            if (indice < 0 || indice >= misTorneos.size()) {
                System.out.println("❌ Selección inválida.");
                return;
            }
            
            Torneo torneo = misTorneos.get(indice);
            
            ResultadoValidacion resultado = servicioTorneos.retirarUsuarioDeTorneo(
                    torneo.getId(), empleado
            );
            
            if (resultado.esValido()) {
                System.out.println("✅ Te has retirado del torneo exitosamente.");
            } else {
                System.out.println("❌ " + resultado.getMensaje());
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Entrada inválida. Debes ingresar un número.");
        }
    }

    /**
     * Muestra el horario de trabajo del empleado.
     */
    private void verHorarioTrabajo() {
        List<Turno> turnos = empleado.getTurnos();
        
        if (turnos.isEmpty()) {
            System.out.println("\n❌ No tienes turnos asignados.");
            return;
        }
        
        System.out.println("\n========== MI HORARIO DE TRABAJO ==========");
        for (Turno turno : turnos) {
            if (turno.getEstado() != EstadoReserva.CANCELADO) {
                System.out.println("🕐 " + ValidadorTorneo.obtenerNombreDiaSemana(turno.getDiaSemana()));
                System.out.println("   Hora: " + turno.getHoraInicio() + " - " + turno.getHoraFin());
                System.out.println("   Estado: " + turno.getEstado().toString());
            }
        }
    }
}
