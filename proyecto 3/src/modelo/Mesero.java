package modelo;

import java.util.HashSet;
import java.util.Set;

public class Mesero extends Empleado {
    private Set<String> juegosQuePuedeEnsenar;

    public Mesero(String login, String password, String id, String nombre, String codigoDescuento) {
        super(login, password, id, nombre, codigoDescuento);
        this.juegosQuePuedeEnsenar = new HashSet<>();
    }

    public boolean puedeEnsenar(JuegoDeMesa juego) {
        return juegosQuePuedeEnsenar.contains(juego.getIdJuego());
    }

    public void registrarJuegoEnsenable(JuegoDeMesa juego) {
        juegosQuePuedeEnsenar.add(juego.getIdJuego());
    }
}
