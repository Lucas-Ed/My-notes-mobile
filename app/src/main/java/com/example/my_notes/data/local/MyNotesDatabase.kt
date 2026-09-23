package com.example.my_notes.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.my_notes.data.model.Category
import com.example.my_notes.data.model.Note

/**
 * Banco de dados Room do aplicativo My Notes.
 * Armazena notas e categorias localmente no dispositivo,
 * dispensando o uso de APIs externas.
 */
@Database(
    entities = [Note::class, Category::class],
    version = 2,
    exportSchema = false
)
abstract class MyNotesDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun categoryDao(): CategoryDao

    companion object {

        /**
         * Migração 1 -> 2: remove as categorias pré-existentes
         * (Angular, React, Vue e Backend) e zera a categoria
         * das notas que as referenciavam, evitando ids órfãos.
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    UPDATE notes SET categoryId = 0
                    WHERE categoryId IN (
                        SELECT id FROM categories
                        WHERE (name = 'Angular' AND color = '#f6c2d9')
                           OR (name = 'React' AND color = '#a1c8e9')
                           OR (name = 'Vue' AND color = '#bcdfc9')
                           OR (name = 'Backend' AND color = '#255db6')
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    DELETE FROM categories
                    WHERE (name = 'Angular' AND color = '#f6c2d9')
                       OR (name = 'React' AND color = '#a1c8e9')
                       OR (name = 'Vue' AND color = '#bcdfc9')
                       OR (name = 'Backend' AND color = '#255db6')
                    """.trimIndent()
                )
            }
        }

        /**
         * Cria (ou retorna em memória) a instância única do banco de dados.
         * O banco inicia vazio: o usuário cria as próprias categorias.
         */
        fun create(context: Context): MyNotesDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                MyNotesDatabase::class.java,
                "my_notes.db"
            )
                .addMigrations(MIGRATION_1_2)
                .build()
        }
    }
}
