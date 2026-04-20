package persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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
import modelo.EstadoSolicitudTurno;
import modelo.JuegoDeMesa;
import modelo.Mesero;
import modelo.Prestamo;
import modelo.RestriccionEdad;
import modelo.RubroVenta;
import modelo.SolicitudCambioTurno;
import modelo.Sugerencia;
import modelo.TipoSolicitudTurno;
import modelo.Usuario;
import modelo.UsuarioBasico;
import modelo.Venta;
import modelo.VentaCafe;
import modelo.VentaJuegos;

public class FilePersistence {
    private static final String BASE_FOLDER = "data";
    private static final String USERS_FILE = "usuarios.txt";
    private static final String GAMES_FILE = "juegos.txt";
    private static final String SALES_FILE = "ventas.txt";
    private static final String LOAN_COPIES_FILE = "copias_prestamo.txt";
    private static final String SALE_COPIES_FILE = "copias_venta.txt";
    private static final String LOANS_FILE = "prestamos.txt";
    private static final String SHIFT_REQUESTS_FILE = "solicitudes_turno.txt";
    private static final String MENU_SUGGESTIONS_FILE = "sugerencias_menu.txt";

    private final Path dataFolder;

    public FilePersistence() {
        this.dataFolder = Paths.get(System.getProperty("user.dir"), BASE_FOLDER);
    }

    public AppData load() {
        ensureFolder();
        AppData data = new AppData();
        List<Usuario> usuarios = loadUsuarios();
        data.setUsuarios(usuarios);
        data.setJuegos(loadJuegos());
        data.setVentas(loadVentas(usuarios));
        Map<String, JuegoDeMesa> juegosMap = new HashMap<>();
        for (JuegoDeMesa juego : data.getJuegos()) {
            juegosMap.put(juego.getIdJuego(), juego);
        }
        List<CopiaJuego> copiasPrestamo = loadCopias(LOAN_COPIES_FILE, true, juegosMap);
        List<CopiaJuego> copiasVenta = loadCopias(SALE_COPIES_FILE, false, juegosMap);
        data.setCopiasPrestamo(copiasPrestamo);
        data.setCopiasVenta(copiasVenta);
        Map<String, CopiaJuego> copiasMap = new HashMap<>();
        for (CopiaJuego c : copiasPrestamo) {
            copiasMap.put(c.getCopyId(), c);
        }
        for (CopiaJuego c : copiasVenta) {
            copiasMap.put(c.getCopyId(), c);
        }
        data.setHistorialPrestamos(loadPrestamos(copiasMap, usuarios));
        data.setSolicitudesTurno(loadSolicitudesTurno());
        data.setSugerenciasMenu(loadSugerencias());
        return data;
    }

    public void save(AppData data) {
        ensureFolder();
        saveUsuarios(data.getUsuarios());
        saveJuegos(data.getJuegos());
        saveVentas(data.getVentas());
        saveCopias(LOAN_COPIES_FILE, data.getCopiasPrestamo());
        saveCopias(SALE_COPIES_FILE, data.getCopiasVenta());
        savePrestamos(data.getHistorialPrestamos());
        saveSolicitudesTurno(data.getSolicitudesTurno());
        saveSugerencias(data.getSugerenciasMenu());
    }

    public String getDataFolderPath() {
        return dataFolder.toAbsolutePath().toString();
    }

    private void ensureFolder() {
        try {
            Files.createDirectories(dataFolder);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear la carpeta de persistencia", e);
        }
    }

