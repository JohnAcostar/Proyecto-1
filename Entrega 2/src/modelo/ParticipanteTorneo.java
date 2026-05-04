package modelo;

import java.io.Serializable;

/**
 * Representa un participante registrado en un torneo específico.
 * Cada participante es un usuario que se ha registrado para un torneo.
 */
public class ParticipanteTorneo implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idUsuario;
    private String nombreUsuario;
    private boolean esFan; // ¿Es fan del juego del torneo?
    private boolean pagoPorEntrada; // En torneos competitivos, ¿pagó la entrada?
    private double montoInscripcion; // Cantidad pagada (0 para amistosos o empleados)
    private boolean gano; // ¿Ganó el torneo?
    private double premioODescuento; // Premio en efectivo o descuento según tipo

    /**
     * Constructor para crear un participante de torneo.
     *
     * @param idUsuario ID del usuario
     * @param nombreUsuario Nombre del usuario
     * @param esFan ¿Es fan del juego del torneo?
     */
    public ParticipanteTorneo(int idUsuario, String nombreUsuario, boolean esFan) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.esFan = esFan;
        this.pagoPorEntrada = false;
        this.montoInscripcion = 0;
        this.gano = false;
        this.premioODescuento = 0;
    }

    // Getters y Setters

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
