package modelo;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class Turno {
    private String turnoId;
    private DayOfWeek dia;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean activo;
    private Empleado empleado;

    public Turno(String turnoId, DayOfWeek dia, LocalTime startTime, LocalTime endTime, Empleado empleado) {
        this.turnoId = turnoId;
        this.dia = dia;
        this.startTime = startTime;
        this.endTime = endTime;
        this.empleado = empleado;
        this.activo = true;
    }

    public DayOfWeek getDia() {
        return dia;
    }

    public Empleado getEmpleado() {
        return empleado;
    }
}
