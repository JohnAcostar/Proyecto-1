package modelo;

import java.util.HashSet;
import java.util.Set;

/**
 * Bebida servida en el café.
 * Puede ser alcohólica, caliente o fría.
 * Registra alérgenos.
 */
public class Bebida extends ProductoMenu {
    public enum TipoTemperatura {
        CALIENTE, FRIA
    }

    private TipoTemperatura temperatura;
    private boolean conAlcohol;
    private Set<String> alergenos;

    public Bebida(String productoId, String nombre, double precioBase, TipoTemperatura temperatura) {
        super(productoId, nombre, precioBase);
        this.temperatura = temperatura;
        this.conAlcohol = false;
        this.alergenos = new HashSet<>();
    }

    public Bebida(String productoId, String nombre, double precioBase, TipoTemperatura temperatura,
            boolean conAlcohol) {
        this(productoId, nombre, precioBase, temperatura);
        this.conAlcohol = conAlcohol;
    }

    @Override
    public double calcularPrecioFinal() {
        double precio = precioBase;
        if (temperatura == TipoTemperatura.CALIENTE) {
            precio += 500; // +500 pesos para bebidas calientes
        }
        return precio;
    }

    @Override
    public String getTipo() {
        return "BEBIDA";
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
    public TipoTemperatura getTemperatura() {
        return temperatura;
    }

    public boolean isConAlcohol() {
        return conAlcohol;
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
    public void setConAlcohol(boolean conAlcohol) {
        this.conAlcohol = conAlcohol;
    }
}
