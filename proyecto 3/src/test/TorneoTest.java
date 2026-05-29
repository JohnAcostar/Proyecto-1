package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import modelo.*;

/**
 * Test unitarios para la clase Torneo.
 * Verifica la lógica de creación, gestión de participantes y cálculo de spots.
 */
@DisplayName("Pruebas unitarias de la clase Torneo")
public class TorneoTest {

    private Torneo torneo;
    private ParticipanteTorneo participante1;
    private ParticipanteTorneo participante2;

    @BeforeEach
    void setUp() {
        torneo = new Torneo(
                1,
                "Torneo de Ajedrez",
                1,
                "Ajedrez",
                TipoTorneo.AMISTOSO,
                10,
                1,
                1,
                "Admin1",
                0
        );
        
        participante1 = new ParticipanteTorneo(1, "Usuario1", false);
        participante2 = new ParticipanteTorneo(2, "Usuario2", true);
    }

    @Test
    @DisplayName("Crear torneo con parámetros válidos")
    void testCrearTorneoValido() {
        assertNotNull(torneo);
        assertEquals("Torneo de Ajedrez", torneo.getNombre());
        assertEquals(1, torneo.getIdJuego());
        assertEquals(TipoTorneo.AMISTOSO, torneo.getTipo());
        assertEquals(EstadoTorneo.ABIERTO, torneo.getEstado());
        assertEquals(10, torneo.getCantidadMaximaParticipantes());
    }

    @Test
    @DisplayName("Calcular spots reservados para fans (20% redondeado hacia arriba)")
    void testCalculoSpotsReservadosFans() {
        // 20% de 10 = 2
        assertEquals(2, torneo.getSpotReservadosFans());
        
        // Crear torneo con 15 participantes: 20% = 3
        Torneo torneo2 = new Torneo(2, "Torneo2", 1, "Ajedrez", TipoTorneo.AMISTOSO, 15, 1, 1, "Admin", 0);
        assertEquals(3, torneo2.getSpotReservadosFans());
        
        // Crear torneo con 5 participantes: 20% = 1 (redondeado hacia arriba)
        Torneo torneo3 = new Torneo(3, "Torneo3", 1, "Ajedrez", TipoTorneo.AMISTOSO, 5, 1, 1, "Admin", 0);
        assertEquals(1, torneo3.getSpotReservadosFans());
    }

    @Test
    @DisplayName("Agregar participante al torneo")
    void testAgregarParticipante() {
        assertTrue(torneo.agregarParticipante(participante1));
        assertEquals(1, torneo.getCantidadParticipantes());
        assertNotNull(torneo.obtenerParticipante(1));
    }

    @Test
    @DisplayName("Verificar que hay spot disponible")
    void testHaySpotDisponible() {
        assertTrue(torneo.haySpotDisponible());
        
        // Llenar el torneo
        for (int i = 0; i < 10; i++) {
            ParticipanteTorneo p = new ParticipanteTorneo(i + 1, "Usuario" + i, false);
            torneo.agregarParticipante(p);
        }
        
        assertFalse(torneo.haySpotDisponible());
    }

    @Test
    @DisplayName("Calcular spots disponibles para participantes normales y fans")
    void testCalculoSpotsDisponibles() {
        // Inicialmente: 10 spots totales, 2 reservados para fans, 8 para normales
        assertEquals(8, torneo.getSpotsDisponiblesNormales());
        assertEquals(2, torneo.getSpotsDisponiblesFans());
        
        // Agregar un participante normal
        torneo.agregarParticipante(participante1);
        assertEquals(7, torneo.getSpotsDisponiblesNormales());
        assertEquals(2, torneo.getSpotsDisponiblesFans());
        
        // Agregar un fan
        torneo.agregarParticipante(participante2);
        assertEquals(7, torneo.getSpotsDisponiblesNormales());
        assertEquals(1, torneo.getSpotsDisponiblesFans());
    }

    @Test
    @DisplayName("No agregar participante cuando el torneo está lleno")
    void testNoAgregarParticipanteLleno() {
        // Llenar el torneo
        for (int i = 0; i < 10; i++) {
            ParticipanteTorneo p = new ParticipanteTorneo(i + 1, "Usuario" + i, false);
            torneo.agregarParticipante(p);
        }
        
        ParticipanteTorneo extra = new ParticipanteTorneo(11, "UsuarioExtra", false);
        assertFalse(torneo.agregarParticipante(extra));
    }

