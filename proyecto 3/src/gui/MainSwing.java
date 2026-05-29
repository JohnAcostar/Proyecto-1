package gui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import persistence.AppData;
import persistence.FilePersistence;
import service.SistemaCafe;
import service.ServicioTorneos;

public class MainSwing {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // The default Swing look and feel is acceptable if the system one is unavailable.
            }

            FilePersistence persistence = new FilePersistence();
            SistemaCafe sistema = new SistemaCafe();
            ServicioTorneos servicioTorneos = new ServicioTorneos();

            AppData data = persistence.load();
            sistema.cargarDatos(data.getUsuarios(), data.getJuegos(), data.getVentas());
            sistema.cargarEstadoOperativo(data.getCopiasPrestamo(), data.getCopiasVenta(),
                    data.getHistorialPrestamos(), data.getSolicitudesTurno(), data.getSugerenciasMenu());
            servicioTorneos.setTorneos(data.getTorneos());
            servicioTorneos.setVouchers(data.getVouchersDescuento());
            sistema.reconstruirHistorialesUsuarios();
            sistema.inicializarDatosBaseSiVacio();

            Proyecto3Frame frame = new Proyecto3Frame(sistema, servicioTorneos, persistence);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
