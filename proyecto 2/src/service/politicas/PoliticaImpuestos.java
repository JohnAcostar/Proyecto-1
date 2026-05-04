package service.politicas;

import modelo.Venta;
import modelo.VentaCafe;
import modelo.VentaJuegos;

/**
 * Política configurable para cálculo de impuestos.
 * Define IVA y retención para diferentes tipos de venta.
 */
public class PoliticaImpuestos {
    private double ivaJuegos; // Default: 0.19 (19%)
    private double impuestoConsumoCafe; // Default: 0.08 (8%)
    private double retencionesAdicionales; // Default: 0.0

    public PoliticaImpuestos() {
        this.ivaJuegos = 0.19;
        this.impuestoConsumoCafe = 0.08;
        this.retencionesAdicionales = 0.0;
    }

    public PoliticaImpuestos(double ivaJuegos, double impuestoConsumoCafe) {
        this.ivaJuegos = ivaJuegos;
        this.impuestoConsumoCafe = impuestoConsumoCafe;
        this.retencionesAdicionales = 0.0;
    }

    /**
     * Calcula el impuesto según el tipo de venta.
     */
    public double calcularImpuesto(Venta venta, double subtotal) {
        if (venta instanceof VentaJuegos) {
            return subtotal * ivaJuegos;
        } else if (venta instanceof VentaCafe) {
            return subtotal * impuestoConsumoCafe;
        }
        return 0.0;
    }

    /**
     * Calcula el total incluyendo impuestos.
     */
    public double calcularTotal(Venta venta, double subtotal) {
        double impuesto = calcularImpuesto(venta, subtotal);
        return subtotal + impuesto;
    }

    /**
     * Retorna desglose de impuestos para un subtotal.
     */
    public String desglose(Venta venta, double subtotal) {
        double impuesto = calcularImpuesto(venta, subtotal);
        double total = subtotal + impuesto;

        if (venta instanceof VentaJuegos) {
            return String.format("Subtotal: $%.0f | IVA (19%%): $%.0f | Total: $%.0f", subtotal, impuesto, total);
        } else if (venta instanceof VentaCafe) {
            return String.format("Subtotal: $%.0f | Imp. Consumo (8%%): $%.0f | Total: $%.0f", subtotal, impuesto,
                    total);
        }

        return String.format("Subtotal: $%.0f | Total: $%.0f", subtotal, total);
    }

    // Getters y Setters
    public double getIvaJuegos() {
        return ivaJuegos;
    }

    public void setIvaJuegos(double ivaJuegos) {
        this.ivaJuegos = ivaJuegos;
    }

    public double getImpuestoConsumoCafe() {
        return impuestoConsumoCafe;
    }

    public void setImpuestoConsumoCafe(double impuestoConsumoCafe) {
        this.impuestoConsumoCafe = impuestoConsumoCafe;
    }

    public double getRetenciones() {
        return retencionesAdicionales;
    }

    public void setRetenciones(double retencionesAdicionales) {
        this.retencionesAdicionales = retencionesAdicionales;
    }
}
