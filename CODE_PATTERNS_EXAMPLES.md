# BoardGameCafe - Code Patterns & Examples

## 1. CREATING ENTITIES

### Creating a Usuario (Via SistemaCafe)
```java
// Create a basic user
boolean creado = sistema.crearUsuarioBasico("newuser", "password123");

// Create through direct instantiation (for testing/initialization)
Cliente cliente = new Cliente("john", "pass123", "C-02", "VIP10");
Mesero mesero = new Mesero("carlos", "pass456", "M-05", "Carlos García", "EMP20");
Administrador admin = new Administrador("superadmin", "admin999", "A-02");
```

### Creating a JuegoDeMesa
```java
JuegoDeMesa nuevoJuego = new JuegoDeMesa(
    "J-11",                              // ID
    "Carcassonne",                       // Name
    2000,                                // Year
    "Z-Man Games",                       // Publisher
    2, 5,                                // Min-max players
    RestriccionEdad.MAYORES_DE_5,        // Age restriction
    CategoriaJuego.TABLERO,              // Category
    EstadoJuego.NUEVO,                   // State
    false                                // Difficult
);
nuevoJuego.setPrecioVenta(45000);

// Add to system
sistema.agregarJuegoAlCatalogo(nuevoJuego);
```

### Creating Copies for Inventory
```java
// Create rental copies
for (int i = 0; i < 3; i++) {
    CopiaJuego copia = new CopiaJuego("P-" + (nextId++), juego);
    sistema.getInventarioPrestamos().agregarCopia(copia);
}

// Create sale copies
for (int i = 0; i < 2; i++) {
    CopiaJuego copia = new CopiaJuego("V-" + (nextId++), juego);
    sistema.getInventarioVenta().agregarCopia(copia);
}
```

---

## 2. PERFORMING OPERATIONS WITH VALIDATORS

### Requesting a Loan (Proper Pattern)
```java
public void solicitarPrestamo(SistemaCafe sistema, Cliente cliente, 
                              JuegoDeMesa juego, Cafe cafe, Mesa mesa) {
    // 1. Call service which validates internally
    ResultadoValidacion resultado = sistema.getServicioPrestamos()
        .solicitarPrestamo(juego, cafe, mesa, cliente, "PREST-001");
    
    // 2. Check result
    if (!resultado.esValido()) {
        System.out.println("Error: " + resultado.getMensaje());
        System.out.println("Código: " + resultado.getCodigoError());
        return;
    }
    
    // 3. Operation succeeded
    System.out.println(resultado.toString());  // Prints with checkmark
}
```

### Making a Reservation (With Validation)
```java
public void hacerReserva(SistemaCafe sistema, Cliente cliente,
                        int cantPersonas, boolean hayNinos, boolean hayJovenes) {
    // 1. Request reservation
    ResultadoValidacion resultado = sistema.getServicioReservas()
        .crearReserva(cantPersonas, hayNinos, hayJovenes, cliente);
    
    // 2. Handle result
    if (!resultado.esValido()) {
        System.out.println("No se pudo crear reserva: " + resultado.getMensaje());
        if ("ERR_MESA_NO_DISPONIBLE".equals(resultado.getCodigoError())) {
            System.out.println("Intenta reducir la cantidad de personas.");
        }
        return;
    }
    
    System.out.println("✓ Reserva creada exitosamente");
}
```

### Completing a Sale
```java
public void venderJuego(SistemaCafe sistema, Usuario usuario, 
                       JuegoDeMesa juego) {
    // 1. Create sale
    double precioBase = juego.getPrecioVenta();
    double descuento = 0; // Or 0.20 if employee
    
    VentaJuegos venta = new VentaJuegos("V-JUEGO-001", precioBase, usuario);
    venta.aplicarDescuento(descuento);
    
    // 2. Calculate
    venta.calcularSubtotal();
    venta.calcularTotal();
    int puntos = venta.calcularPuntos();
    
    // 3. Register
    usuario.agregarVenta(venta);
    if (usuario instanceof Cliente cliente) {
        cliente.acumularPuntos(puntos);
    }
    
    // 4. Update inventory
    CopiaJuego vendida = sistema.getInventarioVenta().vender(juego);
    if (vendida == null) {
        System.out.println("No hay stock disponible");
        return;
    }
    
    // 5. Report
    System.out.println("✓ Venta completada");
    System.out.println("  Subtotal: $" + venta.getSubtotal());
    System.out.println("  Impuesto: $" + venta.getImpuesto());
    System.out.println("  Total: $" + venta.getTotal());
    System.out.println("  Puntos ganados: " + puntos);
}
```

