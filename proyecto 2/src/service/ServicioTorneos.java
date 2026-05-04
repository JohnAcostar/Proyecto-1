package service;

import modelo.*;
import service.validadores.ValidadorTorneo;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio principal para la gestión de torneos en el sistema.
 * Maneja creación, inscripción, retiro, y finalización de torneos.
 */
public class ServicioTorneos {
    private List<Torneo> torneos;
    private List<VoucherDescuento> vouchers;
    private ValidadorTorneo validador;
    private int proximoIdTorneo = 1;
    private int proximoIdVoucher = 1;

    public ServicioTorneos() {
        this.torneos = new ArrayList<>();
        this.vouchers = new ArrayList<>();
        this.validador = new ValidadorTorneo();
    }

    /**
     * Crea un nuevo torneo con validación previa.
     */
    public ResultadoValidacion crearTorneo(String nombre, JuegoDeMesa juego, TipoTorneo tipo,
                                          int cantidadParticipantes, int diaSemana,
                                          Administrador administrador, double montoEntrada) {
        // Validar
        ResultadoValidacion validacion = validador.validarCreacionTorneo(
                nombre, juego, tipo, cantidadParticipantes, diaSemana, montoEntrada);

        if (!validacion.esValido()) {
            return validacion;
        }

        // Crear torneo
        Torneo nuevoTorneo = new Torneo(
                proximoIdTorneo++,
                nombre,
                juego.getId(),
                juego.getNombre(),
                tipo,
                cantidadParticipantes,
                diaSemana,
                administrador.getId(),
                administrador.getNombre(),
                montoEntrada
        );

        torneos.add(nuevoTorneo);
        return new ResultadoValidacion(true,
                "Torneo '" + nombre + "' creado exitosamente con ID " + nuevoTorneo.getId());
    }

    /**
     * Registra un usuario a un torneo.
     */
    public ResultadoValidacion registrarUsuarioATorneo(int idTorneo, Usuario usuario,
                                                       List<Torneo> allTornaments) {
        Torneo torneo = obtenerTorneoPorId(idTorneo);
        if (torneo == null) {
            return new ResultadoValidacion(false, "Torneo no encontrado");
        }

        // Validar
        ResultadoValidacion validacion = validador.validarRegistroTorneo(torneo, usuario, allTornaments);
        if (!validacion.esValido()) {
            return validacion;
        }

        // Crear participante
        boolean esUnFan = usuario instanceof Cliente &&
                ((Cliente) usuario).getJuegosFavoritos().stream()
                        .anyMatch(jf -> jf == torneo.getIdJuego());

        ParticipanteTorneo participante = new ParticipanteTorneo(
                usuario.getId(),
                usuario.getNombre(),
                esUnFan
        );

        // Si es torneo competitivo, registrar pago
        if (torneo.getTipo() == TipoTorneo.COMPETITIVO) {
            // En un caso real, aquí se procesaría el pago
            participante.setPagoPorEntrada(true);
            participante.setMontoInscripcion(torneo.getMontoEntrada());
            torneo.agregarPremio(torneo.getMontoEntrada());
        }

        // Agregar al torneo
        if (torneo.agregarParticipante(participante)) {
            return new ResultadoValidacion(true,
                    usuario.getNombre() + " registrado al torneo exitosamente");
        } else {
            return new ResultadoValidacion(false, "No se pudo registrar al usuario en el torneo");
        }
    }

    /**
     * Retira un usuario de un torneo, liberando todos sus spots.
     */
    public ResultadoValidacion retirarUsuarioDeTorneo(int idTorneo, Usuario usuario) {
        Torneo torneo = obtenerTorneoPorId(idTorneo);
        if (torneo == null) {
            return new ResultadoValidacion(false, "Torneo no encontrado");
        }

        // Validar
        ResultadoValidacion validacion = validador.validarRetiroTorneo(torneo, usuario);
        if (!validacion.esValido()) {
            return validacion;
        }

        // Obtener el participante para recuperar el monto pagado
        ParticipanteTorneo participante = torneo.obtenerParticipante(usuario.getId());
        double montoAPagar = participante.getMontoInscripcion();

        // Remover del torneo
        torneo.removerParticipante(usuario.getId());

        // Si pagó entrada, ajustar premio total
        if (participante.pagoPorEntrada()) {
            torneo.setPremioTotal(Math.max(0, torneo.getPremioTotal() - montoAPagar));
        }

        return new ResultadoValidacion(true,
                usuario.getNombre() + " retirado del torneo exitosamente");
    }

