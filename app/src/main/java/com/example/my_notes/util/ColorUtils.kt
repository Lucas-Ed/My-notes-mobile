package com.example.my_notes.util

/**
 * Converte uma cor hexadecimal (ex.: "#f6c2d9") em um inteiro ARGB
 * no formato 0xAARRGGBB (mesmo formato de android.graphics.Color).
 * Aceita formatos #RGB, #RRGGBB e #AARRGGBB.
 *
 * Implementação pura em Kotlin (sem android.graphics), o que a torna
 * testável em testes unitários de JVM.
 * Retorna branco (0xFFFFFFFF) caso a string seja inválida.
 */
fun parseHexColor(hex: String): Int {
    return try {
        val clean = hex.removePrefix("#")
        val argb = when (clean.length) {
            3 -> "FF" + clean.map { "$it$it" }.joinToString("")
            6 -> "FF$clean"
            8 -> clean
            else -> return 0xFFFFFFFF.toInt()
        }
        argb.toLong(16).toInt()
    } catch (e: Exception) {
        0xFFFFFFFF.toInt()
    }
}

/**
 * Calcula a luminância relativa (0f escuro .. 1f claro) de uma cor
 * no formato ARGB 0xAARRGGBB. Usada para decidir a cor da fonte
 * sobre um fundo colorido: fundo claro → texto preto, senão branco.
 */
fun Int.luminance(): Float {
    val red = ((this shr 16) and 0xFF) / 255f
    val green = ((this shr 8) and 0xFF) / 255f
    val blue = (this and 0xFF) / 255f
    return 0.299f * red + 0.587f * green + 0.114f * blue
}

/**
 * Cor de texto legível sobre esta cor de fundo (formato ARGB):
 * preto sobre fundos claros, branco sobre fundos escuros.
 */
fun Int.contrastTextColor(): Int {
    return if (luminance() > 0.5f) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
}