---

## 3. QUERYING & FILTERING DATA

### Finding Available Games
```java
public List<JuegoDeMesa> obtenerJuegosDisponibles(SistemaCafe sistema, 
                                                  int cantJugadores) {
    List<JuegoDeMesa> resultado = new ArrayList<>();
    
    for (JuegoDeMesa juego : sistema.getJuegosCatalogo()) {
        // Check if available
        if (sistema.getInventarioPrestamos()
            .consultarDisponibilidad(juego) > 0) {
            // Check player count
            if (juego.esAptoParaCantidadJugadores(cantJugadores)) {
                resultado.add(juego);
            }
        }
    }
    
    return resultado;
}
```

### Finding Compatible Tables
```java
public List<Mesa> obtenerMesasCompatibles(SistemaCafe sistema, 
                                         Reserva reserva) {
    List<Mesa> compatibles = new ArrayList<>();
    
    for (Mesa mesa : sistema.getCafe().getMesas()) {
        // Must be available
        if (mesa.estaOcupada()) continue;
        
        // Must be compatible with reservation
        if (mesa.esCompatible(reserva)) {
            compatibles.add(mesa);
        }
    }
    
    return compatibles;
}
```

### Getting User's Active Loans
```java
public List<Prestamo> obtenerPrestamosPendientes(SistemaCafe sistema, 
                                                 Usuario usuario) {
    return sistema.getInventarioPrestamos()
        .getHistorialCompleto()
        .stream()
        .filter(p -> p.getUsuario() != null && p.getUsuario().equals(usuario))
        .filter(Prestamo::estaActivo)
        .collect(Collectors.toList());
}
```

### Getting Client's Total Spending
```java
public double obtenerGastoTotal(Cliente cliente) {
    return cliente.getHistorialVentas()
        .stream()
        .mapToDouble(Venta::getTotal)
        .sum();
}
```

---

## 4. IMPLEMENTING VALIDATION LOGIC

### Example: Creating a Custom Validator
```java
public class ValidadorJuegoDisponible {
    
    public ResultadoValidacion validar(JuegoDeMesa juego, 
                                      InventarioPrestamos inventario) {
        if (juego == null) {
            return ResultadoValidacion.error(
                "Juego no existe", 
                "ERR_JUEGO_NULL"
            );
        }
        
        int disponibles = inventario.consultarDisponibilidad(juego);
        if (disponibles == 0) {
            return ResultadoValidacion.error(
                "No hay copias disponibles de " + juego.getNombre(),
                "ERR_COPIAS_AGOTADAS"
            );
        }
        
        if (juego.getEstado() == EstadoJuego.EN_REPARACION) {
            return ResultadoValidacion.warning(
                "El juego está en reparación pero se puede prestar",
                "WARN_EN_REPARACION"
            );
        }
        
        return ResultadoValidacion.exitoso();
    }
}
```

### Using Custom Validator in Service
```java
public ResultadoValidacion validarYObtenerCopia(JuegoDeMesa juego,
                                               InventarioPrestamos inventario) {
    // 1. Validate
    ValidadorJuegoDisponible validador = new ValidadorJuegoDisponible();
    ResultadoValidacion resultado = validador.validar(juego, inventario);
    
    // 2. Check for errors (but allow warnings)
    if (!resultado.esValido() && !resultado.esAdvertencia()) {
        return resultado;
    }
    
    // 3. Show warning if exists
    if (resultado.esAdvertencia()) {
        System.out.println("⚠ " + resultado.getMensaje());
    }
    
    // 4. Get copy
    CopiaJuego copia = inventario.buscarCopiaDisponible(juego);
    return ResultadoValidacion.exitoso();
}
```

