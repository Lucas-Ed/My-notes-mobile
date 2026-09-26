package com.example.my_notes

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.my_notes.ui.adapter.CategoriesAdapter
import com.example.my_notes.ui.adapter.NotesAdapter
import com.example.my_notes.ui.dialogs.CategoryDialog
import com.example.my_notes.ui.dialogs.NoteDialog
import com.example.my_notes.ui.feature.NotesUiState
import com.example.my_notes.ui.feature.NotesViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Activity principal do aplicativo My Notes (interface XML/View).
 *
 * Infla o layout activity_main, configura as listas (RecyclerView) e
 * observa o [NotesViewModel] com repeatOnLifecycle, traduzindo cada
 * [NotesUiState] em views — inclusive a abertura/fechamento dos
 * diálogos de nota e categoria.
 *
 * A anotação @AndroidEntryPoint permite que o Hilt injete
 * automaticamente as dependências nesta Activity.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: NotesViewModel by viewModels()

    private lateinit var root: View
    private lateinit var progress: ProgressBar
    private lateinit var notesEmpty: TextView
    private lateinit var categoriesEmpty: TextView
    private lateinit var recyclerNotes: RecyclerView
    private lateinit var recyclerCategories: RecyclerView

    private lateinit var notesAdapter: NotesAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter

    private var noteDialog: AlertDialog? = null
    private var categoryDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        root = findViewById(R.id.root)
        progress = findViewById(R.id.progress)
        notesEmpty = findViewById(R.id.notesEmpty)
        categoriesEmpty = findViewById(R.id.categoriesEmpty)
        recyclerNotes = findViewById(R.id.recyclerNotes)
        recyclerCategories = findViewById(R.id.recyclerCategories)

        setupLists()
        setupButtons()
        observeState()
    }

    override fun onDestroy() {
        noteDialog?.dismiss()
        categoryDialog?.dismiss()
        super.onDestroy()
    }

    private fun setupLists() {
        notesAdapter = NotesAdapter(
            noteColor = { note -> viewModel.colorForNote(note) },
            onClick = { note -> viewModel.onNoteClick(note) }
        )
        recyclerNotes.layoutManager = LinearLayoutManager(this)
        recyclerNotes.adapter = notesAdapter

        categoriesAdapter = CategoriesAdapter(
            onClick = { category -> viewModel.onCategoryClick(category) }
        )
        recyclerCategories.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerCategories.adapter = categoriesAdapter
    }

    private fun setupButtons() {
        findViewById<View>(R.id.btnAddNote).setOnClickListener {
            viewModel.onAddNoteClick()
        }
        findViewById<View>(R.id.btnAddCategory).setOnClickListener {
            viewModel.onAddCategoryClick()
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    /** Traduz o estado atual em views (listas, vazio, erros e diálogos). */
    private fun render(state: NotesUiState) {
        // Carregamento e estados vazios
        progress.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        notesEmpty.visibility =
            if (!state.isLoading && state.notes.isEmpty()) View.VISIBLE else View.GONE
        categoriesEmpty.visibility =
            if (state.categories.isEmpty()) View.VISIBLE else View.GONE

        // Listas
        notesAdapter.submitList(state.notes)
        categoriesAdapter.submitList(state.categories)

        // Erros: diálogo aberto usa Toast (fica acima da janela do diálogo),
        // caso contrário usa Snackbar (comportamento documentado).
        state.error?.let { message ->
            val dialogOpen = noteDialog?.isShowing == true ||
                categoryDialog?.isShowing == true
            if (dialogOpen) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            } else {
                Snackbar.make(root, message, Snackbar.LENGTH_LONG).show()
            }
            viewModel.onErrorDismiss()
        }

        // Diálogo de nota
        if (state.isNoteDialogVisible) {
            if (noteDialog?.isShowing != true) openNoteDialog(state)
        } else {
            noteDialog?.dismiss()
            noteDialog = null
        }

        // Diálogo de categoria
        if (state.isCategoryDialogVisible) {
            if (categoryDialog?.isShowing != true) openCategoryDialog(state)
        } else {
            categoryDialog?.dismiss()
            categoryDialog = null
        }
    }

    private fun openNoteDialog(state: NotesUiState) {
        noteDialog = NoteDialog.show(
            activity = this,
            note = state.noteBeingEdited,
            categories = state.categories,
            onSave = { note -> viewModel.onNoteSave(note) },
            onDelete = { note -> viewModel.onNoteDelete(note) }
        ).also { dialog ->
            dialog.setOnDismissListener {
                noteDialog = null
                viewModel.onNoteDialogDismiss()
            }
        }
    }

    private fun openCategoryDialog(state: NotesUiState) {
        categoryDialog = CategoryDialog.show(
            activity = this,
            category = state.categoryBeingEdited,
            onSave = { category -> viewModel.onCategorySave(category) },
            onDelete = { category -> viewModel.onCategoryDelete(category) }
        ).also { dialog ->
            dialog.setOnDismissListener {
                categoryDialog = null
                viewModel.onCategoryDialogDismiss()
            }
        }
    }
}
