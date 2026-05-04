package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import modelo.VoucherDescuento;
import java.time.LocalDate;

/**
 * Test unitarios para la clase VoucherDescuento.
 * Verifica la validez, uso y vencimiento de vouchers.
 */
@DisplayName("Pruebas unitarias de la clase VoucherDescuento")
public class VoucherDescuentoTest {

    private VoucherDescuento voucher;

    @BeforeEach
    void setUp() {
        voucher = new VoucherDescuento(
                1,
                100,  // idUsuario
                1,    // idTorneo
                "Torneo Amistoso",
                500,  // montoDescuento
                30    // diasValidez
        );
    }

    @Test
    @DisplayName("Crear voucher con parámetros válidos")
    void testCrearVoucherValido() {
        assertNotNull(voucher);
        assertEquals(1, voucher.getId());
        assertEquals(100, voucher.getIdUsuario());
        assertEquals(1, voucher.getIdTorneo());
        assertEquals("Torneo Amistoso", voucher.getNombreTorneo());
        assertEquals(500, voucher.getMontoDescuento());
        assertFalse(voucher.isUsado());
    }

    @Test
    @DisplayName("Voucher recién creado es válido")
    void testVoucherNuevoEsValido() {
        assertTrue(voucher.esValido());
    }

    @Test
    @DisplayName("Marcar voucher como usado")
    void testMarcarVoucherComoUsado() {
        assertTrue(voucher.esValido());
        
        voucher.marcarComoUsado();
        
        assertTrue(voucher.isUsado());
        assertFalse(voucher.esValido());
        assertNotNull(voucher.getFechaUso());
    }

    @Test
    @DisplayName("Voucher usado no es válido")
    void testVoucherUsadoNoValido() {
        voucher.marcarComoUsado();
        
        assertFalse(voucher.esValido());
    }

    @Test
    @DisplayName("Voucher vencido no es válido")
    void testVoucherVencidoNoValido() {
        // Crear voucher que vence hoy
        VoucherDescuento voucherVencido = new VoucherDescuento(
                2, 100, 1, "Torneo", 500, 0
        );
        
        // Establecer fecha de vencimiento al pasado
        voucherVencido.setFechaVencimiento(LocalDate.now().minusDays(1));
        
        assertFalse(voucherVencido.esValido());
    }

    @Test
    @DisplayName("Voucher sin vencimiento es válido indefinidamente")
    void testVoucherSinVencimientoValido() {
        VoucherDescuento voucherSinVencimiento = new VoucherDescuento(
                3, 100, 1, "Torneo", 500, 0
        );
        
        assertTrue(voucherSinVencimiento.esValido());
        
        // Simular paso del tiempo
        voucherSinVencimiento.setFechaOtorgamiento(LocalDate.now().minusYears(1));
        
        assertTrue(voucherSinVencimiento.esValido());
    }

    @Test
    @DisplayName("Obtener información del voucher")
    void testObtenerInformacionVoucher() {
        assertEquals(1, voucher.getId());
        assertEquals(100, voucher.getIdUsuario());
        assertEquals(1, voucher.getIdTorneo());
        assertEquals(500, voucher.getMontoDescuento());
        assertNotNull(voucher.getFechaOtorgamiento());
        assertNull(voucher.getFechaUso());
    }

    @Test
    @DisplayName("Fecha de uso se actualiza al marcar como usado")
    void testFechaDeUsoActualiza() {
        assertNull(voucher.getFechaUso());
        
        voucher.marcarComoUsado();
        
        LocalDate fechaUso = voucher.getFechaUso();
        assertNotNull(fechaUso);
        assertEquals(LocalDate.now(), fechaUso);
    }

    @Test
    @DisplayName("Crear múltiples vouchers con diferentes montos")
    void testVariosVouchersConMontosDiferentes() {
        VoucherDescuento v1 = new VoucherDescuento(1, 100, 1, "Torneo1", 100, 30);
        VoucherDescuento v2 = new VoucherDescuento(2, 100, 2, "Torneo2", 250, 30);
        VoucherDescuento v3 = new VoucherDescuento(3, 100, 3, "Torneo3", 1000, 30);
        
        assertEquals(100, v1.getMontoDescuento());
        assertEquals(250, v2.getMontoDescuento());
        assertEquals(1000, v3.getMontoDescuento());
        
        assertTrue(v1.esValido());
        assertTrue(v2.esValido());
        assertTrue(v3.esValido());
    }

    @Test
    @DisplayName("Modificar monto del descuento")
    void testModificarMontoDescuento() {
        assertEquals(500, voucher.getMontoDescuento());
        
        voucher.setMontoDescuento(750);
        assertEquals(750, voucher.getMontoDescuento());
    }

    @Test
    @DisplayName("Información de torneo se mantiene en voucher")
    void testInformacionTorneoEnVoucher() {
        assertEquals(1, voucher.getIdTorneo());
        assertEquals("Torneo Amistoso", voucher.getNombreTorneo());
        
        voucher.setNombreTorneo("Torneo Actualizado");
        assertEquals("Torneo Actualizado", voucher.getNombreTorneo());
    }
}
