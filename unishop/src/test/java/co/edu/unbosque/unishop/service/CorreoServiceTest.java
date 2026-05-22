package co.edu.unbosque.unishop.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para CorreoService.
 *
 * ✅ SEGURIDAD DE DATOS:
 *    - Solo se prueba la lógica pura de generación de código.
 *    - El método enviarCodigoVerificacion() NO se invoca en estas pruebas
 *      (requiere SMTP real), garantizando que NO se envía ningún correo real.
 *    - No hay mocks de red ni acceso a servicios externos.
 */
public class CorreoServiceTest {

    private CorreoService correoService;

    @BeforeEach
    void setUp() {
        correoService = new CorreoService();
    }

    // ══════════════════════════════════════════════════════════════════════
    // generarCodigo()
    // ══════════════════════════════════════════════════════════════════════

    @Test
    void generarCodigo_noEsNulo() {
        String codigo = correoService.generarCodigo();
        assertNotNull(codigo, "El código generado no debe ser nulo");
    }

    @Test
    void generarCodigo_tieneSeisCifras() {
        String codigo = correoService.generarCodigo();
        assertEquals(6, codigo.length(),
                "El código debe tener exactamente 6 dígitos, obtenido: " + codigo);
    }

    @Test
    void generarCodigo_esSoloNumerico() {
        String codigo = correoService.generarCodigo();
        assertTrue(codigo.matches("\\d{6}"),
                "El código debe contener solo dígitos numéricos, obtenido: " + codigo);
    }

    @Test
    void generarCodigo_estaEnRangoValido() {
        String codigo = correoService.generarCodigo();
        int numero = Integer.parseInt(codigo);
        // Rango esperado: 100000 a 999999 (6 dígitos garantizados)
        assertTrue(numero >= 100000 && numero <= 999999,
                "El código debe estar entre 100000 y 999999, obtenido: " + numero);
    }

    @RepeatedTest(10)
    void generarCodigo_siempreTieneSeisCifras_enMultiplesEjecuciones() {
        // Se repite 10 veces para verificar consistencia con Random
        String codigo = correoService.generarCodigo();
        assertEquals(6, codigo.length(),
                "En cada ejecución el código debe tener 6 dígitos, obtenido: " + codigo);
        assertTrue(codigo.matches("\\d{6}"),
                "Cada código debe ser numérico puro");
    }

    @Test
    void generarCodigo_dosCodigosSonGeneradosIndependientemente() {
        // Dos llamadas consecutivas no deben necesariamente retornar el mismo valor
        // (probabilidad de colisión = 1/900000, prácticamente imposible)
        String codigo1 = correoService.generarCodigo();
        String codigo2 = correoService.generarCodigo();

        // Ambos deben ser válidos
        assertNotNull(codigo1);
        assertNotNull(codigo2);
        assertEquals(6, codigo1.length());
        assertEquals(6, codigo2.length());
    }
}