package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa una mesa en el café.
 * Cada mesa tiene capacidad y información demográfica.
 * Puede estar asociada a una reserva y tener préstamos activos.
 */
public class Mesa {
    private String mesaId;
    private int capacidad;
    private boolean hayNinos;
    private boolean hayJovenes;
    private boolean ocupada;
    private Reserva reservaAsociada;
    private List<Prestamo> juegosPrestados;

    public Mesa(String mesaId, int capacidad) {
        this.mesaId = mesaId;
        this.capacidad = capacidad;
        this.hayNinos = false;
        this.hayJovenes = false;
        this.ocupada = false;
        this.juegosPrestados = new ArrayList<>();
    }

    public Mesa(String mesaId, int capacidad, boolean hayNinos, boolean hayJovenes) {
        this(mesaId, capacidad);
        this.hayNinos = hayNinos;
        this.hayJovenes = hayJovenes;
    }

    public void ocupar() {
        this.ocupada = true;
    }

    public void liberar() {
        this.ocupada = false;
        this.reservaAsociada = null;
        this.juegosPrestados.clear();
    }

    public void asociarReserva(Reserva reserva) {
        if (reserva != null) {
            this.reservaAsociada = reserva;
            this.ocupada = true;
        }
    }

    public boolean esCompatible(Reserva reserva) {
        if (capacidad < reserva.getCantidadPersonas()) {
            return false;
        }
        if (reserva.hayMenores() && !hayNinos && !hayJovenes) {
            return false;
        }
        return true;
    }

    public void agregarPrestamoDeJuego(Prestamo prestamo) {
        if (prestamo != null && juegosPrestados.size() < 2) {
            juegosPrestados.add(prestamo);
        }
    }

    public void removerPrestamoDeJuego(Prestamo prestamo) {
        juegosPrestados.remove(prestamo);
    }

    public int cantidadJuegosPrestados() {
        return juegosPrestados.size();
    }

    // Getters
    public String getMesaId() {
        return mesaId;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public boolean hayNinos() {
        return hayNinos;
    }

    public boolean hayJovenes() {
        return hayJovenes;
    }

    public boolean estaOcupada() {
        return ocupada;
    }

    public Reserva getReservaAsociada() {
        return reservaAsociada;
    }

    public List<Prestamo> getJuegosPrestados() {
        return new ArrayList<>(juegosPrestados);
    }

    // Setters
    public void setHayNinos(boolean hayNinos) {
        this.hayNinos = hayNinos;
    }

    public void setHayJovenes(boolean hayJovenes) {
        this.hayJovenes = hayJovenes;
    }
}
