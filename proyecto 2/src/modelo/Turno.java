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

    public Turno(String turnoId, String idEmpleado, int diaSemana, String horaInicio, String horaFin) {
        this.turnoId = turnoId;
        this.dia = DayOfWeek.of(diaSemana);
        this.startTime = LocalTime.parse(horaInicio);
        this.endTime = LocalTime.parse(horaFin);
        this.activo = true;
    }

    public DayOfWeek getDia() {
        return dia;
    }

    public int getDiaSemana() {
        return dia.getValue();
    }

    public LocalTime getHoraInicio() {
        return startTime;
    }

    public LocalTime getHoraFin() {
        return endTime;
    }

    public EstadoReserva getEstado() {
        return activo ? EstadoReserva.ACEPTADA : EstadoReserva.CANCELADO;
    }

    public Empleado getEmpleado() {
        return empleado;
    }
}
