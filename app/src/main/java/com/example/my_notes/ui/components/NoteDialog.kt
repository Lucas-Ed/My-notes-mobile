package com.example.my_notes.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.EditNote
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.my_notes.data.model.Category
import com.example.my_notes.data.model.Note
import com.example.my_notes.ui.theme.parseHexColor

/**
 * Diálogo para criar ou editar uma nota.
 *
 * Funcionalidades alinhadas à proposta web:
 * - Fundo branco fixo; a cor da categoria aparece apenas nos chips
 *   de seleção (bolinha colorida de cada categoria).
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
    // Se a categoria da nota foi excluída, inicia em 0 para o usuário
    // escolher outra antes de salvar (validação exige categoria != 0).
    var categoryId by rememberSaveable(note?.id) {
        val initial = note?.let { n ->
            if (categories.any { it.id == n.categoryId }) n.categoryId else 0
        } ?: (categories.firstOrNull()?.id ?: 0)
        mutableIntStateOf(initial)
    }

    val isEditing = note != null

    // Formulário válido: título, conteúdo e categoria obrigatórios
    val isFormValid = title.isNotBlank() && content.isNotBlank() && categoryId != 0

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        icon = {
            Icon(
                imageVector = Icons.Outlined.EditNote,
                contentDescription = null,
                tint = DialogBrandColor
            )
        },
        title = {
            Text(
                text = if (isEditing) "Editar Nota" else "Nova Nota",
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
                // --------------------------------------------------
                // Seleção de categoria
                // --------------------------------------------------
                Text(
                    text = "Categoria",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = DialogContentColor.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // horizontalScroll permite rolar quando há muitas categorias
                if (categories.isEmpty()) {
                    Text(
                        text = "Nenhuma categoria disponível. Crie uma em Categorias.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DialogContentColor.copy(alpha = 0.75f)
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        categories.forEach { category ->
                            CategorySelectChip(
                                category = category,
                                isSelected = category.id == categoryId,
                                contentColor = DialogContentColor,
                                onClick = { categoryId = category.id }
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
                enabled = isFormValid,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DialogBrandColor,
                    contentColor = Color.White,
                    disabledContainerColor = DialogContentColor.copy(alpha = 0.10f),
                    disabledContentColor = DialogContentColor.copy(alpha = 0.38f)
                )
            ) {
                Text("Salvar", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Botão de exclusão: exibido somente ao editar nota existente
                if (isEditing) {
                    OutlinedButton(
                        onClick = { onDelete(note) },
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
                    Spacer(modifier = Modifier.width(4.dp))
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancelar", color = DialogContentColor, fontWeight = FontWeight.Medium)
                }
            }
        }
    )
}

/**
 * Chip de seleção de categoria dentro do diálogo de nota.
 * Sobre o fundo branco: seleção destacada com a cor da marca
 * (fundo lavanda suave + borda roxa) e a bolinha colorida da categoria.
 */
@Composable
private fun CategorySelectChip(
    category: Category,
    isSelected: Boolean,
    contentColor: Color,
    onClick: () -> Unit
) {
    val backgroundColor =
        if (isSelected) DialogBrandColor.copy(alpha = 0.12f)
        else contentColor.copy(alpha = 0.06f)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .then(
                if (isSelected) {
                    Modifier.border(1.5.dp, DialogBrandColor, RoundedCornerShape(50))
                } else {
                    Modifier.border(1.dp, contentColor.copy(alpha = 0.15f), RoundedCornerShape(50))
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(parseHexColor(category.color))
        )
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = contentColor
        )
    }
}
