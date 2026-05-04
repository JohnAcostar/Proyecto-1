package test;

import modelo.CategoriaJuego;
import modelo.Cliente;
import modelo.CopiaJuego;
import modelo.EstadoJuego;
import modelo.JuegoDeMesa;
import modelo.Prestamo;
import modelo.RestriccionEdad;

/**
 * Demo 3: Juegos Difíciles y Préstamos por Empleados
 * Valida reglas especiales para juegos de categoría DIFICIL.
 */
public class DemoPrueba3JuegosDificulYEmpleados {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║ Demo 3: Juegos Difíciles & Empleados ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        // Crear juego difícil
        JuegoDeMesa agricola = new JuegoDeMesa("AGR001", "Agrícola", 2007, "Zman Games",
                1, 4, RestriccionEdad.MAYORES_DE_5, CategoriaJuego.TABLERO, EstadoJuego.BUENO, true);

        System.out.println("✓ Juego Difícil creado: " + agricola.getNombre());
        System.out.println("  Categoría: " + agricola.getCategoria());
        System.out.println("  Dificultad: SÍ\n");

        // Crear copia
        CopiaJuego copia = new CopiaJuego("COPAGR001", agricola);

        // Test 1: Préstamo por cliente normal
        System.out.println("─── TEST 1: Préstamo por cliente ───");
        Cliente cliente = new Cliente("juan", "pass", "CLI001", "CODIGO01");
        Prestamo prestamo1 = new Prestamo("PREST-D001", copia, false, cliente);
        System.out.println("✓ Préstamo creado a: " + cliente.getLogin());
        System.out.println("  Juego: " + prestamo1.getCopia().getJuego().getNombre());
        System.out.println("  Advertencia de dificultad: " + prestamo1.tieneAdvertencia() + "\n");

        // Test 2: Simular préstamo con advertencia (sin mesero capacitado)
        System.out.println("─── TEST 2: Préstamo con advertencia (sin mesero capacitado) ───");
        CopiaJuego copia2 = new CopiaJuego("COPAGR002", agricola);
        Prestamo prestamo2 = new Prestamo("PREST-D002", copia2, true, cliente);
        System.out.println("✓ Préstamo con advertencia");
        System.out.println("  ⚠ ADVERTENCIA: Juego difícil sin mesero capacitado");
        System.out.println("  Advertencia registrada: " + prestamo2.tieneAdvertencia() + "\n");

        // Test 3: Información de préstamo
        System.out.println("─── TEST 3: Detalles del préstamo ───");
        System.out.println("Préstamo ID: " + prestamo2.getPrestamoId());
        System.out.println("Juego: " + prestamo2.getCopia().getJuego().getNombre());
        System.out.println("Usuario: " + prestamo2.getUsuario().getLogin());
        System.out.println("Fecha: " + prestamo2.getFechaPrestamo());
        System.out.println("Activo: " + prestamo2.estaActivo() + "\n");

        System.out.println("✓ Demo 3 completada\n");
    }
}
