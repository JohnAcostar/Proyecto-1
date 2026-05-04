package modelo;

import java.util.ArrayList;
import java.util.List;

public class InventarioVenta {
    private List<CopiaJuego> copias;

    public InventarioVenta() {
        this.copias = new ArrayList<>();
    }

    public void agregarCopia(CopiaJuego copia) {
        copia.setEnInventarioVentas(true);
        copias.add(copia);
    }

    public int obtenerCuentaDeStock(JuegoDeMesa juego) {
        return (int) copias.stream()
                .filter(c -> c.getJuego().getIdJuego().equals(juego.getIdJuego()) && c.isDisponible())
                .count();
    }

    public CopiaJuego vender(JuegoDeMesa juego) {
        for (CopiaJuego copia : copias) {
            if (copia.getJuego().getIdJuego().equals(juego.getIdJuego()) && copia.isDisponible()) {
                copia.marcarVendida();
                return copia;
            }
        }
        return null;
    }

    public List<CopiaJuego> getCopias() {
        return new ArrayList<>(copias);
    }

    public void reemplazarCopias(List<CopiaJuego> nuevasCopias) {
        this.copias.clear();
        this.copias.addAll(nuevasCopias);
    }
}
