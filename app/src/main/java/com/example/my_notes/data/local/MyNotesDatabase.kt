package com.example.my_notes.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.my_notes.data.model.Category
import com.example.my_notes.data.model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Banco de dados Room do aplicativo My Notes.
 * Armazena notas e categorias localmente no dispositivo,
 * dispensando o uso de APIs externas.
 */
@Database(
    entities = [Note::class, Category::class],
    version = 1,
    exportSchema = false
)
abstract class MyNotesDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun categoryDao(): CategoryDao

    companion object {

        /**
         * Cria (ou retorna em memória) a instância única do banco de dados.
         * Na primeira criação, popula o banco com categorias iniciais
         * para que o usuário já tenha opções disponíveis.
         */
        fun create(context: Context): MyNotesDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                MyNotesDatabase::class.java,
                "my_notes.db"
            )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Popula categorias iniciais no primeiro acesso ao app
                        val scope = CoroutineScope(Dispatchers.IO)
                        scope.launch {
                            db.execSQL(
                                "INSERT INTO categories (name, color) VALUES ('Angular', '#f6c2d9')"
                            )
                            db.execSQL(
                                "INSERT INTO categories (name, color) VALUES ('React', '#a1c8e9')"
                            )
                            db.execSQL(
                                "INSERT INTO categories (name, color) VALUES ('Vue', '#bcdfc9')"
                            )
                            db.execSQL(
                                "INSERT INTO categories (name, color) VALUES ('Backend', '#255db6')"
                            )
                        }
                    }
                })
                .build()
        }
    }
}