---

## 5. WORKING WITH COLLECTIONS

### Collecting Games by Category
```java
public Map<CategoriaJuego, List<JuegoDeMesa>> agruparPorCategoria(
        List<JuegoDeMesa> juegos) {
    return juegos.stream()
        .collect(Collectors.groupingBy(JuegoDeMesa::getCategoria));
}

// Usage
Map<CategoriaJuego, List<JuegoDeMesa>> agrupados = agruparPorCategoria(
    sistema.getJuegosCatalogo()
);
for (CategoriaJuego cat : agrupados.keySet()) {
    System.out.println("\n=== " + cat + " ===");
    for (JuegoDeMesa juego : agrupados.get(cat)) {
        System.out.println("  - " + juego.getNombre());
    }
}
```

### Sorting Games by Price
```java
public List<JuegoDeMesa> ordenarPorPrecio(List<JuegoDeMesa> juegos, 
                                          boolean descendente) {
    return juegos.stream()
        .sorted((a, b) -> {
            int comparacion = Double.compare(a.getPrecioVenta(), 
                                            b.getPrecioVenta());
            return descendente ? -comparacion : comparacion;
        })
        .collect(Collectors.toList());
}
```

### Finding Most Played Games
```java
public List<JuegoDeMesa> obtenerJuegosMasJugados(
        InventarioPrestamos inventario, int top) {
    return inventario.getCopias()
        .stream()
        .collect(Collectors.groupingBy(
            CopiaJuego::getJuego,
            Collectors.summingInt(CopiaJuego::getVecesPrestado)
        ))
        .entrySet()
        .stream()
        .sorted((a, b) -> b.getValue() - a.getValue())
        .limit(top)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
}
```

---

## 6. PERSISTING DATA

### Saving Everything on Exit
```java
public void guardarTodo(SistemaCafe sistema, FilePersistence persistence) {
    // Prepare AppData
    AppData data = new AppData();
    data.setUsuarios(sistema.getUsuarios());
    data.setJuegos(sistema.getJuegosCatalogo());
    data.setVentas(sistema.getVentas());
    data.setCopiasPrestamo(sistema.getInventarioPrestamos().getCopias());
    data.setCopiasVenta(sistema.getInventarioVenta().getCopias());
    data.setHistorialPrestamos(
        sistema.getInventarioPrestamos().getHistorialCompleto()
    );
    
    // Save to files
    persistence.save(data);
    System.out.println("✓ Datos guardados exitosamente");
}
```

### Loading Data on Startup
```java
public SistemaCafe cargarSistema(FilePersistence persistence) {
    // 1. Create system
    SistemaCafe sistema = new SistemaCafe();
    
    // 2. Load from files
    AppData data = persistence.load();
    
    // 3. Initialize system
    sistema.cargarDatos(
        data.getUsuarios(),
        data.getJuegos(),
        data.getVentas()
    );
    
    sistema.cargarEstadoOperativo(
        data.getCopiasPrestamo(),
        data.getCopiasVenta(),
        data.getHistorialPrestamos(),
        data.getSolicitudesTurno(),
        data.getSugerenciasMenu()
    );
    
    // 4. Reconstruct relationships
    sistema.reconstruirHistorialesUsuarios();
    
    // 5. Initialize defaults if empty
    sistema.inicializarDatosBaseSiVacio();
    
    return sistema;
}
```

---

## 7. CONSOLE UI PATTERNS

### Menu Loop Pattern
```java
private static void menuCliente(SistemaCafe sistema, Cliente cliente) {
    boolean volver = false;
    
    while (!volver) {
        // Display menu
        System.out.println("\n=== MENU CLIENTE ===");
        System.out.println("1) Ver juegos");
        System.out.println("2) Hacer reserva");
        System.out.println("3) Salir");
        System.out.print("Opcion: ");
        
        // Get input
        int opcion = leerEntero();
        
        // Process
        switch (opcion) {
            case 1 -> mostrarJuegos(sistema);
            case 2 -> flujoReserva(sistema, cliente);
            case 3 -> volver = true;
            default -> System.out.println("Opcion invalida");
        }
    }
}
```

