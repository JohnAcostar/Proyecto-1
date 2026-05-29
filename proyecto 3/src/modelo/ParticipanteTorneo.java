package modelo;

import java.io.Serializable;

/**
 * Representa un participante registrado en un torneo especifico.
 */
public class ParticipanteTorneo implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idUsuario;
    private String nombreUsuario;
    private boolean esFan;
    private boolean pagoPorEntrada;
    private double montoInscripcion;
    private boolean gano;
    private double premioODescuento;
    private boolean usoSpotReservadoFan;

    public ParticipanteTorneo(int idUsuario, String nombreUsuario, boolean esFan) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.esFan = esFan;
        this.pagoPorEntrada = false;
        this.montoInscripcion = 0;
        this.gano = false;
        this.premioODescuento = 0;
        this.usoSpotReservadoFan = false;
    }

    public ParticipanteTorneo(String idUsuario, String nombreUsuario, boolean esFan) {
        this(parsearId(idUsuario), nombreUsuario, esFan);
    }

    private static int parsearId(String idTexto) {
        String digitos = idTexto == null ? "" : idTexto.replaceAll("\\D", "");
        return digitos.isEmpty() ? 0 : Integer.parseInt(digitos);
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public boolean esFan() {
        return esFan;
    }

    public void setEsFan(boolean esFan) {
        this.esFan = esFan;
    }

    public boolean usoSpotReservadoFan() {
        return usoSpotReservadoFan;
    }

    public void setUsoSpotReservadoFan(boolean usoSpotReservadoFan) {
        this.usoSpotReservadoFan = usoSpotReservadoFan;
    }

    public boolean pagoPorEntrada() {
        return pagoPorEntrada;
    }

    public void setPagoPorEntrada(boolean pagoPorEntrada) {
        this.pagoPorEntrada = pagoPorEntrada;
    }

    public double getMontoInscripcion() {
        return montoInscripcion;
    }

    public void setMontoInscripcion(double montoInscripcion) {
        this.montoInscripcion = montoInscripcion;
    }

    public boolean gano() {
        return gano;
    }

    public void setGano(boolean gano) {
        this.gano = gano;
    }

    public double getPremioODescuento() {
        return premioODescuento;
    }

    public void setPremioODescuento(double premioODescuento) {
        this.premioODescuento = premioODescuento;
    }

    @Override
    public String toString() {
        return "ParticipanteTorneo{" +
                "idUsuario=" + idUsuario +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", esFan=" + esFan +
                ", pagoPorEntrada=" + pagoPorEntrada +
                ", montoInscripcion=" + montoInscripcion +
                ", gano=" + gano +
                ", premioODescuento=" + premioODescuento +
                '}';
    }
}
