package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import modelo.*;
import service.validadores.ValidadorTorneo;
import java.util.ArrayList;
import java.util.List;

/**
 * Test unitarios para la clase ValidadorTorneo.
 * Verifica la validación de todas las reglas de negocio relacionadas con torneos.
 */
@DisplayName("Pruebas unitarios del Validador de Torneos")
public class ValidadorTorneoTest {

    private ValidadorTorneo validador;
    private JuegoDeMesa juego;
    private Cliente cliente;
    private Mesero mesero;
    private Administrador admin;

    @BeforeEach
    void setUp() {
        validador = new ValidadorTorneo();
        juego = new JuegoDeMesa("1", "Ajedrez", CategoriaJuego.ESTRATEGIA, 
                2, 100.0, RestriccionEdad.MAYORES_18, 2);
        cliente = new Cliente("c1", "Cliente1", "c@email.com", "password");
        mesero = new Mesero("m1", "Mesero1", "m@email.com", "password", "Turno1");
        admin = new Administrador("a1", "Admin1", "a@email.com", "password");
    }

    @Test
    @DisplayName("Validar creación de torneo con parámetros válidos")
    void testValidarCreacionTorneoValido() {
        ResultadoValidacion resultado = validador.validarCreacionTorneo(
                "Torneo Válido",
                juego,
                TipoTorneo.AMISTOSO,
                10,
                1,
                0
        );
        
        assertTrue(resultado.esValido());
    }

    @Test
    @DisplayName("Rechazar torneo sin nombre")
    void testRechazarTorneoSinNombre() {
        ResultadoValidacion resultado = validador.validarCreacionTorneo(
                "",
                juego,
                TipoTorneo.AMISTOSO,
                10,
                1,
                0
        );
        
        assertFalse(resultado.esValido());
    }

    @Test
    @DisplayName("Rechazar torneo con juego nulo")
    void testRechazarTorneoJuegoNulo() {
        ResultadoValidacion resultado = validador.validarCreacionTorneo(
                "Torneo",
                null,
                TipoTorneo.AMISTOSO,
                10,
                1,
                0
        );
        
        assertFalse(resultado.esValido());
    }

    @Test
    @DisplayName("Rechazar torneo con menos de 2 participantes")
    void testRechazarTorneoMenosDe2Participantes() {
        ResultadoValidacion resultado = validador.validarCreacionTorneo(
                "Torneo",
                juego,
                TipoTorneo.AMISTOSO,
                1,
                1,
                0
        );
        
        assertFalse(resultado.esValido());
    }

    @Test
    @DisplayName("Rechazar día de semana inválido")
    void testRechazarDiaSemanInvalido() {
        ResultadoValidacion resultado = validador.validarCreacionTorneo(
                "Torneo",
                juego,
                TipoTorneo.AMISTOSO,
                10,
                8,  // Día inválido
                0
        );
        
        assertFalse(resultado.esValido());
    }

    @Test
    @DisplayName("Rechazar torneo competitivo sin cuota de entrada")
    void testRechazarTorneoCompetitivoSinCuota() {
        ResultadoValidacion resultado = validador.validarCreacionTorneo(
                "Torneo",
                juego,
                TipoTorneo.COMPETITIVO,
                10,
                1,
                0  // Sin cuota
        );
        
        assertFalse(resultado.esValido());
    }

    @Test
    @DisplayName("Rechazar torneo amistoso con cuota")
    void testRechazarTorneoAmistosoConCuota() {
        ResultadoValidacion resultado = validador.validarCreacionTorneo(
                "Torneo",
                juego,
                TipoTorneo.AMISTOSO,
                10,
                1,
                100  // Con cuota
        );
        
        assertFalse(resultado.esValido());
    }

    @Test
    @DisplayName("Validar registro de usuario a torneo")
    void testValidarRegistroTorneoValido() {
        Torneo torneo = new Torneo(1, "Torneo", 1, "Ajedrez", 
                TipoTorneo.AMISTOSO, 10, 1, 1, "Admin", 0);
        
        ResultadoValidacion resultado = validador.validarRegistroTorneo(
                torneo, cliente, new ArrayList<>()
        );
        
        assertTrue(resultado.esValido());
    }

    @Test
    @DisplayName("Rechazar registro en torneo cerrado")
    void testRechazarRegistroTorneoCerrado() {
        Torneo torneo = new Torneo(1, "Torneo", 1, "Ajedrez", 
                TipoTorneo.AMISTOSO, 10, 1, 1, "Admin", 0);
        torneo.setEstado(EstadoTorneo.FINALIZADO);
        
        ResultadoValidacion resultado = validador.validarRegistroTorneo(
                torneo, cliente, new ArrayList<>()
        );
        
        assertFalse(resultado.esValido());
    }

    @Test
    @DisplayName("Rechazar registro cuando torneo está lleno")
    void testRechazarRegistroTorneoLleno() {
        Torneo torneo = new Torneo(1, "Torneo", 1, "Ajedrez", 
                TipoTorneo.AMISTOSO, 2, 1, 1, "Admin", 0);
        
        // Llenar el torneo
        torneo.agregarParticipante(new ParticipanteTorneo(1, "User1", false));
        torneo.agregarParticipante(new ParticipanteTorneo(2, "User2", false));
        
        ResultadoValidacion resultado = validador.validarRegistroTorneo(
                torneo, cliente, new ArrayList<>()
        );
        
        assertFalse(resultado.esValido());
    }

