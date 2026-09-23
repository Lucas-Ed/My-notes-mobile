package com.example.my_notes.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa uma nota (post-it) criada pelo usuário.
 * Cada nota pertence a uma categoria e possui título e conteúdo.
 *
 * @param id Identificador único gerado automaticamente pelo Room
 * @param title Título da nota
 * @param content Conteúdo/texto da nota
 * @param categoryId Identificador da categoria à qual a nota pertence
 */
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val categoryId: Int
)
