package modelo;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Representa un voucher de descuento otorgado como premio en torneos amistosos.
 * El descuento puede utilizarse en cualquier compra pero no se puede combinar con otros descuentos.
 */
public class VoucherDescuento implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int idUsuario;
    private int idTorneo;
    private String nombreTorneo;
    private double montoDescuento;
    private boolean usado;
    private LocalDate fechaOtorgamiento;
    private LocalDate fechaUso;
    private LocalDate fechaVencimiento; // Vencimiento opcional

    /**
     * Constructor para crear un nuevo voucher de descuento.
     *
     * @param id ID único del voucher
     * @param idUsuario ID del usuario propietario
     * @param idTorneo ID del torneo del cual proviene
     * @param nombreTorneo Nombre del torneo
     * @param montoDescuento Cantidad del descuento en pesos
     * @param diasValidez Días de validez desde la fecha de otorgamiento (0 si no vence)
     */
    public VoucherDescuento(int id, int idUsuario, int idTorneo, String nombreTorneo,
                           double montoDescuento, int diasValidez) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idTorneo = idTorneo;
        this.nombreTorneo = nombreTorneo;
        this.montoDescuento = montoDescuento;
        this.usado = false;
        this.fechaOtorgamiento = LocalDate.now();
        this.fechaUso = null;
        this.fechaVencimiento = diasValidez > 0 ? LocalDate.now().plusDays(diasValidez) : null;
    }

    public VoucherDescuento(int id, String idUsuario, int idTorneo, String nombreTorneo,
                           double montoDescuento, int diasValidez) {
        this(id, parsearId(idUsuario), idTorneo, nombreTorneo, montoDescuento, diasValidez);
    }

    private static int parsearId(String idTexto) {
        String digitos = idTexto == null ? "" : idTexto.replaceAll("\\D", "");
        return digitos.isEmpty() ? 0 : Integer.parseInt(digitos);
    }

    /**
     * Marca el voucher como utilizado.
     */
    public void marcarComoUsado() {
        this.usado = true;
        this.fechaUso = LocalDate.now();
    }

    /**
     * Verifica si el voucher es válido (no usado y no vencido).
     */
    public boolean esValido() {
        if (usado) {
            return false;
        }
        if (fechaVencimiento != null && LocalDate.now().isAfter(fechaVencimiento)) {
            return false;
        }
        return true;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdTorneo() {
        return idTorneo;
    }

    public void setIdTorneo(int idTorneo) {
        this.idTorneo = idTorneo;
    }

    public String getNombreTorneo() {
        return nombreTorneo;
    }

    public void setNombreTorneo(String nombreTorneo) {
        this.nombreTorneo = nombreTorneo;
    }

    public double getMontoDescuento() {
        return montoDescuento;
    }

    public void setMontoDescuento(double montoDescuento) {
        this.montoDescuento = montoDescuento;
    }

    public boolean isUsado() {
        return usado;
    }

    public void setUsado(boolean usado) {
        this.usado = usado;
    }

    public LocalDate getFechaOtorgamiento() {
        return fechaOtorgamiento;
    }

    public void setFechaOtorgamiento(LocalDate fechaOtorgamiento) {
        this.fechaOtorgamiento = fechaOtorgamiento;
    }

    public LocalDate getFechaUso() {
        return fechaUso;
    }

    public void setFechaUso(LocalDate fechaUso) {
        this.fechaUso = fechaUso;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    @Override
    public String toString() {
        return "VoucherDescuento{" +
                "id=" + id +
                ", idUsuario=" + idUsuario +
                ", nombreTorneo='" + nombreTorneo + '\'' +
                ", monto=" + montoDescuento +
                ", usado=" + usado +
                ", vencimiento=" + fechaVencimiento +
                '}';
    }
}
