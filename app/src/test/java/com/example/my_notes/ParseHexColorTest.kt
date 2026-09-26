package com.example.my_notes

import com.example.my_notes.util.parseHexColor
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Testes da função utilitária [parseHexColor],
 * responsável por converter cores hexadecimais
 * em inteiros ARGB (0xAARRGGBB).
 */
class ParseHexColorTest {

    @Test
    fun `parseHexColor - cor hex valida de 6 digitos`() {
        val color = parseHexColor("#f6c2d9")
        // #f6c2d9 = FF (alfa opaco) + R:246 G:194 B:217
        assertEquals(0xFFF6C2D9.toInt(), color)
    }

    @Test
    fun `parseHexColor - cor hex sem prefixo`() {
        val color = parseHexColor("ffffff")
        assertEquals(0xFFFFFFFF.toInt(), color)
    }

    @Test
    fun `parseHexColor - cor hex de 3 digitos expande para 6`() {
        val color = parseHexColor("abc")
        // "abc" tem 3 dígitos: a->aa, b->bb, c->cc => FF (aabbcc)
        val expected = parseHexColor("#aabbcc")
        assertEquals(expected, color)
    }

    @Test
    fun `parseHexColor - string vazia retorna branco de fallback`() {
        val color = parseHexColor("")
        assertEquals(0xFFFFFFFF.toInt(), color)
    }
}