### Safe Input Parsing
```java
private static int leerEntero() {
    try {
        return SCANNER.nextInt();
    } catch (InputMismatchException e) {
        SCANNER.nextLine(); // Clear buffer
        System.out.println("Ingrese un número válido");
        return leerEntero(); // Retry
    }
}

private static String leerLinea() {
    return SCANNER.nextLine().trim();
}
```

### Displaying Lists
```java
private static void mostrarJuegos(SistemaCafe sistema) {
    List<JuegoDeMesa> juegos = sistema.getJuegosCatalogo();
    
    if (juegos.isEmpty()) {
        System.out.println("No hay juegos disponibles");
        return;
    }
    
    System.out.println("\n=== CATÁLOGO DE JUEGOS ===");
    for (int i = 0; i < juegos.size(); i++) {
        JuegoDeMesa j = juegos.get(i);
        System.out.printf("%d) %s - $%.0f (Jugadores: %d-%d)\n",
            i + 1,
            j.getNombre(),
            j.getPrecioVenta(),
            j.getMinJugadores(),
            j.getMaxJugadores()
        );
    }
}
```

### Interactive Selection
```java
private static JuegoDeMesa seleccionarJuego(SistemaCafe sistema) {
    mostrarJuegos(sistema);
    System.out.print("Seleccione número: ");
    int numero = leerEntero();
    
    List<JuegoDeMesa> juegos = sistema.getJuegosCatalogo();
    if (numero < 1 || numero > juegos.size()) {
        System.out.println("Selección inválida");
        return null;
    }
    
    return juegos.get(numero - 1);
}
```

---

## 8. BUSINESS LOGIC PATTERNS

### Calculating Sale with Discount & Tax
```java
public void procesarVenta(Usuario usuario, double precioBase, 
                         double descuentoPorcentaje) {
    // 1. Calculate subtotal (after discount)
    double subtotal = precioBase * (1 - descuentoPorcentaje);
    
    // 2. Calculate tax
    double impuesto = subtotal * 0.19;  // 19% IVA
    
    // 3. Calculate total
    double total = subtotal + impuesto;
    
    // 4. Calculate points
    int puntos = (int) Math.floor(total * 0.01);
    if (puntos > 300) puntos = 300;  // Some games cap at 300
    
    // Output
    System.out.println("Subtotal: $" + subtotal);
    System.out.println("Impuesto (19%): $" + impuesto);
    System.out.println("Total: $" + total);
    System.out.println("Puntos: " + puntos);
}
```

### Checking Game Eligibility for Group
```java
public boolean esJuegoApto(JuegoDeMesa juego, int cantPersonas, 
                          boolean hayNinos, boolean hayJovenes) {
    // 1. Check player count
    if (!juego.esAptoParaCantidadJugadores(cantPersonas)) {
        System.out.println("El juego requiere " + juego.getMinJugadores() + 
                         " a " + juego.getMaxJugadores() + " jugadores");
        return false;
    }
    
    // 2. Check age restrictions
    if (!juego.esAptoParaEdad(hayNinos, hayJovenes)) {
        System.out.println("El juego no es apto para las edades del grupo");
        return false;
    }
    
    return true;
}
```

### Managing Loyalty Points
```java
public boolean usarPuntos(Cliente cliente, int puntosRequeridos) {
    if (cliente.getPuntosDeFidelidad() < puntosRequeridos) {
        System.out.println("Puntos insuficientes");
        System.out.println("Tienes: " + cliente.getPuntosDeFidelidad());
        System.out.println("Necesitas: " + puntosRequeridos);
        return false;
    }
    
    cliente.usarPuntos(puntosRequeridos);
    System.out.println("✓ Puntos utilizados");
    System.out.println("Puntos restantes: " + cliente.getPuntosDeFidelidad());
    return true;
}
```

