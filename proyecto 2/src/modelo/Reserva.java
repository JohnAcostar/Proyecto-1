package modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Reserva {
    private LocalDateTime fecha;
    private int cantidadPersonas;
    private boolean hayNinos;
    private boolean hayJovenes;
    private EstadoReserva estado;
    private boolean bebidasCalientesActivas;
    private List<JuegoDeMesa> juegosPrestados;
    private Mesa mesa;

    public Reserva(int cantidadPersonas, boolean hayNinos, boolean hayJovenes) {
        this.fecha = LocalDateTime.now();
        this.cantidadPersonas = cantidadPersonas;
        this.hayNinos = hayNinos;
        this.hayJovenes = hayJovenes;
        this.estado = EstadoReserva.PENDIENTE;
        this.juegosPrestados = new ArrayList<>();
    }

    public void confirmar() {
        this.estado = EstadoReserva.ACEPTADA;
    }

    public void rechazar() {
        this.estado = EstadoReserva.RECHAZADA;
    }

    public void finalizar() {
        this.estado = EstadoReserva.FINALIZADA;
    }

    public boolean validarElegibilidadDelJuego(JuegoDeMesa juego) {
        if (!juego.esAptoParaCantidadJugadores(cantidadPersonas)) {
            return false;
        }
        if (!juego.esAptoParaEdad(hayNinos, hayJovenes)) {
            return false;
        }
        if (bebidasCalientesActivas && juego.getCategoria() == CategoriaJuego.ACCION) {
            return false;
        }
        return juegosPrestados.size() < 2;
    }

    public void anadirPrestamoDeJuego(JuegoDeMesa juego) {
        if (validarElegibilidadDelJuego(juego)) {
            juegosPrestados.add(juego);
        }
    }

    public void activarBebidasCalientes() {
        bebidasCalientesActivas = true;
    }

    public boolean hayMenores() {
        return hayNinos || hayJovenes;
    }

    // Nuevos métodos para integración con Mesa
    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
