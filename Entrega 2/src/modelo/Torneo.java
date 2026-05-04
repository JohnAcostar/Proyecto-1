package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

/**
 * Representa un torneo de juegos de mesa que se lleva a cabo en el café.
 * Cada torneo tiene características específicas como tipo, juego, número de participantes y día de la semana.
 */
public class Torneo implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nombre;
    private int idJuego; // ID del juego de mesa asociado
    private String nombreJuego; // Nombre del juego para referencia
    private TipoTorneo tipo; // AMISTOSO o COMPETITIVO
    private EstadoTorneo estado;
    private int cantidadMaximaParticipantes;
    private int diaSemana; // 1-7 (Lunes=1, Domingo=7)
    private LocalDate fechaCreacion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int idAdministrador; // El administrador que creó el torneo
    private String nombreAdministrador;
    private double montoEntrada; // 0 para torneos amistosos
    private double premioTotal; // Total de premios disponibles (sum de inscripciones)
    
    // Participantes y control
    private List<ParticipanteTorneo> participantes;
    private int spotReservadosFans; // Spots reservados para fans (20% redondeado hacia arriba)
    private int spotsUsadosFans; // Spots de fans ya usados

    /**
     * Constructor completo para crear un nuevo torneo.
     */
    public Torneo(int id, String nombre, int idJuego, String nombreJuego, TipoTorneo tipo,
                  int cantidadMaximaParticipantes, int diaSemana, int idAdministrador, 
                  String nombreAdministrador, double montoEntrada) {
        this.id = id;
        this.nombre = nombre;
        this.idJuego = idJuego;
        this.nombreJuego = nombreJuego;
        this.tipo = tipo;
        this.estado = EstadoTorneo.ABIERTO;
        this.cantidadMaximaParticipantes = cantidadMaximaParticipantes;
        this.diaSemana = diaSemana;
        this.fechaCreacion = LocalDate.now();
        this.idAdministrador = idAdministrador;
        this.nombreAdministrador = nombreAdministrador;
        this.montoEntrada = montoEntrada;
        this.premioTotal = 0;
        this.participantes = new ArrayList<>();
        this.spotReservadosFans = calcularSpotsReservadosFans();
        this.spotsUsadosFans = 0;
    }

    /**
     * Calcula cuántos spots deben reservarse para fans (20% redondeado hacia arriba).
     */
    private int calcularSpotsReservadosFans() {
        return (int) Math.ceil(cantidadMaximaParticipantes * 0.20);
    }

    /**
     * Obtiene el número de spots disponibles para participantes normales.
     */
    public int getSpotsDisponiblesNormales() {
        int totalUsados = participantes.size();
        int normalesUsados = totalUsados - spotsUsadosFans;
        int normalesDisponibles = cantidadMaximaParticipantes - spotReservadosFans - normalesUsados;
        return Math.max(0, normalesDisponibles);
    }

    /**
     * Obtiene el número de spots disponibles para fans.
     */
    public int getSpotsDisponiblesFans() {
        return Math.max(0, spotReservadosFans - spotsUsadosFans);
    }

    /**
     * Obtiene la cantidad total de spots disponibles.
     */
    public int getSpotsDisponibles() {
        return cantidadMaximaParticipantes - participantes.size();
    }

    /**
     * Verifica si hay algún spot disponible.
     */
    public boolean haySpotDisponible() {
        return participantes.size() < cantidadMaximaParticipantes;
    }

    /**
     * Verifica si hay spots disponibles para fans específicamente.
     */
    public boolean haySpotDisponibleParaFan() {
        return spotsUsadosFans < spotReservadosFans || getSpotsDisponiblesNormales() > 0;
    }

    /**
     * Agrega un participante al torneo.
     */
    public boolean agregarParticipante(ParticipanteTorneo participante) {
        if (participantes.size() >= cantidadMaximaParticipantes) {
            return false; // Torneo lleno
        }
        
        // Actualizar contadores de spots reservados para fans si aplica.
        if (participante.esFan() && spotsUsadosFans < spotReservadosFans) {
            participante.setUsoSpotReservadoFan(true);
            spotsUsadosFans++;
        }
        
        return participantes.add(participante);
    }

    /**
     * Remueve un participante del torneo.
     */
    public boolean removerParticipante(int idUsuario) {
        boolean removed = participantes.removeIf(p -> {
            if (p.getIdUsuario() == idUsuario) {
                // Actualizar contadores de spots reservados para fans si aplica.
                if (p.usoSpotReservadoFan()) {
                    spotsUsadosFans = Math.max(0, spotsUsadosFans - 1);
                }
                return true;
            }
            return false;
        });
        return removed;
    }

    public boolean removerParticipante(String idUsuario) {
        return removerParticipante(parsearId(idUsuario));
    }

    /**
     * Obtiene un participante por su ID de usuario.
     */
    public ParticipanteTorneo obtenerParticipante(int idUsuario) {
        return participantes.stream()
                .filter(p -> p.getIdUsuario() == idUsuario)
                .findFirst()
                .orElse(null);
    }

    public ParticipanteTorneo obtenerParticipante(String idUsuario) {
        return obtenerParticipante(parsearId(idUsuario));
    }

    /**
     * Cuenta cuántos participantes tiene un usuario registrado en este torneo.
     */
    public int contarParticipantesDelUsuario(int idUsuario) {
        return (int) participantes.stream()
                .filter(p -> p.getIdUsuario() == idUsuario)
                .count();
    }

    public int contarParticipantesDelUsuario(String idUsuario) {
        return contarParticipantesDelUsuario(parsearId(idUsuario));
    }

    private int parsearId(String idTexto) {
        String digitos = idTexto == null ? "" : idTexto.replaceAll("\\D", "");
        return digitos.isEmpty() ? 0 : Integer.parseInt(digitos);
    }

    /**
     * Incrementa el premio total cuando alguien se registra en un torneo competitivo.
     */
    public void agregarPremio(double monto) {
        this.premioTotal += monto;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdJuego() {
        return idJuego;
    }

    public void setIdJuego(int idJuego) {
        this.idJuego = idJuego;
    }

    public String getNombreJuego() {
        return nombreJuego;
    }

    public void setNombreJuego(String nombreJuego) {
        this.nombreJuego = nombreJuego;
    }

    public TipoTorneo getTipo() {
        return tipo;
    }

    public void setTipo(TipoTorneo tipo) {
        this.tipo = tipo;
    }

    public EstadoTorneo getEstado() {
        return estado;
    }

    public void setEstado(EstadoTorneo estado) {
        this.estado = estado;
    }

    public int getCantidadMaximaParticipantes() {
        return cantidadMaximaParticipantes;
    }

    public void setCantidadMaximaParticipantes(int cantidadMaximaParticipantes) {
        this.cantidadMaximaParticipantes = cantidadMaximaParticipantes;
    }

    public int getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(int diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    public String getNombreAdministrador() {
        return nombreAdministrador;
    }

    public void setNombreAdministrador(String nombreAdministrador) {
        this.nombreAdministrador = nombreAdministrador;
    }

    public double getMontoEntrada() {
        return montoEntrada;
    }

    public void setMontoEntrada(double montoEntrada) {
        this.montoEntrada = montoEntrada;
    }

    public double getPremioTotal() {
        return premioTotal;
    }

    public void setPremioTotal(double premioTotal) {
        this.premioTotal = premioTotal;
    }

    public List<ParticipanteTorneo> getParticipantes() {
        return new ArrayList<>(participantes);
    }

    public int getCantidadParticipantes() {
        return participantes.size();
    }

    public int getSpotReservadosFans() {
        return spotReservadosFans;
    }

    public int getSpotsUsadosFans() {
        return spotsUsadosFans;
    }

    @Override
    public String toString() {
        return "Torneo{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", juego='" + nombreJuego + '\'' +
                ", tipo=" + tipo +
                ", estado=" + estado +
                ", participantes=" + participantes.size() + "/" + cantidadMaximaParticipantes +
                ", dia=" + diaSemana +
                '}';
    }
}
