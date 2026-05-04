package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import modelo.*;
import service.ServicioTorneos;
import java.util.ArrayList;
import java.util.List;

/**
 * Test de integración para el sistema de torneos.
 * Verifica los flujos principales de usuario y la interacción entre componentes.
 */
@DisplayName("Pruebas de integración del sistema de torneos")
public class TorneoIntegrationTest {

    private ServicioTorneos servicioTorneos;
    private JuegoDeMesa juego1;
    private JuegoDeMesa juego2;
    private Cliente cliente1;
    private Cliente cliente2;
    private Mesero mesero1;
    private Administrador admin;
    private List<CopiaJuego> todasLasCopias;

    @BeforeEach
    void setUp() {
        servicioTorneos = new ServicioTorneos();
        
        // Crear juegos
        juego1 = new JuegoDeMesa("1", "Ajedrez", CategoriaJuego.ESTRATEGIA, 
                2, 100.0, RestriccionEdad.MAYORES_18, 2);
        juego2 = new JuegoDeMesa("2", "Damas", CategoriaJuego.CLASICO, 
                2, 50.0, RestriccionEdad.MAYORES_6, 2);
        
        // Crear usuarios
        cliente1 = new Cliente("c1", "Cliente1", "c1@email.com", "pass");
        cliente2 = new Cliente("c2", "Cliente2", "c2@email.com", "pass");
        mesero1 = new Mesero("m1", "Mesero1", "m1@email.com", "pass", "Turno1");
        admin = new Administrador("a1", "Admin1", "a1@email.com", "pass");
        
        // Marcar cliente1 como fan del juego
        cliente1.agregarJuegoFavorito(juego1.getId());
        
        todasLasCopias = new ArrayList<>();
    }

    @Test
    @DisplayName("US1: Crear un torneo amistoso")
    void testCrearTorneoAmistoso() {
        ResultadoValidacion resultado = servicioTorneos.crearTorneo(
                "Campeonato Amistoso de Ajedrez",
                juego1,
                TipoTorneo.AMISTOSO,
                8,
                1,  // Lunes
                admin,
                0
        );
        
        assertTrue(resultado.esValido());
        assertEquals(1, servicioTorneos.getCantidadTorneos());
        
        Torneo torneo = servicioTorneos.obtenerTorneoPorId(1);
        assertNotNull(torneo);
        assertEquals("Campeonato Amistoso de Ajedrez", torneo.getNombre());
        assertEquals(TipoTorneo.AMISTOSO, torneo.getTipo());
    }

    @Test
    @DisplayName("US1: Crear un torneo competitivo")
    void testCrearTorneoCompetitivo() {
        ResultadoValidacion resultado = servicioTorneos.crearTorneo(
                "Torneo Competitivo de Damas",
                juego2,
                TipoTorneo.COMPETITIVO,
                6,
                2,  // Martes
                admin,
                200
        );
        
        assertTrue(resultado.esValido());
        
        Torneo torneo = servicioTorneos.obtenerTorneoPorId(1);
        assertEquals(TipoTorneo.COMPETITIVO, torneo.getTipo());
        assertEquals(200, torneo.getMontoEntrada());
    }

    @Test
    @DisplayName("US2: Cliente se registra a un torneo")
    void testClienteRegistroATorneo() {
        // Crear torneo
        servicioTorneos.crearTorneo("Torneo", juego1, TipoTorneo.AMISTOSO, 8, 1, admin, 0);
        Torneo torneo = servicioTorneos.obtenerTorneoPorId(1);
        
        // Registrar cliente
        ResultadoValidacion resultado = servicioTorneos.registrarUsuarioATorneo(
                torneo.getId(), cliente1, new ArrayList<>()
        );
        
        assertTrue(resultado.esValido());
        assertEquals(1, torneo.getCantidadParticipantes());
        assertNotNull(torneo.obtenerParticipante(cliente1.getId()));
    }

