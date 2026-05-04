package modelo;

import java.time.LocalDateTime;

public class Prestamo {
    private String prestamoId;
    private CopiaJuego copia;
    private LocalDateTime fechaPrestamo;
    private LocalDateTime fechaDevolucion;
    private boolean advertenciaDificultad;
    private boolean activo;
    private Usuario usuario;

    public Prestamo(String prestamoId, CopiaJuego copia, boolean advertenciaDificultad) {
        this.prestamoId = prestamoId;
        this.copia = copia;
        this.advertenciaDificultad = advertenciaDificultad;
        this.fechaPrestamo = LocalDateTime.now();
        this.activo = true;
        copia.marcarPrestada();
    }

    public Prestamo(String prestamoId, CopiaJuego copia, boolean advertenciaDificultad, Usuario usuario) {
        this(prestamoId, copia, advertenciaDificultad);
        this.usuario = usuario;
    }

    public Prestamo(String prestamoId, CopiaJuego copia, LocalDateTime fechaPrestamo,
            LocalDateTime fechaDevolucion, boolean advertenciaDificultad, boolean activo) {
        this.prestamoId = prestamoId;
        this.copia = copia;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.advertenciaDificultad = advertenciaDificultad;
        this.activo = activo;
    }

    public Prestamo(String prestamoId, CopiaJuego copia, LocalDateTime fechaPrestamo,
            LocalDateTime fechaDevolucion, boolean advertenciaDificultad, boolean activo, Usuario usuario) {
        this(prestamoId, copia, fechaPrestamo, fechaDevolucion, advertenciaDificultad, activo);
        this.usuario = usuario;
    }

    public void registrarDevolucion() {
        this.fechaDevolucion = LocalDateTime.now();
        this.activo = false;
        copia.marcarDevuelta();
    }

    public boolean estaActivo() {
        return activo;
    }

    public CopiaJuego getCopia() {
        return copia;
    }

    public String getPrestamoId() {
        return prestamoId;
    }

    public LocalDateTime getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public boolean isAdvertenciaDificultad() {
        return advertenciaDificultad;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean tieneAdvertencia() {
        return advertenciaDificultad;
    }
}
