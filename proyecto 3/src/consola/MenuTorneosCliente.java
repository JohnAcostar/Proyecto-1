package consola;

import modelo.*;
import service.ServicioTorneos;
import java.util.List;
import java.util.Scanner;
import service.validadores.ValidadorTorneo;

/**
 * Interfaz de consola para que clientes gestionen su participación en torneos.
 */
public class MenuTorneosCliente {
    private Scanner scanner;
    private Cliente cliente;
    private ServicioTorneos servicioTorneos;
    private List<Torneo> todosTorneos;

    public MenuTorneosCliente(Scanner scanner, Cliente cliente, ServicioTorneos servicioTorneos, List<Torneo> todosTorneos) {
        this.scanner = scanner;
        this.cliente = cliente;
        this.servicioTorneos = servicioTorneos;
        this.todosTorneos = todosTorneos;
    }

    /**
     * Muestra el menú principal de torneos para clientes.
     */
    public void mostrarMenuTorneos() {
        boolean seguir = true;
        
        while (seguir) {
            System.out.println("\n========== MENÚ DE TORNEOS ==========");
            System.out.println("1. Ver todos los torneos disponibles");
            System.out.println("2. Ver mis inscripciones");
            System.out.println("3. Registrarme a un torneo");
            System.out.println("4. Retirarme de un torneo");
            System.out.println("5. Ver mis vouchers de descuento");
            System.out.println("6. Volver al menú anterior");
            System.out.println("=====================================");
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
                    verVouchers();
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
            System.out.println("   Cuota de entrada: $" + torneo.getMontoEntrada());
            System.out.println("   Premio total: $" + torneo.getPremioTotal());
        }
        
        // Indicar si el cliente es fan del juego
        if (cliente.getJuegosFavoritos().stream().anyMatch(id -> id.equals(torneo.getIdJuego()))) {
            System.out.println("   ⭐ Eres fan de este juego - tienes spot reservado");
        }
    }

    /**
     * Muestra los torneos en los que el cliente está inscrito.
     */
    private void mostrarMisTorneos() {
        List<Torneo> misTorneos = servicioTorneos.obtenerTorneosDelUsuario(cliente.getId());
        
        if (misTorneos.isEmpty()) {
            System.out.println("\n❌ No estás inscrito en ningún torneo.");
            return;
        }
        
        System.out.println("\n========== MIS INSCRIPCIONES ==========");
        for (Torneo torneo : misTorneos) {
            ParticipanteTorneo participante = torneo.obtenerParticipante(cliente.getId());
            System.out.println("🎮 " + torneo.getNombre() + " (" + torneo.getTipo().getNombre() + ")");
            System.out.println("   Juego: " + torneo.getNombreJuego());
            System.out.println("   Día: " + ValidadorTorneo.obtenerNombreDiaSemana(torneo.getDiaSemana()));
            System.out.println("   Estado: " + torneo.getEstado().getDescripcion());
            
            if (participante.gano()) {
                System.out.println("   ✅ GANASTE - Premio/Descuento: $" + participante.getPremioODescuento());
            }
            System.out.println();
        }
    }

    /**
     * Permite que el cliente se registre a un torneo.
     */
    private void registrarseATorneo() {
        List<Torneo> abiertos = servicioTorneos.obtenerTorneosAbiertos();
        
        if (abiertos.isEmpty()) {
            System.out.println("\n❌ No hay torneos disponibles para inscribirse.");
            return;
        }
        
        System.out.println("\n========== TORNEOS DISPONIBLES PARA INSCRIPCIÓN ==========");
        for (int i = 0; i < abiertos.size(); i++) {
            System.out.println((i + 1) + ". " + abiertos.get(i).getNombre() + " - " + abiertos.get(i).getNombreJuego());
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

            System.out.print("Cantidad de cupos a reservar (1-3): ");
            int cupos = Integer.parseInt(scanner.nextLine().trim());
            if (cupos < 1 || cupos > 3) {
                System.out.println("❌ La cantidad debe estar entre 1 y 3.");
                return;
            }

            int registrados = 0;
            ResultadoValidacion ultimoResultado = null;
            for (int i = 0; i < cupos; i++) {
                ultimoResultado = servicioTorneos.registrarUsuarioATorneo(
                        torneo.getId(), cliente, todosTorneos
                );
                if (!ultimoResultado.esValido()) {
                    break;
                }
                registrados++;
            }

            if (registrados == cupos) {
                System.out.println("✅ Cupos reservados exitosamente: " + registrados);
            } else if (registrados > 0) {
                System.out.println("✅ Cupos reservados: " + registrados);
                System.out.println("❌ No se pudieron reservar los demas: " + ultimoResultado.getMensaje());
            } else {
                System.out.println("❌ " + (ultimoResultado != null ? ultimoResultado.getMensaje() : "No se pudo registrar al torneo."));
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Entrada inválida. Debes ingresar un número.");
        }
    }

    /**
     * Permite que el cliente se retire de un torneo.
     */
    private void retirarseDelTorneo() {
        List<Torneo> misTorneos = servicioTorneos.obtenerTorneosDelUsuario(cliente.getId());
        
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
                    torneo.getId(), cliente
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
     * Muestra los vouchers de descuento del cliente.
     */
    private void verVouchers() {
        List<VoucherDescuento> vouchers = servicioTorneos.obtenerVouchersDelUsuario(cliente.getId());
        
        if (vouchers.isEmpty()) {
            System.out.println("\n❌ No tienes vouchers de descuento.");
            return;
        }
        
        System.out.println("\n========== MIS VOUCHERS DE DESCUENTO ==========");
        for (int i = 0; i < vouchers.size(); i++) {
            VoucherDescuento voucher = vouchers.get(i);
            System.out.println((i + 1) + ". " + voucher.getNombreTorneo());
            System.out.println("   Descuento: $" + voucher.getMontoDescuento());
            System.out.println("   Otorgado: " + voucher.getFechaOtorgamiento());
            System.out.println("   Vencimiento: " + (voucher.getFechaVencimiento() != null ? voucher.getFechaVencimiento() : "No vence"));
            System.out.println("   Estado: " + (voucher.isUsado() ? "USADO" : "DISPONIBLE"));
            System.out.println();
        }
    }
}
