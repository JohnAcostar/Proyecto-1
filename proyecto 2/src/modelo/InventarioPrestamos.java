package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InventarioPrestamos {
    private List<CopiaJuego> copias;
    private List<Prestamo> historial;

    public InventarioPrestamos() {
        this.copias = new ArrayList<>();
        this.historial = new ArrayList<>();
    }

    public int consultarDisponibilidad(JuegoDeMesa juego) {
        return (int) copias.stream()
                .filter(c -> c.getJuego().getIdJuego().equals(juego.getIdJuego()) && c.isDisponible())
                .count();
    }

    public void agregarCopia(CopiaJuego copia) {
        copia.setEnInventarioPrestamo(true);
        copias.add(copia);
    }

    public CopiaJuego buscarCopiaDisponible(JuegoDeMesa juego) {
        var disponibles = copias.stream()
                .filter(c -> c.getJuego().getIdJuego().equals(juego.getIdJuego()) && c.isDisponible())
                .collect(java.util.stream.Collectors.toList());
        if (disponibles.isEmpty()) {
            return null;
        }
        java.util.Collections.shuffle(disponibles);
        return disponibles.get(0);
    }

    public Prestamo checkOut(String prestamoId, JuegoDeMesa juego, boolean advertenciaDificultad) {
        CopiaJuego copia = buscarCopiaDisponible(juego);
        if (copia == null) {
            return null;
        }
        Prestamo nuevo = new Prestamo(prestamoId, copia, advertenciaDificultad);
        historial.add(nuevo);
        return nuevo;
    }

    public Prestamo checkOut(String prestamoId, JuegoDeMesa juego, boolean advertenciaDificultad, Usuario usuario) {
        CopiaJuego copia = buscarCopiaDisponible(juego);
        if (copia == null) {
            return null;
        }
        Prestamo nuevo = new Prestamo(prestamoId, copia, advertenciaDificultad, usuario);
        historial.add(nuevo);
        return nuevo;
    }

    public List<Prestamo> historial(JuegoDeMesa juego) {
        return historial.stream()
                .filter(p -> p.getCopia().getJuego().getIdJuego().equals(juego.getIdJuego()))
                .collect(Collectors.toList());
    }

    public List<CopiaJuego> getCopias() {
        return new ArrayList<>(copias);
    }

    public List<Prestamo> getHistorialCompleto() {
        return new ArrayList<>(historial);
    }

    public void reemplazarDatos(List<CopiaJuego> nuevasCopias, List<Prestamo> nuevoHistorial) {
        this.copias.clear();
        this.copias.addAll(nuevasCopias);
        this.historial.clear();
        this.historial.addAll(nuevoHistorial);
    }
}
