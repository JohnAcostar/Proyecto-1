package test;

import java.time.LocalTime;

import modelo.Administrador;
import modelo.Cafe;
import modelo.Cliente;
import modelo.Mesa;
import modelo.Reserva;
import modelo.ResultadoValidacion;
import service.ServicioReservas;

/**
 * Demo 1: Gestión de Reservas
 * Valida creación, aceptación y rechazo de reservas con capacidad del café.
 */
public class DemoPrueba1GestionReservas {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║ Demo 1: Gestión de Reservas          ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        // Crear café con mesas
        Administrador admin = new Administrador("admin", "admin123", "ADM001");
        Cafe cafe = new Cafe("CAFE-001", 30, LocalTime.of(8, 0), LocalTime.of(22, 0), admin);

        Mesa mesa1 = new Mesa("M1", 4, false, false);
        Mesa mesa2 = new Mesa("M2", 6, false, false);
        Mesa mesa3 = new Mesa("M3", 2, false, true); // Con jóvenes

        cafe.agregarMesa(mesa1);
        cafe.agregarMesa(mesa2);
        cafe.agregarMesa(mesa3);

        System.out.println("✓ Café configurado con 3 mesas");
        System.out.println("  Capacidad máxima: " + cafe.getCapacidadMaxima() + " personas");
        System.out.println("  " + cafe.getEstadoOcupacion() + "\n");

        // Servicio de reservas
        ServicioReservas servicioReservas = new ServicioReservas(cafe);
        Cliente cliente1 = new Cliente("juan", "pass", "CLI001", "CODIGO01");
        Cliente cliente2 = new Cliente("maria", "pass", "CLI002", "CODIGO02");

        // Test 1: Crear reserva válida
        System.out.println("─── TEST 1: Crear reserva de 4 personas (mesa disponible) ───");
        ResultadoValidacion res1 = servicioReservas.crearReserva(4, false, false, cliente1);
        System.out.println(res1.toString());
        System.out.println("  " + cafe.getEstadoOcupacion() + "\n");

        // Test 2: Crear otra reserva
        System.out.println("─── TEST 2: Crear reserva de 6 personas ───");
        ResultadoValidacion res2 = servicioReservas.crearReserva(6, false, false, cliente2);
        System.out.println(res2.toString());
        System.out.println("  " + cafe.getEstadoOcupacion() + "\n");

        // Test 3: Intentar reservar más que capacidad disponible
        System.out.println("─── TEST 3: Intentar reservar 25 personas (excede capacidad) ───");
        ResultadoValidacion res3 = servicioReservas.crearReserva(25, false, false, cliente1);
        System.out.println(res3.toString() + "\n");

        // Test 4: Listar reservas activas
        System.out.println("─── TEST 4: Reservas activas ───");
        System.out.println("Total: " + servicioReservas.getReservasActivas().size());
        for (Reserva r : servicioReservas.getReservasActivas()) {
            System.out.println("  • Reserva: " + r.getCantidadPersonas() + " personas, Estado: " + r.getEstado());
        }

        System.out.println("\n✓ Demo 1 completada\n");
    }
}
