# 🏆 INTEGRACIÓN DEL SISTEMA DE RECOMPENSAS DE TORNEOS

## Descripción General

Los ganadores de torneos reciben recompensas que deben integrarse con el sistema existente:
- **Torneos Amistosos**: Descuentos en vouchers
- **Torneos Competitivos**: Premios en efectivo

## 1. ALMACENAR VOUCHERS EN USUARIOS

### Opción A: Agregar a Cliente (Recomendado)

Modificar la clase `Cliente.java`:

```java
public class Cliente extends UsuarioBasico {
    // ... campos existentes ...
    
    private List<VoucherDescuento> vouchersDescuento;
    
    public Cliente(String id, String nombre, String email, String contraseña) {
        super(id, nombre, email, contraseña);
        this.vouchersDescuento = new ArrayList<>();
    }
    
    public void agregarVoucher(VoucherDescuento voucher) {
        this.vouchersDescuento.add(voucher);
    }
    
    public void removerVoucher(VoucherDescuento voucher) {
        this.vouchersDescuento.remove(voucher);
    }
    
    public List<VoucherDescuento> getVouchersValidos() {
        return vouchersDescuento.stream()
            .filter(v -> v.esValido())
            .collect(Collectors.toList());
    }
    
    public VoucherDescuento obtenerVoucher(int id) {
        return vouchersDescuento.stream()
            .filter(v -> v.getId() == id)
            .findFirst()
            .orElse(null);
    }
}
```

### Opción B: Usar ServicioTorneos centralmente (También válido)

Mantener vouchers en `ServicioTorneos` como sistema central:
- `obtenerVouchersDelUsuario(idUsuario)`
- Más simple de sincronizar

## 2. APLICAR VOUCHERS EN VENTAS

### Modificar ServicioInventario o crear nuevo método

```java
/**
 * Aplica un voucher de descuento a una venta
 */
public ResultadoValidacion aplicarVoucherAVenta(int idVoucher, Venta venta, 
                                                ServicioTorneos servicioTorneos,
                                                Cliente cliente) {
    
    VoucherDescuento voucher = servicioTorneos.obtenerVoucherPorId(idVoucher);
    
    if (voucher == null) {
        return new ResultadoValidacion(false, "Voucher no encontrado");
    }
    
    if (!voucher.esValido()) {
        return new ResultadoValidacion(false, "El voucher no es válido o está vencido");
    }
    
    if (voucher.getIdUsuario() != cliente.getId()) {
        return new ResultadoValidacion(false, "Este voucher no te pertenece");
    }
    
    // Aplicar descuento
    double montoDescuento = voucher.getMontoDescuento();
    double montoActual = venta.obtenerMontoTotal();
    
    if (montoDescuento > montoActual) {
        // Descuento más grande que la venta: aplicar completo y crear crédito
        venta.setMontoTotal(0);
        voucher.marcarComoUsado();
        
        // Crear voucher con saldo restante
        VoucherDescuento voucherRestante = new VoucherDescuento(
            -1, // ID será asignado por el servicio
            cliente.getId(),
            voucher.getIdTorneo(),
            voucher.getNombreTorneo(),
            montoDescuento - montoActual,
            30
        );
        servicioTorneos.getVouchers().add(voucherRestante);
        
        return new ResultadoValidacion(true,
            "Descuento aplicado $" + montoDescuento + ". Saldo restante: $" +
            (montoDescuento - montoActual));
    } else {
        // Descuento normal
        venta.setMontoTotal(montoActual - montoDescuento);
        voucher.marcarComoUsado();
        
        return new ResultadoValidacion(true,
            "Descuento aplicado exitosamente. Nuevo total: $" + venta.obtenerMontoTotal());
    }
}
```

### En el menú de ventas (cliente):