    @Test
    @DisplayName("Rechazar registro de usuario ya inscrito")
    void testRechazarRegistroDuplicado() {
        Torneo torneo = new Torneo(1, "Torneo", 1, "Ajedrez", 
                TipoTorneo.AMISTOSO, 10, 1, 1, "Admin", 0);
        
        // Agregar cliente al torneo
        torneo.agregarParticipante(new ParticipanteTorneo(cliente.getId(), cliente.getNombre(), false));
        
        ResultadoValidacion resultado = validador.validarRegistroTorneo(
                torneo, cliente, new ArrayList<>()
        );
        
        assertFalse(resultado.esValido());
    }

    @Test
    @DisplayName("Validar retiro de torneo")
    void testValidarRetiroTorneoValido() {
        Torneo torneo = new Torneo(1, "Torneo", 1, "Ajedrez", 
                TipoTorneo.AMISTOSO, 10, 1, 1, "Admin", 0);
        torneo.agregarParticipante(new ParticipanteTorneo(cliente.getId(), cliente.getNombre(), false));
        
        ResultadoValidacion resultado = validador.validarRetiroTorneo(torneo, cliente);
        
        assertTrue(resultado.esValido());
    }

    @Test
    @DisplayName("Rechazar retiro cuando usuario no está inscrito")
    void testRechazarRetiroNoInscrito() {
        Torneo torneo = new Torneo(1, "Torneo", 1, "Ajedrez", 
                TipoTorneo.AMISTOSO, 10, 1, 1, "Admin", 0);
        
        ResultadoValidacion resultado = validador.validarRetiroTorneo(torneo, cliente);
        
        assertFalse(resultado.esValido());
    }

    @Test
    @DisplayName("Rechazar retiro de torneo en progreso")
    void testRechazarRetiroTorneoEnProgreso() {
        Torneo torneo = new Torneo(1, "Torneo", 1, "Ajedrez", 
                TipoTorneo.AMISTOSO, 10, 1, 1, "Admin", 0);
        torneo.agregarParticipante(new ParticipanteTorneo(cliente.getId(), cliente.getNombre(), false));
        torneo.setEstado(EstadoTorneo.EN_PROGRESO);
        
        ResultadoValidacion resultado = validador.validarRetiroTorneo(torneo, cliente);
        
        assertFalse(resultado.esValido());
    }

    @Test
    @DisplayName("Validar finalización de torneo")
    void testValidarFinalizacionTorneoValido() {
        Torneo torneo = new Torneo(1, "Torneo", 1, "Ajedrez", 
                TipoTorneo.AMISTOSO, 10, 1, 1, "Admin", 0);
        torneo.agregarParticipante(new ParticipanteTorneo(1, "User1", false));
        torneo.agregarParticipante(new ParticipanteTorneo(2, "User2", false));
        
        ResultadoValidacion resultado = validador.validarFinalizacionTorneo(torneo);
        
        assertTrue(resultado.esValido());
    }

    @Test
    @DisplayName("Rechazar finalización con menos de 2 participantes")
    void testRechazarFinalizacionMenosDe2() {
        Torneo torneo = new Torneo(1, "Torneo", 1, "Ajedrez", 
                TipoTorneo.AMISTOSO, 10, 1, 1, "Admin", 0);
        torneo.agregarParticipante(new ParticipanteTorneo(1, "User1", false));
        
        ResultadoValidacion resultado = validador.validarFinalizacionTorneo(torneo);
        
        assertFalse(resultado.esValido());
    }

    @Test
    @DisplayName("Rechazar finalización de torneo ya finalizado")
    void testRechazarFinalizacionYaFinalizado() {
        Torneo torneo = new Torneo(1, "Torneo", 1, "Ajedrez", 
                TipoTorneo.AMISTOSO, 10, 1, 1, "Admin", 0);
        torneo.setEstado(EstadoTorneo.FINALIZADO);
        
        ResultadoValidacion resultado = validador.validarFinalizacionTorneo(torneo);
        
        assertFalse(resultado.esValido());
    }

    @Test
    @DisplayName("Obtener nombre del día de semana")
    void testObtenerNombreDiaSemana() {
        assertEquals("Lunes", ValidadorTorneo.obtenerNombreDiaSemana(1));
        assertEquals("Martes", ValidadorTorneo.obtenerNombreDiaSemana(2));
        assertEquals("Miércoles", ValidadorTorneo.obtenerNombreDiaSemana(3));
        assertEquals("Jueves", ValidadorTorneo.obtenerNombreDiaSemana(4));
        assertEquals("Viernes", ValidadorTorneo.obtenerNombreDiaSemana(5));
        assertEquals("Sábado", ValidadorTorneo.obtenerNombreDiaSemana(6));
        assertEquals("Domingo", ValidadorTorneo.obtenerNombreDiaSemana(7));
        assertEquals("Día inválido", ValidadorTorneo.obtenerNombreDiaSemana(8));
    }
}