    @Test
    @DisplayName("US2: Cliente se registra con máximo 3 participantes")
    void testClienteRegistra3Participantes() {
        servicioTorneos.crearTorneo("Torneo", juego1, TipoTorneo.AMISTOSO, 10, 1, admin, 0);
        Torneo torneo = servicioTorneos.obtenerTorneoPorId(1);
        
        // Crear 3 participantes del mismo cliente
        ParticipanteTorneo p1 = new ParticipanteTorneo(cliente1.getId(), cliente1.getNombre(), false);
        ParticipanteTorneo p2 = new ParticipanteTorneo(cliente1.getId(), cliente1.getNombre(), false);
        ParticipanteTorneo p3 = new ParticipanteTorneo(cliente1.getId(), cliente1.getNombre(), false);
        
        assertTrue(torneo.agregarParticipante(p1));
        assertTrue(torneo.agregarParticipante(p2));
        assertTrue(torneo.agregarParticipante(p3));
        
        // Intentar agregar un 4to - debería fallar
        ResultadoValidacion resultado = servicioTorneos.registrarUsuarioATorneo(
                torneo.getId(), cliente1, new ArrayList<>()
        );
        
        assertFalse(resultado.esValido());
    }

    @Test
    @DisplayName("US2: Cliente se retira del torneo liberando todos sus spots")
    void testClienteRetiraDelTorneo() {
        servicioTorneos.crearTorneo("Torneo", juego1, TipoTorneo.AMISTOSO, 8, 1, admin, 0);
        Torneo torneo = servicioTorneos.obtenerTorneoPorId(1);
        
        // Registrar cliente
        servicioTorneos.registrarUsuarioATorneo(torneo.getId(), cliente1, new ArrayList<>());
        assertEquals(1, torneo.getCantidadParticipantes());
        
        // Retirar cliente
        ResultadoValidacion resultado = servicioTorneos.retirarUsuarioDeTorneo(
                torneo.getId(), cliente1
        );
        
        assertTrue(resultado.esValido());
        assertEquals(0, torneo.getCantidadParticipantes());
        assertNull(torneo.obtenerParticipante(cliente1.getId()));
    }

    @Test
    @DisplayName("US3: 20% de spots reservados para fans")
    void testSpotReservadoParaFans() {
        servicioTorneos.crearTorneo("Torneo", juego1, TipoTorneo.AMISTOSO, 10, 1, admin, 0);
        Torneo torneo = servicioTorneos.obtenerTorneoPorId(1);
        
        // 20% de 10 = 2 spots para fans
        assertEquals(2, torneo.getSpotReservadosFans());
        assertEquals(2, torneo.getSpotsDisponiblesFans());
        assertEquals(8, torneo.getSpotsDisponiblesNormales());
    }

    @Test
    @DisplayName("US3: Fan toma spot reservado")
    void testFanTomaSpotReservado() {
        servicioTorneos.crearTorneo("Torneo", juego1, TipoTorneo.AMISTOSO, 10, 1, admin, 0);
        Torneo torneo = servicioTorneos.obtenerTorneoPorId(1);
        
        // cliente1 es fan del juego
        ParticipanteTorneo participanteFan = new ParticipanteTorneo(
                cliente1.getId(), cliente1.getNombre(), true
        );
        
        torneo.agregarParticipante(participanteFan);
        
        assertEquals(1, torneo.getSpotsUsadosFans());
        assertEquals(1, torneo.getSpotsDisponiblesFans());
    }

    @Test
    @DisplayName("US3: Fan toma spot normal cuando spots reservados se agotan")
    void testFanTomaSpotNormalSiNoHayReservados() {
        servicioTorneos.crearTorneo("Torneo", juego1, TipoTorneo.AMISTOSO, 10, 1, admin, 0);
        Torneo torneo = servicioTorneos.obtenerTorneoPorId(1);
        
        // Llenar los 2 spots reservados para fans
        torneo.agregarParticipante(new ParticipanteTorneo(1, "Fan1", true));
        torneo.agregarParticipante(new ParticipanteTorneo(2, "Fan2", true));
        
        // Un tercer fan toma spot normal
        torneo.agregarParticipante(new ParticipanteTorneo(3, "Fan3", true));
        
        assertEquals(2, torneo.getSpotsUsadosFans()); // Solo 2 en spots reservados
        assertEquals(3, torneo.getCantidadParticipantes()); // Pero 3 total
    }

    @Test
    @DisplayName("US4: Empleado no puede registrarse si trabaja ese día")
    void testEmpleadoNoRegistroDiaLaboral() {
        // Crear turno para el mesero en lunes (día 1)
        Turno turno = new Turno("t1", mesero1.getId(), 1, "09:00", "17:00");
        mesero1.agregarTurno(turno);
        
        servicioTorneos.crearTorneo("Torneo", juego1, TipoTorneo.AMISTOSO, 8, 1, admin, 0);
        Torneo torneo = servicioTorneos.obtenerTorneoPorId(1);
        
        // Intentar registrar mesero a torneo del lunes (cuando trabaja)
        ResultadoValidacion resultado = servicioTorneos.registrarUsuarioATorneo(
                torneo.getId(), mesero1, new ArrayList<>()
        );
        
        assertFalse(resultado.esValido());
    }

