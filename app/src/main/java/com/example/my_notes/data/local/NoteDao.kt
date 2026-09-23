package com.example.my_notes.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.my_notes.data.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) responsável pelo acesso aos dados das notas.
 * Usa Flow para emitir atualizações reativas sempre que o banco mudar.
 */
@Dao
interface NoteDao {

    /**
     * Observa todas as notas ordenadas da mais recente para a mais antiga.
     * Sempre que uma nota for inserida, atualizada ou removida,
     * um novo valor será emitido automaticamente.
     */
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun observeAll(): Flow<List<Note>>

    /**
     * Insere uma nova nota ou atualiza uma existente (mesmo id).
     * Equivale a INSERT OR REPLACE.
     */
    @Upsert
    suspend fun upsert(note: Note)

    /**
     * Remove uma nota do banco de dados.
     */
    @Delete
    suspend fun delete(note: Note)
}
