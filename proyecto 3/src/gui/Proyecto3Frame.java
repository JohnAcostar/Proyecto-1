package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import modelo.Administrador;
import modelo.Cliente;
import modelo.Empleado;
import modelo.JuegoDeMesa;
import modelo.Prestamo;
import modelo.ProductoCafe;
import modelo.RubroVenta;
import modelo.SolicitudCambioTurno;
import modelo.Torneo;
import modelo.Usuario;
import modelo.Venta;
import modelo.VentaCafe;
import modelo.VentaJuegos;
import persistence.AppData;
import persistence.FilePersistence;
import service.SistemaCafe;
import service.ServicioTorneos;

public class Proyecto3Frame extends JFrame {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Color BACKGROUND = new Color(246, 247, 249);
    private static final Color PANEL = Color.WHITE;
    private static final Color INK = new Color(31, 41, 55);
    private static final Color MUTED = new Color(92, 100, 112);

    private final SistemaCafe sistema;
    private final ServicioTorneos servicioTorneos;
    private final FilePersistence persistence;
    private Usuario usuarioActual;

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private JLabel sessionLabel;
    private DefaultTableModel juegosModel;
    private DefaultTableModel cafeModel;
    private DefaultTableModel prestamosModel;
    private DefaultTableModel ventasModel;
    private DefaultTableModel turnosModel;
    private DefaultTableModel torneosModel;
    private JTextArea historialArea;
    private JComboBox<JuegoDeMesa> chartGameCombo;
    private PieChartPanel pieChart;
    private BarChartPanel barChart;
    private LineChartPanel lineChart;