    /**
     * Finaliza un torneo y otorga premios al ganador.
     */
    public ResultadoValidacion finalizarTorneo(int idTorneo, int idGanador) {
        Torneo torneo = obtenerTorneoPorId(idTorneo);
        if (torneo == null) {
            return new ResultadoValidacion(false, "Torneo no encontrado");
        }

        // Validar
        ResultadoValidacion validacion = validador.validarFinalizacionTorneo(torneo);
        if (!validacion.esValido()) {
            return validacion;
        }

        // Obtener ganador
        ParticipanteTorneo ganador = torneo.obtenerParticipante(idGanador);
        if (ganador == null) {
            return new ResultadoValidacion(false, "El ganador no está registrado en este torneo");
        }

        // Calcular premio/descuento
        double premioODescuento = calcularPremioParaGanador(torneo);
        ganador.setGano(true);
        ganador.setPremioODescuento(premioODescuento);

        // Si es torneo amistoso, crear voucher
        if (torneo.getTipo() == TipoTorneo.AMISTOSO) {
            VoucherDescuento voucher = new VoucherDescuento(
                    proximoIdVoucher++,
                    ganador.getIdUsuario(),
                    torneo.getId(),
                    torneo.getNombre(),
                    premioODescuento,
                    30 // 30 días de validez
            );
            vouchers.add(voucher);
        }

        // Finalizar torneo
        torneo.setEstado(EstadoTorneo.FINALIZADO);
        torneo.setFechaFin(LocalDate.now());

        return new ResultadoValidacion(true,
                "Torneo finalizado. " + ganador.getNombreUsuario() + " ganó " +
                (torneo.getTipo() == TipoTorneo.AMISTOSO ? "voucher de $" : "premio de $") +
                premioODescuento);
    }

    /**
     * Calcula el premio o descuento para el ganador de un torneo.
     */
    private double calcularPremioParaGanador(Torneo torneo) {
        if (torneo.getTipo() == TipoTorneo.COMPETITIVO) {
            // 80% del dinero recaudado para el ganador
            return torneo.getPremioTotal() * 0.80;
        } else {
            // Para torneos amistosos, calcular descuento basado en participantes
            // Ejemplo: $100 por participante adicional
            int bonusParticipantes = Math.max(0, torneo.getCantidadParticipantes() - 1);
            return 100 * bonusParticipantes;
        }
    }

    /**
     * Obtiene todos los torneos del sistema.
     */
    public List<Torneo> obtenerTodosTorneos() {
        return new ArrayList<>(torneos);
    }

    /**
     * Obtiene los torneos abiertos para registro.
     */
    public List<Torneo> obtenerTorneosAbiertos() {
        return torneos.stream()
                .filter(t -> t.getEstado() == EstadoTorneo.ABIERTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los torneos disponibles para un día específico.
     */
    public List<Torneo> obtenerTorneosPorDia(int diaSemana) {
        return torneos.stream()
                .filter(t -> t.getDiaSemana() == diaSemana)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un torneo por su ID.
     */
    public Torneo obtenerTorneoPorId(int id) {
        return torneos.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtiene los torneos en los que está registrado un usuario.
     */
    public List<Torneo> obtenerTorneosDelUsuario(int idUsuario) {
        return torneos.stream()
                .filter(t -> t.obtenerParticipante(idUsuario) != null)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los vouchers de descuento de un usuario.
     */
    public List<VoucherDescuento> obtenerVouchersDelUsuario(int idUsuario) {
        return vouchers.stream()
                .filter(v -> v.getIdUsuario() == idUsuario && v.esValido())
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un voucher específico por ID.
     */
    public VoucherDescuento obtenerVoucherPorId(int id) {
        return vouchers.stream()
                .filter(v -> v.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Usa un voucher de descuento.
     */
    public ResultadoValidacion usarVoucher(int idVoucher) {
        VoucherDescuento voucher = obtenerVoucherPorId(idVoucher);
        if (voucher == null) {
            return new ResultadoValidacion(false, "Voucher no encontrado");
        }

        if (!voucher.esValido()) {
            return new ResultadoValidacion(false, "El voucher no es válido o ya está usado");
        }

        voucher.marcarComoUsado();
        return new ResultadoValidacion(true,
                "Voucher utilizado exitosamente. Descuento: $" + voucher.getMontoDescuento());
    }

    /**
     * Obtiene todas las copias del juego para verificar disponibilidad.
     */
    public int verificarDisponibilidadCopias(JuegoDeMesa juego, int cantidadRequerida,
                                             List<CopiaJuego> todasLasCopias) {
        return (int) todasLasCopias.stream()
                .filter(c -> c.getIdJuego() == juego.getId() &&
                           c.getEstado() == EstadoJuego.DISPONIBLE)
                .count();
    }

    // Métodos de gestión de lista

    public void setTorneos(List<Torneo> torneos) {
        this.torneos = torneos;
        if (!torneos.isEmpty()) {
            proximoIdTorneo = torneos.stream()
                    .mapToInt(Torneo::getId)
                    .max()
                    .orElse(0) + 1;
        }
    }

    public void setVouchers(List<VoucherDescuento> vouchers) {
        this.vouchers = vouchers;
        if (!vouchers.isEmpty()) {
            proximoIdVoucher = vouchers.stream()
                    .mapToInt(VoucherDescuento::getId)
                    .max()
                    .orElse(0) + 1;
        }
    }

    public int getCantidadTorneos() {
        return torneos.size();
    }

    public int getCantidadVouchers() {
        return vouchers.size();
    }
}
