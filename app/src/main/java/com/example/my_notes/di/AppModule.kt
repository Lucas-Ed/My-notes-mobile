package com.example.my_notes.di

import android.content.Context
import com.example.my_notes.data.local.MyNotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

/**
 * Módulo Hilt responsável por fornecer as dependências do banco de dados
 * e dos dispatchers de corrotinas.
 * O Hilt injeta automaticamente onde for necessário.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /** Fornece a instância única do banco de dados Room. */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MyNotesDatabase {
        return MyNotesDatabase.create(context)
    }

    /** Fornece o DAO de notas. */
    @Provides
    fun provideNoteDao(database: MyNotesDatabase) = database.noteDao()

    /** Fornece o DAO de categorias. */
    @Provides
    fun provideCategoryDao(database: MyNotesDatabase) = database.categoryDao()

    /**
     * Fornece o dispatcher usado pelo repositório para operações de IO.
     * Injetado (e não hardcoded) para facilitar os testes unitários.
     */
    @Provides
    fun provideDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
