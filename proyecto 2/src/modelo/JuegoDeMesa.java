package modelo;

public class JuegoDeMesa {
    private String idJuego;
    private String nombre;
    private int anioPublicacion;
    private String empresaMatriz;
    private int minJugadores;
    private int maxJugadores;
    private RestriccionEdad restriccionEdad;
    private CategoriaJuego categoria;
    private EstadoJuego estado;
    private boolean dificil;
    private double precioVenta;

    public JuegoDeMesa(String idJuego, String nombre, int anioPublicacion, String empresaMatriz,
            int minJugadores, int maxJugadores, RestriccionEdad restriccionEdad,
            CategoriaJuego categoria, EstadoJuego estado, boolean dificil) {
        this.idJuego = idJuego;
        this.nombre = nombre;
        this.anioPublicacion = anioPublicacion;
        this.empresaMatriz = empresaMatriz;
        this.minJugadores = minJugadores;
        this.maxJugadores = maxJugadores;
        this.restriccionEdad = restriccionEdad;
        this.categoria = categoria;
        this.estado = estado;
        this.dificil = dificil;
        this.precioVenta = 0.0;
    }

    public boolean esAptoParaCantidadJugadores(int cantidad) {
        return cantidad >= minJugadores && cantidad <= maxJugadores;
    }

    public boolean esAptoParaEdad(boolean hayNinos, boolean hayJovenes) {
        if (restriccionEdad == RestriccionEdad.ADULTOS_SOLO) {
            return !hayNinos && !hayJovenes;
        }
        if (restriccionEdad == RestriccionEdad.MAYORES_DE_5) {
            return !hayNinos;
        }
        return true;
    }

    public void cambiarEstado(EstadoJuego nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public String getIdJuego() {
        return idJuego;
    }

    public String getNombre() {
        return nombre;
    }

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    public String getEmpresaMatriz() {
        return empresaMatriz;
    }

    public int getMinJugadores() {
        return minJugadores;
    }

    public int getMaxJugadores() {
        return maxJugadores;
    }

    public RestriccionEdad getRestriccionEdad() {
        return restriccionEdad;
    }

    public CategoriaJuego getCategoria() {
        return categoria;
    }

    public EstadoJuego getEstado() {
        return estado;
    }

    public boolean isDificil() {
        return dificil;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }
}
