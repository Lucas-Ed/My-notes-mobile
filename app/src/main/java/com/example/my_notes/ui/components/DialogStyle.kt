package com.example.my_notes.ui.components

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.my_notes.ui.theme.PurpleHeader

/** Cor de conteúdo fixa sobre o fundo branco dos modais. */
internal val DialogContentColor = Color(0xFF212121)

/** Vermelho destrutivo legível sobre o fundo branco dos modais. */
internal val DialogDestructiveColor = Color(0xFFB71C1C)

/** Cor de marca usada nos botões primários e ícones dos modais. */
internal val DialogBrandColor = PurpleHeader

/**
 * Calcula a luminância relativa de uma cor (0f escuro .. 1f claro)
 * para decidir a cor de texto com melhor contraste sobre ela.
 */
internal fun Color.luminance(): Float {
    return (0.299f * red + 0.587f * green + 0.114f * blue)
}

/**
 * Cor de texto legível sobre esta cor de fundo:
 * escuro sobre fundos claros, claro sobre fundos escuros.
 */
internal fun Color.contrastTextColor(): Color {
    return if (luminance() > 0.5f) Color(0xFF212121) else Color.White
}

/**
 * Cria as cores de um [androidx.compose.material3.OutlinedTextField]
 * dentro dos diálogos coloridos, garantindo contraste adequado
 * com a cor de fundo e um leve preenchimento de superfície.
 */
@Composable
internal fun dialogTextFieldColors(textColor: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = textColor,
    unfocusedBorderColor = textColor.copy(alpha = 0.55f),
    focusedLabelColor = textColor,
    unfocusedLabelColor = textColor.copy(alpha = 0.75f),
    focusedTextColor = textColor,
    unfocusedTextColor = textColor,
    cursorColor = textColor,
    focusedContainerColor = textColor.copy(alpha = 0.06f),
    unfocusedContainerColor = textColor.copy(alpha = 0.04f),
    focusedPlaceholderColor = textColor.copy(alpha = 0.5f),
    unfocusedPlaceholderColor = textColor.copy(alpha = 0.4f)
)