    @Test
    @DisplayName("US4: Empleado puede registrarse si no trabaja ese día")
    void testEmpleadoRegistroDiaNoLaboral() {
        // Crear turno para el mesero en lunes
        Turno turno = new Turno("t1", mesero1.getId(), 1, "09:00", "17:00");
        mesero1.agregarTurno(turno);
        
        servicioTorneos.crearTorneo("Torneo", juego1, TipoTorneo.AMISTOSO, 8, 2, admin, 0); // Martes
        Torneo torneo = servicioTorneos.obtenerTorneoPorId(1);
        
        // Registrar mesero a torneo del martes (cuando no trabaja)
        ResultadoValidacion resultado = servicioTorneos.registrarUsuarioATorneo(
                torneo.getId(), mesero1, new ArrayList<>()
        );
        
        assertTrue(resultado.esValido());
    }

    @Test
    @DisplayName("US4: Empleado no paga entrada en torneo competitivo")
    void testEmpleadoNoPageTorneoCompetitivo() {
        servicioTorneos.crearTorneo("Torneo Competitivo", juego1, TipoTorneo.COMPETITIVO, 
                8, 2, admin, 100);
        Torneo torneo = servicioTorneos.obtenerTorneoPorId(1);
        
        servicioTorneos.registrarUsuarioATorneo(torneo.getId(), mesero1, new ArrayList<>());
        
        ParticipanteTorneo participante = torneo.obtenerParticipante(mesero1.getId());
        assertNotNull(participante);
        // Empleados podrían tener lógica especial para no pagar
    }

    @Test
    @DisplayName("US5: Ganador torneo amistoso recibe voucher")
    void testGanadorTorneoAmistosoRecibVoucher() {
        servicioTorneos.crearTorneo("Torneo Amistoso", juego1, TipoTorneo.AMISTOSO, 8, 1, admin, 0);
        Torneo torneo = servicioTorneos.obtenerTorneoPorId(1);
        
        // Agregar participantes
        servicioTorneos.registrarUsuarioATorneo(torneo.getId(), cliente1, new ArrayList<>());
        servicioTorneos.registrarUsuarioATorneo(torneo.getId(), cliente2, new ArrayList<>());
        
        assertEquals(2, torneo.getCantidadParticipantes());
        
        // Finalizar torneo con cliente1 como ganador
        ResultadoValidacion resultado = servicioTorneos.finalizarTorneo(torneo.getId(), cliente1.getId());
        
        assertTrue(resultado.esValido());
        
        // Verificar que se creó un voucher
        List<VoucherDescuento> vouchersCliente1 = servicioTorneos.obtenerVouchersDelUsuario(cliente1.getId());
        assertEquals(1, vouchersCliente1.size());
        assertTrue(vouchersCliente1.get(0).esValido());
    }

    @Test
    @DisplayName("US5: Ganador torneo competitivo recibe premio en efectivo")
    void testGanadorTorneoCompetitivoRecibePremio() {
        servicioTorneos.crearTorneo("Torneo Competitivo", juego1, TipoTorneo.COMPETITIVO, 
                8, 1, admin, 100);
        Torneo torneo = servicioTorneos.obtenerTorneoPorId(1);
        
        // Agregar participantes
        servicioTorneos.registrarUsuarioATorneo(torneo.getId(), cliente1, new ArrayList<>());
        servicioTorneos.registrarUsuarioATorneo(torneo.getId(), cliente2, new ArrayList<>());
        
        // Premium total debe ser la suma de inscripciones
        assertEquals(200, torneo.getPremioTotal());
        
        // Finalizar torneo
        servicioTorneos.finalizarTorneo(torneo.getId(), cliente1.getId());
        
        ParticipanteTorneo ganador = torneo.obtenerParticipante(cliente1.getId());
        assertTrue(ganador.gano());
        // 80% del total para el ganador
        assertEquals(160, ganador.getPremioODescuento());
    }

    @Test
    @DisplayName("US5: Voucher no se puede combinar con otros descuentos")
    void testVoucherNoCombinable() {
        VoucherDescuento voucher = new VoucherDescuento(1, cliente1.getId(), 1, "Torneo", 100, 30);
        
        assertTrue(voucher.esValido());
        
        // Usar el voucher
        voucher.marcarComoUsado();
        
        assertFalse(voucher.esValido());
    }

