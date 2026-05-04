package modelo;

public class CopiaJuego {
    private String copyId;
    private JuegoDeMesa juego;
    private boolean disponible;
    private boolean enInventarioPrestamo;
    private boolean enInventarioVentas;
    private int vecesPrestado;

    public CopiaJuego(String copyId, JuegoDeMesa juego) {
        this.copyId = copyId;
        this.juego = juego;
        this.disponible = true;
    }

    public CopiaJuego(String copyId, JuegoDeMesa juego, boolean disponible,
            boolean enInventarioPrestamo, boolean enInventarioVentas, int vecesPrestado) {
        this.copyId = copyId;
        this.juego = juego;
        this.disponible = disponible;
        this.enInventarioPrestamo = enInventarioPrestamo;
        this.enInventarioVentas = enInventarioVentas;
        this.vecesPrestado = vecesPrestado;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void marcarPrestada() {
        this.disponible = false;
        this.vecesPrestado++;
    }

    public void marcarVendida() {
        this.disponible = false;
    }

    public void marcarDevuelta() {
        this.disponible = true;
    }

    public JuegoDeMesa getJuego() {
        return juego;
    }

    public String getCopyId() {
        return copyId;
    }

    public int getVecesPrestado() {
        return vecesPrestado;
    }

    public boolean isEnInventarioPrestamo() {
        return enInventarioPrestamo;
    }

    public boolean isEnInventarioVentas() {
        return enInventarioVentas;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public void setEnInventarioPrestamo(boolean enInventarioPrestamo) {
        this.enInventarioPrestamo = enInventarioPrestamo;
    }

    public void setEnInventarioVentas(boolean enInventarioVentas) {
        this.enInventarioVentas = enInventarioVentas;
    }
}