    @Test
    @DisplayName("Remover participante del torneo")
    void testRemoverParticipante() {
        torneo.agregarParticipante(participante1);
        torneo.agregarParticipante(participante2);
        assertEquals(2, torneo.getCantidadParticipantes());
        
        assertTrue(torneo.removerParticipante(1));
        assertEquals(1, torneo.getCantidadParticipantes());
        assertNull(torneo.obtenerParticipante(1));
    }

    @Test
    @DisplayName("No remover participante que no existe")
    void testRemoverParticipanteNoExistente() {
        torneo.agregarParticipante(participante1);
        
        assertFalse(torneo.removerParticipante(99));
        assertEquals(1, torneo.getCantidadParticipantes());
    }

    @Test
    @DisplayName("Contar participantes del mismo usuario")
    void testContarParticipantesDelUsuario() {
        ParticipanteTorneo p1 = new ParticipanteTorneo(1, "Usuario1", false);
        ParticipanteTorneo p2 = new ParticipanteTorneo(1, "Usuario1", false);
        ParticipanteTorneo p3 = new ParticipanteTorneo(1, "Usuario1", false);
        
        torneo.agregarParticipante(p1);
        torneo.agregarParticipante(p2);
        torneo.agregarParticipante(p3);
        
        assertEquals(3, torneo.contarParticipantesDelUsuario(1));
        assertEquals(0, torneo.contarParticipantesDelUsuario(2));
    }

    @Test
    @DisplayName("Agregar premio al torneo competitivo")
    void testAgregarPremio() {
        Torneo torneoCompetitivo = new Torneo(2, "Torneo Competitivo", 1, "Ajedrez",
                TipoTorneo.COMPETITIVO, 8, 2, 1, "Admin", 100);
        
        assertEquals(0, torneoCompetitivo.getPremioTotal());
        
        torneoCompetitivo.agregarPremio(100);
        assertEquals(100, torneoCompetitivo.getPremioTotal());
        
        torneoCompetitivo.agregarPremio(100);
        assertEquals(200, torneoCompetitivo.getPremioTotal());
    }

    @Test
    @DisplayName("Torneo amistoso no requiere cuota de entrada")
    void testTorneoAmistosoSinCuota() {
        assertEquals(0, torneo.getMontoEntrada());
        assertEquals(TipoTorneo.AMISTOSO, torneo.getTipo());
    }

    @Test
    @DisplayName("Torneo competitivo requiere cuota de entrada")
    void testTorneoCompetitivoConCuota() {
        Torneo torneoComp = new Torneo(3, "Competitivo", 1, "Ajedrez",
                TipoTorneo.COMPETITIVO, 8, 3, 1, "Admin", 150);
        
        assertEquals(150, torneoComp.getMontoEntrada());
        assertEquals(TipoTorneo.COMPETITIVO, torneoComp.getTipo());
    }

    @Test
    @DisplayName("Obtener lista de participantes")
    void testObtenerListaParticipantes() {
        torneo.agregarParticipante(participante1);
        torneo.agregarParticipante(participante2);
        
        var participantes = torneo.getParticipantes();
        assertEquals(2, participantes.size());
        assertTrue(participantes.stream().anyMatch(p -> p.getIdUsuario() == 1));
        assertTrue(participantes.stream().anyMatch(p -> p.getIdUsuario() == 2));
    }

    @Test
    @DisplayName("Cambiar estado del torneo")
    void testCambiarEstadoTorneo() {
        assertEquals(EstadoTorneo.ABIERTO, torneo.getEstado());
        
        torneo.setEstado(EstadoTorneo.EN_PROGRESO);
        assertEquals(EstadoTorneo.EN_PROGRESO, torneo.getEstado());
        
        torneo.setEstado(EstadoTorneo.FINALIZADO);
        assertEquals(EstadoTorneo.FINALIZADO, torneo.getEstado());
    }

    @Test
    @DisplayName("Spot para fan se remite correctamente al remover fan")
    void testRemoverFanActualizaSpotsUsados() {
        torneo.agregarParticipante(participante2); // Es fan
        assertEquals(1, torneo.getSpotsUsadosFans());
        assertEquals(1, torneo.getSpotsDisponiblesFans());
        
        torneo.removerParticipante(2);
        assertEquals(0, torneo.getSpotsUsadosFans());
        assertEquals(2, torneo.getSpotsDisponiblesFans());
    }
}