```java
// En la clase de gestión de ventas del cliente

private ServicioTorneos servicioTorneos;

private void procesarVenta(Venta venta, Cliente cliente) {
    // ... lógica de venta existente ...
    
    // Mostrar vouchers disponibles
    List<VoucherDescuento> vouchers = servicioTorneos.obtenerVouchersDelUsuario(cliente.getId());
    
    if (!vouchers.isEmpty()) {
        System.out.println("\n¿Deseas aplicar un descuento?");
        System.out.println("Tienes " + vouchers.size() + " voucher(s) disponibles:");
        
        for (int i = 0; i < vouchers.size(); i++) {
            VoucherDescuento v = vouchers.get(i);
            System.out.println((i+1) + ". " + v.getNombreTorneo() + " - $" + v.getMontoDescuento());
        }
        
        System.out.print("Selecciona un voucher (0 para no usar): ");
        int seleccion = Integer.parseInt(scanner.nextLine().trim());
        
        if (seleccion > 0 && seleccion <= vouchers.size()) {
            VoucherDescuento voucherElegido = vouchers.get(seleccion - 1);
            ResultadoValidacion resultado = aplicarVoucherAVenta(
                voucherElegido.getId(), venta, servicioTorneos, cliente
            );
            
            if (resultado.esValido()) {
                System.out.println("✅ " + resultado.getMensaje());
            } else {
                System.out.println("❌ " + resultado.getMensaje());
            }
        }
    }
    
    // Procesar pago...
}
```

## 3. REGISTRAR PREMIOS EN EFECTIVO

### Crear reporte de premios

```java
public class RegistroPremioTorneo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private int idTorneo;
    private String nombreTorneo;
    private int idGanador;
    private String nombreGanador;
    private double montoGanador;
    private LocalDate fechaPremio;
    private boolean pagado;
    private LocalDate fechaPago;
    
    // getters, setters, constructores...
}
```

### Persistencia en archivo

Agregar a `FilePersistence.java`:

```java
private static final String PRIZES_FILE = "premios_torneo.txt";

// En load():
data.setPremiosTorneo(loadPremiosTorneo());

// En save():
savePremiosTorneo(data.getPremiosTorneo());

private List<RegistroPremioTorneo> loadPremiosTorneo() {
    // Implementar similar a loadTorneos()
}

private void savePremiosTorneo(List<RegistroPremioTorneo> premios) {
    // Implementar similar a saveTorneos()
}
```

### Registrar premio en finalizarTorneo()

```java
// En ServicioTorneos.finalizarTorneo():

if (torneo.getTipo() == TipoTorneo.COMPETITIVO) {
    // Crear registro de premio
    RegistroPremioTorneo premio = new RegistroPremioTorneo(
        proximoIdPremio++,
        torneo.getId(),
        torneo.getNombre(),
        idGanador,
        ganador.getNombreUsuario(),
        montoGanador,
        LocalDate.now()
    );
    registrosPremios.add(premio);
}
```

## 4. INTERFAZ PARA VER/GESTIONAR PREMIOS

### Menú para Cliente

```java
private void verMisRecompensas() {
    List<VoucherDescuento> vouchers = servicioTorneos.obtenerVouchersDelUsuario(cliente.getId());
    double vouchersTotal = vouchers.stream()
        .filter(v -> v.esValido())
        .mapToDouble(VoucherDescuento::getMontoDescuento)
        .sum();
    
    System.out.println("\n========== MIS RECOMPENSAS ==========");
    System.out.println("Vouchers de descuento disponibles: $" + vouchersTotal);
    System.out.println("  - Cantidad: " + vouchers.size());
    System.out.println();
    
    // Premios en efectivo (si existen registros)
    // ...
}
```

### Menú para Admin (Ver todos los premios)

```java
private void verReportePremios() {
    List<RegistroPremioTorneo> premios = servicioTorneos.obtenerTodosPremios();
    
    System.out.println("\n========== REPORTE DE PREMIOS ==========");
    double totalPagado = premios.stream()
        .filter(p -> p.isPagado())
        .mapToDouble(RegistroPremioTorneo::getMontoGanador)
        .sum();
    
    double totalPendiente = premios.stream()
        .filter(p -> !p.isPagado())
        .mapToDouble(RegistroPremioTorneo::getMontoGanador)
        .sum();
    
    System.out.println("Total pagado: $" + totalPagado);
    System.out.println("Total pendiente: $" + totalPendiente);
    System.out.println("Total de premios: $" + (totalPagado + totalPendiente));
}
```

## 5. INTEGRACIÓN CON PUNTOS DE FIDELIDAD

### Opción: Agregar bonus de puntos al usar voucher

