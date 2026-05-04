package modelo;

import java.util.ArrayList;
import java.util.List;

public abstract class Empleado extends Usuario {
    private String codigoDescuento;
    private boolean enTurno;
    private String nombre;
    private List<Turno> turnos;

    protected Empleado(String login, String password, String id, String nombre, String codigoDescuento) {
        super(login, password, id);
        this.nombre = nombre;
        this.codigoDescuento = codigoDescuento;
        this.turnos = new ArrayList<>();
    }

    public SolicitudCambioTurno solicitarCambioTurno(TipoSolicitudTurno tipo) {
        return new SolicitudCambioTurno(tipo);
    }

    public boolean puedePedirPrestado(boolean hayClientesPorAtender) {
        return !enTurno || !hayClientesPorAtender;
    }

    public String compartirCodigoDescuento() {
        return codigoDescuento;
    }

    public void setEnTurno(boolean enTurno) {
        this.enTurno = enTurno;
    }

    public boolean isEnTurno() {
        return enTurno;
    }

    public String getCodigoDescuento() {
        return codigoDescuento;
    }

    public String getNombre() {
        return nombre;
    }

    public void agregarTurno(Turno turno) {
        if (turno != null) {
            turnos.add(turno);
        }
    }

    public List<Turno> getTurnos() {
        return new ArrayList<>(turnos);
    }

    public void devolverJuego(Prestamo prestamo) {
        if (prestamo != null && prestamo.estaActivo()) {
            prestamo.registrarDevolucion();
        }
    }
}
