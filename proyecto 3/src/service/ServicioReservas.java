package service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import modelo.Cafe;
import modelo.Cliente;
import modelo.Mesa;
import modelo.Reserva;
import modelo.ResultadoValidacion;
import service.validadores.ValidadorReserva;

/**
 * Servicio centralizado para gestión de reservas.
 */
public class ServicioReservas {
    private ValidadorReserva validador;
    private List<Reserva> reservas;
    private Cafe cafe;

    public ServicioReservas(Cafe cafe) {
        this.validador = new ValidadorReserva();
        this.reservas = new ArrayList<>();
        this.cafe = cafe;
    }

    /**
     * Crea una reserva después de validar.
     */
    public ResultadoValidacion crearReserva(int cantPersonas, boolean hayNinos, boolean hayJovenes,
            Cliente cliente) {
        Reserva reserva = new Reserva(cantPersonas, hayNinos, hayJovenes);

        // Validar
        ResultadoValidacion resultado = validador.validar(reserva, cafe);
        if (!resultado.esValido()) {
            return resultado;
        }

        // Encontrar mesa
        Mesa mesa = cafe.encontrarMesaDisponible(cantPersonas);
        if (mesa == null) {
            return ResultadoValidacion.error("No hay mesas disponibles del tamaño requerido",
                    "ERR_MESA_NO_DISPONIBLE");
        }

        // Asociar y confirmar
        reserva.confirmar();
        reserva.setMesa(mesa);
        mesa.asociarReserva(reserva);
        cafe.ocuparMesa(mesa);
        reservas.add(reserva);

        return ResultadoValidacion.exitoso();
    }

    /**
     * Acepta una reserva ya creada.
     */
    public ResultadoValidacion aceptarReserva(Reserva reserva, Mesa mesa) {
        if (reserva == null || mesa == null) {
            return ResultadoValidacion.error("Reserva o mesa nula", "ERR_PARAMS_NULL");
        }

        reserva.confirmar();
        reserva.setMesa(mesa);
        mesa.asociarReserva(reserva);

        return ResultadoValidacion.exitoso();
    }

    /**
     * Rechaza una reserva con motivo.
     */
    public ResultadoValidacion rechazarReserva(Reserva reserva, String motivo) {
        if (reserva == null) {
            return ResultadoValidacion.error("Reserva nula", "ERR_RESERVA_NULL");
        }

        reserva.rechazar();
        reservas.remove(reserva);

        return ResultadoValidacion.exitoso();
    }

    /**
     * Finaliza una reserva (cliente se va).
     */
    public ResultadoValidacion finalizarReserva(Reserva reserva) {
        if (reserva == null) {
            return ResultadoValidacion.error("Reserva nula", "ERR_RESERVA_NULL");
        }

        if (reserva.getMesa() != null) {
            cafe.liberarMesa(reserva.getMesa());
        }

        reserva.finalizar();
        reservas.remove(reserva);

        return ResultadoValidacion.exitoso();
    }

    // Getters
    public List<Reserva> getReservasActivas() {
        return reservas.stream()
                .filter(r -> r.getEstado().toString().equals("ACEPTADA"))
                .collect(Collectors.toList());
    }

    public List<Reserva> getTodasReservas() {
        return new ArrayList<>(reservas);
    }
}
