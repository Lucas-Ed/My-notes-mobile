package com.example.my_notes.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ---------------------------------------------------------------
// Cores do tema inspiradas no layout web do My Notes
// ---------------------------------------------------------------

/** Roxo do cabeçalho (rgb(148,0,211)) */
val PurpleHeader = Color(0xFF9400D3)

/** Fundo escuro das seções (rgb(33,33,33)) */
val DarkBackground = Color(0xFF212121)

/** Superfície dos cartões e diálogos */
val SurfaceDark = Color(0xFF2C2C2C)

/** Cor padrão usada quando uma cor hex não pôde ser interpretada */
val FallbackColor = Color(0xFFFFFFFF)

/**
 * Esquema de cores escuro do aplicativo, alinhado
 * com a identidade visual do projeto web de referência.
 */
private val MyNotesColorScheme = darkColorScheme(
    primary = PurpleHeader,
    onPrimary = Color.White,
    secondary = Color(0xFFBB86FC),
    onSecondary = Color.Black,
    background = DarkBackground,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF3A3A3A),
    onSurfaceVariant = Color(0xFFCCCCCC),
    error = Color(0xFFCF6679),
    onError = Color.Black
)

/** Formas arredondadas dos componentes (cartões, botões, diálogos). */
private val MyNotesShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp)
)

/**
 * Tema Material 3 do My Notes.
 * Aplica o esquema de cores escuro e as formas do aplicativo.
 *
 * @param content Conteúdo Compose que receberá o tema aplicado
 */
@Composable
fun MyNotesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MyNotesColorScheme,
        shapes = MyNotesShapes,
        content = content
    )
}

/**
 * Converte uma cor hexadecimal (ex.: "#f6c2d9") em [Color] do Compose.
 * Aceita formatos #RGB, #RRGGBB e #AARRGGBB.
 * Implementação pura em Kotlin (sem depender de android.graphics.Color),
 * o que a torna testável em testes unitários de JVM.
 * Retorna branco caso a string seja inválida.
 */
fun parseHexColor(hex: String): Color {
    return try {
        val clean = hex.removePrefix("#")
        val argb = when (clean.length) {
            3 -> "FF" + clean.map { "$it$it" }.joinToString("")
            6 -> "FF$clean"
            8 -> clean
            else -> return FallbackColor
        }
        val alpha = argb.substring(0, 2).toInt(16) / 255f
        val red = argb.substring(2, 4).toInt(16) / 255f
        val green = argb.substring(4, 6).toInt(16) / 255f
        val blue = argb.substring(6, 8).toInt(16) / 255f
        Color(red = red, green = green, blue = blue, alpha = alpha)
    } catch (e: Exception) {
        FallbackColor
    }
}
