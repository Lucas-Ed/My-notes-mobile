package com.example.my_notes.data.repository

import com.example.my_notes.data.local.CategoryDao
import com.example.my_notes.data.local.NoteDao
import com.example.my_notes.data.model.Category
import com.example.my_notes.data.model.Note
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositório central do aplicativo.
 * Expõe operações de leitura como [Flow] (reativas) e operações de
 * escrita como funções `suspend`, despachadas para a thread de IO.
 * Toda a persistência é local (Room), sem chamadas a APIs externas.
 */
@Singleton
class NotesRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val categoryDao: CategoryDao,
    private val dispatcher: CoroutineDispatcher
) {

    /** Emite a lista de notas sempre que ela mudar no banco. */
    fun observeNotes(): Flow<List<Note>> =
        noteDao.observeAll().flowOn(dispatcher)

    /** Emite a lista de categorias sempre que ela mudar no banco. */
    fun observeCategories(): Flow<List<Category>> =
        categoryDao.observeAll().flowOn(dispatcher)

    /** Insere uma nova nota ou atualiza uma existente. */
    suspend fun saveNote(note: Note) = withContext(dispatcher) {
        noteDao.upsert(note)
    }

    /** Remove uma nota. */
    suspend fun deleteNote(note: Note) = withContext(dispatcher) {
        noteDao.delete(note)
    }

    /** Insere uma nova categoria ou atualiza uma existente. */
    suspend fun saveCategory(category: Category) = withContext(dispatcher) {
        categoryDao.upsert(category)
    }

    /** Remove uma categoria. */
    suspend fun deleteCategory(category: Category) = withContext(dispatcher) {
        categoryDao.delete(category)
    }
}
