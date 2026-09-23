package com.example.my_notes

import com.example.my_notes.ui.theme.parseHexColor
import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Testes da função utilitária [parseHexColor],
 * responsável por converter cores hexadecimais em cores do Compose.
 */
class ParseHexColorTest {

    @Test
    fun `parseHexColor - cor hex valida de 6 digitos`() {
        val color = parseHexColor("#f6c2d9")
        // #f6c2d9 = R:246 G:194 B:217 com alfa 1.0
        val expected = Color(
            red = 246f / 255f,
            green = 194f / 255f,
            blue = 217f / 255f,
            alpha = 1f
        )
        assertEquals(expected, color)
    }

    @Test
    fun `parseHexColor - cor hex sem prefixo`() {
        val color = parseHexColor("ffffff")
        assertEquals(Color.White, color)
    }

    @Test
    fun `parseHexColor - string invalida retorna branco`() {
        val color = parseHexColor("abc")
        // "abc" tem 3 dígitos: a->aa, b->bb, c->cc => FF (aabbcc)
        val expected = parseHexColor("#aabbcc")
        assertEquals(expected, color)
    }

    @Test
    fun `parseHexColor - string vazia retorna branco de fallback`() {
        val color = parseHexColor("")
        assertEquals(Color.White, color)
    }
}
