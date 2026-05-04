package service.politicas;

import modelo.Venta;
import modelo.VentaCafe;
import modelo.VentaJuegos;

/**
 * Política configurable para acumulación de puntos de fidelidad.
 * Permite cambiar el modelo de cálculo sin afectar el sistema.
 */
public class PoliticaFidelidad {
    public enum ModoCalculo {
        PORCENTAJE, // 1% de la compra = 1 punto (por cada 100 pesos, 1 punto)
        FIJO // 1 peso = X puntos (ej: 1 peso = 0.01 puntos)
    }

    private ModoCalculo modo;
    private double valorCalculoParametro; // Si PORCENTAJE: 0.01 (1%), si FIJO: 0.01

    public PoliticaFidelidad() {
        this.modo = ModoCalculo.FIJO;
        this.valorCalculoParametro = 1.0; // 1 peso = 1 punto (legacy)
    }

    public PoliticaFidelidad(ModoCalculo modo, double parametro) {
        this.modo = modo;
        this.valorCalculoParametro = parametro;
    }

    /**
     * Calcula puntos a acumular según venta y modo.
     */
    public int calcularPuntos(Venta venta, double subtotal) {
        if (venta instanceof VentaCafe) {
            // Ventas café: 0 puntos (opcional según política)
            return 0;
        }

        if (venta instanceof VentaJuegos) {
            switch (modo) {
            case PORCENTAJE:
                // 1% de compra = 1 punto. Si paga 10000, gana 100 puntos
                return (int) (subtotal * valorCalculoParametro);
            case FIJO:
                // 1 peso = valorCalculoParametro puntos
                return (int) (subtotal * valorCalculoParametro);
            default:
                return 0;
            }
        }

        return 0;
    }

    /**
     * Convierte puntos a valor en dinero (para canje).
     * Por defecto: 1 punto = 1 peso.
     */
    public double calcularValorPunto(int puntos) {
        // Flexible: puede cambiar si hace falta
        return puntos * 1.0;
    }

    public String describeModo() {
        switch (modo) {
        case PORCENTAJE:
            return String.format("Modo PORCENTAJE: %.2f%% de cada compra", valorCalculoParametro * 100);
        case FIJO:
            return String.format("Modo FIJO: %.2f puntos por peso gastado", valorCalculoParametro);
        default:
            return "Modo desconocido";
        }
    }

    // Getters
    public ModoCalculo getModo() {
        return modo;
    }

    public double getValorParametro() {
        return valorCalculoParametro;
    }

    // Setters
    public void setModo(ModoCalculo modo) {
        this.modo = modo;
    }

    public void setValorParametro(double parametro) {
        this.valorCalculoParametro = parametro;
    }
}
