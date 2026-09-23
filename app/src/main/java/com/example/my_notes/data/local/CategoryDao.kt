package com.example.my_notes.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.my_notes.data.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) responsável pelo acesso aos dados das categorias.
 * Usa Flow para emitir atualizações reativas sempre que o banco mudar.
 */
@Dao
interface CategoryDao {

    /**
     * Observa todas as categorias cadastradas.
     * Sempre que uma categoria for inserida, atualizada ou removida,
     * um novo valor será emitido automaticamente.
     */
    @Query("SELECT * FROM categories ORDER BY id ASC")
    fun observeAll(): Flow<List<Category>>

    /**
     * Insere uma nova categoria ou atualiza uma existente (mesmo id).
     * Equivale a INSERT OR REPLACE.
     */
    @Upsert
    suspend fun upsert(category: Category)

    /**
     * Remove uma categoria do banco de dados.
     */
    @Delete
    suspend fun delete(category: Category)
}
