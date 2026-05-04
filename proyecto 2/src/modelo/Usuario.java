package modelo;

import java.util.ArrayList;
import java.util.List;

public abstract class Usuario {
    private String login;
    private String password;
    private String id;
    private double puntosDeFidelidad;
    private List<JuegoDeMesa> favoritos;
    private List<Venta> historialVentas;
    private List<Prestamo> historialPrestamos;

    protected Usuario(String login, String password, String id) {
        this.login = login;
        this.password = password;
        this.id = id;
        this.puntosDeFidelidad = 0;
        this.favoritos = new ArrayList<>();
        this.historialVentas = new ArrayList<>();
        this.historialPrestamos = new ArrayList<>();
    }

    public boolean autenticar(String login, String password) {
        return this.login.equals(login) && this.password.equals(password);
    }

    public void cambiarPassword(String nueva) {
        this.password = nueva;
    }

    public void agregarFavorito(JuegoDeMesa juego) {
        favoritos.add(juego);
    }

    public void quitarFavorito(JuegoDeMesa juego) {
        favoritos.remove(juego);
    }

    public List<JuegoDeMesa> getFavoritos() {
        return new ArrayList<>(favoritos);
    }

    public void agregarVenta(Venta venta) {
        historialVentas.add(venta);
    }

    public void agregarPrestamo(Prestamo prestamo) {
        historialPrestamos.add(prestamo);
    }

    public List<Venta> getHistorialVentas() {
        return new ArrayList<>(historialVentas);
    }

    public List<Prestamo> getHistorialPrestamos() {
        return new ArrayList<>(historialPrestamos);
    }

    public void usarPuntos(double valor) {
        this.puntosDeFidelidad = Math.max(0, this.puntosDeFidelidad - valor);
    }

    public void acumularPuntos(double puntos) {
        this.puntosDeFidelidad += puntos;
    }

    public double getPuntosDeFidelidad() {
        return puntosDeFidelidad;
    }

    public void setPuntosDeFidelidad(double puntosDeFidelidad) {
        this.puntosDeFidelidad = puntosDeFidelidad;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getId() {
        return id;
    }
}
