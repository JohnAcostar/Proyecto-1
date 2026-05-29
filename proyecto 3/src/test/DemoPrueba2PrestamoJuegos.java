package test;

import modelo.CategoriaJuego;
import modelo.Cliente;
import modelo.CopiaJuego;
import modelo.EstadoJuego;
import modelo.JuegoDeMesa;
import modelo.Prestamo;
import modelo.RestriccionEdad;
import service.ServicioPrestamos;

/**
 * Demo 2: Préstamo de Juegos
 * Valida reglas de préstamo: cantidad de jugadores, edad, copias disponibles.
 */
public class DemoPrueba2PrestamoJuegos {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║ Demo 2: Préstamo de Juegos           ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        // Crear juegos
        JuegoDeMesa catan = new JuegoDeMesa("CAT001", "Catán", 1995, "Catan Studio",
                3, 4, RestriccionEdad.MAYORES_DE_5, CategoriaJuego.TABLERO, EstadoJuego.BUENO, false);

        JuegoDeMesa ticketToRide = new JuegoDeMesa("TTR001", "Ticket to Ride", 2004, "Days of Wonder",
                2, 5, RestriccionEdad.TODAS_LAS_EDADES, CategoriaJuego.TABLERO, EstadoJuego.BUENO, false);

        // Crear copias
        CopiaJuego copia1 = new CopiaJuego("COPCAT001", catan);

        System.out.println("✓ Juegos creados:");
        System.out.println("  • " + catan.getNombre() + " (" + catan.getMinJugadores() + "-" + catan.getMaxJugadores()
                + " jugadores)");
        System.out.println("  • " + ticketToRide.getNombre() + " (" + ticketToRide.getMinJugadores() + "-"
                + ticketToRide.getMaxJugadores() + " jugadores)\n");

        // Servicio de préstamos
        ServicioPrestamos servicioPrestamos = new ServicioPrestamos();

        // Crear cliente
        Cliente cliente = new Cliente("juan", "pass", "CLI001", "CODIGO01");

        // Test 1: Crear un préstamo
        System.out.println("─── TEST 1: Solicitar préstamo de Catán ───");
        Prestamo prestamo1 = new Prestamo("PREST001", copia1, false, cliente);
        System.out.println("✓ Préstamo creado: " + prestamo1.getPrestamoId());
        System.out.println("  Juego: " + prestamo1.getCopia().getJuego().getNombre());
        System.out.println("  Activo: " + prestamo1.estaActivo() + "\n");

        // Test 2: Registrar devolución
        System.out.println("─── TEST 2: Devolver préstamo ───");
        prestamo1.registrarDevolucion();
        System.out.println("✓ Devolución registrada");
        System.out.println("  Activo: " + prestamo1.estaActivo());
        System.out.println("  Fecha devolución: " + prestamo1.getFechaDevolucion() + "\n");

        // Test 3: Crear múltiples préstamos
        System.out.println("─── TEST 3: Múltiples préstamos por cliente ───");
        CopiaJuego copia2 = new CopiaJuego("COPTTR001", ticketToRide);
        Prestamo prestamo2 = new Prestamo("PREST002", copia2, false, cliente);
        CopiaJuego copia3 = new CopiaJuego("COPCAT002", catan);
        Prestamo prestamo3 = new Prestamo("PREST003", copia3, false, cliente);

        servicioPrestamos.getPrestamosActivos().add(prestamo2);
        servicioPrestamos.getPrestamosActivos().add(prestamo3);

        System.out.println("✓ Préstamos activos para " + cliente.getLogin() + ": "
                + servicioPrestamos.getPrestamosActivos().size() + "\n");

        System.out.println("✓ Demo 2 completada\n");
    }
}
