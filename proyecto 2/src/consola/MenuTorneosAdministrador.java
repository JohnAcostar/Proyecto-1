package consola;

import modelo.*;
import service.ServicioTorneos;
import java.util.List;
import java.util.Scanner;
import service.validadores.ValidadorTorneo;

/**
 * Interfaz de consola para que administradores gestionen torneos.
 */
public class MenuTorneosAdministrador {
    private Scanner scanner;
    private Administrador admin;
    private ServicioTorneos servicioTorneos;
    private List<JuegoDeMesa> juegos;
    private List<CopiaJuego> copiasPrestamo;
    private List<Torneo> todosTorneos;

    public MenuTorneosAdministrador(Scanner scanner, Administrador admin, 
                                    ServicioTorneos servicioTorneos, 
                                    List<JuegoDeMesa> juegos, 
                                    List<CopiaJuego> copiasPrestamo,
                                    List<Torneo> todosTorneos) {
        this.scanner = scanner;
        this.admin = admin;
        this.servicioTorneos = servicioTorneos;
        this.juegos = juegos;
        this.copiasPrestamo = copiasPrestamo;
        this.todosTorneos = todosTorneos;
    }

    /**
     * Muestra el menú principal de gestión de torneos para administradores.
     */
    public void mostrarMenuTorneos() {
        boolean seguir = true;
        
        while (seguir) {
            System.out.println("\n========== MENÚ DE GESTIÓN DE TORNEOS (ADMIN) ==========");
            System.out.println("1. Crear un nuevo torneo");
            System.out.println("2. Ver todos los torneos");
            System.out.println("3. Ver participantes de un torneo");
            System.out.println("4. Finalizar un torneo");
            System.out.println("5. Ver reportes de torneos");
            System.out.println("6. Volver al menú anterior");
            System.out.println("======================================================");
            System.out.print("Selecciona una opción: ");
            
            String opcion = scanner.nextLine().trim();
            
            switch (opcion) {
                case "1":
                    crearTorneo();
                    break;
                case "2":
                    verTodosTorneos();
                    break;
                case "3":
                    verParticipantesTorneo();
                    break;
                case "4":
                    finalizarTorneo();
                    break;
                case "5":
                    verReportesTorneos();
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
     * Permite crear un nuevo torneo.
     */
    private void crearTorneo() {
        System.out.println("\n========== CREAR NUEVO TORNEO ==========");
        
        // Obtener nombre
        System.out.print("Nombre del torneo: ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) {
            System.out.println("❌ El nombre no puede estar vacío.");
            return;
        }
        
        // Seleccionar juego
        System.out.println("\nJuegos disponibles:");
        for (int i = 0; i < juegos.size(); i++) {
            System.out.println((i + 1) + ". " + juegos.get(i).getNombre());
        }
        System.out.print("Selecciona el juego: ");
        String seleccionJuego = scanner.nextLine().trim();
        
        int indiceJuego;
        try {
            indiceJuego = Integer.parseInt(seleccionJuego) - 1;
            if (indiceJuego < 0 || indiceJuego >= juegos.size()) {
                System.out.println("❌ Selección inválida.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Entrada inválida.");
            return;
        }
        
        JuegoDeMesa juegoSeleccionado = juegos.get(indiceJuego);
        
        // Seleccionar tipo de torneo
        System.out.println("\nTipo de torneo:");
        System.out.println("1. Amistoso (sin cuota de entrada)");
        System.out.println("2. Competitivo (con cuota de entrada)");
        System.out.print("Selecciona el tipo: ");
        String seleccionTipo = scanner.nextLine().trim();
        
        TipoTorneo tipo;
        if (seleccionTipo.equals("1")) {
            tipo = TipoTorneo.AMISTOSO;
        } else if (seleccionTipo.equals("2")) {
            tipo = TipoTorneo.COMPETITIVO;
        } else {
            System.out.println("❌ Tipo inválido.");
            return;
        }
        
        // Cantidad de participantes
        System.out.print("Cantidad máxima de participantes: ");
        int cantidad;
        try {
            cantidad = Integer.parseInt(scanner.nextLine().trim());
            if (cantidad < 2) {
                System.out.println("❌ Mínimo 2 participantes.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Entrada inválida.");
            return;
        }
        
        if (cantidad > juegoSeleccionado.getMaxJugadores()) {
            int copiasDisponibles = servicioTorneos.verificarDisponibilidadCopias(
                    juegoSeleccionado, cantidad, copiasPrestamo);
            if (copiasDisponibles < cantidad) {
                System.out.println("❌ No hay suficientes copias de prestamo disponibles para superar el maximo normal del juego.");
                System.out.println("   Copias disponibles: " + copiasDisponibles + " / participantes solicitados: " + cantidad);
                return;
            }
        }

        // Día de la semana
        System.out.println("\nDía de la semana:");
        System.out.println("1. Lunes   2. Martes  3. Miércoles  4. Jueves");
        System.out.println("5. Viernes 6. Sábado  7. Domingo");
        System.out.print("Selecciona el día (1-7): ");
        int dia;
        try {
            dia = Integer.parseInt(scanner.nextLine().trim());
            if (dia < 1 || dia > 7) {
                System.out.println("❌ Día inválido.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Entrada inválida.");
            return;
        }
        
        // Cuota de entrada (solo si es competitivo)
        double montoEntrada = 0;
        if (tipo == TipoTorneo.COMPETITIVO) {
            System.out.print("Cuota de entrada ($): ");
            try {
                montoEntrada = Double.parseDouble(scanner.nextLine().trim());
                if (montoEntrada <= 0) {
                    System.out.println("❌ La cuota debe ser mayor a 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Entrada inválida.");
                return;
            }
        }
        
        // Crear torneo
        ResultadoValidacion resultado = servicioTorneos.crearTorneo(
                nombre, juegoSeleccionado, tipo, cantidad, dia, admin, montoEntrada
        );
        
        if (resultado.esValido()) {
            System.out.println("✅ " + resultado.getMensaje());
        } else {
            System.out.println("❌ " + resultado.getMensaje());
        }
    }

    /**
     * Muestra todos los torneos del sistema.
     */
    private void verTodosTorneos() {
        List<Torneo> todos = servicioTorneos.obtenerTodosTorneos();
        
        if (todos.isEmpty()) {
            System.out.println("\n❌ No hay torneos en el sistema.");
            return;
        }
        
        System.out.println("\n========== TODOS LOS TORNEOS ==========");
        for (Torneo torneo : todos) {
            System.out.println("🎮 " + torneo.getNombre());
            System.out.println("   ID: " + torneo.getId());
            System.out.println("   Juego: " + torneo.getNombreJuego());
            System.out.println("   Tipo: " + torneo.getTipo().getNombre());
            System.out.println("   Día: " + ValidadorTorneo.obtenerNombreDiaSemana(torneo.getDiaSemana()));
            System.out.println("   Estado: " + torneo.getEstado().getDescripcion());
            System.out.println("   Participantes: " + torneo.getCantidadParticipantes() + "/" + torneo.getCantidadMaximaParticipantes());
            System.out.println("   Spots reservados para fans: " + torneo.getSpotReservadosFans());
            System.out.println();
        }
    }

    /**
     * Muestra los participantes de un torneo específico.
     */
    private void verParticipantesTorneo() {
        System.out.print("Ingresa el ID del torneo: ");
        int idTorneo;
        try {
            idTorneo = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("❌ Entrada inválida.");
            return;
        }
        
        Torneo torneo = servicioTorneos.obtenerTorneoPorId(idTorneo);
        if (torneo == null) {
            System.out.println("❌ Torneo no encontrado.");
            return;
        }
        
        List<ParticipanteTorneo> participantes = torneo.getParticipantes();
        
        System.out.println("\n========== PARTICIPANTES DE " + torneo.getNombre().toUpperCase() + " ==========");
        System.out.println("Total: " + participantes.size() + "/" + torneo.getCantidadMaximaParticipantes());
        System.out.println();
        
        for (int i = 0; i < participantes.size(); i++) {
            ParticipanteTorneo p = participantes.get(i);
            System.out.print((i + 1) + ". " + p.getNombreUsuario());
            if (p.esFan()) System.out.print(" (Fan)");
            if (p.pagoPorEntrada()) System.out.print(" - Pagó $" + p.getMontoInscripcion());
            if (p.gano()) System.out.print(" - ✅ GANADOR");
            System.out.println();
        }
    }

    /**
     * Permite finalizar un torneo.
     */
    private void finalizarTorneo() {
        System.out.print("Ingresa el ID del torneo a finalizar: ");
        int idTorneo;
        try {
            idTorneo = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("❌ Entrada inválida.");
            return;
        }
        
        Torneo torneo = servicioTorneos.obtenerTorneoPorId(idTorneo);
        if (torneo == null) {
            System.out.println("❌ Torneo no encontrado.");
            return;
        }
        
        if (torneo.getCantidadParticipantes() == 0) {
            System.out.println("❌ El torneo no tiene participantes.");
            return;
        }
        
        // Mostrar participantes
        List<ParticipanteTorneo> participantes = torneo.getParticipantes();
        System.out.println("\nParticipantes del torneo:");
        for (int i = 0; i < participantes.size(); i++) {
            System.out.println((i + 1) + ". " + participantes.get(i).getNombreUsuario());
        }
        
        // Seleccionar ganador
        System.out.print("Selecciona el número del ganador: ");
        int indiceGanador;
        try {
            indiceGanador = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (indiceGanador < 0 || indiceGanador >= participantes.size()) {
                System.out.println("❌ Selección inválida.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Entrada inválida.");
            return;
        }
        
        int idGanador = participantes.get(indiceGanador).getIdUsuario();
        
        // Finalizar torneo
        ResultadoValidacion resultado = servicioTorneos.finalizarTorneo(idTorneo, idGanador);
        
        if (resultado.esValido()) {
            System.out.println("✅ " + resultado.getMensaje());
        } else {
            System.out.println("❌ " + resultado.getMensaje());
        }
    }

    /**
     * Muestra reportes de torneos.
     */
    private void verReportesTorneos() {
        List<Torneo> todos = servicioTorneos.obtenerTodosTorneos();
        
        System.out.println("\n========== REPORTE DE TORNEOS ==========");
        System.out.println("Total de torneos: " + todos.size());
        
        long torneosAbiertos = todos.stream().filter(t -> t.getEstado() == EstadoTorneo.ABIERTO).count();
        long torneosEnProgreso = todos.stream().filter(t -> t.getEstado() == EstadoTorneo.EN_PROGRESO).count();
        long torneosFinalizados = todos.stream().filter(t -> t.getEstado() == EstadoTorneo.FINALIZADO).count();
        
        System.out.println("  - Abiertos: " + torneosAbiertos);
        System.out.println("  - En progreso: " + torneosEnProgreso);
        System.out.println("  - Finalizados: " + torneosFinalizados);
        
        long amistosos = todos.stream().filter(t -> t.getTipo() == TipoTorneo.AMISTOSO).count();
        long competitivos = todos.stream().filter(t -> t.getTipo() == TipoTorneo.COMPETITIVO).count();
        
        System.out.println("\nPor tipo:");
        System.out.println("  - Amistosos: " + amistosos);
        System.out.println("  - Competitivos: " + competitivos);
        
        double totalParticipantes = todos.stream().mapToInt(Torneo::getCantidadParticipantes).sum();
        System.out.println("\nTotal de participantes inscritos: " + (int)totalParticipantes);
        
        double ingresosTotales = todos.stream()
                .filter(t -> t.getTipo() == TipoTorneo.COMPETITIVO)
                .mapToDouble(Torneo::getPremioTotal)
                .sum();
        System.out.println("Ingresos totales por torneos competitivos: $" + ingresosTotales);
    }
}
