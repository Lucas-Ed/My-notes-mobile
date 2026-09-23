package com.example.my_notes.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
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
    "#26c6da"  // Ciano
)

/**
 * Diálogo para criar ou editar uma categoria.
 *
 * - Exibe o fundo na cor selecionada (recursos extras da proposta web).
 * - Permite escolher nome e cor (paleta predefinida).
 * - Valida o formulário antes de habilitar o botão Salvar.
 *
 * @param category Categoria em edição, ou null para criação
 * @param onDismiss Chamado para fechar o diálogo sem salvar
 * @param onSave Chamado com a categoria pronta para salvar
 */
@Composable
fun CategoryDialog(
    category: Category?,
    onDismiss: () -> Unit,
    onSave: (Category) -> Unit
) {
    var name by rememberSaveable(category?.id) { mutableStateOf(category?.name ?: "") }
    var color by rememberSaveable(category?.id) { mutableStateOf(category?.color ?: presetColors.first()) }

    val isEditing = category != null
    val isFormValid = name.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = parseHexColor(color),
        title = {
            Text(
                text = if (isEditing) "Editar Categoria" else "Nova Categoria",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
        },
        text = {
            Column {
                // Campo de nome da categoria
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF212121),
                        unfocusedBorderColor = Color(0xFF555555),
                        focusedLabelColor = Color(0xFF212121),
                        unfocusedLabelColor = Color(0xFF555555),
                        focusedTextColor = Color(0xFF212121),
                        unfocusedTextColor = Color(0xFF212121),
                        cursorColor = Color(0xFF212121)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Cor",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF212121)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Seletor de cor com opções predefinidas
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(presetColors) { presetColor ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(parseHexColor(presetColor))
                                .clickable { color = presetColor }
                                .then(
                                    if (color == presetColor) {
                                        Modifier.background(
                                            Color.Transparent
                                        )
                                    } else {
                                        Modifier
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (color == presetColor) {
                                Text(
                                    text = "✓",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
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
                enabled = isFormValid
            ) {
                Text(if (isEditing) "Atualizar" else "Criar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF212121))
            }
        }
    )
}
