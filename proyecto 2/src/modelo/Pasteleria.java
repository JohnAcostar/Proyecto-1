package modelo;

import java.util.HashSet;
import java.util.Set;

/**
 * Pastelería/productos de repostería servidos en el café.
 * Registra tiempos de preparación y alérgenos.
 */
public class Pasteleria extends ProductoMenu {
    private int tiempoPreparacion; // en minutos
    private Set<String> alergenos;

    public Pasteleria(String productoId, String nombre, double precioBase, int tiempoPreparacion) {
        super(productoId, nombre, precioBase);
        this.tiempoPreparacion = tiempoPreparacion;
        this.alergenos = new HashSet<>();
    }

    @Override
    public double calcularPrecioFinal() {
        return precioBase; // Sin ajuste adicional
    }

    @Override
    public String getTipo() {
        return "PASTELERIA";
    }

    public void agregarAlergeno(String alergeno) {
        if (alergeno != null && !alergeno.trim().isEmpty()) {
            alergenos.add(alergeno.toLowerCase());
        }
    }

    public boolean tieneAlergeno(String alergeno) {
        return alergenos.contains(alergeno.toLowerCase());
    }

    public void removerAlergeno(String alergeno) {
        alergenos.remove(alergeno.toLowerCase());
    }

    // Getters
    public int getTiempoPreparacion() {
        return tiempoPreparacion;
    }

    public Set<String> getAlergenos() {
        return new HashSet<>(alergenos);
    }

    public String getDescripcionAlergenos() {
        if (alergenos.isEmpty()) {
            return "Sin alérgenos conocidos";
        }
        return String.join(", ", alergenos);
    }

    // Setters
    public void setTiempoPreparacion(int tiempoPreparacion) {
        this.tiempoPreparacion = tiempoPreparacion;
    }
}
