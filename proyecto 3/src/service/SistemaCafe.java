package service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.Administrador;
import modelo.CategoriaJuego;
import modelo.Cliente;
import modelo.Cocinero;
import modelo.CopiaJuego;
import modelo.Empleado;
import modelo.EstadoJuego;
import modelo.Horario;
import modelo.InventarioPrestamos;
import modelo.InventarioVenta;
import modelo.JuegoDeMesa;
import modelo.Mesero;
import modelo.Prestamo;
import modelo.ProductoCafe;
import modelo.ReporteVentas;
import modelo.Reserva;
import modelo.RestriccionEdad;
import modelo.RubroVenta;
import modelo.SolicitudCambioTurno;
import modelo.Sugerencia;
import modelo.Usuario;
import modelo.UsuarioBasico;
import modelo.Venta;
import modelo.VentaCafe;
import modelo.VentaJuegos;

public class SistemaCafe {
    private final List<Usuario> usuarios;
    private final List<JuegoDeMesa> juegosCatalogo;
    private final List<ProductoCafe> cafesCatalogo;
    private final List<Venta> ventas;
    private final InventarioPrestamos inventarioPrestamos;
    private final InventarioVenta inventarioVenta;
    private final List<Prestamo> prestamosActivos;
    private final Horario horario;
    private final List<SolicitudCambioTurno> solicitudesCambio;
    private final List<Sugerencia> sugerenciasMenu;
    private final Map<String, Boolean> mesaConJuegoAccion;

    public SistemaCafe() {
        this.usuarios = new ArrayList<>();
        this.juegosCatalogo = new ArrayList<>();
        this.cafesCatalogo = new ArrayList<>();
        this.ventas = new ArrayList<>();
        this.inventarioPrestamos = new InventarioPrestamos();
        this.inventarioVenta = new InventarioVenta();
        this.prestamosActivos = new ArrayList<>();
        this.horario = new Horario();
        this.solicitudesCambio = new ArrayList<>();
        this.sugerenciasMenu = new ArrayList<>();
        this.mesaConJuegoAccion = new HashMap<>();
    }

    public void cargarDatos(List<Usuario> usuarios, List<JuegoDeMesa> juegos, List<Venta> ventas) {
        this.usuarios.clear();
        this.usuarios.addAll(usuarios);
        this.juegosCatalogo.clear();
        this.juegosCatalogo.addAll(juegos);
        this.ventas.clear();
        this.ventas.addAll(ventas);
    }

    public void cargarEstadoOperativo(List<CopiaJuego> copiasPrestamo, List<CopiaJuego> copiasVenta,
            List<Prestamo> historialPrestamos, List<SolicitudCambioTurno> solicitudes,
            List<Sugerencia> sugerencias) {
        this.inventarioPrestamos.reemplazarDatos(copiasPrestamo, historialPrestamos);
        this.inventarioVenta.reemplazarCopias(copiasVenta);
        this.solicitudesCambio.clear();
        this.solicitudesCambio.addAll(solicitudes);
        this.sugerenciasMenu.clear();
        this.sugerenciasMenu.addAll(sugerencias);
        this.prestamosActivos.clear();
        for (Prestamo p : historialPrestamos) {
            if (p.estaActivo()) {
                this.prestamosActivos.add(p);
            }
        }
    }

    public void reconstruirHistorialesUsuarios() {
        for (Venta v : ventas) {
            if (v.getUsuario() != null) {
                v.getUsuario().agregarVenta(v);
            }
        }
        for (Prestamo p : inventarioPrestamos.getHistorialCompleto()) {
            if (p.getUsuario() != null) {
                p.getUsuario().agregarPrestamo(p);
            }
        }
    }

    public void buscarYAsignarUsuariosATransacciones() {
        for (Venta v : ventas) {
            if (v.getUsuario() == null) {
                // Intentar buscar por login si no tiene usuario
                // (pero como no se guarda el login, se deja null)
            }
        }
        for (Prestamo p : inventarioPrestamos.getHistorialCompleto()) {
            if (p.getUsuario() == null) {
                // Intentar buscar por login si no tiene usuario
                // (pero como no se guarda el login, se deja null)
            }
        }
    }

