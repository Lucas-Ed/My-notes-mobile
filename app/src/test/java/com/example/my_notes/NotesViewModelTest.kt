package com.example.my_notes

import app.cash.turbine.test
import com.example.my_notes.data.model.Category
import com.example.my_notes.data.model.Note
import com.example.my_notes.data.repository.NotesRepository
import com.example.my_notes.ui.feature.NotesViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Testes unitários do [NotesViewModel].
 * Verifica o carregamento inicial dos dados, a criação/edição
 * de notas e categorias e o tratamento de erros.
 */
class NotesViewModelTest {

    // Regra que configura o Dispatcher.Main para testes
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Repositório simulado com MockK
    private val repository: NotesRepository = mockk(relaxed = true)

    private lateinit var viewModel: NotesViewModel

    private val sampleCategories = listOf(
        Category(id = 1, name = "Angular", color = "#f6c2d9"),
        Category(id = 2, name = "React", color = "#a1c8e9")
    )

    private val sampleNotes = listOf(
        Note(id = 1, title = "Estudar Kotlin", content = "Revisar Coroutines", categoryId = 1),
        Note(id = 2, title = "Revisar Compose", content = "Praticar StateFlow", categoryId = 2)
    )

    @Before
    fun setup() {
        // Configura o retorno padrão dos Flows do repositório
        every { repository.observeNotes() } returns flowOf(sampleNotes)
        every { repository.observeCategories() } returns flowOf(sampleCategories)
        viewModel = NotesViewModel(repository)
    }

    @Test
    fun `init - carrega notas e categorias do repositorio`() = runTest {
        // Com UnconfinedTestDispatcher, o ViewModel já carregou os dados
        // na construção; basta validar o estado atual.
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(sampleNotes, state.notes)
            assertEquals(sampleCategories, state.categories)
        }
    }

    @Test
    fun `onAddNoteClick - abre dialog de nota para criacao`() = runTest {
        viewModel.onAddNoteClick()

        val state = viewModel.uiState.value
        assertTrue(state.isNoteDialogVisible)
        assertNull(state.noteBeingEdited)
    }

    @Test
    fun `onNoteClick - abre dialog de nota para edicao`() = runTest {
        val noteToEdit = sampleNotes.first()
        viewModel.onNoteClick(noteToEdit)

        val state = viewModel.uiState.value
        assertTrue(state.isNoteDialogVisible)
        assertEquals(noteToEdit, state.noteBeingEdited)
    }

    @Test
    fun `onNoteSave - salva nota e fecha dialog`() = runTest {
        val newNote = Note(id = 0, title = "Nova", content = "Conteudo", categoryId = 1)
        coEvery { repository.saveNote(any()) } returns Unit

        viewModel.onAddNoteClick()
        viewModel.onNoteSave(newNote)

        coVerify(exactly = 1) { repository.saveNote(newNote) }
        assertFalse(viewModel.uiState.value.isNoteDialogVisible)
        assertNull(viewModel.uiState.value.noteBeingEdited)
    }

    @Test
    fun `onNoteDelete - exclui nota e fecha dialog`() = runTest {
        val noteToDelete = sampleNotes.first()
        coEvery { repository.deleteNote(any()) } returns Unit

        viewModel.onNoteClick(noteToDelete)
        viewModel.onNoteDelete(noteToDelete)

        coVerify(exactly = 1) { repository.deleteNote(noteToDelete) }
        assertFalse(viewModel.uiState.value.isNoteDialogVisible)
    }

    @Test
    fun `onCategorySave - salva categoria e fecha dialog`() = runTest {
        val newCategory = Category(id = 0, name = "Flutter", color = "#255db6")
        coEvery { repository.saveCategory(any()) } returns Unit

        viewModel.onAddCategoryClick()
        viewModel.onCategorySave(newCategory)

        coVerify(exactly = 1) { repository.saveCategory(newCategory) }
        assertFalse(viewModel.uiState.value.isCategoryDialogVisible)
    }

    @Test
    fun `onNoteSave - erro no repositorio define mensagem de erro`() = runTest {
        val note = Note(id = 0, title = "T", content = "C", categoryId = 1)
        coEvery { repository.saveNote(any()) } throws RuntimeException("Falha no banco")

        viewModel.onNoteSave(note)

        assertEquals("Falha no banco", viewModel.uiState.value.error)
    }

    @Test
    fun `onErrorDismiss - limpa mensagem de erro`() = runTest {
        val note = Note(id = 0, title = "T", content = "C", categoryId = 1)
        coEvery { repository.saveNote(any()) } throws RuntimeException("Erro")
        viewModel.onNoteSave(note)

        viewModel.onErrorDismiss()

        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `colorForNote - retorna cor da categoria correspondente`() = runTest {
        val note = sampleNotes.first() // categoryId = 1 (Angular)
        val color = viewModel.colorForNote(note)
        assertEquals("#f6c2d9", color)
    }

    @Test
    fun `colorForNote - retorna branco quando categoria nao existe`() = runTest {
        val noteWithoutCategory = Note(id = 99, title = "T", content = "C", categoryId = 999)
        val color = viewModel.colorForNote(noteWithoutCategory)
        assertEquals("#FFFFFF", color)
    }

    @Test
    fun `onCategoryDialogDismiss - fecha dialog de categoria`() = runTest {
        viewModel.onAddCategoryClick()
        assertTrue(viewModel.uiState.value.isCategoryDialogVisible)

        viewModel.onCategoryDialogDismiss()

        assertFalse(viewModel.uiState.value.isCategoryDialogVisible)
        assertNull(viewModel.uiState.value.categoryBeingEdited)
    }
}