    public Proyecto3Frame(SistemaCafe sistema, ServicioTorneos servicioTorneos, FilePersistence persistence) {
        super("Proyecto 3 - Dulces & Dados");
        this.sistema = sistema;
        this.servicioTorneos = servicioTorneos;
        this.persistence = persistence;
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 720));
        setContentPane(root);
        root.add(buildLoginPanel(), "login");
        root.add(buildAppPanel(), "app");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                guardarTodo();
                dispose();
            }
        });
        cards.show(root, "login");
    }

    private JPanel buildLoginPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BACKGROUND);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(219, 223, 230)),
                BorderFactory.createEmptyBorder(28, 32, 28, 32)));

        JLabel title = new JLabel("Dulces & Dados");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        title.setForeground(INK);

        JLabel subtitle = new JLabel("Proyecto 3 - Interfaz grafica Swing");
        subtitle.setForeground(MUTED);

        JTextField login = new JTextField(20);
        JPasswordField password = new JPasswordField(20);
        JButton ingresar = new JButton("Iniciar sesion");
        JButton crear = new JButton("Crear cuenta basica");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(title, gbc);
        gbc.gridy++;
        panel.add(subtitle, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(new JLabel("Usuario"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(login, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(password, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(ingresar, gbc);
        gbc.gridy++;
        panel.add(crear, gbc);

        ingresar.addActionListener(e -> {
            Usuario usuario = sistema.autenticar(login.getText().trim(), new String(password.getPassword()).trim());
            if (usuario == null) {
                showInfo("Credenciales incorrectas.");
                return;
            }
            usuarioActual = usuario;
            sessionLabel.setText("Sesion: " + usuario.getLogin() + " (" + roleName(usuario) + ")");
            refreshAll();
            cards.show(root, "app");
        });
        crear.addActionListener(e -> {
            String nuevoLogin = login.getText().trim();
            String nuevaClave = new String(password.getPassword()).trim();
            if (nuevoLogin.isBlank() || nuevaClave.isBlank()) {
                showInfo("Ingrese usuario y password para crear la cuenta.");
                return;
            }
            if (sistema.crearUsuarioBasico(nuevoLogin, nuevaClave)) {
                guardarTodo();
                showInfo("Cuenta basica creada. Ahora puede iniciar sesion.");
            } else {
                showInfo("No fue posible crear la cuenta. Revise si el usuario ya existe.");
            }
        });

        wrapper.add(panel);
        return wrapper;
    }

    private JPanel buildAppPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Dulces & Dados");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        title.setForeground(INK);
        sessionLabel = new JLabel("Sesion:");
        JButton logout = new JButton("Cerrar sesion");
        logout.addActionListener(e -> {
            guardarTodo();
            usuarioActual = null;
            cards.show(root, "login");
        });
        JPanel session = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        session.setOpaque(false);
        session.add(sessionLabel);
        session.add(logout);
        header.add(title, BorderLayout.WEST);
        header.add(session, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Catalogo", buildCatalogPanel());
        tabs.addTab("Operaciones", buildOperationsPanel());
        tabs.addTab("Reportes", buildReportsPanel());
        tabs.addTab("Graficas", buildChartsPanel());
        tabs.addTab("Torneos", buildTournamentsPanel());

        panel.add(header, BorderLayout.NORTH);
        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);

        juegosModel = new DefaultTableModel(new String[] {
                "ID", "Juego", "Empresa", "Anio", "Precio", "Categoria", "Edad", "Jugadores", "Estado", "Prestamo", "Venta"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = styledTable(juegosModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        JButton refrescar = new JButton("Actualizar");
        JButton mover = new JButton("Mover venta a prestamo");
        JButton desaparecido = new JButton("Marcar desaparecido");
        refrescar.addActionListener(e -> refreshAll());
        mover.addActionListener(e -> withSelectedGame(table, juego -> {
            showInfo(sistema.moverJuegoDeVentaAPrestamo(juego.getIdJuego()));
            guardarYRefrescar();
        }));
        desaparecido.addActionListener(e -> withSelectedGame(table, juego -> {
            showInfo(sistema.marcarJuegoDesaparecido(juego.getIdJuego()));
            guardarYRefrescar();
        }));
        actions.add(refrescar);
        actions.add(mover);
        actions.add(desaparecido);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildOperationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);

        JPanel forms = new JPanel();
        forms.setOpaque(false);
        forms.setLayout(new BoxLayout(forms, BoxLayout.Y_AXIS));
        forms.add(buildPrestamoPanel());
        forms.add(Box.createVerticalStrut(10));
        forms.add(buildCompraPanel());
        forms.add(Box.createVerticalStrut(10));
        forms.add(buildTurnosPanel());

        JPanel history = new JPanel(new BorderLayout(8, 8));
        history.setBackground(PANEL);
        history.setBorder(panelBorder("Historial del usuario"));
        historialArea = new JTextArea(12, 34);
        historialArea.setEditable(false);
        history.add(new JScrollPane(historialArea), BorderLayout.CENTER);

        panel.add(forms, BorderLayout.CENTER);
        panel.add(history, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildPrestamoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL);
        panel.setBorder(panelBorder("Prestamos y reservas"));
        JComboBox<JuegoDeMesa> juegos = new JComboBox<>();
        JSpinner personas = new JSpinner(new SpinnerNumberModel(2, 1, 20, 1));
        JButton reservar = new JButton("Reservar y prestar");
        JButton prestarBasico = new JButton("Prestar sin reserva");
        JButton devolver = new JButton("Devolver prestamo");
        prestamosModel = new DefaultTableModel(new String[] { "ID", "Juego", "Usuario", "Fecha", "Activo" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable prestamos = styledTable(prestamosModel);

        reservar.addActionListener(e -> {
            if (!(usuarioActual instanceof Cliente cliente)) {
                showInfo("La reserva con validacion de mesa esta disponible para clientes.");
                return;
            }
            JuegoDeMesa juego = (JuegoDeMesa) juegos.getSelectedItem();
            if (juego == null) {
                return;
            }
            String resultado = sistema.reservarYPrestarJuego(cliente, juego.getIdJuego(),
                    ((Number) personas.getValue()).intValue(), false, false, false);
            showInfo(resultado);
            guardarYRefrescar();
        });
        prestarBasico.addActionListener(e -> {
            if (usuarioActual == null || usuarioActual instanceof Administrador) {
                showInfo("Seleccione una sesion de cliente, usuario basico o empleado.");
                return;
            }
            JuegoDeMesa juego = (JuegoDeMesa) juegos.getSelectedItem();
            if (juego == null) {
                return;
            }
            String resultado = usuarioActual instanceof Empleado empleado
                    ? sistema.prestarJuegoAEmpleado(empleado, juego.getIdJuego(), false)
                    : sistema.prestarJuegoAUsuarioBasico(usuarioActual, juego.getIdJuego());
            showInfo(resultado);
            guardarYRefrescar();
        });
        devolver.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(this, "ID del prestamo a devolver:");
            if (id == null || id.isBlank()) {
                return;
            }
            String resultado = usuarioActual instanceof Administrador
                    ? sistema.devolverPrestamo(id.trim())
                    : sistema.devolverPrestamo(id.trim(), usuarioActual);
            showInfo(resultado);
            guardarYRefrescar();
        });

        GridBagConstraints gbc = baseGbc();
        panel.add(new JLabel("Juego"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(juegos, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Personas"), gbc);
        gbc.gridx = 3;
        panel.add(personas, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(reservar, gbc);
        gbc.gridx = 1;
        panel.add(prestarBasico, gbc);
        gbc.gridx = 2;
        panel.add(devolver, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(prestamos), gbc);
        panel.putClientProperty("juegosCombo", juegos);
        return panel;
    }

    private JPanel buildCompraPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL);
        panel.setBorder(panelBorder("Compras"));
        JComboBox<JuegoDeMesa> juegos = new JComboBox<>();
        JComboBox<ProductoCafe> cafes = new JComboBox<>();
        JSpinner cantidadJuego = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        JSpinner cantidadCafe = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        JSpinner propina = new JSpinner(new SpinnerNumberModel(10, 0, 50, 1));
        JButton comprarJuego = new JButton("Comprar juego");
        JButton comprarCafe = new JButton("Comprar cafe");
        JButton puntosCafe = new JButton("Canjear cafe");
        JButton puntosJuego = new JButton("Canjear juego");

        comprarJuego.addActionListener(e -> {
            JuegoDeMesa juego = (JuegoDeMesa) juegos.getSelectedItem();
            if (juego == null || usuarioActual == null) {
                return;
            }
            int index = sistema.getJuegosCatalogo().indexOf(juego) + 1;
            VentaJuegos venta = sistema.comprarJuegoPorMenu(usuarioActual, index, ((Number) cantidadJuego.getValue()).intValue());
            showInfo(venta == null ? "No fue posible registrar la venta." : "Venta registrada: " + money(venta.getTotal()));
            guardarYRefrescar();
        });
        comprarCafe.addActionListener(e -> {
            ProductoCafe cafe = (ProductoCafe) cafes.getSelectedItem();
            if (cafe == null || usuarioActual == null) {
                return;
            }
            int index = sistema.getCafesCatalogo().indexOf(cafe) + 1;
            double porcentaje = ((Number) propina.getValue()).doubleValue() / 100.0;
            VentaCafe venta = sistema.comprarCafePorMenu(usuarioActual, index, ((Number) cantidadCafe.getValue()).intValue(), porcentaje);
            showInfo(venta == null ? "Operacion no permitida." : "Venta registrada: " + money(venta.getTotal()));
            guardarYRefrescar();
        });
        puntosCafe.addActionListener(e -> {
            if (!(usuarioActual instanceof Cliente cliente)) {
                showInfo("Solo los clientes pueden canjear puntos.");
                return;
            }
            ProductoCafe cafe = (ProductoCafe) cafes.getSelectedItem();
            int index = sistema.getCafesCatalogo().indexOf(cafe) + 1;
            VentaCafe venta = sistema.comprarCafeConPuntos(cliente, index);
            showInfo(venta == null ? "No hay puntos suficientes o cafe invalido." : "Cafe canjeado.");
            guardarYRefrescar();
        });
        puntosJuego.addActionListener(e -> {
            if (!(usuarioActual instanceof Cliente cliente)) {
                showInfo("Solo los clientes pueden canjear puntos.");
                return;
            }
            JuegoDeMesa juego = (JuegoDeMesa) juegos.getSelectedItem();
            VentaJuegos venta = sistema.comprarJuegoConPuntos(cliente, juego.getIdJuego());
            showInfo(venta == null ? "No hay puntos suficientes o juego invalido." : "Juego canjeado.");
            guardarYRefrescar();
        });

        GridBagConstraints gbc = baseGbc();
        panel.add(new JLabel("Juego"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(juegos, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Cantidad"), gbc);
        gbc.gridx = 3;
        panel.add(cantidadJuego, gbc);
        gbc.gridx = 4;
        panel.add(comprarJuego, gbc);
        gbc.gridx = 5;
        panel.add(puntosJuego, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Cafe"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(cafes, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Cantidad"), gbc);
        gbc.gridx = 3;
        panel.add(cantidadCafe, gbc);
        gbc.gridx = 4;
        panel.add(new JLabel("Propina %"), gbc);
        gbc.gridx = 5;
        panel.add(propina, gbc);
        gbc.gridx = 4;
        gbc.gridy = 2;
        panel.add(comprarCafe, gbc);
        gbc.gridx = 5;
        panel.add(puntosCafe, gbc);

        panel.putClientProperty("juegosCombo", juegos);
        panel.putClientProperty("cafesCombo", cafes);
        return panel;
    }

    private JPanel buildTurnosPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(PANEL);
        panel.setBorder(panelBorder("Solicitudes de turno"));
        turnosModel = new DefaultTableModel(new String[] { "#", "Empleado", "Tipo", "Estado" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable turnos = styledTable(turnosModel);
        JButton solicitar = new JButton("Solicitar cambio");
        JButton aprobar = new JButton("Aprobar seleccion");
        solicitar.addActionListener(e -> {
            if (!(usuarioActual instanceof Empleado empleado)) {
                showInfo("Solo empleados pueden solicitar cambios de turno.");
                return;
            }
            SolicitudCambioTurno solicitud = empleado.solicitarCambioTurno(modelo.TipoSolicitudTurno.CAMBIO);
            solicitud.setEmpleadoOrigen(empleado);
            sistema.registrarSolicitudCambioTurno(solicitud);
            guardarYRefrescar();
        });
        aprobar.addActionListener(e -> {
            if (!(usuarioActual instanceof Administrador)) {
                showInfo("Solo administradores pueden aprobar solicitudes.");
                return;
            }
            int row = turnos.getSelectedRow();
            if (row < 0) {
                showInfo("Seleccione una solicitud.");
                return;
            }
            int index = (Integer) turnosModel.getValueAt(turnos.convertRowIndexToModel(row), 0);
            showInfo(sistema.aprobarSolicitudTurno(index, DayOfWeek.MONDAY));
            guardarYRefrescar();
        });
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.setOpaque(false);
        buttons.add(solicitar);
        buttons.add(aprobar);
        panel.add(new JScrollPane(turnos), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);
        ventasModel = new DefaultTableModel(new String[] { "ID", "Fecha", "Rubro", "Subtotal", "Impuesto", "Total", "Usuario" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable ventas = styledTable(ventasModel);
        JPanel totals = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totals.setOpaque(false);
        JButton refresh = new JButton("Actualizar reportes");
        refresh.addActionListener(e -> refreshAll());
        totals.add(refresh);
        panel.add(new JScrollPane(ventas), BorderLayout.CENTER);
        panel.add(totals, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildChartsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);

        JPanel selector = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selector.setOpaque(false);
        chartGameCombo = new JComboBox<>();
        JButton refresh = new JButton("Actualizar graficas");
        refresh.addActionListener(e -> refreshCharts());
        chartGameCombo.addActionListener(e -> refreshCharts());
        selector.add(new JLabel("Juego para disponibilidad"));
        selector.add(chartGameCombo);
        selector.add(refresh);

        JPanel charts = new JPanel(new GridBagLayout());
        charts.setOpaque(false);
        pieChart = new PieChartPanel("Disponibilidad por juego");
        barChart = new BarChartPanel("Ventas netas por periodo");
        lineChart = new LineChartPanel("Reservas de la semana");

        GridBagConstraints gbc = baseGbc();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        charts.add(wrapChart(pieChart), gbc);
        gbc.gridx = 1;
        charts.add(wrapChart(barChart), gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        charts.add(wrapChart(lineChart), gbc);

        panel.add(selector, BorderLayout.NORTH);
        panel.add(charts, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildTournamentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BACKGROUND);
        torneosModel = new DefaultTableModel(new String[] { "Nombre", "Juego", "Tipo", "Estado", "Participantes" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        panel.add(new JScrollPane(styledTable(torneosModel)), BorderLayout.CENTER);
        return panel;
    }

    private JPanel wrapChart(JPanel chart) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(219, 223, 230)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.add(chart, BorderLayout.CENTER);
        return panel;
    }

    private void refreshAll() {
        refreshCombos(root);
        refreshGames();
        refreshPrestamos();
        refreshVentas();
        refreshTurnos();
        refreshTorneos();
        refreshHistorial();
        refreshCharts();
    }

    private void refreshCombos(Component component) {
        if (component instanceof JPanel panel) {
            Object juegosCombo = panel.getClientProperty("juegosCombo");
            if (juegosCombo instanceof JComboBox<?> combo) {
                refillCombo(combo, sistema.getJuegosCatalogo());
            }
            Object cafesCombo = panel.getClientProperty("cafesCombo");
            if (cafesCombo instanceof JComboBox<?> combo) {
                refillCombo(combo, sistema.getCafesCatalogo());
            }
            for (Component child : panel.getComponents()) {
                refreshCombos(child);
            }
        } else if (component instanceof JScrollPane scroll && scroll.getViewport().getView() != null) {
            refreshCombos(scroll.getViewport().getView());
        } else if (component instanceof JTabbedPane tabs) {
            for (int i = 0; i < tabs.getTabCount(); i++) {
                refreshCombos(tabs.getComponentAt(i));
            }
        }
        if (chartGameCombo != null) {
            refillCombo(chartGameCombo, sistema.getJuegosCatalogo());
        }
    }

    private <T> void refillCombo(JComboBox<?> rawCombo, List<T> values) {
        @SuppressWarnings("unchecked")
        JComboBox<T> combo = (JComboBox<T>) rawCombo;
        Object selected = combo.getSelectedItem();
        combo.removeAllItems();
        for (T value : values) {
            combo.addItem(value);
        }
        if (selected != null) {
            combo.setSelectedItem(selected);
        }
    }

    private void refreshGames() {
        if (juegosModel == null) {
            return;
        }
        juegosModel.setRowCount(0);
        for (JuegoDeMesa juego : sistema.getJuegosCatalogo()) {
            juegosModel.addRow(new Object[] {
                    juego.getIdJuego(), juego.getNombre(), juego.getEmpresaMatriz(), juego.getAnioPublicacion(),
                    money(juego.getPrecioVenta()), juego.getCategoria(), juego.getRestriccionEdad(),
                    juego.getMinJugadores() + "-" + juego.getMaxJugadores(), juego.getEstado(),
                    sistema.disponibilidadPrestamo(juego.getIdJuego()), sistema.disponibilidadVenta(juego.getIdJuego())
            });
        }
    }

    private void refreshPrestamos() {
        if (prestamosModel == null) {
            return;
        }
        prestamosModel.setRowCount(0);
        for (Prestamo prestamo : sistema.getHistorialPrestamos()) {
            String usuario = prestamo.getUsuario() == null ? "" : prestamo.getUsuario().getLogin();
            prestamosModel.addRow(new Object[] {
                    prestamo.getPrestamoId(), prestamo.getCopia().getJuego().getNombre(), usuario,
                    prestamo.getFechaPrestamo().toLocalDate().format(DATE_FORMAT), prestamo.estaActivo() ? "Si" : "No"
            });
        }
    }

    private void refreshVentas() {
        if (ventasModel == null) {
            return;
        }
        ventasModel.setRowCount(0);
        for (Venta venta : sistema.getVentas()) {
            ventasModel.addRow(new Object[] {
                    venta.getVentaId(), venta.getFecha().toLocalDate().format(DATE_FORMAT), venta.getRubro(),
                    money(venta.getSubtotal()), money(venta.getImpuesto()), money(venta.getTotal()),
                    venta.getUsuario() == null ? "" : venta.getUsuario().getLogin()
            });
        }
    }

    private void refreshTurnos() {
        if (turnosModel == null) {
            return;
        }
        turnosModel.setRowCount(0);
        List<SolicitudCambioTurno> solicitudes = sistema.getSolicitudesCambio();
        for (int i = 0; i < solicitudes.size(); i++) {
            SolicitudCambioTurno solicitud = solicitudes.get(i);
            String empleado = solicitud.getEmpleadoOrigen() == null ? "" : solicitud.getEmpleadoOrigen().getNombre();
            turnosModel.addRow(new Object[] { i, empleado, solicitud.getTipo(), solicitud.getEstado() });
        }
    }

    private void refreshTorneos() {
        if (torneosModel == null) {
            return;
        }
        torneosModel.setRowCount(0);
        for (Torneo torneo : servicioTorneos.obtenerTodosTorneos()) {
            torneosModel.addRow(new Object[] {
                    torneo.getNombre(), torneo.getNombreJuego(), torneo.getTipo(), torneo.getEstado(),
                    torneo.getParticipantes().size()
            });
        }
    }

    private void refreshHistorial() {
        if (historialArea == null || usuarioActual == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Usuario: ").append(usuarioActual.getLogin()).append('\n');
        if (usuarioActual instanceof Cliente cliente) {
            sb.append("Puntos: ").append((int) cliente.getPuntosDeFidelidad()).append("\n\n");
        }
        sb.append("Compras\n");
        for (Venta venta : usuarioActual.getHistorialVentas()) {
            sb.append("- ").append(venta.getVentaId()).append(" | ").append(venta.getRubro())
                    .append(" | ").append(money(venta.getTotal())).append('\n');
        }
        sb.append("\nPrestamos\n");
        for (Prestamo prestamo : usuarioActual.getHistorialPrestamos()) {
            sb.append("- ").append(prestamo.getPrestamoId()).append(" | ")
                    .append(prestamo.getCopia().getJuego().getNombre()).append(" | ")
                    .append(prestamo.estaActivo() ? "activo" : "cerrado").append('\n');
        }
        historialArea.setText(sb.toString());
    }

    private void refreshCharts() {
        if (pieChart == null || barChart == null || lineChart == null) {
            return;
        }
        JuegoDeMesa juego = (JuegoDeMesa) chartGameCombo.getSelectedItem();
        Map<String, Double> pie = new LinkedHashMap<>();
        if (juego != null) {
            pie.put("Copias venta", (double) sistema.disponibilidadVenta(juego.getIdJuego()));
            pie.put("Copias prestamo", (double) sistema.disponibilidadPrestamo(juego.getIdJuego()));
            pieChart.setTitle("Disponibilidad " + juego.getNombre());
        }
        pieChart.setValues(pie);

        LocalDate start = LocalDate.now().minusDays(4);
        List<String> labels = new ArrayList<>();
        List<Double> cafe = new ArrayList<>();
        List<Double> juegos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            LocalDate day = start.plusDays(i);
            labels.add(day.format(DateTimeFormatter.ofPattern("dd/MM")));
            cafe.add(totalNeto(day, RubroVenta.CAFE));
            juegos.add(totalNeto(day, RubroVenta.JUEGO));
        }
        barChart.setData(labels, cafe, juegos);

        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        List<String> dias = List.of("Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom");
        List<Double> reservas = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate day = monday.plusDays(i);
            reservas.add((double) sistema.getHistorialPrestamos().stream()
                    .filter(p -> p.getFechaPrestamo().toLocalDate().equals(day))
                    .count());
        }
        lineChart.setData(dias, reservas);
    }

    private double totalNeto(LocalDate day, RubroVenta rubro) {
        return sistema.getVentas().stream()
                .filter(v -> v.getRubro() == rubro && v.getFecha().toLocalDate().equals(day))
                .mapToDouble(v -> Math.max(0, v.getTotal() - v.getImpuesto()))
                .sum();
    }

    private void guardarYRefrescar() {
        guardarTodo();
        refreshAll();
    }

    private void guardarTodo() {
        AppData data = new AppData();
        data.setUsuarios(sistema.getUsuarios());
        data.setJuegos(sistema.getJuegosCatalogo());
        data.setVentas(sistema.getVentas());
        data.setCopiasPrestamo(sistema.getCopiasPrestamo());
        data.setCopiasVenta(sistema.getCopiasVenta());
        data.setHistorialPrestamos(sistema.getHistorialPrestamos());
        data.setSolicitudesTurno(sistema.getSolicitudesCambio());
        data.setSugerenciasMenu(sistema.getSugerenciasMenu());
        data.setTorneos(servicioTorneos.obtenerTodosTorneos());
        data.setVouchersDescuento(servicioTorneos.obtenerTodosVouchers());
        persistence.save(data);
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setVerticalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, renderer);
        return table;
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private javax.swing.border.Border panelBorder(String title) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(219, 223, 230)), title),
                BorderFactory.createEmptyBorder(8, 8, 8, 8));
    }

    private void withSelectedGame(JTable table, GameAction action) {
        if (!(usuarioActual instanceof Administrador)) {
            showInfo("Esta accion es solo para administradores.");
            return;
        }
        int row = table.getSelectedRow();
        if (row < 0) {
            showInfo("Seleccione un juego.");
            return;
        }
        String id = (String) juegosModel.getValueAt(table.convertRowIndexToModel(row), 0);
        JuegoDeMesa juego = sistema.buscarJuegoPorId(id);
        if (juego != null) {
            action.run(juego);
        }
    }

    private String roleName(Usuario usuario) {
        if (usuario instanceof Administrador) {
            return "Administrador";
        }
        if (usuario instanceof Cliente) {
            return "Cliente";
        }
        if (usuario instanceof Empleado) {
            return "Empleado";
        }
        return "Usuario basico";
    }

    private String money(double value) {
        return String.format(Locale.US, "$%,.0f", value);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    @FunctionalInterface
    private interface GameAction {
        void run(JuegoDeMesa juego);
    }
}
