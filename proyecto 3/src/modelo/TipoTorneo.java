package modelo;

/**
 * Enumeración que define los tipos de torneos disponibles en el sistema.
 */
public enum TipoTorneo {
    /**
     * Torneo amigable: Sin cuota de inscripción.
     * Los participantes reciben un voucher de descuento.
     */
    AMISTOSO("Amistoso", false),
    
    /**
     * Torneo competitivo: Requiere cuota de inscripción.
     * Hay premio en efectivo commensurado con el dinero recaudado.
     */
    COMPETITIVO("Competitivo", true);

    private final String nombre;
    private final boolean requiereComisión;

    TipoTorneo(String nombre, boolean requiereComisión) {
        this.nombre = nombre;
        this.requiereComisión = requiereComisión;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean requiereComisión() {
        return requiereComisión;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