---

## 9. ERROR HANDLING PATTERNS

### Handling Null Returns
```java
public void procesarPrestamo(SistemaCafe sistema, Cliente cliente, 
                            JuegoDeMesa juego) {
    // Check if user can borrow
    Usuario usuario = sistema.autenticar(cliente.getLogin(), 
                                         client.getPassword());
    if (usuario == null) {
        System.out.println("Error de autenticación");
        return;
    }
    
    // Try to borrow
    CopiaJuego copia = sistema.getInventarioPrestamos()
        .buscarCopiaDisponible(juego);
    if (copia == null) {
        System.out.println("No hay copias disponibles");
        return;
    }
    
    // Proceed
    Prestamo prestamo = new Prestamo("PREST-001", copia, false, usuario);
    System.out.println("✓ Préstamo registrado");
}
```

### Validating and Recovering
```java
public void realizarOperacionCritica(SistemaCafe sistema) {
    try {
        // Attempt operation
        ResultadoValidacion resultado = sistema.getServicioPrestamos()
            .solicitarPrestamo(juego, cafe, mesa, usuario, prestamoId);
        
        // Check result
        if (!resultado.esValido()) {
            System.out.println("Operación fallida: " + resultado.getMensaje());
            
            // Offer alternatives
            if ("ERR_MAX_JUEGOS".equals(resultado.getCodigoError())) {
                System.out.println("Sugerencia: Devuelve un juego primero");
            }
            return;
        }
        
        System.out.println("Operación completada");
        
    } catch (Exception e) {
        System.out.println("Error inesperado: " + e.getMessage());
        e.printStackTrace();
    }
}
```

---

## 10. INTEGRATION PATTERNS (For Tournament System)

### Example: Creating a ParticipanteTorneo Wrapper
```java
public class ParticipanteTorneo {
    private String participanteId;
    private Cliente cliente;          // The actual user
    private Torneo torneo;
    private int puntuacionTotal;
    
    public ParticipanteTorneo(String id, Cliente cliente, Torneo torneo) {
        this.participanteId = id;
        this.cliente = cliente;
        this.torneo = torneo;
        this.puntuacionTotal = 0;
    }
    
    // Methods follow existing patterns
    public void agregarPuntos(int puntos) {
        this.puntuacionTotal += puntos;
        // Could also update cliente.puntosDeFidelidad here
    }
    
    public double calcularPuntuacion() {
        return (double) puntuacionTotal;
    }
}
```

### Integration Point: Linking Tournament Results to Client
```java
public void finalizarTorneoYActualizarCliente(
        Torneo torneo,
        ParticipanteTorneo ganador,
        ServicioTorneos servicioTorneos) {
    
    // 1. Finish tournament
    servicioTorneos.finalizarTorneo(torneo);
    
    // 2. Award bonus points to client
    Cliente cliente = ganador.getCliente();
    int bonusParalaTorneo = 1000;  // Or calculated
    cliente.acumularPuntos(bonusParalaTorneo);
    
    // 3. Update tournament stats
    ganador.agregarPuntos(bonusParalaTorneo);
    
    // 4. Optionally add to client history
    // cliente.agregarTorneoGanado(torneo);
    
    System.out.println("✓ Campeón actualizado con bonificación");
}
```

---

## KEY PATTERNS TO REMEMBER

✅ **Always use validators** - They return ResultadoValidacion objects
✅ **Check results before proceeding** - Use resultado.esValido()
✅ **Use descriptive error codes** - e.g., "ERR_MAX_JUEGOS"
✅ **Work with services** - Don't create transactions directly in console
✅ **Manage collections with streams** - filter(), map(), collect()
✅ **Handle null returns** - Check after lookups
✅ **Save on exit** - Call guardarTodo() before closing
✅ **Load on startup** - Initialize system from persistence
✅ **Reconstruct relationships** - Call reconstruirHistorialesUsuarios()
✅ **Use menus for user interaction** - Console-based navigation

---

These patterns ensure code consistency, proper error handling, and smooth integration with the existing architecture.
