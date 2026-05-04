package test;

import modelo.Cliente;
import modelo.ProductoCafe;
import modelo.VentaCafe;
import service.politicas.PoliticaImpuestos;

/**
 * Demo 4: Ventas del Café
 * Valida cálculo de impuestos y propinas en ventas de café.
 */
public class DemoPrueba4VentasCafe {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║ Demo 4: Ventas del Café              ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        // Crear cliente
        Cliente cliente = new Cliente("maria", "pass", "CLI002", "CODIGO02");
        System.out.println("✓ Cliente: " + cliente.getLogin() + "\n");

        // Crear productos del café
        ProductoCafe cafe = new ProductoCafe("CAFE001", "Café Espresso", 5000);
        ProductoCafe arepa = new ProductoCafe("AREPA001", "Arepa queso", 8000);

        System.out.println("✓ Productos del café:");
        System.out.println("  • " + cafe.getNombre() + " → $" + cafe.getPrecioBase());
        System.out.println("  • " + arepa.getNombre() + " → $" + arepa.getPrecioBase() + "\n");

        // Test 1: Venta simple
        System.out.println("─── TEST 1: Venta de café con propina ───");
        double subtotal = cafe.getPrecioBase();
        VentaCafe venta1 = new VentaCafe("VENTA001", subtotal, 0.15, cliente); // 15% propina
        venta1.calcularSubtotal();
        venta1.calcularTotal();

        System.out.println("Subtotal: $" + venta1.getSubtotal());
        System.out.println("Propina (15%): $" + (venta1.getSubtotal() * 0.15));
        System.out.println("Impuesto (8%): $" + venta1.getImpuesto());
        System.out.println("TOTAL: $" + venta1.getTotal() + "\n");

        // Test 2: Venta combinada
        System.out.println("─── TEST 2: Venta de múltiples productos ───");
        double subtotal2 = cafe.getPrecioBase() + arepa.getPrecioBase();
        VentaCafe venta2 = new VentaCafe("VENTA002", subtotal2, 0.10, cliente); // 10% propina
        venta2.calcularSubtotal();
        venta2.calcularTotal();

        System.out.println("Café: $" + cafe.getPrecioBase());
        System.out.println("Arepa: $" + arepa.getPrecioBase());
        System.out.println("Subtotal: $" + venta2.getSubtotal());
        System.out.println("Propina (10%): $" + (venta2.getSubtotal() * 0.10));
        System.out.println("Impuesto (8%): $" + venta2.getImpuesto());
        System.out.println("TOTAL: $" + venta2.getTotal() + "\n");

        // Test 3: Política de impuestos
        System.out.println("─── TEST 3: Política de impuestos ───");
        PoliticaImpuestos politica = new PoliticaImpuestos();
        System.out.println("Impuesto consumo café: " + (politica.getImpuestoConsumoCafe() * 100) + "%");
        System.out.println("IVA juegos: " + (politica.getIvaJuegos() * 100) + "%");
        double impuestoCafe = politica.calcularImpuesto(venta1, venta1.getSubtotal());
        System.out.println("Impuesto calculado para venta: $" + impuestoCafe + "\n");

        System.out.println("✓ Demo 4 completada\n");
    }
}