    public void inicializarDatosBaseSiVacio() {
        if (usuarios.isEmpty()) {
            usuarios.add(new Administrador("admin", "admin123", "A-01"));
            usuarios.add(new Cliente("cliente1", "cliente123", "C-01", "CLI10"));
            
            // Meseros
            usuarios.add(new Mesero("mesero1", "mesero123", "M-01", "Luis", "EMP20"));
            usuarios.add(new Mesero("mesero2", "mesero123", "M-02", "Carlos", "EMP20"));
            usuarios.add(new Mesero("mesero3", "mesero123", "M-03", "María", "EMP20"));
            usuarios.add(new Mesero("mesero4", "mesero123", "M-04", "Laura", "EMP20"));
            
            // Cocineros
            usuarios.add(new Cocinero("cocinero1", "cocinero123", "K-01", "Ana", "EMP20"));
            usuarios.add(new Cocinero("cocinero2", "cocinero123", "K-02", "Pedro", "EMP20"));
        }
        
        // Asignar precios a juegos existentes (por nombre)
        for (JuegoDeMesa juego : juegosCatalogo) {
            if (juego.getPrecioVenta() == 0) {
                if ("Uno".equals(juego.getNombre())) {
                    juego.setPrecioVenta(35000);
                } else if ("Catan".equals(juego.getNombre())) {
                    juego.setPrecioVenta(50000);
                } else if ("Twister".equals(juego.getNombre())) {
                    juego.setPrecioVenta(25000);
                }
            }
        }
        
        if (juegosCatalogo.isEmpty()) {
            agregarJuegosBase();
        } else {
            agregarJuegosFaltantes();
        }
        if (cafesCatalogo.isEmpty()) {
            cafesCatalogo.add(new ProductoCafe("1", "Cafe Americano", 8000));
            cafesCatalogo.add(new ProductoCafe("2", "Cafe con Leche", 9000));
            cafesCatalogo.add(new ProductoCafe("3", "Espresso", 7000));
            cafesCatalogo.add(new ProductoCafe("4", "Cappuccino", 10000));
            cafesCatalogo.add(new ProductoCafe("5", "Latte", 11000));
        }
        inicializarInventariosSiVacios();
        asegurarCopiasMinimasParaPrestamo();
    }

    private void inicializarInventariosSiVacios() {
        if (!inventarioPrestamos.getCopias().isEmpty() || !inventarioVenta.getCopias().isEmpty()) {
            return;
        }
        int idx = 1;
        for (JuegoDeMesa juego : juegosCatalogo) {
            for (int i = 0; i < 3; i++) {
                inventarioPrestamos.agregarCopia(new CopiaJuego("P-" + idx++, juego));
            }
            for (int i = 0; i < 2; i++) {
                inventarioVenta.agregarCopia(new CopiaJuego("V-" + idx++, juego));
            }
        }
    }

