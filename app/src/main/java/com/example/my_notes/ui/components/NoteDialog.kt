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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.my_notes.data.model.Note
import com.example.my_notes.ui.theme.parseHexColor

/**
 * Diálogo para criar ou editar uma nota.
 *
 * Funcionalidades alinhadas à proposta web:
 * - Fundo do diálogo muda conforme a categoria selecionada (recurso extra).
 * - Campos: categoria (seleção), título e conteúdo.
 * - Botão de exclusão visível apenas ao editar uma nota existente.
 * - Botão Salvar habilitado somente com formulário válido.
 *
 * @param note Nota em edição, ou null para criação
 * @param categories Lista de categorias disponíveis para seleção
 * @param onDismiss Chamado para fechar o diálogo sem salvar
 * @param onSave Chamado com a nota pronta para salvar
 * @param onDelete Chamado para excluir a nota (apenas em edição)
 */
@Composable
fun NoteDialog(
    note: Note?,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSave: (Note) -> Unit,
    onDelete: (Note) -> Unit
) {
    var title by rememberSaveable(note?.id) { mutableStateOf(note?.title ?: "") }
    var content by rememberSaveable(note?.id) { mutableStateOf(note?.content ?: "") }
    // mutableIntStateOf evita autoboxing de Int (recomendação do lint)
    var categoryId by rememberSaveable(note?.id) {
        mutableIntStateOf(note?.categoryId ?: categories.firstOrNull()?.id ?: 0)
    }

    val isEditing = note != null

    // Formulário válido: título, conteúdo e categoria obrigatórios
    val isFormValid = title.isNotBlank() && content.isNotBlank() && categoryId != 0

    // Cor de fundo do diálogo conforme a categoria selecionada
    val dialogColor = categories
        .find { it.id == categoryId }
        ?.let { parseHexColor(it.color) }
        ?: MaterialTheme.colorScheme.surface

    // Cor de texto legível sobre o fundo colorido do diálogo
    val onDialogColor = if (dialogColor.luminance() > 0.5f) Color(0xFF212121) else Color.White

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = dialogColor,
        title = {
            Text(
                text = if (isEditing) "Editar Nota" else "Nova Nota",
                fontWeight = FontWeight.Bold,
                color = onDialogColor
            )
        },
        text = {
            Column {
                // --------------------------------------------------
                // Seleção de categoria
                // --------------------------------------------------
                Text(
                    text = "Categoria",
                    style = MaterialTheme.typography.bodyMedium,
                    color = onDialogColor
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.take(5).forEach { category ->
                        val isSelected = category.id == categoryId
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (isSelected) Color.Black.copy(alpha = 0.25f)
                                    else Color.Black.copy(alpha = 0.10f)
                                )
                                .clickable { categoryId = category.id }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(parseHexColor(category.color), CircleShape)
                            )
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.labelMedium,
                                color = onDialogColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --------------------------------------------------
                // Campo de título
                // --------------------------------------------------
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = dialogTextFieldColors(onDialogColor)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // --------------------------------------------------
                // Campo de conteúdo (textarea)
                // --------------------------------------------------
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Conteúdo") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth(),
                    colors = dialogTextFieldColors(onDialogColor)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        Note(
                            id = note?.id ?: 0,
                            title = title.trim(),
                            content = content.trim(),
                            categoryId = categoryId
                        )
                    )
                },
                enabled = isFormValid
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Row {
                // Botão de exclusão: exibido somente ao editar nota existente
                if (isEditing) {
                    Button(
                        onClick = { onDelete(note) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB71C1C)
                        )
                    ) {
                        Text("Excluir")
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancelar", color = onDialogColor)
                }
            }
        }
    )
}

/**
 * Cria as cores de um [OutlinedTextField] dentro dos diálogos coloridos,
 * garantindo contraste adequado com a cor de fundo.
 */
@Composable
private fun dialogTextFieldColors(textColor: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = textColor,
    unfocusedBorderColor = textColor.copy(alpha = 0.6f),
    focusedLabelColor = textColor,
    unfocusedLabelColor = textColor.copy(alpha = 0.8f),
    focusedTextColor = textColor,
    unfocusedTextColor = textColor,
    cursorColor = textColor
)

/**
 * Calcula a luminância relativa de uma cor para decidir
 * se o texto deve ser escuro ou claro (contraste).
 */
private fun Color.luminance(): Float {
    return (0.299f * red + 0.587f * green + 0.114f * blue)
}
