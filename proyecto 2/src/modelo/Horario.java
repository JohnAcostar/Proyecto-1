package modelo;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Horario {
    private List<Turno> turnos;

    public Horario() {
        this.turnos = new ArrayList<>();
    }

    public void agregarTurno(Turno turno) {
        turnos.add(turno);
    }

    public List<Turno> obtenerTurnosParaEmpleado(Empleado empleado) {
        return turnos.stream()
                .filter(t -> t.getEmpleado().equals(empleado))
                .collect(Collectors.toList());
    }

    public boolean validarPersonalMinimo(DayOfWeek dia) {
        long cocineros = turnos.stream()
                .filter(t -> t.getDia() == dia && t.getEmpleado() instanceof Cocinero)
                .count();
        long meseros = turnos.stream()
                .filter(t -> t.getDia() == dia && t.getEmpleado() instanceof Mesero)
                .count();
        return cocineros >= 1 && meseros >= 2;
    }
}
