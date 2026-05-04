package test;

import java.time.LocalDate;

import modelo.Administrador;
import modelo.CategoriaJuego;
import modelo.Cliente;
import modelo.JuegoDeMesa;
import modelo.ReporteVentas;
import modelo.RestriccionEdad;
import modelo.RubroVenta;
import modelo.VentaJuegos;
import service.ServicioReportes;

/**
 * Demo 7: Funciones Administrativas
 * Valida generación de reportes, inventario y funciones de administrador.
 */
public class DemoPrueba7FuncionesAdministrativas {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║ Demo 7: Funciones Administrativas    ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        // Crear administrador
        Administrador admin = new Administrador("admin", "admin123", "ADM001");
        System.out.println("✓ Administrador: " + admin.getLogin() + "\n");

        // Crear juegos
        JuegoDeMesa catan = new JuegoDeMesa("CAT001", "Catán", 1995, "Catan Studio",
                3, 4, RestriccionEdad.MAYORES_DE_5, CategoriaJuego.TABLERO, modelo.EstadoJuego.BUENO, false);

        JuegoDeMesa monopoly = new JuegoDeMesa("MON001", "Monopoly", 1935, "Hasbro",
                2, 8, RestriccionEdad.TODAS_LAS_EDADES, CategoriaJuego.TABLERO, modelo.EstadoJuego.BUENO, false);

        System.out.println("✓ Juegos en catálogo:");
        System.out.println("  • " + catan.getNombre());
        System.out.println("  • " + monopoly.getNombre() + "\n");

        // Test 1: Crear ventas para reporte
        System.out.println("─── TEST 1: Crear ventas de juegos ───");
        Cliente cliente1 = new Cliente("juan", "pass", "CLI001", "CODIGO01");
        Cliente cliente2 = new Cliente("maria", "pass", "CLI002", "CODIGO02");

        VentaJuegos venta1 = new VentaJuegos("VENTA-J001", 80000, cliente1);
        venta1.calcularSubtotal();
        venta1.calcularTotal();

        VentaJuegos venta2 = new VentaJuegos("VENTA-J002", 120000, cliente2);
        venta2.calcularSubtotal();
        venta2.calcularTotal();

        System.out.println("✓ Ventas registradas:");
        System.out.println("  • Venta 1: $" + venta1.getTotal());
        System.out.println("  • Venta 2: $" + venta2.getTotal() + "\n");

        // Test 2: Servicio de reportes
        System.out.println("─── TEST 2: Generar reporte por rubro ───");
        ServicioReportes servicioReportes = new ServicioReportes();

        java.util.List<modelo.Venta> ventasList = new java.util.ArrayList<>();
        ventasList.add(venta1);
        ventasList.add(venta2);
        servicioReportes.cargarVentas(ventasList);

        ReporteVentas reporteJuegos = servicioReportes.generarReportePorRubro(RubroVenta.JUEGO,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));

        System.out.println("✓ Reporte generado: " + reporteJuegos.getNombre());
        System.out.println("  Ventas en reporte: " + reporteJuegos.getVentas().size() + "\n");

        // Test 3: Ingresos totales
        System.out.println("─── TEST 3: Ingresos totales del período ───");
        double ingresoTotal = servicioReportes.obtenerIngresosTotal(LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(1));
        System.out.println("✓ Ingresos totales: $" + ingresoTotal + "\n");

        // Test 4: Información del administrador
        System.out.println("─── TEST 4: Información del administrador ───");
        System.out.println("✓ Datos del admin:");
        System.out.println("  ID: " + admin.getId());
        System.out.println("  Login: " + admin.getLogin());
        System.out.println("  Tipo: Administrador");
        System.out.println("  Funciones:");
        System.out.println("    • Acceso a reportes");
        System.out.println("    • Gestión de inventario");
        System.out.println("    • Aprobación de solicitudes\n");

        System.out.println("✓ Demo 7 completada\n");
    }
}
