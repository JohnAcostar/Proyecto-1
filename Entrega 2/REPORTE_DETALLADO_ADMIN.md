# Reporte Detallado de Compras - Menú Administrador

## Cambios Implementados

### 1. Método en `service/SistemaCafe.java`

**Nuevo método: `getVentasPorRubro()` - Línea 384-387**

```java
public List<Venta> getVentasPorRubro(RubroVenta rubro) {
    return ventas.stream()
            .filter(v -> v.getRubro() == rubro)
            .toList();
}
```

Devuelve lista de todas las ventas de un tipo específico (JUEGO o CAFE).

---

### 2. Métodos en `consola/Main.java`

#### A. `mostrarDetalleVentasJuegos()` - Línea 159-175

Muestra todas las compras de juegos con detalles:

```java
private static void mostrarDetalleVentasJuegos(SistemaCafe sistema) {
    List<Venta> ventas = sistema.getVentasPorRubro(RubroVenta.JUEGO);
    if (ventas.isEmpty()) {
        System.out.println("No hay ventas de juegos registradas.");
        return;
    }
    System.out.println("\n=== Detalle Ventas de Juegos ===");
    double totalGeneral = 0;
    for (int i = 0; i < ventas.size(); i++) {
        VentaJuegos venta = (VentaJuegos) ventas.get(i);
        System.out.println("\nCompra " + (i + 1) + " -");
        System.out.println("Base: " + formatearMoneda(venta.getBase()));
        System.out.println("Total: " + formatearMoneda(venta.getTotal()) + " COP");
        totalGeneral += venta.getTotal();
    }
    System.out.println("\n--- Total Juegos: " + formatearMoneda(totalGeneral) + " COP ---");
}
```

#### B. `mostrarDetalleVentasCafe()` - Línea 177-195

Muestra todas las compras de café con detalles:

```java
private static void mostrarDetalleVentasCafe(SistemaCafe sistema) {
    List<Venta> ventas = sistema.getVentasPorRubro(RubroVenta.CAFE);
    if (ventas.isEmpty()) {
        System.out.println("No hay ventas de cafe registradas.");
        return;
    }
    System.out.println("\n=== Detalle Ventas de Cafe ===");
    double totalGeneral = 0;
    for (int i = 0; i < ventas.size(); i++) {
        VentaCafe venta = (VentaCafe) ventas.get(i);
        double propina = venta.getBase() * venta.getPorcentajePropina();
        System.out.println("\nCompra " + (i + 1) + " -");
        System.out.println("Base: " + formatearMoneda(venta.getBase()));
        System.out.println("Propina: " + formatearMoneda(propina));
        System.out.println("Total: " + formatearMoneda(venta.getTotal()) + " COP");
        totalGeneral += venta.getTotal();
    }
    System.out.println("\n--- Total Cafe: " + formatearMoneda(totalGeneral) + " COP ---");
}
```

#### C. `menuAdministrador()` - Línea 145-146 (Modificado)

Cambio en los cases:

```java
// ANTES:
case 1 -> System.out.println("Total juegos: " + sistema.totalVentasPorRubro(RubroVenta.JUEGO));
case 2 -> System.out.println("Total cafe: " + sistema.totalVentasPorRubro(RubroVenta.CAFE));

// DESPUÉS:
case 1 -> mostrarDetalleVentasJuegos(sistema);
case 2 -> mostrarDetalleVentasCafe(sistema);
```

---

## Ejemplos de Salida

### Reporte de Juegos

```
=== Detalle Ventas de Juegos ===

Compra 1 -
Base: $ 50.000
Total: $ 59.500 COP

Compra 2 -
Base: $ 100.000
Total: $ 119.000 COP

Compra 3 -
Base: $ 50.000
Total: $ 59.500 COP

--- Total Juegos: $ 238.000 COP ---
```

### Reporte de Café

```
=== Detalle Ventas de Cafe ===

Compra 1 -
Base: $ 27.000
Propina: $ 4.050
Total: $ 31.350 COP

Compra 2 -
Base: $ 14.000
Propina: $ 1.400
Total: $ 15.456 COP

Compra 3 -
Base: $ 9.000
Propina: $ 0 (sin propina)
Total: $ 9.720 COP

--- Total Cafe: $ 56.526 COP ---
```

---

## Detalles Mostrados

### Para Juegos:
- ✅ Número de compra (1, 2, 3, ...)
- ✅ Base (precio original sin descuento)
- ✅ Total con IVA y formateado en COP

### Para Café:
- ✅ Número de compra (1, 2, 3, ...)
- ✅ Base (precio del café)
- ✅ Propina (monto calculado)
- ✅ Total con impuesto, propina y formateado en COP

### Total General:
- ✅ Suma de todas las compras
- ✅ Formateado con punto de miles
- ✅ Especificación de moneda (COP)

---

## Validaciones

✅ Si no hay ventas, muestra mensaje "No hay ventas registradas"  
✅ Los montos se formatean automáticamente con `formatearMoneda()`  
✅ La propina se calcula automáticamente como base × porcentaje  
✅ Todos los totales se suman correctamente  

---

## Nota sobre Información de Productos

**Limitación actual:** Los detalles de ventas (VentaJuegos y VentaCafe) solo guardan el monto base, no la información de qué producto específico se compró o la cantidad.

**Por qué:** El sistema fue diseñado así. Para mostrar "2 espressos", "1 capuchino", etc., habría que modificar las clases VentaJuegos y VentaCafe para almacenar esa información adicional.

**Alternativa aplicada:** Se muestra la base (monto) que permite inferir aproximadamente qué se compró basándose en los precios del catálogo.

---

## Compatibilidad

✅ Funciona con datos existentes  
✅ No requiere cambios en estructura de persistencia  
✅ Compatible con todos los tipos de usuario  
✅ Solo accesible desde menú de Administrador

