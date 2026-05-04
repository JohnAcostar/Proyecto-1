package modelo;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa el café/bar del sistema.
 * Gestiona mesas, capacidad, ocupación y horarios.
 * Clave para validación de reservas y préstamos.
 */
public class Cafe {
    private String cafeId;
    private int capacidadMaxima;
    private LocalTime horaApertura;
    private LocalTime horaCierre;
    private List<Mesa> mesas;
    private Administrador administrador;
    private int ocupacionActual;

    public Cafe(String cafeId, int capacidadMaxima, LocalTime horaApertura, LocalTime horaCierre) {
        this.cafeId = cafeId;
        this.capacidadMaxima = capacidadMaxima;
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
        this.mesas = new ArrayList<>();
        this.ocupacionActual = 0;
    }

    public Cafe(String cafeId, int capacidadMaxima, LocalTime horaApertura, LocalTime horaCierre,
            Administrador administrador) {
        this(cafeId, capacidadMaxima, horaApertura, horaCierre);
        this.administrador = administrador;
    }

    public void agregarMesa(Mesa mesa) {
        if (mesa != null && !mesas.contains(mesa)) {
            mesas.add(mesa);
        }
    }

    public void ocuparMesa(Mesa mesa) {
        if (mesas.contains(mesa) && ocupacionActual + mesa.getCapacidad() <= capacidadMaxima) {
            mesa.ocupar();
            ocupacionActual += mesa.getCapacidad();
        }
    }

    public void liberarMesa(Mesa mesa) {
        if (mesas.contains(mesa) && mesa.estaOcupada()) {
            mesa.liberar();
            ocupacionActual -= mesa.getCapacidad();
            if (ocupacionActual < 0) ocupacionActual = 0;
        }
    }

    public int getCapacidadDisponible() {
        return capacidadMaxima - ocupacionActual;
    }

    public boolean puedeAlbergarReserva(Reserva reserva) {
        return getCapacidadDisponible() >= reserva.getCantidadPersonas();
    }

    public Mesa encontrarMesaDisponible(int cantidadPersonas) {
        for (Mesa mesa : mesas) {
            if (!mesa.estaOcupada() && mesa.getCapacidad() >= cantidadPersonas) {
                return mesa;
            }
        }
        return null;
    }

    public List<Mesa> getMesasDisponibles() {
        List<Mesa> disponibles = new ArrayList<>();
        for (Mesa mesa : mesas) {
            if (!mesa.estaOcupada()) {
                disponibles.add(mesa);
            }
        }
        return disponibles;
    }

    public String getEstadoOcupacion() {
        return String.format("Ocupación: %d/%d personas", ocupacionActual, capacidadMaxima);
    }

    public boolean estaAbiertoAhora() {
        LocalTime ahora = LocalTime.now();
        return !ahora.isBefore(horaApertura) && ahora.isBefore(horaCierre);
    }

    // Getters
    public String getCafeId() {
        return cafeId;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public LocalTime getHoraApertura() {
        return horaApertura;
    }

    public LocalTime getHoraCierre() {
        return horaCierre;
    }

    public List<Mesa> getMesas() {
        return new ArrayList<>(mesas);
    }

    public Administrador getAdministrador() {
        return administrador;
    }

    public int getOcupacionActual() {
        return ocupacionActual;
    }

    public void setAdministrador(Administrador admin) {
        this.administrador = admin;
    }
}
