package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;

public class ReporteVentas {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<Venta> ventas;
    private String nombre;

    public ReporteVentas(LocalDate fechaInicio, LocalDate fechaFin, List<Venta> ventas) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.ventas = ventas;
    }

    // Constructor adicional para compatibilidad
    public ReporteVentas(String nombre) {
        this.nombre = nombre;
        this.fechaInicio = LocalDate.now();
        this.fechaFin = LocalDate.now();
        this.ventas = new ArrayList<>();
    }

    public double totalRubro(RubroVenta rubro) {
        return ventas.stream()
                .filter(v -> v.getRubro() == rubro)
                .mapToDouble(Venta::getTotal)
                .sum();
    }

    public Map<LocalDate, Double> generarDiario() {
        return ventas.stream()
                .filter(v -> {
                    LocalDate fecha = v.getFecha().toLocalDate();
                    return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
                })
                .collect(Collectors.groupingBy(v -> v.getFecha().toLocalDate(),
                        Collectors.summingDouble(Venta::getTotal)));
    }

    public Map<Integer, Double> generarSemanal() {
        return ventas.stream()
                .filter(v -> {
                    LocalDate fecha = v.getFecha().toLocalDate();
                    return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
                })
                .collect(Collectors.groupingBy(v -> v.getFecha().getDayOfYear() / 7,
                        Collectors.summingDouble(Venta::getTotal)));
    }

    public Map<Integer, Double> generarMensual() {
        return ventas.stream()
                .filter(v -> {
                    LocalDate fecha = v.getFecha().toLocalDate();
                    return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
                })
                .collect(Collectors.groupingBy(v -> v.getFecha().getMonthValue(),
                        Collectors.summingDouble(Venta::getTotal)));
    }

    // Método adicional para agregar ventas
    public void agregarVenta(Venta venta) {
        if (venta != null) {
            this.ventas.add(venta);
        }
    }

    public String getNombre() {
        return nombre;
    }

    public List<Venta> getVentas() {
        return ventas;
    }
}
