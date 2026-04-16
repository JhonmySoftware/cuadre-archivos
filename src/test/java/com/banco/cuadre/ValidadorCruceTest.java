package com.banco.cuadre;

import org.junit.Test;
import static org.junit.Assert.*;

public class ValidadorCruceTest {

    @Test
    public void testNormalizarCerosIzquierda() {
        assertEquals("123", ValidadorCruce.normalizarValor("00123"));
        assertEquals("123", ValidadorCruce.normalizarValor("00000123"));
        assertEquals("1", ValidadorCruce.normalizarValor("001"));
    }
    
    @Test
    public void testNormalizarMayusculas() {
        assertEquals("abc", ValidadorCruce.normalizarValor("ABC"));
        assertEquals("abc", ValidadorCruce.normalizarValor("AbC"));
    }
    
    @Test
    public void testNormalizarEspacios() {
        assertEquals("abc", ValidadorCruce.normalizarValor("  abc  "));
        assertEquals("abc", ValidadorCruce.normalizarValor(" abc"));
    }
    
    @Test
    public void testNormalizarVacio() {
        assertEquals("", ValidadorCruce.normalizarValor(""));
        assertEquals("", ValidadorCruce.normalizarValor(null));
        assertEquals("", ValidadorCruce.normalizarValor("   "));
    }
    
    @Test
    public void testNormalizarDecimal() {
        assertEquals("12.34", ValidadorCruce.normalizarValor("12.34"));
        assertEquals("00012.34", ValidadorCruce.normalizarValor("00012.34"));
    }
    
    @Test
    public void testNormalizarNegativos() {
        assertEquals("-123", ValidadorCruce.normalizarValor("-00123"));
        assertEquals("-123", ValidadorCruce.normalizarValor("-123"));
    }
}
