package modelo;

public abstract class Empleado extends Usuario {
    private String codigoDescuento;
    private boolean enTurno;
    private String nombre;

    protected Empleado(String login, String password, String id, String nombre, String codigoDescuento) {
        super(login, password, id);
        this.nombre = nombre;
        this.codigoDescuento = codigoDescuento;
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

    public void devolverJuego(Prestamo prestamo) {
        if (prestamo != null && prestamo.estaActivo()) {
            prestamo.registrarDevolucion();
        }
    }
}