    private List<Usuario> loadUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        Path path = dataFolder.resolve(USERS_FILE);
        if (!Files.exists(path)) {
            return usuarios;
        }
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                String[] p = line.split(";", -1);
                String role = p[0];
                if ("ADMIN".equals(role)) {
                    usuarios.add(new Administrador(p[1], p[2], p[3]));
                } else if ("CLIENTE".equals(role)) {
                    Cliente cliente = new Cliente(p[1], p[2], p[3], p[4]);
                    cliente.setPuntosDeFidelidad((int) Double.parseDouble(p[5]));
                    usuarios.add(cliente);
                } else if ("MESERO".equals(role)) {
                    usuarios.add(new Mesero(p[1], p[2], p[3], p[4], p[5]));
                } else if ("COCINERO".equals(role)) {
                    usuarios.add(new Cocinero(p[1], p[2], p[3], p[4], p[5]));
                } else if ("BASICO".equals(role)) {
                    UsuarioBasico basico = new UsuarioBasico(p[1], p[2], p[3]);
                    if (p.length > 4 && !p[4].isBlank()) {
                        basico.setPuntosDeFidelidad((int) Double.parseDouble(p[4]));
                    }
                    usuarios.add(basico);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudieron cargar usuarios", e);
        }
        return usuarios;
    }

    private List<JuegoDeMesa> loadJuegos() {
        List<JuegoDeMesa> juegos = new ArrayList<>();
        Path path = dataFolder.resolve(GAMES_FILE);
        if (!Files.exists(path)) {
            return juegos;
        }
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                String[] p = line.split(";", -1);
                JuegoDeMesa juego = new JuegoDeMesa(
                        p[0], p[1], Integer.parseInt(p[2]), p[3],
                        Integer.parseInt(p[4]), Integer.parseInt(p[5]),
                        RestriccionEdad.valueOf(p[6]),
                        CategoriaJuego.valueOf(p[7]),
                        EstadoJuego.valueOf(p[8]),
                        Boolean.parseBoolean(p[9]));
                if (p.length > 10 && !p[10].isBlank()) {
                    juego.setPrecioVenta(Double.parseDouble(p[10]));
                }
                juegos.add(juego);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudieron cargar juegos", e);
        }
        return juegos;
    }

    private List<Venta> loadVentas(List<Usuario> usuarios) {
        List<Venta> ventas = new ArrayList<>();
        Path path = dataFolder.resolve(SALES_FILE);
        if (!Files.exists(path)) {
            return ventas;
        }
        Map<String, Usuario> usuariosMap = new HashMap<>();
        for (Usuario u : usuarios) {
            usuariosMap.put(u.getLogin(), u);
        }
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            int contador = 0;
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                String[] p = line.split(";", -1);
                Usuario usuarioVenta = p.length > 4 && !p[4].isBlank() ? usuariosMap.get(p[4]) : null;
                
                // Variar las fechas: cada venta obtiene una fecha diferente (entre 5 y 1 dias atras)
                long diasAtras = (contador % 5) + 1;
                java.time.LocalDateTime fecha = java.time.LocalDateTime.now().minusDays(diasAtras);
                
                if ("JUEGO".equals(p[0])) {
                    VentaJuegos venta = new VentaJuegos(p[1], Double.parseDouble(p[2]), usuarioVenta);
                    venta.aplicarDescuento(Double.parseDouble(p[3]));
                    venta.calcularTotal();
                    venta.setFecha(fecha);
                    ventas.add(venta);
                } else if ("CAFE".equals(p[0])) {
                    VentaCafe venta = new VentaCafe(p[1], Double.parseDouble(p[2]), Double.parseDouble(p[3]), usuarioVenta);
                    venta.calcularTotal();
                    venta.setFecha(fecha);
                    ventas.add(venta);
                }
                contador++;
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudieron cargar ventas", e);
        }
        return ventas;
    }

    private void saveUsuarios(List<Usuario> usuarios) {
        List<String> lines = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario instanceof Administrador) {
                lines.add("ADMIN;" + usuario.getLogin() + ";" + usuario.getPassword() + ";" + usuario.getId());
            } else if (usuario instanceof Cliente) {
                Cliente cliente = (Cliente) usuario;
                lines.add("CLIENTE;" + usuario.getLogin() + ";" + usuario.getPassword() + ";" + usuario.getId()
                        + ";" + cliente.getCodigoDeDescuento() + ";" + cliente.getPuntosDeFidelidad());
            } else if (usuario instanceof Mesero) {
                Empleado e = (Empleado) usuario;
                lines.add("MESERO;" + usuario.getLogin() + ";" + usuario.getPassword() + ";" + usuario.getId()
                        + ";" + e.getNombre() + ";" + e.getCodigoDescuento());
            } else if (usuario instanceof Cocinero) {
                Empleado e = (Empleado) usuario;
                lines.add("COCINERO;" + usuario.getLogin() + ";" + usuario.getPassword() + ";" + usuario.getId()
                        + ";" + e.getNombre() + ";" + e.getCodigoDescuento());
            } else if (usuario instanceof UsuarioBasico) {
                lines.add("BASICO;" + usuario.getLogin() + ";" + usuario.getPassword() + ";" + usuario.getId() + ";" + usuario.getPuntosDeFidelidad());
            }
        }
        writeLines(dataFolder.resolve(USERS_FILE), lines);
    }

    private void saveJuegos(List<JuegoDeMesa> juegos) {
        List<String> lines = new ArrayList<>();
        for (JuegoDeMesa juego : juegos) {
            lines.add(String.join(";",
                    juego.getIdJuego(),
                    juego.getNombre(),
                    String.valueOf(juego.getAnioPublicacion()),
                    juego.getEmpresaMatriz(),
                    String.valueOf(juego.getMinJugadores()),
                    String.valueOf(juego.getMaxJugadores()),
                    juego.getRestriccionEdad().name(),
                    juego.getCategoria().name(),
                    juego.getEstado().name(),
                    String.valueOf(juego.isDificil()),
                    String.valueOf(juego.getPrecioVenta())));
        }
        writeLines(dataFolder.resolve(GAMES_FILE), lines);
    }

    private void saveVentas(List<Venta> ventas) {
        List<String> lines = new ArrayList<>();
        for (Venta venta : ventas) {
            String usuarioLogin = (venta.getUsuario() != null) ? venta.getUsuario().getLogin() : "";
            if (venta instanceof VentaJuegos juegos) {
                lines.add("JUEGO;" + juegos.getVentaId() + ";" + juegos.getBase() + ";" + juegos.getDescuentoAplicado() + ";" + usuarioLogin);
            } else if (venta instanceof VentaCafe cafe) {
                lines.add("CAFE;" + cafe.getVentaId() + ";" + cafe.getBase() + ";" + cafe.getPorcentajePropina() + ";" + usuarioLogin);
            }
        }
        writeLines(dataFolder.resolve(SALES_FILE), lines);
    }

    private List<CopiaJuego> loadCopias(String fileName, boolean inventarioPrestamo, Map<String, JuegoDeMesa> juegosMap) {
        List<CopiaJuego> copias = new ArrayList<>();
        Path path = dataFolder.resolve(fileName);
        if (!Files.exists(path)) {
            return copias;
        }
        try {
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                if (line.isBlank()) {
                    continue;
                }
                String[] p = line.split(";", -1);
                JuegoDeMesa juego = juegosMap.get(p[1]);
                if (juego == null) {
                    continue;
                }
                boolean disponible = Boolean.parseBoolean(p[2]);
                int vecesPrestado = Integer.parseInt(p[3]);
                CopiaJuego copia = new CopiaJuego(p[0], juego, disponible, inventarioPrestamo, !inventarioPrestamo, vecesPrestado);
                copias.add(copia);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudieron cargar copias de inventario", e);
        }
        return copias;
    }

    private void saveCopias(String fileName, List<CopiaJuego> copias) {
        List<String> lines = new ArrayList<>();
        for (CopiaJuego copia : copias) {
            lines.add(String.join(";",
                    copia.getCopyId(),
                    copia.getJuego().getIdJuego(),
                    String.valueOf(copia.isDisponible()),
                    String.valueOf(copia.getVecesPrestado())));
        }
        writeLines(dataFolder.resolve(fileName), lines);
    }

    private List<Prestamo> loadPrestamos(Map<String, CopiaJuego> copiasMap, List<Usuario> usuarios) {
        List<Prestamo> prestamos = new ArrayList<>();
        Path path = dataFolder.resolve(LOANS_FILE);
        if (!Files.exists(path)) {
            return prestamos;
        }
        Map<String, Usuario> usuariosMap = new HashMap<>();
        for (Usuario u : usuarios) {
            usuariosMap.put(u.getLogin(), u);
        }
        try {
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                if (line.isBlank()) {
                    continue;
                }
                String[] p = line.split(";", -1);
                CopiaJuego copia = copiasMap.get(p[1]);
                if (copia == null) {
                    continue;
                }
                LocalDateTime fechaPrestamo = LocalDateTime.parse(p[2]);
                LocalDateTime fechaDevolucion = p[3].isBlank() ? null : LocalDateTime.parse(p[3]);
                boolean advertencia = Boolean.parseBoolean(p[4]);
                boolean activo = Boolean.parseBoolean(p[5]);
                Usuario usuarioPrestamo = p.length > 6 && !p[6].isBlank() ? usuariosMap.get(p[6]) : null;
                Prestamo prestamo = new Prestamo(p[0], copia, fechaPrestamo, fechaDevolucion, advertencia, activo, usuarioPrestamo);
                prestamos.add(prestamo);
                if (activo) {
                    copia.setDisponible(false);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudieron cargar prestamos", e);
        }
        return prestamos;
    }

    private void savePrestamos(List<Prestamo> prestamos) {
        List<String> lines = new ArrayList<>();
        for (Prestamo prestamo : prestamos) {
            String fechaDevolucion = prestamo.getFechaDevolucion() == null ? "" : prestamo.getFechaDevolucion().toString();
            String usuarioLogin = (prestamo.getUsuario() != null) ? prestamo.getUsuario().getLogin() : "";
            lines.add(String.join(";",
                    prestamo.getPrestamoId(),
                    prestamo.getCopia().getCopyId(),
                    prestamo.getFechaPrestamo().toString(),
                    fechaDevolucion,
                    String.valueOf(prestamo.isAdvertenciaDificultad()),
                    String.valueOf(prestamo.estaActivo()),
                    usuarioLogin));
        }
        writeLines(dataFolder.resolve(LOANS_FILE), lines);
    }

    private List<SolicitudCambioTurno> loadSolicitudesTurno() {
        List<SolicitudCambioTurno> solicitudes = new ArrayList<>();
        Path path = dataFolder.resolve(SHIFT_REQUESTS_FILE);
        if (!Files.exists(path)) {
            return solicitudes;
        }
        try {
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                if (line.isBlank()) {
                    continue;
                }
                String[] p = line.split(";", -1);
                solicitudes.add(new SolicitudCambioTurno(
                        p[0],
                        TipoSolicitudTurno.valueOf(p[1]),
                        EstadoSolicitudTurno.valueOf(p[2])));
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudieron cargar solicitudes de turno", e);
        }
        return solicitudes;
    }

    private void saveSolicitudesTurno(List<SolicitudCambioTurno> solicitudes) {
        List<String> lines = new ArrayList<>();
        for (SolicitudCambioTurno solicitud : solicitudes) {
            lines.add(String.join(";",
                    solicitud.getIdCambioTurno(),
                    solicitud.getTipo().name(),
                    solicitud.getEstado().name()));
        }
        writeLines(dataFolder.resolve(SHIFT_REQUESTS_FILE), lines);
    }

    private List<Sugerencia> loadSugerencias() {
        List<Sugerencia> sugerencias = new ArrayList<>();
        Path path = dataFolder.resolve(MENU_SUGGESTIONS_FILE);
        if (!Files.exists(path)) {
            return sugerencias;
        }
        try {
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                if (line.isBlank()) {
                    continue;
                }
                String[] p = line.split(";", -1);
                sugerencias.add(new Sugerencia(p[0], p[1], Boolean.parseBoolean(p[2])));
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudieron cargar sugerencias", e);
        }
        return sugerencias;
    }

    private void saveSugerencias(List<Sugerencia> sugerencias) {
        List<String> lines = new ArrayList<>();
        for (Sugerencia sugerencia : sugerencias) {
            lines.add(String.join(";",
                    sugerencia.getNombrePropuesto(),
                    sugerencia.getDescripcion(),
                    String.valueOf(sugerencia.isAprobada())));
        }
        writeLines(dataFolder.resolve(MENU_SUGGESTIONS_FILE), lines);
    }

    private void writeLines(Path path, List<String> lines) {
        try {
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar archivo: " + path.getFileName(), e);
        }
    }
}
