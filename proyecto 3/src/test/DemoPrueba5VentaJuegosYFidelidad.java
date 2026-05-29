package test;

import modelo.CategoriaJuego;
import modelo.Cliente;
import modelo.JuegoDeMesa;
import modelo.RestriccionEdad;
import modelo.VentaJuegos;

import service.politicas.PoliticaFidelidad;
import service.politicas.PoliticaFidelidad.ModoCalculo;

/**
 * Demo 5: Venta de Juegos y Fidelización
 * Valida descuentos, IVA y acumulación de puntos.
 */
public class DemoPrueba5VentaJuegosYFidelidad {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║ Demo 5: Venta Juegos y Fidelización  ║");
        System.out.println("╚═══════════════════════════════════════╝\n");

        // Crear cliente
        Cliente cliente = new Cliente("juan", "pass", "CLI001", "CODIGO01");
        System.out.println("✓ Cliente: " + cliente.getLogin());
        System.out.println("  Puntos iniciales: " + cliente.holaPuntosActuales() + "\n");

        // Crear juego para venta
        JuegoDeMesa catan = new JuegoDeMesa("CAT001", "Catán", 1995, "Catan Studio",
                3, 4, RestriccionEdad.MAYORES_DE_5, CategoriaJuego.TABLERO, modelo.EstadoJuego.BUENO, false);

        System.out.println("✓ Producto a vender: " + catan.getNombre() + "\n");

        // Test 1: Venta sin descuento
        System.out.println("─── TEST 1: Venta sin descuento ───");
        double precioCatan = 80000;
        VentaJuegos venta1 = new VentaJuegos("VENTA-J001", precioCatan, cliente);
        venta1.calcularSubtotal();
        venta1.calcularTotal();

        System.out.println("Producto: " + catan.getNombre());
        System.out.println("Subtotal: $" + venta1.getSubtotal());
        System.out.println("IVA (19%): $" + venta1.getImpuesto());
        System.out.println("TOTAL: $" + venta1.getTotal());
        System.out.println("Puntos ganados: " + venta1.calcularPuntos() + "\n");

        // Test 2: Política de fidelidad - Modo FIJO
        System.out.println("─── TEST 2: Política de fidelidad (FIJO) ───");
        PoliticaFidelidad politicaFija = new PoliticaFidelidad(ModoCalculo.FIJO, 1.0); // 1 peso = 1 punto
        int puntosGanados = politicaFija.calcularPuntos(venta1, venta1.getSubtotal());
        System.out.println("Modo: " + politicaFija.describeModo());
        System.out.println("Puntos ganados: " + puntosGanados);
        cliente.acumulaPuntos(puntosGanados);
        System.out.println("Puntos del cliente: " + cliente.holaPuntosActuales() + "\n");

        // Test 3: Canje de puntos
        System.out.println("─── TEST 3: Canje de puntos ───");
        int puntosACanjar = 50000;
        System.out.println("Puntos disponibles: " + cliente.holaPuntosActuales());
        System.out.println("Intentando canjear: " + puntosACanjar + " puntos");
        cliente.canjeaPuntos(puntosACanjar);
        System.out.println("Puntos después del canje: " + cliente.holaPuntosActuales() + "\n");

        // Test 4: Política de fidelidad - Modo PORCENTAJE
        System.out.println("─── TEST 4: Política de fidelidad (PORCENTAJE) ───");
        PoliticaFidelidad politicaPorcentaje = new PoliticaFidelidad(ModoCalculo.PORCENTAJE, 0.01); // 1% de la compra
        int puntosPorcentaje = politicaPorcentaje.calcularPuntos(venta1, venta1.getSubtotal());
        System.out.println("Modo: " + politicaPorcentaje.describeModo());
        System.out.println("Puntos ganados: " + puntosPorcentaje + "\n");

        System.out.println("✓ Demo 5 completada\n");
    }
}
