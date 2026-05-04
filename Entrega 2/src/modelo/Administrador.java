package modelo;

public class Administrador extends Usuario {
    public Administrador(String login, String password, String id) {
        super(login, password, id);
    }

    public Administrador(String login, String nombre, String id, String password) {
        super(login, password, id);
    }
}