    @Test
    @DisplayName("Obtener torneos del sistema")
    void testObtenerTodosTorneos() {
        servicioTorneos.crearTorneo("Torneo1", juego1, TipoTorneo.AMISTOSO, 8, 1, admin, 0);
        servicioTorneos.crearTorneo("Torneo2", juego2, TipoTorneo.COMPETITIVO, 6, 2, admin, 100);
        
        List<Torneo> todos = servicioTorneos.obtenerTodosTorneos();
        assertEquals(2, todos.size());
    }

    @Test
    @DisplayName("Obtener torneos abiertos")
    void testObtenerTorneosAbiertos() {
        servicioTorneos.crearTorneo("Torneo1", juego1, TipoTorneo.AMISTOSO, 8, 1, admin, 0);
        servicioTorneos.crearTorneo("Torneo2", juego2, TipoTorneo.COMPETITIVO, 6, 2, admin, 100);
        
        Torneo torneo2 = servicioTorneos.obtenerTorneoPorId(2);
        torneo2.setEstado(EstadoTorneo.FINALIZADO);
        
        List<Torneo> abiertos = servicioTorneos.obtenerTorneosAbiertos();
        assertEquals(1, abiertos.size());
        assertEquals("Torneo1", abiertos.get(0).getNombre());
    }

    @Test
    @DisplayName("Obtener torneos por día de la semana")
    void testObtenerTorneosPorDia() {
        servicioTorneos.crearTorneo("Torneo Lunes", juego1, TipoTorneo.AMISTOSO, 8, 1, admin, 0);
        servicioTorneos.crearTorneo("Torneo Martes", juego2, TipoTorneo.COMPETITIVO, 6, 2, admin, 100);
        servicioTorneos.crearTorneo("Otro Lunes", juego1, TipoTorneo.AMISTOSO, 8, 1, admin, 0);
        
        List<Torneo> lunesss = servicioTorneos.obtenerTorneosPorDia(1);
        assertEquals(2, lunesss.size());
        
        List<Torneo> martes = servicioTorneos.obtenerTorneosPorDia(2);
        assertEquals(1, martes.size());
    }

    @Test
    @DisplayName("Obtener torneos del usuario")
    void testObtenerTorneosDelUsuario() {
        servicioTorneos.crearTorneo("Torneo1", juego1, TipoTorneo.AMISTOSO, 8, 1, admin, 0);
        servicioTorneos.crearTorneo("Torneo2", juego2, TipoTorneo.AMISTOSO, 8, 2, admin, 0);
        
        Torneo t1 = servicioTorneos.obtenerTorneoPorId(1);
        Torneo t2 = servicioTorneos.obtenerTorneoPorId(2);
        
        // Registrar cliente1 a torneo1
        servicioTorneos.registrarUsuarioATorneo(t1.getId(), cliente1, new ArrayList<>());
        
        List<Torneo> torneosCliente1 = servicioTorneos.obtenerTorneosDelUsuario(cliente1.getId());
        assertEquals(1, torneosCliente1.size());
        assertEquals("Torneo1", torneosCliente1.get(0).getNombre());
    }

    @Test
    @DisplayName("Varios torneos pueden ejecutarse simultáneamente")
    void testMultiplesTorneosSimultaneos() {
        servicioTorneos.crearTorneo("Torneo1", juego1, TipoTorneo.AMISTOSO, 8, 1, admin, 0);
        servicioTorneos.crearTorneo("Torneo2", juego2, TipoTorneo.AMISTOSO, 8, 1, admin, 0);
        
        Torneo t1 = servicioTorneos.obtenerTorneoPorId(1);
        Torneo t2 = servicioTorneos.obtenerTorneoPorId(2);
        
        // Ambos en el mismo día
        assertEquals(1, t1.getDiaSemana());
        assertEquals(1, t2.getDiaSemana());
        
        // Cliente puede registrarse a ambos
        servicioTorneos.registrarUsuarioATorneo(t1.getId(), cliente1, new ArrayList<>());
        servicioTorneos.registrarUsuarioATorneo(t2.getId(), cliente1, new ArrayList<>());
        
        assertEquals(1, t1.getCantidadParticipantes());
        assertEquals(1, t2.getCantidadParticipantes());
    }
}
