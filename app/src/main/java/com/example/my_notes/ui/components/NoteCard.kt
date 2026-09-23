package com.example.my_notes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.my_notes.data.model.Note
import com.example.my_notes.ui.theme.parseHexColor

/**
 * Cartão que representa uma nota na lista.
 * Exibe título e prévia do conteúdo, com fundo colorido
 * conforme a cor da categoria da nota.
 *
 * Contraste da fonte: fundo branco → texto preto;
 * demais cores de fundo → texto branco.
 *
 * @param note Nota a ser exibida
 * @param categoryColor Cor hexadecimal da categoria da nota
 * @param onClick Chamado quando o usuário toca no cartão (abre edição)
 */
@Composable
fun NoteCard(
    note: Note,
    categoryColor: String,
    onClick: () -> Unit
) {
    val noteBackgroundColor = parseHexColor(categoryColor)
    val noteTextColor =
        if (noteBackgroundColor == Color.White) Color.Black else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = noteBackgroundColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(16.dp)
        ) {
            // Título da nota em negrito
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = noteTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Prévia do conteúdo da nota
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                color = noteTextColor,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Item de categoria exibido na lista/chips de categorias.
 * Mostra um ponto colorido seguido do nome da categoria.
 *
 * Exibição alinhada ao layout web: bolinha colorida + nome.
 *
 * @param categoryName Nome da categoria
 * @param color Cor hexadecimal da categoria
 * @param isSelected Indica se esta categoria está selecionada (realça o item)
 * @param onClick Chamado quando o usuário toca no item (abre edição)
 */
@Composable
fun CategoryItem(
    categoryName: String,
    color: String,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Bolinha com a cor da categoria
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(
                    color = parseHexColor(color),
                    shape = CircleShape
                )
        )
        Text(
            text = categoryName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
