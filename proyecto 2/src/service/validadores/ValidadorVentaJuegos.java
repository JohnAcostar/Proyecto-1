package service.validadores;

import modelo.Cliente;
import modelo.ResultadoValidacion;
import modelo.VentaJuegos;

/**
 * Valida si una venta de juegos puede realizarse.
 */
public class ValidadorVentaJuegos {

    public ResultadoValidacion validar(VentaJuegos venta, Cliente cliente) {
        if (venta == null) {
            return ResultadoValidacion.error("Venta nula", "ERR_VENTA_NULL");
        }

        if (cliente == null) {
            return ResultadoValidacion.error("Cliente nulo", "ERR_CLIENTE_NULL");
        }

        return ResultadoValidacion.exitoso();
    }

    public ResultadoValidacion validarDescuentos(double descuentoEmpleado, double descuentoCodigo) {
        // Descuentos no acumulables: máximo el mayor de los dos
        if (descuentoEmpleado > 0 && descuentoCodigo > 0) {
            return ResultadoValidacion.error("Descuentos no acumulables. Aplique el mayor",
                    "ERR_DESCUENTOS_ACUMULADOS");
        }

        if (descuentoEmpleado > 0.20) {
            return ResultadoValidacion.error("Descuento de empleado no puede superar 20%", "ERR_DESC_EMPLEADO");
        }

        if (descuentoCodigo > 0.10) {
            return ResultadoValidacion.error("Descuento por código no puede superar 10%", "ERR_DESC_CODIGO");
        }

        return ResultadoValidacion.exitoso();
    }
}