```java
// En aplicarVoucherAVenta():

// Dar bonus de puntos fidelidad
int puntosBonus = (int) (voucher.getMontoDescuento() / 10); // 1 punto cada 10 pesos
cliente.agregarPuntosDeFidelidad(puntosBonus);

return new ResultadoValidacion(true,
    "Descuento aplicado. Bonus: +" + puntosBonus + " puntos de fidelidad");
```

## 6. RESTRICCIONES Y REGLAS

### El voucher NO se puede combinar:
- ✅ No con otros vouchers
- ✅ No con promociones
- ✅ No con puntos de fidelidad (opcional)

### Implementar verificación:

```java
public ResultadoValidacion validarSinOtrosDescuentos(Venta venta) {
    if (venta.tieneDescuento()) {
        return new ResultadoValidacion(false,
            "Ya hay un descuento aplicado. Los vouchers no se pueden combinar.");
    }
    return new ResultadoValidacion(true, "Válido");
}
```

## 7. SINCRONIZACIÓN DE DATOS

### Después de finalizar torneo:

```java
// En la aplicación principal o al finalizar torneo:

ResultadoValidacion resultado = servicioTorneos.finalizarTorneo(idTorneo, idGanador);

if (resultado.esValido()) {
    // Actualizar AppData
    appData.setVouchersDescuento(servicioTorneos.obtenerTodoLosVouchers());
    
    // Si usas vouchers en Cliente:
    VoucherDescuento voucher = servicioTorneos.obtenerVoucherDelGanador(idGanador);
    Cliente ganador = (Cliente) appData.obtenerUsuario(idGanador);
    ganador.agregarVoucher(voucher);
    
    // Guardar
    filePersistence.save(appData);
}
```

## 8. EJEMPLO DE FLUJO COMPLETO

```java
// 1. Cliente gana un torneo amistoso
servicioTorneos.finalizarTorneo(1, idCliente);
// → Se crea VoucherDescuento automáticamente

// 2. Cliente ve sus vouchers
List<VoucherDescuento> vouchers = servicioTorneos.obtenerVouchersDelUsuario(idCliente);
// → Muestra $500 de descuento disponible

// 3. Cliente realiza una venta
Venta venta = new Venta(1000); // $1000 total

// 4. Aplica voucher a la venta
ResultadoValidacion resultado = aplicarVoucherAVenta(idVoucher, venta, servicioTorneos, cliente);
// → venta.montoTotal = 500
// → voucher marcado como usado

// 5. Procesar pago de $500
procesarPago(500);

// 6. Voucher ya no está disponible
List<VoucherDescuento> vouchersRestantes = servicioTorneos.obtenerVouchersDelUsuario(idCliente);
// → Lista vacía
```

## 9. ARCHIVOS DE PERSISTENCIA

### Actualizar AppData:

```java
public class AppData {
    private List<Torneo> torneos;
    private List<VoucherDescuento> vouchersDescuento;
    private List<RegistroPremioTorneo> premiosTorneo;  // Nuevo
    
    // getters y setters...
}
```

### Rutas de archivos:
- `data/torneos.txt` ✅ (Ya implementado)
- `data/vouchersDescuento.txt` ✅ (Ya implementado)
- `data/premios_torneo.txt` (Nuevo - Opcional)

## 10. TESTING DE RECOMPENSAS

```java
@Test
void testAplicarVoucherAVenta() {
    VoucherDescuento voucher = new VoucherDescuento(1, cliente.getId(), 1, 
                                                     "Torneo", 500, 30);
    Venta venta = new Venta(1000);
    
    ResultadoValidacion resultado = aplicarVoucherAVenta(voucher.getId(), venta,
                                                        servicioTorneos, cliente);
    
    assertTrue(resultado.esValido());
    assertEquals(500, venta.getMontoTotal());
    assertTrue(voucher.isUsado());
}
```

## Resumen de Integración

| Componente | Acción | Archivo |
|-----------|--------|---------|
| Torneo Amistoso | Crear Voucher | ServicioTorneos |
| Voucher | Aplicar a Venta | MenuTorneosCliente o Ventas |
| Venta | Descuento | ServicioInventario |
| Premio (Competitivo) | Registrar | ServicioTorneos |
| Reporte | Ver premios | MenuTorneosAdministrador |

---

**Estado**: Documento de integración
**Completado**: ✅ Sistema de torneos implementado
**Próximos pasos**: Integrar rewards con ventas existentes