    private void asegurarCopiasMinimasParaPrestamo() {
        int siguiente = inventarioPrestamos.getCopias().stream()
                .mapToInt(c -> {
                    String id = c.getCopyId();
                    if (id.startsWith("P-")) {
                        try {
                            return Integer.parseInt(id.substring(2));
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }
                    return 0;
                })
                .max()
                .orElse(0) + 1;

        for (JuegoDeMesa juego : juegosCatalogo) {
            int disponibles = inventarioPrestamos.consultarDisponibilidad(juego);
            while (disponibles < 3) {
                CopiaJuego copia = new CopiaJuego("P-" + (siguiente++), juego);
                inventarioPrestamos.agregarCopia(copia);
                disponibles++;
            }
        }
    }

    private void agregarJuegosBase() {
        JuegoDeMesa juego1 = new JuegoDeMesa("J-01", "Uno", 1971, "Mattel", 2, 10,
                RestriccionEdad.TODAS_LAS_EDADES, CategoriaJuego.CARTAS, EstadoJuego.BUENO, false);
        juego1.setPrecioVenta(35000);
        juegosCatalogo.add(juego1);

        JuegoDeMesa juego2 = new JuegoDeMesa("J-02", "Catan", 1995, "Kosmos", 3, 4,
                RestriccionEdad.MAYORES_DE_5, CategoriaJuego.TABLERO, EstadoJuego.BUENO, true);
        juego2.setPrecioVenta(50000);
        juegosCatalogo.add(juego2);

        JuegoDeMesa juego3 = new JuegoDeMesa("J-03", "Twister", 1966, "Hasbro", 2, 6,
                RestriccionEdad.TODAS_LAS_EDADES, CategoriaJuego.ACCION, EstadoJuego.BUENO, false);
        juego3.setPrecioVenta(25000);
        juegosCatalogo.add(juego3);

        JuegoDeMesa juego4 = new JuegoDeMesa("J-04", "Poker Deluxe", 2010, "Bicycle", 2, 8,
                RestriccionEdad.ADULTOS_SOLO, CategoriaJuego.CARTAS, EstadoJuego.NUEVO, false);
        juego4.setPrecioVenta(45000);
        juegosCatalogo.add(juego4);

        JuegoDeMesa juego5 = new JuegoDeMesa("J-05", "Ajedrez Clasico", 1000, "Varios", 2, 2,
                RestriccionEdad.TODAS_LAS_EDADES, CategoriaJuego.TABLERO, EstadoJuego.BUENO, true);
        juego5.setPrecioVenta(55000);
        juegosCatalogo.add(juego5);

        JuegoDeMesa juego6 = new JuegoDeMesa("J-06", "Monopoly", 1935, "Hasbro", 2, 8,
                RestriccionEdad.MAYORES_DE_5, CategoriaJuego.TABLERO, EstadoJuego.BUENO, false);
        juego6.setPrecioVenta(60000);
        juegosCatalogo.add(juego6);

        JuegoDeMesa juego7 = new JuegoDeMesa("J-07", "Memory", 1959, "Ravensburger", 2, 4,
                RestriccionEdad.TODAS_LAS_EDADES, CategoriaJuego.CARTAS, EstadoJuego.BUENO, false);
        juego7.setPrecioVenta(25000);
        juegosCatalogo.add(juego7);

        JuegoDeMesa juego8 = new JuegoDeMesa("J-08", "Risk", 1959, "Parker Brothers", 2, 6,
                RestriccionEdad.ADULTOS_SOLO, CategoriaJuego.TABLERO, EstadoJuego.FALTA_PIEZA, true);
        juego8.setPrecioVenta(70000);
        juegosCatalogo.add(juego8);

        JuegoDeMesa juego9 = new JuegoDeMesa("J-09", "Dino Clash", 2018, "Asmodee", 2, 4,
                RestriccionEdad.TODAS_LAS_EDADES, CategoriaJuego.ACCION, EstadoJuego.NUEVO, true);
        juego9.setPrecioVenta(40000);
        juegosCatalogo.add(juego9);

        JuegoDeMesa juego10 = new JuegoDeMesa("J-10", "Scrabble", 1938, "Mattel", 2, 4,
                RestriccionEdad.MAYORES_DE_5, CategoriaJuego.CARTAS, EstadoJuego.BUENO, false);
        juego10.setPrecioVenta(38000);
        juegosCatalogo.add(juego10);

        JuegoDeMesa juego11 = new JuegoDeMesa("J-11", "Dominoes Pro", 1900, "Varios", 2, 6,
                RestriccionEdad.ADULTOS_SOLO, CategoriaJuego.CARTAS, EstadoJuego.EN_REPARACION, false);
        juego11.setPrecioVenta(32000);
        juegosCatalogo.add(juego11);

        JuegoDeMesa juego12 = new JuegoDeMesa("J-12", "Pandemic", 2008, "Z-Man Games", 2, 4,
                RestriccionEdad.MAYORES_DE_5, CategoriaJuego.TABLERO, EstadoJuego.NUEVO, true);
        juego12.setPrecioVenta(65000);
        juegosCatalogo.add(juego12);

        JuegoDeMesa juego13 = new JuegoDeMesa("J-13", "Gloomhaven", 2017, "Cephalofair Games", 1, 4,
                RestriccionEdad.ADULTOS_SOLO, CategoriaJuego.TABLERO, EstadoJuego.EN_REPARACION, true);
        juego13.setPrecioVenta(120000);
        juegosCatalogo.add(juego13);

        JuegoDeMesa juego14 = new JuegoDeMesa("J-14", "Codenames", 2015, "Czech Games", 2, 8,
                RestriccionEdad.TODAS_LAS_EDADES, CategoriaJuego.CARTAS, EstadoJuego.BUENO, false);
        juego14.setPrecioVenta(45000);
        juegosCatalogo.add(juego14);

        JuegoDeMesa juego15 = new JuegoDeMesa("J-15", "Ticket to Ride", 2004, "Days of Wonder", 2, 5,
                RestriccionEdad.MAYORES_DE_5, CategoriaJuego.TABLERO, EstadoJuego.BUENO, false);
        juego15.setPrecioVenta(68000);
        juegosCatalogo.add(juego15);

        JuegoDeMesa juego16 = new JuegoDeMesa("J-16", "Carcassonne", 2000, "Z-Man Games", 2, 5,
                RestriccionEdad.TODAS_LAS_EDADES, CategoriaJuego.TABLERO, EstadoJuego.BUENO, false);
        juego16.setPrecioVenta(52000);
        juegosCatalogo.add(juego16);
    }

    private void agregarJuegosFaltantes() {
        Map<String, JuegoDeMesa> porId = new HashMap<>();
        for (JuegoDeMesa juego : juegosCatalogo) {
            porId.put(juego.getIdJuego(), juego);
        }

        if (!porId.containsKey("J-04")) {
            JuegoDeMesa juego4 = new JuegoDeMesa("J-04", "Poker Deluxe", 2010, "Bicycle", 2, 8,
                    RestriccionEdad.ADULTOS_SOLO, CategoriaJuego.CARTAS, EstadoJuego.NUEVO, false);
            juego4.setPrecioVenta(45000);
            juegosCatalogo.add(juego4);
        }
        if (!porId.containsKey("J-05")) {
            JuegoDeMesa juego5 = new JuegoDeMesa("J-05", "Ajedrez Clasico", 1000, "Varios", 2, 2,
                    RestriccionEdad.TODAS_LAS_EDADES, CategoriaJuego.TABLERO, EstadoJuego.BUENO, true);
            juego5.setPrecioVenta(55000);
            juegosCatalogo.add(juego5);
        }
        if (!porId.containsKey("J-06")) {
            JuegoDeMesa juego6 = new JuegoDeMesa("J-06", "Monopoly", 1935, "Hasbro", 2, 8,
                    RestriccionEdad.MAYORES_DE_5, CategoriaJuego.TABLERO, EstadoJuego.BUENO, false);
            juego6.setPrecioVenta(60000);
            juegosCatalogo.add(juego6);
        }
        if (!porId.containsKey("J-07")) {
            JuegoDeMesa juego7 = new JuegoDeMesa("J-07", "Memory", 1959, "Ravensburger", 2, 4,
                    RestriccionEdad.TODAS_LAS_EDADES, CategoriaJuego.CARTAS, EstadoJuego.BUENO, false);
            juego7.setPrecioVenta(25000);
            juegosCatalogo.add(juego7);
        }
        if (!porId.containsKey("J-08")) {
            JuegoDeMesa juego8 = new JuegoDeMesa("J-08", "Risk", 1959, "Parker Brothers", 2, 6,
                    RestriccionEdad.ADULTOS_SOLO, CategoriaJuego.TABLERO, EstadoJuego.FALTA_PIEZA, true);
            juego8.setPrecioVenta(70000);
            juegosCatalogo.add(juego8);
        }
        if (!porId.containsKey("J-09")) {
            JuegoDeMesa juego9 = new JuegoDeMesa("J-09", "Dino Clash", 2018, "Asmodee", 2, 4,
                    RestriccionEdad.TODAS_LAS_EDADES, CategoriaJuego.ACCION, EstadoJuego.NUEVO, true);
            juego9.setPrecioVenta(40000);
            juegosCatalogo.add(juego9);
        }
        if (!porId.containsKey("J-10")) {
            JuegoDeMesa juego10 = new JuegoDeMesa("J-10", "Scrabble", 1938, "Mattel", 2, 4,
                    RestriccionEdad.MAYORES_DE_5, CategoriaJuego.CARTAS, EstadoJuego.BUENO, false);
            juego10.setPrecioVenta(38000);
            juegosCatalogo.add(juego10);
        }
        if (!porId.containsKey("J-11")) {
            JuegoDeMesa juego11 = new JuegoDeMesa("J-11", "Dominoes Pro", 1900, "Varios", 2, 6,
                    RestriccionEdad.ADULTOS_SOLO, CategoriaJuego.CARTAS, EstadoJuego.EN_REPARACION, false);
            juego11.setPrecioVenta(32000);
            juegosCatalogo.add(juego11);
        }
        if (!porId.containsKey("J-12")) {
            JuegoDeMesa juego12 = new JuegoDeMesa("J-12", "Pandemic", 2008, "Z-Man Games", 2, 4,
                    RestriccionEdad.MAYORES_DE_5, CategoriaJuego.TABLERO, EstadoJuego.NUEVO, true);
            juego12.setPrecioVenta(65000);
            juegosCatalogo.add(juego12);
        }
        if (!porId.containsKey("J-13")) {
            JuegoDeMesa juego13 = new JuegoDeMesa("J-13", "Gloomhaven", 2017, "Cephalofair Games", 1, 4,
                    RestriccionEdad.ADULTOS_SOLO, CategoriaJuego.TABLERO, EstadoJuego.EN_REPARACION, true);
            juego13.setPrecioVenta(120000);
            juegosCatalogo.add(juego13);
        }
        if (!porId.containsKey("J-14")) {
            JuegoDeMesa juego14 = new JuegoDeMesa("J-14", "Codenames", 2015, "Czech Games", 2, 8,
                    RestriccionEdad.TODAS_LAS_EDADES, CategoriaJuego.CARTAS, EstadoJuego.BUENO, false);
            juego14.setPrecioVenta(45000);
            juegosCatalogo.add(juego14);
        }
        if (!porId.containsKey("J-15")) {
            JuegoDeMesa juego15 = new JuegoDeMesa("J-15", "Ticket to Ride", 2004, "Days of Wonder", 2, 5,
                    RestriccionEdad.MAYORES_DE_5, CategoriaJuego.TABLERO, EstadoJuego.BUENO, false);
            juego15.setPrecioVenta(68000);
            juegosCatalogo.add(juego15);
        }
        if (!porId.containsKey("J-16")) {
            JuegoDeMesa juego16 = new JuegoDeMesa("J-16", "Carcassonne", 2000, "Z-Man Games", 2, 5,
                    RestriccionEdad.TODAS_LAS_EDADES, CategoriaJuego.TABLERO, EstadoJuego.BUENO, false);
            juego16.setPrecioVenta(52000);
            juegosCatalogo.add(juego16);
        }
    }

    public Usuario autenticar(String login, String password) {
        for (Usuario usuario : usuarios) {
            if (usuario.autenticar(login, password)) {
                return usuario;
            }
        }
        return null;
    }

    public boolean crearUsuarioBasico(String login, String password) {
        if (autenticar(login, password) != null) {
            return false; // Ya existe
        }
        Usuario nuevo = new UsuarioBasico(login, password, "UB-" + (usuarios.size() + 1));
        usuarios.add(nuevo);
        return true;
    }

    public List<JuegoDeMesa> getJuegosCatalogo() {
        return new ArrayList<>(juegosCatalogo);
    }

    public int getDisponibilidadPrestamo(JuegoDeMesa juego) {
        return inventarioPrestamos.consultarDisponibilidad(juego);
    }

    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public List<Venta> getVentas() {
        return new ArrayList<>(ventas);
    }

    public JuegoDeMesa buscarJuegoPorId(String idJuego) {
        for (JuegoDeMesa juego : juegosCatalogo) {
            if (juego.getIdJuego().equalsIgnoreCase(idJuego)) {
                return juego;
            }
        }
        return null;
    }

    public String reservarYPrestarJuego(Cliente cliente, String idJuego, int cantidadPersonas, boolean hayNinos,
            boolean hayJovenes, boolean hayBebidasCalientes) {
        JuegoDeMesa juego = buscarJuegoPorId(idJuego);
        if (juego == null) {
            return "Juego no encontrado.";
        }
        Reserva reserva = cliente.hacerReserva(cantidadPersonas, hayNinos, hayJovenes);
        if (hayBebidasCalientes) {
            reserva.activarBebidasCalientes();
        }
        if (!reserva.validarElegibilidadDelJuego(juego)) {
            return "Prestamo bloqueado por edad/cantidad/reglas de mesa.";
        }
        if (inventarioPrestamos.consultarDisponibilidad(juego) <= 0) {
            return "No hay copias disponibles para prestamo.";
        }
        boolean advertencia = false;
        if (juego.isDificil()) {
            Mesero mesero = buscarMeseroCapacitado(juego);
            advertencia = (mesero == null);
        }
        Prestamo prestamo = inventarioPrestamos.checkOut("PR-" + (inventarioPrestamos.getHistorialCompleto().size() + 1),
                juego, advertencia, cliente);
        if (prestamo == null) {
            return "No fue posible crear el prestamo.";
        }
        prestamosActivos.add(prestamo);
        cliente.agregarPrestamo(prestamo);
        reserva.confirmar();
        reserva.anadirPrestamoDeJuego(juego);
        if (juego.getCategoria() == CategoriaJuego.ACCION) {
            mesaConJuegoAccion.put(cliente.getId(), true);
        }
        if (advertencia) {
            return "Prestamo aprobado con advertencia: no hay mesero capacitado para juego dificil.";
        }
        return "Prestamo aprobado.";
    }

    public VentaJuegos comprarJuego(Usuario comprador, double base) {
        if (!(comprador instanceof Administrador)) {
            if (inventarioVenta.getCopias().isEmpty()) {
                return null;
            }
        }
        VentaJuegos venta = new VentaJuegos("VJ-" + (ventas.size() + 1), base, comprador);
        if (comprador instanceof Empleado) {
            venta.aplicarDescuento(0.20);
        }
        venta.calcularTotal();
        ventas.add(venta);
        comprador.agregarVenta(venta);
        if (comprador instanceof Cliente) {
            ((Cliente) comprador).acumularPuntos(venta.calcularPuntos());
        }
        return venta;
    }

    public VentaCafe comprarCafeConPuntos(Cliente cliente, int indiceCafe) {
        if (cliente.getPuntosDeFidelidad() < 1000) {
            return null;
        }
        if (indiceCafe < 1 || indiceCafe > cafesCatalogo.size()) {
            return null;
        }
        ProductoCafe cafe = cafesCatalogo.get(indiceCafe - 1);
        cliente.usarPuntos(1000);
        VentaCafe venta = new VentaCafe("VC-" + (ventas.size() + 1), 0, 0, cliente);
        venta.calcularTotal();
        ventas.add(venta);
        cliente.agregarVenta(venta);
        return venta;
    }

    public VentaJuegos comprarJuegoConPuntos(Cliente cliente, String idJuego) {
        if (cliente.getPuntosDeFidelidad() < 4000) {
            return null;
        }
        JuegoDeMesa juego = buscarJuegoPorId(idJuego);
        if (juego == null) {
            return null;
        }
        cliente.usarPuntos(4000);
        VentaJuegos venta = new VentaJuegos("VJ-" + (ventas.size() + 1), 0, cliente);
        venta.calcularTotal();
        ventas.add(venta);
        cliente.agregarVenta(venta);
        return venta;
    }

    public VentaCafe comprarCafe(Usuario comprador, double base, double propinaPorcentaje) {
        if (comprador instanceof Administrador) {
            return null;
        }
        double baseAplicada = base;
        if (comprador instanceof Empleado) {
            baseAplicada = base * 0.8;
        }
        VentaCafe venta = new VentaCafe("VC-" + (ventas.size() + 1), baseAplicada, propinaPorcentaje, comprador);
        venta.calcularTotal();
        ventas.add(venta);
        comprador.agregarVenta(venta);
        if (comprador instanceof Cliente) {
            ((Cliente) comprador).acumularPuntos(venta.calcularPuntos());
        }
        return venta;
    }

    public boolean puedeDespacharBebidaCaliente(String mesaId) {
        return !mesaConJuegoAccion.getOrDefault(mesaId, false);
    }

    public String registrarBebidaCalienteEnMesa(String mesaId) {
        if (!puedeDespacharBebidaCaliente(mesaId)) {
            return "No se puede despachar bebida caliente: hay juego de ACCION en la mesa.";
        }
        return "Bebida caliente permitida.";
    }

    public String prestarJuegoAEmpleado(Empleado empleado, String idJuego, boolean hayClientesPorAtender) {
        if (!empleado.puedePedirPrestado(hayClientesPorAtender)) {
            return "Empleado no puede pedir prestamo en este momento.";
        }
        JuegoDeMesa juego = buscarJuegoPorId(idJuego);
        if (juego == null) {
            return "Juego no encontrado.";
        }
        if (inventarioPrestamos.consultarDisponibilidad(juego) <= 0) {
            return "No hay copias disponibles.";
        }
        Prestamo prestamo = inventarioPrestamos.checkOut("PR-" + (inventarioPrestamos.getHistorialCompleto().size() + 1),
                juego, false, empleado);
        if (prestamo != null) {
            prestamosActivos.add(prestamo);
            empleado.agregarPrestamo(prestamo);
            return "Prestamo a empleado aprobado.";
        }
        return "No fue posible registrar prestamo.";
    }

    public String prestarJuegoAUsuarioBasico(Usuario usuario, String idJuego) {
        JuegoDeMesa juego = buscarJuegoPorId(idJuego);
        if (juego == null) {
            return "Juego no encontrado.";
        }
        if (inventarioPrestamos.consultarDisponibilidad(juego) <= 0) {
            return "No hay copias disponibles.";
        }
        Prestamo prestamo = inventarioPrestamos.checkOut("PR-" + (inventarioPrestamos.getHistorialCompleto().size() + 1),
                juego, false, usuario);
        if (prestamo != null) {
            prestamosActivos.add(prestamo);
            usuario.agregarPrestamo(prestamo);
            return "Prestamo aprobado.";
        }
        return "No fue posible registrar prestamo.";
    }

    public String devolverPrestamo(String prestamoId, Usuario usuario) {
        for (Prestamo p : prestamosActivos) {
            if (p.getPrestamoId().equals(prestamoId) && p.getUsuario() != null && p.getUsuario().equals(usuario) && p.estaActivo()) {
                p.registrarDevolucion();
                prestamosActivos.remove(p);
                return "Prestamo devuelto exitosamente.";
            }
        }
        return "Prestamo no encontrado, no pertenece al usuario o ya fue devuelto.";
    }

    public String devolverPrestamo(String prestamoId) {
        for (Prestamo p : prestamosActivos) {
            if (p.getPrestamoId().equalsIgnoreCase(prestamoId) && p.estaActivo()) {
                p.registrarDevolucion();
                return "Devolucion registrada.";
            }
        }
        return "Prestamo no encontrado o ya devuelto.";
    }

    public int disponibilidadPrestamo(String idJuego) {
        JuegoDeMesa juego = buscarJuegoPorId(idJuego);
        return juego == null ? 0 : inventarioPrestamos.consultarDisponibilidad(juego);
    }

    public int disponibilidadVenta(String idJuego) {
        JuegoDeMesa juego = buscarJuegoPorId(idJuego);
        return juego == null ? 0 : inventarioVenta.obtenerCuentaDeStock(juego);
    }

    public String moverJuegoDeVentaAPrestamo(String idJuego) {
        JuegoDeMesa juego = buscarJuegoPorId(idJuego);
        if (juego == null) {
            return "Juego no encontrado.";
        }
        CopiaJuego copia = inventarioVenta.vender(juego);
        if (copia == null) {
            return "No hay copia en inventario de venta.";
        }
        CopiaJuego nueva = new CopiaJuego("P-ADM-" + System.nanoTime(), juego);
        inventarioPrestamos.agregarCopia(nueva);
        return "Juego movido de venta a prestamo.";
    }

    public String repararJuego(String idJuego) {
        return moverJuegoDeVentaAPrestamo(idJuego);
    }

    public String marcarJuegoDesaparecido(String idJuego) {
        JuegoDeMesa juego = buscarJuegoPorId(idJuego);
        if (juego == null) {
            return "Juego no encontrado.";
        }
        juego.cambiarEstado(EstadoJuego.DESAPARECIDO);
        return "Juego marcado como desaparecido.";
    }

    public String sugerirNuevoPlatillo(String nombre, String descripcion) {
        sugerenciasMenu.add(new Sugerencia(nombre, descripcion));
        return "Sugerencia registrada.";
    }

    public String aprobarSugerencia(int indice) {
        if (indice < 0 || indice >= sugerenciasMenu.size()) {
            return "Indice invalido.";
        }
        sugerenciasMenu.get(indice).aprobar();
        return "Sugerencia aprobada.";
    }

    public List<Sugerencia> getSugerenciasMenu() {
        return new ArrayList<>(sugerenciasMenu);
    }

    public void registrarSolicitudCambioTurno(SolicitudCambioTurno solicitud) {
        solicitudesCambio.add(solicitud);
    }

    public String aprobarSolicitudTurno(int indice, DayOfWeek dia) {
        if (indice < 0 || indice >= solicitudesCambio.size()) {
            return "Solicitud no encontrada.";
        }
        if (!horario.validarPersonalMinimo(dia)) {
            return "No se puede aprobar: no cumple personal minimo.";
        }
        solicitudesCambio.get(indice).aprobar();
        return "Solicitud aprobada.";
    }

    public List<SolicitudCambioTurno> getSolicitudesCambio() {
        return new ArrayList<>(solicitudesCambio);
    }

    public List<Prestamo> getPrestamosActivos() {
        return new ArrayList<>(prestamosActivos);
    }

    public List<Prestamo> getHistorialPrestamos() {
        return inventarioPrestamos.getHistorialCompleto();
    }

    public List<CopiaJuego> getCopiasPrestamo() {
        return inventarioPrestamos.getCopias();
    }

    public List<CopiaJuego> getCopiasVenta() {
        return inventarioVenta.getCopias();
    }

    public List<Venta> getVentasPorRubro(RubroVenta rubro) {
        return ventas.stream()
                .filter(v -> v.getRubro() == rubro)
                .toList();
    }

    public ReporteVentas generarReporteSimple() {
        return new ReporteVentas(LocalDate.now().minusMonths(1), LocalDate.now(), new ArrayList<>(ventas));
    }

    public double totalVentasPorRubro(RubroVenta rubro) {
        return ventas.stream()
                .filter(v -> v.getRubro() == rubro)
                .mapToDouble(Venta::getTotal)
                .sum();
    }

    public Map<String, Double> resumenFinancieroPorRubro(RubroVenta rubro) {
        double subtotal = 0.0;
        double impuestos = 0.0;
        double propinas = 0.0;
        for (Venta venta : ventas) {
            if (venta.getRubro() != rubro) {
                continue;
            }
            subtotal += venta.getSubtotal();
            impuestos += venta.getImpuesto();
            if (venta instanceof VentaCafe cafe) {
                propinas += cafe.getBase() * cafe.getPorcentajePropina();
            }
        }
        Map<String, Double> resumen = new HashMap<>();
        resumen.put("subtotal", subtotal);
        resumen.put("impuestos", impuestos);
        resumen.put("propinas", propinas);
        resumen.put("total", subtotal + impuestos + propinas);
        return resumen;
    }

    public List<ProductoCafe> getCafesCatalogo() {
        return new ArrayList<>(cafesCatalogo);
    }

    public ProductoCafe buscarCafePorId(String id) {
        return cafesCatalogo.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public VentaJuegos comprarJuegoPorMenu(Usuario comprador, int indice, int cantidad) {
        if (indice < 1 || indice > juegosCatalogo.size()) {
            return null;
        }
        JuegoDeMesa juego = juegosCatalogo.get(indice - 1);
        double base = juego.getPrecioVenta() * cantidad;
        return comprarJuego(comprador, base);
    }

    public VentaCafe comprarCafePorMenu(Usuario comprador, int indice, int cantidad, double propinaPorcentaje) {
        if (indice < 1 || indice > cafesCatalogo.size()) {
            return null;
        }
        ProductoCafe cafe = cafesCatalogo.get(indice - 1);
        double base = cafe.getPrecioBase() * cantidad;
        return comprarCafe(comprador, base, propinaPorcentaje);
    }

    private Mesero buscarMeseroCapacitado(JuegoDeMesa juego) {
        for (Usuario usuario : usuarios) {
            if (usuario instanceof Mesero mesero && mesero.puedeEnsenar(juego)) {
                return mesero;
            }
        }
        return null;
    }
}
