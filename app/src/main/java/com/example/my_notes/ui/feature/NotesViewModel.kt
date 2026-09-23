package com.example.my_notes.ui.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_notes.data.model.Category
import com.example.my_notes.data.model.Note
import com.example.my_notes.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado da interface (UI State) da tela de notas.
 * Concentra todos os dados e flags exibidos na tela,
 * permitindo que a UI seja um mapeamento puro do estado.
 */
data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    // Controle do diálogo de nota (criar/editar)
    val isNoteDialogVisible: Boolean = false,
    val noteBeingEdited: Note? = null,
    // Controle do diálogo de categoria (criar/editar)
    val isCategoryDialogVisible: Boolean = false,
    val categoryBeingEdited: Category? = null
)

/**
 * ViewModel da tela principal do My Notes.
 * Gerencia o estado da UI, expõe os dados do repositório via [StateFlow]
 * e centraliza o tratamento de eventos (cliques, salvamentos, exclusões).
 *
 * @param repository Repositório local (Room) de notas e categorias
 */
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    init {
        // Observa notas e categorias em conjunto; sempre que qualquer
        // uma mudar no banco, o estado da UI é atualizado automaticamente.
        viewModelScope.launch {
            combine(
                repository.observeNotes(),
                repository.observeCategories()
            ) { notes, categories ->
                notes to categories
            }
                .catch { e ->
                    // Tratamento de erros vindos dos Flows do banco
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Erro ao carregar dados")
                    }
                }
                .collect { (notes, categories) ->
                    _uiState.update {
                        it.copy(
                            notes = notes,
                            categories = categories,
                            isLoading = false
                        )
                    }
                }
        }
    }

    // ---------------------------------------------------------------
    // Eventos do diálogo de nota
    // ---------------------------------------------------------------

    /** Abre o diálogo de nota. Se [note] for informado, entra em modo de edição. */
    fun onAddNoteClick() {
        _uiState.update {
            it.copy(isNoteDialogVisible = true, noteBeingEdited = null)
        }
    }

    /** Abre o diálogo de nota já preenchido com os dados da nota clicada. */
    fun onNoteClick(note: Note) {
        _uiState.update {
            it.copy(isNoteDialogVisible = true, noteBeingEdited = note)
        }
    }

    /** Fecha o diálogo de nota sem salvar. */
    fun onNoteDialogDismiss() {
        _uiState.update {
            it.copy(isNoteDialogVisible = false, noteBeingEdited = null)
        }
    }

    /** Salva (insere ou atualiza) a nota e fecha o diálogo. */
    fun onNoteSave(note: Note) {
        viewModelScope.launch {
            try {
                repository.saveNote(note)
                onNoteDialogDismiss()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Erro ao salvar nota") }
            }
        }
    }

    /** Exclui a nota informada. */
    fun onNoteDelete(note: Note) {
        viewModelScope.launch {
            try {
                repository.deleteNote(note)
                onNoteDialogDismiss()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Erro ao excluir nota") }
            }
        }
    }

    // ---------------------------------------------------------------
    // Eventos do diálogo de categoria
    // ---------------------------------------------------------------

    /** Abre o diálogo de categoria. Se [category] for informada, entra em modo de edição. */
    fun onAddCategoryClick() {
        _uiState.update {
            it.copy(isCategoryDialogVisible = true, categoryBeingEdited = null)
        }
    }

    /** Abre o diálogo de categoria já preenchido com os dados clicados. */
    fun onCategoryClick(category: Category) {
        _uiState.update {
            it.copy(isCategoryDialogVisible = true, categoryBeingEdited = category)
        }
    }

    /** Fecha o diálogo de categoria sem salvar. */
    fun onCategoryDialogDismiss() {
        _uiState.update {
            it.copy(isCategoryDialogVisible = false, categoryBeingEdited = null)
        }
    }

    /** Salva (insere ou atualiza) a categoria e fecha o diálogo. */
    fun onCategorySave(category: Category) {
        viewModelScope.launch {
            try {
                repository.saveCategory(category)
                onCategoryDialogDismiss()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Erro ao salvar categoria") }
            }
        }
    }

    // ---------------------------------------------------------------
    // Outros eventos
    // ---------------------------------------------------------------

    /** Limpa a mensagem de erro exibida no Snackbar. */
    fun onErrorDismiss() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Retorna a cor da categoria associada à nota.
     * Usada para colorir o cartão da nota conforme a categoria.
     */
    fun colorForNote(note: Note): String {
        return _uiState.value.categories
            .find { it.id == note.categoryId }
            ?.color
            ?: "#FFFFFF"
    }
}
