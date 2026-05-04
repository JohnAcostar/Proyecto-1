package consola;

import java.util.List;
import java.util.Scanner;

import modelo.Administrador;
import modelo.Cliente;
import modelo.Empleado;
import modelo.JuegoDeMesa;
import modelo.Prestamo;
import modelo.ProductoCafe;
import modelo.RubroVenta;
import modelo.SolicitudCambioTurno;
import modelo.TipoSolicitudTurno;
import modelo.Usuario;
import modelo.Venta;
import modelo.VentaCafe;
import modelo.VentaJuegos;
import persistence.AppData;
import persistence.FilePersistence;
import service.SistemaCafe;

public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        SistemaCafe sistema = new SistemaCafe();
        FilePersistence persistence = new FilePersistence();

        AppData data = persistence.load();
        sistema.cargarDatos(data.getUsuarios(), data.getJuegos(), data.getVentas());
        sistema.cargarEstadoOperativo(data.getCopiasPrestamo(), data.getCopiasVenta(),
                data.getHistorialPrestamos(), data.getSolicitudesTurno(), data.getSugerenciasMenu());
        sistema.reconstruirHistorialesUsuarios();
        sistema.inicializarDatosBaseSiVacio();

        System.out.println("BoardGameCafe - Consola");
        System.out.println("Persistencia en: " + persistence.getDataFolderPath());

        boolean salir = false;
        while (!salir) {
System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("1) Iniciar sesion");
        System.out.println("2) Crear cuenta basica");
        System.out.println("3) Salir");
            System.out.print("Seleccione opcion: ");
            int opcion = leerEntero();

            switch (opcion) {
                case 1 -> flujoLogin(sistema);
                case 2 -> flujoCrearCuentaBasica(sistema);
                case 3 -> {
                    guardarTodo(sistema, persistence);
                    salir = true;
                }
                default -> System.out.println("Opcion invalida.");
            }
        }
        SCANNER.close();
    }

    private static void flujoLogin(SistemaCafe sistema) {
        System.out.print("Login: ");
        String login = SCANNER.nextLine().trim();
        System.out.print("Password: ");
        String password = SCANNER.nextLine().trim();

        Usuario usuario = sistema.autenticar(login, password);
        if (usuario == null) {
            System.out.println("Credenciales incorrectas.");
            return;
        }

        if (usuario instanceof Cliente cliente) {
            menuCliente(sistema, cliente);
        } else if (usuario instanceof Administrador) {
            menuAdministrador(sistema);
        } else if (usuario instanceof modelo.UsuarioBasico) {
            menuUsuarioBasico(sistema, usuario);
        } else {
            menuEmpleado(sistema, usuario);
        }
    }

    private static void flujoCrearCuentaBasica(SistemaCafe sistema) {
        System.out.print("Nuevo login: ");
        String login = SCANNER.nextLine().trim();
        System.out.print("Nueva password: ");
        String password = SCANNER.nextLine().trim();

        if (sistema.crearUsuarioBasico(login, password)) {
            System.out.println("Cuenta basica creada exitosamente. Ahora puede iniciar sesion.");
        } else {
            System.out.println("Error: El login ya existe o es invalido.");
        }
    }

    private static void menuUsuarioBasico(SistemaCafe sistema, Usuario usuario) {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n=== Menu Usuario Basico ===");
            System.out.println("Juegos y compras:");
            System.out.println("  1) Ver catalogo de juegos");
            System.out.println("  2) Pedir prestamo de juego");
            System.out.println("  3) Comprar juego");
            System.out.println("  4) Comprar cafe");
            System.out.println("Historiales:");
            System.out.println("  5) Ver historial de compras");
            System.out.println("  6) Ver historial de prestamos");
            System.out.println("Devoluciones y salida:");
            System.out.println("  7) Devolver juego prestado");
            System.out.println("  8) Cerrar sesion");
            System.out.print("Opcion: ");
            int opcion = leerEntero();

            switch (opcion) {
                case 1 -> mostrarJuegos(sistema);
                case 2 -> flujoPrestamoUsuarioBasico(sistema, usuario);
                case 3 -> flujoCompraJuego(sistema, usuario);
                case 4 -> flujoCompraCafe(sistema, usuario);
                case 5 -> mostrarHistorialCompras(usuario);
                case 6 -> mostrarHistorialPrestamosUsuario(usuario);
                case 7 -> flujoDevolverPrestamo(sistema, usuario);
                case 8 -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private static void menuCliente(SistemaCafe sistema, Cliente cliente) {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n=== Menu Cliente ===");
            System.out.println("Operaciones con juegos:");
            System.out.println("  1) Ver catalogo de juegos");
            System.out.println("  2) Reservar y pedir prestamo");
            System.out.println("  3) Comprar juego");
            System.out.println("  4) Comprar cafe");
            System.out.println("  5) Usar 1000 puntos en cafe");
            System.out.println("  6) Usar 4000 puntos en juego");
            System.out.println("Devoluciones:");
            System.out.println("  7) Devolver juego prestado");
            System.out.println("Cuenta:");
            System.out.println("  8) Ver puntos de fidelidad");
            System.out.println("  9) Menu de puntos");
            System.out.println("  10) Cerrar sesion");
            System.out.print("Opcion: ");
            int opcion = leerEntero();

            switch (opcion) {
                case 1 -> mostrarJuegos(sistema);
                case 2 -> flujoReservaPrestamo(sistema, cliente);
                case 3 -> flujoCompraJuego(sistema, cliente);
                case 4 -> flujoCompraCafe(sistema, cliente);
                case 5 -> flujoUsarPuntosCafe(sistema, cliente);
                case 6 -> flujoUsarPuntosJuego(sistema, cliente);
                case 7 -> flujoDevolverPrestamo(sistema, cliente);
                case 8 -> System.out.println("Puntos actuales: " + cliente.getPuntosDeFidelidad());
                case 9 -> menuPuntos(sistema, cliente);
                case 10 -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private static void menuEmpleado(SistemaCafe sistema, Usuario empleado) {
        Empleado emp = (Empleado) empleado;
        boolean volver = false;
        while (!volver) {
            System.out.println("\n=== Menu Empleado ===");
            System.out.println("Atencion y descuentos:");
            System.out.println("  1) Ver catalogo de juegos");
            System.out.println("  2) Comprar juego (20% descuento)");
            System.out.println("  3) Comprar cafe (20% descuento)");
            System.out.println("Servicio:");
            System.out.println("  4) Pedir prestamo de juego");
            System.out.println("  5) Devolver juego prestado");
            System.out.println("  6) Solicitar cambio de turno");
            System.out.println("  7) Cerrar sesion");
            System.out.print("Opcion: ");
            int opcion = leerEntero();

            switch (opcion) {
                case 1 -> mostrarJuegos(sistema);
                case 2 -> flujoCompraJuego(sistema, empleado);
                case 3 -> flujoCompraCafe(sistema, empleado);
                case 4 -> flujoPrestamoEmpleado(sistema, emp);
                case 5 -> flujoDevolverPrestamo(sistema, empleado);
                case 6 -> flujoSolicitudTurno(sistema, emp);
                case 7 -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private static void menuAdministrador(SistemaCafe sistema) {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n=== Menu Administrador ===");
            System.out.println("Ventas:");
            System.out.println("  1) Ver total ventas juegos");
            System.out.println("  2) Ver total ventas cafe");
            System.out.println("Inventario y prestamos:");
            System.out.println("  3) Ver catalogo de juegos");
            System.out.println("  4) Ver historial de prestamos");
            System.out.println("  5) Mover juego de venta a prestamo");
            System.out.println("  6) Marcar juego desaparecido");
            System.out.println("Turnos y reportes:");
            System.out.println("  7) Aprobar solicitud de cambio de turno");
            System.out.println("  8) Reporte ventas (diario/semanal/mensual)");
            System.out.println("  9) Cerrar sesion");
            System.out.print("Opcion: ");
            int opcion = leerEntero();

            switch (opcion) {
                case 1 -> mostrarDetalleVentasJuegos(sistema);
                case 2 -> mostrarDetalleVentasCafe(sistema);
                case 3 -> mostrarJuegos(sistema);
                case 4 -> mostrarHistorialPrestamos(sistema);
                case 5 -> flujoMoverJuegoInventario(sistema);
                case 6 -> flujoMarcarDesaparecido(sistema);
                case 7 -> flujoAprobarSolicitudTurno(sistema);
                case 8 -> flujoReporteVentas(sistema);
                case 9 -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private static void menuPuntos(SistemaCafe sistema, Cliente cliente) {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n=== Menu de Puntos ===");
            System.out.println("Puntos actuales: " + cliente.getPuntosDeFidelidad());
            System.out.println("1) Canjar 1000 puntos en cafe");
            System.out.println("2) Canjar 4000 puntos en juego");
            System.out.println("3) Ver saldo de puntos");
            System.out.println("4) Volver");
            System.out.print("Opcion: ");
            int opcion = leerEntero();

            switch (opcion) {
                case 1 -> flujoUsarPuntosCafe(sistema, cliente);
                case 2 -> flujoUsarPuntosJuego(sistema, cliente);
                case 3 -> System.out.println("Saldo de puntos: " + cliente.getPuntosDeFidelidad());
                case 4 -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

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

    private static void mostrarJuegos(SistemaCafe sistema) {
        System.out.println("\n=== CATALOGO DE JUEGOS ===");
        System.out.println(String.format("%-5s %-20s %-15s %-12s %-8s %-15s %-15s %-8s %-12s %-10s %-10s",
                "ID", "Nombre", "Empresa", "Año", "Precio", "Restriccion", "Categoria", "Jugadores", "Estado", "Dificil", "Disp."));
        System.out.println("=".repeat(150));
        for (JuegoDeMesa juego : sistema.getJuegosCatalogo()) {
            String jugadores = juego.getMinJugadores() + "-" + juego.getMaxJugadores();
            System.out.println(String.format("%-5s %-20s %-15s %-12d %-8s %-15s %-15s %-8s %-12s %-10s %-10d",
                    juego.getIdJuego(),
                    juego.getNombre(),
                    juego.getEmpresaMatriz(),
                    juego.getAnioPublicacion(),
                    formatearMoneda(juego.getPrecioVenta()),
                    juego.getRestriccionEdad(),
                    juego.getCategoria(),
                    jugadores,
                    juego.getEstado(),
                    juego.isDificil() ? "Sí" : "No",
                    sistema.getDisponibilidadPrestamo(juego)));
        }
        System.out.println("=".repeat(150));
    }

    private static void flujoReservaPrestamo(SistemaCafe sistema, Cliente cliente) {
        System.out.print("ID juego: ");
        String idJuego = SCANNER.nextLine().trim();
        JuegoDeMesa juego = sistema.buscarJuegoPorId(idJuego);
        if (juego != null) {
            System.out.println("Copias disponibles para prestamo: " + sistema.getDisponibilidadPrestamo(juego));
        }
        System.out.print("Cantidad personas: ");
        int cantidad = leerEntero();
        System.out.print("Hay ninos (<5)? (s/n): ");
        boolean hayNinos = leerBoolean();
        System.out.print("Hay jovenes (<18)? (s/n): ");
        boolean hayJovenes = leerBoolean();
        System.out.print("Hay bebidas calientes activas? (s/n): ");
        boolean bebidasCalientes = leerBoolean();

        String resultado = sistema.reservarYPrestarJuego(cliente, idJuego, cantidad, hayNinos, hayJovenes, bebidasCalientes);
        System.out.println(resultado);
    }

    private static void flujoCompraJuego(SistemaCafe sistema, Usuario usuario) {
        System.out.println("\n=== Catalogo de Juegos ===");
        int indice = 1;
        for (JuegoDeMesa juego : sistema.getJuegosCatalogo()) {
            System.out.println(String.format("%d. %-15s | %s", 
                indice++, juego.getNombre(), formatearMoneda(juego.getPrecioVenta())));
        }
        System.out.print("\nSeleccione juego (1-" + sistema.getJuegosCatalogo().size() + "): ");
        int seleccion = leerEntero();
        System.out.print("Cantidad: ");
        int cantidad = leerEntero();
        
        VentaJuegos venta = sistema.comprarJuegoPorMenu(usuario, seleccion, cantidad);
        if (venta == null) {
            System.out.println("Juego no valido o no se pudo registrar la venta.");
            return;
        }
        System.out.println("Venta registrada. Total: " + formatearMoneda(venta.getTotal()) 
            + " | Impuesto: " + formatearMoneda(venta.getImpuesto()) + " | COP");
    }
    private static void flujoPrestamoUsuarioBasico(SistemaCafe sistema, Usuario usuario) {
        mostrarJuegos(sistema);
        System.out.print("ID juego a prestar: ");
        String idJuego = SCANNER.nextLine().trim();
        JuegoDeMesa juego = sistema.buscarJuegoPorId(idJuego);
        if (juego != null) {
            System.out.println("Copias disponibles para prestamo: " + sistema.getDisponibilidadPrestamo(juego));
        }
        String resultado = sistema.prestarJuegoAUsuarioBasico(usuario, idJuego);
        System.out.println(resultado);
    }

    private static void mostrarHistorialCompras(Usuario usuario) {
        System.out.println("\n--- Historial de Compras ---");
        List<Venta> ventas = usuario.getHistorialVentas();
        if (ventas.isEmpty()) {
            System.out.println("No hay compras registradas.");
            return;
        }
        for (Venta v : ventas) {
            System.out.println("- " + v.getVentaId() + " | " + v.getFecha() + " | Total: " + formatearMoneda(v.getTotal()));
        }
    }

    private static void mostrarHistorialPrestamosUsuario(Usuario usuario) {
        System.out.println("\n--- Historial de Prestamos ---");
        List<Prestamo> prestamos = usuario.getHistorialPrestamos();
        if (prestamos.isEmpty()) {
            System.out.println("No hay prestamos registrados.");
            return;
        }
        for (Prestamo p : prestamos) {
            System.out.println("- " + p.getPrestamoId() + " | Juego: " + p.getCopia().getJuego().getNombre() 
                + " | Fecha: " + p.getFechaPrestamo() + " | Activo: " + p.estaActivo());
        }
    }

    private static void flujoDevolverPrestamo(SistemaCafe sistema, Usuario usuario) {
        System.out.println("\n--- Prestamos Activos ---");
        List<Prestamo> prestamos = usuario.getHistorialPrestamos();
        boolean hayActivos = false;
        for (Prestamo p : prestamos) {
            if (p.estaActivo()) {
                System.out.println("- " + p.getPrestamoId() + " | Juego: " + p.getCopia().getJuego().getNombre());
                hayActivos = true;
            }
        }
        if (!hayActivos) {
            System.out.println("No tienes prestamos activos para devolver.");
            return;
        }
        System.out.print("ID del prestamo a devolver: ");
        String idPrestamo = SCANNER.nextLine().trim();
        String resultado = sistema.devolverPrestamo(idPrestamo, usuario);
        System.out.println(resultado);
    }
    private static void flujoCompraCafe(SistemaCafe sistema, Usuario usuario) {
        System.out.println("\n=== Catalogo de Cafes ===");
        int indice = 1;
        for (ProductoCafe cafe : sistema.getCafesCatalogo()) {
            System.out.println(String.format("%d. %-20s | %s", 
                indice++, cafe.getNombre(), formatearMoneda(cafe.getPrecioBase())));
        }
        System.out.print("\nSeleccione cafe (1-" + sistema.getCafesCatalogo().size() + "): ");
        int seleccion = leerEntero();
        System.out.print("Cantidad: ");
        int cantidad = leerEntero();
        System.out.print("Porcentaje propina (ejemplo 0.10): ");
        double propina = leerDouble();
        
        VentaCafe venta = sistema.comprarCafePorMenu(usuario, seleccion, cantidad, propina);
        if (venta == null) {
            System.out.println("Cafe no valido o operacion no permitida para este usuario.");
            return;
        }
        double montoPropina = venta.getBase() * propina;
        System.out.println("Venta registrada. Total: " + formatearMoneda(venta.getTotal()) 
            + " | Impuesto: " + formatearMoneda(venta.getImpuesto()) 
            + " | Propina: " + formatearMoneda(montoPropina) + " | COP");
    }

    private static void flujoUsarPuntosCafe(SistemaCafe sistema, Cliente cliente) {
        System.out.println("\n=== Cafes disponibles ===");
        int indice = 1;
        for (ProductoCafe cafe : sistema.getCafesCatalogo()) {
            System.out.println(String.format("%d) %-20s | %s", indice++, cafe.getNombre(), formatearMoneda(cafe.getPrecioBase())));
        }
        System.out.println("Necesitas 1000 puntos para canjear un cafe.");
        System.out.println("Puntos actuales: " + cliente.getPuntosDeFidelidad());
        if (cliente.getPuntosDeFidelidad() < 1000) {
            System.out.println("No tienes suficientes puntos.");
            return;
        }
        System.out.print("Selecciona cafe (1-" + sistema.getCafesCatalogo().size() + "): ");
        int seleccion = leerEntero();
        VentaCafe venta = sistema.comprarCafeConPuntos(cliente, seleccion);
        if (venta == null) {
            System.out.println("No se pudo canjear el cafe.");
            return;
        }
        System.out.println("Cafe canjeado con 1000 puntos. Puntos restantes: " + cliente.getPuntosDeFidelidad());
    }

    private static void flujoUsarPuntosJuego(SistemaCafe sistema, Cliente cliente) {
        System.out.println("\n=== Juegos disponibles ===");
        for (JuegoDeMesa juego : sistema.getJuegosCatalogo()) {
            System.out.println("- " + juego.getIdJuego() + ": " + juego.getNombre() + " | " + formatearMoneda(juego.getPrecioVenta()));
        }
        System.out.println("Necesitas 4000 puntos para canjear un juego.");
        System.out.println("Puntos actuales: " + cliente.getPuntosDeFidelidad());
        if (cliente.getPuntosDeFidelidad() < 4000) {
            System.out.println("No tienes suficientes puntos.");
            return;
        }
        System.out.print("Ingrese ID del juego a canjear: ");
        String idJuego = SCANNER.nextLine().trim();
        VentaJuegos venta = sistema.comprarJuegoConPuntos(cliente, idJuego);
        if (venta == null) {
            System.out.println("No se pudo canjear el juego.");
            return;
        }
        System.out.println("Juego canjeado con 4000 puntos. Puntos restantes: " + cliente.getPuntosDeFidelidad());
    }

    private static void flujoPrestamoEmpleado(SistemaCafe sistema, Empleado empleado) {
        System.out.print("Hay clientes por atender? (s/n): ");
        boolean hayClientes = leerBoolean();
        if (hayClientes) {
            System.out.println("No puedes pedir prestamo si hay clientes por atender. Atiende a los clientes primero.");
            return;
        }
        System.out.print("ID juego: ");
        String idJuego = SCANNER.nextLine().trim();
        JuegoDeMesa juego = sistema.buscarJuegoPorId(idJuego);
        if (juego != null) {
            System.out.println("Copias disponibles para prestamo: " + sistema.getDisponibilidadPrestamo(juego));
        }
        System.out.println(sistema.prestarJuegoAEmpleado(empleado, idJuego, false));
    }

    private static void flujoSolicitudTurno(SistemaCafe sistema, Empleado empleado) {
        System.out.println("Tipo solicitud: 1.CAMBIO 2.INTERCAMBIO");
        int op = leerEntero();
        TipoSolicitudTurno tipo = (op == 2) ? TipoSolicitudTurno.INTERCAMBIO : TipoSolicitudTurno.CAMBIO;
        SolicitudCambioTurno solicitud = empleado.solicitarCambioTurno(tipo);
        sistema.registrarSolicitudCambioTurno(solicitud);
        System.out.println("Solicitud registrada en estado: " + solicitud.getEstado());
    }

    private static void mostrarHistorialPrestamos(SistemaCafe sistema) {
        System.out.println("Historial prestamos:");
        for (Prestamo p : sistema.getHistorialPrestamos()) {
            String usuario = (p.getUsuario() != null) ? p.getUsuario().getLogin() : "Sin usuario asignado";
            System.out.println("- " + p.getPrestamoId() + " | usuario: " + usuario + " | juego: " + p.getCopia().getJuego().getNombre()
                    + " | activo: " + p.estaActivo() + " | advertencia: " + p.isAdvertenciaDificultad());
        }
    }

    private static void flujoMoverJuegoInventario(SistemaCafe sistema) {
        System.out.print("ID juego a mover de venta a prestamo: ");
        String idJuego = SCANNER.nextLine().trim();
        System.out.println(sistema.moverJuegoDeVentaAPrestamo(idJuego));
    }

    private static void flujoMarcarDesaparecido(SistemaCafe sistema) {
        System.out.print("ID juego a marcar desaparecido: ");
        String idJuego = SCANNER.nextLine().trim();
        System.out.println(sistema.marcarJuegoDesaparecido(idJuego));
    }

    private static void flujoAprobarSolicitudTurno(SistemaCafe sistema) {
        System.out.print("Indice de solicitud (empezando en 0): ");
        int idx = leerEntero();
        System.out.println(sistema.aprobarSolicitudTurno(idx, java.time.DayOfWeek.MONDAY));
    }

    private static void flujoReporteVentas(SistemaCafe sistema) {
        var reporte = sistema.generarReporteSimple();
        
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║         REPORTE DE VENTAS - BOARDGAMECAFE            ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
        
        System.out.println("\nREPORTE DIARIO:");
        reporte.generarDiario().forEach((fecha, total) -> 
            System.out.println("   " + fecha + " -> $" + String.format("%,.0f", total))
        );
        
        System.out.println("\nREPORTE SEMANAL:");
        reporte.generarSemanal().forEach((semana, total) -> 
            System.out.println("   Semana " + semana + " -> $" + String.format("%,.0f", total))
        );
        
        System.out.println("\nREPORTE MENSUAL:");
        reporte.generarMensual().forEach((mes, total) -> 
            System.out.println("   Mes " + mes + " -> $" + String.format("%,.0f", total))
        );
        
        System.out.println("\nDETALLE JUEGOS:");
        var juegoDetalle = sistema.resumenFinancieroPorRubro(RubroVenta.JUEGO);
        juegoDetalle.forEach((key, value) -> 
            System.out.println("   " + key + ": $" + String.format("%,.0f", (Double)value))
        );
        
        System.out.println("\nDETALLE CAFE:");
        var cafeDetalle = sistema.resumenFinancieroPorRubro(RubroVenta.CAFE);
        cafeDetalle.forEach((key, value) -> 
            System.out.println("   " + key + ": $" + String.format("%,.0f", (Double)value))
        );
        
        System.out.println("\n" + "═".repeat(60) + "\n");
    }

    private static int leerEntero() {
        while (true) {
            try {
                return Integer.parseInt(SCANNER.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Numero invalido. Intente de nuevo: ");
            }
        }
    }

    private static double leerDouble() {
        while (true) {
            try {
                return Double.parseDouble(SCANNER.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Numero invalido. Intente de nuevo: ");
            }
        }
    }

    private static boolean leerBoolean() {
        while (true) {
            String valor = SCANNER.nextLine().trim().toLowerCase();
            if ("s".equals(valor)) {
                return true;
            }
            if ("n".equals(valor)) {
                return false;
            }
            System.out.print("Ingrese s o n: ");
        }
    }

    private static String formatearMoneda(double cantidad) {
        String formato = String.format("%,.0f", cantidad);
        return "$ " + formato.replace(",", ".");
    }

    private static void guardarTodo(SistemaCafe sistema, FilePersistence persistence) {
        AppData data = new AppData();
        data.setUsuarios(sistema.getUsuarios());
        data.setJuegos(sistema.getJuegosCatalogo());
        data.setVentas(sistema.getVentas());
        data.setCopiasPrestamo(sistema.getCopiasPrestamo());
        data.setCopiasVenta(sistema.getCopiasVenta());
        data.setHistorialPrestamos(sistema.getHistorialPrestamos());
        data.setSolicitudesTurno(sistema.getSolicitudesCambio());
        data.setSugerenciasMenu(sistema.getSugerenciasMenu());
        persistence.save(data);
        System.out.println("Datos guardados.");
    }

}
