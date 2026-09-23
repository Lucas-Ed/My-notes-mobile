package com.example.my_notes.ui.feature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.my_notes.ui.components.CategoryDialog
import com.example.my_notes.ui.components.CategoryItem
import com.example.my_notes.ui.components.NoteCard
import com.example.my_notes.ui.components.NoteDialog
import com.example.my_notes.ui.theme.PurpleHeader

/**
 * Tela principal do My Notes.
 * Exibe a lista de categorias (chips) e a lista de notas (cartões),
 * com botões para criar novos itens e diálogos de criação/edição.
 *
 * A tela é um mapeamento puro do [NotesUiState]: cada alteração
 * de estado do ViewModel reflete imediatamente na interface.
 *
 * @param viewModel ViewModel injetado via Hilt
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: NotesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Exibe mensagens de erro em um Snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onErrorDismiss()
        }
    }

    Scaffold(
        topBar = {
            // Cabeçalho roxo com o nome do aplicativo
            TopAppBar(
                title = {
                    Text(
                        text = "My Notes",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PurpleHeader,
                    titleContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ==============================================================
            // Seção de categorias
            // ==============================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Categorias",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                FilledTonalButton(onClick = viewModel::onAddCategoryClick) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.height(18.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Nova")
                }
            }

            // Lista horizontal de categorias (chips)
            if (uiState.categories.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.categories, key = { it.id }) { category ->
                        CategoryItem(
                            categoryName = category.name,
                            color = category.color,
                            onClick = { viewModel.onCategoryClick(category) }
                        )
                    }
                }
            } else {
                Text(
                    text = "Nenhuma categoria cadastrada",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==============================================================
            // Seção de notas
            // ==============================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Notas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Button(onClick = viewModel::onAddNoteClick) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.height(18.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Nova")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Conteúdo principal: carregando, vazio ou lista de notas
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    uiState.notes.isEmpty() -> {
                        Text(
                            text = "Nenhuma nota ainda.\nToque em \"Nova\" para criar a primeira!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    else -> {
                        // Lista vertical de cartões de notas (RecyclerView do Compose)
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(uiState.notes, key = { it.id }) { note ->
                                NoteCard(
                                    note = note,
                                    categoryColor = viewModel.colorForNote(note),
                                    onClick = { viewModel.onNoteClick(note) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ==============================================================
    // Diálogos de criação/edição
    // ==============================================================

    // Diálogo de nota: visível conforme estado do ViewModel
    if (uiState.isNoteDialogVisible) {
        NoteDialog(
            note = uiState.noteBeingEdited,
            categories = uiState.categories,
            onDismiss = viewModel::onNoteDialogDismiss,
            onSave = viewModel::onNoteSave,
            onDelete = viewModel::onNoteDelete
        )
    }

    // Diálogo de categoria: visível conforme estado do ViewModel
    if (uiState.isCategoryDialogVisible) {
        CategoryDialog(
            category = uiState.categoryBeingEdited,
            onDismiss = viewModel::onCategoryDialogDismiss,
            onSave = viewModel::onCategorySave,
            onDelete = viewModel::onCategoryDelete
        )
    }
}
