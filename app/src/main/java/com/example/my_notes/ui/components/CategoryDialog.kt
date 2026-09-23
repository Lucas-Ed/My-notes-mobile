package com.example.my_notes.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.my_notes.data.model.Category
import com.example.my_notes.ui.theme.parseHexColor

// Paleta de cores predefinidas usadas no seletor de cor da categoria
private val presetColors = listOf(
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

/**
 * Diálogo para criar ou editar uma categoria.
 *
 * - Fundo branco fixo; a cor escolhida aparece apenas no ícone
 *   da pré-visualização (e é salva na categoria).
 * - Permite escolher nome e cor (paleta predefinida).
 * - Valida o formulário antes de habilitar o botão Salvar.
 * - Botões com hierarquia M3 clara: primário roxo, cancelar neutro,
 *   excluir em outline vermelho (visíveis sobre o branco).
 * - Botão de exclusão visível apenas ao editar uma categoria existente.
 *
 * @param category Categoria em edição, ou null para criação
 * @param onDismiss Chamado para fechar o diálogo sem salvar
 * @param onSave Chamado com a categoria pronta para salvar
 * @param onDelete Chamado para excluir a categoria (apenas em edição)
 */
@Composable
fun CategoryDialog(
    category: Category?,
    onDismiss: () -> Unit,
    onSave: (Category) -> Unit,
    onDelete: (Category) -> Unit
) {
    var name by rememberSaveable(category?.id) { mutableStateOf(category?.name ?: "") }
    var color by rememberSaveable(category?.id) { mutableStateOf(category?.color ?: presetColors.first()) }

    val isEditing = category != null
    val isFormValid = name.isNotBlank()

    // Cor escolhida: usada apenas no círculo da pré-visualização
    val dialogColor = parseHexColor(color)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Palette,
                contentDescription = null,
                tint = DialogBrandColor
            )
        },
        title = {
            Text(
                text = if (isEditing) "Editar Categoria" else "Nova Categoria",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = DialogContentColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            // verticalScroll evita que o conteúdo seja cortado em telas pequenas
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // Campo de nome da categoria
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome") },
                    singleLine = true,
                    // Aceita qualquer caractere exatamente como digitado:
                    // sem autocorreção para não mascarar símbolos/acentos
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrectEnabled = false
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = dialogTextFieldColors(DialogContentColor)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Cor",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = DialogContentColor.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Seletor de cor com opções predefinidas
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(presetColors) { presetColor ->
                        ColorSwatch(
                            hex = presetColor,
                            isSelected = color == presetColor,
                            ringColor = DialogContentColor,
                            onClick = { color = presetColor }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pré-visualização da categoria como ela aparecerá nos chips
                Text(
                    text = "Pré-visualização",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = DialogContentColor.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(DialogContentColor.copy(alpha = 0.06f))
                        .border(1.dp, DialogContentColor.copy(alpha = 0.15f), RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Único lugar onde a cor escolhida aparece no modal
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(dialogColor)
                            .border(1.dp, DialogContentColor.copy(alpha = 0.4f), CircleShape)
                    )
                    Text(
                        text = name.trim().ifBlank { "Nome da categoria" },
                        style = MaterialTheme.typography.labelLarge,
                        color = DialogContentColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        Category(
                            id = category?.id ?: 0,
                            name = name.trim(),
                            color = color
                        )
                    )
                },
                enabled = isFormValid,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DialogBrandColor,
                    contentColor = Color.White,
                    disabledContainerColor = DialogContentColor.copy(alpha = 0.10f),
                    disabledContentColor = DialogContentColor.copy(alpha = 0.38f)
                )
            ) {
                Text(
                    text = if (isEditing) "Atualizar" else "Criar",
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Botão de exclusão: exibido somente ao editar categoria existente
                if (isEditing) {
                    OutlinedButton(
                        onClick = { onDelete(category) },
                        shape = MaterialTheme.shapes.medium,
                        border = BorderStroke(1.dp, DialogDestructiveColor),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DialogDestructiveColor)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = DialogDestructiveColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Excluir")
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancelar", color = DialogContentColor, fontWeight = FontWeight.Medium)
                }
            }
        }
    )
}

/**
 * Swatch circular de cor com anel de seleção e ícone de check
 * com contraste calculado pela luminância da própria cor.
 * Área de toque de 44dp com estados acessíveis.
 */
@Composable
private fun ColorSwatch(
    hex: String,
    isSelected: Boolean,
    ringColor: Color,
    onClick: () -> Unit
) {
    val swatchColor = parseHexColor(hex)
    val description = "Selecionar cor $hex"

    Box(
        modifier = Modifier
            .size(44.dp)
            .semantics { contentDescription = description }
            .clip(CircleShape)
            .border(
                width = 2.dp,
                color = if (isSelected) ringColor else Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(swatchColor)
                .border(
                    1.dp,
                    if (isSelected) Color.Transparent else ringColor.copy(alpha = 0.25f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = swatchColor.contrastTextColor(),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
