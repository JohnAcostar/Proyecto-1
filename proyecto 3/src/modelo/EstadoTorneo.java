package modelo;

/**
 * Enumeración que define los posibles estados de un torneo.
 */
public enum EstadoTorneo {
    /**
     * Torneo creado pero aún no iniciado. Los participantes pueden registrarse.
     */
    ABIERTO("Abierto"),
    
    /**
     * Torneo en ejecución. No se pueden registrar nuevos participantes.
     */
    EN_PROGRESO("En progreso"),
    
    /**
     * Torneo completado. Los resultados están finalizados.
     */
    FINALIZADO("Finalizado"),
    
    /**
     * Torneo cancelado. No se completó.
     */
    CANCELADO("Cancelado");

    private final String descripcion;

    EstadoTorneo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
