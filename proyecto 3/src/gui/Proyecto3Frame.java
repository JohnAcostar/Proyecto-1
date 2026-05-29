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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import modelo.ParticipanteTorneo;
import modelo.Prestamo;
import modelo.ProductoCafe;
import modelo.ResultadoValidacion;
import modelo.RubroVenta;
import modelo.SolicitudCambioTurno;
import modelo.TipoSolicitudTurno;
import modelo.TipoTorneo;
import modelo.Torneo;
import modelo.Usuario;
import modelo.Venta;
import modelo.VentaCafe;
import modelo.VentaJuegos;
import modelo.VoucherDescuento;
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
    private static final Color SIDEBAR = new Color(248, 250, 252);
    private static final Color ACCENT = new Color(5, 122, 85);
    private static final Color ACCENT_SOFT = new Color(225, 245, 237);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final String CATALOG_CARD = "catalogo";
    private static final String OPERATIONS_CARD = "operaciones";
    private static final String REPORTS_CARD = "reportes";
    private static final String LOANS_CARD = "prestamos";
    private static final String CHARTS_CARD = "graficas";
    private static final String TOURNAMENTS_CARD = "torneos";

    private final SistemaCafe sistema;
    private final ServicioTorneos servicioTorneos;
    private final FilePersistence persistence;
    private Usuario usuarioActual;

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final CardLayout contentCards = new CardLayout();
    private JPanel contentPanel;
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private Component reportsTab;
    private Component chartsTab;
    private JLabel sessionLabel;
    private JLabel reportCountValue;
    private JLabel reportTotalValue;
    private JLabel reportTaxValue;
    private JLabel reportScopeValue;
    private JLabel loanCountValue;
    private JLabel loanActiveValue;
    private JLabel loanReturnedValue;
    private JLabel loanScopeValue;
    private JComboBox<String> reportRubroFilter;
    private DefaultTableModel juegosModel;
    private DefaultTableModel cafeModel;
    private DefaultTableModel prestamosModel;
    private DefaultTableModel prestamosReporteModel;
    private DefaultTableModel ventasModel;
    private DefaultTableModel turnosModel;
    private DefaultTableModel torneosModel;
    private JTextArea historialArea;
    private JTextArea torneosDetalleArea;
    private JPanel turnosPanel;
    private JLabel tipoSolicitudLabel;
    private JComboBox<TipoSolicitudTurno> tipoSolicitudCombo;
    private JButton solicitarTurnoButton;
    private JButton aprobarTurnoButton;
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
            configureRoleTabs();
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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND);
        panel.add(buildSidebar(), BorderLayout.WEST);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND);
        header.setBorder(BorderFactory.createEmptyBorder(24, 28, 12, 28));
        JLabel title = new JLabel("Dulces & Dados");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        title.setForeground(INK);
        sessionLabel = new JLabel("Sesion:");
        sessionLabel.setForeground(MUTED);
        JButton logout = new JButton("Cerrar sesion");
        styleSecondaryButton(logout);
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

        contentPanel = new JPanel(contentCards);
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 28, 28, 28));
        reportsTab = buildReportsPanel();
        chartsTab = buildChartsPanel();
        contentPanel.add(buildCatalogPanel(), CATALOG_CARD);
        contentPanel.add(buildOperationsPanel(), OPERATIONS_CARD);
        contentPanel.add(reportsTab, REPORTS_CARD);
        contentPanel.add(buildLoansPanel(), LOANS_CARD);
        contentPanel.add(chartsTab, CHARTS_CARD);
        contentPanel.add(buildTournamentsPanel(), TOURNAMENTS_CARD);
        selectNavigation(CATALOG_CARD);

        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.add(header, BorderLayout.NORTH);
        main.add(contentPanel, BorderLayout.CENTER);
        panel.add(main, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(SIDEBAR);
        sidebar.setPreferredSize(new Dimension(228, 720));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(BorderFactory.createEmptyBorder(26, 18, 18, 18));

        JLabel brand = new JLabel("Dulces & Dados");
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.setFont(brand.getFont().deriveFont(Font.BOLD, 21f));
        brand.setForeground(ACCENT);
        top.add(brand);
        top.add(Box.createVerticalStrut(24));
        top.add(sectionLabel("MENU"));
        top.add(Box.createVerticalStrut(8));
        top.add(navButton("Catalogo", CATALOG_CARD));
        top.add(navButton("Operaciones", OPERATIONS_CARD));
        top.add(navButton("Reportes", REPORTS_CARD));
        top.add(navButton("Prestamos", LOANS_CARD));
        top.add(navButton("Graficas", CHARTS_CARD));
        top.add(navButton("Torneos", TOURNAMENTS_CARD));

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(BorderFactory.createEmptyBorder(18, 18, 24, 18));
        bottom.add(sectionLabel("SISTEMA"));
        bottom.add(Box.createVerticalStrut(8));
        JButton logout = navButton("Cerrar sesion", "logout");
        logout.addActionListener(e -> {
            guardarTodo();
            usuarioActual = null;
            cards.show(root, "login");
        });
        bottom.add(logout);

        sidebar.add(top, BorderLayout.NORTH);
        sidebar.add(bottom, BorderLayout.SOUTH);
        return sidebar;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 11f));
        label.setForeground(new Color(148, 163, 184));
        return label;
    }

    private JButton navButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.putClientProperty("cardName", cardName);
        if (!"logout".equals(cardName)) {
            navButtons.put(cardName, button);
            button.addActionListener(e -> selectNavigation(cardName));
        }
        styleNavButton(button, false);
        return button;
    }

    private void selectNavigation(String cardName) {
        if (contentPanel != null && navButtons.containsKey(cardName)) {
            contentCards.show(contentPanel, cardName);
        }
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            styleNavButton(entry.getValue(), entry.getKey().equals(cardName));
        }
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
        stylePrimaryButton(refrescar);
        styleSecondaryButton(mover);
        styleSecondaryButton(desaparecido);
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
        turnosPanel = buildTurnosPanel();
        forms.add(turnosPanel);

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
        JCheckBox hayNinos = new JCheckBox("Ninos <5");
        JCheckBox hayJovenes = new JCheckBox("Jovenes <18");
        JCheckBox bebidasCalientes = new JCheckBox("Bebidas calientes");
        JCheckBox clientesPendientes = new JCheckBox("Hay clientes por atender");
        hayNinos.setOpaque(false);
        hayJovenes.setOpaque(false);
        bebidasCalientes.setOpaque(false);
        clientesPendientes.setOpaque(false);
        JButton reservar = new JButton("Reservar y prestar");
        JButton prestarBasico = new JButton("Prestar sin reserva");
        JButton devolver = new JButton("Devolver prestamo");
        stylePrimaryButton(reservar);
        styleSecondaryButton(prestarBasico);
        styleSecondaryButton(devolver);
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
                    ((Number) personas.getValue()).intValue(), hayNinos.isSelected(), hayJovenes.isSelected(),
                    bebidasCalientes.isSelected());
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
            if (usuarioActual instanceof Empleado && clientesPendientes.isSelected()) {
                showInfo("No puedes pedir prestamo si hay clientes por atender.");
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
        panel.add(hayNinos, gbc);
        gbc.gridx = 1;
        panel.add(hayJovenes, gbc);
        gbc.gridx = 2;
        panel.add(bebidasCalientes, gbc);
        gbc.gridx = 3;
        panel.add(clientesPendientes, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(reservar, gbc);
        gbc.gridx = 1;
        panel.add(prestarBasico, gbc);
        gbc.gridx = 2;
        panel.add(devolver, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
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
        JButton favorito = new JButton("Marcar favorito");
        stylePrimaryButton(comprarJuego);
        stylePrimaryButton(comprarCafe);
        styleSecondaryButton(puntosCafe);
        styleSecondaryButton(puntosJuego);
        styleSecondaryButton(favorito);

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
        favorito.addActionListener(e -> {
            if (!(usuarioActual instanceof Cliente cliente)) {
                showInfo("Solo los clientes pueden marcar juegos favoritos.");
                return;
            }
            JuegoDeMesa juego = (JuegoDeMesa) juegos.getSelectedItem();
            if (juego == null) {
                return;
            }
            if (cliente.getJuegosFavoritos().contains(juego.getId())) {
                showInfo("Ese juego ya estaba marcado como favorito.");
                return;
            }
            cliente.agregarFavorito(juego);
            showInfo("Juego favorito agregado: " + juego.getNombre());
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
        gbc.gridx = 6;
        panel.add(favorito, gbc);

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
        tipoSolicitudLabel = new JLabel("Tipo");
        tipoSolicitudCombo = new JComboBox<>(TipoSolicitudTurno.values());
        solicitarTurnoButton = new JButton("Solicitar cambio");
        aprobarTurnoButton = new JButton("Aprobar seleccion");
        stylePrimaryButton(solicitarTurnoButton);
        styleSecondaryButton(aprobarTurnoButton);
        solicitarTurnoButton.addActionListener(e -> {
            if (!(usuarioActual instanceof Empleado empleado)) {
                showInfo("Solo empleados pueden solicitar cambios de turno.");
                return;
            }
            TipoSolicitudTurno tipo = (TipoSolicitudTurno) tipoSolicitudCombo.getSelectedItem();
            SolicitudCambioTurno solicitud = empleado.solicitarCambioTurno(tipo == null ? TipoSolicitudTurno.CAMBIO : tipo);
            solicitud.setEmpleadoOrigen(empleado);
            sistema.registrarSolicitudCambioTurno(solicitud);
            guardarYRefrescar();
        });
        aprobarTurnoButton.addActionListener(e -> {
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
        buttons.add(tipoSolicitudLabel);
        buttons.add(tipoSolicitudCombo);
        buttons.add(solicitarTurnoButton);
        buttons.add(aprobarTurnoButton);
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
        JPanel summary = new JPanel(new GridBagLayout());
        summary.setOpaque(false);
        reportCountValue = new JLabel("0");
        reportTotalValue = new JLabel("$0");
        reportTaxValue = new JLabel("$0");
        reportScopeValue = new JLabel("-");
        GridBagConstraints gbc = baseGbc();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        summary.add(metricCard("Ventas visibles", reportCountValue), gbc);
        gbc.gridx = 1;
        summary.add(metricCard("Total vendido", reportTotalValue), gbc);
        gbc.gridx = 2;
        summary.add(metricCard("Impuestos", reportTaxValue), gbc);
        gbc.gridx = 3;
        summary.add(metricCard("Alcance", reportScopeValue), gbc);

        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        tablePanel.setBackground(PANEL);
        tablePanel.setBorder(panelBorder("Detalle de reportes"));
        tablePanel.add(new JScrollPane(ventas), BorderLayout.CENTER);

        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filters.setOpaque(false);
        reportRubroFilter = new JComboBox<>(new String[] { "Todos", "Juego", "Cafe" });
        reportRubroFilter.addActionListener(e -> refreshVentas());
        filters.add(new JLabel("Rubro"));
        filters.add(reportRubroFilter);
        JPanel totals = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totals.setOpaque(false);
        JButton refresh = new JButton("Actualizar reportes");
        stylePrimaryButton(refresh);
        refresh.addActionListener(e -> refreshAll());
        totals.add(refresh);
        actions.add(filters, BorderLayout.WEST);
        actions.add(totals, BorderLayout.EAST);
        panel.add(summary, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildLoansPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);
        prestamosReporteModel = new DefaultTableModel(new String[] {
                "ID", "Juego", "Usuario", "Fecha prestamo", "Fecha devolucion", "Estado", "Advertencia"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable prestamos = styledTable(prestamosReporteModel);
        JPanel summary = new JPanel(new GridBagLayout());
        summary.setOpaque(false);
        loanCountValue = new JLabel("0");
        loanActiveValue = new JLabel("0");
        loanReturnedValue = new JLabel("0");
        loanScopeValue = new JLabel("-");
        GridBagConstraints gbc = baseGbc();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        summary.add(metricCard("Prestamos visibles", loanCountValue), gbc);
        gbc.gridx = 1;
        summary.add(metricCard("Activos", loanActiveValue), gbc);
        gbc.gridx = 2;
        summary.add(metricCard("Devueltos", loanReturnedValue), gbc);
        gbc.gridx = 3;
        summary.add(metricCard("Alcance", loanScopeValue), gbc);

        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        tablePanel.setBackground(PANEL);
        tablePanel.setBorder(panelBorder("Detalle de prestamos"));
        tablePanel.add(new JScrollPane(prestamos), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton refresh = new JButton("Actualizar prestamos");
        stylePrimaryButton(refresh);
        refresh.addActionListener(e -> refreshAll());
        actions.add(refresh);
        panel.add(summary, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildChartsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);

        JPanel selector = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selector.setOpaque(false);
        chartGameCombo = new JComboBox<>();
        JButton refresh = new JButton("Actualizar graficas");
        stylePrimaryButton(refresh);
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
        torneosModel = new DefaultTableModel(new String[] {
                "ID", "Nombre", "Juego", "Tipo", "Dia", "Estado", "Participantes", "Disponibles", "Entrada"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable torneos = styledTable(torneosModel);
        torneos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalleTorneoSeleccionado(torneos);
            }
        });

        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        tablePanel.setBackground(PANEL);
        tablePanel.setBorder(panelBorder("Torneos"));
        tablePanel.add(new JScrollPane(torneos), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        JButton crear = new JButton("Crear torneo");
        JButton inscribir = new JButton("Inscribirme");
        JButton retirar = new JButton("Retirarme");
        JButton finalizar = new JButton("Finalizar");
        JButton vouchers = new JButton("Mis vouchers");
        JButton actualizar = new JButton("Actualizar");
        stylePrimaryButton(crear);
        stylePrimaryButton(inscribir);
        styleSecondaryButton(retirar);
        styleSecondaryButton(finalizar);
        styleSecondaryButton(vouchers);
        styleSecondaryButton(actualizar);
        crear.addActionListener(e -> crearTorneoDesdeGui());
        inscribir.addActionListener(e -> inscribirATorneoSeleccionado(torneos));
        retirar.addActionListener(e -> retirarDeTorneoSeleccionado(torneos));
        finalizar.addActionListener(e -> finalizarTorneoSeleccionado(torneos));
        vouchers.addActionListener(e -> mostrarVouchersUsuario());
        actualizar.addActionListener(e -> refreshAll());
        actions.add(crear);
        actions.add(inscribir);
        actions.add(retirar);
        actions.add(finalizar);
        actions.add(vouchers);
        actions.add(actualizar);
        tablePanel.add(actions, BorderLayout.SOUTH);

        torneosDetalleArea = new JTextArea(10, 32);
        torneosDetalleArea.setEditable(false);
        JPanel detail = new JPanel(new BorderLayout(8, 8));
        detail.setBackground(PANEL);
        detail.setBorder(panelBorder("Detalle"));
        detail.add(new JScrollPane(torneosDetalleArea), BorderLayout.CENTER);

        panel.add(tablePanel, BorderLayout.CENTER);
        panel.add(detail, BorderLayout.EAST);
        return panel;
    }

    private void crearTorneoDesdeGui() {
        if (!(usuarioActual instanceof Administrador admin)) {
            showInfo("Solo administradores pueden crear torneos.");
            return;
        }
        JTextField nombre = new JTextField(18);
        JComboBox<JuegoDeMesa> juego = new JComboBox<>();
        refillCombo(juego, sistema.getJuegosCatalogo());
        JComboBox<TipoTorneo> tipo = new JComboBox<>(TipoTorneo.values());
        JSpinner participantes = new JSpinner(new SpinnerNumberModel(4, 2, 100, 1));
        JSpinner dia = new JSpinner(new SpinnerNumberModel(1, 1, 7, 1));
        JSpinner entrada = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1_000_000.0, 1000.0));
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = baseGbc();
        form.add(new JLabel("Nombre"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(nombre, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Juego"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(juego, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Tipo"), gbc);
        gbc.gridx = 1;
        form.add(tipo, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Participantes"), gbc);
        gbc.gridx = 1;
        form.add(participantes, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Dia 1-7"), gbc);
        gbc.gridx = 1;
        form.add(dia, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Entrada"), gbc);
        gbc.gridx = 1;
        form.add(entrada, gbc);
        int result = JOptionPane.showConfirmDialog(this, form, "Crear torneo", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }
        JuegoDeMesa juegoSeleccionado = (JuegoDeMesa) juego.getSelectedItem();
        if (juegoSeleccionado == null || nombre.getText().trim().isBlank()) {
            showInfo("Ingrese nombre y juego para crear el torneo.");
            return;
        }
        ResultadoValidacion respuesta = servicioTorneos.crearTorneo(nombre.getText().trim(), juegoSeleccionado,
                (TipoTorneo) tipo.getSelectedItem(), ((Number) participantes.getValue()).intValue(),
                ((Number) dia.getValue()).intValue(), admin, ((Number) entrada.getValue()).doubleValue());
        showInfo(respuesta.getMensaje());
        guardarYRefrescar();
    }

    private void inscribirATorneoSeleccionado(JTable table) {
        if (usuarioActual == null || usuarioActual instanceof Administrador) {
            showInfo("Inicie sesion como cliente o empleado para inscribirse.");
            return;
        }
        Torneo torneo = torneoSeleccionado(table);
        if (torneo == null) {
            showInfo("Seleccione un torneo.");
            return;
        }
        int cupos = 1;
        if (usuarioActual instanceof Cliente) {
            String input = JOptionPane.showInputDialog(this, "Cupos a reservar (1-3):", "1");
            if (input == null) {
                return;
            }
            try {
                cupos = Integer.parseInt(input.trim());
            } catch (NumberFormatException ex) {
                showInfo("Cantidad invalida.");
                return;
            }
            if (cupos < 1 || cupos > 3) {
                showInfo("La cantidad debe estar entre 1 y 3.");
                return;
            }
        }
        int registrados = 0;
        ResultadoValidacion ultimo = null;
        for (int i = 0; i < cupos; i++) {
            ultimo = servicioTorneos.registrarUsuarioATorneo(torneo.getId(), usuarioActual,
                    servicioTorneos.obtenerTodosTorneos());
            if (!ultimo.esValido()) {
                break;
            }
            registrados++;
        }
        showInfo(registrados == cupos ? "Cupos reservados: " + registrados
                : (registrados > 0 ? "Cupos reservados: " + registrados + ". Faltantes: " + ultimo.getMensaje()
                        : ultimo == null ? "No fue posible registrar." : ultimo.getMensaje()));
        guardarYRefrescar();
    }

    private void retirarDeTorneoSeleccionado(JTable table) {
        if (usuarioActual == null || usuarioActual instanceof Administrador) {
            showInfo("Inicie sesion como cliente o empleado para retirarse.");
            return;
        }
        Torneo torneo = torneoSeleccionado(table);
        if (torneo == null) {
            showInfo("Seleccione un torneo.");
            return;
        }
        ResultadoValidacion respuesta = servicioTorneos.retirarUsuarioDeTorneo(torneo.getId(), usuarioActual);
        showInfo(respuesta.getMensaje());
        guardarYRefrescar();
    }

    private void finalizarTorneoSeleccionado(JTable table) {
        if (!(usuarioActual instanceof Administrador)) {
            showInfo("Solo administradores pueden finalizar torneos.");
            return;
        }
        Torneo torneo = torneoSeleccionado(table);
        if (torneo == null) {
            showInfo("Seleccione un torneo.");
            return;
        }
        String ganador = JOptionPane.showInputDialog(this, "ID numerico del ganador:");
        if (ganador == null || ganador.isBlank()) {
            return;
        }
        ResultadoValidacion respuesta = servicioTorneos.finalizarTorneo(torneo.getId(), ganador.trim());
        showInfo(respuesta.getMensaje());
        guardarYRefrescar();
    }

    private void mostrarVouchersUsuario() {
        if (usuarioActual == null) {
            showInfo("Inicie sesion para ver vouchers.");
            return;
        }
        List<VoucherDescuento> vouchers = usuarioActual instanceof Administrador
                ? servicioTorneos.obtenerTodosVouchers()
                : servicioTorneos.obtenerVouchersDelUsuario(usuarioActual.getId());
        if (vouchers.isEmpty()) {
            showInfo("No hay vouchers disponibles.");
            return;
        }
        StringBuilder sb = new StringBuilder("Vouchers\n");
        for (VoucherDescuento voucher : vouchers) {
            sb.append("#").append(voucher.getId()).append(" | ").append(voucher.getNombreTorneo())
                    .append(" | ").append(money(voucher.getMontoDescuento()))
                    .append(" | ").append(voucher.esValido() ? "valido" : "no valido").append('\n');
        }
        showInfo(sb.toString());
    }

    private void mostrarDetalleTorneoSeleccionado(JTable table) {
        if (torneosDetalleArea == null) {
            return;
        }
        Torneo torneo = torneoSeleccionado(table);
        if (torneo == null) {
            torneosDetalleArea.setText("");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(torneo.getNombre()).append('\n');
        sb.append("Juego: ").append(torneo.getNombreJuego()).append('\n');
        sb.append("Dia: ").append(nombreDia(torneo.getDiaSemana())).append('\n');
        sb.append("Estado: ").append(torneo.getEstado()).append('\n');
        sb.append("Spots fans: ").append(torneo.getSpotReservadosFans()).append('\n');
        sb.append("Premio total: ").append(money(torneo.getPremioTotal())).append("\n\n");
        sb.append("Participantes\n");
        if (torneo.getParticipantes().isEmpty()) {
            sb.append("- Sin participantes\n");
        }
        for (ParticipanteTorneo participante : torneo.getParticipantes()) {
            sb.append("- ID ").append(participante.getIdUsuario()).append(" | ")
                    .append(participante.getNombreUsuario());
            if (participante.esFan()) {
                sb.append(" | fan");
            }
            if (participante.gano()) {
                sb.append(" | ganador ").append(money(participante.getPremioODescuento()));
            }
            sb.append('\n');
        }
        torneosDetalleArea.setText(sb.toString());
    }

    private Torneo torneoSeleccionado(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        int id = (Integer) torneosModel.getValueAt(table.convertRowIndexToModel(row), 0);
        return servicioTorneos.obtenerTorneoPorId(id);
    }

    private JPanel wrapChart(JPanel chart) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        panel.add(chart, BorderLayout.CENTER);
        return panel;
    }

    private void refreshAll() {
        refreshCombos(root);
        refreshGames();
        refreshPrestamos();
        refreshPrestamosReporte();
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
        List<Prestamo> prestamos = prestamosVisiblesParaUsuario();
        prestamos = prestamos.stream()
                .sorted(Comparator.comparing(Prestamo::getFechaPrestamo).reversed())
                .toList();
        for (Prestamo prestamo : prestamos) {
            String usuario = prestamo.getUsuario() == null ? "" : prestamo.getUsuario().getLogin();
            prestamosModel.addRow(new Object[] {
                    prestamo.getPrestamoId(), prestamo.getCopia().getJuego().getNombre(), usuario,
                    prestamo.getFechaPrestamo().toLocalDate().format(DATE_FORMAT), prestamo.estaActivo() ? "Si" : "No"
            });
        }
    }

    private void refreshPrestamosReporte() {
        if (prestamosReporteModel == null) {
            return;
        }
        prestamosReporteModel.setRowCount(0);
        List<Prestamo> prestamos = prestamosVisiblesParaUsuario();
        prestamos = prestamos.stream()
                .sorted(Comparator.comparing(Prestamo::getFechaPrestamo).reversed())
                .toList();
        int activos = 0;
        int devueltos = 0;
        for (Prestamo prestamo : prestamos) {
            if (prestamo.estaActivo()) {
                activos++;
            } else {
                devueltos++;
            }
            String usuario = prestamo.getUsuario() == null ? "" : prestamo.getUsuario().getLogin();
            String fechaDevolucion = prestamo.getFechaDevolucion() == null
                    ? "-"
                    : prestamo.getFechaDevolucion().toLocalDate().format(DATE_FORMAT);
            prestamosReporteModel.addRow(new Object[] {
                    prestamo.getPrestamoId(), prestamo.getCopia().getJuego().getNombre(), usuario,
                    prestamo.getFechaPrestamo().toLocalDate().format(DATE_FORMAT), fechaDevolucion,
                    prestamo.estaActivo() ? "Activo" : "Devuelto",
                    prestamo.tieneAdvertencia() ? "Si" : "No"
            });
        }
        refreshLoanSummary(prestamos.size(), activos, devueltos);
    }

    private void refreshVentas() {
        if (ventasModel == null) {
            return;
        }
        ventasModel.setRowCount(0);
        List<Venta> ventas = ventasVisiblesParaUsuario().stream()
                .filter(this::coincideConFiltroRubro)
                .sorted(Comparator.comparing(Venta::getFecha).reversed())
                .toList();
        double total = 0.0;
        double impuestos = 0.0;
        for (Venta venta : ventas) {
            total += venta.getTotal();
            impuestos += venta.getImpuesto();
            ventasModel.addRow(new Object[] {
                    venta.getVentaId(), venta.getFecha().toLocalDate().format(DATE_FORMAT), venta.getRubro(),
                    money(venta.getSubtotal()), money(venta.getImpuesto()), money(venta.getTotal()),
                    venta.getUsuario() == null ? "" : venta.getUsuario().getLogin()
            });
        }
        refreshReportSummary(ventas.size(), total, impuestos);
    }

    private boolean coincideConFiltroRubro(Venta venta) {
        if (reportRubroFilter == null || reportRubroFilter.getSelectedItem() == null) {
            return true;
        }
        String filtro = reportRubroFilter.getSelectedItem().toString();
        if ("Juego".equals(filtro)) {
            return venta.getRubro() == RubroVenta.JUEGO;
        }
        if ("Cafe".equals(filtro)) {
            return venta.getRubro() == RubroVenta.CAFE;
        }
        return true;
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
                    torneo.getId(), torneo.getNombre(), torneo.getNombreJuego(), torneo.getTipo(),
                    nombreDia(torneo.getDiaSemana()), torneo.getEstado(),
                    torneo.getCantidadParticipantes() + "/" + torneo.getCantidadMaximaParticipantes(),
                    torneo.getSpotsDisponibles(), money(torneo.getMontoEntrada())
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
        if (!(usuarioActual instanceof Administrador)) {
            pieChart.setValues(new LinkedHashMap<>());
            barChart.setData(List.of(), List.of(), List.of());
            lineChart.setData(List.of(), List.of());
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

    private void configureRoleTabs() {
        if (contentPanel == null) {
            return;
        }
        boolean admin = usuarioActual instanceof Administrador;
        JButton charts = navButtons.get(CHARTS_CARD);
        if (charts != null) {
            charts.setVisible(admin);
        }
        boolean empleado = usuarioActual instanceof Empleado;
        if (turnosPanel != null) {
            turnosPanel.setVisible(empleado || admin);
        }
        if (tipoSolicitudLabel != null) {
            tipoSolicitudLabel.setVisible(empleado);
        }
        if (tipoSolicitudCombo != null) {
            tipoSolicitudCombo.setVisible(empleado);
        }
        if (solicitarTurnoButton != null) {
            solicitarTurnoButton.setVisible(empleado);
        }
        if (aprobarTurnoButton != null) {
            aprobarTurnoButton.setVisible(admin);
        }
        String selected = currentNavigation();
        if (!admin && CHARTS_CARD.equals(selected)) {
            selectNavigation(CATALOG_CARD);
        } else if (selected == null) {
            selectNavigation(CATALOG_CARD);
        }
    }

    private List<Venta> ventasVisiblesParaUsuario() {
        if (usuarioActual == null) {
            return List.of();
        }
        if (usuarioActual instanceof Administrador) {
            return sistema.getVentas();
        }
        return sistema.getVentas().stream()
                .filter(venta -> perteneceAUsuarioActual(venta.getUsuario()))
                .toList();
    }

    private List<Prestamo> prestamosVisiblesParaUsuario() {
        if (usuarioActual == null) {
            return List.of();
        }
        if (usuarioActual instanceof Administrador) {
            return sistema.getHistorialPrestamos();
        }
        return sistema.getHistorialPrestamos().stream()
                .filter(prestamo -> perteneceAUsuarioActual(prestamo.getUsuario()))
                .toList();
    }

    private boolean perteneceAUsuarioActual(Usuario usuarioVenta) {
        if (usuarioVenta == null || usuarioActual == null) {
            return false;
        }
        return usuarioVenta == usuarioActual
                || usuarioVenta.getLogin().equals(usuarioActual.getLogin())
                || usuarioVenta.getId().equals(usuarioActual.getId());
    }

    private String currentNavigation() {
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            if (entry.getValue().getBackground().equals(ACCENT_SOFT)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private JPanel metricCard(String label, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(6, 6));
        card.setBackground(PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));
        JLabel title = new JLabel(label);
        title.setForeground(MUTED);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 12f));
        valueLabel.setForeground(INK);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 22f));
        card.add(title, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void refreshReportSummary(int count, double total, double impuestos) {
        if (reportCountValue == null) {
            return;
        }
        reportCountValue.setText(String.valueOf(count));
        reportTotalValue.setText(money(total));
        reportTaxValue.setText(money(impuestos));
        reportScopeValue.setText(usuarioActual instanceof Administrador ? "Todos" : usuarioActual.getLogin());
    }

    private void refreshLoanSummary(int count, int activos, int devueltos) {
        if (loanCountValue == null) {
            return;
        }
        loanCountValue.setText(String.valueOf(count));
        loanActiveValue.setText(String.valueOf(activos));
        loanReturnedValue.setText(String.valueOf(devueltos));
        loanScopeValue.setText(usuarioActual instanceof Administrador ? "Todos" : usuarioActual.getLogin());
    }

    private void styleNavButton(JButton button, boolean selected) {
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
        button.setForeground(selected ? ACCENT : MUTED);
        button.setBackground(selected ? ACCENT_SOFT : SIDEBAR);
        button.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
    }

    private void stylePrimaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    private void styleSecondaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setForeground(INK);
        button.setBackground(PANEL);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)));
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setGridColor(new Color(241, 245, 249));
        table.setSelectionBackground(ACCENT_SOFT);
        table.setSelectionForeground(INK);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        table.getTableHeader().setForeground(MUTED);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));
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
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDER), title),
                BorderFactory.createEmptyBorder(12, 12, 12, 12));
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

    private String nombreDia(int dia) {
        return switch (dia) {
            case 1 -> "Lunes";
            case 2 -> "Martes";
            case 3 -> "Miercoles";
            case 4 -> "Jueves";
            case 5 -> "Viernes";
            case 6 -> "Sabado";
            case 7 -> "Domingo";
            default -> "Dia " + dia;
        };
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    @FunctionalInterface
    private interface GameAction {
        void run(JuegoDeMesa juego);
    }
}
