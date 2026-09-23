package com.example.my_notes.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa uma categoria usada para organizar as notas.
 * Possui um nome e uma cor (formato hex, ex.: "#f6c2d9") para identificação visual.
 *
 * @param id Identificador único gerado automaticamente pelo Room
 * @param name Nome da categoria
 * @param color Cor da categoria no formato hexadecimal (#RRGGBB)
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val color: String
)
