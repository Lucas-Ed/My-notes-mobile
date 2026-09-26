package com.example.my_notes.ui.dialogs

import android.content.Context
import android.graphics.drawable.GradientDrawable
import com.example.my_notes.R
import com.example.my_notes.util.parseHexColor

/** Paleta de cores predefinidas usada no seletor de cor da categoria. */
internal val presetColors = listOf(
    "#f6c2d9", // Rosa (Angular)
    "#a1c8e9", // Azul claro (React)
    "#bcdfc9", // Verde menta (Vue)
    "#255db6", // Azul escuro (Backend)
    "#9400d3", // Roxo (cor primária)
    "#ffd54f", // Amarelo
    "#ef5350", // Vermelho
    "#66bb6a", // Verde
    "#ffa726", // Laranja
    "#26c6da", // Ciano
    "#ffffff"  // Branco
)

/** Cria a bolinha circular colorida usada nos chips de categoria. */
internal fun chipDot(context: Context, hex: String): GradientDrawable {
    return GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(parseHexColor(hex))
        setStroke(1, context.getColor(R.color.swatch_border))
    }
}
