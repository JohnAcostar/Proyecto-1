package modelo;

/**
 * Encapsula el resultado de una validación.
 * Permite retornar si fue válido + mensaje de error si aplica.
 */
public class ResultadoValidacion {
    private boolean valido;
    private String mensaje;
    private String codigoError;
    private boolean esAdvertencia;

    public ResultadoValidacion(boolean valido) {
        this(valido, valido ? "OK" : "Validación fallida", null, false);
    }

    public ResultadoValidacion(boolean valido, String mensaje) {
        this(valido, mensaje, null, false);
    }

    public ResultadoValidacion(boolean valido, String mensaje, String codigoError) {
        this(valido, mensaje, codigoError, false);
    }

    public ResultadoValidacion(boolean valido, String mensaje, String codigoError, boolean esAdvertencia) {
        this.valido = valido;
        this.mensaje = mensaje;
        this.codigoError = codigoError;
        this.esAdvertencia = esAdvertencia;
    }

    public static ResultadoValidacion exitoso() {
        return new ResultadoValidacion(true, "Validación exitosa", null, false);
    }

    public static ResultadoValidacion error(String mensaje) {
        return new ResultadoValidacion(false, mensaje, null, false);
    }

    public static ResultadoValidacion error(String mensaje, String codigoError) {
        return new ResultadoValidacion(false, mensaje, codigoError, false);
    }

    public static ResultadoValidacion warning(String mensaje, String codigoError) {
        return new ResultadoValidacion(true, mensaje, codigoError, true);
    }

    // Getters
    public boolean esValido() {
        return valido;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getCodigoError() {
        return codigoError;
    }

    public boolean esAdvertencia() {
        return esAdvertencia;
    }

    @Override
    public String toString() {
        if (esAdvertencia) {
            return "⚠ " + mensaje;
        }
        return valido ? "✓ " + mensaje : "✗ " + mensaje;
    }
}
