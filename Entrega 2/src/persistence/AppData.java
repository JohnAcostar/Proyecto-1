package persistence;

import java.util.ArrayList;
import java.util.List;

import modelo.CopiaJuego;
import modelo.JuegoDeMesa;
import modelo.Prestamo;
import modelo.SolicitudCambioTurno;
import modelo.Sugerencia;
import modelo.Torneo;
import modelo.Usuario;
import modelo.Venta;
import modelo.VoucherDescuento;

public class AppData {
    private List<Usuario> usuarios;
    private List<JuegoDeMesa> juegos;
    private List<Venta> ventas;
    private List<CopiaJuego> copiasPrestamo;
    private List<CopiaJuego> copiasVenta;
    private List<Prestamo> historialPrestamos;
    private List<SolicitudCambioTurno> solicitudesTurno;
    private List<Sugerencia> sugerenciasMenu;
    private List<Torneo> torneos;
    private List<VoucherDescuento> vouchersDescuento;

    public AppData() {
        this.usuarios = new ArrayList<>();
        this.juegos = new ArrayList<>();
        this.ventas = new ArrayList<>();
        this.copiasPrestamo = new ArrayList<>();
        this.copiasVenta = new ArrayList<>();
        this.historialPrestamos = new ArrayList<>();
        this.solicitudesTurno = new ArrayList<>();
        this.sugerenciasMenu = new ArrayList<>();
        this.torneos = new ArrayList<>();
        this.vouchersDescuento = new ArrayList<>();
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public List<JuegoDeMesa> getJuegos() {
        return juegos;
    }

    public void setJuegos(List<JuegoDeMesa> juegos) {
        this.juegos = juegos;
    }

    public List<Venta> getVentas() {
        return ventas;
    }

    public void setVentas(List<Venta> ventas) {
        this.ventas = ventas;
    }

    public List<CopiaJuego> getCopiasPrestamo() {
        return copiasPrestamo;
    }

    public void setCopiasPrestamo(List<CopiaJuego> copiasPrestamo) {
        this.copiasPrestamo = copiasPrestamo;
    }

    public List<CopiaJuego> getCopiasVenta() {
        return copiasVenta;
    }

    public void setCopiasVenta(List<CopiaJuego> copiasVenta) {
        this.copiasVenta = copiasVenta;
    }

    public List<Prestamo> getHistorialPrestamos() {
        return historialPrestamos;
    }

    public void setHistorialPrestamos(List<Prestamo> historialPrestamos) {
        this.historialPrestamos = historialPrestamos;
    }

    public List<SolicitudCambioTurno> getSolicitudesTurno() {
        return solicitudesTurno;
    }

    public void setSolicitudesTurno(List<SolicitudCambioTurno> solicitudesTurno) {
        this.solicitudesTurno = solicitudesTurno;
    }

    public List<Sugerencia> getSugerenciasMenu() {
        return sugerenciasMenu;
    }

    public void setSugerenciasMenu(List<Sugerencia> sugerenciasMenu) {
        this.sugerenciasMenu = sugerenciasMenu;
    }

    public List<Torneo> getTorneos() {
        return torneos;
    }

    public void setTorneos(List<Torneo> torneos) {
        this.torneos = torneos;
    }

    public List<VoucherDescuento> getVouchersDescuento() {
        return vouchersDescuento;
    }

    public void setVouchersDescuento(List<VoucherDescuento> vouchersDescuento) {
        this.vouchersDescuento = vouchersDescuento;
    }
}
