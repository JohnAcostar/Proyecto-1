package service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import modelo.Empleado;
import modelo.ReporteVentas;
import modelo.RubroVenta;
import modelo.Venta;

/**
 * Servicio centralizado para generación de reportes.
 */
public class ServicioReportes {
    private List<Venta> ventas;
    private List<ReporteVentas> reportes;

    public ServicioReportes() {
        this.ventas = new ArrayList<>();
        this.reportes = new ArrayList<>();
    }

    /**
     * Carga las ventas para reportería.
     */
    public void cargarVentas(List<Venta> ventasDelSistema) {
        this.ventas = new ArrayList<>(ventasDelSistema);
    }

    /**
     * Genera reporte por rubro en un rango de fechas.
     */
    public ReporteVentas generarReportePorRubro(RubroVenta rubro, LocalDate desde, LocalDate hasta) {
        List<Venta> ventasFiltered = ventas.stream()
                .filter(v -> v.getRubro() == rubro && !v.getFecha().toLocalDate().isBefore(desde)
                        && !v.getFecha().toLocalDate().isAfter(hasta))
                .collect(Collectors.toList());

        ReporteVentas reporte = new ReporteVentas(String.format("Reporte_%s_%s_a_%s", rubro, desde, hasta));
        for (Venta v : ventasFiltered) {
            reporte.agregarVenta(v);
        }

        reportes.add(reporte);
        return reporte;
    }

    /**
     * Genera reporte por empleado.
     */
    public ReporteVentas generarReportePorEmpleado(Empleado empleado, LocalDate desde, LocalDate hasta) {
        List<Venta> ventasFiltered = ventas.stream()
                .filter(v -> v.getUsuario() != null && v.getUsuario().equals(empleado)
                        && !v.getFecha().toLocalDate().isBefore(desde)
                        && !v.getFecha().toLocalDate().isAfter(hasta))
                .collect(Collectors.toList());

        ReporteVentas reporte = new ReporteVentas(String.format("Reporte_empleado_%s_%s", empleado.getId(), desde));
        for (Venta v : ventasFiltered) {
            reporte.agregarVenta(v);
        }

        reportes.add(reporte);
        return reporte;
    }

    /**
     * Obtiene ingresos totales en un rango.
     */
    public double obtenerIngresosTotal(LocalDate desde, LocalDate hasta) {
        return ventas.stream()
                .filter(v -> !v.getFecha().toLocalDate().isBefore(desde)
                        && !v.getFecha().toLocalDate().isAfter(hasta))
                .mapToDouble(Venta::getTotal).sum();
    }

    // Getters
    public List<ReporteVentas> getReportesGenerados() {
        return new ArrayList<>(reportes);
    }
}
